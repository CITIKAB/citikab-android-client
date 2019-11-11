package com.trioangle.gofer.views.signinsignup;
/**
 * @package com.trioangle.gofer
 * @subpackage signin_signup
 * @category SSOTPActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.signinsignup.SigninResult;
import com.trioangle.gofer.helper.Constants;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.Enums.REQ_RESEND_OTP;
import static com.trioangle.gofer.utils.Enums.REQ_SIGNUP;

/* ************************************************************
   Sign in and reset password while enter the OTP page
   ************************************************************ */
public class SSOTPActivity extends AppCompatActivity implements ServiceListener {

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

    public @InjectView(R.id.resend)
    TextView resend;
    public @InjectView(R.id.codetext)
    TextView codetext;
    public @InjectView(R.id.resend_timer)
    TextView resend_timer;
    public @InjectView(R.id.one)
    EditText one;
    public @InjectView(R.id.two)
    EditText two;
    public @InjectView(R.id.three)
    EditText three;
    public @InjectView(R.id.four)
    EditText four;
    public @InjectView(R.id.backArrow)
    ImageView backArrow;
    public CountDownTimer countDownTimer;
    public String otp;
    public String phoneno;
    public int signUpType;
    public String profilemobile;
    public String countrycode;
    public String newphonenum;
    public SigninResult signinResult;
    protected boolean isInternetAvailable;

    @OnClick(R.id.next)
    public void next() {
        /**
         *  OTP validate
         */
        onNext();
    }

    @OnClick(R.id.resend)
    public void resendTimer() {
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        } else {

            if (signUpType == 2) {
                commonMethods.showProgressDialog(SSOTPActivity.this, customDialog);
                apiService.numbervalidation(sessionManager.getPhoneNumber(),
                        sessionManager.getCountryCode(),
                        sessionManager.getType(), sessionManager.getLanguageCode(), "1").enqueue(new RequestCallback(REQ_RESEND_OTP, SSOTPActivity.this));

            } else if(newphonenum!=null&&!newphonenum.equals("")) {
                commonMethods.showProgressDialog(SSOTPActivity.this, customDialog);
                apiService.numbervalidation(newphonenum,
                        sessionManager.getCountryCode(),
                        sessionManager.getType(), sessionManager.getLanguageCode(), "").enqueue(new RequestCallback(REQ_RESEND_OTP, SSOTPActivity.this));

            }else{
                commonMethods.showProgressDialog(SSOTPActivity.this, customDialog);
                apiService.numbervalidation(sessionManager.getPhoneNumber(),
                        sessionManager.getCountryCode(),
                        sessionManager.getType(), sessionManager.getLanguageCode(), "").enqueue(new RequestCallback(REQ_RESEND_OTP, SSOTPActivity.this));
            }
            //new socialSignup().execute(url);
        }
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    public View.OnKeyListener keyListener2 =new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {

                two.requestFocus();
            }
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                    && two.getText().toString().length() == 0) {

                one.requestFocus();
                one.getText().clear();
            }
            return false;
        }
    };
    public View.OnKeyListener keyListener3 =new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {

                three.requestFocus();
            }
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                    && three.getText().toString().length() == 0) {

                two.requestFocus();
                two.getText().clear();
            }
            return false;
        }
    };
    public View.OnKeyListener keyListener4 =new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
                four.requestFocus();
            }
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)
                    && four.getText().toString().length() == 0) {
                three.requestFocus();
                three.getText().clear();
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssotp);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());

        dialog = commonMethods.getAlertDialog(this);
        getIntentVal();


        //TO set  select all in edit text
        setSelectEdit();

        //Action for Backspace on Keypad
        two.setOnKeyListener(keyListener2);
        three.setOnKeyListener(keyListener3);
        four.setOnKeyListener(keyListener4);

        setMsgTxt();


        if(otp!=null&&!otp.equals(""))
        {
            one.setText(otp.substring(0,1).toString());
            two.setText(otp.substring(1,2).toString());
            three.setText(otp.substring(2,3).toString());
            four.setText(otp.substring(3,4).toString());
        }


        /**
         *  Text watcher
         */
        one.addTextChangedListener(new OtpTextWatcher());

        two.addTextChangedListener(new OtpTextWatcher());

        three.addTextChangedListener(new OtpTextWatcher());

        four.addTextChangedListener(new OtpTextWatcher());


    }

    public void setSelectEdit(){
        one.setSelectAllOnFocus(true);
        two.setSelectAllOnFocus(true);
        three.setSelectAllOnFocus(true);
        four.setSelectAllOnFocus(true);

        //Setting the edit text cursor at Start
        int position = one.getSelectionStart();
        int position2 = two.getSelectionStart();
        int position3 = three.getSelectionStart();
        int position4 = four.getSelectionStart();
        one.setSelection(position);
        two.setSelection(position2);
        three.setSelection(position3);
        four.setSelection(position4);
    }

    public void getIntentVal(){
        Intent x = getIntent();
        otp = x.getStringExtra("otp");

        // if the below profilemobile == "profilemobile" it indicated, it called from profile activity to change user phone number
        profilemobile = x.getStringExtra("profilemobile");
        countrycode = x.getStringExtra("countrycode");
        newphonenum = x.getStringExtra("mobilenum");
        signUpType = sessionManager.getSignuptype();
        phoneno = sessionManager.getPhoneNumber();
    }

    private class OtpTextWatcher implements TextWatcher {


        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogI("Gofer", "Textchange");
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogI("Gofer", "Textchange");
            if (one.getText().toString().length() == 1)     //size as per your requirement
            {
                two.requestFocus();
                //one.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
            }
            if (two.getText().toString().length() == 1)     //size as per your requirement
            {
                three.requestFocus();
                //two.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
            }
            if (three.getText().toString().length() == 1)     //size as per your requirement
            {
                four.requestFocus();
                //three.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
            }
        }

        public void afterTextChanged(Editable editable) {
            DebuggableLogI("Gofer", "Textchange");
        }
    }

    public void setMsgTxt(){
        String str1;
        int strmi;
        if (profilemobile != null && profilemobile.equals("profilemobile")) {
            str1 = getString(R.string.enter4digit) + " " + newphonenum;
            strmi = newphonenum.length();
        } else {
            str1 = getString(R.string.enter4digit) + " " + phoneno;
            strmi = phoneno.length();
        }
        int str = str1.length();
        int start = str1.length() - strmi;


        /**
         *  Countdown for otp expire
         */
        countdown();

        final SpannableStringBuilder str3 = new SpannableStringBuilder(str1);
        str3.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, str, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        codetext.setText(str3);


        /**
         *  resend OTP code
         */
        resend.setEnabled(false);
    }

    public void onNext() {
        String emtytextone = one.getText().toString().trim();
        String emtytexttwo = two.getText().toString().trim();
        String emtytextthree = three.getText().toString().trim();
        String emtytextfour = four.getText().toString().trim();


        if (emtytextone.isEmpty() || emtytexttwo.isEmpty() || emtytextthree.isEmpty() || emtytextfour.isEmpty()) {
            commonMethods.showMessage(this, dialog, getString(R.string.otp_emty));
        } else {

            if (!isInternetAvailable) {
                commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
            } else {
                String otpcode = one.getText().toString() + "" +
                        two.getText().toString() + "" +
                        three.getText().toString() + "" +
                        four.getText().toString() + "";

                if (profilemobile != null && profilemobile.equals("profilemobile")) {
                    if (otp.equals(otpcode)) {
                        sessionManager.setProfileDetail("");
                        sessionManager.setPhoneNumber(newphonenum);
                        sessionManager.setCountryCode(countrycode);
                        onBackPressed();
                    } else {
                        commonMethods.showMessage(this, dialog, getString(R.string.otp_mismatch));
                    }
                } else {
                    if (otp.equals(otpcode)) {
                        if (signUpType == 0) {
                            countDownTimer.cancel();
                            Intent intent = new Intent(getApplicationContext(), SignupNameActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                        } else if (signUpType == 1) {
                            signupSocial();
                        } else {
                            countDownTimer.cancel();
                            Intent intent = new Intent(getApplicationContext(), SSResetPassword.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                        }
                    } else {
                        commonMethods.showMessage(this, dialog, getString(R.string.otp_mismatch));
                    }

                }
            }
        }
    }

    public void signupSocial(){
        String profileimage = null;
        String firstnamestr = null;
        String lastnamestr = null;
        try {
            //profileimage = URLEncoder.encode(sessionManager.getProfilepicture(), "UTF-8");
            profileimage = sessionManager.getProfilepicture();

            System.out.println("Fb Profile image Two : "+profileimage);

            firstnamestr = URLEncoder.encode(sessionManager.getFirstName(), "UTF-8");
            lastnamestr = URLEncoder.encode(sessionManager.getLastName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        commonMethods.showProgressDialog(SSOTPActivity.this, customDialog);
        HashMap<String, String> locationHashMap;
        locationHashMap = new HashMap<>();
        locationHashMap.put("new_user", "1");
        locationHashMap.put("user_type", sessionManager.getType());
        locationHashMap.put("mobile_number", sessionManager.getPhoneNumber());
        locationHashMap.put("country_code", sessionManager.getCountryCode());
        locationHashMap.put("first_name", firstnamestr);
        locationHashMap.put("last_name", lastnamestr);
        locationHashMap.put("password", sessionManager.getPassword());
        locationHashMap.put("email_id", sessionManager.getemail());
        locationHashMap.put("user_image", profileimage);
        locationHashMap.put("device_type", sessionManager.getDeviceType());
        locationHashMap.put("device_id", sessionManager.getDeviceId());
        locationHashMap.put("language", sessionManager.getLanguageCode());



        if (sessionManager.getFacebookId().equals("FBID")) {
            locationHashMap.put("google_id", sessionManager.getGoogleId());
        } else {
            locationHashMap.put("fb_id", sessionManager.getFacebookId());
        }
        /**
         *  Signup API called
         */
        apiService.socialsignup(locationHashMap).enqueue(new RequestCallback(REQ_SIGNUP, SSOTPActivity.this));
    }

    /**
     * Back button pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    /**
     * Count down timer for otp exprire
     */
    public void countdown() {
        resend.setEnabled(false);
        resend_timer.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = NumberFormat.getNumberInstance(Locale.US);
                DecimalFormat formatter = (DecimalFormat) f;
                formatter.applyPattern("00");
                resend_timer.setText("00:" + formatter.format(millisUntilFinished / 1000));
            }
            public void onFinish() {
                resend.setEnabled(true);
                resend_timer.setText("00:00");
                resend_timer.setVisibility(View.INVISIBLE);
                //resend_timer.setText("done!");
            }
        }.start();
    }

    /**
     * Social signup API called
     */

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {

            // Resend OTP
            case REQ_RESEND_OTP:
                if (jsonResp.isSuccess()) {
                    if(jsonResp.getStatusMsg().equals("Mobile Number Exist"))
                    {
                        commonMethods.showMessage(this, dialog, getString(R.string.mobilenum));
                    }else {
                        onSuccessResendOTP(jsonResp);
                    }
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())&&jsonResp.getStatusMsg().equals("Message sending Failed,please try again..")) {
                    backArrow.setVisibility(View.VISIBLE);
                    onSuccessResendOTP(jsonResp);
                    //commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    // snackBar(jsonResp.getStatusMsg(), "hi", false, 2);
                }
                break;

            //Register Rider
            case REQ_SIGNUP:
                if (jsonResp.isSuccess()) {
                    onSuccessRegister(jsonResp);
                } else {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    backArrow.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            backArrow.setVisibility(View.VISIBLE);
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            // snackBar(jsonResp.getStatusMsg(), "hi", false, 2);
        }
    }

    public void onSuccessRegister(JsonResponse jsonResp) {

        signinResult = gson.fromJson(jsonResp.getStrResponse(), SigninResult.class);
        sessionManager.setIsrequest(false);
        sessionManager.setCurrencySymbol(String.valueOf(Html.fromHtml(signinResult.getCurrencySymbol())));
        sessionManager.setCurrencyCode(signinResult.getCurrencyCode());
        sessionManager.setAcesssToken(signinResult.getToken());
        sessionManager.setWalletAmount(signinResult.getWalletAmount());
        sessionManager.setPaypalMode(signinResult.getPaypalMode());
        sessionManager.setPaypalAppId(signinResult.getPaypalAppId());
        sessionManager.setGoogleMapKey(signinResult.getGoogleMapKey());
        sessionManager.setFacebookAppId(signinResult.getFbId());
        sessionManager.setUserId(signinResult.getUserId());

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    public void onSuccessResendOTP(JsonResponse jsonResp) {
        countdown();
        try {

            Integer otpin = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, Integer.class);
            otp = String.valueOf(otpin);
        }catch (Exception e){

            String otpST = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, String.class);
            otp = otpST;
        }

        if(otp!=null&&!otp.equals("")){
            one.setText(otp.substring(0,1).toString());
            two.setText(otp.substring(1,2).toString());
            three.setText(otp.substring(2,3).toString());
            four.setText(otp.substring(3,4).toString());
        }



        /*Toast toast = Toast.makeText(getApplicationContext(),
                "Your OTP IS " + otp, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();*/
    }

}
