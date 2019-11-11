package com.trioangle.gofer.sidebar;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar
 * @category AddHome
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
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
import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.helper.Constants;
import com.trioangle.gofer.helper.Permission;
import com.trioangle.gofer.helper.ReturnValues;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.placesearch.PlacesAutoCompleteAdapter;
import com.trioangle.gofer.placesearch.RecyclerItemClickListener;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.search.GoogleMapPlaceSearchAutoCompleteRecyclerView;
import com.trioangle.gofer.views.search.PlaceSearchActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogD;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.Enums.REQ_UPDATE_LOCATION;

/* ************************************************************
   Update rider home and work location while search nearest car
    *********************************************************** */
public class AddHome extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnCameraMoveStartedListener,
        OnCameraMoveListener,
        OnCameraMoveCanceledListener,
        OnCameraIdleListener, ServiceListener,GoogleMapPlaceSearchAutoCompleteRecyclerView.AutoCompleteAddressTouchListener {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));
    private static String TAG = "MAP LOCATION";

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

    public SupportMapFragment mapFragment;

    public HashMap<String, String> locationHashMap;
    public @InjectView(R.id.destclose)
    TextView destclose;
    public @InjectView(R.id.destadddress)
    EditText destadddress;
    public @InjectView(R.id.address_search)
    ScrollView address_search;
    public @InjectView(R.id.pin_map)
    ImageView pin_map;
    public @InjectView(R.id.drop_done)
    TextView drop_done;

    public String searchlocation;
    public String lat;
    public String log;
    public GoogleMap googleMap;
    public String Listlat;
    public String Listlong;
    public String pickipCountry;
    public String destCountry;
    public ArrayList markerPoints = new ArrayList();
    public String oldstring = "";
    public String workaddress;
    public String searchwork;
    public String searchhome;
    public String settinghome;
    public String settingwork;
    public @InjectView(R.id.location_placesearch)
    RecyclerView mRecyclerView;
    protected GoogleApiClient mGoogleApiClient;
    protected boolean isInternetAvailable;
    //private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private List<AutocompletePrediction>addressAutoCompletePredictions = new ArrayList<>() ;
    GoogleMapPlaceSearchAutoCompleteRecyclerView googleMapPlaceSearchAutoCompleteRecyclerView;
    PlacesClient placesClient;
    /**
     * Textwatcher for place search
     */
    protected TextWatcher PlaceTextWatcher = new TextWatcher() {

        int count = 0;

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


            if (!s.toString().equals("") ) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!oldstring.equals(s.toString())) {
                            oldstring = s.toString();
                            count++;
                            // Toast.makeText(getApplicationContext(), count + " search " + s.toString(), Toast.LENGTH_SHORT).show();
                            //mAutoCompleteAdapter.getFilter().filter(s.toString());
                            getFullAddressUsingEdittextStringFromGooglePlaceSearchAPI(s.toString());
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                }, 1000);

            } else {
                googleMapPlaceSearchAutoCompleteRecyclerView.clearAddresses();
                mRecyclerView.setVisibility(View.GONE);
            }

        }
    };

    public void getFullAddressUsingEdittextStringFromGooglePlaceSearchAPI(String queryAddress){
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request =
                FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(queryAddress)
                        .build();
        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(
                        (response) -> {
                            googleMapPlaceSearchAutoCompleteRecyclerView.updateList(response.getAutocompletePredictions());

                        })
                .addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        DebuggableLogE(TAG, "Place not found: " + apiException.getStatusCode());
                        //CommonMethods.showUserMessage("place not found "+ apiException.getStatusCode());
                    }
                    exception.printStackTrace();
                });
    }

    @OnClick(R.id.arrow)
    public void onArrow() {
        onBackPressed();
    }

    @OnClick(R.id.destclose)
    public void destclose() {
        /**
         * Destination address close button
         */
        LatLng ll = new LatLng(1, 1);
        markerPoints.set(1, ll);
        destadddress.setText("");
        destadddress.requestFocus();
        destadddress.addTextChangedListener(PlaceTextWatcher);
    }

    @OnClick(R.id.drop_done)
    public void drop_done() {
        String destup = destadddress.getText().toString();



        if (destup.matches("")) {
            address_search.setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.GONE);
            pin_map.setVisibility(View.GONE);
            drop_done.setVisibility(View.GONE);

            if (destup.matches("")) {
                destadddress.requestFocus();
            }

        } else {


            //if (destCountry.equals(pickipCountry)) {


//
            sessionManager.setIsrequest(true);
            address_search.setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.GONE);
            pin_map.setVisibility(View.GONE);
            drop_done.setVisibility(View.GONE);
            destadddress.removeTextChangedListener(PlaceTextWatcher);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(destadddress.getWindowToken(), 0);


            if (workaddress != null) {

                /**
                 * Update rider work address
                 */
                locationHashMap = new HashMap<>();
                locationHashMap.put("latitude", lat);
                locationHashMap.put("longitude", log);
                locationHashMap.put("work", destup);
                locationHashMap.put("token", sessionManager.getAccessToken());
                updateRiderLoc();

                Intent intent = new Intent(getApplicationContext(), Setting.class);
                intent.putExtra("worktextstr", destup);
                sessionManager.setWorkAddress(destup);
                startActivity(intent);
                finish();
            } else if (searchhome != null && searchhome.equals("searchhome")) {

                /**
                 *  Update rider home address
                 */
                getLocationFromAddress(destup);
                locationHashMap = new HashMap<>();
                locationHashMap.put("latitude", lat);
                locationHashMap.put("longitude", log);
                locationHashMap.put("home", destup);
                locationHashMap.put("token", sessionManager.getAccessToken());
                updateRiderLoc();
                Intent intent = new Intent(getApplicationContext(), PlaceSearchActivity.class);
                // intent.putExtra("searchhome",addressstring);
                LatLng objLatLng=getIntent().getExtras().getParcelable("Latlng");
                intent.putExtra("Latlng", objLatLng);
                intent.putExtra("Country", getIntent().getStringExtra("Country"));
                intent.putExtra("Work", getIntent().getStringExtra("Work"));
                intent.putExtra("Home", destup);
                intent.putExtra("Schedule", getIntent().getStringExtra("Schedule"));
                intent.putExtra("PickupAddress", getIntent().getStringExtra("PickupAddress"));
                intent.putExtra("DropAddress", getIntent().getStringExtra("DropAddress"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                sessionManager.setHomeAddress(destup);
                startActivity(intent);
                finish();

            } else if (searchwork != null && searchwork.equals("searchwork")) {

                getLocationFromAddress(destup);
                locationHashMap = new HashMap<>();
                locationHashMap.put("latitude", lat);
                locationHashMap.put("longitude", log);
                locationHashMap.put("work", destup);
                locationHashMap.put("token", sessionManager.getAccessToken());
                updateRiderLoc();

                Intent intent = new Intent(getApplicationContext(), PlaceSearchActivity.class);
                LatLng objLatLng=getIntent().getExtras().getParcelable("Latlng");
                intent.putExtra("Latlng", objLatLng);
                intent.putExtra("Country", getIntent().getStringExtra("Country"));
                intent.putExtra("Work", destup);
                intent.putExtra("Home", getIntent().getStringExtra("Home"));
                intent.putExtra("Schedule", getIntent().getStringExtra("Schedule"));
                intent.putExtra("PickupAddress", getIntent().getStringExtra("PickupAddress"));
                intent.putExtra("DropAddress", getIntent().getStringExtra("DropAddress"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                sessionManager.setHomeAddress(destup);
                startActivity(intent);
                finish();

            } else {

                getLocationFromAddress(destup);
                locationHashMap = new HashMap<>();
                locationHashMap.put("latitude", lat);
                locationHashMap.put("longitude", log);
                locationHashMap.put("home", destup);
                locationHashMap.put("token", sessionManager.getAccessToken());
                updateRiderLoc();

                Intent intent = new Intent(getApplicationContext(), Setting.class);
                intent.putExtra("hometextstr", destup);
                sessionManager.setHomeAddress(destup);
                startActivity(intent);
                finish();
            }


           /* } else {
                commonMethods.showMessage(AddHome.this, dialog, getString(R.string.service_not_available));
            }*/
        }
    }

    @OnClick(R.id.setlocaion_onmap)
    public void setlocaion_onmap() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(destadddress.getWindowToken(), 0);
        address_search.setVisibility(View.GONE);
        mapFragment.getView().setVisibility(View.VISIBLE);
        pin_map.setVisibility(View.VISIBLE);
        drop_done.setVisibility(View.VISIBLE);
    }

    public void getIntentval() {
        /**
         *  Get Address data from other activity
         */
        Intent intent = getIntent();
        pickipCountry = getIntent().getStringExtra("Country");
        workaddress = intent.getStringExtra("workaddress");
        searchwork = intent.getStringExtra("searchwork");
        searchhome = intent.getStringExtra("searchhome");
        settinghome = intent.getStringExtra("settinghome");
        settingwork = intent.getStringExtra("settingwork");
        destadddress.setText(getIntent().getStringExtra("DropAddress"));

        if (getIntent().getSerializableExtra("PickupDrop") != null) {
            markerPoints = (ArrayList) getIntent().getSerializableExtra("PickupDrop");
            LatLng lll = (LatLng) markerPoints.get(0);
            destCountry = getCountry(lll.latitude, lll.longitude);
        }

    }

    public void setHints() {
        DebuggableLogD(TAG, "searchwork" + searchwork);
        if (searchwork != null && searchhome != null) {
            if ("searchwork".equals(searchwork)) {
                destadddress.setHint(getString(R.string.enterworkaddress));
            } else if ("searchhome".equals(searchhome)) {
                destadddress.setHint(getString(R.string.enterhomeaddress));
            }
        } else {

            if (workaddress != null && "workaddress".equals(workaddress)) {
                destadddress.setHint(getString(R.string.enterworkaddress));
            } else if (settinghome != null && "settinghome".equals(settinghome)) {
                destadddress.setHint(getString(R.string.enterhomeaddress));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        setContentView(R.layout.activity_add_home);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);
        placesClient = com.google.android.libraries.places.api.Places.createClient(this);
        googleMapPlaceSearchAutoCompleteRecyclerView = new GoogleMapPlaceSearchAutoCompleteRecyclerView(addressAutoCompletePredictions, this, this);
        showPermissionDialog();
        getIntentval();
        setHints();
        initRecycleview();

        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * Destination address focus listener
         */
        destadddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    destadddress.addTextChangedListener(PlaceTextWatcher);
                    address_search.setVisibility(View.VISIBLE);
                }
            }
        });

        /**
         *  Place search list item click
         */


        isInternetAvailable = commonMethods.isOnline(getApplicationContext());

        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        }

    }

    public void initRecycleview() {
        /**
         *  Place search list
         */
        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(googleMapPlaceSearchAutoCompleteRecyclerView);
        destadddress.addTextChangedListener(PlaceTextWatcher);
    }

    public void onTouchList(AutocompletePrediction item) {
        if (item != null) {

            final String placeId = String.valueOf(item.getPlaceId());
            DebuggableLogI("TAG", "Autocomplete item selected: " + item.getFullText(null));

            searchlocation = item.getFullText(null).toString();
            destadddress.setText(item.getPrimaryText(null));
            String addressstring = item.getPrimaryText(null).toString();
            destadddress.removeTextChangedListener(PlaceTextWatcher);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(destadddress.getWindowToken(), 0);


            if (workaddress != null) {

                /**
                 * Update rider work address
                 */
                locationHashMap = new HashMap<>();
                locationHashMap.put("latitude", lat);
                locationHashMap.put("longitude", log);
                locationHashMap.put("work", addressstring);
                locationHashMap.put("token", sessionManager.getAccessToken());
                updateRiderLoc();

                Intent intent = new Intent(getApplicationContext(), Setting.class);
                intent.putExtra("worktextstr", addressstring);
                sessionManager.setWorkAddress(addressstring);
                startActivity(intent);
                finish();
            } else if (searchhome != null && searchhome.equals("searchhome")) {

                /**
                 *  Update rider home address
                 */
                getLocationFromAddress(addressstring);
                locationHashMap = new HashMap<>();
                locationHashMap.put("latitude", lat);
                locationHashMap.put("longitude", log);
                locationHashMap.put("home", addressstring);
                locationHashMap.put("token", sessionManager.getAccessToken());
                updateRiderLoc();
                LatLng objLatLng=getIntent().getExtras().getParcelable("Latlng");
                Intent intent = new Intent(getApplicationContext(), PlaceSearchActivity.class);
                // intent.putExtra("searchhome",addressstring);
                intent.putExtra("Latlng", objLatLng);
                intent.putExtra("Country", getIntent().getStringExtra("Country"));
                intent.putExtra("Work", getIntent().getStringExtra("Work"));
                intent.putExtra("Home", addressstring);
                intent.putExtra("Schedule", getIntent().getStringExtra("Schedule"));
                intent.putExtra("PickupAddress", getIntent().getStringExtra("PickupAddress"));
                intent.putExtra("DropAddress", getIntent().getStringExtra("DropAddress"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                sessionManager.setHomeAddress(addressstring);
                startActivity(intent);
                finish();

            } else if (searchwork != null && searchwork.equals("searchwork")) {

                getLocationFromAddress(addressstring);
                locationHashMap = new HashMap<>();
                locationHashMap.put("latitude", lat);
                locationHashMap.put("longitude", log);
                locationHashMap.put("work", addressstring);
                locationHashMap.put("token", sessionManager.getAccessToken());
                updateRiderLoc();
                LatLng objLatLng=getIntent().getExtras().getParcelable("Latlng");
                Intent intent = new Intent(getApplicationContext(), PlaceSearchActivity.class);
                intent.putExtra("Latlng", objLatLng);
                intent.putExtra("Country", getIntent().getStringExtra("Country"));
                intent.putExtra("Work", addressstring);
                intent.putExtra("Home", getIntent().getStringExtra("Home"));
                intent.putExtra("Schedule", getIntent().getStringExtra("Schedule"));
                intent.putExtra("PickupAddress", getIntent().getStringExtra("PickupAddress"));
                intent.putExtra("DropAddress", getIntent().getStringExtra("DropAddress"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                sessionManager.setHomeAddress(addressstring);
                startActivity(intent);
                finish();

            } else {

                getLocationFromAddress(addressstring);
                locationHashMap = new HashMap<>();
                locationHashMap.put("latitude", lat);
                locationHashMap.put("longitude", log);
                locationHashMap.put("home", addressstring);
                locationHashMap.put("token", sessionManager.getAccessToken());
                updateRiderLoc();

                Intent intent = new Intent(getApplicationContext(), Setting.class);
                intent.putExtra("hometextstr", addressstring);
                sessionManager.setHomeAddress(addressstring);
                startActivity(intent);
                finish();
            }



                        /*
                             Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
                         */

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(PlaceBuffer places) {
                    if (places.getCount() == 1) {
                        ReturnValues returnValues = getLocationFromAddress(AddHome.this, searchlocation);
                        LatLng cur_Latlng = returnValues.getLatLng();
                        Listlat = String.valueOf(cur_Latlng.latitude);
                        Listlong = String.valueOf(cur_Latlng.longitude);
                    }
                }
            });
            DebuggableLogI("TAG", "Clicked: " + item.getFullText(null));
            DebuggableLogI("TAG", "Called getPlaceById to get Place details for " + item.getPlaceId());
        }
    }

    /**
     * get Address from location
     */
    public ReturnValues getLocationFromAddress(Context context, String strAddress) {

        String country = null;
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            country = address.get(0).getCountryName();
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException ex) {

            ex.printStackTrace();
        }

        return new ReturnValues(p1, country);
        //return p1;
    }

    /**
     * Google API client connect
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

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

        } else
            try {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onConnectionSuspended(int i) {
        DebuggableLogI(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        DebuggableLogI(TAG, "Connection suspended");
    }

    /**
     * Move map to current location
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

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            DebuggableLogV("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            DebuggableLogV("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    private void initilizeMap() {
        if (googleMap == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.search_map);
            mapFragment.getMapAsync(this);
            mapFragment.getView().setVisibility(View.GONE);
            pin_map.setVisibility(View.GONE);
            drop_done.setVisibility(View.GONE);

        }
    }

    /**
     * Function to load map. If map is not created it will create it for you
     */
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


    /**
     * Get full address details
     */

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        // String strAdd = "";
        String address = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                String test1 = addresses.get(0).getCountryName();
                String test2 = addresses.get(0).getAdminArea();
                String test3 = addresses.get(0).getCountryCode();
                String test4 = addresses.get(0).getFeatureName();
                int test5 = addresses.get(0).getMaxAddressLineIndex();
                String test6 = addresses.get(0).getUrl();
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                // String city = addresses.get(0).getLocality();
                // String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                // String postalCode = addresses.get(0).getPostalCode();

                destCountry = country;

                /*if (address != null) {
                    strAdd = address + " ";
                }

                if (city != null) {
                    strAdd = strAdd + city + " ";
                }

                if (state != null) {
                    strAdd = strAdd + state + " ";
                }
                if (country != null) {
                    strAdd = strAdd + country + " ";
                }

                if (postalCode != null) {
                    strAdd = strAdd + postalCode + " ";
                }*/

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    private String getCountry(double LATITUDE, double LONGITUDE) {
        String country = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                country = addresses.get(0).getCountryName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Log.w("My Current loction address", "Canont get Address!");
        }
        return country;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    /**
     * Check location permission
     */
    private void showPermissionDialog() {
        if (!Permission.checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    4);
        }
    }


    /**
     * Map move camera listener
     */
    @Override
    public void onCameraMoveStarted(int reason) {
        //Toast.makeText(this, "The camera moving Started.",Toast.LENGTH_SHORT).show();
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
        // Toast.makeText(this, "The camera has stopped moving.",Toast.LENGTH_SHORT).show();

        CameraPosition cameraPosition = googleMap.getCameraPosition();

        destadddress.removeTextChangedListener(PlaceTextWatcher);
        String address = getCompleteAddressString(cameraPosition.target.latitude, cameraPosition.target.longitude);

        destadddress.setText(address);

        Listlat = String.valueOf(cameraPosition.target.latitude);
        Listlong = String.valueOf(cameraPosition.target.longitude);
        DebuggableLogI("centerLat", String.valueOf(cameraPosition.target.latitude));
        DebuggableLogI("centerLong", String.valueOf(cameraPosition.target.longitude));
        LatLng cur_Latlng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
        if (getIntent().getSerializableExtra("PickupDrop") != null) {
            markerPoints = (ArrayList) getIntent().getSerializableExtra("PickupDrop");
            LatLng lll = (LatLng) markerPoints.get(0);
            destCountry = getCountry(lll.latitude, lll.longitude);
        }
        // markerPoints.set(0, cur_Latlng);
        destadddress.addTextChangedListener(PlaceTextWatcher);

    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            if (jsonResp.getStatusCode().equals("2"))
                commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            // snackBar(jsonResp.getStatusMsg(), "hi", false, 2);
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
     * Update Rider Location
     */
    public void updateRiderLoc() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.uploadRiderLocation(locationHashMap).enqueue(new RequestCallback(REQ_UPDATE_LOCATION, this));

    }

    /**
     * Get location from address
     */

    public void getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return;
            }

            if (!address.isEmpty()) {
                Address location = address.get(0);
                location.getLatitude();
                location.getLongitude();

                lat = String.valueOf(location.getLatitude());
                log = String.valueOf(location.getLongitude());

                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException ex) {

            ex.printStackTrace();
        }

        //return p1;
    }

    @Override
    public void selectedAddress(AutocompletePrediction autocompletePrediction) {
        onTouchList(autocompletePrediction);
    }
}