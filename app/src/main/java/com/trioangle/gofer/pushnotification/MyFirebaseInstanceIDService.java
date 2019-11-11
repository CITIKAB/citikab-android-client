package com.trioangle.gofer.pushnotification;
/**
 * @package com.trioangle.gofer
 * @subpackage MyFirebaseInstanceIDService
 * @category Firebase instance service
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.RequestCallback;

import javax.inject.Inject;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;

/* ************************************************************
   Firebase instance service to get device ID
   *************************************************************** */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService implements ServiceListener {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();


    public @Inject
    SessionManager sessionManager;
    public @Inject
    ApiService apiService;
    public @Inject
    Gson gson;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        AppController.getAppComponent().inject(this);        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);


        /*// Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);*/
    }

    private void sendRegistrationToServer(final String token) {
        // sending FCM token to server
        DebuggableLogE(TAG, "sendRegistrationToServer: " + token);

        sessionManager.setDeviceId(token);

        if (!"".equals(sessionManager.getToken())) {
            storeRegId();
        }
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }

    public void storeRegId() {
        apiService.updateDevice(sessionManager.getAccessToken(), sessionManager.getType(), "2", sessionManager.getDeviceId()).enqueue(new RequestCallback(this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            DebuggableLogV("onsuccess", "onsuccess");
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            DebuggableLogV("onsuccess", "onsuccess");
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        DebuggableLogV("onFailure", "onFailure");
    }

}

