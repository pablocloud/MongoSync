package classes;

import model.SyncLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by eduardo on 23/09/16.
 */
public class Dump extends Thread {

    private Client clientFrom;
    private Client clientTo;
    private Collection collection;
    private Parameters parameters;

    private SyncLogger logger = new SyncLogger();

    public Dump(){}

    public Dump(Client from, Client to, Collection collection, Parameters parameters){
        this.clientFrom = from;
        this.clientTo = to;
        this.collection = collection;
        this.parameters = parameters;
    }

    public Client getClientFrom() {
        return clientFrom;
    }

    public void setClientFrom(Client clientFrom) {
        this.clientFrom = clientFrom;
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
        String command = "mongodump -h " + clientFrom.getHost() + " -d '" + collection.getDatabaseOrigin() + "' -c '" + collection.getNameFinal() + "' -q '{$and : [{_id : {$gt : ObjectId(\"" + collection.getResultFrom() + "\") }}, {_id : {$lte : ObjectId(\"" + collection.getResultTo() + "\") }}]}' --archive=" + collection.getNameFinal() + ".bson";
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
        processBuilder.directory(new File(parameters.getWorkingDirectory()));
        Process process;
        try {
            process = processBuilder.start();
            while(process.isAlive()){
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                in.lines().forEach(line -> logger.logMessage("ID THREAD RESTORE : " + Thread.currentThread().getId() + " : " + line , SyncLogger.ANSI_BLUE, false));
                Thread.sleep(process.waitFor());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
