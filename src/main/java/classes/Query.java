package classes;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Connection;
import org.bson.Document;
import org.bson.types.ObjectId;

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
        System.out.println(collection.getNameFinal().toUpperCase() + " de " + idFrom + " hasta " + idTo + ". La diferencia es de : " + count + " documentos.");
    }
}
