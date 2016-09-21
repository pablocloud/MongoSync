package classes;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Collection implements Serializable {

    @SerializedName("databaseOrigin")
    private String databaseOrigin;
    @SerializedName("databaseFinal")
    private String databaseFinal;
    @SerializedName("nameOrigin")
    private String nameOrigin;
    @SerializedName("nameFinal")
    private String nameFinal;
    @SerializedName("fieldOrigin")
    private String fieldOrigin;
    @SerializedName("fieldFinal")
    private String fieldFinal;
    private Object resultFrom;
    private Object resultTo;

    public Collection() {

    }

    public Collection(String databaseOrigin, String databaseFinal, String nameOrigin, String nameFinal, String fieldOrigin, String fieldFinal) {
        this.databaseOrigin = databaseOrigin;
        this.databaseFinal = databaseFinal;
        this.nameOrigin = nameOrigin;
        this.nameFinal = nameFinal;
        this.fieldOrigin = fieldOrigin;
        this.fieldFinal = fieldFinal;
    }

    public String getDatabaseOrigin() {
        return databaseOrigin;
    }

    public void setDatabaseOrigin(String databaseOrigin) {
        this.databaseOrigin = databaseOrigin;
    }

    public String getDatabaseFinal() {
        return databaseFinal;
    }

    public void setDatabaseFinal(String databaseFinal) {
        this.databaseFinal = databaseFinal;
    }

    public String getNameOrigin() {
        return nameOrigin;
    }

    public void setNameOrigin(String nameOrigin) {
        this.nameOrigin = nameOrigin;
    }

    public String getNameFinal() {
        return nameFinal;
    }

    public void setNameFinal(String nameFinal) {
        this.nameFinal = nameFinal;
    }

    public String getFieldOrigin() {
        return fieldOrigin;
    }

    public void setFieldOrigin(String fieldOrigin) {
        this.fieldOrigin = fieldOrigin;
    }

    public String getFieldFinal() {
        return fieldFinal;
    }

    public void setFieldFinal(String fieldFinal) {
        this.fieldFinal = fieldFinal;
    }

    public Object getResultFrom() {
        return resultFrom;
    }

    public void setResultFrom(Object resultFrom) {
        this.resultFrom = resultFrom;
    }

    public Object getResultTo() {
        return resultTo;
    }

    public void setResultTo(Object resultTo) {
        this.resultTo = resultTo;
    }
}
