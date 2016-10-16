package classes;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import model.Connection;
import model.SyncLogger;
import org.bson.BSON;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lte;

/**
 * Created by eduardo on 23/09/16.
 */
public class Dump extends Thread {

    private Client clientFrom;
    private Client clientTo;
    private Collection collection;
    private Parameters parameters;
    private SyncLogger syncLogger = SyncLogger.getInstance();

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

/*
        //connection
        Connection connection = new Connection();
        MongoClient mongoClient = connection.getConnection(clientFrom);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(collection.getDatabaseOrigin());
        MongoCollection mongoCollection = mongoDatabase.getCollection(collection.getNameOrigin());

        //connection to
        Connection connection1 = new Connection();
        MongoClient mongoClient1 = connection1.getConnection(clientTo);
        MongoDatabase mongoDatabase1 = mongoClient1.getDatabase(collection.getDatabaseFinal());
        MongoCollection mongoCollection1 = mongoDatabase1.getCollection(collection.getNameFinal());

        //query
        List<BasicDBObject> basicDBObjects = new ArrayList<>();
        basicDBObjects.add(new BasicDBObject("_id", new BasicDBObject("$gt", new ObjectId(this.collection.getResultFrom().toString()))));
        basicDBObjects.add(new BasicDBObject("_id", new BasicDBObject("$lte", new ObjectId(this.collection.getResultTo().toString()))));
        BasicDBObject and = new BasicDBObject("$and", basicDBObjects);


        //resultSet
        FindIterable<Document> findIterable = mongoCollection.find(and);
        List<Document> documents = new ArrayList<>();
        for (Document document : findIterable){documents.add(document);}

        mongoCollection1.insertMany(documents);
*/

        //TODO: make dump process
/*
        File out = new File(parameters.getWorkingDirectory() + collection.getNameFinal() + ".bson");
        try {
            FileWriter fileWriter = new FileWriter(out);

            for(Document document : findIterable){
                System.out.println(document);
                fileWriter.write(document.toJson());
            }
            fileWriter.flush();



        } catch (IOException e) {
            e.printStackTrace();
        }
*/




        //Old way
        String command = "mongodump -h " + clientFrom.getHost() + " -d '" + collection.getDatabaseOrigin() + "' -c '" + collection.getNameFinal() + "' -q '{$and : [{_id : {$gt : ObjectId(\"" + collection.getResultFrom() + "\") }}, {_id : {$lte : ObjectId(\"" + collection.getResultTo() + "\") }}]}'";
        if(clientFrom.getPassword() != null && clientFrom.getUsername() != null && clientFrom.getAuthDB() != null){
            if(!clientFrom.getPassword().isEmpty() && !clientFrom.getUsername().isEmpty() && !clientFrom.getAuthDB().isEmpty()){
                command += " -u " + clientFrom.getUsername() + " -p " + clientFrom.getPassword() + " --authenticationDatabase " + clientFrom.getAuthDB() + "";
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
                in.lines().forEach(line -> syncLogger.logMessage("ID THREAD DUMP    : " + Thread.currentThread().getId() + " : " + line , SyncLogger.ANSI_BLUE, false));
                Thread.sleep(process.waitFor());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
