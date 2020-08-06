package edu.ufp.inf.sd.rabbitmqservices.pubsub.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface WorkerRI extends Remote {

    public void consume(String[] argv) throws Exception;

    public void sendMatch(String[] argv) throws Exception;

    public String getID() throws RemoteException;

    public String userID() throws RemoteException;

    public void pauseWorker() throws RemoteException;

    void taskGroupIsPaused(String tgID) throws RemoteException;

}
