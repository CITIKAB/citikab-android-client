package com.trioangle.gofer.GladePay.apiui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.trioangle.gofer.R;

import co.paystack.android.design.widget.PinPadView;

public class PINActivity extends AppCompatActivity {
    final PinSingleton si = PinSingleton.getInstance();
    private PinPadView pinpadView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        setTitle("ENTER OTP");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pinpadView = findViewById(R.id.pinpadView);
        setup();
    }
    protected void setup() {

        // pinpadView.setPromptText(si.getOtp());
          pinpadView.setVisibility(View.VISIBLE);
        pinpadView.setVibrateOnIncompleteSubmit(false);
        pinpadView.setAutoSubmit(false);


        pinpadView.setOnPinChangedListener(new PinPadView.OnPinChangedListener() {
            @Override
            public void onPinChanged(String oldPin, String newPin) {
                // We had set length to 10 while creating,
                // but in case some otp is longer,
                // we will keep increasing pin length

                if(newPin.length() <= pinpadView.getPinLength()){
                    pinpadView.setPinLength(pinpadView.getPinLength());
                   // pinpadView.setPromptText(newPin);
                    //   pinpadView.setPinLength(pinpadView.getPinLength()+1);
                }
            }
        });

        pinpadView.setOnSubmitListener(new PinPadView.OnSubmitListener() {
            // Always submit (we never expect this to complete since
            // we keep increasing the pinLength during pinChanged event)
            // we still handle onComplete nonetheless
            @Override
            public void onCompleted(String pin) {
                handleSubmit(pin);
            }

            @Override
            public void onIncompleteSubmit(String pin) {
                handleSubmit(pin);
            }
        });
    }
    public void onDestroy() {
        super.onDestroy();
        handleSubmit("");
    }
    public void handleSubmit(String pin){
        synchronized (si) {
            si.setPin(pin);
            si.notify();
        }
        finish();
    }
}
