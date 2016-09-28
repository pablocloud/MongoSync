package classes;

import java.io.File;
import java.io.IOException;

/**
 * Created by eduardo on 23/09/16.
 */
public class Dump extends Thread {
    private Client clientFrom;
    private Client clientTo;
    private Collection collection;

    public Dump(){}

    public Dump(Client from, Client to, Collection collection){
        this.clientFrom = from;
        this.clientTo = to;
        this.collection = collection;
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
        String command = "mongodump -h " + clientFrom.getHost() + " -d '" + collection.getDatabaseOrigin() + "' -c '" + collection.getNameFinal() + "' -q '{$and : [{_id : {$gte : ObjectId(\"" + collection.getResultFrom() + "\") }}, {_id : {$lte : ObjectId(\"" + collection.getResultTo() + "\") }}]}' --archive=" + collection.getNameFinal() + ".bson";
        System.out.println(command);
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
        processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
        Process process;
        try {
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}