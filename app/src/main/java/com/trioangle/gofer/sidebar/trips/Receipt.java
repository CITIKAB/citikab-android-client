package com.trioangle.gofer.sidebar.trips;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.trips
 * @category Receipt
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.adapters.PriceRecycleAdapter;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.trip.InvoiceModel;
import com.trioangle.gofer.network.AppController;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.trioangle.gofer.sidebar.trips.Past.tripDetailModels;
import static com.trioangle.gofer.sidebar.trips.TripDetails.postion;

/* ************************************************************
    Receipt view page tap fragment
    *********************************************************** */
public class Receipt extends Fragment {


    public @Inject
    SessionManager sessionManager;

    public @InjectView(R.id.basefare_amount)
    TextView basefare_amount;
    public @InjectView(R.id.distance_fare)
    TextView distance_fare;
    public @InjectView(R.id.time_fare)
    TextView time_fare;
    public @InjectView(R.id.fee)
    TextView fee;
    public @InjectView(R.id.totalamount)
    TextView totalamount;
    public @InjectView(R.id.walletamount)
    TextView walletamount;
    public @InjectView(R.id.promoamount)
    TextView promoamount;
    public @InjectView(R.id.payableamount)
    TextView payableamount;
    public @InjectView(R.id.walletamountlayout)
    RelativeLayout walletamountlayout;
    public @InjectView(R.id.promoamountlayout)
    RelativeLayout promoamountlayout;
    public @InjectView(R.id.payableamountlayout)
    RelativeLayout payableamountlayout;

    public float payable_amt = 0;
    public String total_fare;
    public String driver_payout;
    public String admin_payout;
    public String payment_method;
    public @InjectView(R.id.rvPrice)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_receipt, container, false);
        ButterKnife.inject(this, rootView);
        AppController.getAppComponent().inject(this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<InvoiceModel> invoiceModels = tripDetailModels.get(postion).getInvoice();
        RecyclerView.Adapter adapter = new PriceRecycleAdapter(getActivity(),invoiceModels);
        recyclerView.setAdapter(adapter);

        /**
         * Show the receipt details for the trip
         */
       /* total_fare = tripDetailModels.get(postion).getTotalFare();
        String access_fee = tripDetailModels.get(postion).getAccessFee();
        String base_fare = tripDetailModels.get(postion).getBaseFare();
        String total_km_fare = tripDetailModels.get(postion).getDistanceFare();
        String total_time_fare = tripDetailModels.get(postion).getTimeFare();
        String wallet_amount = tripDetailModels.get(postion).getWalletAmount();
        String promo_amount = tripDetailModels.get(postion).getPromoAmount();

        driver_payout = tripDetailModels.get(postion).getDriverPayout();
        admin_payout = tripDetailModels.get(postion).getAccessFee();
        payment_method = tripDetailModels.get(postion).getPaymentMethod();

        // Check is cashLayout trip, wallet amount available or promo amount available


        *//**
         * show receipt details
         *//*
        basefare_amount.setText(sessionManager.getCurrencySymbol() + base_fare);
        distance_fare.setText(sessionManager.getCurrencySymbol() + total_km_fare);
        time_fare.setText(sessionManager.getCurrencySymbol() + total_time_fare);
        fee.setText(sessionManager.getCurrencySymbol() + access_fee);
        totalamount.setText(sessionManager.getCurrencySymbol() + total_fare.replaceAll(",", "."));
        payableamount.setText(sessionManager.getCurrencySymbol() + String.format("%.2f", payable_amt).replaceAll(",", "."));//String.valueOf(payable_amt));
        walletamount.setText("- " + sessionManager.getCurrencySymbol() + wallet_amount);
        promoamount.setText("- " + sessionManager.getCurrencySymbol() + promo_amount);*/

        return rootView;
    }
}
