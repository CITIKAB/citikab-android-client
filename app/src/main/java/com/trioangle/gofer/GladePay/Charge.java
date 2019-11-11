package com.trioangle.gofer.GladePay;



import android.util.Log;
import android.util.Patterns;

import com.google.gson.Gson;
import com.trioangle.gofer.BuildConfig;
import com.trioangle.gofer.GladePay.exceptions.ChargeException;
import com.trioangle.gofer.GladePay.exceptions.InvalidAmountException;
import com.trioangle.gofer.GladePay.exceptions.WrongEmailFormatException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;


public class Charge {

        private final String TAG = Charge.class.getSimpleName();

        private Card card;
        private String email;
        private String access_code;
        private double amount;
        private JSONObject metadata;
        private JSONArray custom_fields;
        private boolean hasMeta = false;
        private HashMap<String, String> additionalParameters;
        private String reference;

        private boolean localStarted = false;
        private boolean remoteStarted = false;

        private void beforeLocalSet(String fieldName){
            if(remoteStarted && BuildConfig.DEBUG){
                throw new ChargeException("You cannot set this " + fieldName + " after specifying an access code");
            }
            localStarted = true;
        }
        private void beforeRemoteSet(){
            if(localStarted && BuildConfig.DEBUG){
                throw new ChargeException("You can not set access code when providing transaction parameters in app");
            }
            remoteStarted = true;
        }
        public Charge() {
            this.metadata = new JSONObject();
            this.amount = -1;
            this.additionalParameters = new HashMap<>();
            this.custom_fields = new JSONArray();
            try {
                this.metadata.put("custom_fields", new Gson().toJson(custom_fields).toString());
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }

        public void addParameter(String key, String value) {
            beforeLocalSet(key);
            this.additionalParameters.put(key, value);
        }

        public HashMap<String, String> getAdditionalParameters() {
            return additionalParameters;
        }



        public String getReference() {
            return reference;
        }

        public Charge setReference(String reference) {
            beforeLocalSet("reference");
            this.reference = reference;
            return this;
        }

        public Card getCard() {
            return card;
        }

        public Charge setCard(Card card) {
            this.card = card;
            return this;
        }

        public Charge putMetadata(String name, String value) throws JSONException {
            beforeLocalSet("metadata");
            this.metadata.put(name, value);
            this.hasMeta = true;
            return this;
        }

        public Charge putMetadata(String name, JSONObject value) throws JSONException {
            beforeLocalSet("metadata");
            this.metadata.put(name, value);
            this.hasMeta = true;
            return this;
        }

        public Charge putCustomField(String displayName, String value) throws JSONException {
            beforeLocalSet("custom field");
            JSONObject customObj = new JSONObject();
            customObj.put("value", value);
            customObj.put("display_name", displayName);
            customObj.put("variable_name", displayName.toLowerCase(Locale.getDefault()).replaceAll("[^a-z0-9 ]","_"));
            this.custom_fields.put(customObj);
            this.hasMeta = true;
            return this;
        }

        public String getMetadata(){
            if(!hasMeta){
                return null;
            }
            return this.metadata.toString();
        }

        public String getEmail() {
            return email;
        }

        public Charge setEmail(String email) {
            beforeLocalSet("email");
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                throw new WrongEmailFormatException(email);
            }
            this.email = email;
            return this;
        }

        public double getAmount() {
            return amount;
        }

        public Charge setAmount(double amount) throws InvalidAmountException {
            beforeLocalSet("amount");
            if (amount <= 0)
                throw new InvalidAmountException(amount);
            this.amount = amount;
            return this;
        }


    }

