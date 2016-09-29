package controller;

import classes.Client;
import classes.Config;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import model.Connection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubController {

    public static void main(String... args) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = null;
        try {
            resource = new URL("file://" + new File("/home/pablo/Descargas/Insertar a mongo/config.json").getAbsolutePath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
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
        Config config = new Gson().fromJson(json, Config.class);
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        while (true) {
            try {
                Connection connection = new Connection();

                String database = "front_log";
                String collection = "trace_booking_process_2016_09";
                Client clientTo = config.getMongoTo();
                MongoClient mongoClient = connection.getConnection(clientTo);
                MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
                MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
                Document first = mongoCollection.find().sort(new BasicDBObject("_id", -1)).limit(1).first();
                Object idFrom = first.get("_id");
                mongoClient.close();
                Client clientFrom = config.getMongoFrom();
                mongoClient = connection.getConnection(clientFrom);
                MongoClient connection1 = connection.getConnection(clientTo);
                mongoDatabase = mongoClient.getDatabase(database);
                mongoCollection = mongoDatabase.getCollection(collection);
                MongoDatabase mongoDatabase1 = connection1.getDatabase(database);
                MongoCollection<Document> mongoCollection1 = mongoDatabase1.getCollection(collection);
                FindIterable<Document> limit = mongoCollection.find(new BasicDBObject("_id", new BasicDBObject("$gt", new ObjectId("" + idFrom + "")))).sort(new BasicDBObject("_id", 1)).limit(10000);
                for (Document doc : limit) {
                    first = doc;
                    mongoCollection1.insertOne(doc);
                }
                Object idTo = first.get("_id");
                System.out.println(collection + " de " + idFrom + " hasta " + idTo);
                mongoClient.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            /*String command = "mongodump -h " + clientFrom.getHost() + " -d 'front_log' -c 'trace_booking_process_2016_09' -q '{$and : [{_id : {$gte : ObjectId(\"" + idFrom + "\") }}, {_id : {$lte : ObjectId(\"" + idTo + "\") }}]}' --archive=trace_booking_process_2016_08.bson";
            String command2 = "mongorestore -h " + clientTo.getHost() + " -u " + clientTo.getUsername() + " -p " + clientTo.getPassword() + " --authenticationDatabase " + clientTo.getAuthDb() + " -d front_log -c trace_booking_process_2016_08 --archive=trace_booking_process_2016_08.bson";
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
            }*/
            /*try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

}
