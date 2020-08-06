package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.rabbitmqservices.pubsub.client.WorkerRI;

import java.util.ArrayList;

public class Task {

  private WorkerRI worker;

  private boolean match;

  private int count;

  private int line;

  private int delta;

  private String alfabeto;

  private ArrayList<String> palavrasACodificar;

  private String hash;

  private String user;

  private String filename;

  private int min;

  private int max;

  private int id;

  private TaskGroupRI taskGroupRI;

  public Task(int id, WorkerRI worker, boolean match, int count, int line, int delta,
              String alfabeto, ArrayList<String> palavrasACodificar, String hash, String user, String filename, int min, int max, TaskGroupRI taskGroupRI) {

    this.line = line;
    this.id = id;
    this.delta = delta;
    this.alfabeto = alfabeto;
    this.palavrasACodificar = palavrasACodificar;
    this.hash = hash;
    this.user = user;
    this.filename = filename;
    this.min=min;
    this.max=max;
    this.taskGroupRI =taskGroupRI;
  }

  public TaskGroupRI getTaskGroupRI() {
    return taskGroupRI;
  }

  public void setTaskGroupRI(TaskGroupRI taskGroupRI) {
    this.taskGroupRI = taskGroupRI;
  }

  public WorkerRI getWorker() {
    return worker;
  }

  public void setWorker(WorkerRI worker) {
    this.worker = worker;
  }

  public boolean isMatch() {
    return match;
  }

  public void setMatch(boolean match) {
    this.match = match;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }

  public int getDelta() {
    return delta;
  }

  public void setDelta(int delta) {
    this.delta = delta;
  }

  public String getAlfabeto() {
    return alfabeto;
  }

  public void setAlfabeto(String alfabeto) {
    this.alfabeto = alfabeto;
  }

  public ArrayList<String> getPalavrasACodificar() {
    return palavrasACodificar;
  }

  public void setPalavrasACodificar(ArrayList<String> palavrasACodificar) {
    this.palavrasACodificar = palavrasACodificar;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  private boolean matching() {
  return false;
  }

  public int getMin() {
    return min;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public int getMax() {
    return max;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Task{" +
            ", worker='" + worker + '\'' +
            ", match=" + match +
            ", count=" + count +
            ", line=" + line +
            ", delta=" + delta +
            ", alfabeto='" + alfabeto + '\'' +
            ", palavrasACodificar=" + palavrasACodificar +
            ", hash='" + hash + '\'' +
            ", user='" + user + '\'' +
            ", filename='" + filename + '\'' +
            ", min=" + min +
            ", max=" + max +
            ", id=" + id+
            '}';
  }
}