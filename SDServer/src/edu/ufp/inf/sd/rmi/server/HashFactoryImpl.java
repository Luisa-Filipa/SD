package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.algorithm.JWT;
import io.jsonwebtoken.Claims;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HashFactoryImpl extends UnicastRemoteObject implements HashFactoryRI {

  private final DB db;

  private HashMap<String, HashSessionRI> sessions;

  public HashFactoryImpl() throws RemoteException {
    super();
    this.db= DB.getInstance();
    sessions = new HashMap<>();
  }

  public DB getDb() {
    return db;
  }

  @Override
  public boolean registerUser(String token) {
    Claims claims= JWT.decodeJWT(token);
    if(db.canRegister(claims.getIssuer())){
      db.register(claims.getIssuer(), claims.getSubject());
      return true;
    }
    return false;
  }

  @Override
  public HashSessionRI login(String token) throws RemoteException {
    Claims claims= JWT.decodeJWT(token);
    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "login=" + claims.getIssuer() +"," +claims.getSubject());
    for (User user: db.getUsers()){
      if (user.getUsername().compareTo(claims.getIssuer())==0 && user.getPassword().compareTo(claims.getSubject())==0){
        HashSessionRI session = getHashSession(claims.getIssuer());
        if (session == null){
          session = new HashSessionImpl(this,token,sessions);
          this.sessions.put(claims.getIssuer(),session);
        }
        return  session;
        }
    }
      return null;
  }

  private HashSessionRI getHashSession(String uName) {
    HashSessionRI sessionRI= null;
    this.sessions.get(uName);
    return sessionRI;
  }

  protected boolean removeHashSession(String username, HashSessionRI session){
    return this.sessions.remove(username,session);
  }
}