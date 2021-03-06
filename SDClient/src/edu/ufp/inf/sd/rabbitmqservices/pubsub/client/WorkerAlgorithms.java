package edu.ufp.inf.sd.rabbitmqservices.pubsub.client;

import edu.ufp.inf.sd.algorithm.BCrypt;
import edu.ufp.inf.sd.rmi.server.HashSessionRI;
import edu.ufp.inf.sd.rmi.server.TaskGroupRI;
import sun.tools.jconsole.Worker;

import javax.xml.bind.DatatypeConverter;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class WorkerAlgorithms {

    WorkerImpl worker;

    String task;

    TaskGroupRI taskGroupRI;

    HashSessionRI hashSessionRI;

    public WorkerAlgorithms(WorkerImpl worker, String task, HashSessionRI hashSessionRI) {
        this.worker = worker;
        this.task= task;
        this.hashSessionRI=hashSessionRI;
    }

    public void run() throws RemoteException {
        doWork(task);
    }

    /**
     * Método para realização da task
     * @param task task para o worker fazer
     * @throws RemoteException
     */
    public void doWork(String task) throws RemoteException {
        System.out.println("I'm working!! Receive:"+task);
        String[] t=task.split(";");
        String tgID=t[2];
        this.taskGroupRI = hashSessionRI.getTaskGroupRI(tgID);

        if (taskGroupRI==null){
            //falhou logo não trabalha
            return;
        }
        this.taskGroupRI.addWorker(worker);


        String[] w=t[3].split(",");
        ArrayList<String> words = new ArrayList<>(Arrays.asList(w));
        hashing(t[0],words,t[1]);
    }

    /**
     * Método para testar as palavras consoante o método pretendido
     * @param algorithm algoritmo a usar
     * @param words palavras a codificar
     * @param hash hash à qual se quer comparar
     */
    public void hashing(String algorithm, ArrayList<String> words, String hash) throws RemoteException {
        if(algorithm.compareToIgnoreCase("BCrypt") == 0){
            algorithm_BCrypt(words,hash);
        }else{
            try {
                another_algorithms(words,hash,algorithm);
            } catch (NoSuchAlgorithmException | RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hashing válido para algoritmos tais como:
     *MD2
     *MD5
     *SHA-1
     *SHA-256
     *SHA-384
     *SHA-512
     * @param words palavras a codificar
     * @param hash a hash a que se quer comparar
     * @param hashMethod método de hash pretendido
     * @throws NoSuchAlgorithmException
     */
    public void another_algorithms(ArrayList<String> words, String hash, String hashMethod) throws NoSuchAlgorithmException, RemoteException {

        for (String aux: words){

            while (taskGroupRI.isPaused()){
                // Está à espera
            }

            //Todo se encontrar return null

            MessageDigest msgD = MessageDigest.getInstance(hashMethod);
            msgD.update(aux.getBytes());
            byte[] digiest = msgD.digest();
            String temp = DatatypeConverter.printHexBinary(digiest);
            if (temp.compareTo(hash)==0){
                System.out.println("FOUND:\t"+aux);
                taskGroupRI.match(aux,hash,worker);

            }
        }
    }

    /**
     * Hashing para o algoritmo BCrypt
     * @param words palavras a codificar
     * @param hash para comparação final
     */
    public void algorithm_BCrypt(ArrayList<String> words, String hash) throws RemoteException {
        System.out.println("HashCode Generated by Bcrypt: ");
        for (String originalPassword: words){

            while (taskGroupRI.isPaused()){
                // Está à espera
            }

            System.out.println("------- PALAVRA: "+originalPassword);
            System.out.println("------- HASH: "+hash);
            if (BCrypt.checkpw(originalPassword,hash)){
                String[] argv={"FOUND MATCH",originalPassword,hash};
                try {
                    worker.sendMatch(argv);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("FOUND:\t"+originalPassword);
                taskGroupRI.match(originalPassword,hash,worker);
            }
        }
    }

}
