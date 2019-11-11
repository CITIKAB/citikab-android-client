package com.trioangle.gofer.datamodels.trip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by trioangle on 8/9/18.
 */

public class ScheduleTripResult {


    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("scheduled_rides")
    @Expose
    private ArrayList<ScheduleDetail> scheduleTrip;

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

    public ArrayList<ScheduleDetail> getScheduleTrip() {
        return scheduleTrip;
    }

    public void setScheduleTrip(ArrayList<ScheduleDetail> scheduleTrip) {
        this.scheduleTrip = scheduleTrip;
    }
}
