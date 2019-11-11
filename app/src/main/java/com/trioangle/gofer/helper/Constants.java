package com.trioangle.gofer.helper;

import android.Manifest;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Trioangle Product Team
 * @version 1.5
 * @package com.trioangle.gofer
 * @subpackage helper
 * @category Constants
 */

/* ************************************************************
 Constants values for shared preferences
*************************************************************** */

public class Constants {

    public static final String APP_NAME = "Gofer";
    public static final String FILE_NAME = "gofer";
    public static final String STATUS_MSG = "status_message";
    public static final String STATUS_CODE = "status_code";
    public static final String REFRESH_ACCESS_TOKEN = "refresh_token";
    public static final String OTP = "otp";

    public static final int REQUEST_CODE_GALLERY = 5;
    public static final String JSON = "json"; //save some JSON response

    public static final String carname = "carname"; // save currency code

    // Google place Search
    public static final String API_NOT_CONNECTED = "Google API not connected";   // check google API client connected or not
    public static final String SOMETHING_WENT_WRONG = "OOPs!!! Something went wrong...";
    public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");
    public static final SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public static final SimpleDateFormat chatUtcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
    public static String PlacesTag = "Google Places";  // Google place tag




}
