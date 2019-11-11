package com.trioangle.gofer.datamodels.trip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by trioangle on 8/9/18.
 */

public class ScheduleDetail {


    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("pickup_latitude")
    @Expose
    private String pickupLatitude;
    @SerializedName("pickup_longitude")
    @Expose
    private String pickupLongitude;
    @SerializedName("drop_latitude")
    @Expose
    private String dropLatitude;
    @SerializedName("drop_longitude")
    @Expose
    private String dropLongitude;
    @SerializedName("pickup_location")
    @Expose
    private String pickupLocation;
    @SerializedName("drop_location")
    @Expose
    private String dropLocation;
    @SerializedName("car_id")
    @Expose
    private String carId;
    @SerializedName("schedule_date")
    @Expose
    private String scheduleDate;
    @SerializedName("schedule_time")
    @Expose
    private String scheduleTime;
    @SerializedName("schedule_display_date")
    @Expose
    private String scheduleDisplayDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("car_name")
    @Expose
    private String carName;
    @SerializedName("fare_estimation")
    @Expose
    private String fareEstimation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(String pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public String getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(String pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public String getDropLatitude() {
        return dropLatitude;
    }

    public void setDropLatitude(String dropLatitude) {
        this.dropLatitude = dropLatitude;
    }

    public String getDropLongitude() {
        return dropLongitude;
    }

    public void setDropLongitude(String dropLongitude) {
        this.dropLongitude = dropLongitude;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropLocation() {
        return dropLocation;
    }

    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getScheduleDisplayDate() {
        return scheduleDisplayDate;
    }

    public void setScheduleDisplayDate(String scheduleDisplayDate) {
        this.scheduleDisplayDate = scheduleDisplayDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getFareEstimation() {
        return fareEstimation;
    }

    public void setFareEstimation(String fareEstimation) {
        this.fareEstimation = fareEstimation;
    }

}
