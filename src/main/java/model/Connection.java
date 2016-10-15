package model;

import classes.Client;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.Objects;

public class Connection {

    private Client client;

    /**
     * Returns a MongoClient with the config data from Client.
     *
     * @param client client.
     * @return MongoClient.
     */
    public MongoClient getConnection(Client client) {
        this.client = new Client();
        String connection = "";

        try {
            if (client.getHost() != null && !client.getHost().isEmpty()) {
                this.client.setHost(client.getHost());
            } else {
                throw new Exception("Server not set");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            if (client.getPort() != null && !client.getPort().isEmpty()) {
                this.client.setPort(client.getPort());
            } else {
                throw new Exception("Port not set");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }

        try {
            if ((client.getUsername() == null || client.getUsername().isEmpty()) && (client.getPassword() == null || client.getPassword().isEmpty()) && (client.getAuthDB() == null || client.getAuthDB().isEmpty())) {
                connection = "mongodb://" + this.client.getHost() + ":" + this.client.getPort();
            } else if ((client.getUsername() != null && !client.getUsername().isEmpty()) && (client.getPassword() != null && !client.getPassword().isEmpty()) && (client.getAuthDB() != null && !client.getAuthDB().isEmpty())) {
                this.client.setUsername(client.getUsername());
                this.client.setPassword(client.getPassword());
                this.client.setAuthDB(client.getAuthDB());
                connection = "mongodb://" + this.client.getUsername() + ":" + this.client.getPassword() + "@" + this.client.getHost() + ":" + this.client.getPort();
            } else {
                throw new Exception("User, password and/or authDB not set");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(3);
        }
        return new MongoClient(new MongoClientURI(connection));
    }
}
