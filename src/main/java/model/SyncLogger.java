package model;

import classes.Parameters;
import controller.Controller;

import java.io.File;
import java.io.IOException;

public class SyncLogger {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private Parameters parameters;
    private ProcessBuilder processBuilder;
    private Process process;

    private static SyncLogger syncLogger;

    private SyncLogger(){}

    public static SyncLogger getInstance(){
        if (syncLogger == null){
            syncLogger = new SyncLogger();
        }
        return syncLogger;
    }

    /**
     * First filter between LocalLog and a SlackLog.
     *
     * @param message   String
     * @param color     String
     * @param slackable boolean
     */
    public void logMessage(String message, String color, boolean slackable) {
        if (slackable) {
            localLog(message, color);
            slackLog(message);
        } else {
            localLog(message, color);
        }
    }

    /**
     * Prints the message in the console.
     *
     * @param message String.
     * @param color   String.
     */
    private void localLog(String message, String color) {
        System.out.println(color + message + ANSI_RESET);
    }

    /**
     * Sends a message to slack.
     *
     * @param message String
     */
    private void slackLog(String message) {
        processBuilder = new ProcessBuilder("/bin/bash", "-c", "curl -X POST --data-urlencode 'payload={\"username\": \"mongosync-menorca\",\"icon_emoji\": \":goku:\",\"text\" : \"" + message + "\", \"channel\" : \"#monguitotrace\"}' https://hooks.slack.com/services/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");
        processBuilder.directory(new File(parameters.getWorkingDirectory()));
        process = null;
        try {
            process = processBuilder.start();
            while (process.isAlive()) {
                Thread.sleep(process.waitFor());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
}
