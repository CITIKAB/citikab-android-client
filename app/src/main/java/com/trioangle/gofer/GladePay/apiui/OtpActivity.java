package com.trioangle.gofer.GladePay.apiui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


import com.trioangle.gofer.R;

import co.paystack.android.design.widget.PinPadView;

public class OtpActivity extends Activity {
    final OtpSingleton si = OtpSingleton.getInstance();
    private PinPadView pinpadView;
    private TextView tv_otp_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.co_gladepay___activity_otp);
        setTitle("ENTER OTP");
        Intent x = getIntent();
        String otpmessage=x.getStringExtra("otp_message");
        System.out.println("otp_message"+otpmessage);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pinpadView = findViewById(R.id.pinpadView);
        tv_otp_message=findViewById(R.id.otp_message);
        tv_otp_message.setText(otpmessage);
        setup();
    }
    protected void setup() {
       // pinpadView.setPromptText(si.getOtp());
      //  pinpadView.setVisibility(View.VISIBLE);
        pinpadView.setVibrateOnIncompleteSubmit(false);
        pinpadView.setAutoSubmit(false);
        pinpadView.setOnPinChangedListener(new PinPadView.OnPinChangedListener() {
            @Override
            public void onPinChanged(String oldPin, String newPin) {
                // We had set length to 10 while creating,
                // but in case some otp is longer,
                // we will keep increasing pin length

                if(newPin.length() <= pinpadView.getPinLength()){
                    pinpadView.setPromptText(newPin);
                 //   pinpadView.setPinLength(pinpadView.getPinLength()+1);
                }
            }
        });

        pinpadView.setOnSubmitListener(new PinPadView.OnSubmitListener() {
            // Always submit (we never expect this to complete since
            // we keep increasing the pinLength during pinChanged event)
            // we still handle onComplete nonetheless
            @Override
            public void onCompleted(String otp) {
                handleSubmit(otp);
            }

            @Override
            public void onIncompleteSubmit(String otp) {
                handleSubmit(otp);
            }
        });
    }
    public void onDestroy() {
        super.onDestroy();
        handleSubmit("");
    }
    public void handleSubmit(String otp){
        synchronized (si) {
            si.setOtp(otp);
            si.notify();
        }
        finish();
    }
}
