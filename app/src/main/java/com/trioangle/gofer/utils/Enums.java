package com.trioangle.gofer.utils;
/**
 * @package com.trioangle.gofer
 * @subpackage utils
 * @category Enums
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*****************************************************************
 Enums
 ****************************************************************/

public class Enums {

    public static final int REQ_SIGNUP = 101;
    public static final int REQ_RESEND_OTP = 102;
    public static final int REQ_GET_RIDER_PROFILE = 103;
    public static final int REQ_SEARCH_CARS = 104;
    public static final int REQ_UPDATE_LOCATION = 105;
    public static final int REQ_SEND_REQUEST = 106;
    public static final int REQ_GET_DRIVER = 107;
    public static final int REQ_TRIP_DETAIL = 108;
    public static final int REQ_PAYPAL_CURRENCY = 109;
    public static final int REQ_AFTER_PAY = 110;
    public static final int REQ_UPDATE_PROFILE = 111;
    public static final int REQ_LOGOUT = 112;
    public static final int REQ_CURRENCYLIST = 113;
    public static final int REQ_UPDATE_CURR = 114;
    public static final int REQ_UPDATE_LANG = 115;
    public static final int REQ_GET_PROMO = 116;
    public static final int REQ_ADD_PROMO = 117;
    public static final int REQ_ADD_WALLET = 118;
    public static final int REQ_UPLOAD_PROFILE_IMG = 119;
    public static final int REQ_ADD_CARD = 120;
    public static final int REQ_VALIDATE_NUMBER = 121;
    public static final int REQ_INCOMPLETE_TRIP_DETAILS = 122;
    public static final int REQ_GET_INVOICE = 123;



    @Retention(RetentionPolicy.SOURCE)
    public @interface Frag {
    }

    @IntDef({REQ_SIGNUP, REQ_RESEND_OTP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestType {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface MatchType {
    }
}
