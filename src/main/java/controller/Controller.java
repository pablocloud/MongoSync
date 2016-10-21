package controller;

import classes.Config;
import classes.Parameters;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import factories.ThreadsFactory;
import model.Connection;
import model.SyncLogger;
import model.Task;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    private static String WORKING_DIRECTORY;
    private static final int concurrentThreads = 1;
    private static SyncLogger syncLogger;
    private static ExecutorService executorService;

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
            // noinspection InfiniteLoopStatement
            while (true) {
                Config config = getConfig(new File(args[0]));
                Parameters parameters = config.getParameters();
                //TODO: Create connection instance here.
                Connection connection = Connection.getInstance();

                //TODO: build mongoClients here, and use it in all transactions
                MongoClient from = connection.getConnection(config.getMongoFrom());
                MongoClient to = connection.getConnection(config.getMongoTo());

                syncLogger.setParameters(parameters);
                WORKING_DIRECTORY = config.getParameters().getWorkingDirectory();
                String msg = "Colecciones a actualizar : " + config.getCollections().length;
                syncLogger.logMessage(msg, SyncLogger.ANSI_WHITE, true);
                syncLogger.logMessage("Diferencia máxima para dump/restore : " + config.getParameters().getMaxDiff() + ".",
                        "", true);
                executorService = Executors.newFixedThreadPool(config.getCollections().length,
                        ThreadsFactory.getInstance());

                ArrayList<Task> tasks = new ArrayList<>();
/*                Arrays.stream(config.getCollections()).forEach(
                        collection -> tasks.add(new Task(config.getMongoFrom(), config.getMongoTo(), collection,
                                parameters, config.getParameters().getMaxDiff())));*/

                Arrays.stream(config.getCollections()).forEach(collection -> tasks.add(new Task(from, to, collection, parameters, config.getParameters().getMaxDiff())));

//                tasks.forEach(executorService::execute);

                //List<FutureTask<Task>> futureTasks = new ArrayList<>();

                tasks.forEach(task -> {
                    FutureTask<Task> futureTask = (FutureTask) executorService.submit(task);
                   // futureTasks.add(futureTask);
                   // System.out.println(task.getName() + " " + futureTask.isDone());
                });


  /*              futureTasks.forEach(taskFutureTask -> {
                    System.out.println(taskFutureTask.isDone());
                });*/

            /*    tasks.forEach(task -> {
                    System.out.println(task.getExecutorService().);
                });
*/
                System.out.println(tasks.size());

                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                msg = "-";
                while (msg.length() < 100) {
                    msg = msg + "-";
                }
                syncLogger.logMessage(msg, SyncLogger.ANSI_WHITE, true);

                // Wait for a set time.
                Thread.sleep(1000);

                //FIXME: close connections
                from.close();
                to.close();

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