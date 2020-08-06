package edu.ufp.inf.sd.rmi.server;

import edu.ufp.inf.sd.algorithm.JWT;
import edu.ufp.inf.sd.rabbitmqservices.pubsub.client.WorkerRI;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.toHexString;

public class HashSessionImpl extends UnicastRemoteObject implements HashSessionRI {

    //public static final String FILES_PATH = "/Users/nunocoutosoares/Documents/SistemasDistribuidos/projeto/SDseparados/SDServer/";
    public static final String FILES_PATH = "/Users/faculdade/Documents/SistemasDistribuidos/projeto/SDServer/";

    private HashFactoryImpl factory;
    private User user;
    private final DB db;
    private String token;

    public HashSessionImpl(HashFactoryImpl factory, String token, HashMap<String, HashSessionRI> hashMapSessions) throws RemoteException {
        super();
        this.factory = factory;
        this.db= DB.getInstance();
        this.token=token;

        for (User u: db.getUsers()) {
            if (u.getUsername().equals(JWT.decodeJWT(token).getIssuer())){
                this.user=u;
                break;
            }
        }
    }

    /**
     * Remover a sessão
     * @throws RemoteException
     */
    @Override
    public void logout() throws RemoteException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "logout = " + Thread.currentThread().getName() + ")");
        Claims claims= JWT.decodeJWT(this.token);
        this.factory.removeHashSession(claims.getIssuer(), this);
    }

    @Override
    public void receiveCredits(int credits) throws RemoteException {
        System.out.println("-----ENTREI-----");
        System.out.println(credits);
        this.user.setCredits(this.user.getCredits()+credits);
    }

    /**
     * Listar o TaskGroup
     * @return
     * @throws RemoteException
     */
    @Override
    public ArrayList<String> listTaskGroups() throws RemoteException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ListTaskGroups = )");
        ArrayList<String> list = new ArrayList<String>();
        for (TaskGroupImpl task : db.getTaskGroups()) {
            System.out.println(task);
            list.add(task.toString());
        }
        return list;
    }


    /**
     * Remove a tarefa da base de dados
     * @param taskGroupID
     * @return false se não conseguir ser removido
     * @throws RemoteException
     */
    @Override
    public void deleteTaskGroup(String taskGroupID) throws RemoteException, Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "RemoveTasks:" + taskGroupID);
        ArrayList<TaskGroupImpl> temp = new ArrayList<>();
        TaskGroupImpl aux= null;
        for (TaskGroupImpl taskgroup : db.getTaskGroups()){
            if (taskgroup.getId().compareTo(taskGroupID)==0){
                temp.add(taskgroup);
                aux=taskgroup;
                System.out.println("Entrou "+aux.getUserID()+"<--->"+this.user.getID());
            }else {
                System.out.println("Não existe a taskGroup");
            }
        }

        if (aux!= null){
            if (aux.getUserID().compareTo(this.user.getID())==0){
                System.out.println("COMPARAR "+aux.getUserID()+"<--->"+this.user.getID());
                for (TaskGroupImpl taskgroup:temp) {
                    if (Integer.parseInt(taskgroup.getCreditos())>0){
                        int auxCredits = Integer.parseInt(taskgroup.getCreditos());
                        for (User user: db.getUsers()){
                            if (user.getID().equals(taskgroup.getUserID())){
                                auxCredits+= user.getCredits();
                                user.setCredits(auxCredits);
                            }
                        }
                    }
                    Producer.deleteQueue("task_queue"+taskGroupID);
                    db.deleteTaskGroup(taskGroupID);
                }
            }
        }
    }

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
        TaskGroupImpl taskGroup = new TaskGroupImpl(taskGroupID, url, algoritmo, creditos, hash, min, max, alfabeto,user.getID());

        db.getTaskGroups().add(taskGroup);

        file file = new file(FILES_PATH + "taskGroupList.txt");
        if (!file.existTaskGroup(db, taskGroup)) {
            file.putTaskGroupFile(db);
        }

        try {
            taskGroup.createTask(url, taskGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.createWorkQueue(db,taskGroup);
    }

    public String attach(String username, WorkerRI workerRI, String taskGroupID) throws RemoteException {

        RemoveCredits(1);
        TaskGroupImpl taskGroup= null;
        for(TaskGroupImpl t: db.getTaskGroups()){
            //System.out.println(t.getId()+"---"+taskGroupID);
            if(t.getId().equals(taskGroupID)){
                taskGroup=t;
            }
        }

        if(taskGroup!=null){
                for (Task task: db.getTasks()){
                if (task.getTaskGroupRI().getID().compareTo(taskGroupID)==0){
                    if (task.getWorker()==null){
                        taskGroup.addWorker(workerRI);
                        task.setWorker(workerRI);
                        return "task_queue"+taskGroupID;
                    }
                }
            }
        }else{
            System.out.println("\nTaskGroup Not Found!\n");
        }
        return null;
    }

    @Override
    public String getUsername() throws RemoteException {
        String token = this.token;
        Claims claims= JWT.decodeJWT(token);
        return claims.getIssuer();
    }

    @Override
    public String getID() throws RemoteException {
        return this.user.getID();
    }

    @Override
    public String getCredits() throws RemoteException {
        return Integer.toString(this.user.getCredits());
    }

    @Override
    public TaskGroupRI getTaskGroupRI(String ID) throws RemoteException {
        for (TaskGroupRI taskGroupRI: db.getTaskGroups()){
            if (taskGroupRI.getID().equals(ID)){
                return taskGroupRI;
            }
        }
        return null;
    }

    @Override
    public boolean setCreditsToTaskGroup(int credits, String taskGroupID) throws RemoteException {
        int userCredits = this.getUser().getCredits();
        TaskGroupImpl temp=null;
        for (TaskGroupImpl taskGroup: db.getTaskGroups()){
            if (taskGroup.getId().equals(taskGroupID)){
                temp= taskGroup;
            }
        }
        if (temp==null){
            return false;
        }

        if (temp.getUserID().equals(this.user.getID()) && temp.getId().equals(taskGroupID)){
            if(userCredits>=10){
                db.getTaskGroups().remove(temp);
                this.getUser().setCredits(userCredits-10);
                temp.setCreditos(String.valueOf(parseInt(temp.getCreditos())+credits));
                temp.setPaused(true);
                db.getTaskGroups().add(temp);
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean RemoveCredits(int debit) throws RemoteException {
        int current=user.getCredits();

        if (current>= debit){
            this.user.setCredits(current-debit);
            db.updateUsersFile();
            return true;
        }
        return false;
    }

    @Override
    public void changeTaskGroupFlag(String taskGroupID) throws RemoteException {
        for (TaskGroupImpl taskGroup: db.getTaskGroups()){
            if (taskGroup.getId().equals(taskGroupID)){
                taskGroup.setFlagTrueUpdateWorker();
            }
        }
    }

    /**
     * Gets e Sets dos atributos da Classe Session
     * @return
     */
    public HashFactoryImpl getFactory() {
        return factory;
    }

    public void setFactory(HashFactoryImpl factory) {
        this.factory = factory;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}