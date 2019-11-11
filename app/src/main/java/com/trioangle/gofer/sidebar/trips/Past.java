package com.trioangle.gofer.sidebar.trips;

/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.trips
 * @category Past
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.adapters.DataAdapter;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.trip.InvoiceModel;
import com.trioangle.gofer.datamodels.trip.PaymentDetails;
import com.trioangle.gofer.datamodels.trip.TripDetailModel;
import com.trioangle.gofer.datamodels.trip.TripResult;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.interfaces.YourTripsListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sendrequest.AcceptedDriverDetails;
import com.trioangle.gofer.sendrequest.DriverRatingActivity;
import com.trioangle.gofer.sendrequest.PaymentAmountPage;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import javax.inject.Inject;
import java.util.ArrayList;

import static com.trioangle.gofer.utils.Enums.REQ_GET_DRIVER;

/* ************************************************************
    Past Trip details
    *********************************************************** */
public class Past extends Fragment implements ServiceListener {

    public static AcceptedDriverDetails acceptedDriverDetails = new AcceptedDriverDetails();
    public static ArrayList<TripDetailModel> tripDetailModels = new ArrayList<>();
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
    public String url;

    public @InjectView(R.id.rcView)
    RecyclerView rcView;
    public @InjectView(R.id.listempty)
    TextView listempty;

    public TripResult tripResult;
    public String tripStatus;
    public boolean check = true;
    protected boolean isInternetAvailable;
    private YourTripsListener listener;
    private YourTrips mActivity;
    private DataAdapter dataAdapter;

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

        View rootView = inflater.inflate(R.layout.activity_past, container, false);
        ButterKnife.inject(this, rootView);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(getActivity());
        init();
        /**
         *  Common loader
         */

        rcView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rcView.setLayoutManager(layoutManager);
        int resId = R.anim.layout_animation_bottom_up;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rcView.setLayoutAnimation(animation);

        /**
         * Past trips details list
         */
        /*rcView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && gestureDetector.onTouchEvent(e)) {
                    int position = rv.getChildAdapterPosition(child);
                    tripStatus = tripDetailModels.get(position).getStatus();
                    if ("Completed".equals(tripStatus) || "Cancelled".equals(tripStatus)||"Rating".equals(tripStatus)) {

                        Intent intent = new Intent(getActivity(), TripDetails.class);
                        intent.putExtra("postion", position);
                        startActivity(intent);
                    } else {//if(tripStatus.equals("Scheduled")||tripStatus.equals("Begin trip")||tripStatus.equals("End trip")){
                        if (!isInternetAvailable) {
                            commonMethods.showMessage(mActivity, dialog, getString(R.string.no_connection));
                        } else {
                            check = false;
                            sessionManager.setTripId(tripDetailModels.get(position).getId());

                            getDriverDetails();

                        }
                    }
                    //Toast.makeText(getApplicationContext(), countries.get(position), Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                DebuggableLogI("Past", "onTouchEvent");
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                DebuggableLogI("Past", "onTouchEvent");
            }
        });*/

        /**
         * Get Rider trips details from API
         */
        isInternetAvailable = commonMethods.isOnline(mActivity.getApplicationContext());
        if (!isInternetAvailable) {
            dialogfunction();
        } else {
            getRiderTrip();
        }
        return rootView;
    }

    /**
     * API called for rider trips details
     */

    public void getRiderTrip() {
        tripDetailModels = mActivity.getPastTripList();

        if (tripDetailModels.size() > 0) {
            dataAdapter = new DataAdapter(tripDetailModels, getContext());
            rcView.setAdapter(dataAdapter);
            dataAdapter.setOnItemRatingClickListner(new DataAdapter.onItemRatingClickListner() {
                @Override
                public void setRatingClick(int position) {
                    tripStatus = tripDetailModels.get(position).getStatus();
                    if ("Completed".equals(tripStatus) || "Cancelled".equals(tripStatus) || "Rating".equals(tripStatus)) {

                        Intent intent = new Intent(getActivity(), TripDetails.class);
                        intent.putExtra("postion", position);
                        startActivity(intent);
                    } else {//if(tripStatus.equals("Scheduled")||tripStatus.equals("Begin trip")||tripStatus.equals("End trip")){
                        if (!isInternetAvailable) {
                            commonMethods.showMessage(mActivity, dialog, getString(R.string.no_connection));
                        } else {
                            check = false;
                            sessionManager.setTripId(tripDetailModels.get(position).getId());

                            getDriverDetails();

                        }
                    }
                }
            });
        } else {
            listempty.setVisibility(View.VISIBLE);
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_bottom_up);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    /**
     * Get Accepted driver details
     */
    public void getDriverDetails() {
        commonMethods.showProgressDialog(mActivity, customDialog);
        apiService.getDriverDetails(sessionManager.getAccessToken(), sessionManager.getTripId()).enqueue(new RequestCallback(REQ_GET_DRIVER, this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(mActivity, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {

            case REQ_GET_DRIVER:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    if (isAdded())
                        onSuccessDriver(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                }
                break;
            default:
                if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
                }
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
        }
    }

    public void onSuccessDriver(JsonResponse jsonResp) {
        /*if (CommonKeys.IS_ALREADY_IN_TRIP){
            CommonKeys.IS_ALREADY_IN_TRIP=false;
        }*/
        acceptedDriverDetails = gson.fromJson(jsonResp.getStrResponse(), AcceptedDriverDetails.class);
        PaymentDetails paymentDetails = acceptedDriverDetails.getPaymentDetails();
        ArrayList<InvoiceModel> invoiceModels = acceptedDriverDetails.getInvoice();
        sessionManager.setIsrequest(false);
        sessionManager.setIsTrip(true);
        //runLayoutAnimation(rcView);
        // If trips status is schedule or begin trip or endtrip to open the trips page ( Map Page)
        if ("Scheduled".equals(tripStatus) || "Begin trip".equals(tripStatus) || "End trip".equals(tripStatus)) {

            if ("Scheduled".equals(tripStatus)) {
                sessionManager.setTripStatus("arrive_now");
            } else if ("Begin trip".equals(tripStatus)) {
                sessionManager.setTripStatus("arrive_now");
            } else if ("End trip".equals(tripStatus)) {
                sessionManager.setTripStatus("end_trip");
            }

            Intent requstreceivepage = new Intent(getActivity(), MainActivity.class);
            requstreceivepage.putExtra("driverDetails", acceptedDriverDetails);
            if ("Scheduled".equals(tripStatus) || "Begin trip".equals(tripStatus)) {
                requstreceivepage.putExtra("isTripBegin", false);
            } else if ("End trip".equals(tripStatus)) {
                requstreceivepage.putExtra("isTripBegin", true);
            }
            sessionManager.setDriverAndRiderAbleToChat(true);
            startActivity(requstreceivepage);
        } else if ("Rating".equals(tripStatus)) {  // To open rating page
            sessionManager.setDriverAndRiderAbleToChat(false);
            CommonMethods.stopFirebaseChatListenerService(mActivity);
            Intent rating = new Intent(getActivity(), DriverRatingActivity.class);
            rating.putExtra("imgprofile", acceptedDriverDetails.getProfileimg());
            startActivity(rating);

        } else if ("Payment".equals(tripStatus)) {  // To open the payment page

            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);
            Bundle bundle = new Bundle();
            bundle.putSerializable("invoiceModels", invoiceModels);
            Intent main = new Intent(getActivity(), PaymentAmountPage.class);
            main.putExtra("AmountDetails", jsonResp.getStrResponse().toString());
            main.putExtra("paymentDetails", paymentDetails);
            main.putExtra("driverDetails", acceptedDriverDetails);
            main.putExtra("isBack", 1);
            main.putExtras(bundle);
            startActivity(main);
        }
        getActivity().overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }


    /**
     * Dialog to show turn on the internet
     */
    public void dialogfunction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.turnoninternet))
                .setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void init() {
        if (listener == null) return;

        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (YourTrips) getActivity();
    }


}
