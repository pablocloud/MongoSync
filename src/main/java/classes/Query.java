package classes;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Connection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;

/**
 * Created by eduardo on 29/09/16.
 */
public class Query extends Thread {

    private Client clientFrom;
    private Client clientTo;
    private Collection collection;

    Connection connection;
    String database;
    String collectionName;
    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;
    Document first;

    private Object idFrom;
    private Object idTo;
    private int count;

    /**
     * Main constructor.
     *
     * @param clientFrom Client
     * @param clientTo   Client
     * @param collection Collection
     */
    public Query(Client clientFrom, Client clientTo, Collection collection) {
        this.clientFrom = clientFrom;
        this.clientTo = clientTo;
        this.collection = collection;
    }

    @Override
    public void run() {
        try {
            connection = new Connection();
            database = collection.getDatabaseFinal();
            collectionName = collection.getNameFinal();
            mongoClient = connection.getConnection(clientTo);
            mongoDatabase = mongoClient.getDatabase(database);
            mongoCollection = mongoDatabase.getCollection(collectionName);
            first = mongoCollection.find().sort(new BasicDBObject("_id", -1)).limit(1).first();
            idFrom = first.get("_id");
            collection.setResultFrom(idFrom);
            mongoClient.close();
            mongoClient = connection.getConnection(clientFrom);
            mongoDatabase = mongoClient.getDatabase(database);
            mongoCollection = mongoDatabase.getCollection(collectionName);
            first = mongoCollection.find().sort(new BasicDBObject("_id", -1)).limit(1).first();
            idTo = first.get("_id");
            count = Math.toIntExact(mongoCollection.count(and(gte("_id", new ObjectId(idFrom.toString())), lte("_id", new ObjectId(idTo.toString())))));
            collection.setDiff(count);
            collection.setResultFrom(idFrom);
            collection.setResultTo(idTo);
            String curl = collection.getNameFinal().toUpperCase() + ". La diferencia es de : " + (count - 1) + " documentos.";
            System.out.println(curl);
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "curl -X POST --data-urlencode 'payload={\"text\" : \"" + curl + "\", \"channel\" : \"#monguitotrace\"}' url");
            processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
            Process process;
            process = processBuilder.start();
            while (process.isAlive()) {
                Thread.sleep(process.waitFor());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            String curl = e.getMessage();
            System.out.println(curl);
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "curl -X POST --data-urlencode 'payload={\"text\" : \"" + curl + "\", \"channel\" : \"#monguitotrace\"}' url");
            processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
            Process process;
            try {
                process = processBuilder.start();
                while (process.isAlive()) {
                    Thread.sleep(process.waitFor());
                }
            } catch (IOException | InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }
}
