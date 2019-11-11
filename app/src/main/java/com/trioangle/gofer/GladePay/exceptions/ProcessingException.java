package com.trioangle.gofer.GladePay.exceptions;

import android.widget.Toast;

/**
 * @author Light Chinaka.
 */
public class ProcessingException extends ChargeException {
    public ProcessingException() {
        super("A transaction is currently being processed, please wait till it is completed before attempting a new charge.");
       // Toast.makeText(, "A transaction is currently being processed, please wait till it is completed before attempting a new charge.", Toast.LENGTH_SHORT).show();
    }
}
