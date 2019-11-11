package com.trioangle.gofer.sidebar.payment;

/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.payment
 * @category PaymentPage
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.views.customize.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
    Promo code and amount detsils list
    *********************************************************** */
public class PromoAmountActivity extends AppCompatActivity {

    public static ArrayList<PromoAmountModel> promoAmountModelList = new ArrayList<>();
    public static PromoAmountModel promoAmountModel = new PromoAmountModel();
    public AlertDialog dialog;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    CustomDialog customDialog;
    @InjectView(R.id.promotions_list)
    public RecyclerView listView;
    public Context context;
    public PromoAmountAdapter promoAmountAdapter;
    protected boolean isInternetAvailable;

    @OnClick(R.id.arrow)
    public void onback() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_details);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());

        /**
         *  List to show the promo code and amount details
         */
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        //recyclerView.addItemDecoration(new VerticalLineDecorator(2));
        listView.setAdapter(promoAmountAdapter);

        /*listView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

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
                    // tripStatus=tripdetailarraylist.get(position).getStatus();

                    //Toast.makeText(getApplicationContext(), countries.get(position), Toast.LENGTH_SHORT).show();
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });*/

        loadData();

    }

    /**
     * Get and show the promo code and amount details
     */

    public void loadData() {
        promoAmountModelList.clear();

        try {
            JSONArray json = new JSONArray(String.valueOf(sessionManager.getPromoDetail()));
            if (json.length() > 0) {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObj = json.getJSONObject(i);
                    String id = jsonObj.getString("id");
                    String promo_amount = jsonObj.getString("amount");
                    String promo_code = jsonObj.getString("code");
                    String promo_exp = jsonObj.getString("expire_date");

                    promoAmountModel = new PromoAmountModel(promo_amount, id, promo_code, promo_exp);
                    promoAmountModelList.add(promoAmountModel);

                    RecyclerView.Adapter adapter = new PromoAmountAdapter(promoAmountModelList, getApplicationContext());
                    listView.setAdapter(adapter);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        }

    }


}