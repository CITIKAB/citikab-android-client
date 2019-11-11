package com.trioangle.gofer.datamodels.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by trioangle on 7/9/18.
 */

public class SearchCarResult implements Serializable {


    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("nearest_car")
    @Expose
    private ArrayList<NearestCar> nearestCar;

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public ArrayList<NearestCar> getNearestCar() {
        return nearestCar;
    }

    public void setNearestCar(ArrayList<NearestCar> nearestCar) {
        this.nearestCar = nearestCar;
    }
}
