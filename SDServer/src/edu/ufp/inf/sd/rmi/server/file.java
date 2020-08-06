package edu.ufp.inf.sd.rmi.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class file {

    //public static final String FILES_PATH = "/Users/nunocoutosoares/Documents/SistemasDistribuidos/projeto/SDseparados/SDServer/";
    public static final String FILES_PATH = "/Users/faculdade/Documents/SistemasDistribuidos/projeto/SDServer/";

    private String txt;

    public file(String txt) {
        this.txt = txt;
        createFile(this.txt);
    }

    /**
     * Por no ficheiro todos os users registados
     * @param db
     */
    public void putDB(DB db){
        try {
            FileWriter myWriter = new FileWriter(this.txt);
            for (User u : db.getUsers()) {
                myWriter.write(u.getUsername() + ";" + u.getPassword()+ ";" +u.getID()+ ";" +u.getCredits() +"\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Por no ficheiro todos os users registados
     * @param db
     */
    public void matches(DB db){
        try {
            FileWriter myWriter = new FileWriter("matches.txt");
            for (Map.Entry<String, String> m : db.getMatches().entrySet()) {
                myWriter.write(m.getKey() + ";"+ m.getValue()+ "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Ler do ficheiro todos os users registados
     * @param db
     */
    public void getToDB(DB db){
        try {
            String[] aux ;
            ArrayList<User> temp;
            File myObj = new File(FILES_PATH + "user.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                aux=data.split(";");
                temp = db.getUsers();
                System.out.println(aux[3]);
                System.out.println(parseInt(aux[3]));
                temp.add(new User(aux[0],aux[1],aux[2],parseInt(aux[3])));
                db.setUsers(temp);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Ir buscar ao ficheiro todas as TaskGroups e por na db
     * @param db base de dados
     */
    public void getTaskGroupFile(DB db){
        try {
            String[] aux ;
            ArrayList<TaskGroupImpl> temp;
            File myObj = new File(FILES_PATH+"taskGroupList.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                aux=data.split(";");
                temp = db.getTaskGroups();
                TaskGroupImpl taskGroup = new TaskGroupImpl(aux[0],aux[1],aux[2],aux[3],aux[4],aux[5],aux[6],aux[7],aux[8]);
                temp.add(taskGroup);
                db.setTaskGroups(temp);
                getTaskFile(db, parseInt(aux[0]));
                System.out.println(db.getTasks());
                createWorkQueue(db,taskGroup);
            }
            myReader.close();
        } catch (FileNotFoundException | RemoteException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void createWorkQueue(DB db, TaskGroupImpl taskGroup) throws RemoteException {
        for (Task task : db.getTasks()) {
            if (!task.getTaskGroupRI().getID().equals(taskGroup.getId())){
                continue;
            }
            String[] argv = {taskGroup.getId(),""};

            TaskGroupImpl temp = db.getTaskGroupImpl(task.getTaskGroupRI().getID());

            argv[1]=temp.getAlgoritmo() + ";" + task.getHash()+";" + temp.getID() + ";";

            System.out.println("------HASH: "+task.getHash());

            for (String s: task.getPalavrasACodificar()) {
                argv[1]=argv[1] + s +",";
            }
            try {
                Producer.produce(argv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Guardar as TaskGroups no ficheiro taskGroupList
     * @param db base de dados
     */
    public void putTaskGroupFile(DB db){
        try {
            FileWriter myWriter = new FileWriter(FILES_PATH+"taskGroupList.txt");
            for (TaskGroupImpl taskGroup : db.getTaskGroups()) {
                myWriter.write(taskGroup.getId() +";"+taskGroup.getFileName() +  ";" + taskGroup.getAlgoritmo() + ";" + taskGroup.getCreditos()+ ";" + taskGroup.getHash()+";"+ taskGroup.getMin()+";"+ taskGroup.getMax()+ ";"+ taskGroup.getAlfabeto()+";"+ taskGroup.getUserID() + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Cria um ficheiro
     * @param filename nome do ficheiro que se quer criar
     */
    public void createFile(String filename){
        try {
            File myObj = new File(filename);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public Boolean existTaskGroup(DB db, TaskGroupImpl taskGroup){
        ArrayList<TaskGroupImpl> taskGroups = db.getTaskGroups();
        System.out.println(taskGroups.size());
        System.out.println(taskGroups.toString());
        for(TaskGroupImpl aux: taskGroups){
            if (aux.getFileName().compareTo(taskGroup.getFileName()) == 0){
                 return false;
            }
        }
        return true;
    }

    /**
     * Conta as linhas do ficheiro
     * @param filename
     * @return numero de linhas
     */
    public int countLines(String filename){
        int count=0;
        try {
            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                count++;
            }
            myReader.close();
            return count;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Por as Tasks Todas no ficheiro
     * @param db
     */
    public void putTaskFile(DB db, int taskGroupID){
        try {
            String txt = FILES_PATH+"tasks" + taskGroupID +".txt";
            FileWriter myWriter = new FileWriter(txt);
            for (Task task : db.getTasks()) {
                if(parseInt(task.getTaskGroupRI().getID())==taskGroupID) {
                    String words = task.getPalavrasACodificar().get(0);
                    for (int i = 1; i < task.getPalavrasACodificar().size(); i++) {
                        String aux = task.getPalavrasACodificar().get(i);
                        words = words.concat(",");
                        words = words.concat(aux);
                    }
                    myWriter.write(task.getId()  + ";" + task.getWorker() + ";" + task.isMatch() + ";" + task.getCount() +
                            ";" + task.getLine() + ";" + task.getDelta() + ";" + task.getAlfabeto() +
                            ";" + words + ";" + task.getHash() + ";" + task.getUser() + ";" + task.getFilename() + ";" + task.getMin() + ";" + task.getMax() + ";" + taskGroupID + "\n");
                }
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Obter uma linha expecífica de um ficheiro
     * @param filename nome do ficheiro pretendido
     * @param l linha requerida
     * @return conteúdo da linha
     * @throws Exception
     */
    public String getLine(String filename, int l) throws Exception {
        File myObj = new File(filename);
        Scanner myReader = new Scanner(myObj);
        int count=0;
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            if(count==l){
                return data;
            }
            count++;
        }
        myReader.close();
        return null;
    }

    /**
     * Ir buscar as Tasks
     * @param db
     */
    public void getTaskFile(DB db, int taskGroupID){
        try {
            String[] aux ;
            ArrayList<Task> temp = new ArrayList<>();
            String txt = FILES_PATH+ "tasks" + taskGroupID +".txt";
            File myObj = new File(txt);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                    aux=data.split(";");
                    String[] words = aux[7].split(",");
                    ArrayList<String> arrayListWord = new ArrayList<>(Arrays.asList(words));
                    temp = db.getTasks();
                TaskGroupImpl taskGroup = null;
                    for(TaskGroupImpl t:db.getTaskGroups())
                        if((t.getId()).compareTo(aux[13])==0){
                            taskGroup=t;
                        }
                    temp.add(new Task(parseInt(aux[0]),null, Boolean.parseBoolean(aux[2]), parseInt(aux[3]), parseInt(aux[4]), parseInt(aux[5]),aux[6],arrayListWord,aux[8],aux[9],aux[10], parseInt(aux[11]), parseInt(aux[12]),taskGroup));
            }
            myReader.close();
            db.setTasks(temp);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
