package com.trioangle.gofer.GladePay.exceptions;

import java.io.Serializable;

/**
 * This is the base class for Gladepay Exceptions
 */
public class GladepayException extends RuntimeException implements Serializable {

    public GladepayException(String message){
        super(message, null);
    }

    public GladepayException(String message, Throwable e){
        super(message, e);
    }
}
