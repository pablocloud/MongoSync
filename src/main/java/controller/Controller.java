package controller;

import classes.Config;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.net.URL;

public class Controller {


    public static void main(String... args) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource("config.json");
        assert resource != null;
        FileInputStream fis =  new FileInputStream(resource.getFile());
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        String json = sb.toString();
        Config config = new Gson().fromJson(json, Config.class);
        System.out.println(config.getMongoFrom().getHost());
        /*try {
            Config config = gson.fromJson("", Config.class);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*String database = "integration-engine_log";
        String collection = "trace_booking_2016_09";
        Connection connection = new Connection();
        Client clientTo = new Client("Sm2Server", "27017", "root", "menorcadesign", "admin");
        MongoClient mongoClient = connection.getConnection(clientTo);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collection);
        Document first = mongoCollection.find().sort(new BasicDBObject("_id", -1)).limit(1).first();
        Object idFrom = first.get("_id");
        mongoClient.close();
        Client clientFrom = new Client();
        clientFrom.setHost("mongo01-pmi");
        clientFrom.setPort("27017");
        mongoClient = connection.getConnection(clientFrom);
        mongoDatabase = mongoClient.getDatabase(database);
        mongoCollection = mongoDatabase.getCollection(collection);
        first = mongoCollection.find().sort(new BasicDBObject("_id", -1)).limit(1).first();
        Object idTo = first.get("_id");
        mongoClient.close();
        System.out.println("Debemos actualizar desde : ");
        System.out.println(idFrom);
        System.out.println("Hasta : ");
        System.out.println(idTo);
        File file = new File("/home/pablo/Descargas/Insertar a mongo/");
        String command = "mongodump -h "+clientFrom.getHost()+" -d '"+database+"' -c '"+collection+"' -q '{$and : [{_id : {$gte : ObjectId(\""+idFrom+"\") }}, {_id : {$lte : ObjectId(\""+idTo+"\") }}]}' --archive="+collection+".bson";
        String command2 = "mongorestore -h "+clientTo.getHost()+ " -u "+clientTo.getUsername()+" -p "+clientTo.getPassword()+" --authenticationDatabase "+clientTo.getAuthDb()+" -d "+database+" -c "+collection+" --archive="+collection+".bson";
        try {
            System.out.println(command);
            ProcessBuilder processBuilder = null;
            processBuilder.directory(file);
            Process process = processBuilder.start();
            process.waitFor();
            System.out.println(command2);
            processBuilder = new ProcessBuilder("/bin/bash", "-c", command2);
            processBuilder.directory(file);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/
    }

}