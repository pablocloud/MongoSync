package classes;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Connection;
import org.bson.Document;
import org.bson.types.ObjectId;

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

    /**
     * Main constructor.
     *  @param clientFrom Client
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
        System.out.println("PYLON HAMMER STARTED ON " + collection.getNameFinal() + " WILL ONLY GET 200 DOCUMENTS ON EACH CALL.");
        connection = new Connection();
        mongoClient = connection.getConnection(clientFrom);
        mongoClient1 = connection.getConnection(clientTo);
        mongoDatabase = mongoClient.getDatabase(collection.getDatabaseFinal());
        mongoCollection = mongoDatabase.getCollection(collection.getNameFinal());
        mongoDatabase1 = mongoClient1.getDatabase(collection.getDatabaseFinal());
        mongoCollection1 = mongoDatabase1.getCollection(collection.getNameFinal());
        int current = 0;
        FindIterable<Document> limit = mongoCollection.find(new BasicDBObject("_id", new BasicDBObject("$gt", new ObjectId("" + collection.getResultFrom() + "")))).sort(new BasicDBObject("_id", 1)).limit(maxDiff);
        for (Document doc : limit) {
            mongoCollection1.insertOne(doc);
            current++;
            if(current % 100 == 0){
                System.out.println("PYLON HAMMER STARTED ON " + collection.getNameFinal() + " ON POSITION " + current);
            }
        }
        mongoClient1.close();
        mongoClient.close();
    }
}
