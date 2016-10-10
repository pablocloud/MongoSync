package controller;

import classes.Config;
import com.google.gson.Gson;
import factories.ThreadsFactory;
import model.SyncLogger;
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

    public static String CONFIG_DIRECTORY = "/home/pablo/Descargas/Insertar a mongo/";
    public static String WORKING_DIRECTORY;

    private static final int concurrentThreads = 1;
    private static final ClassLoader CLASS_LOADER = Thread.currentThread().getContextClassLoader();
    private static final File configurationFile = new File(CLASS_LOADER.getResource("config.json").getFile());
    private static final SyncLogger logger = new SyncLogger();

    private static Config getConfig(File configurationFile) {
        URL resource = null;
        try {
            resource = new URL("file://" + new File(CONFIG_DIRECTORY + "config.json").getAbsolutePath());
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
        assert fis != null;
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
            ExecutorService executorService;
            //noinspection InfiniteLoopStatement
            while (true) {
                Config config = getConfig(configurationFile);
                WORKING_DIRECTORY = config.getParameters().getWorkingDirectory();
                String msg = "Colecciones a actualizar : " + config.getCollections().length;
                logger.logMessage(msg, SyncLogger.ANSI_WHITE, true);
                logger.logMessage("Diferencia máxima para dump/restore : " + config.getParameters().getMaxDiff() + ".", "", true);
                executorService = Executors.newFixedThreadPool(config.getCollections().length, ThreadsFactory.getInstance());
                ArrayList<Task> tasks = new ArrayList<>();
                Arrays.stream(config.getCollections()).forEach(collection -> tasks.add(new Task(config.getMongoFrom(), config.getMongoTo(), collection, config.getParameters().getMaxDiff())));
                tasks.forEach(executorService::execute);
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                msg = "-";
                while (msg.length() < 100) {
                    msg = msg + "-";
                }
                logger.logMessage(msg, SyncLogger.ANSI_WHITE, true);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            String curl = e.getMessage();
            logger.logMessage("@pablo.verdugo @eduardo.espinosa : " + curl, SyncLogger.ANSI_RED, true);
        }
    }

    /**
     * TODO: Build the queue priority manager method
     */

}