package com.trioangle.gofer.sendrequest;

/**
 * @package com.trioangle.gofer
 * @subpackage sendrequest
 * @category AcceptedDriverDetails Model
 * @author Trioangle Product Team
 * @version 1.5
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.trioangle.gofer.datamodels.trip.InvoiceModel;
import com.trioangle.gofer.datamodels.trip.PaymentDetails;

import java.io.Serializable;
import java.util.ArrayList;

/* ************************************************************
    Get Accepted driver details s
    *************************************************************** */
public class AcceptedDriverDetails implements Serializable {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("trip_id")
    @Expose
    private String tripId;
    @SerializedName("driver_name")
    @Expose
    private String drivername;
    @SerializedName("mobile_number")
    @Expose
    private String mobilenumber;
    @SerializedName("driver_thumb_image")
    @Expose
    private String profileimg;
    @SerializedName("rating_value")
    @Expose
    private String ratingvalue;
    @SerializedName("car_type")
    @Expose
    private String cartype;
    @SerializedName("pickup_location")
    @Expose
    private String pickuplocation;
    @SerializedName("drop_location")
    @Expose
    private String droplocation;
    @SerializedName("driver_latitude")
    @Expose
    private String driverlatitude;
    @SerializedName("driver_longitude")
    @Expose
    private String driverlongitude;
    @SerializedName("pickup_latitude")
    @Expose
    private String pickuplatitude;
    @SerializedName("pickup_longitude")
    @Expose
    private String pickuplongitude;
    @SerializedName("drop_latitude")
    @Expose
    private String droplatitude;
    @SerializedName("drop_longitude")
    @Expose
    private String droplongitude;
    @SerializedName("vehicle_number")
    @Expose
    private String vehicle_number;
    @SerializedName("vehicle_name")
    @Expose
    private String vehicleName;
    @SerializedName("arrival_time")
    @Expose
    private String arival_time;
    @SerializedName("trip_status")
    @Expose
    private String tripStatus;
    @SerializedName("payment_details")
    @Expose
    private PaymentDetails paymentDetails;
    @SerializedName("invoice")
    @Expose
    private ArrayList<InvoiceModel> invoice;


    /**
     * Accepted driver details getter and setter methods
     */

    public String getDrivername() {
        return drivername;
    }

    public void setDrivername(String drivername) {
        this.drivername = drivername;
    }

    public String getRatingvalue() {
        return ratingvalue;
    }

    public void setRatingvalue(String ratingvalue) {
        this.ratingvalue = ratingvalue;
    }

    public String getCartype() {
        return cartype;
    }

    public void setCartype(String cartype) {
        this.cartype = cartype;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getPickuplocation() {
        return pickuplocation;
    }

    public void setPickuplocation(String pickuplocation) {
        this.pickuplocation = pickuplocation;
    }

    public String getDroplocation() {
        return droplocation;
    }

    public void setDroplocation(String droplocation) {
        this.droplocation = droplocation;
    }

    public String getProfileimg() {
        return profileimg;
    }

    public void setProfileimg(String profileimg) {
        this.profileimg = profileimg;
    }

    public String getPickuplatitude() {
        return pickuplatitude;
    }

    public void setPickuplatitude(String pickuplatitude) {
        this.pickuplatitude = pickuplatitude;
    }

    public String getPickuplongitude() {
        return pickuplongitude;
    }

    public void setpickuplongitude(String pickuplongitude) {
        this.pickuplongitude = pickuplongitude;
    }

    public String getDroplatitude() {
        return droplatitude;
    }

    public void setDroplatitude(String droplatitude) {
        this.droplatitude = droplatitude;
    }

    public String getDroplongitude() {
        return droplongitude;
    }

    public void setDroplongitude(String droplongitude) {
        this.droplongitude = droplongitude;
    }

    public String getDriverlatitude() {
        return driverlatitude;
    }

    public void setDriverlatitude(String driverlatitude) {
        this.driverlatitude = driverlatitude;
    }

    public String getDriverlongitude() {
        return driverlongitude;
    }

    public void setDriverlongitude(String driverlongitude) {
        this.driverlongitude = driverlongitude;
    }

    public String getVehicleNumber() {
        return vehicle_number;
    }

    public void setVehicleNumber(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicle_name) {
        this.vehicleName = vehicle_name;
    }

    public String getArrivaltime() {
        return arival_time;
    }

    public void setArrivaltime(String arival_time) {
        this.arival_time = arival_time;
    }

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

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public ArrayList<InvoiceModel> getInvoice() {
        return invoice;
    }

    public void setInvoice(ArrayList<InvoiceModel> invoice) {
        this.invoice = invoice;
    }
}