package com.trioangle.gofer.views.emergency;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;

public class SosActivity extends AppCompatActivity implements ServiceListener {

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
    @InjectView(R.id.message_icon)
    public ImageView message_icon;
    @InjectView(R.id.loader)
    public ImageView loader;
    @InjectView(R.id.close)
    public TextView close;
    @InjectView(R.id.alertyourcontact)
    public LinearLayout alert_your_contact;
    public Double latitude;
    public Double longitude;
    //Dialog_loading loader;
    protected boolean isInternetAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());

        DrawableImageViewTarget imageViewTarget = new DrawableImageViewTarget(loader);
        Glide.with(getApplicationContext()).load(R.drawable.dot_loader).into(imageViewTarget);
        /**
         * get Rider Latlong from Main activity
         */

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);


        /**
         * On Click Close Button
         */

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        /**
         *  Button to confrom the Alert
         */

        alert_your_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmergencyDialog();
            }

        });

    }


    /**
     * Alert Dialog
     */
    public void EmergencyDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SosActivity.this);
        LayoutInflater inflater = SosActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_emergency_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();


        TextView continuebutton = (TextView) dialogView.findViewById(R.id.continuebutton);
        TextView cancelbutton = (TextView) dialogView.findViewById(R.id.cancel);


        /**
         * Continue Button to call api
         */
        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Api calling


                sendSos();
                alertDialog.dismiss();


            }
        });

        /**
         * cancel button
         */

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }


    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            onSuccessSOS(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();

        }

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        DebuggableLogV("SOS", "onFailure");
    }

    public void onSuccessSOS(JsonResponse jsonResp) {
        if (jsonResp.getStatusCode().matches("1")) {
            //if (loader.isShowing()) loader.dismiss();
            loader.setVisibility(View.GONE);
            message_icon.setImageDrawable(getResources().getDrawable(R.drawable.checksymbol));
            Toast.makeText(getApplicationContext(), getString(R.string.sucessmessfe), Toast.LENGTH_SHORT).show();

        } else if (jsonResp.getStatusCode().matches("2")) {
            loader.setVisibility(View.GONE);
            message_icon.setImageDrawable(getResources().getDrawable(R.drawable.checksymbol));
            Toast.makeText(getApplicationContext(), getString(R.string.messagesenttoadmin), Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    /**
     * Cancel reason API called
     */
    public void sendSos() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.sosalert(sessionManager.getAccessToken(), String.valueOf(latitude), String.valueOf(longitude)).enqueue(new RequestCallback(this));

    }

}
