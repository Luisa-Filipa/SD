package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.rabbitmqservices.pubsub.client.WorkerRI;

import java.io.IOException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskGroupRI extends Remote {

    void createTaskGroup(String id, String url, String algoritmo, String creditos, String hash, String min, String max, String alfabeto, String userID) throws RemoteException;

    void createTask(String filename, TaskGroupImpl taskGroup)throws IOException;

    void est1(String filename, TaskGroupImpl taskGroup, DB db) throws IOException;

    void est2(String filename, TaskGroupImpl taskGroup, DB db) throws IOException;

    void est3(String filename, TaskGroupImpl taskGroup, DB db) throws IOException;

    String url(URL url, String taskID) throws IOException;

    String getID() throws RemoteException;

    boolean addWorker(WorkerRI workerRI) throws RemoteException;

    void match(String aux, String hash, WorkerRI workerRI) throws RemoteException;

    String getCreditos() throws RemoteException;

    boolean isPaused() throws RemoteException;
}