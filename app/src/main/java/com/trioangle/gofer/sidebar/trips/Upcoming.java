package com.trioangle.gofer.sidebar.trips;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.trips
 * @category Upcoming
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.adapters.UpcomingAdapter;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.trip.ScheduleDetail;
import com.trioangle.gofer.datamodels.trip.ScheduleTripResult;
import com.trioangle.gofer.interfaces.YourTripsListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.views.customize.CustomDialog;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/* ************************************************************
    Upcoming Trip details
    *********************************************************** */
public class Upcoming extends Fragment {

    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    CustomDialog customDialog;
    public @InjectView(R.id.rcView)
    RecyclerView rcView;
    public @InjectView(R.id.listempty)
    TextView listempty;
    public ScheduleTripResult scheduleTripResult;
    public ArrayList<ScheduleDetail> scheduleDetails = new ArrayList<>();
    protected boolean isInternetAvailable;
    private YourTripsListener listener;
    private YourTrips mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (YourTripsListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Past must implement ActivityListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_upcoming, container, false);

        ButterKnife.inject(this, rootView);
        AppController.getAppComponent().inject(this);
        init();
         /*
         *  Common loader initilize
         */

        rcView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rcView.setLayoutManager(layoutManager);
        int resId = R.anim.layout_animation_bottom_up;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rcView.setLayoutAnimation(animation);
        isInternetAvailable = commonMethods.isOnline(mActivity.getApplicationContext());
        if (!isInternetAvailable) {
            dialogfunction();
        } else {
            getScheduledTrip();
        }

        return rootView;

    }

    /**
     * API called for Scheduled trips details
     */

    public void getScheduledTrip() {
        scheduleDetails = mActivity.getUpcomingTripList();
        if (scheduleDetails.size()>0){
            RecyclerView.Adapter adapter = new UpcomingAdapter(scheduleDetails, mActivity);
            rcView.setAdapter(adapter);
        }else {
            listempty.setVisibility(View.VISIBLE);
        }

    }


    /**
     * Dialog to show turn on the internet
     */
    public void dialogfunction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Turn on your Internet")
                .setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void init() {
        if (listener == null) return;

        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (YourTrips) getActivity();
    }
}

