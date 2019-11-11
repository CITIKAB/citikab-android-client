package com.trioangle.gofer.sendrequest;
/**
 * @package com.trioangle.gofer
 * @subpackage sendrequest
 * @category AcceptedTripId Model
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
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.database.AddFirebaseDatabase;
import com.trioangle.gofer.database.IFirebaseReqListener;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.helper.AcceptedTrip;
import com.trioangle.gofer.helper.CircularMusicProgressBar;
import com.trioangle.gofer.helper.WaveDrawable;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.pushnotification.Config;
import com.trioangle.gofer.pushnotification.NotificationUtils;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.github.krtkush.lineartimer.LinearTimer;
import io.github.krtkush.lineartimer.LinearTimerView;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.Enums.REQ_GET_DRIVER;

/* ************************************************************
    Sending request to nearest selected type drivers
    *************************************************************** */
public class SendingRequestActivity extends AppCompatActivity implements LinearTimer.TimerListener, ServiceListener, IFirebaseReqListener {

    public static AcceptedDriverDetails acceptedDriverDetails = new AcceptedDriverDetails();
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
    public @InjectView(R.id.carname_request)
    TextView carname_request;
    public @InjectView(R.id.album_art)
    CircularMusicProgressBar progressBar;

    public WaveDrawable waveDrawable;
    public HashMap<String, String> locationHashMap;
    public RelativeLayout request_receive_dialog_layout;
    public LinearTimerView linearTimerView;
    public LinearTimer linearTimer;
    public ImageView map_snap;
    public String JSON_DATA;
    public String carname;
    protected boolean isInternetAvailable;
    private long duration = 120 * 1000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private AddFirebaseDatabase addFirebaseDatabase=new AddFirebaseDatabase();

    public static boolean isSendingRequestisLive=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_sending_request);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        /**
         *  Load static map image
         */
        Picasso.with(getApplicationContext()).load(getIntent().getStringExtra("mapurl"))

                .into(progressBar);

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        locationHashMap = (HashMap<String, String>) getIntent().getSerializableExtra("hashMap");

        dialog = commonMethods.getAlertDialog(this);
        carname = getIntent().getStringExtra("carname");
        DebuggableLogV("carname1", "carname=" + carname);
        try {
            if (carname != null) {
                sessionManager.setCarName(carname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         *  Get total car count from main activity
         */
        if (getIntent().getIntExtra("totalcar", 0) != 0) {
            duration = getIntent().getIntExtra("totalcar", 0) * 60000;

        }

        /**
         * Create a req in Firebase DB
         */
        addFirebaseDatabase.createRequestTable(this);

        /**
         *  Push notification received
         */

        requestReceive();

        /**
         * Load reqeuest details
         */
        if (getIntent().getStringExtra("loadData").equals("loadData")) {
            loadJSONDATA();
        } else {
            circularProgressfunction();
        }

        carname_request.setText(
                getResources().getString(R.string.request_car_msg)
                        + " " + sessionManager.getCarName()
                        + "" + getResources().getString(R.string.request_car_msg1));
        // set progress to 40%
        progressBar.setValue(100);

       /* progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    linearTimer.pauseTimer();
                    //linearTimer.resetTimer();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                //waveDrawable.stopAnimation();


            }
        });*/
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    /**
     * Waiting for driver response to show the circular image
     */
    public void circularProgressfunction() {
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        int radius;
        if (width < height)
            radius = (int) (width / 2.2);
        else
            radius = (int) (height / 2.2);
        System.out.print("radius: " + radius);
        //radius-= 4;
        radius = (int) (radius / getResources().getDisplayMetrics().density);
        System.out.print("Height: " + height + " Width: " + width);

        waveDrawable = new WaveDrawable(getResources().getColor(R.color.sending_request_blue), width - 250);

        request_receive_dialog_layout = (RelativeLayout) findViewById(R.id.request_receive_dialog_layout);
        linearTimerView = (LinearTimerView) findViewById(R.id.linearTimer);
        map_snap = (ImageView) findViewById(R.id.map_snap);


        ViewTreeObserver vto = linearTimerView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                linearTimerView.getViewTreeObserver().removeOnPreDrawListener(this);
                int finalHeight1 = linearTimerView.getMeasuredHeight();
                int finalWidth1 = linearTimerView.getMeasuredWidth();
                System.out.print("Height: " + finalHeight1 + " Width: " + finalWidth1);
                map_snap.getLayoutParams().height = (int) (finalHeight1 / 1.1);
                map_snap.getLayoutParams().width = (int) (finalWidth1 / 1.1);
                map_snap.requestLayout();
                return true;
            }
        });
        System.out.print("radius: " + radius);
        linearTimerView.setCircleRadiusInDp(radius);


        linearTimer = new LinearTimer.Builder()
                .linearTimerView(linearTimerView)
                .duration(duration)
                .timerListener(this)
                .progressDirection(LinearTimer.COUNTER_CLOCK_WISE_PROGRESSION)
                .preFillAngle(0)
                .endingAngle(360)
                .getCountUpdate(LinearTimer.COUNT_UP_TIMER, 1000)
                .build();
        request_receive_dialog_layout.setBackgroundDrawable(waveDrawable);
        Interpolator interpolator = new LinearInterpolator();

        waveDrawable.setWaveInterpolator(interpolator);
        waveDrawable.startAnimation();


        try {
            linearTimerView.clearAnimation();
            linearTimerView.animate();
            linearTimerView.setAnimation(null);
            linearTimer.startTimer();
            linearTimerView.setVisibility(View.GONE);

        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * After completed time for reqeust to stop animation
     */
    @Override
    public void animationComplete() {
        waveDrawable.stopAnimation();
//        linearTimer.pauseTimer();
        //linearTimer.resumeTimer();
        linearTimer.resetTimer();
        Intent main = new Intent(getApplicationContext(), DriverNotAcceptActivity.class);
        main.putExtra("url", getIntent().getStringExtra("url"));
        main.putExtra("carname", getIntent().getStringExtra("carname"));
        main.putExtra("mapurl", getIntent().getStringExtra("mapurl"));
        main.putExtra("totalcar", getIntent().getIntExtra("totalcar", 0));
        main.putExtra("hashMap", locationHashMap);
        startActivity(main);
        finish();
    }

    /**
     * Loader timer for request
     */
    @Override
    public void timerTick(long tickUpdateInMillis) {
        DebuggableLogI("Time left", String.valueOf(tickUpdateInMillis));

    }

    @Override
    public void onTimerReset() {
        DebuggableLogV("Do", "Nothing");
    }


    /**
     * Receive push notification
     */
    public void requestReceive() {
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

                    //  String message = intent.getStringExtra("message");

                    loadJSONDATA();

                }
            }
        };
    }



    /**
     * Load Trip details
     */
    public void loadJSONDATA() {
        JSON_DATA = sessionManager.getPushJson();



        try {
            JSONObject jsonObject = new JSONObject(JSON_DATA);

            if (jsonObject.getJSONObject("custom").has("accept_request")) {

                String trip_id = jsonObject.getJSONObject("custom").getJSONObject("accept_request").getString("trip_id");
                //sessionManager.setTripId(trip_id);
                sessionManager.setTripStatus("accept_request");
                //   req_id = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("request_id");
                //   pickup_address= jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("pickup_location");
                commonMethods.showProgressDialog(this, customDialog);

                if (!isInternetAvailable) {
                    commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
                } else {
                    if(!trip_id .equalsIgnoreCase(sessionManager.getTripId())){
                        sessionManager.setTripId(trip_id);
                        getDriverDetails(trip_id);
                    }

                }
            } else if (jsonObject.getJSONObject("custom").has("no_cars")) {
                addFirebaseDatabase.removeRequestTable();

                Intent main = new Intent(getApplicationContext(), DriverNotAcceptActivity.class);
                main.putExtra("url", getIntent().getStringExtra("url"));
                main.putExtra("carname", getIntent().getStringExtra("carname"));
                main.putExtra("mapurl", getIntent().getStringExtra("mapurl"));
                main.putExtra("totalcar", getIntent().getIntExtra("totalcar", 0));
                main.putExtra("hashMap", locationHashMap);
                startActivity(main);
                finish();
            }


        } catch (JSONException e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isSendingRequestisLive=true;
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

    @Override
    public void onPause() {
        super.onPause();
        isSendingRequestisLive=false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        MainActivity.isMainActivity=true;
    }

    @Override
    protected void onDestroy() {
        isSendingRequestisLive=false;
        super.onDestroy();
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            onSuccessDriver(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }

    }

    public void onSuccessDriver(JsonResponse jsonResp) {
        addFirebaseDatabase.removeRequestTable();

        commonMethods.hideProgressDialog();
        acceptedDriverDetails = gson.fromJson(jsonResp.getStrResponse(), AcceptedDriverDetails.class);
        sessionManager.setIsrequest(false);
        sessionManager.setIsTrip(true);
        MainActivity.isMainActivity=true;
        Intent requstreceivepage = new Intent(getApplicationContext(), MainActivity.class);
        requstreceivepage.putExtra("driverDetails", acceptedDriverDetails);
        startActivity(requstreceivepage);
    }


    /**
     * After driver accept the request to get driver details from the API
     */
    public void getDriverDetails(String tripId) {
        MainActivity.isMainActivity=true;
        sessionManager.setTripId(tripId);
        commonMethods.showProgressDialog(this, customDialog);
        apiService.getDriverDetails(sessionManager.getAccessToken(), tripId).enqueue(new RequestCallback(REQ_GET_DRIVER, this));
    }



    @Override
    public void RequestListener(String Tripid) {
        getDriverDetails(Tripid);
    }
}

