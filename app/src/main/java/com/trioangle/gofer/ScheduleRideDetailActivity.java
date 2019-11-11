package com.trioangle.gofer;

/**
 * @package com.trioangle.gofer
 * @subpackage -
 * @category MainActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sidebar.trips.YourTrips;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;

import java.util.HashMap;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
   To call Api for Schedule ride
    *********************************************************** */


public class ScheduleRideDetailActivity extends AppCompatActivity implements ServiceListener {

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

    public HashMap<String, String> scheduleHashMap;

    public @InjectView(R.id.trip_date_time)
    TextView trip_date_time;
    public @InjectView(R.id.amountscheduled)
    TextView amountscheduled;
    public String date;
    public String location_id;
    public String peak_id;
    public String clicked_car;
    public String pickup_location;
    public String drop_location;
    public String is_wallet;
    public String fare;
    public double pickup_latitude;
    public double pickup_longitude;
    public double drop_latitude;
    public double drop_longitude;
    protected boolean isInternetAvailable;

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @OnClick(R.id.done_layout)
    public void done() {
        saveScheduleRide();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_ride_detail_);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());  //Check internet access


        date = sessionManager.getScheduleDateTime();
        location_id  = getIntent().getStringExtra("location_id");
        peak_id = getIntent().getStringExtra("peak_id");
        clicked_car = getIntent().getStringExtra("clicked_car");
        pickup_location = getIntent().getStringExtra("pickup_location");
        drop_location = getIntent().getStringExtra("drop_location");
        is_wallet = getIntent().getStringExtra("is_wallet");
        fare = getIntent().getStringExtra("fare_estimation");
        pickup_latitude = getIntent().getDoubleExtra("pickup_latitude", 0);
        pickup_longitude = getIntent().getDoubleExtra("pickup_longitude", 0);
        drop_latitude = getIntent().getDoubleExtra("drop_latitude", 0);
        drop_longitude = getIntent().getDoubleExtra("drop_longitude", 0);

        trip_date_time.setText(date);
        amountscheduled.setText(sessionManager.getCurrencySymbol() + fare);

    }

    /**
     * To save schedule by calling api
     */

    public void saveScheduleRide() {

        if (isInternetAvailable) {
            sessionManager.setIsrequest(false);
            TimeZone tz = TimeZone.getDefault();
            scheduleHashMap = new HashMap<>();

            scheduleHashMap.put("schedule_date", sessionManager.getScheduleDate());
            scheduleHashMap.put("schedule_time", sessionManager.getPresentTime());
            scheduleHashMap.put("car_id", clicked_car);
            scheduleHashMap.put("pickup_latitude", String.valueOf(pickup_latitude));
            scheduleHashMap.put("pickup_longitude", String.valueOf(pickup_longitude));
            scheduleHashMap.put("drop_latitude", String.valueOf(drop_latitude));
            scheduleHashMap.put("drop_longitude", String.valueOf(drop_longitude));
            scheduleHashMap.put("pickup_location", pickup_location);
            scheduleHashMap.put("drop_location", drop_location);
            scheduleHashMap.put("payment_method", sessionManager.getPaymentMethod());
            scheduleHashMap.put("is_wallet", is_wallet);
            scheduleHashMap.put("user_type", sessionManager.getType());
            scheduleHashMap.put("device_type", sessionManager.getDeviceType());
            scheduleHashMap.put("device_id", sessionManager.getDeviceId());
            scheduleHashMap.put("token", sessionManager.getAccessToken());
            scheduleHashMap.put("timezone", tz.getID());
            scheduleHashMap.put("location_id",location_id);
            scheduleHashMap.put("peak_id",peak_id);
            scheduleRide();
            //   new scheduleARide().execute(url);

        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
        }
    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            Intent intent = new Intent(ScheduleRideDetailActivity.this, YourTrips.class);
            intent.putExtra("upcome", "upcome");
            startActivity(intent);
            finish();
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            commonMethods.hideProgressDialog();
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
        commonMethods.hideProgressDialog();
    }

    public void scheduleRide() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.scheduleRide(scheduleHashMap).enqueue(new RequestCallback(this));
    }

}

