package com.trioangle.gofer.sidebar;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar
 * @category DriverContactActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.network.AppController;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.trioangle.gofer.utils.CommonMethods;

/* ************************************************************
   Current trip driver details and contact page
    *********************************************************** */
/*
* Editor:Umasankar
* on: 24/12/2018
 * Edited: Android action call permission removed and moved to Dial (Intent.ACTION_CALL -> Intent.ACTION_DIAL)
* purpose to reduse the no.of permission
* */
public class DriverContactActivity extends AppCompatActivity {

    public static final Integer CALL = 0x2;


    @InjectView(R.id.driver_name_contact)
    public TextView driver_name_contact;
    @InjectView(R.id.driver_phone_contact)
    public TextView driver_phone_contact;
    @InjectView(R.id.callbutton)
    public LinearLayout callbutton;

    @OnClick(R.id.back)
    public void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.messageButton)
    public void messageDriver(){
        CommonMethods.startChatActivityFrom(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_contact);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        driver_name_contact.setText(getIntent().getStringExtra("drivername"));
        driver_phone_contact.setText(getIntent().getStringExtra("driverno"));
        /**
         *  Call driver
         */
        callbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String callnumber = driver_phone_contact.getText().toString();
                Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + callnumber));
                startActivity(intent2);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
        finish();
    }

}
