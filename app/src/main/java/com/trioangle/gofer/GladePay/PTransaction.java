package com.trioangle.gofer.GladePay;

import com.google.gson.JsonObject;

public class PTransaction {
    private String transactionRef;
    private String statusId;
    private String message = "";


    public String getTransactionRef() {
        return transactionRef;
    }

    public void loadFromResponse(JsonObject jsonObject) {
        if(jsonObject.has("status") && jsonObject.has("txnRef")){
            this.transactionRef = jsonObject.get("txnRef").getAsString();
            this.statusId = jsonObject.get("status").getAsString();
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatusId() {
        return statusId;
    }

    boolean hasStartedProcessingOnServer() {
        return (this.transactionRef != null) && ( statusId != null );
    }
}
