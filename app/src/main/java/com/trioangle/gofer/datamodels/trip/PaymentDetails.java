package com.trioangle.gofer.datamodels.trip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by trioangle on 24/9/18.
 */

public class PaymentDetails implements Serializable {

    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("total_time")
    @Expose
    private String totalTime;
    @SerializedName("total_km")
    @Expose
    private String totalKm;
    @SerializedName("total_time_fare")
    @Expose
    private String timeFare;
    @SerializedName("total_km_fare")
    @Expose
    private String totalKmFare;
    @SerializedName("base_fare")
    @Expose
    private String baseFare;
    @SerializedName("total_fare")
    @Expose
    private String totalFare;
    @SerializedName("access_fee")
    @Expose
    private String accessFee;
    @SerializedName("schedule_fare")
    @Expose
    private String scheduleFare;
    @SerializedName("pickup_location")
    @Expose
    private String pickuplocation;
    @SerializedName("drop_location")
    @Expose
    private String droplocation;
    @SerializedName("driver_payout")
    @Expose
    private String driverPayout;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("owe_amount")
    @Expose
    private String oweAmount;
    @SerializedName("applied_owe_amount")
    @Expose
    private String appliedOweAmount;
    @SerializedName("wallet_amount")
    @Expose
    private String walletAmount;
    @SerializedName("remaining_owe_amount")
    @Expose
    private String remainingOweAmount;
    @SerializedName("promo_amount")
    @Expose
    private String promoAmount;
    @SerializedName("trip_status")
    @Expose
    private String tripStatus;

    @SerializedName("admin_paypal_id")
    @Expose
    private String adminPaypalId;

    @SerializedName("paypal_mode")
    @Expose
    private String paypalMode;

    @SerializedName("paypal_app_id")
    @Expose
    private String paypalAppId;

    @SerializedName("driver_paypal_id")
    @Expose
    private String driverPaypalId;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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

    public String getTotalKmFare() {
        return totalKmFare;
    }

    public void setTotalKmFare(String totalKmFare) {
        this.totalKmFare = totalKmFare;
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

    public String getScheduleFare() {
        return scheduleFare;
    }

    public void setScheduleFare(String scheduleFare) {
        this.scheduleFare = scheduleFare;
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

    public String getRemainingOweAmount() {
        return remainingOweAmount;
    }

    public void setRemainingOweAmount(String remainingOweAmount) {
        this.remainingOweAmount = remainingOweAmount;
    }

    public String getPromoAmount() {
        return promoAmount;
    }

    public void setPromoAmount(String promoAmount) {
        this.promoAmount = promoAmount;
    }

    public String getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(String tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getAdminPaypalId() {
        return adminPaypalId;
    }

    public void setAdminPaypalId(String adminPaypalId) {
        this.adminPaypalId = adminPaypalId;
    }

    public String getPaypalMode() {
        return paypalMode;
    }

    public void setPaypalMode(String paypalMode) {
        this.paypalMode = paypalMode;
    }

    public String getPaypalAppId() {
        return paypalAppId;
    }

    public void setPaypalAppId(String paypalAppId) {
        this.paypalAppId = paypalAppId;
    }

    public String getDriverPaypalId() {
        return driverPaypalId;
    }

    public void setDriverPaypalId(String driverPaypalId) {
        this.driverPaypalId = driverPaypalId;
    }


}
