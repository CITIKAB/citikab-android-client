package com.trioangle.gofer.sendrequest;
/**
 * @package com.trioangle.gofer
 * @subpackage sendrequest
 * @category CancelYourTripActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.pushnotification.Config;
import com.trioangle.gofer.pushnotification.NotificationUtils;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
    Rider cancel the trip
    *************************************************************** */
public class CancelYourTripActivity extends AppCompatActivity implements ServiceListener {

    public AlertDialog dialog;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    CustomDialog customDialog;
    public @Inject
    Gson gson;

    public @InjectView(R.id.spinner)
    Spinner spinner;
    public @InjectView(R.id.cancel_reason)
    EditText cancel_reason;
    public String cancelreason;
    public String cancelmessage;
    protected boolean isInternetAvailable;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @OnClick(R.id.cancel_close)
    public void back() {
        finish();
    }

    @OnClick(R.id.cancelreservation)
    public void cancelreservation() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
        } else {
            String spinnerpos = String.valueOf(spinner.getSelectedItemPosition());
            if ("0".equals(spinnerpos)) {
                cancelreason = "";
            } else {
                cancelreason = spinner.getSelectedItem().toString();
            }
            cancelmessage = cancel_reason.getText().toString();

            /*try {
                cancelmessage = URLEncoder.encode(cancelmessage, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/

            if (cancelreason.equals("")){
                commonMethods.showMessage(this, dialog, getResources().getString(R.string.cancelreason));
            }else
            if (getIntent().getStringExtra("upcome") != null && !getIntent().getStringExtra("upcome").equals("") && getIntent().getStringExtra("upcome").equals("upcome")) {

                String tripid = getIntent().getStringExtra("scheduleID");
                cancelScheduleTrip(tripid);
            } else {

                cancelTrip(sessionManager.getTripId());

            }

            //new CancelTrip().execute(url);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_your_trip);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

        /**
         * Receiver push notification
         */
        receivePushNotification();
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());

        ArrayAdapter<CharSequence> canceladapter;

        // Get Cancel reason
        canceladapter = ArrayAdapter.createFromResource(
                this, R.array.cancel_types, R.layout.spinner_layout);
        canceladapter.setDropDownViewResource(R.layout.spinner_layout);


        spinner.setAdapter(canceladapter);


    }

    /**
     * Cancel reason API called
     */
    public void cancelTrip(String tripid) {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.cancelTrip(cancelreason, cancelmessage, tripid, sessionManager.getType(), sessionManager.getAccessToken()).enqueue(new RequestCallback(this));
    }
    /**
     * Cancel Scheduled ride API called
     */
    public void cancelScheduleTrip(String tripid) {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.cancelScheduleTrip(cancelreason, cancelmessage, tripid, sessionManager.getType(), sessionManager.getAccessToken()).enqueue(new RequestCallback(this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            sessionManager.clearTripID();
            sessionManager.setDriverAndRiderAbleToChat(false);
            CommonMethods.stopFirebaseChatListenerService(this);

            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    /**
     * Get notification from Firebase broadcast
     */
    public void receivePushNotification() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // FCM successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String JSON_DATA = sessionManager.getPushJson();


                    try {
                        JSONObject jsonObject = new JSONObject(JSON_DATA);

                        if (jsonObject.getJSONObject("custom").has("begin_trip")) {
                            Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    public void onResume() {

        super.onResume();

        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }
}