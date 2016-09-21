package factories;

import java.util.concurrent.ThreadFactory;

/**
 * Created by eduardo on 4/03/16.
 */
public class ThreadsFactory implements ThreadFactory {

    private static ThreadsFactory instance;

    private ThreadsFactory(){}

    public static ThreadsFactory getInstance() {
        return (instance == null) ? new ThreadsFactory() : instance;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }
}
