package classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by pablo on 3/10/16.
 */
public class Parameters implements Serializable{

    @SerializedName("maxDiff")
    private int maxDiff;

    public Parameters() {

    }

    public Parameters(int maxDiff) {
        this.maxDiff = maxDiff;
    }

    public int getMaxDiff() {
        return maxDiff;
    }

    public void setMaxDiff(int maxDiff) {
        this.maxDiff = maxDiff;
    }
}
