package com.trioangle.gofer.datamodels.trip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by trioangle on 8/9/18.
 */

public class TripDetailModel {


    private String staticMapURL;

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
    @SerializedName("request_id")
    @Expose
    private String requestId;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("total_time")
    @Expose
    private String totalTime;
    @SerializedName("total_km")
    @Expose
    private String totalKm;
    @SerializedName("time_fare")
    @Expose
    private String timeFare;
    @SerializedName("distance_fare")
    @Expose
    private String distanceFare;
    @SerializedName("base_fare")
    @Expose
    private String baseFare;
    @SerializedName("total_fare")
    @Expose
    private String totalFare;
    @SerializedName("access_fee")
    @Expose
    private String accessFee;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("vehicle_name")
    @Expose
    private String vehicleName;
    @SerializedName("deleted_at")
    @Expose
    private String deletedAt;
    @SerializedName("driver_name")
    @Expose
    private String driverName;
    @SerializedName("driver_thumb_image")
    @Expose
    private String driverThumbImage;
    @SerializedName("trip_path")
    @Expose
    private String tripPath;
    @SerializedName("owe_amount")
    @Expose
    private String oweAmount;
    @SerializedName("applied_owe_amount")
    @Expose
    private String appliedOweAmount;
    @SerializedName("wallet_amount")
    @Expose
    private String walletAmount;
    @SerializedName("promo_amount")
    @Expose
    private String promoAmount;
    @SerializedName("driver_payout")
    @Expose
    private String driverPayout;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("remaining_owe_amount")
    @Expose
    private String remainingOweAmount;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("map_image")
    @Expose
    private String mapImage;
    @SerializedName("invoice")
    @Expose
    private ArrayList<InvoiceModel> invoice;

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalKm() {
        return totalKm;
    }

    public void setTotalKm(String totalKm) {
        this.totalKm = totalKm;
    }

    public String getTimeFare() {
        return timeFare;
    }

    public void setTimeFare(String timeFare) {
        this.timeFare = timeFare;
    }

    public String getDistanceFare() {
        return distanceFare;
    }

    public void setDistanceFare(String distanceFare) {
        this.distanceFare = distanceFare;
    }

    public String getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(String baseFare) {
        this.baseFare = baseFare;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getAccessFee() {
        return accessFee;
    }

    public void setAccessFee(String accessFee) {
        this.accessFee = accessFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverThumbImage() {
        return driverThumbImage;
    }

    public void setDriverThumbImage(String driverThumbImage) {
        this.driverThumbImage = driverThumbImage;
    }

    public String getTripPath() {
        return tripPath;
    }

    public void setTripPath(String tripPath) {
        this.tripPath = tripPath;
    }

    public String getOweAmount() {
        return oweAmount;
    }

    public void setOweAmount(String oweAmount) {
        this.oweAmount = oweAmount;
    }

    public String getAppliedOweAmount() {
        return appliedOweAmount;
    }

    public void setAppliedOweAmount(String appliedOweAmount) {
        this.appliedOweAmount = appliedOweAmount;
    }

    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String getPromoAmount() {
        return promoAmount;
    }

    public void setPromoAmount(String promoAmount) {
        this.promoAmount = promoAmount;
    }

    public String getDriverPayout() {
        return driverPayout;
    }

    public void setDriverPayout(String driverPayout) {
        this.driverPayout = driverPayout;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRemainingOweAmount() {
        return remainingOweAmount;
    }

    public void setRemainingOweAmount(String remainingOweAmount) {
        this.remainingOweAmount = remainingOweAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStaticMapURL() {
        return staticMapURL;
    }

    public void setStaticMapUR(String staticMapURL) {
        this.staticMapURL = staticMapURL;
    }

    public String getMapImage() {
        return mapImage;
    }

    public void setMapImage(String mapImage) {
        this.mapImage = mapImage;
    }

    public ArrayList<InvoiceModel> getInvoice() {
        return invoice;
    }

    public void setInvoice(ArrayList<InvoiceModel> invoice) {
        this.invoice = invoice;
    }
}
