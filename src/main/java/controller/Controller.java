package controller;

import classes.Client;
import classes.Collection;
import classes.Config;
import classes.TheLast;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    private static final int concurrentThreads = 1;
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final File configurationFile = new File(CLASS_LOADER.getResource("config.json").getFile());

    private static Config getConfig(File configurationFile) {
        URL resource = null;
        try {
            resource = new URL("file://" + new File("/home/pablo/Descargas/Insertar a mongo/config.json").getAbsolutePath());
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

    public static void main(String... args) throws IOException, InterruptedException {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.SEVERE);
        //noinspection InfiniteLoopStatement
        ExecutorService executorService;
        while (true) {
            Config config = getConfig(configurationFile);
            executorService = Executors.newFixedThreadPool(config.getCollections().length, ThreadsFactory.getInstance());
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

            ArrayList<Task> tasks = new ArrayList<>();
            Arrays.stream(config.getCollections()).forEach(collection -> tasks.add(new Task(config.getMongoFrom(), config.getMongoTo(), collection)));
            tasks.forEach(executorService::execute);
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            System.out.println("EXECUTOR DEAD");

            Thread.sleep(1000);

        }

    }

    /**
     * TODO: Build the queue priority manager method
     */

}