package classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Config implements Serializable {

    @SerializedName("mongoFrom")
    private Client mongoFrom;
    @SerializedName("mongoTo")
    private Client mongoTo;
    @SerializedName("collections")
    private Collection[] collections;

    public Config() {

    }

    public Config(Client mongoFrom, Client mongoTo, Collection[] collections) {
        this.mongoFrom = mongoFrom;
        this.mongoTo = mongoTo;
        this.collections = collections;
    }

    public Client getMongoFrom() {
        return mongoFrom;
    }

    public void setMongoFrom(Client mongoFrom) {
        this.mongoFrom = mongoFrom;
    }

    public Client getMongoTo() {
        return mongoTo;
    }

    public void setMongoTo(Client mongoTo) {
        this.mongoTo = mongoTo;
    }

    public Collection[] getCollections() {
        return collections;
    }

    public void setCollections(Collection[] collections) {
        this.collections = collections;
    }
}
