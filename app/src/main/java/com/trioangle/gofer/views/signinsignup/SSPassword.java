package com.trioangle.gofer.views.signinsignup;
/**
 * @package com.trioangle.gofer
 * @subpackage signin_signup
 * @category SSPassword
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.signinsignup.SigninResult;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.*;
import com.trioangle.gofer.views.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.Enums.REQ_RESEND_OTP;
import static com.trioangle.gofer.utils.Enums.REQ_SIGNUP;

/* ************************************************************
   Sign in and sign up get rider password page
   ************************************************************ */
public class SSPassword extends AppCompatActivity implements ServiceListener {

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

    public @InjectView(R.id.forgorpassword)
    TextView forgorpassword;
    public @InjectView(R.id.input_password)
    EditText input_password;
    public @InjectView(R.id.input_layout_password)
    TextInputLayout input_layout_password;

    public @InjectView(R.id.backArrow)
    ImageView backArrow;
    public SigninResult signinResult;
    protected boolean isInternetAvailable;

    /**
     * Create new account or Signup page called
     */
    @OnClick(R.id.createacount)
    public void createacount() {
        Intent intent = new Intent(getApplicationContext(), SSOTPActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    /**
     * Forgot password click
     */
    @OnClick(R.id.forgorpassword)
    public void forgorpassword() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        } else {
            sessionManager.setIssignin(0);
            FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
            // new checkForgotPassword().execute(url);

        }
    }

    @OnClick(R.id.next)
    public void next() {
        onNext();
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ss_password);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        dialog = commonMethods.getAlertDialog(this);
        final int isSignin = sessionManager.getIssignin();


        if (isSignin == 0) {
            forgorpassword.setVisibility(View.INVISIBLE);
        } else {
            forgorpassword.setVisibility(View.VISIBLE);
        }

    }

    public void onNext() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        int isSignin = sessionManager.getIssignin();
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        } else {
            if (!validateFirst()) {
                return;
            } else {
                String input_password_str = input_password.getText().toString();
                if (input_password_str.length() > 5) {

                    if (isSignin == 0) {


                        sessionManager.setPassword(input_password_str);
                        String firstnamestr = sessionManager.getFirstName();
                        String lastnamestr = sessionManager.getLastName();
                        String passwordstr = sessionManager.getPassword();
                        try {
                            firstnamestr = URLEncoder.encode(firstnamestr, "UTF-8");
                            lastnamestr = URLEncoder.encode(lastnamestr, "UTF-8");
                            passwordstr = URLEncoder.encode(passwordstr, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                        /**
                         *   Register user
                         */
                        register(input_password_str, firstnamestr, lastnamestr);
                    } else {
                        sessionManager.setPassword(input_password_str);
                        String passwordstr = sessionManager.getPassword();
                        try {

                            passwordstr = URLEncoder.encode(passwordstr, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        /**
                         *  Login user
                         */
                        login(passwordstr);
                    }
                    //  url = url.replaceAll(" ", "%20");
                    //new SigninSignup().execute(url);

                } else {
                    commonMethods.showMessage(this, dialog, getString(R.string.InvalidPassword));

                }
            }
        }
    }

    /**
     * Signup API called
     */
        public void login(String passwordstr) {
            commonMethods.showProgressDialog(this, customDialog);
            backArrow.setVisibility(View.GONE);
            apiService.login(sessionManager.getTemporaryPhonenumber(),
                    sessionManager.getTemporaryCountryCode(),
                    sessionManager.getType(), passwordstr, sessionManager.getDeviceType(), sessionManager.getDeviceId(), sessionManager.getLanguageCode()).enqueue(new RequestCallback(REQ_SIGNUP, SSPassword.this));

        }

    public void register(String passwordstr, String firstnamestr, String lastnamestr) {
        commonMethods.showProgressDialog(this, customDialog);
        backArrow.setVisibility(View.GONE);
        HashMap<String, String> locationHashMap;
        locationHashMap = new HashMap<>();
        locationHashMap.put("user_type", sessionManager.getType());
        locationHashMap.put("mobile_number", sessionManager.getTemporaryPhonenumber());
        locationHashMap.put("country_code", sessionManager.getTemporaryCountryCode());
        locationHashMap.put("first_name", firstnamestr);
        locationHashMap.put("last_name", lastnamestr);
        locationHashMap.put("password", passwordstr);
        locationHashMap.put("device_type", sessionManager.getDeviceType());
        locationHashMap.put("device_id", sessionManager.getDeviceId());
        locationHashMap.put("language", sessionManager.getLanguageCode());
        apiService.register(locationHashMap).enqueue(new RequestCallback(REQ_SIGNUP, SSPassword.this));

    }

    /**
     * Validate first name
     */
    private boolean validateFirst() {
        if (input_password.getText().toString().trim().isEmpty()) {
            input_layout_password.setError(getString(R.string.Enteryourpassword));
            requestFocus(input_password);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * reqeust focus
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Back button pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

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
                    onSuccessForget(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    backArrow.setVisibility(View.VISIBLE);
                    commonMethods.hideProgressDialog();
                    if (jsonResp.getStatusMsg().equals("Message sending Failed,please try again..")) {
                        onSuccessForget(jsonResp);
                    }
                    // snackBar(jsonResp.getStatusMsg(), "hi", false, 2);
                }
                break;

            //Register Rider
            /*case REQ_SIGNUP:
                if (jsonResp.isSuccess()) {
                    onSuccessLogin(jsonResp);
                } else {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    DebuggableLogV("jsonResp.getStatusMsg()", "" + jsonResp.getStatusMsg());
                    backArrow.setVisibility(View.VISIBLE);
                }
                break;*/
            default:
                commonMethods.hideProgressDialog();
                backArrow.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
    }

    public void onSuccessForget(JsonResponse jsonResp) {

        sessionManager.setIssignin(0);
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    /*public void onSuccessLogin(JsonResponse jsonResp) {
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
        sessionManager.setCountryCode(sessionManager.getTemporaryCountryCode());
        sessionManager.setPhoneNumber(sessionManager.getTemporaryPhonenumber());
sessionManager.setCountryCode(sessionManager.getTemporaryCountryCode());
        sessionManager.setPhoneNumber(sessionManager.getTemporaryPhonenumber());

        commonMethods.hideProgressDialog();
        backArrow.setVisibility(View.VISIBLE);

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

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);

    }*/

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) { // confirm that this response matches your request
            if (resultCode == ApiSessionAppConstants.FACEBOOK_ACCOUNT_KIT_VERIFACATION_SUCCESS) {
                afterSucceedMobileVerificationUsingFacebookAccountKit();
            }
            // status message
        }
    }

    private void afterSucceedMobileVerificationUsingFacebookAccountKit() {
        Intent intent = new Intent(getApplicationContext(), SSResetPassword.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }


}
