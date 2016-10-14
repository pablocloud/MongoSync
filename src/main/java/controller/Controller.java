package controller;

import classes.Config;
import classes.Parameters;
import com.google.gson.Gson;
import com.jcraft.jsch.ChannelSftp;
import factories.ThreadsFactory;
import model.Ssh;
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
    private static String WORKING_DIRECTORY;
    private static final int concurrentThreads = 1;
    private static SyncLogger syncLogger;

    private final static String CONFIG = "mongosync.json";

    private final static String SSH_HOST = "";
    private final static String SSH_USERNAME = "";
    private final static String SSH_PASSWORD = "";


    private static Config getConfig(File configurationFile) {
        URL resource = null;
        StringBuilder sb = new StringBuilder();

        try {
            resource = new URL("file://" + configurationFile.getAbsolutePath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assert resource != null;
        try {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(resource.getFile())));
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Gson().fromJson(sb.toString(), Config.class);
    }

    public static void main(String... args) throws IOException, InterruptedException {
        syncLogger = SyncLogger.getInstance();
        try {
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.SEVERE);
            ExecutorService executorService;
            // noinspection InfiniteLoopStatement
            while (true) {
                Ssh ssh = new Ssh();
                ChannelSftp sftp = ssh.Connect(SSH_HOST, SSH_USERNAME, SSH_PASSWORD);
                Config config = new Gson().fromJson(new InputStreamReader(sftp.get(CONFIG)), Config.class);
                sftp.disconnect();
                Parameters parameters = config.getParameters();
                syncLogger.setParameters(parameters);
                WORKING_DIRECTORY = config.getParameters().getWorkingDirectory();
                String msg = "Colecciones a actualizar : " + config.getCollections().length;
                syncLogger.logMessage(msg, SyncLogger.ANSI_WHITE, false);
                syncLogger.logMessage("Diferencia máxima para dump/restore : " + config.getParameters().getMaxDiff() + ".",
                        "", false);
                executorService = Executors.newFixedThreadPool(config.getCollections().length,
                        ThreadsFactory.getInstance());
                ArrayList<Task> tasks = new ArrayList<>();
                Arrays.stream(config.getCollections()).forEach(
                        collection -> tasks.add(new Task(config.getMongoFrom(), config.getMongoTo(), collection,
                                parameters, config.getParameters().getMaxDiff())));
                tasks.forEach(executorService::execute);
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                for(msg = "-"; msg.length() < 100; msg = msg + "-");
                syncLogger.logMessage(msg, SyncLogger.ANSI_WHITE, false);
                Thread.sleep(1000);
                System.gc();
            }
        } catch (Exception e) {
            String curl = e.getMessage();
            syncLogger.logMessage("@pablo.verdugo @eduardo.espinosa : " + curl, SyncLogger.ANSI_RED, true);
        }
    }

    /**
     * TODO: Build the queue priority manager method
     */

}