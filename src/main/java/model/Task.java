package model;

import classes.Client;
import classes.Collection;
import classes.Dump;
import classes.Restore;
import factories.ThreadsFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Task extends Thread {

    private Client clientFrom;
    private Client clientTo;
    private Collection collection;
    private ExecutorService executorService;

    public Task(Client clientFrom, Client clientTo, Collection collection) {
        this.clientFrom = clientFrom;
        this.clientTo = clientTo;
        this.collection = collection;
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

        ArrayList<Thread> threadList = new ArrayList<>();
        threadList.add(new Dump(clientFrom, clientTo, collection));
        threadList.add(new Restore(clientTo, collection));
        threadList.forEach(executorService::execute);
/*        Dump dump = new Dump(clientFrom, clientTo, collection);
        dump.run();
        *//*while(dump.isAlive()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*//*
        //executorService.execute(dump);
        Restore restore = new Restore(clientTo, collection);
        restore.run();*/

    }
}
