package com.trioangle.gofer.views.signinsignup;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.signinsignup.SigninResult;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.gofer.views.main.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import static com.trioangle.gofer.utils.CommonKeys.*;

public class SSLoginActivity extends AppCompatActivity implements ServiceListener {

    public AlertDialog dialog;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    Gson gson;
    public @Inject
    CustomDialog customDialog;




    public @InjectView(R.id.input_layout_mobile)
    TextInputLayout input_layout_mobile;
    public @InjectView(R.id.input_layout_passsword)
    TextInputLayout input_layout_passsword;

    public @InjectView(R.id.phone)
    EditText phone;

    public @InjectView(R.id.imgv_login_backarrow)
    ImageView backArrow;


    public @InjectView(R.id.ccp)
    CountryCodePicker ccp;
    public @InjectView(R.id.password_edit)
    EditText password_edit;
    public @InjectView(R.id.sigin_button)
    Button sigin_button;
    public @InjectView(R.id.forgot_password)
    Button forgot_password;

    protected boolean isInternetAvailable;
    public SigninResult signinResult;

    @OnClick(R.id.forgot_password)
    public void forgetPassword() {
        /*Intent intent = new Intent(getApplicationContext(), MobileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);*/
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this,1);
    }

    @OnClick(R.id.imgv_login_backarrow)
    public void backPressed(){
        super.onBackPressed();
    }

    @OnClick(R.id.sigin_button)
    public void signIn() {

        isInternetAvailable = commonMethods.isOnline(this);


        isInternetAvailable = commonMethods.isOnline(this);
        if (isInternetAvailable) {


            if (!validateMobile("check")) {
                input_layout_mobile.setError(getString(R.string.InvalidMobileNumber));
            }
            initLoginApi();
            // new SigninSignup().execute(url);
        } else {
            commonMethods.showMessage(getApplicationContext(), dialog, getResources().getString(R.string.no_connection));

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sslogin);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);
        ccp.setAutoDetectedCountry(true);
        if (Locale.getDefault().getLanguage().equals("fa") || Locale.getDefault().getLanguage().equals("ar")) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
        }


        sigin_button.setEnabled(false);
        password_edit.addTextChangedListener(new NameTextWatcher(password_edit));
        phone.addTextChangedListener(new NameTextWatcher(phone));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT && resultCode == RESULT_OK){
            /*if(resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER){
                commonMethods.showMessage(this, dialog, data.getStringExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY));
            }else if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER){
                openPasswordResetActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY),data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
            }*/
            openPasswordResetActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY),data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
        }


    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (jsonResp.isSuccess()) {
            onSuccessLogin(jsonResp);
        } else {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            backArrow.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }


    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (validatePassword("validate") && validateMobile("validate")) {
                sigin_button.setEnabled(true);
                sigin_button.setBackgroundColor(getResources().getColor(R.color.button_color));
                //sigin_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.driverloginboarderblue));
            } else {

                sigin_button.setEnabled(false);
                sigin_button.setBackgroundColor(getResources().getColor(R.color.button_disable_color));
            }
            if (phone.getText().toString().startsWith("0")) {
                phone.setText("");
            }
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.password_edit:
                    validatePassword("check");
                    break;
                case R.id.phone:
                    validateMobile("check");
                    break;
            }
        }
    }
    private boolean validateMobile(String type) {
        if (phone.getText().toString().trim().isEmpty() || phone.getText().length() < 6) {
            if ("check".equals(type)) {
                //input_layout_mobile.setError(getString(R.string.error_msg_mobilenumber));
                requestFocus(phone);
            }
            return false;
        } else {
            input_layout_mobile.setErrorEnabled(false);
        }


        return true;
    }

    /*
     *   validate password
     */
    private boolean validatePassword(String type) {
        if (password_edit.getText().toString().trim().isEmpty() || password_edit.getText().length() < 6) {
            if ("check".equals(type)) {
                //input_layout_passsword.setError(getString(R.string.error_msg_password));
                requestFocus(password_edit);
            }
            return false;
        } else {
            input_layout_passsword.setErrorEnabled(false);
        }

        return true;
    }

    /*
     *   Validate user name
     */
    /*
     *   focus edit text
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void openPasswordResetActivity(String phoneNumber, String countryCode) {

        Intent signin = new Intent(getApplicationContext(), SSResetPassword.class);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY,phoneNumber);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY,countryCode);
        startActivity(signin);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        finish();

    }


    public void initLoginApi() {


        commonMethods.showProgressDialog(this, customDialog);
        backArrow.setVisibility(View.GONE);
        apiService.login(phone.getText().toString().trim(),
                ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""),
                sessionManager.getType(), password_edit.getText().toString().trim(), sessionManager.getDeviceType(), sessionManager.getDeviceId(), sessionManager.getLanguageCode()).enqueue(new RequestCallback(this));

    }

    public void onSuccessLogin(JsonResponse jsonResp) {
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
        sessionManager.setCountryCode(signinResult.getCountryCode());
        sessionManager.setPhoneNumber(signinResult.getMobileNumber());
        sessionManager.setUserId(signinResult.getUserId());

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

    }

}
