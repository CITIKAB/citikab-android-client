package com.trioangle.gofer.views.signinsignup;


/**
 * @package com.trioangle.gofer
 * @subpackage signin_signup
 * @category SSResetPassword
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.signinsignup.SigninResult;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static com.trioangle.gofer.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;
import static com.trioangle.gofer.utils.Enums.REQ_UPDATE_LOCATION;

/* ************************************************************
   Rider can reset password when forgot the password
   ************************************************************ */
public class SSResetPassword extends AppCompatActivity implements ServiceListener {

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
    public @InjectView(R.id.backArrow)
    ImageView backArrow;
    public @InjectView(R.id.input_password)
    EditText input_password;
    public @InjectView(R.id.input_confirmpassword)
    EditText input_confirmpassword;
    public @InjectView(R.id.input_layout_password)
    TextInputLayout input_layout_password;
    public SigninResult signinResult;
    protected boolean isInternetAvailable;
    private String facebookVerifiedMobileNumberCountryCode="";
    private String facebookKitVerifiedMobileNumber="";

    /**
     * Reset Password next button clicked
     */
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
        setContentView(R.layout.activity_ss_resetpassword);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        getMobileNumerAndCountryCodeFromIntent();

        dialog = commonMethods.getAlertDialog(this);

    }

    private void getMobileNumerAndCountryCodeFromIntent() {
        if (getIntent() != null) {
            facebookKitVerifiedMobileNumber = getIntent().getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY);
            facebookVerifiedMobileNumberCountryCode = getIntent().getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY);
        }


    }


    public void onNext() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        } else {

            if (!validateFirst()) {
                return;
            } else {
                String input_password_str = input_password.getText().toString();
                String input_password_confirmstr = input_confirmpassword.getText().toString();
                if (input_password_str.length() > 5 && input_password_confirmstr.length() > 5 && input_password_confirmstr.equals(input_password_str)) {

                    sessionManager.setPassword(input_password_str);
                    forgotPassword(input_password_str);

                } else {
                    if (!input_password_confirmstr.equals(input_password_str)) {
                        commonMethods.showMessage(this, dialog, getResources().getString(R.string.Passwordmismatch));
                    } else {
                        commonMethods.showMessage(this, dialog, getResources().getString(R.string.InvalidPassword));
                    }
                }
            }
        }

        hideSoftKeyboard();
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
     * Edit text request focus
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
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            onSuccessPwd(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    /**
     * Forgot password API called
     */
    public void forgotPassword(String pwd) {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.forgotpassword(facebookKitVerifiedMobileNumber, facebookVerifiedMobileNumberCountryCode, sessionManager.getType(), pwd, sessionManager.getDeviceType(), sessionManager.getDeviceId()).enqueue(new RequestCallback(this));
    }

    public void onSuccessPwd(JsonResponse jsonResp) {
        signinResult = gson.fromJson(jsonResp.getStrResponse(), SigninResult.class);
        sessionManager.setCurrencySymbol(String.valueOf(Html.fromHtml(signinResult.getCurrencySymbol())));
        sessionManager.setCountryCode(signinResult.getCurrencyCode());
        sessionManager.setWalletAmount(signinResult.getWalletAmount());
        sessionManager.setPaypalMode(signinResult.getPaypalMode());
        sessionManager.setPaypalAppId(signinResult.getPaypalAppId());
        sessionManager.setIsrequest(false);
        sessionManager.setAcesssToken(signinResult.getToken());
        sessionManager.setGoogleMapKey(signinResult.getGoogleMapKey());
        sessionManager.setFacebookAppId(signinResult.getFbId());
        sessionManager.setUserId(signinResult.getUserId());

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
    }


    /**
     * Hide keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
