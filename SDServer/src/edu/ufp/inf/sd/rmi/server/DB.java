package edu.ufp.inf.sd.rmi.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class DB {

    //public static final String FILES_PATH = "/Users/nunocoutosoares/Documents/SistemasDistribuidos/projeto/SDseparados/SDServer/";
    public static final String FILES_PATH = "/Users/faculdade/Documents/SistemasDistribuidos/projeto/SDServer/";

    private static DB dbInstance;
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<TaskGroupImpl> taskGroups = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private HashMap<String,String> matches = new HashMap<>();
    private file file;

    public DB (){
        this.file = new file(FILES_PATH+"user.txt");
        file.getToDB(this);
    }

    public static synchronized DB getInstance() {
        if (dbInstance == null)
            dbInstance = new DB();
        return dbInstance;
    }

    /**
     * Fazer o update do ficheiro users.txt
     */
    public void updateUsersFile(){
        file.putDB(dbInstance);
    }

    public boolean canRegister(String u){
        if (this.users.isEmpty()) {
            System.out.println("Vazio");
            return false;
        }
        for (User usr : this.users) {
            if (usr.getUsername().compareTo(u) == 0) {
                return false;
            }
        }
        return true;
    }

    public boolean exists(String u, String p) {
        if (this.users.isEmpty()) {
            System.out.println("Vazio");
            return false;
        }
        for (User usr : this.users) {
            System.out.println("Tem algum");
            if (usr.getUsername().compareTo(u) == 0 && usr.getPassword().compareTo(p) == 0) {
                System.out.println("Tentou comparar");
                return true;
            }
        }
        return false;
    }

    public void insertTask(Task task) {
        this.tasks.add(task);
        String s= null;
        try {
            s = FILES_PATH+"tasks" + task.getTaskGroupRI().getID() + ".txt";
            file filetoDB = new file(s);
            filetoDB.putTaskFile(this, Integer.parseInt(task.getTaskGroupRI().getID()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void insertTaskGroup(TaskGroupImpl taskGroup) {
        this.taskGroups.add(taskGroup);
        file filetoDB = new file(FILES_PATH+"taskGroupList.txt");
        filetoDB.putTaskGroupFile(this);
    }

    public void insertUser(User user){
        this.users.add(user);
        file filetoDB = new file(FILES_PATH+"users.txt");
        filetoDB.putDB(this);
    }

    public void register(String u, String p) {
        if (!exists(u, p)) {
            String lastUserID = (users.get(users.size()-1)).getID();
            String userID = ""+ (Integer.parseInt(lastUserID)+1);
            this.users.add(new User(u, p,userID,20));
            System.out.println(this.users.get(0));
            file.putDB(this);
        } else
            System.out.println("Já existe");
    }

    public void deleteTasks(String taskGroupID){
        if(this.tasks.isEmpty()) return;
        this.tasks.removeIf(t -> {
            try {
                return t.getTaskGroupRI().getID().compareTo(taskGroupID) == 0;
            } catch (RemoteException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    /**
     * Ir buscar a taskGroup
     * @param ID id da taskGroup que se pretende
     * @return
     */
    public TaskGroupImpl getTaskGroupImpl(String ID){
        for(TaskGroupImpl taskGroupImpl: this.getTaskGroups()){
            if (taskGroupImpl.getId().compareTo(ID)==0){
                return taskGroupImpl;
            }
        }
        return null;
    }

    public  void deleteTaskGroup(String taskGroupID){
        if(this.taskGroups.isEmpty()) return;
        deleteTasks(taskGroupID);
        for(TaskGroupImpl t:this.taskGroups){
            if(t.getId().compareTo(taskGroupID)==0) {
                this.taskGroups.remove(t);
                return;
            }
        }
    }

    public User getUser(String id){
        for (User u : this.users) {
            if(u.getID().compareTo(id)==0)
                return u;
        }
        return null;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public void setFile(file file) {
        this.file = file;
    }

    public ArrayList<TaskGroupImpl> getTaskGroups() {
        return taskGroups;
    }

    public HashMap<String, String> getMatches() {
        return matches;
    }

    public void setMatches(HashMap<String, String> matches) {
        this.matches = matches;
    }

    public void setTaskGroups(ArrayList<TaskGroupImpl> taskGroups) {
        this.taskGroups = taskGroups;
    }
}