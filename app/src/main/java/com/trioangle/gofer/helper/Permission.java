package com.trioangle.gofer.helper;
/**
 * @package com.trioangle.gofer
 * @subpackage helper
 * @category Permission
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/* ************************************************************
 Permission for marshMallow version mobiles
*************************************************************** */
public class Permission {

    public static final int GET_ACCOUNT = 5;

    public Activity activity;

    public Permission(Activity activity) {
        this.activity = activity;
    }

    /**
     * Check location permission
     */
    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Check Google account permission
     */
    public boolean checkPermissionForGetAccount() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS);

            return result == PackageManager.PERMISSION_GRANTED;

    }



    /**
     * Request Google plus login permission
     */
    public void requestPermissionForGetAccount() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.GET_ACCOUNTS)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.GET_ACCOUNTS}, GET_ACCOUNT);
            //Toast.makeText(activity, "Get Account permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.GET_ACCOUNTS}, GET_ACCOUNT);
        }
    }



}
