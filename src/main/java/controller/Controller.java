package controller;

import classes.Collection;
import classes.Config;
import classes.Query;
import com.google.gson.Gson;
import factories.ThreadsFactory;
import model.Task;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
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
        try {
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");

            mongoLogger.setLevel(Level.SEVERE);
            //noinspection InfiniteLoopStatement
            ExecutorService executorService;
            while (true) {
                Config config = getConfig(configurationFile);
                String curl = "Colecciones a actualizar : " + config.getCollections().length;
                System.out.println(curl);
                ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "curl -X POST --data-urlencode 'payload={\"text\" : \"" + curl + "\", \"channel\" : \"#monguitotrace\"}' url");
                processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
                Process process;
                process = processBuilder.start();
                while (process.isAlive()) {
                    Thread.sleep(process.waitFor());
                }
                executorService = Executors.newFixedThreadPool(config.getCollections().length, ThreadsFactory.getInstance());
                ArrayList<Task> tasks = new ArrayList<>();
                Arrays.stream(config.getCollections()).forEach(collection -> tasks.add(new Task(config.getMongoFrom(), config.getMongoTo(), collection, config.getParameters().getMaxDiff())));
                tasks.forEach(executorService::execute);
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                System.out.println("EXECUTOR DEAD");
                curl = "-------------------------------------------------";
                processBuilder = new ProcessBuilder("/bin/bash", "-c", "curl -X POST --data-urlencode 'payload={\"text\" : \"" + curl + "\", \"channel\" : \"#monguitotrace\"}' url");
                processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
                process = processBuilder.start();
                while (process.isAlive()) {
                    Thread.sleep(process.waitFor());
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            String curl = e.getMessage();
            System.out.println(curl);
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "curl -X POST --data-urlencode 'payload={\"text\" : \"" + curl + "\", \"channel\" : \"#monguitotrace\"}' url");
            processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
            Process process;
            process = processBuilder.start();
            while (process.isAlive()) {
                Thread.sleep(process.waitFor());
            }
        }
    }

    /**
     * TODO: Build the queue priority manager method
     */

}