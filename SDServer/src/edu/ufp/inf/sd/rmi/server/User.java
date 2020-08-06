package edu.ufp.inf.sd.rmi.server;

import java.io.Serializable;

public class User implements Serializable {

  private String username;

  private String password;

  private String ID;

  private int credits;

  public User(String username, String password, String ID, int credits) {
    this.username = username;
    this.password = password;
    this.ID=ID;
    this.credits=credits;
  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public String toString() {
    return "User{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", ID='" + ID + '\'' +
            ", credits=" + credits +
            '}';
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getID() {
    return ID;
  }

  public int getCredits() {
    return credits;
  }

  public void setCredits(int credits) {
    this.credits = credits;
  }
}
