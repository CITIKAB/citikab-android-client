package com.trioangle.gofer.datamodels.signinsignup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by trioangle on 6/9/18.
 */

public class SigninResult {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("access_token")
    @Expose
    private String token;

    @SerializedName("car_detais")
    @Expose
    private ArrayList<CarDetails> cardetais;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

    @SerializedName("email_id")
    @Expose
    private String emailId;

    @SerializedName("user_status")
    @Expose
    private String userStatus;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("country_code")
    @Expose
    private String countryCode;

    @SerializedName("address_line1")
    @Expose
    private String addressLine1;

    @SerializedName("address_line2")
    @Expose
    private String addressLine2;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("postal_code")
    @Expose
    private String postalCode;

    @SerializedName("user_thumb_image")
    @Expose
    private String userThumbImage;

    @SerializedName("home")
    @Expose
    private String home;

    @SerializedName("work")
    @Expose
    private String work;

    @SerializedName("home_latitude")
    @Expose
    private String homeLatitude;

    @SerializedName("home_longitude")
    @Expose
    private String homeLongitude;

    @SerializedName("work_latitude")
    @Expose
    private String workLatitude;

    @SerializedName("work_longitude")
    @Expose
    private String workLongitude;

    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;

    @SerializedName("currency_code")
    @Expose
    private String currencyCode;

    @SerializedName("payout_id")
    @Expose
    private String payoutId;

    @SerializedName("wallet_amount")
    @Expose
    private String walletAmount;

    @SerializedName("admin_paypal_id")
    @Expose
    private String adminPaypalId;

    @SerializedName("paypal_mode")
    @Expose
    private String paypalMode;

    @SerializedName("paypal_app_id")
    @Expose
    private String paypalAppId;

    @SerializedName("google_map_key")
    @Expose
    private String googleMapKey;

    @SerializedName("fb_id")
    @Expose
    private String fbId;


    public String getWalletAmount() {
        return walletAmount;
    }

    public void setWalletAmount(String walletAmount) {
        this.walletAmount = walletAmount;
    }

    public String getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ArrayList<CarDetails> getCardetais() {
        return cardetais;
    }

    public void setCardetais(ArrayList<CarDetails> cardetais) {
        this.cardetais = cardetais;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getUserThumbImage() {
        return userThumbImage;
    }

    public void setUserThumbImage(String userThumbImage) {
        this.userThumbImage = userThumbImage;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getHomeLatitude() {
        return homeLatitude;
    }

    public void setHomeLatitude(String homeLatitude) {
        this.homeLatitude = homeLatitude;
    }

    public String getHomeLongitude() {
        return homeLongitude;
    }

    public void setHomeLongitude(String homeLongitude) {
        this.homeLongitude = homeLongitude;
    }

    public String getWorkLatitude() {
        return workLatitude;
    }

    public void setWorkLatitude(String workLatitude) {
        this.workLatitude = workLatitude;
    }

    public String getWorkLongitude() {
        return workLongitude;
    }

    public void setWorkLongitude(String workLongitude) {
        this.workLongitude = workLongitude;
    }

    public String getAdminPaypalId() {
        return adminPaypalId;
    }

    public void setAdminPaypalId(String adminPaypalId) {
        this.adminPaypalId = adminPaypalId;
    }

    public String getGoogleMapKey() {
        return googleMapKey;
    }

    public void setGoogleMapKey(String googleMapKey) {
        this.googleMapKey = googleMapKey;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }
}
