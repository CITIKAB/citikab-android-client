package com.trioangle.gofer.sidebar.trips;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.trips
 * @category YourTrips
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.adapters.DataAdapter;
import com.trioangle.gofer.adapters.UpcomingAdapter;
import com.trioangle.gofer.adapters.ViewPagerAdapter;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.trip.ScheduleDetail;
import com.trioangle.gofer.datamodels.trip.ScheduleTripResult;
import com.trioangle.gofer.datamodels.trip.TripDetailModel;
import com.trioangle.gofer.datamodels.trip.TripResult;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.interfaces.YourTripsListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.Enums.REQ_TRIP_DETAIL;

/* ************************************************************
    YourTrips connect view pager
    *********************************************************** */
public class YourTrips extends AppCompatActivity implements TabLayout.OnTabSelectedListener, YourTripsListener,ServiceListener {

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

    public ArrayList<ScheduleDetail> scheduleDetails = new ArrayList<>();
    public TripResult tripResult;
    public static ArrayList<TripDetailModel> tripDetailModels = new ArrayList<>();

    //This is our tablayout
    @InjectView(R.id.tabLayout)
    public TabLayout tabLayout;

    //This is our viewPager
    @InjectView(R.id.pager)
    public ViewPager viewPager;

    @OnClick(R.id.back)
    public void onBack() {
        if (getIntent().getStringExtra("upcome").equals("upcome")) {
            Intent intent = new Intent(YourTrips.this, MainActivity.class);
            intent.putExtra("upcome", "upcome");
            startActivity(intent);
        } else {
            onBackPressed();
        }
    }
    protected boolean isInternetAvailable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_trips);

        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this);

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        dialog = commonMethods.getAlertDialog(this);

        if (!isInternetAvailable) {
            //dialogfunction();
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));

        } else {
            getRiderTrip();
        }
        if (getIntent().getStringExtra("upcome").equals("upcome")) {
            switchTab(1);
        } else {
            switchTab(0);
        }



    }

    /**
     * Setup tab
     */

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Past(), getResources().getString(R.string.pasttrip));
        adapter.addFragment(new Upcoming(), getResources().getString(R.string.upcomingtrips));
        viewPager.setAdapter(adapter);
        if (getIntent().getStringExtra("upcome").equals("upcome")) {
            switchTab(1);
        } else {
            switchTab(0);
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        DebuggableLogI("Gofer", "Tab");
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        DebuggableLogI("Gofer", "Tab");
    }

    public void switchTab(int tabno) {
        viewPager.setCurrentItem(tabno);
    }

    @Override
    public Resources getRes() {
        return YourTrips.this.getResources();
    }

    @Override
    public YourTrips getInstance() {
        return YourTrips.this;
    }

    public void getRiderTrip() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.getRiderTrips(sessionManager.getAccessToken(), sessionManager.getType()).enqueue(new RequestCallback(REQ_TRIP_DETAIL, this));

    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
                onSuccessTrip(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {

    }

    public void onSuccessTrip(JsonResponse jsonResp) {
        tripResult = gson.fromJson(jsonResp.getStrResponse(), TripResult.class);
        ArrayList<TripDetailModel> trip = tripResult.getTripDetail();
        tripDetailModels.clear();

        ArrayList<ScheduleDetail> schedule = tripResult.getScheduleTrip();
        scheduleDetails.clear();
        if (schedule.size() > 0) {
            scheduleDetails.addAll(schedule);
            Collections.reverse(scheduleDetails);
        }
        if (trip.size() > 0) {
            for (int i = 0; i < trip.size(); i++) {
                String staticMapURL;
                if ((trip.get(i).getStatus().equals("Completed") || trip.get(i).getStatus().equals("Rating")
                        || trip.get(i).getStatus().equals("Payment")) && !trip.get(i).getMapImage().equals("")) {
                    staticMapURL = trip.get(i).getMapImage();
                } else {
                    LatLng pikcuplatlng = new LatLng(Double.valueOf(trip.get(i).getPickupLatitude()), Double.valueOf(trip.get(i).getPickupLongitude()));
                    LatLng droplatlng = new LatLng(Double.valueOf(trip.get(i).getDropLatitude()), Double.valueOf(trip.get(i).getDropLongitude()));

                    String pathString = "&path=color:0x000000ff%7Cweight:4%7Cenc:" + trip.get(i).getTripPath();
                    String pickupstr = +pikcuplatlng.latitude + "," + pikcuplatlng.longitude;
                    String dropstr = droplatlng.latitude + "," + droplatlng.longitude;
                    String positionOnMap = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "pickup.png|" + pickupstr;
                    String positionOnMap1 = "&markers=size:mid|icon:" + CommonKeys.imageUrl + "drop.png|" + dropstr;


                    staticMapURL = "https://maps.googleapis.com/maps/api/staticmap?size=640x250&" +
                            pikcuplatlng.latitude + "," + pikcuplatlng.longitude +
                            pathString + "" + positionOnMap + "" + positionOnMap1 +
                            "&key=" + sessionManager.getGoogleMapKey() + "&language=" +
                            Locale.getDefault();
                }
                trip.get(i).setStaticMapUR(staticMapURL);
                //sessionManager.setCurrencySymbol(trip.get(i).getCurrencyCode());
                tripDetailModels.add(trip.get(i));



            }
        }
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    public ArrayList<ScheduleDetail> getUpcomingTripList() {
        return scheduleDetails;
    }

    public ArrayList<TripDetailModel> getPastTripList() {
        return tripDetailModels;
    }

    /**
     * Dialog to show turn on the internet
     */
    public void dialogfunction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.turnoninternet))
                .setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();
    }
}
