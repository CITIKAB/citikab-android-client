package com.trioangle.gofer.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CommonKeys {
    public static final String APIKEY_PAYMENT_TYPE_STRIPE = "Stripe";
    public static final int ChangePaymentOpetionBeforeRequestCarApi = 124;
    public static final int IncompleteReferralArray = 358;// just a random number
    public static final int CompletedReferralArray = 247;// just a random number
    public static final String DeviceTypeAndroid = "2";
    public static Boolean isLoggable = true;

    public static final int REQ_GladePayonPaymentPage = 5;
    public static  boolean isLiveServer = false;
    public static String merchantId = "GP0000001";
    public static  String merchantKey = "123456789";

    public static boolean isPaymentwithToken = false;

    //check gladepay successfully Added
    public static boolean isGladePayinPaymentpage= false;
    public static boolean isGladePayinWalletpage= false;
    public static String Brand = "";
    public static  String last4 = "";
    public static String card_token = "";
    public static String googleMapsKey = "AIzaSyB0efJyL4VKIbR2rTcugSC_z-m3z06hjEk"; // Google map and place API key

    //public static String baseUrl = "http://gofer.trioangle.com/";
   // public static String baseUrl = "http://192.168.0.219/product/Gofer-Enterprise-2.0/public/";
   // public static String baseUrl = "http://trioangledemo.com/citikab/public/";
    public static String baseUrl = "http://3.208.43.254/";


    public static String imageUrl = baseUrl+"images/";
    public static String apiBaseUrl = baseUrl+"api/";
    public static String termPolicyUrl = baseUrl;
    public static String gladepay_baseUrl = "";
    public static  String gladepaycard_tokenusingAuth_pin="";

    // payment types used in payment select activity for shared preference purpose
    public static final String
            PAYMENT_GLADEPAY = "GladePay",
           /* PAYMENT_GLADEPAYwithToken = "Gladepay",
            PAYMENT_GLADEPAYwithCard = "Gladepay",*/
            PAYMENT_CASH = "Cash",
          //  PAYMENT_CARD = "Stripe",
            TYPE_INTENT_ARGUMENT_KEY = "type";


    public static String chatFirebaseDatabaseName = "driver_rider_trip_chats";
    public static String FIREBASE_CHAT_MESSAGE_KEY = "message";
    public static String FIREBASE_CHAT_TYPE_KEY = "type";
    public static String FIREBASE_CHAT_TYPE_RIDER = "rider";
    public static String FIREBASE_CHAT_TYPE_DRIVER = "driver";

    public static boolean IS_ALREADY_IN_TRIP = false;




    @IntDef({StatusCode.startPaymentActivityForView, StatusCode.startPaymentActivityForChangePaymentOption, StatusCode.startPaymentActivityForAddMoneyToWallet ,StatusCode.startPaymentActivityForChangePaymentOptionGLadepay})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusCode {
        int startPaymentActivityForView = 0;
        int startPaymentActivityForChangePaymentOption = 1;
        int startPaymentActivityForAddMoneyToWallet = 2;
       int  startPaymentActivityForChangePaymentOptionGLadepay=3;
    }

    @IntDef({FirebaseChatserviceTriggeredFrom.backgroundService, FirebaseChatserviceTriggeredFrom.chatActivity})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FirebaseChatserviceTriggeredFrom {
        int backgroundService = 0;
        int chatActivity = 1;

    }


    /*Activity request codes*/
    public static final int REQUEST_CODE_PAYMENT = 1;
    public static final int REQUEST_CODE_OPEN_ADD_CARD_ACTIVITY = 124;

    public static final int ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT = 102;
    public static final int ACTIVITY_REQUEST_CODE_SHOW_PEAK_HOURS_DETAILS = 103;

    public static final String FIREBASE_CHAT_ACTIVITY_SOURCE_ACTIVITY_TYPE_CODE = "sourceActivityCode";
    public static final int FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_RIDER_OR_DRIVER_PROFILE = 110;
    public static final int FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_NOTIFICATION = 111;

    public static final int PEAK_PRICE_ACCEPTED = 1,PEAK_PRICE_DENIED = 0;

    public static final String YES ="Yes", NO ="No";
    public static final String KEY_PEAK_PRICE ="peakPrice", KEY_MIN_FARE="minimumFare", KEY_PER_KM="perKM",KEY_PER_MINUTES="perMin";

    public static final String NUMBER_VALIDATION_API_RESULT_OLD_USER = "1";
    public static final String NUMBER_VALIDATION_API_RESULT_NEW_USER = "0";
    public static final int FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER = 157; // Number declared randomly, not specifically
    public static final int FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER = 158; // Number declared randomly, not specifically

    public static final String FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY="message";
    public static final String ISSCHEDULE="yes";

    public static final String FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY="phoneNumber";
    public static final String FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY="countryCode";
}
