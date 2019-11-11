package com.trioangle.gofer.GladePay.exceptions;

public class InvalidAmountException extends GladepayException{

    private double amount;

    public InvalidAmountException(double amount) {
        super(amount + " is not a valid amount. only positive and non-zero values are allowed.");
        this.setAmount(amount);
    }

    public double getAmount() {
        return amount;
    }

    public InvalidAmountException setAmount(double amount) {
        this.amount = amount;
        return this;
    }

}

