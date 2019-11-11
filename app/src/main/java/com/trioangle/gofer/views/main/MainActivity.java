package com.trioangle.gofer.views.main;
/**
 * @package com.trioangle.gofer
 * @subpackage -
 * @category MainActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.*;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;
import com.trioangle.gofer.GladePay.GladepaySdk;
import com.trioangle.gofer.R;
import com.trioangle.gofer.ScheduleRideDetailActivity;
import com.trioangle.gofer.adapters.CarDetailsListAdapter;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.custompalette.CustomTypefaceSpan;
import com.trioangle.gofer.database.AddFirebaseDatabase;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.main.NearestCar;
import com.trioangle.gofer.datamodels.main.RiderProfile;
import com.trioangle.gofer.datamodels.trip.InvoiceModel;
import com.trioangle.gofer.datamodels.trip.PaymentDetails;
import com.trioangle.gofer.helper.LatLngInterpolator;
import com.trioangle.gofer.helper.Permission;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.map.AppUtils;
import com.trioangle.gofer.map.DriverLocation;
import com.trioangle.gofer.map.drawpolyline.DownloadTask;
import com.trioangle.gofer.map.drawpolyline.PolylineOptionsInterface;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.placesearch.CustomInfoWindowGoogleMap;
import com.trioangle.gofer.placesearch.InfoWindowData;
import com.trioangle.gofer.pushnotification.Config;
import com.trioangle.gofer.pushnotification.NotificationUtils;
import com.trioangle.gofer.sendrequest.AcceptedDriverDetails;
import com.trioangle.gofer.sendrequest.DriverRatingActivity;
import com.trioangle.gofer.sendrequest.PaymentAmountPage;
import com.trioangle.gofer.sendrequest.SendingRequestActivity;
import com.trioangle.gofer.sidebar.EnRoute;
import com.trioangle.gofer.sidebar.FareBreakdown;
import com.trioangle.gofer.sidebar.Profile;
import com.trioangle.gofer.sidebar.Setting;
import com.trioangle.gofer.sidebar.payment.AddWalletActivity;
import com.trioangle.gofer.sidebar.payment.PaymentPage;
import com.trioangle.gofer.sidebar.referral.ShowReferralOptions;
import com.trioangle.gofer.sidebar.trips.YourTrips;
import com.trioangle.gofer.singledateandtimepicker.SingleDateAndTimePicker;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.utils.RuntimePermissionDialogFragment;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.emergency.EmergencyContact;
import com.trioangle.gofer.views.emergency.SosActivity;
import com.trioangle.gofer.views.firebaseChat.FirebaseChatNotificationService;
import com.trioangle.gofer.views.peakPricing.PeakPricing;
import com.trioangle.gofer.views.search.PlaceSearchActivity;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.trioangle.gofer.utils.CommonKeys.Brand;
import static com.trioangle.gofer.utils.CommonKeys.card_token;
import static com.trioangle.gofer.utils.CommonKeys.gladepay_baseUrl;
import static com.trioangle.gofer.utils.CommonKeys.isLiveServer;
import static com.trioangle.gofer.utils.CommonKeys.last4;
import static com.trioangle.gofer.utils.CommonKeys.merchantId;
import static com.trioangle.gofer.utils.CommonKeys.merchantKey;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogD;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.CommonMethods.startFirebaseChatListenerService;
import static com.trioangle.gofer.utils.Enums.REQ_GET_DRIVER;
import static com.trioangle.gofer.utils.Enums.REQ_GET_RIDER_PROFILE;
import static com.trioangle.gofer.utils.Enums.REQ_INCOMPLETE_TRIP_DETAILS;
import static com.trioangle.gofer.utils.Enums.REQ_SEARCH_CARS;
import static com.trioangle.gofer.utils.Enums.REQ_SEND_REQUEST;
import static com.trioangle.gofer.utils.Enums.REQ_UPDATE_LOCATION;

/* ************************************************************
   After rider login this page will shown its main page for rider
    *********************************************************** */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, ServiceListener, RuntimePermissionDialogFragment.RuntimePermissionRequestedCallback {


    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    private final static int REQ_GladePay = 789;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    public static boolean isMainActivity = true;
    public static AcceptedDriverDetails acceptedDriverDetails;//=new AcceptedTripId();
    private static String TAG = "MAP LOCATION";
    public final ArrayList movepoints = new ArrayList();
    public final AndroidHttpClient ANDROID_HTTP_CLIENT = AndroidHttpClient.newInstance(MainActivity.class.getName());
    Intent scheduleIntent;
    String isSchedule = "";
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

    public @InjectView(R.id.rideicon)
    TextView rideicon;
    /*public @InjectView(R.id.driver_car_name)
    TextView driverCarName;*/
    public @InjectView(R.id.whereto_and_schedule)
    RelativeLayout whereto_and_schedule;
    public @InjectView(R.id.nav_view)
    NavigationView navigationView;
    public @InjectView(R.id.drawer_layout)
    DrawerLayout drawer;
    public @InjectView(R.id.whereto)
    TextView whereto;        // Where to search page button
    public @InjectView(R.id.tvAppVersion)
    TextView appVersion;
    public @InjectView(R.id.no_car_txt)
    TextView no_car_txt;
    public @InjectView(R.id.car_details_list)
    RecyclerView listView;        // Search car list
    public @InjectView(R.id.paymentmethod)
    RelativeLayout paymentmethod;        // Payment method selection layout
    public @InjectView(R.id.paymentmethod_img)
    ImageView paymentmethod_img;        // Cash or paypal iamge show for payment method
    public @InjectView(R.id.wallet_img)
    ImageView wallet_img;        // If wallet choose show in image
    public @InjectView(R.id.sos)
    ImageView sos;
    public @InjectView(R.id.paymentmethod_type)
    TextView paymentmethod_type;// Payment method type show in text view
    public @InjectView(R.id.pickup_location)
    TextView pickup_location;
    public @InjectView(R.id.driver_name)
    TextView driver_name;
    public @InjectView(R.id.driver_car_name)
    TextView driver_car_name;
    public @InjectView(R.id.driver_rating)
    TextView driver_rating;
    public @InjectView(R.id.driver_car_number)
    TextView driver_car_number;
    public @InjectView(R.id.driver_image)
    CircleImageView driver_image;

    public @InjectView(R.id.fab_startChat)
    FloatingActionButton startChat;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    public @InjectView(R.id.meetlayout)
    RelativeLayout meetlayout;        // Show address after trip start
    public @InjectView(R.id.bottomlayout)
    RelativeLayout bottomlayout;        //Search car bottom layout
    public @InjectView(R.id.content_main)
    RelativeLayout content_main;
    public @InjectView(R.id.no_car)
    RelativeLayout no_car;
    public @InjectView(R.id.loading_car)
    RelativeLayout loading_car;
    public @InjectView(R.id.requestubers)
    RelativeLayout requestubers;
    public @InjectView(R.id.edit_map)
    RelativeLayout edit_map;
    public @InjectView(R.id.scheduleride)
    ImageView scheduleride;        // Schedule ride button
    public @InjectView(R.id.requestuber)
    TextView requestuber;        // Reqest button
    public @InjectView(R.id.textview1)
    TextView textView1;
    @InjectView((R.id.activity_fare_estimation))
    public ViewGroup request_view;//Fare estimate

    public @InjectView(R.id.rlt_contact_admin)
    RelativeLayout rltContactAdmin;

    @OnClick(R.id.rlt_contact_admin)
    public void setRltContactAdmin() {
        if(!TextUtils.isEmpty(sessionManager.getAdminContact())) {
            createAdminpopup(1);
        }else {
            createAdminpopup(0);
        }
    }


    public TextView username;
    public CircleImageView userprofile;
    public SingleDateAndTimePicker singleDateAndTimePicker;
    public TextView scheduleridetext;
    public TextView scheduleride_text;
    public HashMap<String, String> locationHashMap;
    public Date now;
    public Marker carmarker;
    public int i = 0;
    public float startbear = 0;
    public float endbear = 0;
    public int totalcar = 0;
    public int count1 = 0;
    public Marker marker;
    public String trip_path;
    public String staticMapURL;
    public LatLng driverlatlng;
    public String clickedcar; // = "2";
    public String isPeakPriceAppliedForClickedCar = CommonKeys.NO;
    public String minimumFareForClickedCar;
    public String perKMForClickedCar;
    public String perMinForClickedCar;
    public String peakPriceforClickedCar;
    public String peakPriceIdForClickedCar;
    public String locationIDForSelectedCar;
    public String peakIDForSelectedCar;

    public String clickedCarName;
    public String clickedCarAppliedPeak;
    public JSONObject nearest_carObj;
    public LatLng latLong;
    public String first_name;
    public String last_name;
    public String profile_image;
    public String mobile_number;
    public String country_code;
    public String email_id;
    public String country;
    public String pickupaddresss = "";
    public String dropaddress = "";
    public String dropfulladdress = "";
    public String pickupfulladdress = "";
    public SupportMapFragment mapFragment;
    public Location mLastLocation;
    public DecimalFormat twoDForm;
    public Context mContext;
    public List<Marker> carmarkers = new ArrayList<Marker>();
    public List<Address> addressList = null;
    public String address = null;
    public String countrys = null;
    public LatLng getLocations = null;
    //public List<CarDetailModel> searchlist;
    public ArrayList<NearestCar> nearestCars = new ArrayList<>();
    public CarDetailsListAdapter adapter;
    public boolean firstloop = true;
    public boolean samelocation = false;
    public int lastposition;// = 1;
    public boolean alreadyRunning = true;
    public Polyline polyline;
    public Animation bottomUp, bottomDown;

    public ActionBarDrawerToggle toggle;
    public boolean isRequest = false;
    public boolean isTrip = false;
    public boolean isTripBegin = false;
    public String home;
    public String work;
    public float speed = 13f;
    public ValueAnimator valueAnimator;
    public Query query;
    public View headerview;
    public LinearLayout profilelayout;
    public ArrayList markerPoints = new ArrayList();
    protected boolean isInternetAvailable;
    private int backPressed = 0;    // used by onBackPressed()
    private GoogleMap mMap;
    Animation slideUpAnimation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mToolBarNavigationListenerIsRegistered = false;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Location lastLocation = null;
    private DatabaseReference mFirebaseDatabase;
    private ValueEventListener mSearchedLocationReferenceListener;
    private String caramount;
    private String finalDay;
    private String time;
    private String date;

    // Creating MarkerOptions
    MarkerOptions pickupOptions = new MarkerOptions();

    private AddFirebaseDatabase addFirebaseDatabase=new AddFirebaseDatabase();

    /*
     *  Rotate marker
     **/
    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    @OnClick(R.id.fab_startChat)
    public void startChatActivity() {

        CommonMethods.startChatActivityFrom(this);
    }

    @OnClick(R.id.sos)
    public void sos() {
        Intent intent = new Intent(getApplicationContext(), SosActivity.class);
        intent.putExtra("latitude", latLong.latitude);
        intent.putExtra("longitude", latLong.longitude);
        startActivity(intent);
    }

    @OnClick(R.id.edit_map)
    public void editMap() {
        Intent intent = new Intent(getApplicationContext(), PlaceSearchActivity.class);
        intent.putExtra("Latlng", latLong);
        intent.putExtra("Country", country);
        intent.putExtra("PickupAddress", pickupaddresss);
        intent.putExtra("DropAddress", dropaddress);
        intent.putExtra("PickupDrop", markerPoints);
        intent.putExtra("Home", home);
        intent.putExtra("Schedule", "");
        intent.putExtra("Work", work);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @OnClick(R.id.scheduleride)
    public void schedulerideSearchCar() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
        } else {
            LatLng pickup = (LatLng) markerPoints.get(0);
            LatLng drop = (LatLng) markerPoints.get(1);

            drawRoute(markerPoints);
            locationHashMap = new HashMap<>();
            locationHashMap.put("pickup_latitude", String.valueOf(pickup.latitude));
            locationHashMap.put("pickup_longitude", String.valueOf(pickup.longitude));
            locationHashMap.put("drop_latitude", String.valueOf(drop.latitude));
            locationHashMap.put("drop_longitude", String.valueOf(drop.longitude));
            locationHashMap.put("schedule_date", sessionManager.getScheduleDate());
            locationHashMap.put("schedule_time", sessionManager.getPresentTime());
            locationHashMap.put("user_type", sessionManager.getType());
            locationHashMap.put("token", sessionManager.getAccessToken());
            locationHashMap.put("timezone", TimeZone.getDefault().getID());


            searchCars();
            requestubers.setVisibility(View.INVISIBLE);
            edit_map.setVisibility(View.INVISIBLE);
            rltContactAdmin.setVisibility(View.GONE);
            requestuber.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.INVISIBLE);
            loading_car.setVisibility(View.VISIBLE);
            no_car.setVisibility(View.INVISIBLE);
            requestuber.setEnabled(false);

        }

    }

    @OnClick(R.id.requestuber)
    public void requestuber() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
        } else {
            String iswallet;
            LatLng pickup = (LatLng) markerPoints.get(0);
            LatLng drop = (LatLng) markerPoints.get(1);
            if ("Schedule".equals(getIntent().getStringExtra("Schedule"))) {

                if ("".equals(pickupfulladdress) || "".equals(dropfulladdress)) {
                    commonMethods.showMessage(MainActivity.this, dialog, "Pickup or Drop Address missing please try again...");
                } else {
                    pickupfulladdress = pickupfulladdress.replaceAll("null", "");
                    dropfulladdress = dropfulladdress.replaceAll("null", "");


                    if (sessionManager.getIsWallet()) {
                        if (Float.valueOf(sessionManager.getWalletAmount()) > 0) iswallet = "Yes";
                        else iswallet = "No";
                    } else {
                        iswallet = "No";
                    }

                    scheduleIntent = new Intent(MainActivity.this, ScheduleRideDetailActivity.class);
                    scheduleIntent.putExtra("Schedule", "Schedule");
                    scheduleIntent.putExtra("clicked_car", clickedcar);
                    scheduleIntent.putExtra("pickup_latitude", pickup.latitude);
                    scheduleIntent.putExtra("pickup_longitude", pickup.longitude);
                    scheduleIntent.putExtra("drop_latitude", drop.latitude);
                    scheduleIntent.putExtra("drop_longitude", drop.longitude);
                    scheduleIntent.putExtra("fare_estimation", caramount);
                    scheduleIntent.putExtra("pickup_location", pickupfulladdress);
                    scheduleIntent.putExtra("drop_location", dropfulladdress);
                    scheduleIntent.putExtra("pickup_location", pickupfulladdress);
                    scheduleIntent.putExtra("drop_location", dropfulladdress);
                    scheduleIntent.putExtra("location_id", locationIDForSelectedCar);
                    scheduleIntent.putExtra("peak_id", peakIDForSelectedCar);
                    scheduleIntent.putExtra("is_wallet", iswallet);


                    isSchedule = CommonKeys.ISSCHEDULE;
                    //sendRequest();
                    if (isPeakPriceAppliedForClickedCar.equals(CommonKeys.YES)) {
                        startPeakPriceingActivity();
                    } else {
                        startActivity(scheduleIntent);
                    }
                    //finish();
                }

            } else {


                if ("".equals(pickupfulladdress) || "".equals(dropfulladdress)) {
                    commonMethods.showMessage(MainActivity.this, dialog, "Pickup or Drop Address missing please try again...");
                } else {
                    pickupfulladdress = pickupfulladdress.replaceAll("null", "");
                    dropfulladdress = dropfulladdress.replaceAll("null", "");


                    if (sessionManager.getIsWallet()) {
                        if (Float.valueOf(sessionManager.getWalletAmount()) > 0) iswallet = "Yes";
                        else iswallet = "No";
                    } else {
                        iswallet = "No";
                    }
                    TimeZone tz = TimeZone.getDefault();
                    locationHashMap = new HashMap<>();
                    locationHashMap.put("car_id", clickedcar);
                    locationHashMap.put("pickup_latitude", String.valueOf(pickup.latitude));
                    locationHashMap.put("pickup_longitude", String.valueOf(pickup.longitude));
                    locationHashMap.put("drop_latitude", String.valueOf(drop.latitude));
                    locationHashMap.put("drop_longitude", String.valueOf(drop.longitude));
                    locationHashMap.put("pickup_location", pickupfulladdress);
                    locationHashMap.put("drop_location", dropfulladdress);
                    locationHashMap.put("payment_method", sessionManager.getPaymentMethod());
                    locationHashMap.put("is_wallet", iswallet);
                    locationHashMap.put("user_type", sessionManager.getType());
                    locationHashMap.put("device_type", sessionManager.getDeviceType());
                    locationHashMap.put("device_id", sessionManager.getDeviceId());
                    locationHashMap.put("token", sessionManager.getAccessToken());
                    locationHashMap.put("timezone", tz.getID());
                    locationHashMap.put("location_id", locationIDForSelectedCar);

                    //sendRequest();
                    if (isPeakPriceAppliedForClickedCar.equals(CommonKeys.YES)) {
                        startPeakPriceingActivity();
                    } else {
                        sendRequest();
                    }
                }
            }
        }

    }

    @OnClick(R.id.whereto)
    public void whereto() {
        /**
         *  Where to button for search car
         */
        sessionManager.setScheduleDate("");
        sessionManager.setPresentTime("");
        schedulewhere("", "");

    }

    public void schedulewhere(String date, String sched) {
        if (latLong != null && country != null) {


            Intent intent = new Intent(getApplicationContext(), PlaceSearchActivity.class);
            intent.putExtra("Latlng", latLong);
            intent.putExtra("Country", country);
            intent.putExtra("Home", home);
            intent.putExtra("date", date);
            intent.putExtra("Schedule", sched);
            intent.putExtra("Work", work);
            intent.putExtra("PickupAddress", pickupaddresss);
            intent.putExtra("DropAddress", dropaddress);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        } else {
            isInternetAvailable = commonMethods.isOnline(getApplicationContext());
            if (!isInternetAvailable) {

                commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
            } else {
                verifyAccessPermission(new String[]{RuntimePermissionDialogFragment.LOCATION_PERMISSION}, RuntimePermissionDialogFragment.locationCallbackCode, 1);

            }
        }
    }

    @OnClick(R.id.drivewithuber)
    public void drivewithuber() {
        /**
         *   Open driver app or Google play link
         */
        PackageManager managerclock = getPackageManager();
        Intent i = managerclock.getLaunchIntentForPackage(getResources().getString(R.string.package_driver));

        if (i == null) {
            i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getResources().getString(R.string.package_driver)));
            //Toast.makeText(this, "No Application Name", Toast.LENGTH_LONG).show();
        } else {
            i.addCategory(Intent.CATEGORY_LAUNCHER);

        }
        startActivity(i);
    }

    @OnClick(R.id.rideicon)
    public void ride() {
        scheduleRide();//schedule ride date picker
        scheduleMethod();
    }

    @OnClick(R.id.bottomlayout)
    public void bottomlayout() {
        /**
         *   Driver details after trip started
         */
        Intent intent = new Intent(getApplicationContext(), EnRoute.class);
        intent.putExtra("driverDetails", acceptedDriverDetails);
        startActivity(intent);
    }

    @OnClick(R.id.paymentmethod_change)
    public void paymentchange() {
        /**
         *   Payment method Changed
         */
        gotoPaymentpage(CommonKeys.StatusCode.startPaymentActivityForChangePaymentOptionGLadepay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        initGladePay();
        initNavitgaionview();

        rideicon.setText("n");

        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up_animation);


        textView1.setMovementMethod(new ScrollingMovementMethod());

        getintent();

        sessionGetset(); //getset Session Values

        if (!isInternetAvailable) {
            dialogfunction(); //no internet avaliabilty dialog
        }

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();

        mFirebaseDatabase = mFirebaseInstance.getReference("live_tracking"); // get reference to 'Driver Location'

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showPermissionDialog(); // Do something for lollipop and above versions
        } else {
            checkGPSEnable();  // do something for phones running on SDK before lollipop
        }


        headerViewset();

        googleMapfunc();

        initMapPlaceAPI();


    }

    private void initGladePay()
    {
        if (isInternetAvailable) {
         //   commonMethods.showProgressDialog(MainActivity.this, customDialog);
            apiService.glade_pay_key(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_GladePay, this));
        } else {
            commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
        }
        /*GladepaySdk.initialize(getApplicationContext());
        GladepaySdk.setLiveStatus(isLiveServer);
        GladepaySdk.setMerchantId(merchantId);
        GladepaySdk.setMerchantKey(merchantKey)*/;

    }

    private void initMapPlaceAPI() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
    }

    public void getintent() {
        date = getIntent().getStringExtra("date");
        Bundle extrass = getIntent().getExtras();
        if (extrass != null) {
            markerPoints = (ArrayList) extrass.getSerializable("PickupDrop");
        }
    }

    public void sessionGetset() {
        isRequest = sessionManager.getIsrequest();
        isTrip = sessionManager.getIsTrip();
        System.out.println("Payment method One : "+sessionManager.getPaymentMethod());
        if (sessionManager.getPaymentMethod().equals("")) {
            sessionManager.setPaymentMethod(CommonKeys.PAYMENT_CASH);
            sessionManager.setIsWallet(true);
        }
        mContext = this;
    }

    public void initNavitgaionview() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);


        // get App version from gradle, and assigning to Textview
        // here V indicates Version and it will not changable according to Language
        appVersion.setText("v" + CommonMethods.getAppVersionNameFromGradle(this));

        String laydir = getResources().getString(R.string.layout_direction);
        float scale = getResources().getDisplayMetrics().density;
        int sizeInDp;
        if ("1".equals(laydir)) {
            sizeInDp = 70;
        } else {
            sizeInDp = 60;
        }
        int dpAsPixels = (int) (sizeInDp * scale + 0.5f);
        no_car.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    public void googleMapfunc() {
        /* ********************************   Google MAP function Start *************************** */

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        receivePushNotification();

        if (!isRequest && isTrip) {

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                acceptedDriverDetails = (AcceptedDriverDetails) getIntent().getSerializableExtra("driverDetails"); //Obtaining data
                resetToRide();
            }
            isTripBegin = getIntent().getBooleanExtra("isTripBegin", false);

            if (isTripBegin) {
                firstloop = true;
            }
            // Live Tracking Function
            // getDriverTacking();
        }

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());

        if (!CommonKeys.IS_ALREADY_IN_TRIP) {
            if (isInternetAvailable) {
                commonMethods.showProgressDialog(MainActivity.this, customDialog);
                apiService.getInCompleteTripsDetails(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_INCOMPLETE_TRIP_DETAILS, this));
            } else {
                commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
            }
        }
    }

    public void headerViewset() {
        headerview = navigationView.getHeaderView(0);

        /**
         *   After trip driver profile details page
         */
        profilelayout = (LinearLayout) headerview.findViewById(R.id.profilelayout);
        username = (TextView) headerview.findViewById(R.id.username);
        userprofile = (CircleImageView) headerview.findViewById(R.id.userprofile);
        profilelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile.class);
                intent.putExtra("first_name", first_name);
                intent.putExtra("last_name", last_name);
                intent.putExtra("profile_image", profile_image);
                intent.putExtra("mobile_number", mobile_number);
                intent.putExtra("country_code", country_code);
                intent.putExtra("email_id", email_id);
                startActivity(intent);
                overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

    }

    /**
     * Check GPS enable or not
     */
    public void checkGPSEnable() {
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!AppUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
            buildGoogleApiClient();
        } else {
            Toast.makeText(mContext, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Navigation view for rider
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item click here.
        int id = item.getItemId();

        if (id == R.id.nav_payment) {

//            Intent intent = new Intent(getApplicationContext(), PaymentPage.class);
//            intent.putExtra("type", "request");
//            startActivity(intent);
//            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);

            gotoPaymentpage(CommonKeys.StatusCode.startPaymentActivityForView);
            // Handle the camera action
        } else if (id == R.id.nav_yourtrips) {
            Intent intent = new Intent(getApplicationContext(), YourTrips.class);
            intent.putExtra("upcome", "");
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        } else if (id == R.id.nav_wallet) {
            Intent intent = new Intent(getApplicationContext(), AddWalletActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), Setting.class);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        } else if (id == R.id.emg_contact) {
            Intent intent = new Intent(getApplicationContext(), EmergencyContact.class);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        } else if (id == R.id.manual_booking) {
            if(!TextUtils.isEmpty(sessionManager.getAdminContact())) {
                createAdminpopup(1);
            }else {
                createAdminpopup(0);
            }
        }else if (id == R.id.nav_referral) {
            Intent intent = new Intent(getApplicationContext(), ShowReferralOptions.class);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void createAdminpopup(int type) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        if (type==1) {
            StringBuilder ReverseNumber = new StringBuilder(sessionManager.getAdminContact());
            if (ViewCompat.getLayoutDirection(whereto) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                // The view has RTL layout
                ReverseNumber.reverse();
                System.out.println("get Reverse " + ReverseNumber);
            }
            builder1.setTitle(getResources().getString(R.string.dial, sessionManager.getAdminContact()));
            builder1.setMessage(getResources().getString(R.string.contact_admin_for_manual_booking));
            builder1.setCancelable(false);

            builder1.setPositiveButton(getResources().getString(R.string.call), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    String callnumber = sessionManager.getAdminContact();
                    Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callnumber));
                    startActivity(intent2);
                }
            });

            builder1.setNegativeButton(getResources().getString(R.string.cancel_s), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        }else {
            builder1.setMessage(getResources().getString(R.string.no_contact_found));
            builder1.setCancelable(false);

            builder1.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        }

        AlertDialog alert11 = builder1.create();
        if (alert11.getWindow() != null)
            alert11.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
        alert11.show();
    }

    /**
     * Apply font
     */
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts_UBERBook));
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.ub__map_style));

            if (!success) {
                DebuggableLogE("Gofer", "Style parsing failed.");
            }
            System.out.print("isRequestisRequest" + isRequest);
            if (isRequest) {
                enableViews(true, false);
            }

        } catch (Resources.NotFoundException e) {
            DebuggableLogE("Gofer", "Can't find style. Error: ", e);
        }

        /**
         *   map on Camera change listener
         */
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                DebuggableLogD("Camera postion change" + "", cameraPosition + "");
                LatLng mCenterLatLong = cameraPosition.target;
                // mMap.clear();

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    isInternetAvailable = commonMethods.isOnline(getApplicationContext());
                    if (!isInternetAvailable) {
                        commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
                    } else {

                        if (mCenterLatLong.longitude > 0.0 && mCenterLatLong.latitude > 0.0 && sessionManager.getIsUpdateLocation() == 0) {
                            sessionManager.setIsUpdateLocation(1);
                            /*String url = "updateriderlocation?"
                                    + "latitude=" +
                                    + "&longitude=" + mCenterLatLong.longitude
                                    + "&user_type=" + sessionManager.getType()
                                    + "&token=" + sessionManager.getAccessToken();

                            url = url.replaceAll(" ", "%20");*/
                            locationHashMap = new HashMap<>();
                            locationHashMap.put("latitude", String.valueOf(mCenterLatLong.latitude));
                            locationHashMap.put("longitude", String.valueOf(mCenterLatLong.longitude));
                            locationHashMap.put("user_type", sessionManager.getType());
                            locationHashMap.put("token", sessionManager.getAccessToken());
                            updateRiderLoc();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * get direction for given origin, dest
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        try {
            System.out.print("sad");
        } catch (Exception e) {

        }

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + sessionManager.getGoogleMapKey();


        return url;
    }

    public void verifyAccessPermission(String[] requestPermissionFor, int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {

        RuntimePermissionDialogFragment.checkPermissionStatus(this, getSupportFragmentManager(), this, requestPermissionFor, requestCodeForCallbackIdentificationCode, requestCodeForCallbackIdentificationCodeSubDivision);
    }

    @Override
    public void onConnected(Bundle bundle) {
        /*
        * Here we removed ACCESS_COARSE LOCATION by the advice of google developer console
        * statement mentioned by google: If you are using both NETWORK_PROVIDER and GPS_PROVIDER, then you need to request only the ACCESS_FINE_LOCATION permission, because it includes permission for both providers. Permission for ACCESS_COARSE_LOCATION allows access only to NETWORK_PROVIDER.
          Link:
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                changeMap(mLastLocation);
                DebuggableLogD(TAG, "ON connected");

            } else try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        DebuggableLogI(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * update while location changed
     */
    @Override
    public void onLocationChanged(Location location) {
        try {
            DebuggableLogI(TAG, "On Location Changed...");
            if (location != null) {

                //Toast.makeText(getActivity(), "Current speed:" + location.getSpeed(),Toast.LENGTH_SHORT).show();
                changeMap(location);
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        DebuggableLogV(TAG, "onConnectionFailed");
    }

    /**
     * Google API client called
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isMainActivity = true;
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        DebuggableLogE(TAG, "Driver Location data removed!");
        if (mSearchedLocationReferenceListener != null) {
            query.removeEventListener(mSearchedLocationReferenceListener);
            mFirebaseDatabase.removeEventListener(mSearchedLocationReferenceListener);
            mSearchedLocationReferenceListener = null;

            DebuggableLogE(TAG, "Driver Location data removed! success");
        } else {
            DebuggableLogE(TAG, "Driver Location data removed! Failed");
        }
        isMainActivity = false;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Check google API service or not
     */
    private boolean checkPlayServices() {
//        code updated due to deprication, code updated by the reference of @link: https://stackoverflow.com/a/31016761/6899791
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this); this is commented due to depricated
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    /**
     * change google map current location
     */
    private void changeMap(Location location) {

        DebuggableLogD(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // check if map is created successfully or not
            if (mMap != null) {
                mMap.getUiSettings().setZoomControlsEnabled(false);


                latLong = new LatLng(location.getLatitude(), location.getLongitude());
                if (!isInternetAvailable) {
                    commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
                } else {
                    if (alreadyRunning) {
                        fetchAddress(latLong, "pickupaddress");
                    }
                }
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(16.5f).tilt(0).build();

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                isRequest = sessionManager.getIsrequest();
                isTrip = sessionManager.getIsTrip();
                if (!isRequest && isTrip) {
                    if (acceptedDriverDetails != null) {
                        isTripFunc();
                    } else {
                        getDriverDetails();  //get accepted driver details
                    }
                } else {
                    isRequestFunc();
                }

                if (!isRequest) {
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }


                //  startIntentService(location);

            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.sorry_unable_create_map), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    public void startPeakPriceingActivity() {
        Intent peakHoursIntent = new Intent(this, PeakPricing.class);
        peakHoursIntent.putExtra(CommonKeys.KEY_PEAK_PRICE, peakPriceforClickedCar);
        peakHoursIntent.putExtra(CommonKeys.KEY_MIN_FARE, minimumFareForClickedCar);
        peakHoursIntent.putExtra(CommonKeys.KEY_PER_KM, perKMForClickedCar);
        peakHoursIntent.putExtra(CommonKeys.KEY_PER_MINUTES, perMinForClickedCar);

        startActivityForResult(peakHoursIntent, CommonKeys.ACTIVITY_REQUEST_CODE_SHOW_PEAK_HOURS_DETAILS);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE && resultCode == RESULT_OK) {
            // Get the user's selected place from the Intent.

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }

        } else if (requestCode == CommonKeys.ACTIVITY_REQUEST_CODE_SHOW_PEAK_HOURS_DETAILS) {
            if (resultCode == CommonKeys.PEAK_PRICE_ACCEPTED) {

                if (isSchedule.equals(CommonKeys.ISSCHEDULE)) {
                    startActivity(scheduleIntent);

                } else {
                    locationHashMap.put("peak_id", peakPriceIdForClickedCar);
                    sendRequest();
                }

            } else {
                // peak price not accepted
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            backPressed = 0;


        } else {
            if (backPressed >= 1) {
                CommonKeys.IS_ALREADY_IN_TRIP = false;
                finishAffinity();
                super.onBackPressed();       // bye
            } else {
                // clean up
                backPressed = backPressed + 1;
                Toast.makeText(this, getResources().getString(R.string.press_again_toexit), Toast.LENGTH_SHORT).show();
            }
        }

    }



    /* **********************************************************************************  */
    /*                           Car List and Bottom View                                  */
    /* **********************************************************************************  */

    /**
     * Check location permission
     */
    private void showPermissionDialog() {
        if (!Permission.checkPermission(this)) {
            verifyAccessPermission(new String[]{RuntimePermissionDialogFragment.LOCATION_PERMISSION}, RuntimePermissionDialogFragment.locationCallbackCode, 0);
        }
        buildGoogleApiClient();

    }

    public void getCarList() {

        adapter = new CarDetailsListAdapter(getApplicationContext(), nearestCars);

        listView.setHasFixedSize(false);
        listView.setNestedScrollingEnabled(true);
        listView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        listView.setAdapter(adapter);
        listView.setSelected(true);
        listView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //countries.get(position)
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    CommonMethods.DebuggableLogI("car recycler view", "clicked");

                    // requestuber.setText(getResources().getString(R.string.requestuber) + searchlist.get(position).getCarName());
                    if (lastposition == Integer.parseInt(nearestCars.get(position).getCarId())) {
                        fareEstimate(position, nearestCars);
                    } else {

                        CommonMethods.DebuggableLogI("car recycler view", nearestCars.get(position).getApplyPeak());

                        lastposition = Integer.parseInt(nearestCars.get(position).getCarId());
                        clickedcar = nearestCars.get(position).getCarId();
                        clickedCarName = nearestCars.get(position).getCarName();
                        isPeakPriceAppliedForClickedCar = nearestCars.get(position).getApplyPeak();
                        peakPriceIdForClickedCar = nearestCars.get(position).getPeakId();
                        minimumFareForClickedCar = nearestCars.get(position).getMinFare();
                        perKMForClickedCar = nearestCars.get(position).getPerKm();
                        perMinForClickedCar = nearestCars.get(position).getPerMin();
                        peakPriceforClickedCar = nearestCars.get(position).getPeakPrice();
                        caramount = nearestCars.get(position).getFareEstimation();
                        getCar(nearest_carObj, nearestCars.get(position).getCarId());
                        locationIDForSelectedCar = nearestCars.get(position).getLocationID();
                        peakIDForSelectedCar = nearestCars.get(position).getPeak_id();
                        //Toast.makeText(getApplicationContext(),"Current position "+searchlist.get(position).getCarName(),Toast.LENGTH_SHORT).show();
                    }
                    if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
                        date = sessionManager.getScheduleDateTime();

                        requestuber.setText(getResources().getString(R.string.schedule) + clickedCarName + "\n" + date);
                    } else {
                        requestuber.setText(getResources().getString(R.string.requestuber) + clickedCarName);
                    }

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                DebuggableLogV(TAG, e.toString());
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                DebuggableLogV(TAG, "disallowIntercept");
            }
        });
    }

    /**
     * Show hide views based on trip or not
     */
    private void enableViews(boolean enable, boolean resetToRide) {

        // To keep states of ActionBar and ActionBarDrawerToggle synchronized,
        // when you enable on one, you disable on the other.
        // And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT.
        if (enable) {

            getCarList();
            // getCar(nearest_carObj,clickedcar);
            // Adding new item to the ArrayList
            LatLng pickup = null;
            LatLng drop = null;
            try {
                if (getIntent().hasExtra("PickupDrop"))
                    markerPoints = (ArrayList) getIntent().getSerializableExtra("PickupDrop");

                pickup = (LatLng) markerPoints.get(0);
                drop = (LatLng) markerPoints.get(1);
            } catch (Exception e) {
                e.printStackTrace();
                commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
            }


            /**
             *   Draw route
             */
            if (markerPoints != null) drawRoute(markerPoints);
            isInternetAvailable = commonMethods.isOnline(getApplicationContext());


            searchViewset(pickup, drop);


            toggle.setDrawerIndicatorEnabled(false);
            // Show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Doesn't have to be onBackPressed
                        //onBackPressed();
                        isRequest = false;
                        enableViews(false, false);
                        markerPoints.clear();
                        pickupaddresss = "";
                        dropaddress = "";
                        bottomDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_down);
                        request_view.startAnimation(bottomDown);
                        request_view.setVisibility(View.GONE);
                        whereto.setVisibility(View.VISIBLE);
                        whereto_and_schedule.setVisibility(View.VISIBLE);

                        rltContactAdmin.setVisibility(View.GONE);
                        edit_map.setVisibility(View.INVISIBLE);

                    }
                });

                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {


            mMap.clear();
            if (resetToRide) {
                resetViewsToRide();
            }
            if (mLastLocation != null) {
                changeMap(mLastLocation);
                DebuggableLogD(TAG, "ON connected");
            }
            // Remove back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            toggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
            sessionManager.setIsrequest(false);
        }

        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }

    public void searchViewset(LatLng pickup, LatLng drop) {
        if (!isInternetAvailable) {
            commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
        } else {
            fetchAddress(drop, "dropfulladdress");
            locationHashMap = new HashMap<>();
            locationHashMap.put("pickup_latitude", String.valueOf(pickup.latitude));
            locationHashMap.put("pickup_longitude", String.valueOf(pickup.longitude));
            locationHashMap.put("drop_latitude", String.valueOf(drop.latitude));
            locationHashMap.put("drop_longitude", String.valueOf(drop.longitude));
            locationHashMap.put("schedule_date", sessionManager.getScheduleDate());
            locationHashMap.put("schedule_time", sessionManager.getPresentTime());
            locationHashMap.put("user_type", sessionManager.getType());
            locationHashMap.put("token", sessionManager.getAccessToken());
            locationHashMap.put("timezone", TimeZone.getDefault().getID());
            searchCars();
        }
        requestubers.setVisibility(View.INVISIBLE);
        edit_map.setVisibility(View.INVISIBLE);
        rltContactAdmin.setVisibility(View.GONE);
        listView.setVisibility(View.INVISIBLE);
        paymentmethod.setVisibility(View.GONE);
        requestuber.setVisibility(View.INVISIBLE);
        loading_car.setVisibility(View.VISIBLE);
        no_car.setVisibility(View.INVISIBLE);
        requestuber.setEnabled(false);

        bottomUp = AnimationUtils.loadAnimation(getApplicationContext(), // Animation for bottom up and down
                R.anim.bottom_up);
        request_view.startAnimation(bottomUp);
        request_view.setVisibility(View.VISIBLE);
        whereto.setVisibility(View.GONE);
        whereto_and_schedule.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {

        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data)) commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {

            // Get Rider Profile
            case REQ_GET_RIDER_PROFILE:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessProfile(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_GladePay:
                if(jsonResp.isSuccess())
                {
                    commonMethods.hideProgressDialog();
                    onSuccessInitGladePay(jsonResp);
                }else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            // Update Rider Location
            case REQ_UPDATE_LOCATION:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_GET_DRIVER:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessDriver(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            // Send Request
            case REQ_SEND_REQUEST:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;

            // Search Cars
            case REQ_SEARCH_CARS:
                if (jsonResp.isSuccess()) {
                    if (jsonResp.getStatusCode().equals("1")) {
                        commonMethods.hideProgressDialog();
                        onSuccessSearchCar(jsonResp);
                    } else if (jsonResp.getStatusCode().equals("2")) {
                        commonMethods.hideProgressDialog();
                        no_car_txt.setText(jsonResp.getStatusMsg());
                        requestubers.setVisibility(View.VISIBLE);
                        edit_map.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                        paymentmethod.setVisibility(View.GONE);
                        requestuber.setVisibility(View.VISIBLE);
                        no_car.setVisibility(View.VISIBLE);
                        loading_car.setVisibility(View.GONE);
                        requestuber.setEnabled(false);
                    }
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    requestubers.setVisibility(View.VISIBLE);
                    edit_map.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    paymentmethod.setVisibility(View.GONE);
                    requestuber.setVisibility(View.VISIBLE);
                    no_car.setVisibility(View.VISIBLE);
                    loading_car.setVisibility(View.GONE);
                    requestuber.setEnabled(false);
                    no_car_txt.setText(getResources().getString(R.string.car_unavailable));
                    commonMethods.hideProgressDialog();
                }
                break;
            case REQ_INCOMPLETE_TRIP_DETAILS: {
                if (jsonResp.isSuccess()) {
                    try {
                        addFirebaseDatabase.removeRequestTable();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    resetToRide();
                    ResumeRiderTipsResponse(jsonResp);
                }
                break;
            }
            default:
                break;
        }
    }
    private void onSuccessInitGladePay(JsonResponse jsonResp) {
        try {
            JSONObject response=new JSONObject(jsonResp.getStrResponse());

            merchantId=response.getString("merchant_id");
            merchantKey=response.getString("merchant_key");
            Brand=response.getString("brand");
            last4=response.getString("last4");
            isLiveServer=response.getBoolean("is_live");
            gladepay_baseUrl=response.getString("glade_pay_url");
            card_token=response.getString("token");
            System.out.println("merchantId"+merchantId+merchantKey);
            initializeGladepay();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initializeGladepay() {
        if(merchantKey!=null && !merchantKey.isEmpty() && merchantId !=null && !merchantId.isEmpty())
        {
            System.out.println("initialize");
            GladepaySdk.initialize(getApplicationContext());
            GladepaySdk.setLiveStatus(isLiveServer);
            GladepaySdk.setMerchantId(merchantId);
            GladepaySdk.setMerchantKey(merchantKey);
        }
    }

    private void resetToRide() {
        toggle.setDrawerIndicatorEnabled(false);
        // Show back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
        // clicks are disabled i.e. the UP button will not work.
        // We need to add a listener, as in below, so DrawerToggle will forward
        if (!mToolBarNavigationListenerIsRegistered) {
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Doesn't have to be onBackPressed
                    //onBackPressed();

                    try {
                        isRequest = false;
                        enableViews(false, true);
                        CommonKeys.IS_ALREADY_IN_TRIP = true;
                        movepoints.clear();
                        if(polyline!=null){
                            polyline.remove();
                        }
                        pickupaddresss = "";
                        dropaddress = "";
                        bottomDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_down);
                        request_view.startAnimation(bottomDown);
                        request_view.setVisibility(View.GONE);
                        whereto.setVisibility(View.VISIBLE);
                        whereto_and_schedule.setVisibility(View.VISIBLE);


                        edit_map.setVisibility(View.INVISIBLE);
                        rltContactAdmin.setVisibility(View.GONE);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });

            mToolBarNavigationListenerIsRegistered = true;
        }
    }

    private void resetViewsToRide() {
        whereto.setVisibility(View.VISIBLE);
        whereto_and_schedule.setVisibility(View.VISIBLE);
        meetlayout.setVisibility(View.GONE);
        bottomlayout.setVisibility(View.GONE);

        pickupOptions.visible(false);
        sos.setVisibility(View.GONE);
        sessionManager.setIsTrip(false);
        sessionManager.setTripId("");
        sessionManager.setTripStatus("");
        isTripBegin = false;
        sessionManager.setDriverAndRiderAbleToChat(false);
    }

    private void ResumeRiderTipsResponse(JsonResponse jsonResp) {
        acceptedDriverDetails = gson.fromJson(jsonResp.getStrResponse(), AcceptedDriverDetails.class);
        String tripStatus = acceptedDriverDetails.getPaymentDetails().getTripStatus();
        sessionManager.setTripId(acceptedDriverDetails.getTripId());
        PaymentDetails paymentDetails = acceptedDriverDetails.getPaymentDetails();
        ArrayList<InvoiceModel> invoiceModels = acceptedDriverDetails.getInvoice();
        sessionManager.setIsrequest(false);
        sessionManager.setIsTrip(true);
        // If trips status is schedule or begin trip or endtrip to open the trips page ( Map Page)
        if ("Scheduled".equals(tripStatus) || "Begin trip".equals(tripStatus) || "End trip".equals(tripStatus)) {
            commonMethods.hideProgressDialog();
            if ("Scheduled".equals(tripStatus)) {
                sessionManager.setTripStatus("arrive_now");
                isTripBegin = false;
            } else if ("Begin trip".equals(tripStatus)) {
                sessionManager.setTripStatus("arrive_now");
                isTripBegin = false;
            } else if ("End trip".equals(tripStatus)) {
                sessionManager.setTripStatus("end_trip");
                isTripBegin = true;
            }
            sessionManager.setDriverAndRiderAbleToChat(true);
            isTripFunc();
            /*Intent requstreceivepage = new Intent(this, MainActivity.class);
            requstreceivepage.putExtra("driverDetails", acceptedDriverDetails);
            if ("Scheduled".equals(tripStatus) || "Begin trip".equals(tripStatus)) {
                requstreceivepage.putExtra("isTripBegin", false);
            } else if ("End trip".equals(tripStatus)) {
                requstreceivepage.putExtra("isTripBegin", true);
            }
            sessionManager.setDriverAndRiderAbleToChat(true);
            startActivity(requstreceivepage);*/
        } else if ("Rating".equals(tripStatus)) {  // To open rating page
            sessionManager.setDriverAndRiderAbleToChat(false);
            CommonMethods.stopFirebaseChatListenerService(this);
            Intent rating = new Intent(this, DriverRatingActivity.class);
            rating.putExtra("imgprofile", acceptedDriverDetails.getProfileimg());
            commonMethods.hideProgressDialog();
            startActivity(rating);

        } else if ("Payment".equals(tripStatus)) {  // To open the payment page

            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);
            Bundle bundle = new Bundle();
            bundle.putSerializable("invoiceModels", invoiceModels);
            Intent main = new Intent(this, PaymentAmountPage.class);
            main.putExtra("AmountDetails", jsonResp.getStrResponse().toString());
            main.putExtra("paymentDetails", paymentDetails);
            main.putExtra("driverDetails", acceptedDriverDetails);
            main.putExtras(bundle);
            commonMethods.hideProgressDialog();
            startActivity(main);
        }
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        DebuggableLogV(TAG, "onFailure");
    }

    public void onSuccessDriver(JsonResponse jsonResp) {
        acceptedDriverDetails = gson.fromJson(jsonResp.getStrResponse(), AcceptedDriverDetails.class);
        try {
            sessionManager.setDriverName(acceptedDriverDetails.getDrivername());
            sessionManager.setDriverRating(acceptedDriverDetails.getRatingvalue());
            sessionManager.setDriverProfilePic(acceptedDriverDetails.getProfileimg());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onSuccessProfile(JsonResponse jsonResp) {
        RiderProfile riderProfile = gson.fromJson(jsonResp.getStrResponse(), RiderProfile.class);
        String adminContact = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "contact", String.class);
        sessionManager.setAdminContact(adminContact);
        String currency_code = riderProfile.getCurrencyCode();
        String currency_symbol = riderProfile.getCurrencySymbol();
        currency_symbol = Html.fromHtml(currency_symbol).toString();
        String first_name = riderProfile.getFirstName();
        String last_name = riderProfile.getLastName();
        String user_thumb_image = riderProfile.getProfileImage();
        home = riderProfile.getHome();
        work = riderProfile.getWork();
        sessionManager.setFirstName(first_name);
        sessionManager.setLastName(last_name);
        sessionManager.setEmail(riderProfile.getEmailId());

        System.out.print("OutputEmail" + first_name + " " + last_name+" "+sessionManager.getemail());

        username.setText(first_name + " " + last_name);
        Picasso.with(getApplicationContext()).load(user_thumb_image).into(userprofile);
        sessionManager.setCurrencyCode(currency_code);
        sessionManager.setCurrencySymbol(currency_symbol);
        sessionManager.setHomeAddress(home);
        sessionManager.setWorkAddress(work);
        sessionManager.setProfileDetail(jsonResp.getStrResponse());
        sessionManager.setWalletAmount(riderProfile.getWalletAmount());
    }

    public void onSuccessSearchCar(JsonResponse jsonResp) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                requestubers.setVisibility(View.INVISIBLE);
                edit_map.setVisibility(View.INVISIBLE);
                rltContactAdmin.setVisibility(View.GONE);
                listView.setVisibility(View.INVISIBLE);
                paymentmethod.setVisibility(View.GONE);
                requestuber.setVisibility(View.INVISIBLE);
                loading_car.setVisibility(View.VISIBLE);
                no_car.setVisibility(View.INVISIBLE);
                requestuber.setEnabled(false);
            }
        });
        //  commonMethods.hideProgressDialog();
//        SearchCarResult searchCar = gson.fromJson(jsonResp.getStrResponse(),SearchCarResult.class);
        //   ArrayList<NearestCar> nearestCarss= searchCar.getNearestCar();
        /*GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(NearCarList.class, new CarListDeserializer());
        Gson gson = builder.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
        NearCarList list = gson.fromJson(json, NearCarList.class);*/

        nearestCars.clear();
       /* boolean selectedcar = true;
        for (int i=0;i<nearestCarss.size();i++){
            if (nearestCarss.get(i).getMinTime().equals("No cabs")){
                nearestCarss.get(i).setCarIsSelected(false);
            }else if (Integer.parseInt(nearestCarss.get(i).getMinTime())<=1){


                nearestCarss.get(i).setMinTime(nearestCarss.get(i).getMinTime() + " " + "Min");
                if (selectedcar) {
                    nearestCarss.get(i).setCarIsSelected(true);
                    clickedCarName = nearestCarss.get(i).getCarName();
                    clickedcar = nearestCarss.get(i).getCarId();
                    caramount = nearestCarss.get(i).getFareEstimation();
                    lastposition = Integer.parseInt(clickedcar);
                    selectedcar = false;
                } else {
                    nearestCarss.get(i).setCarIsSelected(false);
                }
            }else {
                nearestCarss.get(i).setMinTime(nearestCarss.get(i).getMinTime() + " " + "Mins");
                if (selectedcar) {
                    nearestCarss.get(i).setCarIsSelected(true);
                    clickedCarName = nearestCarss.get(i).getCarName();
                    caramount = nearestCarss.get(i).getFareEstimation();
                    clickedcar = nearestCarss.get(i).getCarId();
                    lastposition = Integer.parseInt(clickedcar);
                    selectedcar = false;
                } else {
                    nearestCarss.get(i).setCarIsSelected(false);
                }
            }
            this.nearestCars.add(nearestCarss.get(i));
        }*/
        requestubers.setVisibility(View.VISIBLE);
        edit_map.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        paymentmethod.setVisibility(View.VISIBLE);
        requestuber.setVisibility(View.VISIBLE);
        no_car.setVisibility(View.INVISIBLE);
        loading_car.setVisibility(View.GONE);
        requestuber.setEnabled(true);
        // nearest_carObj = response.getJSONObject("nearest_car");
        adapter.notifyDataChanged();
        if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
            date = sessionManager.getScheduleDateTime();

            requestuber.setText(getResources().getString(R.string.schedule) + clickedCarName + "\n" + date);
        } else {
            requestuber.setText(getResources().getString(R.string.requestuber) + clickedCarName);
        }


        try {
            //for (int i = 0; i < response.length(); i++) {
            JSONObject response = new JSONObject(jsonResp.getStrResponse());
            String statuscode;
            if (response != null) {
                statuscode = response.getString("status_code");
                String statusmessage = response.getString("status_message");

                // remove_status= remove_jsonobj.getString("status");
                DebuggableLogD("OUTPUT IS", statuscode);
                DebuggableLogD("OUTPUT IS", statusmessage);


                if (statuscode.matches("1")) {


                    requestubers.setVisibility(View.VISIBLE);
                    edit_map.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    paymentmethod.setVisibility(View.VISIBLE);
                    requestuber.setVisibility(View.VISIBLE);
                    no_car.setVisibility(View.INVISIBLE);
                    loading_car.setVisibility(View.GONE);
                    requestuber.setEnabled(true);
                    nearest_carObj = response.getJSONObject("nearest_car");

                    // searchlist.clear();
                    Iterator iterator = nearest_carObj.keys();

                    boolean selectedcar = true;
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        if (nearest_carObj.has(key)) {

                            JSONObject car = nearest_carObj.getJSONObject(key);

                            NearestCar listdata = new NearestCar();
                            listdata.setCarId(car.getString("car_id"));
                            listdata.setCarName(car.getString("car_name"));

                            if (car.getString("min_time").equals("No cabs") || car.getString("min_time").equals("")) {
                                listdata.setMinTime(getResources().getString(R.string.no_cabs));
                                listdata.setCarIsSelected(false);
                            } else if (Integer.valueOf(car.getString("min_time")) <= 1) {
                                listdata.setMinTime(car.getString("min_time") + " " + "Min");
                                if (selectedcar) {
                                    listdata.setCarIsSelected(true);
                                    clickedCarName = car.getString("car_name");
                                    clickedcar = car.getString("car_id");
                                    isPeakPriceAppliedForClickedCar = car.getString("apply_peak");
                                    peakPriceIdForClickedCar = car.getString("peak_id");
                                    minimumFareForClickedCar = car.getString("min_fare");
                                    perKMForClickedCar = car.getString("per_km");
                                    perMinForClickedCar = car.getString("per_min");
                                    peakPriceforClickedCar = car.getString("peak_price");
                                    locationIDForSelectedCar = car.getString("location_id");
                                    peakIDForSelectedCar = car.getString("peak_id");

                                    caramount = car.getString("fare_estimation");
                                    lastposition = Integer.parseInt(car.getString("car_id"));
                                    selectedcar = false;
                                } else {
                                    listdata.setCarIsSelected(false);
                                }
                            } else {
                                listdata.setMinTime(car.getString("min_time") + " " + "Mins");
                                if (selectedcar) {
                                    listdata.setCarIsSelected(true);
                                    clickedCarName = car.getString("car_name");
                                    caramount = car.getString("fare_estimation");
                                    clickedcar = car.getString("car_id");
                                    isPeakPriceAppliedForClickedCar = car.getString("apply_peak");
                                    peakPriceIdForClickedCar = car.getString("peak_id");
                                    minimumFareForClickedCar = car.getString("min_fare");
                                    perKMForClickedCar = car.getString("per_km");
                                    perMinForClickedCar = car.getString("per_min");
                                    peakPriceforClickedCar = car.getString("peak_price");
                                    lastposition = Integer.parseInt(car.getString("car_id"));
                                    locationIDForSelectedCar = car.getString("location_id");
                                    peakIDForSelectedCar = car.getString("peak_id");
                                    selectedcar = false;
                                } else {
                                    listdata.setCarIsSelected(false);
                                }
                            }
                            listdata.setFareEstimation(car.getString("fare_estimation"));
                            listdata.setBaseFare(car.getString("base_fare"));
                            listdata.setMinFare(car.getString("min_fare"));
                            listdata.setPerMin(car.getString("per_min"));
                            listdata.setPerKm(car.getString("per_km"));
                            listdata.setCapacity(car.getString("capacity"));
                            listdata.setApplyPeak(car.getString("apply_peak"));
                            listdata.setPeakPrice(car.getString("peak_price"));
                            listdata.setPeakId(car.getString("peak_id"));
                            listdata.setCarActiveImage(car.getString("car_active_image"));
                            listdata.setCarImage(car.getString("car_image"));
                            listdata.setLocationID(car.getString("location_id"));
                            listdata.setPeak_id(car.getString("peak_id"));


                            nearestCars.add(listdata);
                            //  DebuggableLogD("search list.length", String.valueOf(searchlist.size()));
                        }
                    }
                    adapter.notifyDataChanged();
                    if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
                        date = sessionManager.getScheduleDateTime();
                        requestuber.setText(getResources().getString(R.string.schedule) + clickedCarName + "\n" + date);
                    } else {
                        requestuber.setText(getResources().getString(R.string.requestuber) + clickedCarName);
                    }
                    for (int i = 0; i < nearestCars.size(); i++) {
                        if (nearestCars.get(i).getMinTime().equalsIgnoreCase(getResources().getString(R.string.no_cabs))) {
                            if (rltContactAdmin.getVisibility() != View.VISIBLE) {
                                rltContactAdmin.setVisibility(View.VISIBLE);
                                requestuber.setEnabled(false);
                                requestuber.setBackground(getResources().getDrawable(R.drawable.d_botton_background));
                                requestuber.setText(getResources().getString(R.string.no_cars));
                            }
                        } else {
                            rltContactAdmin.setVisibility(View.GONE);
                            break;
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        getCar(nearest_carObj, clickedcar);
        //  commonMethods.hideProgressDialog();
    }

    /**
     * Get nearest API called
     */
    public void getCar(JSONObject nearest_carObj, String cartype) {


        removeMarkers(carmarkers);
        int pos = 0;
          /*  for (int i =0;i<nearestCars.size();i++){
                if (cartype.equals(nearestCars.get(i).getCarId()))
                    pos=i;
            }



            ArrayList <LocationModel> locationModel = nearestCars.get(pos).getLocation();
            if (locationModel.size() == 0) {
                requestuber.setEnabled(false);
                String statusmessage = "No cars available...";
                commonMethods.showMessage(this, dialog, statusmessage);
            } else {
                requestuber.setEnabled(true);
                totalcar = locationModel.size();
                for (int j = 0; j < locationModel.size(); j++) {

                    String latitude = locationModel.get(j).getLatitude();
                    String longitude = locationModel.get(j).getLongitude();
                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    drawMarker(latLng, cartype);
                }
            }*/
        try {
            JSONObject car = nearest_carObj.getJSONObject(cartype);

            JSONArray location = car.getJSONArray("location");
            if (location.length() == 0) {
                requestuber.setEnabled(false);
                String statusmessage = getResources().getString(R.string.no_cars);
                commonMethods.showMessage(this, dialog, statusmessage);
            } else {
                requestuber.setEnabled(true);
                totalcar = location.length();
                for (int j = 0; j < location.length(); j++) {
                    JSONObject cardata = location.getJSONObject(j);

                    String latitude = cardata.getString("latitude");
                    String longitude = cardata.getString("longitude");
                    LatLng latLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    drawMarker(latLng, cartype);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Show marker
     */
    private void drawMarker(LatLng point, String cartype) {
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        if ("1".equals(cartype)) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_go));
        } else if ("2".equals(cartype)) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_x));
        } else if ("3".equals(cartype)) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_xl));
        } else {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_whitesuv));
        }
        markerOptions.anchor(0.5f, 0.5f);
        // Adding marker on the Google Map
        carmarkers.add(mMap.addMarker(markerOptions));
        // commonMethods.hideProgressDialog();
    }

    /**
     * Remove marker
     */
    private void removeMarkers(List<Marker> mMarkers) {
        for (Marker marker : mMarkers) {
            marker.remove();
        }
        mMarkers.clear();

    }

    /**
     * Draw route (Poliline)
     */
    public void drawRoute(final ArrayList markerPoints) {


        //
        //
        // Creating MarkerOptions


        // Setting the position of the marker
        MarkerOptions pickupmarker;
        MarkerOptions dropmarker;

        //
        if (null != markerPoints && markerPoints.size() >= 1) {
            InfoWindowData info = new InfoWindowData();
            info.setImage("snowqualmie");
            info.setHotel("Hotel : excellent hotels available");
            info.setFood("Food : all types of restaurants available");
            info.setTransport("Reach the site by bus, car and train.");

            pickupmarker = new MarkerOptions();
            pickupmarker.position((LatLng) markerPoints.get(0));
            pickupmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_dot));

            CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
            //mMap.setInfoWindowAdapter(customInfoWindow);

            mMap.addMarker(pickupmarker);
            /*Marker m = mMap.addMarker(pickupmarker);
            m.setTag(info);
            m.showInfoWindow();*/

            dropmarker = new MarkerOptions();
            dropmarker.position((LatLng) markerPoints.get(1));
            dropmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.dest_dot));
            mMap.addMarker(dropmarker);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
            builder.include(pickupmarker.getPosition());
            builder.include(dropmarker.getPosition());

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels / 2;
            int height = getResources().getDisplayMetrics().heightPixels / 2;
            int padding = (int) (width * 0.08); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

            mMap.moveCamera(cu);
            //  mMap.moveCamera(cu);
        }

        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            final LatLng origin = (LatLng) markerPoints.get(0);
            LatLng dest = (LatLng) markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);


            final DownloadTask downloadTask = new DownloadTask(new PolylineOptionsInterface() {
                @Override
                public void getPolylineOptions(PolylineOptions output, ArrayList points) {


                    if (mMap != null) {
                        mMap.addPolyline(output);
                        if (points != null && points.size() > 0) {
                            LatLng pikcuplatlng = (LatLng) markerPoints.get(0);
                            LatLng droplatlng = (LatLng) markerPoints.get(1);

                            String encodedPathLocations = PolyUtil.encode(points);
                            trip_path = encodedPathLocations;

                            System.out.print("polyline " + trip_path + " End");
                            String pathString = "&path=color:0x000000ff%7Cweight:4%7Cenc:" + encodedPathLocations;
                            String pickupstr = pikcuplatlng.latitude + "," + pikcuplatlng.longitude;
                            String dropstr = droplatlng.latitude + "," + droplatlng.longitude;
                            String positionOnMap = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "pickup.png|" + pickupstr;
                            String positionOnMap1 = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "drop.png|" + dropstr;


                            /**
                             *   Generate static map
                             */
                            staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=640x480&" + pikcuplatlng.latitude + "," + pikcuplatlng.longitude + pathString + "" + positionOnMap + "" + positionOnMap1 + "&zoom=14" + "&key=" + sessionManager.getGoogleMapKey() + "&language=" + Locale.getDefault();


                            // MapAnimator.getInstance().animateRoute(mMap,output.getPoints());
                        }
                        //  MapAnimator.getInstance().animateRoute(mMap,output.getPoints());
                    } else {
                        Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
                    }
                }
            }, this);

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    /**
     * Fare estimate function
     */
    public void fareEstimate(final int position, final ArrayList<NearestCar> searchlist) {
        final BottomSheetDialog dialog2 = new BottomSheetDialog(MainActivity.this);
        dialog2.setContentView(R.layout.activity_cars_available);

        TextView carname;
        TextView amount;
        TextView people;
        ImageView carImage;

        carname = (TextView) dialog2.findViewById(R.id.carname);
        amount = (TextView) dialog2.findViewById(R.id.amount);
        people = (TextView) dialog2.findViewById(R.id.people);
        carImage = dialog2.findViewById(R.id.car2);

        carname.setText(searchlist.get(position).getCarName());
        amount.setText(sessionManager.getCurrencySymbol() + searchlist.get(position).getFareEstimation());
        people.setText(searchlist.get(position).getCapacity() + getResources().getString(R.string.people));
        Picasso.with(this).load(searchlist.get(position).getCarActiveImage()).error(R.drawable.car).into(carImage);

        ImageView faredetails = (ImageView) dialog2.findViewById(R.id.faredeatils);
        RelativeLayout done = (RelativeLayout) dialog2.findViewById(R.id.done);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });
        faredetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FareBreakdown.class);
                intent.putExtra("position", position);
                intent.putExtra("list", (Serializable) searchlist);
                startActivity(intent);
                overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
            }
        });
        dialog2.show();
    }

    /**
     * schedule ride function
     */
    public void scheduleRide() {
        final BottomSheetDialog dialog3 = new BottomSheetDialog(MainActivity.this);
        dialog3.setContentView(R.layout.activity_schedule_ride);
        scheduleridetext = (TextView) dialog3.findViewById(R.id.time_date);
        scheduleride_text = (TextView) dialog3.findViewById(R.id.scheduleride_text);
        singleDateAndTimePicker = (SingleDateAndTimePicker) dialog3.findViewById(R.id.single_day_picker);
        final Button set_pickup_window = (Button) dialog3.findViewById(R.id.set_pickup_window);
        set_pickup_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.dismiss();
                String date = finalDay + " - " + time;
                schedulewhere(date, "Schedule");
            }
        });

        dialog3.show();
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

                        if (jsonObject.getJSONObject("custom").has("arrive_now")) {
                            isTripBegin = false;
                            statusDialog(getResources().getString(R.string.driverarrrive), 0);
                            String trip_id = jsonObject.getJSONObject("custom").getJSONObject("arrive_now").getString("trip_id");
                            sessionManager.setTripId(trip_id);

                            //   req_id = jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("request_id");
                            //   pickup_address= jsonObject.getJSONObject("custom").getJSONObject("ride_request").getString("pickup_location");

                        } else if (jsonObject.getJSONObject("custom").has("begin_trip")) {
                            pickup_location.setText(acceptedDriverDetails.getDroplocation());
                            isTripBegin = true;
                            statusDialog(getResources().getString(R.string.yourtripbegin), 0);
                            String trip_id = jsonObject.getJSONObject("custom").getJSONObject("begin_trip").getString("trip_id");
                            sessionManager.setTripId(trip_id);

                        } else if (jsonObject.getJSONObject("custom").has("end_trip")) {
                            isTrip = false;
                            //statusDialog("Your Trip Ends Now...");
                            String trip_id = jsonObject.getJSONObject("custom").getJSONObject("end_trip").getString("trip_id");
                            String driver_image = jsonObject.getJSONObject("custom").getJSONObject("end_trip").getString("driver_thumb_image");
                            sessionManager.setTripId(trip_id);
                            sessionManager.setDriverProfilePic(driver_image);
                            sessionManager.setTripStatus("end_trip");
                            startActivity(new Intent(getApplicationContext(), PaymentAmountPage.class));
                            /*Intent rating = new Intent(getApplicationContext(), DriverRatingActivity.class);
                            if (!acceptedDriverDetails.getProfileimg().equals(""))
                                rating.putExtra("imgprofile", acceptedDriverDetails.getProfileimg());
                            startActivity(rating);*/
                        } else if (jsonObject.getJSONObject("custom").has("cancel_trip")) {

                            sessionManager.setIsrequest(false);
                            sessionManager.setIsTrip(false);
                            statusDialog(getResources().getString(R.string.yourtripcancelledbydriver), 1);
                        }

                    } catch (JSONException e) {

                    }
                }
            }
        };
    }

    /**
     * Trip status dialog ( like Arrive now, Begin trip, Payment completed )
     */
    public void statusDialog(String message, final int show) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.addphoto_header, null);
        TextView tl = (TextView) view.findViewById(R.id.header);
        tl.setText(message);
        builder.setCustomTitle(view);
        //.setMessage("Are you sure you want to delete this entry?")
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (show == 0) {
                    if (!isRequest && isTrip) {
                        isTripFunc();
                    } else {
                        isRequestFunc();
                    }
                } else {
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(false);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
        positiveButtonLL.gravity = Gravity.END;
        positiveButton.setLayoutParams(positiveButtonLL);
    }

    /**
     * Mainactivity called from other activity
     */
    @Override
    public void onResume() {

        super.onResume();
        isMainActivity = true;

        /*YoYo.with(Techniques.FlipInX)
                .duration(500)
                .repeat(1)
                .playOn(findViewById(R.id.whereto_and_schedule));

        //whereto_and_schedule.startAnimation(slideUpAnimation);*/

        if (sessionManager.getTripId() != null && !TextUtils.isEmpty(sessionManager.getTripId()) && mSearchedLocationReferenceListener == null)
            addLatLngChangeListener();
        /*if(mSearchedLocationReferenceListener!=null)
            mFirebaseDatabase.removeEventListener(mSearchedLocationReferenceListener);*/

        System.out.println("Payment method two : "+sessionManager.getPaymentMethod());
        if (sessionManager.getPaymentMethod().equals("")) {
            sessionManager.setPaymentMethod(CommonKeys.PAYMENT_CASH);
            sessionManager.setIsWallet(true);
        }


        if (sessionManager.getPromoCount() > 0) {
            if (sessionManager.getPaymentMethod().equals(CommonKeys.PAYMENT_CASH)) {
                paymentmethod_img.setImageDrawable(getResources().getDrawable(R.drawable.cash));
                paymentmethod_type.setBackground(getResources().getDrawable(R.drawable.d_green_fillboarder));
                paymentmethod_type.setTextColor(getResources().getColor(R.color.white));
                paymentmethod_type.setText(getResources().getString(R.string.promo_applied));
                paymentmethod_type.setAllCaps(false);
            } else if (sessionManager.getPaymentMethod().equals(CommonKeys.PAYMENT_GLADEPAY)) {
                paymentmethod_img.setImageDrawable(getResources().getDrawable(R.drawable.gladepay));
               // paymentmethod_img.setImageDrawable(CommonMethods.getCardImage(sessionManager.getCardBrand(), getResources()));
                paymentmethod_type.setBackground(getResources().getDrawable(R.drawable.d_green_fillboarder));
                paymentmethod_type.setTextColor(getResources().getColor(R.color.white));
                paymentmethod_type.setText(getResources().getString(R.string.promo_applied));
                paymentmethod_type.setAllCaps(false);

            } else {
                paymentmethod_img.setImageDrawable(getResources().getDrawable(R.drawable.gladepay));
                paymentmethod_type.setBackground(getResources().getDrawable(R.drawable.d_green_fillboarder));
                paymentmethod_type.setTextColor(getResources().getColor(R.color.white));
                paymentmethod_type.setAllCaps(false);
                paymentmethod_type.setText(getResources().getString(R.string.promo_applied));
            }
        } else {
            if (sessionManager.getPaymentMethod().equals(CommonKeys.PAYMENT_CASH)) {
                paymentmethod_img.setImageDrawable(getResources().getDrawable(R.drawable.cash));
                paymentmethod_type.setText(getResources().getString(R.string.cash));
            } else if (sessionManager.getPaymentMethod().equals(CommonKeys.PAYMENT_GLADEPAY)) {
                paymentmethod_img.setImageDrawable(getResources().getDrawable(R.drawable.gladepay));
                paymentmethod_type.setText(getResources().getString(R.string.gladepay));
               /* paymentmethod_img.setImageDrawable(CommonMethods.getCardImage(sessionManager.getCardBrand(), getResources()));
                paymentmethod_type.setText((sessionManager.getCardValue()));*/
            } else {
                paymentmethod_img.setImageDrawable(getResources().getDrawable(R.drawable.gladepay));
                paymentmethod_type.setText(getResources().getString(R.string.gladepay));
            }
        }

        if (sessionManager.getIsWallet()) {
            DebuggableLogV("isWallet", "iswallet" + sessionManager.getWalletAmount());
            //iswallet="Yes";
            if (sessionManager.getWalletAmount() != null && !"".equals(sessionManager.getWalletAmount())) {
                if (Float.valueOf(sessionManager.getWalletAmount()) > 0)
                    wallet_img.setVisibility(View.VISIBLE);
                else wallet_img.setVisibility(View.GONE);
            }
        } else {
            //iswallet="No";
            wallet_img.setVisibility(View.GONE);
        }

        /**
         *   Get Rider Profile page
         */
        String profiledetails = sessionManager.getProfileDetail();

        if ("".equals(profiledetails)) {

            isInternetAvailable = commonMethods.isOnline(getApplicationContext());
            if (!isInternetAvailable) {
                commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
            } else {
                getRiderDetails();
            }

        } else {
            isInternetAvailable = commonMethods.isOnline(getApplicationContext());
            if (!isInternetAvailable) {
                commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.no_connection));
            } else {
                getRiderDetails();
            }
            //loaddata(profiledetails);
        }
        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());


        // code to initiate firebase chat service
        if (!TextUtils.isEmpty(sessionManager.getTripId()) && (sessionManager.isDriverAndRiderAbleToChat()) && (!CommonMethods.isMyBackgroundServiceRunning(FirebaseChatNotificationService.class, this))) {
            startFirebaseChatListenerService(this);
        }
    }




    /* ***************************************************************** */
    /*                 Trip   Functionality                              */
    /* ***************************************************************** */

    @Override
    public void onPause() {
        super.onPause();
        //isMainActivity = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    public void isTripFunc() {


        whereto.setVisibility(View.GONE);
        whereto_and_schedule.setVisibility(View.GONE);
        meetlayout.setVisibility(View.VISIBLE);
        bottomlayout.setVisibility(View.VISIBLE);
        String status = sessionManager.getTripStatus();
        if ((status.equals("accept_request")) || (status.equals("arrive_now")) || (status.equals("begin_trip"))) {
            if (!TextUtils.isEmpty(sessionManager.getTripId()) && (sessionManager.isDriverAndRiderAbleToChat()) && (!CommonMethods.isMyBackgroundServiceRunning(FirebaseChatNotificationService.class, this))) {
                startFirebaseChatListenerService(this);
            }
        }

        /*setting driver details for chat page*/
        sessionManager.setDriverName(acceptedDriverDetails.getDrivername());
        sessionManager.setDriverRating(acceptedDriverDetails.getRatingvalue());
        sessionManager.setDriverProfilePic(acceptedDriverDetails.getProfileimg());

        driver_name.setText(acceptedDriverDetails.getDrivername());
        driver_car_name.setText(acceptedDriverDetails.getVehicleName());
        if (acceptedDriverDetails.getRatingvalue().equals("") || acceptedDriverDetails.getRatingvalue().equals("0.0")) {
            driver_rating.setVisibility(View.GONE);
        } else {
            driver_rating.setText(acceptedDriverDetails.getRatingvalue());
        }
        driver_car_number.setText(acceptedDriverDetails.getVehicleNumber());
        //driverCarName.setText(acceptedDriverDetails.getVehicleName());

        Picasso.with(getApplicationContext()).load(acceptedDriverDetails.getProfileimg()).into(driver_image);

        LatLng pickuplatlng = new LatLng(Double.valueOf(acceptedDriverDetails.getPickuplatitude()), Double.valueOf(acceptedDriverDetails.getPickuplongitude()));
        LatLng droplatlng = new LatLng(Double.valueOf(acceptedDriverDetails.getDroplatitude()), Double.valueOf(acceptedDriverDetails.getDroplongitude()));

        if (!"".equals(sessionManager.getTripStatus())
                //|| !sessionManager.getTripStatus().equals("null")
                && sessionManager.getTripStatus().equals("begin_trip") || sessionManager.getTripStatus().equals("end_trip")) {
            pickup_location.setText(acceptedDriverDetails.getDroplocation());
        } else {
            pickup_location.setText(acceptedDriverDetails.getPickuplocation());
        }
        //pickup_location.setText(acceptedDriverDetails.getPickuplocation());


        ArrayList pickupPoints = new ArrayList();
        if (isTripBegin) {
            /*movepoints.add(0,driverlatlng);
            movepoints.add(1,driverlatlng);*/
            // Adding new item to the ArrayList
            if (firstloop) {
                driverlatlng = new LatLng(Double.valueOf(acceptedDriverDetails.getDriverlatitude()), Double.valueOf(acceptedDriverDetails.getDriverlongitude()));
                movepoints.add(0, driverlatlng);
                movepoints.add(1, driverlatlng);
                firstloop = false;
            }
            // driverlatlng=new LatLng(Double.valueOf(acceptedDriverDetails.getDriverlatitude()),Double.valueOf(acceptedDriverDetails.getDriverlongitude()));
            pickupPoints.add(0, driverlatlng);
            pickupPoints.add(1, droplatlng);

        } else {
            if (firstloop) {

                driverlatlng = new LatLng(Double.valueOf(acceptedDriverDetails.getDriverlatitude()), Double.valueOf(acceptedDriverDetails.getDriverlongitude()));
                movepoints.add(0, driverlatlng);
                movepoints.add(1, driverlatlng);
                firstloop = false;
            }
            pickupPoints.add(0, driverlatlng);
            pickupPoints.add(1, pickuplatlng);
        }


        updateRoute(driverlatlng);
        MoveMarker(driverlatlng);
        if ("begin_trip".equals(status) || "end_trip".equals(status)) {
            sos.setVisibility(View.VISIBLE);

        } else {
            sos.setVisibility(View.GONE);
        }

    }


    /**
     * Update route while trip (Car moving)
     */
    public void updateRoute(LatLng driverlatlng) {

        mMap.clear();
        LatLng pickuplatlng = new LatLng(Double.valueOf(acceptedDriverDetails.getPickuplatitude()), Double.valueOf(acceptedDriverDetails.getPickuplongitude()));
        LatLng droplatlng = new LatLng(Double.valueOf(acceptedDriverDetails.getDroplatitude()), Double.valueOf(acceptedDriverDetails.getDroplongitude()));

        // Setting the position of the marker

        if (isTripBegin) {
            pickupOptions.position(droplatlng);
            pickupOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__ic_pin_dropoff));
        } else {
            pickupOptions.position(pickuplatlng);
            pickupOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__ic_pin_pickup));
        }
        // Add new marker to the Google Map Android API V2
        mMap.addMarker(pickupOptions);

        // Creating MarkerOptions
        MarkerOptions dropOptions = new MarkerOptions();


        // Setting the position of the marker
        dropOptions.position(driverlatlng);
        dropOptions.anchor(0.5f, 0.5f);
        dropOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_booking_prime_map_topview));
        // Add new marker to the Google Map Android API V2
        carmarker = mMap.addMarker(dropOptions);

    }

    /**
     * Update route while trip (Car moving)
     */
    public void MoveMarker(LatLng driverlatlng) {

        // mMap.clear();
        LatLng pickuplatlng = new LatLng(Double.valueOf(acceptedDriverDetails.getPickuplatitude()), Double.valueOf(acceptedDriverDetails.getPickuplongitude()));
        LatLng droplatlng = new LatLng(Double.valueOf(acceptedDriverDetails.getDroplatitude()), Double.valueOf(acceptedDriverDetails.getDroplongitude()));

        // Creating MarkerOptions
        MarkerOptions pickupOptions = new MarkerOptions();

        // Setting the position of the marker

        if (isTripBegin) {
            pickupOptions.position(droplatlng);
            pickupOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__ic_pin_dropoff));
        } else {
            pickupOptions.position(pickuplatlng);
            pickupOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__ic_pin_pickup));
        }
        // Add new marker to the Google Map Android API V2
        // mMap.addMarker(pickupOptions);

        // Creating MarkerOptions
        MarkerOptions dropOptions = new MarkerOptions();


        // Setting the position of the marker
        dropOptions.position(driverlatlng);
        dropOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_booking_prime_map_topview));
        // Add new marker to the Google Map Android API V2
        // carmarker = mMap.addMarker(dropOptions);


        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(driverlatlng);
        if (isTripBegin) {
            builder.include(droplatlng);
        } else {
            builder.include(pickuplatlng);
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels / 2;
        int height = getResources().getDisplayMetrics().heightPixels / 2;
        int padding = (int) (width * 0.08); // offset from edges of the map 10% of screen

        if (firstloop) {
            System.out.println("iSFirst Loop");
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.moveCamera(cu);
        }

        String url;
        if (isTripBegin) {
            // Getting URL to the Google Directions API
            url = getDirectionsUrl(driverlatlng, droplatlng);
        } else {
            url = getDirectionsUrl(driverlatlng, pickuplatlng);
        }
        DownloadTask downloadTask = new DownloadTask(new PolylineOptionsInterface() {
            @Override
            public void getPolylineOptions(PolylineOptions output, ArrayList points) {

                if (mMap != null && output != null) {
                    if (polyline != null) polyline.remove();
                    polyline = mMap.addPolyline(output);
                }
            }
        }, this);

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    public void isRequestFunc() {
        meetlayout.setVisibility(View.GONE);
        bottomlayout.setVisibility(View.GONE);
        // whereto.setVisibility(View.VISIBLE);
    }

    /**
     * Get Rider profile API called
     */
    public void getRiderDetails() {
        //commonMethods.showProgressDialog(MainActivity.this, customDialog);
        isMainActivity = true;
        apiService.getRiderProfile(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_GET_RIDER_PROFILE, this));

    }

    /**
     * Update Rider Location
     */
    public void updateRiderLoc() {
        //commonMethods.showProgressDialog(MainActivity.this, customDialog);
        apiService.updateLocation(locationHashMap).enqueue(new RequestCallback(REQ_UPDATE_LOCATION, this));

    }

    /**
     * Serach car API called
     */
    public void searchCars() {
        //commonMethods.showProgressDialog(MainActivity.this, customDialog);
        apiService.searchCars(locationHashMap).enqueue(new RequestCallback(REQ_SEARCH_CARS, this));

    }

    /**
     * Send request API called
     */
    public void sendRequest() {
        //commonMethods.showProgressDialog(MainActivity.this, customDialog);

        Intent sendrequst = new Intent(getApplicationContext(), SendingRequestActivity.class);
        sendrequst.putExtra("loadData", "load");
        sendrequst.putExtra("carname", clickedCarName);
        sendrequst.putExtra("url", "");
        sendrequst.putExtra("mapurl", staticMapURL);
        sendrequst.putExtra("totalcar", totalcar);
        sendrequst.putExtra("hashMap", locationHashMap);
        startActivity(sendrequst);
        apiService.sendRequest(locationHashMap).enqueue(new RequestCallback(REQ_SEND_REQUEST, this));
        finish();
    }

    /**
     * Get Accepted driver details
     */
    public void getDriverDetails() {
        // commonMethods.showProgressDialog(MainActivity.this, customDialog);
        apiService.getDriverDetails(sessionManager.getAccessToken(), sessionManager.getTripId()).enqueue(new RequestCallback(REQ_GET_DRIVER, this));
    }


    /**
     * Network error
     */

    public void dialogfunction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.turnoninternet)).setCancelable(false).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Fetch address from location
     */
    public void fetchAddress(LatLng location, final String type) {
        alreadyRunning = false;
        address = null;
        getLocations = location;

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                if (Geocoder.isPresent()) {
                    try {

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(getLocations.latitude, getLocations.longitude, 1);
                        if (addresses != null) {
                            countrys = addresses.get(0).getCountryName();

                            String adress0 = addresses.get(0).getAddressLine(0);
                            String adress1 = addresses.get(0).getAddressLine(1);

                            address = adress0 + " " + adress1; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                            /*if (address != null) {
                                //   pickupaddresss=address;
                                return address;
                            }*/
                        }
                    } catch (Exception ignored) {
                        // after a while, Geocoder start to throw "Service not availalbe" exception. really weird since it was working before (same device, same Android version etc..
                    }
                }

                if (address != null) // i.e., Geocoder succeed
                {

                    return address;
                } else // i.e., Geocoder failed
                {
                    return fetchAddressUsingGoogleMap();
                }
            }

            // Geocoder failed :-(
            // Our B Plan : Google Map
            private String fetchAddressUsingGoogleMap() {

                addressList = new ArrayList<Address>();
                String googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + getLocations.latitude + "," + getLocations.longitude + "&sensor=false" + "&key=" + sessionManager.getGoogleMapKey();

                try {
                    JSONObject googleMapResponse = new JSONObject(ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl), new BasicResponseHandler()));

                    // many nested loops.. not great -> use expression instead
                    // loop among all results
                    JSONArray results = (JSONArray) googleMapResponse.get("results");
                    for (int i = 0; i < results.length(); i++) {


                        JSONObject result = results.getJSONObject(i);


                        String indiStr = result.getString("formatted_address");


                        Address addr = new Address(Locale.getDefault());


                        addr.setAddressLine(0, indiStr);
                        // countrys=addr.getCountryName();

                        addressList.add(addr);


                    }
                    /*countrys = ((JSONArray)googleMapResponse.get("results")).getJSONObject(0)
                            .getJSONArray("address_components").getJSONObject(3).getString("long_name");
                   */
                    countrys = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0).getJSONArray("address_components").getJSONObject(3).getString("long_name");
                    int len = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0).getJSONArray("address_components").length();
                    for (int i = 0; i < len; i++) {
                        if (((JSONArray) googleMapResponse.get("results")).getJSONObject(0).getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0).equals("country")) {
                            countrys = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0).getJSONArray("address_components").getJSONObject(i).getString("long_name");

                        }
                    }

                    if (addressList != null) {
                        // countrys = addressList.get(0).getCountryName();

                        String adress0 = addressList.get(0).getAddressLine(0);
                        String adress1 = addressList.get(0).getAddressLine(1);


                        //address = adress0+" "+adress1;
                        address = adress0; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        address.replaceAll("null", "");

                        if (address != null) {

                            return address;
                        }
                    }

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String address) {
                alreadyRunning = true;
                if (address != null) {
                    country = countrys;
                    // Do something with cityName
                    DebuggableLogI("GeocoderHelper", address);
                    if ("pickupaddress".equals(type)) {
                        pickupaddresss = address;
                        country = countrys;

                    } else if ("dropaddress".equals(type)) {
                        dropaddress = address;

                    } else if ("dropfulladdress".equals(type)) {
                        dropfulladdress = address;

                        dropaddress = address;

                        LatLng pickup = (LatLng) markerPoints.get(0);

                        fetchAddress(pickup, "pickupfulladdress");
                    } else if ("pickupfulladdress".equals(type)) {
                        pickupfulladdress = address;
                        pickupaddresss = address;


                    }
                } else {
                    commonMethods.showMessage(MainActivity.this, dialog, "Unable to get location please try again...");
                }
            }

            ;
        }.execute();
    }

    /**
     * Location permission request permission result
     */
    /* ***************************************************************** */
    /*                  Animate Marker for Live Tracking                 */
    /* ***************************************************************** */

    /**
     * Location enable dialog
     */
    private void displayLocationSettingsRequest() {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        DebuggableLogI(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        DebuggableLogI(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult((Activity) mContext, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            DebuggableLogI(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        DebuggableLogI(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                    default:
                        break;
                }
            }
        });
    }
    /*
     *  Move marker
     **/

    /*
     *  Move marker for given locations
     **/
    public void adddefaultMarker(LatLng latlng, LatLng latlng1) {
        try {


            Location startbearlocation = new Location(LocationManager.GPS_PROVIDER);
            Location endbearlocation = new Location(LocationManager.GPS_PROVIDER);

            startbearlocation.setLatitude(latlng.latitude);
            startbearlocation.setLongitude(latlng.longitude);

            endbearlocation.setLatitude(latlng1.latitude);
            endbearlocation.setLongitude(latlng1.longitude);

            if (endbear != 0.0) {
                startbear = endbear;
            }


            //carmarker.setPosition(latlng);
            // carmarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_booking_prime_map_topview));
            carmarker.setFlat(true);
            carmarker.setAnchor(0.5f, 0.5f);
            marker = carmarker;
            // Move map while marker gone
            ensureMarkerOnBounds(latlng, "updated");

            endbear = (float) bearing(startbearlocation, endbearlocation);
            endbear = (float) (endbear * (180.0 / 3.14));

            //double distance = Double.valueOf(twoDForm.format(startbearlocation.distanceTo(endbearlocation)));
            double distance = Double.valueOf((startbearlocation.distanceTo(endbearlocation)));

            if (distance > 0 && distance < 30) animateMarker(latlng1, marker, speed, endbear);

        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void animateMarker(final LatLng destination, final Marker marker, final float speed, final float endbear) {

        final LatLng[] newPosition = new LatLng[1];
        final LatLng[] oldPosition = new LatLng[1];
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.latitude, destination.longitude);
            long duration;
            Location newLoc = new Location(LocationManager.GPS_PROVIDER);
            newLoc.setLatitude(startPosition.latitude);
            newLoc.setLongitude(startPosition.longitude);
            Location prevLoc = new Location(LocationManager.GPS_PROVIDER);
            prevLoc.setLatitude(endPosition.latitude);
            prevLoc.setLongitude(endPosition.longitude);

            //final double distance = Double.valueOf(twoDForm.format(newLoc.distanceTo(prevLoc)));
            final double distance = Double.valueOf((newLoc.distanceTo(prevLoc)));

            duration = (long) ((distance / speed) * 950);

            duration = 2000;
            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            if (valueAnimator != null) {
                valueAnimator.cancel();
                valueAnimator.end();
            }
            valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(duration);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        newPosition[0] = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition[0]); // Move Marker
                        marker.setRotation(computeRotation(v, startRotation, endbear)); // Rotate Marker

                        /*if(oldPosition[0]==null)
                            oldPosition[0]=newPosition[0];
                        Location newLoc1 = new Location(LocationManager.GPS_PROVIDER);
                        newLoc1.setLatitude(newPosition[0].latitude);
                        newLoc1.setLongitude(newPosition[0].longitude);
                        Location prevLoc1 = new Location(LocationManager.GPS_PROVIDER);
                        prevLoc1.setLatitude(oldPosition[0].latitude);
                        prevLoc1.setLongitude(oldPosition[0].longitude);

                        double distance = Double.valueOf(twoDForm.format(newLoc1.distanceTo(prevLoc1)));
                        showLatLng(0.0,0.0,distance);

                        oldPosition[0]=newPosition[0];*/
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                }
            });

            valueAnimator.start();



            /*if(runnable_movemap!=null)
                handler_movemap.removeCallbacks(runnable_movemap);
            handler_movemap.removeCallbacks(null);
            runnable_movemap= new Runnable() {
                public void run() {

                    ensureMarkerOnBounds(newPosition[0],"position"); // Move map

                    handler_movemap.postDelayed(this, 500);
                }
            };
            handler_movemap.postDelayed(runnable_movemap, 500);*/
        }
    }

    /*
     *  Find GPS rotate position
     **/
    private double bearing(Location startPoint, Location endPoint) {
        double deltaLongitude = endPoint.getLongitude() - startPoint.getLongitude();
        double deltaLatitude = endPoint.getLatitude() - startPoint.getLatitude();
        double angle = (3.14 * .5f) - Math.atan(deltaLatitude / deltaLongitude);

        if (deltaLongitude > 0) return angle;
        else if (deltaLongitude < 0) return angle + 3.14;
        else if (deltaLatitude < 0) return 3.14;

        return 0.0f;
    }

    /*
     *  move map to center position while marker hide
     **/
    private void ensureMarkerOnBounds(LatLng toPosition, String type) {
        if (marker != null) {
            float currentZoomLevel = (float) mMap.getCameraPosition().zoom;
            float bearing = (float) mMap.getCameraPosition().bearing;
            /*if (16.5f > currentZoomLevel) {
                currentZoomLevel = 16.5f;
            }*/
            CameraPosition cameraPosition = new CameraPosition.Builder().target(toPosition).zoom(currentZoomLevel).bearing(bearing).build();

            if ("updated".equals(type)) {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                if (!mMap.getProjection().getVisibleRegion().latLngBounds.contains(toPosition)) {
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        }
    }

    /**
     * Driver Location change listener
     */
    private void addLatLngChangeListener() {
        DebuggableLogE(TAG, "Driver Location data called!");

        // User data change listener
        query = mFirebaseDatabase.child(sessionManager.getTripId());

        mSearchedLocationReferenceListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DebuggableLogE(TAG, "Driver Location data called! success");
                if (!sessionManager.getTripId().equals("")) {
                    DriverLocation driverLocation = dataSnapshot.getValue(DriverLocation.class);

                    // Check for null
                    if (driverLocation == null || driverLocation.lat == null || driverLocation.lng == null) {
                        DebuggableLogE(TAG, "Driver Location data is null!");
                        return;
                    }
                    showLatLng(Double.valueOf(driverLocation.lat), Double.valueOf(driverLocation.lng), 0.0);
                    liveTrackingFirebase(Double.valueOf(driverLocation.lat), Double.valueOf(driverLocation.lng));
                    DebuggableLogE(TAG, "Driver Location data is changed!" + driverLocation.lat + ", " + driverLocation.lng);
                } else {
                    query.removeEventListener(this);
                    mFirebaseDatabase.removeEventListener(this);
                    mFirebaseDatabase.onDisconnect();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                DebuggableLogE(TAG, "Failed to read user", error.toException());
            }
        });
    }

    /**
     * Live tracking function
     */
    public void liveTrackingFirebase(Double driverlat, Double driverlong) {

        driverlatlng = new LatLng(driverlat, driverlong);

        if (movepoints.size() < 1) {
            movepoints.add(0, driverlatlng);
            movepoints.add(1, driverlatlng);

        } else {
            movepoints.set(1, movepoints.get(0));
            movepoints.set(0, driverlatlng);
        }
        DecimalFormat twoDForm = new DecimalFormat("#.#######", DecimalFormatSymbols.getInstance(Locale.getDefault()));
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        twoDForm.setDecimalFormatSymbols(dfs);
        String zerolat = twoDForm.format(((LatLng) movepoints.get(0)).latitude);
        String zerolng = twoDForm.format(((LatLng) movepoints.get(0)).longitude);

        String onelat = twoDForm.format(((LatLng) movepoints.get(1)).latitude);
        String onelng = twoDForm.format(((LatLng) movepoints.get(1)).longitude);

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(((LatLng) movepoints.get(1)).latitude);
        location.setLongitude(((LatLng) movepoints.get(1)).longitude);
        location.setTime(System.currentTimeMillis());
        //if(location!=null)
        //speed = location.getSpeed();

        float calculatedSpeed = 0;
        float distance = 0;
        if (lastLocation != null) {
            double elapsedTime = (location.getTime() - lastLocation.getTime()) / 1_000; // Convert milliseconds to seconds

            // elapsedTime= Double.parseDouble(twoDForm.format(elapsedTime));


            if (elapsedTime > 0)
                calculatedSpeed = (float) (lastLocation.distanceTo(location) / elapsedTime);
            else calculatedSpeed = (float) (lastLocation.distanceTo(location) / 1);
            //calculatedSpeed= Float.valueOf(twoDForm.format(calculatedSpeed));
            distance = location.distanceTo(lastLocation);

        }


        lastLocation = location;

        //speed = location.hasSpeed() ? location.getSpeed() : calculatedSpeed;

        if (!Float.isNaN(calculatedSpeed) && !Float.isInfinite(calculatedSpeed)) {
            speed = calculatedSpeed;

        }

        if (speed <= 0) speed = 10;

        System.out.println("ZERO LATLNG " + zerolat);
        System.out.println("ZERO LATLNG " + zerolng);
        System.out.println("ONE LATLNG " + onelat);
        System.out.println("ONE LATLNG " + onelng);
        System.out.println("Distance " + distance);
        System.out.println("drivertripdetyailds " + acceptedDriverDetails);

        if ((!zerolat.equals(onelat) || !zerolng.equals(onelng)) && distance < 30 && acceptedDriverDetails != null) {
            System.out.println("print MoveMarker calling");
            MoveMarker((LatLng) movepoints.get(1));
            adddefaultMarker((LatLng) movepoints.get(1), (LatLng) movepoints.get(0));
            samelocation = false;
        }
    }

    public Date scheduleRideDate(Date dateToSelect) {
        final Calendar calendarMax = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);  //Normal date Format
        String newTime = "";
        try {
            Date d = df.parse(dateToSelect.toString());

            calendarMax.setTime(d);
            calendarMax.add(Calendar.MINUTE, 15);       //Setting Maximum Duration upto 15.
            calendarMax.add(Calendar.DATE, 30);          //Setting Maximum Date upto 30.
            newTime = df.format(calendarMax.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Date maxDate = calendarMax.getTime();

        now = Calendar.getInstance().getTime();

        String input_date = dateToSelect.toString();
        String new_date = newTime;
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = df.parse(input_date);
            dt2 = df.parse(new_date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat format2 = new SimpleDateFormat("EEE, MMM dd 'at' hh:mm a", Locale.ENGLISH); //Getting the date format from normal present time
        finalDay = format2.format(dt1);

        DateFormat format4 = new SimpleDateFormat("HH:mm", Locale.ENGLISH); //Getting normal time with seconds
        String presenttime = format4.format(dt1);

        DateFormat format3 = new SimpleDateFormat("hh:mm a", Locale.ENGLISH); //Getting the time format from 15 mins time
        String time_15 = format3.format(dt2);

        time = time_15;

        DateFormat format5 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String ScheduleRidedate = format5.format(dt1);

        String ans = finalDay + " - " + time;
        scheduleridetext.setText(ans);
        sessionManager.setScheduleDateTime(ans);
        sessionManager.setScheduleDate(ScheduleRidedate);
        sessionManager.setPresentTime(presenttime);
        return maxDate;
    }


    public void showLatLng(Double lat, Double lng, Double distance) {

        if (lat <= 0) {
           /* twoDForm = new DecimalFormat("#.###");
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setDecimalSeparator('.');
            twoDForm.setDecimalFormatSymbols(dfs);*/
            //Double.parseDouble(twoDForm.format(distance));
            textView1.append(" + " + distance);
        } else {
            count1++;
            String text = String.valueOf(lat + " -- " + lng);
            textView1.append("\n" + count1 + " * " + text);
        }
    }


    public void scheduleMethod() {
        /**
         * Schedule a ride wheel picker
         */


        Date mindate = Calendar.getInstance().getTime();           //Getting Today's date
        mindate.setMinutes(mindate.getMinutes() + 15);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mindate);


        singleDateAndTimePicker.setDefaultDate(mindate);
        singleDateAndTimePicker.selectDate(calendar);
        singleDateAndTimePicker.setDisplayDays(true);
        singleDateAndTimePicker.setIsAmPm(true);
        singleDateAndTimePicker.setMustBeOnFuture(true);
        singleDateAndTimePicker.mustBeOnFuture();                               //Setting date only to future
        singleDateAndTimePicker.setMinDate(mindate);                           // MIn date ie-Today date
        singleDateAndTimePicker.setMaxDate(scheduleRideDate(mindate));        //Max date upto 30 dates
        singleDateAndTimePicker.setStepMinutes(1);                           //Setting minute intervals to 1min
        singleDateAndTimePicker.toString();
        singleDateAndTimePicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(String displayed, Date date) {
                /**
                 * When scrolling the picker
                 */


                scheduleRideDate(date);

            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            ANDROID_HTTP_CLIENT.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void permissionGranted(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        switch (requestCodeForCallbackIdentificationCode) {
            case RuntimePermissionDialogFragment.locationCallbackCode: {
                if (requestCodeForCallbackIdentificationCodeSubDivision == 0) {
                    buildGoogleApiClient();
                    displayLocationSettingsRequest();
                    break;
                } else if (requestCodeForCallbackIdentificationCodeSubDivision == 1) {
                    if (!AppUtils.isLocationEnabled(mContext)) {
                        checkGPSEnable();  // Check GPS Enable or not
                    } else {

                        if (latLong != null) {
                            fetchAddress(latLong, "pickupaddress"); // Fetch address
                        }
                        commonMethods.showMessage(MainActivity.this, dialog, getResources().getString(R.string.gettinglocate));
                    }
                }

            }

            default: {
                break;
            }


        }
    }

    @Override
    public void permissionDenied(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {

    }

    public void gotoPaymentpage(int type) {
        if (commonMethods.isOnline(this)) {
            Intent intent = new Intent(getApplicationContext(), PaymentPage.class);
            intent.putExtra(CommonKeys.TYPE_INTENT_ARGUMENT_KEY, type);
            startActivityForResult(intent, CommonKeys.ChangePaymentOpetionBeforeRequestCarApi);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        } else {
            CommonMethods.showUserMessage(getResources().getString(R.string.turnoninternet));
        }
    }
}
/*
 * Wiper
 * wiper water
 * clutch noise
 * engine oil
 * body rust paste
 * door oil and door smooth close
 * low beam light
 * tyre alignment
 * front tyre quality
 * Resuce noise while vechicle jumping
 * back speaker complete pad change
 *
 * */