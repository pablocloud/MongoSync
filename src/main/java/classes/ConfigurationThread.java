package classes;

import model.Connection;

import java.util.concurrent.ExecutorService;

/**
 * Created by eduardo on 29/09/16.
 */
public class ConfigurationThread {
    private ExecutorService executorService;
    private Connection connection;


    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
