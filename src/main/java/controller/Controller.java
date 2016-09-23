package controller;

import classes.Client;
import classes.Collection;
import classes.Config;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import factories.ThreadsFactory;
import model.Connection;
import model.Task;
import org.bson.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    private static final int concurrentThreads = 3;
    private static final File configurationFile = new File("/home/pablo/Descargas/Insertar a mongo/config.json");

    private static Config getConfig(File configurationFile) {
        URL resource = null;
        try {
            resource = new URL("file://" + configurationFile.getAbsolutePath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assert resource != null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(resource.getFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = sb.toString();
        return new Gson().fromJson(json, Config.class);
    }

    public static void main(String... args) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads, ThreadsFactory.getInstance());

        //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        //noinspection InfiniteLoopStatement
        while (true) {
            Config config = getConfig(configurationFile);
            Arrays.stream(config.getCollections()).forEach((collectionFor) -> {
                try {
                    Connection connection = new Connection();
                    String database = collectionFor.getDatabaseFinal();
                    String collection = collectionFor.getNameFinal();
                    Client clientTo = config.getMongoTo();
                    MongoClient mongoClient = connection.getConnection(clientTo);
                    MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
                    MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
                    Document first = mongoCollection.find().sort(new BasicDBObject("_id", -1)).limit(1).first();
                    Object idFrom = first.get("_id");
                    collectionFor.setResultFrom(idFrom);
                    mongoClient.close();
                    Client clientFrom = config.getMongoFrom();
                    mongoClient = connection.getConnection(clientFrom);
                    mongoDatabase = mongoClient.getDatabase(database);
                    mongoCollection = mongoDatabase.getCollection(collection);
                    first = mongoCollection.find().sort(new BasicDBObject("_id", -1)).limit(1).first();
                    Object idTo = first.get("_id");
                    collectionFor.setResultTo(idTo);
                    System.out.println(collection + " de " + idFrom + " hasta " + idTo);
                    mongoClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

/*
            for(Collection c : config.getCollections()){
                Task task = new Task(config.getMongoFrom(), config.getMongoTo(), c);
                executorService.execute(task);
            }
*/

            Arrays.stream(config.getCollections()).forEach(collection -> executorService.execute(new Task(config.getMongoFrom(), config.getMongoTo(), collection)));
            executorService.shutdown();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * TODO: Build the queue priority manager method
     */

}