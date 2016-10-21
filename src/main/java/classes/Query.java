package classes;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Connection;
import model.SyncLogger;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.IOException;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
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

	private MongoClient from;
	private MongoClient to;

	private Object idFrom;
	private Object idTo;
	private int count;

	private SyncLogger syncLogger = SyncLogger.getInstance();

	/**
	 * Main constructor.
	 *
	 * @param clientFrom
	 *            Client
	 * @param clientTo
	 *            Client
	 * @param collection
	 *            Collection
	 */
	public Query(Client clientFrom, Client clientTo, Collection collection) {
		this.clientFrom = clientFrom;
		this.clientTo = clientTo;
		this.collection = collection;
	}

	public Query(MongoClient from, MongoClient to, Collection collection) {
		this.from = from;
		this.to = to;
		this.collection = collection;
	}

	public void oldWay() {
		connection = Connection.getInstance();
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
		count = Math.toIntExact(mongoCollection.count(and(gt("_id", new ObjectId(idFrom.toString())),
				lte("_id", new ObjectId(idTo.toString())))));
		if (count == -1) {
			count = 0;
		}
		collection.setDiff(count);
		collection.setResultFrom(idFrom);
		collection.setResultTo(idTo);
		String collectionStr = collection.getNameFinal();
		while (collectionStr.length() < 50) {
			collectionStr = collectionStr + " ";
		}
		String curl = collectionStr + "La diferencia es de : " + (count) + " documentos.";
		syncLogger.logMessage(curl, SyncLogger.ANSI_WHITE, true);
	}

	@Override
	public void run() {
	    //FIXME: if the connection is impossible this shouldn't runs
		database = collection.getDatabaseFinal();
		collectionName = collection.getNameFinal();
		mongoDatabase = to.getDatabase(database);
		mongoCollection = mongoDatabase.getCollection(collectionName);
		first = mongoCollection.find().sort(new BasicDBObject("_id", -1)).limit(1).first();
		idFrom = first.get("_id");
		collection.setResultFrom(idFrom);
		// mongoClient.close();
		mongoDatabase = from.getDatabase(database);
		mongoCollection = mongoDatabase.getCollection(collectionName);
		try {
			first = mongoCollection.find().sort(new BasicDBObject("_id", -1)).limit(1).first();
			idTo = first.get("_id");
			count = Math.toIntExact(mongoCollection.count(and(gt("_id", new ObjectId(idFrom.toString())),
					lte("_id", new ObjectId(idTo.toString())))));
			if (count == -1) {
				count = 0;
			}
			collection.setDiff(count);
			collection.setResultFrom(idFrom);
			collection.setResultTo(idTo);
			String collectionStr = collection.getNameFinal();
			while (collectionStr.length() < 50) {
				collectionStr = collectionStr + " ";
			}
			String curl = collectionStr + "La diferencia es de : " + (count) + " documentos.";
			syncLogger.logMessage(curl, SyncLogger.ANSI_WHITE, true);
		} catch (MongoTimeoutException e) {
			System.err.println(e);
		}
	}
}
