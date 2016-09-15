package model;

import classes.Client;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import java.util.Objects;

public class Connection {

    /**
     * Returns a MongoClient with the config data from Client.
     *
     * @param client client.
     * @return MongoClient.
     */
    public MongoClient getConnection(Client client) {
        String connection;
        if (!Objects.equals(client.getUsername(),null) && !Objects.equals(client.getPassword(),null)) {
            connection = "mongodb://" + client.getUsername() + ":" + client.getPassword() + "@" + client.getHost() + ":" + client.getPort();
        } else {
            connection = "mongodb://" + client.getHost() + ":" + client.getPort();
        }
        MongoClientURI uri = new MongoClientURI(connection);
        return new MongoClient(uri);
    }

}
