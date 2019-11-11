package com.trioangle.gofer.sidebar;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar
 * @category EnRoute
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.pushnotification.Config;
import com.trioangle.gofer.pushnotification.NotificationUtils;
import com.trioangle.gofer.sendrequest.AcceptedDriverDetails;
import com.trioangle.gofer.sendrequest.CancelYourTripActivity;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/* ************************************************************
  Driver start the trip map and route shown
    *********************************************************** */
public class EnRoute extends AppCompatActivity {

    public static AcceptedDriverDetails acceptedDriverDetails;
    public @Inject
    SessionManager sessionManager;

    public @InjectView(R.id.cancel)
    RelativeLayout cancel;
    public @InjectView(R.id.cancel_txt)
    TextView cancel_txt;
    public @InjectView(R.id.driver_name)
    TextView driver_name;
    public @InjectView(R.id.arrivel_time)
    TextView arrivel_time;
    public @InjectView(R.id.vehicle_name)
    TextView vehicle_name;
    public @InjectView(R.id.starrating)
    TextView starrating;
    public @InjectView(R.id.driver_car_number)
    TextView driver_car_number;
    public @InjectView(R.id.arrival_txt)
    TextView arrival_txt;
    public @InjectView(R.id.pickup_location)
    TextView pickup_location;
    public @InjectView(R.id.driver_profile_image)
    CircleImageView driver_profile_image;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @OnClick(R.id.arrow)
    public void onback() {
        onBackPressed();
    }

    @OnClick(R.id.cancel)
    public void cancel() {
        Intent intent = new Intent(getApplicationContext(), CancelYourTripActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.contactlayout)
    public void contactlayout() {
        Intent intent = new Intent(getApplicationContext(), DriverContactActivity.class);
        intent.putExtra("drivername", acceptedDriverDetails.getDrivername());
        intent.putExtra("driverno", acceptedDriverDetails.getMobilenumber());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_route);
        Bundle extras = getIntent().getExtras();
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        /**
         *  Get accepted driver details
         */
        if (extras != null) {
            acceptedDriverDetails = (AcceptedDriverDetails) getIntent().getSerializableExtra("driverDetails"); //Obtaining data
        }

        /**
         *  Receive push notification
         */
        receivePushNotification();


        /**
         * Show driver details
         */
        insertDriverInfoToSession();
        driver_name.setText(acceptedDriverDetails.getDrivername());


        if (Integer.parseInt(acceptedDriverDetails.getArrivaltime()) > 1) {
            arrivel_time.setText(acceptedDriverDetails.getArrivaltime() + " " + getResources().getString(R.string.mins));
        } else {
            arrivel_time.setText(acceptedDriverDetails.getArrivaltime() + " " + getResources().getString(R.string.min));
        }
        vehicle_name.setText(acceptedDriverDetails.getVehicleName());

        if (acceptedDriverDetails.getRatingvalue().equals("") || acceptedDriverDetails.getRatingvalue().equals("0.0")) {
            starrating.setVisibility(View.GONE);
        } else {
            starrating.setText(acceptedDriverDetails.getRatingvalue());

        }
        driver_car_number.setText(acceptedDriverDetails.getVehicleNumber());

        if (Integer.parseInt(acceptedDriverDetails.getArrivaltime()) > 1) {
            arrival_txt.setText(acceptedDriverDetails.getArrivaltime() + " " + getResources().getString(R.string.mins));
        } else {
            arrival_txt.setText(acceptedDriverDetails.getArrivaltime() + " " + getResources().getString(R.string.min));
        }
        pickup_location.setText(acceptedDriverDetails.getPickuplocation());

        Picasso.with(getApplicationContext()).load(acceptedDriverDetails.getProfileimg())
                .into(driver_profile_image);


        if (!sessionManager.getTripStatus().equals("")
                //|| !sessionManager.getTripStatus().equals("null")
                && sessionManager.getTripStatus().equals("begin_trip")
                || sessionManager.getTripStatus().equals("end_trip")) {
            cancel.setClickable(false);
            cancel.setEnabled(false);
            cancel_txt.setTextColor(getResources().getColor(R.color.cancel_text_color));
        } else {
            cancel.setClickable(true);
            cancel.setEnabled(true);
            cancel_txt.setTextColor(getResources().getColor(R.color.blue_light));
        }

    }

    public void insertDriverInfoToSession(){
        sessionManager.setDriverProfilePic(acceptedDriverDetails.getProfileimg());
        sessionManager.setDriverRating(acceptedDriverDetails.getRatingvalue());
        sessionManager.setDriverName(acceptedDriverDetails.getDrivername());
    }

    /**
     * Receive push notification
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
                            //String trip_id = jsonObject.getJSONObject("custom").getJSONObject("begin_trip").getString("trip_id");
                            cancel.setClickable(false);
                            cancel.setEnabled(false);
                            cancel_txt.setTextColor(getResources().getColor(R.color.cancel_text_color));

                        }


                    } catch (JSONException e) {

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
