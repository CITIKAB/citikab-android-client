package com.trioangle.gofer.sidebar.referral.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompletedOrPendingReferrals {

@SerializedName("id")
@Expose
private Long id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("profile_image")
@Expose
private String profileImage;
@SerializedName("days")
@Expose
private Long days;
@SerializedName("remaining_days")
@Expose
private Long remainingDays;
@SerializedName("trips")
@Expose
private Long trips;
@SerializedName("remaining_trips")
@Expose
private Long remainingTrips;
@SerializedName("start_date")
@Expose
private String startDate;
@SerializedName("end_date")
@Expose
private String endDate;
@SerializedName("earnable_amount")
@Expose
private String earnableAmount;
@SerializedName("status")
@Expose
private String status;

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getProfileImage() {
    return profileImage;
}

public void setProfileImage(String profileImage) {
    this.profileImage = profileImage;
}

public Long getDays() {
    return days;
}

public void setDays(Long days) {
    this.days = days;
}

public Long getRemainingDays() {
    return remainingDays;
}

public void setRemainingDays(Long remainingDays) {
    this.remainingDays = remainingDays;
}

public Long getTrips() {
    return trips;
}

public void setTrips(Long trips) {
    this.trips = trips;
}

public Long getRemainingTrips() {
    return remainingTrips;
}

public void setRemainingTrips(Long remainingTrips) {
    this.remainingTrips = remainingTrips;
}

public String getStartDate() {
    return startDate;
}

public void setStartDate(String startDate) {
    this.startDate = startDate;
}

public String getEndDate() {
    return endDate;
}

public void setEndDate(String endDate) {
    this.endDate = endDate;
}

public String getEarnableAmount() {
    return earnableAmount;
}

public void setEarnableAmount(String earnableAmount) {
    this.earnableAmount = earnableAmount;
}

public String getStatus() {
    return status;
}

public void setStatus(String status) {
    this.status = status;
}

}