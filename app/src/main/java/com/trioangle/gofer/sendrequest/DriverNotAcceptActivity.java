package com.trioangle.gofer.sendrequest;
/**
 * @package com.trioangle.gofer
 * @subpackage sendrequest
 * @category DriverNotAcceptActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;

import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;

/* ************************************************************
   Drivers Not Accept the request rider can give request again otherwise goto home page
    *************************************************************** */
public class DriverNotAcceptActivity extends AppCompatActivity implements ServiceListener {

    public AlertDialog dialog;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    Gson gson;
    public @InjectView(R.id.try_again)
    TextView try_again;
    public @InjectView(R.id.drivernotaccept_back)
    ImageView drivernotaccept_back;

    public @InjectView(R.id.tv_call)
    TextView tv_call;

    public @InjectView(R.id.rlt_contact_admin)
    RelativeLayout rlt_contact_admin;
    @OnClick(R.id.rlt_contact_admin)
    public void callAdmin(){
        String callnumber = sessionManager.getAdminContact();
        Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callnumber));
        startActivity(intent2);
        finish();
    }

    public HashMap<String, String> locationHashMap;
    protected boolean isInternetAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_not_accept);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        locationHashMap = (HashMap<String, String>) getIntent().getSerializableExtra("hashMap");
        // Back button click
        if(!TextUtils.isEmpty(sessionManager.getAdminContact())) {
            tv_call.setText(getResources().getString(R.string.call_admin,sessionManager.getAdminContact()));
            rlt_contact_admin.setBackgroundColor(getResources().getColor(R.color.button_color));
        }else {
            tv_call.setText(getResources().getString(R.string.no_contact_found));
            rlt_contact_admin.setEnabled(false);
            rlt_contact_admin.setBackgroundColor(getResources().getColor(R.color.button_disable_color));
        }

        drivernotaccept_back = (ImageView) findViewById(R.id.drivernotaccept_back);
        drivernotaccept_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setIsrequest(false);
             /* Intent main=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(main);*/
                overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                finish();
            }
        });

        // Try again button click
        try_again = (TextView) findViewById(R.id.try_again);
        try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInternetAvailable) {
                    commonMethods.showMessage(DriverNotAcceptActivity.this, dialog, getResources().getString(R.string.no_connection));
                } else {
                    sendRequest();
                    //new SendRequest().execute(getIntent().getStringExtra("url"));
                }


            }
        });
    }

    /**
     * Send car request to rider again
     */
    public void sendRequest() {
        // commonMethods.showProgressDialog(this, customDialog);
        Intent sendrequst = new Intent(getApplicationContext(), SendingRequestActivity.class);
        sendrequst.putExtra("loadData", "load");
        sendrequst.putExtra("carname", getIntent().getStringExtra("carname"));
        sendrequst.putExtra("url", getIntent().getStringExtra("url"));
        sendrequst.putExtra("mapurl", getIntent().getStringExtra("mapurl"));
        sendrequst.putExtra("totalcar", getIntent().getIntExtra("totalcar", 0));
        sendrequst.putExtra("hashMap", locationHashMap);
        startActivity(sendrequst);
        apiService.sendRequest(locationHashMap).enqueue(new RequestCallback(this));
        finish();
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        DebuggableLogV("DriverNotAccept", "jsonResp");
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        DebuggableLogV("DriverNotAccept", "jsonResp" + jsonResp.getStatusMsg());
    }

}
