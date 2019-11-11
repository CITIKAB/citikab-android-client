package com.trioangle.gofer.datamodels.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by trioangle on 7/9/18.
 */

public class NearestCar implements Serializable {

    public boolean isSelected;
    @SerializedName("min_time")
    @Expose
    private String minTime;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("car_id")
    @Expose
    private String carId;
    @SerializedName("car_name")
    @Expose
    private String carName;
    @SerializedName("base_fare")
    @Expose
    private String baseFare;
    @SerializedName("min_fare")
    @Expose
    private String minFare;
    @SerializedName("per_min")
    @Expose
    private String perMin;
    @SerializedName("per_km")
    @Expose
    private String perKm;
    @SerializedName("schedule_fare")
    @Expose
    private String scheduleFare;
    @SerializedName("schedule_cancel_fare")
    @Expose
    private String scheduleCancelFare;
    @SerializedName("capacity")
    @Expose
    private String capacity;
    @SerializedName("fare_estimation")
    @Expose
    private String fareEstimation;
    @SerializedName("apply_fare")
    @Expose
    private String applyFare;
    @SerializedName("location")//location
    @Expose
    private ArrayList<LocationModel> location;

    @SerializedName("apply_peak")
    @Expose
    private String applyPeak;

    @SerializedName("peak_price")
    @Expose
    private String peakPrice;

    @SerializedName("peak_id")
    @Expose
    private String peakId;


    @SerializedName("car_image")
    @Expose
    private String carImage;

    @SerializedName("car_active_image")
    @Expose
    private String carActiveImage;

    @SerializedName("location_id")
    @Expose
    private String locationID;

    @SerializedName("peak_id")
    @Expose
    private String peak_id;


    public String getPeak_id() {
        return peak_id;
    }

    public void setPeak_id(String peak_id) {
        this.peak_id = peak_id;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCarImage() {
        return carImage;
    }

    public void setCarImage(String carImage) {
        this.carImage = carImage;
    }

    public String getCarActiveImage() {
        return carActiveImage;
    }

    public void setCarActiveImage(String carActiveImage) {
        this.carActiveImage = carActiveImage;
    }

    public String getApplyPeak() {
        return applyPeak;
    }

    public void setApplyPeak(String applyPeak) {
        this.applyPeak = applyPeak;
    }

    public String getPeakPrice() {
        return peakPrice;
    }

    public void setPeakPrice(String peakPrice) {
        this.peakPrice = peakPrice;
    }

    public String getPeakId() {
        return peakId;
    }

    public void setPeakId(String peakId) {
        this.peakId = peakId;
    }
    /* @SerializedName("km")
    @Expose
    private ArrayList<LocationModel> km;*/

    public String getMinTime() {
        return minTime;
    }

    public void setMinTime(String minTime) {
        this.minTime = minTime;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getMinFare() {
        return minFare;
    }

    public void setMinFare(String minFare) {
        this.minFare = minFare;
    }

    public String getPerMin() {
        return perMin;
    }

    public void setPerMin(String perMin) {
        this.perMin = perMin;
    }

    public String getPerKm() {
        return perKm;
    }

    public void setPerKm(String perKm) {
        this.perKm = perKm;
    }

    public String getScheduleFare() {
        return scheduleFare;
    }

    public void setScheduleFare(String scheduleFare) {
        this.scheduleFare = scheduleFare;
    }

    public String getScheduleCancelFare() {
        return scheduleCancelFare;
    }

    public void setScheduleCancelFare(String scheduleCancelFare) {
        this.scheduleCancelFare = scheduleCancelFare;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getFareEstimation() {
        return fareEstimation;
    }

    public void setFareEstimation(String fareEstimation) {
        this.fareEstimation = fareEstimation;
    }

    public String getApplyFare() {
        return applyFare;
    }

    public void setApplyFare(String applyFare) {
        this.applyFare = applyFare;
    }

    public ArrayList<LocationModel> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<LocationModel> location) {
        this.location = location;
    }

    public boolean getCarIsSelected() {
        return isSelected;
    }

    public void setCarIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}
