package classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Client implements Serializable {

    @SerializedName("host")
    private String host;
    @SerializedName("port")
    private String port;
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("authDB")
    private String authDB;

    public Client() {

    }

    public Client(String host, String port, String username, String password, String authDB) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.authDB = authDB;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthDB() {
        return authDB;
    }

    public void setAuthDB(String authDB) {
        this.authDB = authDB;
    }

}
