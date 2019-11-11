package com.trioangle.gofer.GladePay.exceptions;


public class WrongEmailFormatException extends GladepayException{

    private String email;

    public WrongEmailFormatException(String email) {
        super(email + " is not a valid email");
        this.setEmail(email);
    }

    public String getEmail() {
        return email;
    }

    public WrongEmailFormatException setEmail(String email) {
        this.email = email;
        return this;
    }

}

