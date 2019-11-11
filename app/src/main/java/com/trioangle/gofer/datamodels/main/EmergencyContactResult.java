package com.trioangle.gofer.datamodels.main;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by trioangle on 12/9/18.
 */

public class EmergencyContactResult {

    @SerializedName("status_message")
    @Expose
    private String statusMessage;
    @SerializedName("status_code")
    @Expose
    private String statusCode;

    @SerializedName("contact_count")
    @Expose
    private int contactCount;
    @SerializedName("contact_details")
    @Expose
    private ArrayList<EmergencyContactModel> contactDetails;

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

    public ArrayList<EmergencyContactModel> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ArrayList<EmergencyContactModel> contactDetails) {
        this.contactDetails = contactDetails;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }
}
