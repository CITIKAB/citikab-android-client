package com.trioangle.gofer.GladePay.apiui;

public class PinSingleton {
    private static PinSingleton instance = new PinSingleton();
    private String pin = "";
    private String pinMessage = "";

    private PinSingleton() {
    }
    public static PinSingleton getInstance() {
        return instance;
    }
    public String getPin() {
        return pin;
    }

    public PinSingleton setPin(String pin) {
        this.pin = pin;
        return this;
    }

    public String getPinMessage() {
        return pinMessage;
    }

    public PinSingleton setPinMessage(String pinMessage) {
        this.pinMessage = pinMessage;
        return this;
    }
}
