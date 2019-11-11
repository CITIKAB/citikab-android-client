package com.trioangle.gofer.sidebar.referral.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReferredFriendsModel {

    @SerializedName("status_code")
    @Expose
    private String statusCode;
    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("referral_code")
    @Expose
    private String referralCode;
    @SerializedName("total_earning")
    @Expose
    private String totalEarning;
    @SerializedName("pending_referrals")
    @Expose
    private List<CompletedOrPendingReferrals> pendingReferrals = null;
    @SerializedName("completed_referrals")
    @Expose
    private List<CompletedOrPendingReferrals> completedReferrals = null;
    @SerializedName("admin_max_referral_earning")
    @Expose
    private String maxReferralEarnings;

    @SerializedName("referral_amount")
    @Expose
    private String referralAmount;

    public String getMaxReferralEarnings() {
        return maxReferralEarnings;
    }

    public void setMaxReferralEarnings(String maxReferralEarnings) {
        this.maxReferralEarnings = maxReferralEarnings;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getTotalEarning() {
        return totalEarning;
    }

    public void setTotalEarning(String totalEarning) {
        this.totalEarning = totalEarning;
    }

    public List<CompletedOrPendingReferrals> getPendingReferrals() {
        return pendingReferrals;
    }

    public void setPendingReferrals(List<CompletedOrPendingReferrals> pendingReferrals) {
        this.pendingReferrals = pendingReferrals;
    }

    public List<CompletedOrPendingReferrals> getCompletedReferrals() {
        return completedReferrals;
    }

    public void setCompletedReferrals(List<CompletedOrPendingReferrals> completedReferrals) {
        this.completedReferrals = completedReferrals;
    }

    public String getReferralAmount() {
        return referralAmount;
    }

    public void setReferralAmount(String referralAmount) {
        this.referralAmount = referralAmount;
    }
}