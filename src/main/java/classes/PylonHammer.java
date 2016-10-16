package classes;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Connection;
import model.SyncLogger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pablo on 30/09/16.
 */
public class PylonHammer extends Thread {

    private Client clientFrom;
    private Client clientTo;
    private Collection collection;

    Connection connection;
    String database;
    String collectionName;
    MongoClient mongoClient;
    MongoClient mongoClient1;
    MongoDatabase mongoDatabase;
    MongoDatabase mongoDatabase1;
    MongoCollection<Document> mongoCollection;
    MongoCollection<Document> mongoCollection1;
    private int maxDiff;

    private SyncLogger syncLogger = SyncLogger.getInstance();

    /**
     * Main constructor.
     *
     * @param clientFrom Client
     * @param clientTo   Client
     * @param collection Collection
     * @param maxDiff
     */
    public PylonHammer(Client clientFrom, Client clientTo, Collection collection, int maxDiff) {
        this.clientFrom = clientFrom;
        this.clientTo = clientTo;
        this.collection = collection;
        this.maxDiff = maxDiff;
    }

    @Override
    public void run() {
        syncLogger.logMessage("ID THREAD PYLON   : " + String.valueOf(Thread.currentThread().getId()) + " : " + Instant.now().toString() + "       " + collection.getNameFinal(), SyncLogger.ANSI_CYAN, false);
        connection = new Connection();
        mongoClient = connection.getConnection(clientFrom);
        mongoClient1 = connection.getConnection(clientTo);
        mongoDatabase = mongoClient.getDatabase(collection.getDatabaseFinal());
        mongoCollection = mongoDatabase.getCollection(collection.getNameFinal());
        mongoDatabase1 = mongoClient1.getDatabase(collection.getDatabaseFinal());
        mongoCollection1 = mongoDatabase1.getCollection(collection.getNameFinal());
        FindIterable<Document> documentFindIterable = mongoCollection.find(new BasicDBObject("_id", new BasicDBObject("$gt", new ObjectId("" + collection.getResultFrom() + "")))).sort(new BasicDBObject("_id", 1)).limit(maxDiff);
        List<Document> documents = new ArrayList<>();
        documentFindIterable.iterator().forEachRemaining(documents::add);
        mongoCollection1.insertMany(documents);
        mongoClient1.close();
        mongoClient.close();
        syncLogger.logMessage("ID THREAD PYLON   : " + String.valueOf(Thread.currentThread().getId()) + " : " + Instant.now().toString() + "       " + collection.getNameFinal() + " finished", SyncLogger.ANSI_CYAN, false);
    }
}
