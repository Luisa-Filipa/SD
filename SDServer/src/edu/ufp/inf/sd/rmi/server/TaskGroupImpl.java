package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.algorithm.BCrypt;
import edu.ufp.inf.sd.rabbitmqservices.pubsub.client.WorkerRI;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class TaskGroupImpl extends UnicastRemoteObject implements TaskGroupRI{

    //public static final String FILES_PATH = "/Users/nunocoutosoares/Documents/SistemasDistribuidos/projeto/SDseparados/SDServer/";
    public static final String FILES_PATH = "/Users/faculdade/Documents/SistemasDistribuidos/projeto/SDServer/";
    private final DB db;
    private String fileName;
    private String algoritmo;
    private String creditos;
    private String hash;
    private String min;
    private String max;
    private String id;
    private String alfabeto;
    private String userID;
    private ArrayList<WorkerRI> workersRI = new ArrayList<>();
    private boolean paused = false;

    public TaskGroupImpl(String id, String fileName, String algoritmo, String creditos, String hash, String min, String max, String alfabeto, String userID) throws RemoteException {
        super();
        this.db= DB.getInstance();

        this.id=id;
        this.fileName = fileName;
        this.algoritmo = algoritmo;
        this.creditos = creditos;
        this.hash = hash;
        this.min = min;
        this.max = max;
        this.alfabeto = alfabeto;
        this.userID=userID;
    }

    /**
     * Criar a taskGroup estratégia 1
     *
     * @param url  ficheiro que contém o hash e as pp
     * @param algoritmo qual o algoritmo a usar para desencriptar
     * @throws RemoteException
     */
    @Override
    public void createTaskGroup(String id, String url, String algoritmo, String creditos, String hash, String min, String max, String alfabeto, String userID) throws RemoteException {
        String lastTaskGroupID="";
        if(db.getTaskGroups().isEmpty()){
            lastTaskGroupID="0";
        }
        else{
            lastTaskGroupID = (db.getTaskGroups().get(db.getTaskGroups().size()-1)).getId();
        }

        String taskGroupID = ""+ (parseInt(lastTaskGroupID)+1);
        TaskGroupImpl taskGroup = new TaskGroupImpl(taskGroupID, url, algoritmo, creditos, hash, min, max, alfabeto,userID);

        db.getTaskGroups().add(taskGroup);
        System.out.println(db.getTaskGroups().size());

        file file = new file(FILES_PATH + "taskGroupList.txt");
        if (!file.existTaskGroup(db, taskGroup)) {
            file.putTaskGroupFile(db);
        }

        try {
            createTask(url, taskGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.createWorkQueue(db,taskGroup);
    }

    /**
     * Testa as estratégias e cria a tarefa
     * @param url
     * @param taskGroup
     */
    @Override
    public void createTask(String url, TaskGroupImpl taskGroup) throws IOException {
        //id, worker, match, count, line, delta, alfabeto, palavrasACodificar, hash, user, filename, min, max
        String filename="";
        try {
            filename = url(new URL(url),taskGroup.getId());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        int min = parseInt(taskGroup.getMin()), max = parseInt(taskGroup.getMax());

        String alfabeto = taskGroup.getAlfabeto();

        //est3
        if (alfabeto.compareTo("all") != 0) {
            try {
                est3(filename, taskGroup, db);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (min != 0 && max != 0) {
            try {
                est2(filename, taskGroup, db);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            try {
                est1(filename, taskGroup, db);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Criar a Task caso seja pedido para criar a Task pela estratégia 1
     * @param filename
     * @param taskGroup
     * @param db
     * @throws RemoteException
     */
    @Override
    public void est1(String filename, TaskGroupImpl taskGroup, DB db) throws IOException {
        file file = new file(filename);
        int lines = file.countLines(filename);
        int vagas = numberOfWorkers(lines);
        int delta = (lines / vagas);
        int res = (lines % vagas);
        int actualLine = 0;
        int lastLine = actualLine + delta;
        int aux = vagas;

        if (res != 0) { // se for impar deixa a ultima task para ser feita no fim
            aux = vagas - 1;
        }
        for (int i = 0; i < aux; i++) {
            ArrayList<String> words = new ArrayList<>();
            for (int j = actualLine; j < lastLine; j++) {
                try {
                    String word = file.getLine(filename, j);
                    words.add(word);
                } catch (Exception e) {
                    e.printStackTrace();}
            }

            Task task = new Task(0, null, false, 0, actualLine, delta, taskGroup.getAlfabeto(), words, taskGroup.getHash(), "user", filename, parseInt(taskGroup.getMin()), parseInt(taskGroup.getMax()),taskGroup);
            db.insertTask(task);
            actualLine = lastLine;
            lastLine += delta;

        }
        // ultima task fica com o que sobra
        if (res!=0){
            lastLine+= res;
            ArrayList<String> words = new ArrayList<>();
            for (int j = actualLine; j < lastLine; j++) {
                try {
                    String word = file.getLine(filename, j);
                    words.add(word);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Task task = new Task(0, null, false, 0, actualLine, delta, taskGroup.getAlfabeto(), words, taskGroup.getHash(), "user", filename, parseInt(taskGroup.getMin()), parseInt(taskGroup.getMax()),taskGroup);
            db.insertTask(task);
        }
    }


    /**
     * Criar a Task caso seja pedido para criar a Task pela estratégia 2
     * @param filename
     * @param taskGroup
     * @param db
     * @throws RemoteException
     */
    @Override
    public void est2(String filename, TaskGroupImpl taskGroup, DB db) throws RemoteException {
        file file = new file(filename);
        int lines = file.countLines(filename);
        ArrayList<String> palavrasACodificar = new ArrayList<>();

        //mete no arraylist palavrasACodificar as palavras que fazem parte dos criterios
        for (int i = 0; i < lines; i++) {
            try {
                String word = file.getLine(filename, i);
                if(taskGroup.getMin().compareTo(taskGroup.getMin())==0){
                    if(word.length() == parseInt(taskGroup.getMin()))
                        palavrasACodificar.add(word);
                }
                else {
                    if (word.length() >= parseInt(taskGroup.getMin()) && word.length() <= parseInt(taskGroup.getMax())) {
                        palavrasACodificar.add(word);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int vagas = numberOfWorkers(palavrasACodificar.size());
        int delta = palavrasACodificar.size() / vagas;
        int res = (palavrasACodificar.size() % vagas);
        int j = 0, lastj = j;
        if (res!=0){ vagas=vagas-1;} // se for impar deixa a ultima task para ser feita no fim
        for (int i = 0; i < vagas; i++) {
            ArrayList<String> words = new ArrayList<>();
            for (; j < lastj + delta; j++) {
                words.add(palavrasACodificar.get(j));
            }
            Task task = new Task(0, null, false, 0, 0, delta, taskGroup.getAlfabeto(), words, taskGroup.getHash(), "user", filename, parseInt(taskGroup.getMin()), parseInt(taskGroup.getMax()),taskGroup);
            db.insertTask(task);
            lastj = j;
        }
        // ultima task fica com o que sobra
        if (res!=0){
            lastj = j;
            ArrayList<String> words = new ArrayList<>();
            for (; j < lastj + delta+res; j++) {
                words.add(palavrasACodificar.get(j));
            }
            Task task = new Task(0, null, false, 0, 0, delta, taskGroup.getAlfabeto(), words, taskGroup.getHash(), "user", filename, parseInt(taskGroup.getMin()), parseInt(taskGroup.getMax()),taskGroup);
            db.insertTask(task);
        }
    }

    /**
     * Criar a Task caso seja pedido para criar a Task pela estratégia 3
     * @param filename
     * @param taskGroup
     * @param db
     * @throws RemoteException
     */
    @Override
    public void est3(String filename, TaskGroupImpl taskGroup, DB db) throws RemoteException {
        file file = new file(filename);
        int lines = file.countLines(filename);
        ArrayList<String> words= new ArrayList<>();
        int vagas = numberOfWorkers(lines);
        int min =Integer.parseInt(taskGroup.getMin());
        int max = Integer.parseInt(taskGroup.getMax());
        char[] alphabet = taskGroup.getAlfabeto().toCharArray();
       for(int i=min;i<=max;i++){
           alfabetWords(alphabet,i, new char[alphabet.length],0,words);
       }
        System.out.println("Words"+ words.size());
        ArrayList<String> selectedWords =new ArrayList<>();
        for(String aux:words){
            if(aux.length()>=Integer.parseInt(taskGroup.getMin()) && aux.length()<=Integer.parseInt(taskGroup.getMax())){
                selectedWords.add(aux);
            }
        }
        System.out.println("BUILD----------TEMP---- :"+selectedWords.size());

        int delta = selectedWords.size() / vagas;
        int res = (selectedWords.size() % vagas);
        int j = 0, lastj = j;
        if (res!=0){ vagas=vagas-1;} // se for impar deixa a ultima task para ser feita no fim
        for (int i = 0; i < vagas; i++) {
            ArrayList<String> taskWords= new ArrayList<>();
            for (; j < lastj + delta; j++) {
                taskWords.add(selectedWords.get(j));
            }
            Task task = new Task(0, null, false, 0, 0, delta, taskGroup.getAlfabeto(), taskWords, taskGroup.getHash(), "user", filename, parseInt(taskGroup.getMin()), parseInt(taskGroup.getMax()),taskGroup);
            db.insertTask(task);
            lastj = j;
        }
        // ultima task fica com o que sobra
        if (res!=0){
            lastj = j;
            ArrayList<String> taskWords = new ArrayList<>();
            for (; j < lastj + delta+res; j++) {
                taskWords.add(selectedWords.get(j));
            }
            Task task = new Task(0, null, false, 0, 0, delta, taskGroup.getAlfabeto(), taskWords, taskGroup.getHash(), "user", filename, parseInt(taskGroup.getMin()), parseInt(taskGroup.getMax()),taskGroup);
            db.insertTask(task);
        }
    }

    /**
     * Número de workers a pôr por taskGroup
     * @param w numero de linhas do ficheiro
     * @return o número de workers
     */
    public int numberOfWorkers(int w){
        if(w<10)
            return 2;
        else if(w<100) {
            return w/10;
        }
        else if(w<1000)
            return w/100;
        else
            return 0;
    }

    /**
     * Descobrir todas as palavras com o alfabeto
     * @param chars alfabeto
     * @param len tamanho do alfabeto
     * @param build palavra que vai ser construída
     * @param pos
     * @return
     */
    public void alfabetWords(char[] chars, int len, char[] build, int pos, ArrayList<String> words){

        if (pos == len) {
            String aux = new String(build);
                words.add(aux);
                if(!aux.contains(",")) {
                    System.out.println("BUILD---------AUX---------- :" + aux);
                }
            return;
        }

        for (int i = 0; i < chars.length; i++) {
            build[pos] = chars[i];
            alfabetWords(chars, len, build, pos + 1,words);
        }
    }

    /**
     * Copia o ficheiro do URL para o SO do server
     * @param url url onde se encontra o TXT
     * @param taskID componente do nome do novo ficheiro
     * @return o nome do novo ficheiro
     * @throws IOException
     */
    public String url(URL url, String taskID) throws IOException {
        //String filename = "/Users/nunocoutosoares/Documents/SistemasDistribuidos/projeto/SDseparados/SDServer/Data/globalTask"+taskID+".txt";
        String filename = "/Users/faculdade/Documents/SistemasDistribuidos/projeto/SDServer/Data/globalTask"+taskID+".txt";

        URLConnection connection = url.openConnection();
        InputStream stream = connection.getInputStream();
        BufferedInputStream in = new BufferedInputStream(stream);
        FileOutputStream file = new FileOutputStream(filename);
        BufferedOutputStream out = new BufferedOutputStream(file);
        int i;
        while ((i = in.read()) != -1) {
            out.write(i);
        }
        out.flush();
        return filename;
    }

    @Override
    public String getID() throws RemoteException {
        return this.id;
    }

    public boolean addWorker(WorkerRI workerRI) throws RemoteException {
        if (workersRI.contains(workerRI)){
            return false;
        }
        workersRI.add(workerRI);
        return true;
    }

    @Override
    public void match(String aux, String hash, WorkerRI workerRI) throws RemoteException {
        if (this.paused){
            workerRI.taskGroupIsPaused(this.id+ " is Paused");
            return;
        }

        if (!workersRI.contains(workerRI)){
            return;
        }

        String hashmethod = this.getAlgoritmo();
        boolean match;
        if (hashmethod.equals("BCrypt")) {
            match = algorithm_BCrypt(aux, hash);
        } else {
            match = another_algorithms(aux, hash, hashmethod);
        }
        if(match){

            workerRI.taskGroupIsPaused(this.id+"  YOU FOUND!");

            this.setCreditos(Integer.toString(parseInt(this.getCreditos())-10));
            for (User user : db.getUsers()){
                if(user.getID().equals(workerRI.userID())){
                    user.setCredits(user.getCredits()+10);
                    db.updateUsersFile();
                }
            }
            if (parseInt(this.getCreditos())<10){
                this.paused =true;
            }

            for(WorkerRI temp: workersRI){
                temp.pauseWorker();
                temp.taskGroupIsPaused(this.id+" FOUND: STOP WORKING!");
            }
            try {
                Producer.deleteQueue("task_queue"+this.id);
            } catch (Exception e) {
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
     * @param word palavra a codificar
     * @param hash a hash a que se quer comparar
     * @param hashMethod método de hash pretendido
     */
    public boolean another_algorithms(String word, String hash, String hashMethod){
        try{
            MessageDigest msgD = MessageDigest.getInstance(hashMethod);
            msgD.update(word.getBytes());
            byte[] digiest = msgD.digest();
            String temp = DatatypeConverter.printHexBinary(digiest);
            if (temp.compareTo(hash)==0){
                return true;
            }
            else{
                return false;
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setFlagTrueUpdateWorker(){
        for (WorkerRI workerRI: this.workersRI){
            try {
                workerRI.taskGroupIsPaused("TaskGroup "+this.id+" FOUND");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        this.setPaused(true);
    }

    public boolean algorithm_BCrypt(String word, String hash) {
        if (BCrypt.checkpw(word,hash)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean isPaused() throws RemoteException{
        return paused;
    }

    @Override
    public String toString() {
        return "TaskGroupImpl{" +
                "db=" + db +
                ", fileName='" + fileName + '\'' +
                ", algoritmo='" + algoritmo + '\'' +
                ", creditos='" + creditos + '\'' +
                ", hash='" + hash + '\'' +
                ", min='" + min + '\'' +
                ", max='" + max + '\'' +
                ", id='" + id + '\'' +
                ", alfabeto='" + alfabeto + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Gets e Sets
     */


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public void setAlgoritmo(String algoritmo) {
        this.algoritmo = algoritmo;
    }

    public String getCreditos() {
        return creditos;
    }

    public void setCreditos(String creditos) {
        this.creditos = creditos;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlfabeto() {
        return alfabeto;
    }

    public void setAlfabeto(String alfabeto) {
        this.alfabeto = alfabeto;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        userID = userID;
    }
}