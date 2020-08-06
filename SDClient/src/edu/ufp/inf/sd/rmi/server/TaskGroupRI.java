package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.rabbitmqservices.pubsub.client.WorkerRI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskGroupRI extends Remote {

    public void createTaskGroup(String id, String url, String algoritmo, String creditos, String hash, String min, String max, String alfabeto, String userID) throws RemoteException;

    public String getID() throws RemoteException;

    boolean addWorker(WorkerRI workerRI) throws RemoteException;

    public void match(String aux, String hash, WorkerRI workerRI) throws RemoteException;

    boolean isPaused() throws RemoteException;

    String getCreditos() throws RemoteException;


}
