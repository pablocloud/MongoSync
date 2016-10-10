package classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pablo on 3/10/16.
 */
public class Parameters implements Serializable{

    @SerializedName("maxDiff")
    private int maxDiff;

    @SerializedName("workingDirectory")
    private String workingDirectory;

    public Parameters() {

    }

    public Parameters(int maxDiff, String workingDirectory) {
        this.maxDiff = maxDiff;
        this.workingDirectory = workingDirectory;
    }

    public int getMaxDiff() {
        return maxDiff;
    }

    public void setMaxDiff(int maxDiff) {
        this.maxDiff = maxDiff;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}
