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
        int contadorCurl = 0;
        while (true) {
            Config config = getConfig(configurationFile);
            System.out.println("Colecciones a actualizar : " + config.getCollections().length);
            executorService = Executors.newFixedThreadPool(config.getCollections().length, ThreadsFactory.getInstance());
            ArrayList<Task> tasks = new ArrayList<>();
            Arrays.stream(config.getCollections()).forEach(collection -> tasks.add(new Task(config.getMongoFrom(), config.getMongoTo(), collection)));
            tasks.forEach(executorService::execute);
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            System.out.println("EXECUTOR DEAD");
            contadorCurl++;
            if (contadorCurl >= 100) {
                String curl = "Colecciones en sincronización : ";
                for (Collection collection : config.getCollections()) {
                    curl += collection.getDatabaseOrigin() + " " + collection.getNameOrigin() + ". ";
                }
                ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", "curl -X POST --data-urlencode 'payload={\"text\" : \"" + curl + "\", \"channel\" : \"#monguitotrace\"}' https://hooks.slack.com/services/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");
                processBuilder.directory(new File("/home/pablo/Descargas/Insertar a mongo/"));
                Process process;
                try {
                    process = processBuilder.start();
                    while (process.isAlive()) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        in.lines().forEach(line -> System.out.println(" ID CURL : " + String.valueOf(Thread.currentThread().getId()) + " " + line));
                        Thread.sleep(process.waitFor());
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                contadorCurl = 0;
            }
            Thread.sleep(1000);
        }
    }

    /**
     * TODO: Build the queue priority manager method
     */

}