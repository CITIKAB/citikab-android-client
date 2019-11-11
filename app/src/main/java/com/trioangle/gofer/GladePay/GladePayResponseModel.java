package com.trioangle.gofer.GladePay;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GladePayResponseModel implements Serializable {
    @SerializedName("status")
    private String status;
    @SerializedName("txnStatus")
    private String txnStatus;
    @SerializedName("cardToken")
    private String cardToken;
    @SerializedName("bank_message")
   private String bank_message;
    @SerializedName("fullname")
    private String fullname;
    @SerializedName("email")
    private String email;
    @SerializedName("txnRef")
    private String txnRef;
    @SerializedName("chargedAmount")
    private String chargedAmount;
    @SerializedName("fraudStatus")
    private String fraudStatus;
    @SerializedName("card")
    private CardMask card;
    @SerializedName("currency")
    private String currency;
    @SerializedName("payment_method")
    private String payment_method;
    @SerializedName("message")
    private  String message;


    public class CardMask {
        @SerializedName("hash")
        private String hash;
        @SerializedName("mask")
        private String mask;
        @SerializedName("bin")
        private String bin;
        @SerializedName("brand")
        private String brand;

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getMask() {
            return mask;
        }

        public void setMask(String mask) {
            this.mask = mask;
        }

        public String getBin() {
            return bin;
        }

        public void setBin(String bin) {
            this.bin = bin;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getBank_message() {
        return bank_message;
    }

    public void setBank_message(String bank_message) {
        this.bank_message = bank_message;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getChargedAmount() {
        return chargedAmount;
    }

    public void setChargedAmount(String chargedAmount) {
        this.chargedAmount = chargedAmount;
    }

    public String getFraudStatus() {
        return fraudStatus;
    }

    public void setFraudStatus(String fraudStatus) {
        this.fraudStatus = fraudStatus;
    }

    public CardMask getCard() {
        return card;
    }

    public void setCard(CardMask card) {
        this.card = card;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
