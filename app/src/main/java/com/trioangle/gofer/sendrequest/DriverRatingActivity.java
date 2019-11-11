package com.trioangle.gofer.sendrequest;
/**
 * @package com.trioangle.gofer
 * @subpackage sendrequest
 * @category DriverRatingActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.trip.InvoiceModel;
import com.trioangle.gofer.datamodels.trip.TripRatingResult;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogD;

/* ************************************************************
    Rider give the rate and comments for driver
    *************************************************************** */
public class DriverRatingActivity extends AppCompatActivity implements ServiceListener {

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
    public @InjectView(R.id.rider_rating)
    SimpleRatingBar riderrate;
    public @InjectView(R.id.rider_comments)
    TextView ridercomments;
    public @InjectView(R.id.profile_image1)
    CircleImageView profile_image1;
    protected boolean isInternetAvailable;

    @OnClick(R.id.rate_submit)
    public void submit() {  // Rating submit button
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
        } else {
            sessionManager.clearDriverNameRatingAndProfilePicture();
            float rating = riderrate.getRating();
            String ratingstr = String.valueOf(rating);
            DebuggableLogD("OUTPUT REBAI", ratingstr);
            if (!"0.0".equals(ratingstr)) {
                String ridercomment = ridercomments.getText().toString();
                ridercomment = ridercomment.replaceAll("[\\t\\n\\r]", " ");
                sessionManager.setIsrequest(false);
                sessionManager.setIsTrip(false);
                submitRating(rating, ridercomment);
            } else {
                commonMethods.showMessage(this, dialog, getResources().getString(R.string.error_msg_rating));
                DebuggableLogD("OUTPUT REBAI", "error_msg_rating");
            }
        }
    }

    @OnClick(R.id.tvskip)
    public void skip() {
        onBackPressed();
    }

    String user_thumb_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driverrating);
        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this);
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        dialog = commonMethods.getAlertDialog(this);

        String laydir = getResources().getString(R.string.layout_direction);
        if ("1".equals(laydir)) {
            riderrate.setGravity(SimpleRatingBar.Gravity.Right);
        }
        // Get Driver profile image
        Intent intent = getIntent();
        if (intent.getStringExtra("imgprofile")!=null&&!TextUtils.isEmpty(intent.getStringExtra("imgprofile"))) {
            user_thumb_image = intent.getStringExtra("imgprofile");
        }else {
            user_thumb_image=sessionManager.getDriverProfilePic();
        }

        Picasso.with(getApplicationContext()).load(user_thumb_image)
                .into(profile_image1);

        ridercomments.clearFocus();

    }

    /**
     * Submit rating API called
     */
    public void submitRating(float rating, String ridercomment) {
        commonMethods.showProgressDialog(this, customDialog);
        String tripId;
        if (getIntent().getIntExtra("back",0)==1){
            tripId=getIntent().getStringExtra("tripid");
        }else {
            tripId=sessionManager.getTripId();
        }
        apiService.tripRating(sessionManager.getAccessToken(), tripId, String.valueOf(rating), ridercomment, sessionManager.getType()).enqueue(new RequestCallback(this));

    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            onSuccessRating(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            commonMethods.hideProgressDialog();
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            commonMethods.hideProgressDialog();
        }
    }

    public void onSuccessRating(JsonResponse jsonResp) {
        riderrate.setRating(0);
        ridercomments.setText("");
        commonMethods.hideProgressDialog();
        if (jsonResp.getStatusCode().equals("1")) {
            TripRatingResult earningModel = gson.fromJson(jsonResp.getStrResponse(), TripRatingResult.class);
            ArrayList<InvoiceModel> invoiceModels = earningModel.getInvoice();
            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);

            try {
                JSONObject response = new JSONObject(jsonResp.getStrResponse());
                if (response.has("promo_details")) {
                    int promocount = response.getJSONArray("promo_details").length();
                    sessionManager.setPromoDetail(response.getString("promo_details"));
                    sessionManager.setPromoCount(promocount);
                }
            } catch (JSONException j) {
                j.printStackTrace();
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("invoiceModels", invoiceModels);
           /* Intent main = new Intent(getApplicationContext(), PaymentAmountPage.class);
            main.putExtra("AmountDetails", jsonResp.getStrResponse().toString());
            main.putExtras(bundle);
            startActivity(main);*/
            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);
            sessionManager.setDriverAndRiderAbleToChat(false);
            CommonMethods.stopFirebaseChatListenerService(this);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else if (jsonResp.getStatusCode().equals("2")) {
            commonMethods.hideProgressDialog();
            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getIntExtra("back",0)==1){
            super.onBackPressed();
        }else {
            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);
            sessionManager.setDriverAndRiderAbleToChat(false);
            CommonMethods.stopFirebaseChatListenerService(this);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
