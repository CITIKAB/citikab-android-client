package com.trioangle.gofer.datamodels.signinsignup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by trioangle on 6/9/18.
 */

public class CarDetails {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("carName")
    @Expose
    private String carName;
    @SerializedName("description")
    @Expose
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
