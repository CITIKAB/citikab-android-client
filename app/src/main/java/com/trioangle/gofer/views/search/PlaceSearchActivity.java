package com.trioangle.gofer.views.search;
/**
 * @package com.trioangle.gofer
 * @subpackage search
 * @category PlaceSearchActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveCanceledListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.helper.Permission;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.placesearch.PlacesAutoCompleteAdapter;
import com.trioangle.gofer.sidebar.AddHome;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.trioangle.gofer.utils.CommonMethods.*;

/* ***************************************************************
    Search pickup and drop location for send request and Add home and work address
   *************************************************************** */
public class PlaceSearchActivity extends AppCompatActivity implements View.OnFocusChangeListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnCameraMoveStartedListener, OnCameraMoveListener, OnCameraMoveCanceledListener, OnCameraIdleListener, GoogleMapPlaceSearchAutoCompleteRecyclerView.AutoCompleteAddressTouchListener {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));
    private static String TAG = "MAP LOCATION";
    public final AndroidHttpClient ANDROID_HTTP_CLIENT = AndroidHttpClient.newInstance(PlaceSearchActivity.class.getName());
    public AlertDialog dialog;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    CustomDialog customDialog;
    public List<Address> addressList = null; // Addresslist
    public String address = null; // Address
    public String countrys = null; // Country name
    public LatLng getLocations = null;
    public String getAddress = null;
    public @InjectView(R.id.destadddress)
    EditText destadddress;
    public @InjectView(R.id.pickupaddress)
    EditText pickupaddress;
    public @InjectView(R.id.dest_point)
    ImageView dest_point;
    public @InjectView(R.id.pickup_point)
    ImageView pickup_point;
    public @InjectView(R.id.pin_map)
    ImageView pin_map;
    public @InjectView(R.id.drop_done)
    TextView drop_done;
    public @InjectView(R.id.hometext)
    TextView hometext;
    public @InjectView(R.id.homeaddress)
    TextView homeaddress;
    public @InjectView(R.id.worktext)
    TextView worktext;
    public @InjectView(R.id.workaddress)
    TextView workaddress;
    public @InjectView(R.id.setlocaion_onmap)
    LinearLayout setlocaion_onmap;
    public @InjectView(R.id.homelayoyt)
    LinearLayout homelayoyt;
    public @InjectView(R.id.worklayout)
    LinearLayout worklayout;
    public @InjectView(R.id.location_placesearch)
    RecyclerView mRecyclerView;
    public @InjectView(R.id.address_search)
    ScrollView address_search;
    public @InjectView(R.id.schedule_date_time)
    TextView schedule_date_time;

    public @InjectView(R.id.pb_address_searchbar_loading)
    ProgressBar pbAddressSearchbarLoading;
    public @InjectView(R.id.scheduleride_layout)
    RelativeLayout scheduleride_layout;
    public SupportMapFragment mapFragment;
    public String Schedule_ride;
    public String date;
    public int counti = 0;
    public String lat;
    public String log;

    //scheduleride_layout
    public String homegettext;
    public String workgettext;
    public String searchlocation;
    public LatLng objLatLng;
    public GoogleMap googleMap;
    public String Listlat;
    public String Listlong;
    public boolean isPickup = false;
    public String pickipCountry;
    public String destCountry;
    public ArrayList markerPoints = new ArrayList();
    public String oldstring = "";
    public LatLng pickuplatlng;
    public boolean isFirst = true;
    protected GoogleApiClient mGoogleApiClient;
    protected boolean isInternetAvailable;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    PlacesClient placesClient;
    AutocompleteSessionToken googleAutoCompleteToken;
    GoogleMapPlaceSearchAutoCompleteRecyclerView googleMapPlaceSearchAutoCompleteRecyclerView;

    private List<AutocompletePrediction>addressAutoCompletePredictions = new ArrayList<>() ;

    @OnClick(R.id.searchback)
    public void back() {
        onBackPressed();
    }

    @OnClick(R.id.arrow)
    public void arrow() {
        onBackPressed();
    }

    @OnClick(R.id.pickupclose)
    public void pickupclose() {
        LatLng ll = new LatLng(1, 1);
        markerPoints.set(0, ll);
        isPickup = true;
        pickupaddress.setText("");
        pickupaddress.requestFocus();
        drop_done.setVisibility(View.GONE);
    }

    @OnClick(R.id.destclose)
    public void destclose() {
        LatLng ll = new LatLng(1, 1);
        markerPoints.set(1, ll);
        destadddress.setText("");
        destadddress();
    }

    @OnClick(R.id.destadddress)
    public void destadddress() {
        destadddress.requestFocus();
        drop_done.setVisibility(View.GONE);
        destAddsFocus();
    }

    public void destAddsFocus() {

        isPickup = false;
        pickupaddress.removeTextChangedListener(new NameTextWatcher(pickupaddress));
        destadddress.addTextChangedListener(new NameTextWatcher(destadddress));

        mRecyclerView.setVisibility(View.GONE);
        drop_done.setVisibility(View.GONE);
        address_search.setVisibility(View.VISIBLE);
        mapFragment.getView().setVisibility(View.GONE);
        pin_map.setVisibility(View.GONE);
        // code to execute when EditText loses focus
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pickup_point.setBackgroundTintList(getResources().getColorStateList(R.color.pickup_dest_point_color));
            dest_point.setBackgroundTintList(getResources().getColorStateList(R.color.ub__black));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();  // Connect Google API client
        setContentView(R.layout.activity_place_search);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

        showPermissionDialog();  // Location permission dialog
        LatLng ll = new LatLng(1, 1);

        objLatLng = getIntent().getExtras().getParcelable("Latlng");
        pickipCountry = getIntent().getStringExtra("Country");
        date = getIntent().getStringExtra("date");
        placesClient = com.google.android.libraries.places.api.Places.createClient(this);
        googleAutoCompleteToken = AutocompleteSessionToken.newInstance();
        googleMapPlaceSearchAutoCompleteRecyclerView = new GoogleMapPlaceSearchAutoCompleteRecyclerView(addressAutoCompletePredictions, this, this);
        try {

            homegettext = getIntent().getStringExtra("Home");
            workgettext = getIntent().getStringExtra("Work");
            Schedule_ride = getIntent().getStringExtra("Schedule");

            if (homegettext == null) {
                homegettext = "";
            }
            if (workgettext == null) {
                workgettext = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            homegettext = "";
            workgettext = "";
        }
        if (null!=getIntent().getStringExtra("Schedule"))
            if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
                scheduleride_layout.setVisibility(View.VISIBLE);
                schedule_date_time.setText(date);
            } else {
                scheduleride_layout.setVisibility(View.GONE);
            }


        pickuplatlng = new LatLng(objLatLng.latitude, objLatLng.longitude);
        markerPoints.add(0, pickuplatlng);
        markerPoints.add(1, ll);


        DebuggableLogD("OUTPUT IS", homegettext);
        DebuggableLogD("OUTPUT IS", workgettext);
        if (homegettext != null) {
            homeaddress.setText(homegettext);
        } else if (workgettext != null) {
            workaddress.setText(workgettext);
        }
        if ("".equals(homegettext)) {
            hometext.setText(getString(R.string.addhome));
        }
        if ("".equals(workgettext)) {
            worktext.setText(getString(R.string.addwork));
        }

        if (homegettext != null && workgettext != null) {
            homeaddress.setText(homegettext);
            workaddress.setText(workgettext);
            homelayoyt.setVisibility(View.VISIBLE);
            worklayout.setVisibility(View.VISIBLE);
        }

        // Home address layout click
        homelayoyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInternetAvailable) {
                    if (homegettext != null && !homegettext.equals("")) {

                        fetchLocation(homegettext, "homeclick");
                    } else {
                        Intent intent = new Intent(getApplicationContext(), AddHome.class);
                        intent.putExtra("searchhome", "searchhome");
                        intent.putExtra("searchwork", "");
                        intent.putExtra("Schedule", getIntent().getStringExtra("Schedule"));
                        intent.putExtra("Latlng", objLatLng);
                        intent.putExtra("Country", pickipCountry);
                        intent.putExtra("Home", homegettext);
                        intent.putExtra("Work", workgettext);
                        intent.putExtra("PickupAddress", getIntent().getStringExtra("PickupAddress"));
                        intent.putExtra("DropAddress", getIntent().getStringExtra("DropAddress"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);

                    }
                } else {
                    commonMethods.showMessage(PlaceSearchActivity.this, dialog, getString(R.string.no_connection));
                }
            }

        });

        // Work address layout click
        worklayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isInternetAvailable) {

                    if (workgettext != null && !workgettext.equals("")) {
                        fetchLocation(workgettext, "workclick");
                    } else {
                        Intent intent = new Intent(getApplicationContext(), AddHome.class);
                        intent.putExtra("searchhome", "");
                        intent.putExtra("searchwork", "searchwork");
                        intent.putExtra("Schedule", getIntent().getStringExtra("Schedule"));
                        intent.putExtra("Latlng", objLatLng);
                        intent.putExtra("Country", pickipCountry);
                        intent.putExtra("Home", homegettext);
                        intent.putExtra("Work", workgettext);
                        intent.putExtra("PickupAddress", getIntent().getStringExtra("PickupAddress"));
                        intent.putExtra("DropAddress", getIntent().getStringExtra("DropAddress"));
                        startActivity(intent);
                        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                    }

                } else {
                    commonMethods.showMessage(PlaceSearchActivity.this, dialog, getString(R.string.no_connection));
                }
            }
        });


        pickupaddress.setText(getIntent().getStringExtra("PickupAddress"));
        destadddress.setText(getIntent().getStringExtra("DropAddress"));


        destadddress.addTextChangedListener(new NameTextWatcher(destadddress));


        if (getIntent().getSerializableExtra("PickupDrop") != null) {
            markerPoints = (ArrayList) getIntent().getSerializableExtra("PickupDrop");
            fetchAddress((LatLng) markerPoints.get(0), "country");  // Fetch Address using if Geocode or Google
        }

        // Done button for map address selection
        drop_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isInternetAvailable = commonMethods.isOnline(getApplicationContext());
                if (!isInternetAvailable) {
                    commonMethods.showMessage(PlaceSearchActivity.this, dialog, getString(R.string.no_connection));
                }

                String pickup = pickupaddress.getText().toString();
                String destup = destadddress.getText().toString();



                if (pickup.matches("") || destup.matches("")) {
                    address_search.setVisibility(View.VISIBLE);
                    mapFragment.getView().setVisibility(View.GONE);
                    pin_map.setVisibility(View.GONE);
                    drop_done.setVisibility(View.GONE);
                    if (pickup.matches("")) {
                        pickupaddress.requestFocus();
                    }

                    if (destup.matches("")) {
                        destadddress.requestFocus();
                    }

                } else {



                    if (destCountry.equals(pickipCountry)) {


                        if (pickupaddress.getText().toString().equals(destadddress.getText().toString())) {

                            LatLng l1 = (LatLng) markerPoints.get(0);
                            LatLng l2 = (LatLng) markerPoints.get(1);
                            if (distance(l1.latitude, l1.longitude, l2.latitude, l2.longitude) < 0.1) {
                                commonMethods.showMessage(PlaceSearchActivity.this, dialog, "Pickup and Drop location is same...");
                            } else {

                                sessionManager.setIsrequest(true);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("PickupDrop", markerPoints);

                                if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
                                    intent.putExtra("Schedule", "Schedule");
                                    intent.putExtra("date", date);
                                } else {
                                    intent.putExtra("Schedule", "");
                                }

                                startActivity(intent);
                            }
                        } else {

                            sessionManager.setIsrequest(true);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("PickupDrop", markerPoints);

                            if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
                                intent.putExtra("Schedule", "Schedule");
                                intent.putExtra("date", date);
                            } else {
                                intent.putExtra("Schedule", "");
                            }
                            startActivity(intent);
                        }


                    } else {
                        commonMethods.showMessage(PlaceSearchActivity.this, dialog, getString(R.string.service_not_available));
                    }
                }
            }
        });


        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        }

        // Destination address clear


        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }


        setlocaion_onmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pickupaddress.getWindowToken(), 0);
                address_search.setVisibility(View.GONE);
                mapFragment.getView().setVisibility(View.VISIBLE);
                pin_map.setVisibility(View.VISIBLE);
            }
        });


        // Check pickup address foucus
        pickupaddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // pickupaddress.setText("");
                    destadddress.removeTextChangedListener(new NameTextWatcher(destadddress));
                    pickupaddress.addTextChangedListener(new NameTextWatcher(pickupaddress));
                    mRecyclerView.setVisibility(View.GONE);
                    drop_done.setVisibility(View.GONE);
                    pickupaddress.setTextColor(getResources().getColor(R.color.text_black));

                    isPickup = true;
                    address_search.setVisibility(View.VISIBLE);
                    mapFragment.getView().setVisibility(View.GONE);
                    pin_map.setVisibility(View.GONE);
                    // code to execute when EditText loses focus
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        pickup_point.setBackgroundTintList(getResources().getColorStateList(R.color.ub__black));
                        dest_point.setBackgroundTintList(getResources().getColorStateList(R.color.cardview_shadow_start_color));
                    }

                }
            }
        });

        // Check dest address focus
        destadddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    destAddsFocus();
                }
            }
        });


        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, R.layout.location_search,
                mGoogleApiClient, BOUNDS_INDIA, null,mRecyclerView);

        // Place search list
        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(googleMapPlaceSearchAutoCompleteRecyclerView);

        // Place search list click
        /*mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
                        if (counti > 0) {
                            item = null;
                        }
                        if (item != null) {
                            counti++;
                            if (!(PlaceSearchActivity.this).isFinishing()) {
                                commonMethods.showProgressDialog(PlaceSearchActivity.this, customDialog);
                            }


                            DebuggableLogI("TAG", "Autocomplete item selected: " + item.description);

                            searchlocation = (String) item.description;
                            if (isPickup) {
                                pickupaddress.setText(item.addresss);

                                oldstring = item.addresss.toString();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(pickupaddress.getWindowToken(), 0);
                                mRecyclerView.setVisibility(View.GONE);
                            } else {
                                destadddress.setText(item.addresss);
                                oldstring = item.addresss.toString();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(destadddress.getWindowToken(), 0);
                            }
                            mRecyclerView.setVisibility(View.GONE);

                            fetchLocation(searchlocation, "searchitemclick");

                            DebuggableLogI("TAG", "Clicked: " + item.description);
                            DebuggableLogI("TAG", "Called getPlaceById to get Place details for " + item.placeId);
                        }


                    }
                })
        );*/


    }



    /**
     * Connect Google APi Client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(com.google.android.gms.location.places.Places.GEO_DATA_API)
                .build();
    }

    /**
     * Google APi on Connected
     */
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            changeMap(mLastLocation);
            DebuggableLogD(TAG, "ON connected");

        }

    }

    /**
     * Google API suspended
     */
    @Override
    public void onConnectionSuspended(int i) {
        DebuggableLogI(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Google API connection failed
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        DebuggableLogD(TAG, "onConnectionFailed");

    }

    /**
     * Move map to current location and get address while move amp
     */
    private void changeMap(Location location) {

        DebuggableLogD(TAG, "Reaching map" + googleMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(16.5f).tilt(0).build();

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            // startIntentService(location);

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.sorry_unable_create_map), Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            DebuggableLogV("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
        initilizeMap();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            DebuggableLogV("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                PlaceSearchActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        PlaceSearchActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.search_map);
            mapFragment.getMapAsync(this);
            mapFragment.getView().setVisibility(View.GONE);
            pin_map.setVisibility(View.GONE);

        }
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        googleMap = Map;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.ub__map_style));

            if (!success) {
                DebuggableLogE("Gofer", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            DebuggableLogE("Gofer", "Can't find style. Error: ", e);
        }

        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraMoveCanceledListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.equals(R.id.pickupaddress)) {
            if (hasFocus) {
                isPickup = true;
                pickup_point.setBackgroundTintList(getResources().getColorStateList(R.color.ub__black));
                dest_point.setBackgroundTintList(getResources().getColorStateList(R.color.pickup_dest_point_color));
            } else {
                isPickup = false;
                pickup_point.setBackgroundTintList(getResources().getColorStateList(R.color.pickup_dest_point_color));
                dest_point.setBackgroundTintList(getResources().getColorStateList(R.color.ub__black));
            }
        } else {
            if (!hasFocus) {
                isPickup = true;
                pickup_point.setBackgroundTintList(getResources().getColorStateList(R.color.ub__black));
                dest_point.setBackgroundTintList(getResources().getColorStateList(R.color.pickup_dest_point_color));
            } else {
                isPickup = false;
                pickup_point.setBackgroundTintList(getResources().getColorStateList(R.color.pickup_dest_point_color));
                dest_point.setBackgroundTintList(getResources().getColorStateList(R.color.ub__black));
            }
        }
    }

    /**
     * Show GPS permission Dialog
     */
    private void showPermissionDialog() {
        if (!Permission.checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    4);
        }
    }

    /**
     * Map Camera move listener
     */
    @Override
    public void onCameraMoveStarted(int reason) {

       /* if (reason == OnCameraMoveStartedListener.REASON_GESTURE) {
            Toast.makeText(this, "The user gestured on the map.",
                    Toast.LENGTH_SHORT).show();
        } else if (reason == OnCameraMoveStartedListener
                .REASON_API_ANIMATION) {
            Toast.makeText(this, "The user tapped something on the map.",
                    Toast.LENGTH_SHORT).show();
        } else if (reason == OnCameraMoveStartedListener
                .REASON_DEVELOPER_ANIMATION) {
            Toast.makeText(this, "The app moved the camera.",
                    Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onCameraMove() {
        //Toast.makeText(this, "The camera is moving.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraMoveCanceled() {
        // Toast.makeText(this, "Camera movement canceled.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraIdle() {
        //Toast.makeText(this, "The camera has stopped moving.",Toast.LENGTH_SHORT).show();
        drop_done.setVisibility(View.VISIBLE);
        CameraPosition cameraPosition = googleMap.getCameraPosition();

        if (!isFirst) {

            Listlat = String.valueOf(cameraPosition.target.latitude);
            Listlong = String.valueOf(cameraPosition.target.longitude);
            DebuggableLogI("centerLat", String.valueOf(cameraPosition.target.latitude));
            DebuggableLogI("centerLong", String.valueOf(cameraPosition.target.longitude));
            LatLng cur_Latlng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);

            if (isPickup) {

                markerPoints.set(0, cur_Latlng);

            } else {

                markerPoints.set(1, cur_Latlng);

            }
            fetchAddress(cur_Latlng, "settext");// Fetch address from latitude and longitude
        } else {
            isFirst = false;
        }
    }

    /**
     * calculates the distance between two locations in MILES
     */
    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
    }

    /**
     * Fetch Location from address if Geocode available get from geocode otherwise get location from google
     */
    public void fetchLocation(String addresss, final String type) {
        getAddress = addresss;

        new AsyncTask<Void, Void, String>() {
            String locations = null;


            @Override
            protected String doInBackground(Void... params) {

                if (Geocoder.isPresent()) // Check geo code available or not
                {
                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> address;

                        // May throw an IOException
                        address = geocoder.getFromLocationName(getAddress, 5);
                        if (address == null) {
                            return null;
                        }
                        Address location = address.get(0);

                        countrys = address.get(0).getCountryName();

                        if (isPickup) {
                            if (!"".equals(countrys))
                                pickipCountry = address.get(0).getCountryName();

                        } else {
                            destCountry = address.get(0).getCountryName();

                            if ("".equals(destCountry))
                                destCountry = pickipCountry;

                        }
                        location.getLatitude();
                        location.getLongitude();

                        lat = String.valueOf(location.getLatitude());
                        log = String.valueOf(location.getLongitude());
                        locations = lat + "," + log;
                    } catch (Exception ignored) {
                        // after a while, Geocoder start to throw "Service not availalbe" exception. really weird since it was working before (same device, same Android version etc..
                    }
                }

                if (locations != null) // i.e., Geocoder succeed
                {
                    return locations;
                } else // i.e., Geocoder failed
                {
                    return fetchLocationUsingGoogleMap(); // If geocode not available or location null call google API
                }
            }

            // Geocoder failed :-(
            // Our B Plan : Google Map
            private String fetchLocationUsingGoogleMap() {
                getAddress = getAddress.replaceAll(" ", "%20");
                String googleMapUrl = "https://maps.google.com/maps/api/geocode/json?address=" + getAddress + "&sensor=false"
                        +"&key=" + getString(R.string.google_maps_key);

                try {
                    JSONObject googleMapResponse = new JSONObject(ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
                            new BasicResponseHandler()));

                    // many nested loops.. not great -> use expression instead
                    // loop among all results

                    if (googleMapResponse.length() > 0) {
                        String longitute = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getString("lng");

                        String latitude = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getString("lat");

                        int len = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONArray("address_components").length();
                        for (int i = 0; i < len; i++) {
                            if (((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0).equals("country")) {
                                countrys = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                        .getJSONArray("address_components").getJSONObject(i).getString("long_name");

                            }
                        }

                        if (isPickup) {
                            if (!"".equals(countrys))
                                pickipCountry = countrys;

                        } else {
                            destCountry = countrys;

                        }
                        return latitude + "," + longitute;
                    } else {
                        return null;
                    }

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String location) {
                if (location != null) {
                    String[] parts = location.split(",");
                    Double lat = Double.valueOf(parts[0]);
                    Double lng = Double.valueOf(parts[1]);
                    LatLng latLng = new LatLng(lat, lng);

                    if ("homeclick".equals(type)) {
                        if (isPickup) {
                            markerPoints.set(0, latLng);
                            pickupaddress.setText(getAddress);
                            homeClick();

                        } else {
                            markerPoints.set(1, latLng);
                            destadddress.setText(getAddress);
                            homeClick();
                        }
                    } else if ("workclick".equals(type)) {
                        if (isPickup) {
                            markerPoints.set(0, latLng);
                            pickupaddress.setText(getAddress);
                            workClick();

                        } else {
                            markerPoints.set(1, latLng);
                            destadddress.setText(getAddress);
                            workClick();
                        }

                    } else if ("searchitemclick".equals(type)) {
                        if ("".equals(countrys))
                            countrys = pickipCountry;

                        searchItemClick(latLng, countrys);
                    }
                } else {
                    commonMethods.showMessage(PlaceSearchActivity.this, dialog, "Unable to get location please try again...");
                    commonMethods.hideProgressDialog();
                }
            }

            ;
        }.execute();
    }

    /**
     * Fetch address from location if Geocode available get from geocode otherwise get location from google
     */
    public void fetchAddress(LatLng location, final String type) {
        getLocations = location;
        address = null;
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                if (Geocoder.isPresent()) // Check Geo code available or not
                {
                    System.out.print("Geo OK Ad");
                    try {

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(getLocations.latitude, getLocations.longitude, 1);
                        if (addresses != null) {
                            countrys = addresses.get(0).getCountryName();

                            String adress0 = addresses.get(0).getAddressLine(0);
                            String adress1 = addresses.get(0).getAddressLine(1);

                            address = adress0 + " " + adress1; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


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
                String googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + getLocations.latitude + ","
                        + getLocations.longitude + "&sensor=false"+"&key=" + sessionManager.getGoogleMapKey();

                try {
                    JSONObject googleMapResponse = new JSONObject(ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
                            new BasicResponseHandler()));

                    // many nested loops.. not great -> use expression instead
                    // loop among all results

                    JSONArray results = (JSONArray) googleMapResponse.get("results");
                    for (int i = 0; i < results.length(); i++) {


                        JSONObject result = results.getJSONObject(i);


                        String indiStr = result.getString("formatted_address");


                        Address addr = new Address(Locale.getDefault());


                        addr.setAddressLine(0, indiStr);
                        //  country=addr.getCountryName();

                        addressList.add(addr);


                    }

                    int len = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                            .getJSONArray("address_components").length();
                    for (int i = 0; i < len; i++) {
                        if (((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0).equals("country")) {
                            countrys = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(i).getString("long_name");

                        }
                    }

                    if (addressList != null) {

                        String adress0 = addressList.get(0).getAddressLine(0);
                        address = adress0;//+" "+adress1;
                        //address = adress0+" "+adress1; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
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
                if (address != null) {
                    if ("settext".equals(type)) {
                        //destCountry=countrys;
                        if (isPickup) {
                            pickupaddress.setText(address);
                            pickipCountry = countrys;
                        } else {

                            destadddress.setText(address);
                            destCountry = countrys;
                        }
                    } else if ("country".equals(type)) {
                        destCountry = countrys;
                    }
                }


            }

            ;
        }.execute();
    }

    /**
     * Home Address click
     */
    public void homeClick() {
        String pickup = pickupaddress.getText().toString();
        String destup = destadddress.getText().toString();


        if ("".matches(pickup) || "".matches(destup)) {
            if (isPickup) {
                pickupaddress.removeTextChangedListener(new NameTextWatcher(pickupaddress));
                destadddress.requestFocus();
            } else {
                destadddress.removeTextChangedListener(new NameTextWatcher(destadddress));
                pickupaddress.requestFocus();
            }
        } else {


            //  if (destCountry.equals(pickipCountry)) {
            LatLng l1 = (LatLng) markerPoints.get(0);
            LatLng l2 = (LatLng) markerPoints.get(1);
            if (distance(l1.latitude, l1.longitude, l2.latitude, l2.longitude) < 0.1) {
                commonMethods.showMessage(PlaceSearchActivity.this, dialog, "Pickup and Drop location is same...");
            } else {
                sessionManager.setIsrequest(true);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (null!=getIntent().getStringExtra("Schedule"))
                    if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
                        intent.putExtra("Schedule", "Schedule");
                    } else {
                        intent.putExtra("Schedule", "");
                    }
                intent.putExtra("PickupDrop", markerPoints);
                startActivity(intent);
            }
        }
    }

    /**
     * Work address click
     */
    public void workClick() {
        mRecyclerView.setVisibility(View.GONE);
        String pickup = pickupaddress.getText().toString();
        String destup = destadddress.getText().toString();
        if (pickup.matches("") || destup.matches("")) {
            if (isPickup) {
                pickupaddress.removeTextChangedListener(new NameTextWatcher(pickupaddress));
                destadddress.requestFocus();
            } else {
                destadddress.removeTextChangedListener(new NameTextWatcher(destadddress));
                pickupaddress.requestFocus();
            }
        } else {
            LatLng l1 = (LatLng) markerPoints.get(0);
            LatLng l2 = (LatLng) markerPoints.get(1);
            // if (destCountry.equals(pickipCountry)) {
            if (distance(l1.latitude, l1.longitude, l2.latitude, l2.longitude) < 0.1) {
                commonMethods.showMessage(PlaceSearchActivity.this, dialog, "Pickup and Drop location is same...");
            } else {

                sessionManager.setIsrequest(true);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (null!=getIntent().getStringExtra("Schedule"))
                    if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
                        intent.putExtra("Schedule", "Schedule");
                    } else {
                        intent.putExtra("Schedule", "");
                    }
                intent.putExtra("PickupDrop", markerPoints);
                startActivity(intent);
            }
        }

    }

    /**
     * Place search item click
     */
    public void searchItemClick(LatLng cur_Latlng, String country) {
        final LatLng ll = new LatLng(1, 1);
        destCountry = country;
        Listlat = String.valueOf(cur_Latlng.latitude);
        Listlong = String.valueOf(cur_Latlng.longitude);
        // destCountry = getCountry(cur_Latlng.latitude, cur_Latlng.longitude);
        if (isPickup) {
            if (!"".equals(destCountry))
                pickipCountry = destCountry;
            markerPoints.set(0, cur_Latlng);

            if (!markerPoints.get(1).equals(ll)) {

                if (destCountry.equals(pickipCountry)) {

                    if (pickupaddress.getText().toString().trim().length() == 0 || destadddress.getText().toString().trim().length() == 0) {
                        commonMethods.hideProgressDialog();
                    } else {
                        LatLng l1 = (LatLng) markerPoints.get(0);
                        LatLng l2 = (LatLng) markerPoints.get(1);
                        if (distance(l1.latitude, l1.longitude, l2.latitude, l2.longitude) < 0.1) {
                            commonMethods.showMessage(PlaceSearchActivity.this, dialog, "Pickup and Drop location is same...");
                        } else {
                            sessionManager.setIsrequest(true);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
                                intent.putExtra("Schedule", "Schedule");
                                intent.putExtra("date", date);
                            } else {
                                intent.putExtra("Schedule", "");
                            }
                            intent.putExtra("PickupDrop", markerPoints);
                            startActivity(intent);
                        }


                    }
                } else {

                    commonMethods.showMessage(PlaceSearchActivity.this, dialog, getString(R.string.service_not_available));

                }
            } else {

                destadddress.requestFocus();
                // destadddress.setText("");

            }

        } else {
            // destCountry=country;
            markerPoints.set(1, cur_Latlng);








            if (destCountry.equals(pickipCountry)) {
                if (pickupaddress.getText().toString().trim().length() == 0 || destadddress.getText().toString().trim().length() == 0) {
                    commonMethods.hideProgressDialog();

                }
                if (pickupaddress.getText().toString().equals(destadddress.getText().toString())) {
                    LatLng l1 = (LatLng) markerPoints.get(0);
                    LatLng l2 = (LatLng) markerPoints.get(1);
                    if (distance(l1.latitude, l1.longitude, l2.latitude, l2.longitude) < 0.1) {
                        commonMethods.showMessage(PlaceSearchActivity.this, dialog, "Pickup and Drop location is same...");
                    }
                } else {
                    sessionManager.setIsrequest(true);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    if (getIntent().getStringExtra("Schedule").equals("Schedule")) {
                        intent.putExtra("Schedule", "Schedule");
                        intent.putExtra("date", date);
                    } else {
                        intent.putExtra("Schedule", "");
                    }
                    intent.putExtra("PickupDrop", markerPoints);
                    startActivity(intent);
                }
            } else {
                commonMethods.showMessage(PlaceSearchActivity.this, dialog, getString(R.string.servicenotavailable));
            }
        }

        commonMethods.hideProgressDialog();
        counti = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            ANDROID_HTTP_CLIENT.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void selectedAddress(AutocompletePrediction autocompletePrediction) {

        if (counti > 0) {
            autocompletePrediction = null;
        }
        if (autocompletePrediction != null) {
            counti++;
            if (!(PlaceSearchActivity.this).isFinishing()) {
                commonMethods.showProgressDialog(PlaceSearchActivity.this, customDialog);
            }


            DebuggableLogI("TAG", "Autocomplete item selected: " + autocompletePrediction.getFullText(null));

            searchlocation = autocompletePrediction.getFullText(null).toString();
            if (isPickup) {
                pickupaddress.setText(autocompletePrediction.getPrimaryText(null));

                oldstring = autocompletePrediction.getPrimaryText(null).toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pickupaddress.getWindowToken(), 0);
                mRecyclerView.setVisibility(View.GONE);
            } else {
                destadddress.setText(autocompletePrediction.getPrimaryText(null));
                oldstring = autocompletePrediction.getPrimaryText(null).toString();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(destadddress.getWindowToken(), 0);
            }
            mRecyclerView.setVisibility(View.GONE);

            fetchLocation(searchlocation, "searchitemclick");

            DebuggableLogI("TAG", "Clicked: " + autocompletePrediction.getPrimaryText(null));
            DebuggableLogI("TAG", "Called getPlaceById to get Place details for " + autocompletePrediction.getPlaceId());
        }
    }

    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
            if (s.toString().equals("")) {
                mRecyclerView.setVisibility(View.GONE);
            }else{
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }

        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if (s.toString().equals("")) {
                mRecyclerView.setVisibility(View.GONE);
            }else{
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }

        public void afterTextChanged(final Editable s) {
            isInternetAvailable = commonMethods.isOnline(getApplicationContext());
            if (!isInternetAvailable) {
                commonMethods.showMessage(PlaceSearchActivity.this, dialog, getString(R.string.no_connection));
            }

            if (view.hasFocus()) {
                if (!s.toString().equals("")) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!oldstring.equals(s.toString())) {
                                oldstring = s.toString();
                                //mAutoCompleteAdapter.getFilter().filter(s.toString()); // Place search
                                showAddressSearchProgressbar();
                                getFullAddressUsingEdittextStringFromGooglePlaceSearchAPI(s.toString());
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 1000);

                } else  {
                    clearAddressAndHideRecyclerView();
                }

               /* if (s.toString().equals("")) {
                    googleMapPlaceSearchAutoCompleteRecyclerView.clearAddresses();
                    mRecyclerView.setVisibility(View.GONE);
                }*/
            } else {
                clearAddressAndHideRecyclerView();
            }

        }
    }

    public void getFullAddressUsingEdittextStringFromGooglePlaceSearchAPI(String queryAddress){

        //RectangularBounds bounds = RectangularBounds.newInstance(first, sec);
        FindAutocompletePredictionsRequest request =
                FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(googleAutoCompleteToken)
                        .setQuery(queryAddress)
                        .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(
                        (response) -> {
                            /*Toast toast = Toast.makeText(this, "address auto comp succ" + response.getAutocompletePredictions().size(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
                            toast.show();*/
                            hideAddressSearchProgressbar();
                            if(response.getAutocompletePredictions().size()>0){
                                googleMapPlaceSearchAutoCompleteRecyclerView.updateList(response.getAutocompletePredictions());
                            }else{
                                clearAddressAndHideRecyclerView();
                                //showUserMessage(getResources().getString(R.string.no_address_found));
                            }


                        })
                .addOnFailureListener((exception) -> {
                    hideAddressSearchProgressbar();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        DebuggableLogE(TAG, "Place not found: " + apiException.getStatusCode());
                        //CommonMethods.showUserMessage("place not found "+ apiException.getStatusCode());
                         /*Toast toast = Toast.makeText(this, "address error "+apiException.getStatusCode(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
                            toast.show();*/
                    }
                    exception.printStackTrace();
                    /*Toast toast = Toast.makeText(this, "address error ", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
                    toast.show();*/
                });
    }
    public void hideAddressSearchProgressbar(){
        pbAddressSearchbarLoading.setVisibility(View.INVISIBLE);
    }
    public void showAddressSearchProgressbar(){
        pbAddressSearchbarLoading.setVisibility(View.VISIBLE);
    }

    public void clearAddressAndHideRecyclerView(){
        googleMapPlaceSearchAutoCompleteRecyclerView.clearAddresses();
        mRecyclerView.setVisibility(View.GONE);
    }
}
