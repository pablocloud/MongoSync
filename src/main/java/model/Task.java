package model;

import classes.Client;
import classes.Collection;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Task extends Thread {

    private Client clientFrom;
    private Client clientTo;
    private Collection collection;

    public Task(Client clientFrom, Client clientTo, Collection collection) {
        this.clientFrom = clientFrom;
        this.clientTo = clientTo;
        this.collection = collection;
    }

    @Override
    public void run() {
        String command = "mongodump -h " + clientFrom.getHost() + " -d '" + collection.getDatabaseOrigin() + "' -c '" + collection.getNameFinal() + "' -q '{$and : [{_id : {$gte : ObjectId(\"" + collection.getResultFrom() + "\") }}, {_id : {$lte : ObjectId(\"" + collection.getResultTo() + "\") }}]}' --archive=" + collection.getNameFinal() + ".bson";
        String command2 = "mongorestore -h " + clientTo.getHost() + " -u " + clientTo.getUsername() + " -p " + clientTo.getPassword() + " --authenticationDatabase " + clientTo.getAuthDb() + " -d " + collection.getDatabaseFinal() + " -c " + collection.getNameFinal() + " --archive=" + collection.getNameFinal() + ".bson";
        System.out.println(command);
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
        processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
        Process process = null;
        try {
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(command2);
        processBuilder = new ProcessBuilder("/bin/bash", "-c", command2);
        processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
        try {
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
