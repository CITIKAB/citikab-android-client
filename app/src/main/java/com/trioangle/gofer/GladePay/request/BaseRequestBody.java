package com.trioangle.gofer.GladePay.request;

import android.provider.Settings;


import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.trioangle.gofer.GladePay.GladepaySdk;


/**
 * A base for all request bodies
 */
abstract class BaseRequestBody {
    static final String FIELD_DEVICE = "device";
    @SerializedName(FIELD_DEVICE)
    String device;

    public abstract JsonObject getInitiateParamsJsonObjects();

    void setDeviceId() {
        this.device = "androidsdk_" + Settings.Secure.getString(GladepaySdk.applicationContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

}
