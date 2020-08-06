package edu.ufp.inf.sd.rmi.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HashFactoryRI extends Remote {

  public boolean registerUser(String token)throws RemoteException;

  public HashSessionRI login(String token) throws RemoteException;
}