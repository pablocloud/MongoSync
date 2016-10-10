package classes;

import model.SyncLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by eduardo on 23/09/16.
 */
public class Restore extends Thread {

    private Client clientTo;
    private Collection collection;
    private Parameters parameters;
    private SyncLogger syncLogger = SyncLogger.getInstance();

    public Restore(){}

    public Restore(Client to, Collection collection, Parameters parameters){
        this.clientTo = to;
        this.collection = collection;
        this.parameters = parameters;
    }

    public Client getClientTo() {
        return clientTo;
    }

    public void setClientTo(Client clientTo) {
        this.clientTo = clientTo;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    @Override
    public void run() {
        String command = "mongorestore -h " + clientTo.getHost() + " -d " + collection.getDatabaseFinal() + " -c " + collection.getNameFinal();
        if(clientTo.getPassword() != null && clientTo.getUsername() != null && clientTo.getAuthDB() != null){
            if(!clientTo.getPassword().isEmpty() && !clientTo.getUsername().isEmpty() && !clientTo.getAuthDB().isEmpty()){
                command += " -u " + clientTo.getUsername() + " -p " + clientTo.getPassword() + " --authenticationDatabase " + clientTo.getAuthDB();
            }
        }
        command += " --archive=" + collection.getNameFinal() + ".bson";
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
        processBuilder.directory(new File(parameters.getWorkingDirectory()));
        Process process;
        try {
            process = processBuilder.start();
            while(process.isAlive()){
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                in.lines().forEach(line -> syncLogger.logMessage("ID THREAD RESTORE : " + Thread.currentThread().getId() + " : " + line , SyncLogger.ANSI_GREEN, false));
                Thread.sleep(process.waitFor());
            }
        } catch (IOException | InterruptedException e) {
            syncLogger.logMessage(e.getMessage(), "", true);
        }
    }
}
