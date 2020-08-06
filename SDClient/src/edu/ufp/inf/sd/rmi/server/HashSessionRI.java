package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.rabbitmqservices.pubsub.client.WorkerRI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface HashSessionRI extends Remote {

  public void logout()throws RemoteException;

  public void receiveCredits(int credits) throws RemoteException;

  public ArrayList<String> listTaskGroups()throws RemoteException;

  public void deleteTaskGroup(String taskGroupID)throws RemoteException, Exception;

  public void createTaskGroup(String id, String url, String algoritmo, String creditos, String hash, String min, String max, String alfabeto, String userID) throws RemoteException;

  public String attach(String username, WorkerRI workerRI, String taskGroupID) throws RemoteException;

  public String getUsername() throws RemoteException;

  public String getID() throws RemoteException;

  public boolean RemoveCredits(int debito) throws RemoteException;

  public String getCredits() throws RemoteException;

  public TaskGroupRI getTaskGroupRI(String ID) throws RemoteException;

  public boolean setCreditsToTaskGroup (int credits, String taskGroupID) throws RemoteException;

  void changeTaskGroupFlag (String taskGroupID) throws RemoteException;
}