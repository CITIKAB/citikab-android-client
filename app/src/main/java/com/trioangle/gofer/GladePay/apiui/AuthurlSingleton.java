package com.trioangle.gofer.GladePay.apiui;






import org.json.JSONObject;

public class AuthurlSingleton {
    private static AuthurlSingleton instance = new AuthurlSingleton();
    private JSONObject response=null;
    private String otpMessage = "";

    private AuthurlSingleton() {
    }

    public static AuthurlSingleton getInstance() {
        return instance;
    }

    public JSONObject getResponse() {
        return response;
    }

    public AuthurlSingleton setResponse(JSONObject response) {
        this.response = response;
        return this;
    }

    public String getOtpMessage() {
        return otpMessage;
    }

    public AuthurlSingleton setOtpMessage(String otpMessage) {
        this.otpMessage = otpMessage;
        return this;
    }
}
