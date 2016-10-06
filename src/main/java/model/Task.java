package model;

import classes.Client;
import classes.Collection;
import classes.Dump;
import classes.Index;
import classes.IndexField;
import classes.PylonHammer;
import classes.Query;
import classes.Restore;
import factories.ThreadsFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Task extends Thread {

    private Client clientFrom;
    private Client clientTo;
    private Collection collection;
    private ExecutorService executorService;
    private int maxDiff;

    public Task(Client clientFrom, Client clientTo, Collection collection, int maxDiff) {
        this.clientFrom = clientFrom;
        this.clientTo = clientTo;
        this.collection = collection;
        this.maxDiff = maxDiff;
    }

    public Client getClientFrom() {
        return clientFrom;
    }

    public void setClientFrom(Client clientFrom) {
        this.clientFrom = clientFrom;
    }

    public Client getClientTo() {
        return clientTo;
    }

    public void setClientTo(Client clientTo) {
        this.clientTo = clientTo;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newSingleThreadExecutor(ThreadsFactory.getInstance());
        try {
            Query query = new Query(clientFrom, clientTo, collection);
            query.run();
            while (query.isAlive()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (collection.getDiff() < maxDiff) {
            ArrayList<Thread> threadList = new ArrayList<>();
            threadList.add(new Dump(clientFrom, clientTo, collection));
            threadList.add(new Restore(clientTo, collection));
            threadList.forEach(Thread::run);
        } else {
            ArrayList<Thread> threadList = new ArrayList<>();
            threadList.add(new PylonHammer(getClientFrom(), getClientTo(), getCollection(), maxDiff));
            threadList.forEach(Thread::run);
        }
        if(collection.getIndexes() != null){
            for(IndexField indexField : collection.getIndexes()){
                new Index(clientTo, collection, indexField).run();
            }
        }
    }
}
