package model;

import classes.*;
import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import factories.ThreadsFactory;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Task extends Thread {

    private Client clientFrom;
    private Client clientTo;
    private Collection collection;
    private Parameters parameters;
    private ExecutorService executorService;
    private int maxDiff;
    private MongoClient from;
    private MongoClient to;

    public Task() {
    }

    public Task(Client clientFrom, Client clientTo, Collection collection, Parameters parameters, int maxDiff) {
        this.clientFrom = clientFrom;
        this.clientTo = clientTo;
        this.collection = collection;
        this.parameters = parameters;
        this.maxDiff = maxDiff;
    }

    public Task(MongoClient from, MongoClient to, Collection collection, Parameters parameters, int maxDiff) {
        this.from = from;
        this.to = to;
        this.collection = collection;
        this.parameters = parameters;
        this.maxDiff = maxDiff;
        this.setName(collection.getNameFinal());
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

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void oldWay(){
        ExecutorService executorService = Executors.newSingleThreadExecutor(ThreadsFactory.getInstance());
        // Check is collection exist
        MongoClient mongoClientFinal = Connection.getInstance().getConnection(clientTo);
        MongoDatabase database = mongoClientFinal.getDatabase(collection.getDatabaseFinal());
        MongoIterable<String> strings = database.listCollectionNames();
        boolean exist = false;
        for (String col : strings) {
            if (col.equals(collection.getNameFinal())) {
                exist = true;
            }
        }
        // Case false create it
        if (!exist) {
            database.createCollection(collection.getNameFinal());
        }

        // Get records
        MongoCollection<Document> collectionResult = database.getCollection(this.collection.getNameFinal());
        // If no records inject the first
        if (collectionResult.count() <= 0) {
            MongoClient mongoClientOrigin = Connection.getInstance().getConnection(clientFrom);
            MongoDatabase databaseOrigin = mongoClientOrigin.getDatabase(collection.getDatabaseOrigin());
            MongoCollection<Document> collectionOrigin = databaseOrigin.getCollection(this.collection.getNameOrigin());
            Document id = collectionOrigin.find().sort(new BasicDBObject("_id", 1)).limit(1).first();
            collectionResult.insertOne(id);
            mongoClientOrigin.close();
        }
        mongoClientFinal.close();
        try {
            Query query = new Query(clientFrom, clientTo, this.collection);
            query.run();
            while (query.isAlive()) {
                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.collection.getDiff() > 0) {
            if (this.collection.getDiff() < maxDiff) {
                ArrayList<Thread> threadList = new ArrayList<>();
                threadList.add(new Dump(clientFrom, clientTo, this.collection, parameters));
                threadList.add(new Restore(clientTo, this.collection, parameters));
                threadList.forEach(Thread::run);
            } else {
                ArrayList<Thread> threadList = new ArrayList<>();
                threadList.add(new PylonHammer(getClientFrom(), getClientTo(), getCollection(), maxDiff));
                threadList.forEach(Thread::run);
            }
            // TODO: if the index exist don't call the Index class or chose the right index to build
            if (this.collection.getIndexes() != null) {
                for (IndexField indexField : this.collection.getIndexes()) {
                    new Index(clientTo, this.collection, indexField).run();
                }
            }
        }
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newSingleThreadExecutor(ThreadsFactory.getInstance());
        // Check is collection exist
        MongoDatabase database = to.getDatabase(collection.getDatabaseFinal());
        MongoIterable<String> strings = database.listCollectionNames();
/*        boolean exist = false;
        for (String col : strings) {
            if (col.equals(collection.getNameFinal())) {
                exist = true;
            }
        }*/
        // Case false create it
/*        if (!exist) {
            database.createCollection(collection.getNameFinal());
        }*/

        // Get records
        MongoCollection<Document> collectionResult = database.getCollection(this.collection.getNameFinal());
        // If no records inject the first
        if (collectionResult.count() <= 0) {
            MongoDatabase databaseOrigin = from.getDatabase(collection.getDatabaseOrigin());
            MongoCollection<Document> collectionOrigin = databaseOrigin.getCollection(this.collection.getNameOrigin());
            Document id = collectionOrigin.find().sort(new BasicDBObject("_id", 1)).limit(1).first();
            collectionResult.insertOne(id);
//            mongoClientOrigin.close();
        }
//        mongoClientFinal.close();
        try {
            Query query = new Query(from, to, this.collection);
            query.run();
            while (query.isAlive()) {
                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.collection.getDiff() > 0) {
           /* if (this.collection.getDiff() < maxDiff) {
                ArrayList<Thread> threadList = new ArrayList<>();
                threadList.add(new Dump(clientFrom, clientTo, this.collection, parameters));
                threadList.add(new Restore(clientTo, this.collection, parameters));
                threadList.forEach(Thread::run);
            } else {*/
                ArrayList<Thread> threadList = new ArrayList<>();
                threadList.add(new PylonHammer(from, to, getCollection(), maxDiff));
                threadList.forEach(Thread::run);
            //}
        }
        // TODO: if the index exist don't call the Index class or chose the right index to build
        if (this.collection.getIndexes() != null) {
            for (IndexField indexField : this.collection.getIndexes()) {
                new Index(to, this.collection, indexField).run();
            }
        }
        // Show my name
        System.out.println(this.getName());
    }
}
