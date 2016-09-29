package classes;

import java.util.concurrent.ExecutorService;

/**
 * Created by eduardo on 29/09/16.
 */
public class ConfigurationThread {
    private ExecutorService executorService;

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
