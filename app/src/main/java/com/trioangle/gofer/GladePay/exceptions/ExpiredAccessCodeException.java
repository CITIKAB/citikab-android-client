package com.trioangle.gofer.GladePay.exceptions;

/**
 * @author {androidsupport@paystack.co} on 9/25/15.
 */
public class ExpiredAccessCodeException extends GladepayException {
    public ExpiredAccessCodeException(String message) {
        super(message);
    }
}
