package com.trioangle.gofer.GladePay.exceptions;

/**
 * author: Chinaka Light on 14/11/2018
 */
public class CardException extends GladepayException {

    public CardException(String message){
        super(message);
    }

    public CardException(String message, Throwable e){
        super(message, e);
    }
}
