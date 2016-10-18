package classes;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import model.Connection;
import model.SyncLogger;
import org.bson.Document;

import java.time.Instant;

public class Index extends Thread {

    private Client client;
    private Collection collection;
    private IndexField index;
    private SyncLogger syncLogger = SyncLogger.getInstance();

    private MongoClient to;

    public Index (Client client, Collection collection, IndexField index){
        this.client = client;
        this.collection = collection;
        this.index = index;
    }

    public Index (MongoClient to, Collection collection, IndexField index){
        this.to = to;
        this.collection = collection;
        this.index = index;
    }

    private void oldWay(){
        MongoClient mongoClient = Connection.getInstance().getConnection(client);
        MongoDatabase database = mongoClient.getDatabase(collection.getDatabaseFinal());
        MongoCollection<Document> collectionDb = database.getCollection(this.collection.getNameFinal());
        syncLogger.logMessage("ID THREAD INDEX   : " + String.valueOf(Thread.currentThread().getId()) + " : " + Instant.now().toString() + "     " + collection.getNameFinal(), SyncLogger.ANSI_CYAN, false);
        collectionDb.createIndex(new BasicDBObject(this.index.getName(), this.index.getOrder()), new IndexOptions().background(true));
        mongoClient.close();
    }



    @Override
    public void run() {
        MongoDatabase database = to.getDatabase(collection.getDatabaseFinal());
        MongoCollection<Document> collectionDb = database.getCollection(this.collection.getNameFinal());
        syncLogger.logMessage("ID THREAD INDEX   : " + String.valueOf(Thread.currentThread().getId()) + " : " + Instant.now().toString() + "     " + collection.getNameFinal(), SyncLogger.ANSI_CYAN, false);
        collectionDb.createIndex(new BasicDBObject(this.index.getName(), this.index.getOrder()), new IndexOptions().background(true));
    }
}
