package com.trioangle.gofer.GladePay.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.trioangle.gofer.GladePay.GladepaySdk;
import com.trioangle.gofer.GladePay.exceptions.GladepaySdkNotInitializedException;


/**
 * General Utilities class
 *
 * @author {chinakalight@googlemail.com} on 16/11/18.
 */
public class Utilities {


  /**
   * Util class for validation
   */
  public static class Validate {
    /**
     * To validate if the sdk has been initialized
     *
     * @throws GladepaySdkNotInitializedException - An exception that let's you know that you
     *          did not initialize the sdk before the call was made
     */
    public static void validateSdkInitialized() throws GladepaySdkNotInitializedException {
      if (!GladepaySdk.isSdkInitialized()) {
        throw new GladepaySdkNotInitializedException("Gladepay SDK has not been initialized." +
            "The SDK has to be initialized before use");
      }
    }

    /**
     * To check for internet permission
     *
     * @param context - Application context for current run
     */
    public static void hasInternetPermission(Context context) {
      validateNotNull(context, "context");
      PackageManager pm = context.getPackageManager();
      int hasPermission = pm.checkPermission(Manifest.permission.INTERNET, context.getPackageName());
      if (hasPermission == PackageManager.PERMISSION_DENIED) {
        throw new IllegalStateException("Gladepay requires internet permission. " +
            "Please add the intenet permission to your AndroidManifest.xml");
      }
    }

    public static void validateNotNull(Object arg, String name) {
      if (arg == null) {
        throw new NullPointerException("Argument '" + name + "' cannot be null");
      }
    }
  }
}
