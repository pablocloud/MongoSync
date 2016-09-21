package controller;

import classes.Client;
import classes.Collection;
import classes.Config;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Connection;
import model.Task;
import org.bson.Document;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    public static void main(String... args) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = new URL("file://" + new File("/home/pablo/Descargas/Insertar a mongo/config.json").getAbsolutePath());
        assert resource != null;
        //noinspection InfiniteLoopStatement
        while (true) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            FileInputStream fis = new FileInputStream(resource.getFile());
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            Config config = new Gson().fromJson(json, Config.class);
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
            for(int i = 0; i < config.getCollections().length; i++){
                Client clientFrom = config.getMongoFrom();
                Client clientTo = config.getMongoTo();
                Collection collection = config.getCollections()[i];
                String command = "mongodump -h " + clientFrom.getHost() + " -d '" + collection.getDatabaseOrigin() + "' -c '" + collection.getNameFinal() + "' -q '{$and : [{_id : {$gte : ObjectId(\"" + collection.getResultFrom() + "\") }}, {_id : {$lte : ObjectId(\"" + collection.getResultTo() + "\") }}]}' --archive=" + collection.getNameFinal() + ".bson";
                String command2 = "mongorestore -h " + clientTo.getHost() + " -u " + clientTo.getUsername() + " -p " + clientTo.getPassword() + " --authenticationDatabase " + clientTo.getAuthDb() + " -d " + collection.getDatabaseFinal() + " -c " + collection.getNameFinal() + " --archive=" + collection.getNameFinal() + ".bson";
                System.out.println(command);
                ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
                processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
                Process process = null;
                try {
                    process = processBuilder.start();
                    while(process.isAlive()){
                        Thread.sleep(process.waitFor());
                    }
                    process.destroy();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(command2);
                processBuilder = new ProcessBuilder("/bin/bash", "-c", command2);
                processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
                try {
                    process = processBuilder.start();
                    while(process.isAlive()){
                        Thread.sleep(process.waitFor());
                    }
                    process.destroy();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*Arrays.stream(config.getCollections()).forEach((collection) -> {
                Client clientFrom = config.getMongoFrom();
                Client clientTo = config.getMongoTo();

            });*/
            //executor.shutdown();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}