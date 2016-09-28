package classes;

import java.io.File;
import java.io.IOException;

/**
 * Created by eduardo on 23/09/16.
 */
public class Restore extends Thread {
    private Client clientTo;
    private Collection collection;

    public Restore(){}

    public Restore(Client to, Collection collection){
        this.clientTo = to;
        this.collection = collection;
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
        String command = "mongorestore -h " + clientTo.getHost() + " -u " + clientTo.getUsername() + " -p " + clientTo.getPassword() + " --authenticationDatabase " + clientTo.getAuthDb() + " -d " + collection.getDatabaseFinal() + " -c " + collection.getNameFinal() + " --archive=" + collection.getNameFinal() + ".bson";
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
