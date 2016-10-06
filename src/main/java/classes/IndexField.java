package classes;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class IndexField implements Serializable {

    @SerializedName("name")
    private String name;
    @SerializedName("order")
    private int order;

    public IndexField(){

    }

    public IndexField(String name, int order) {
        this.name = name;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
