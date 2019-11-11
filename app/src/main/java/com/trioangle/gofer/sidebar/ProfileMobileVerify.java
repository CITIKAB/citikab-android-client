package com.trioangle.gofer.sidebar;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar
 * @category ProfileMobileVerify
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.helper.Constants;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.*;
import com.trioangle.gofer.views.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.gofer.views.customize.CustomDialog;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;

/* ************************************************************
   Edit profile phone number to verfy the mobile number
    *********************************************************** */

public class ProfileMobileVerify extends AppCompatActivity implements ServiceListener {

    public AlertDialog dialog;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    CustomDialog customDialog;

    public @InjectView(R.id.verify)
    Button verify;
    public @InjectView(R.id.ccp)
    CountryCodePicker ccp;
    public @InjectView(R.id.phone)
    EditText phone;

    protected boolean isInternetAvailable;

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @OnClick(R.id.verify)
    public void verify() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prpfile_mobileverification);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

        ccp.setAutoDetectedCountry(true);


        String laydir = getString(R.string.layout_direction);
        if ("1".equals(laydir)) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
            ccp.setCurrentTextGravity(CountryCodePicker.TextGravity.RIGHT);
            ccp.setGravity(Gravity.START);
        }

        /** Setting mobile number depends upon country code**/

        //Text listner
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                DebuggableLogI("Profile", "TextChange");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (phone.getText().toString().startsWith("0")) {
                    phone.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                DebuggableLogI("Profile", "TextChange");
            }
        });

        /**
         *  Verify button clicked
         */
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isInternetAvailable = commonMethods.isOnline(getApplicationContext());
                if (!isInternetAvailable) {
                    //snackBar(getResources().getString(R.string.no_connection),getResources().getString(R.string.go_online),false,2);
                    commonMethods.showMessage(ProfileMobileVerify.this, dialog, getResources().getString(R.string.no_connection));
                } else {
                    String phonestr = phone.getText().toString();
                    if (phonestr.length() > 5) {
                        verify.setEnabled(false);
                        checkPhoneNumber(phonestr);
                    } else {
                        commonMethods.showMessage(ProfileMobileVerify.this, dialog, getResources().getString(R.string.InvalidMobileNumber));
                        //snackBar(getResources().getString(R.string.InvalidMobileNumber), "hi", false, 2);
                    }
                }
            }
        });

    }

    /**
     * Check phone number
     */
    public void checkPhoneNumber(String phonestr) {
        commonMethods.showProgressDialog(this, customDialog);
        sessionManager.setCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
        sessionManager.setPhoneNumber(phonestr);



        apiService.numbervalidation(phonestr, ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""), sessionManager.getType(), sessionManager.getLanguageCode(), "").enqueue(new RequestCallback(this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        verify.setEnabled(true);
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            onSuccessMob(jsonResp);

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            // commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            commonMethods.hideProgressDialog();

            if (jsonResp.getStatusMsg().equals("Message sending Failed,please try again..")) {
                Integer otp = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, Integer.class);
                
                sessionManager.setSignuptype(0);
                sessionManager.setIssignin(0);
                sessionManager.setTemporaryPhonenumber(phone.getText().toString());
                sessionManager.setTemporaryCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));

                FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
                /*Intent intent = new Intent(getApplicationContext(),SSOTPActivity.class);
                intent.putExtra("otp",otp.toString());
                intent.putExtra("profilemobile","profilemobile");

                intent.putExtra("mobilenum",phone.getText().toString());
                intent.putExtra("countrycode",ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
                startActivity(intent);*/

                /*sessionManager.setProfileDetail("");
                        sessionManager.setPhoneNumber(newphonenum);
                        sessionManager.setCountryCode(countrycode);
                * */
                //overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);

            } else {
                commonMethods.hideProgressDialog();
                commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());

            }
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg()))
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        commonMethods.hideProgressDialog();
    }

    public void onSuccessMob(JsonResponse jsonResp) {

        String statusCode = jsonResp.getStatusCode();
        if (statusCode.matches("1")) {
            verify.setEnabled(true);
            sessionManager.setTemporaryPhonenumber(phone.getText().toString());
            sessionManager.setTemporaryCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));

            FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
            /*String otp = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, String.class);
            
            sessionManager.setSignuptype(0);
            sessionManager.setIssignin(0);
            Intent intent = new Intent(getApplicationContext(),SSOTPActivity.class);
            intent.putExtra("otp",otp);
            intent.putExtra("profilemobile","profilemobile");
            intent.putExtra("mobilenum",phone.getText().toString());
            intent.putExtra("countrycode",ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);*/


        } else if (statusCode.matches("2")) {
           /* verify.setEnabled(true);
            sessionManager.setIssignin(1);
            Intent intent = new Intent(getApplicationContext(), ProfileMobileVerify.class);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);*/
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());

        }
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) { // confirm that this response matches your request
            if (resultCode == ApiSessionAppConstants.FACEBOOK_ACCOUNT_KIT_VERIFACATION_SUCCESS) {
                sessionManager.setPhonenumberEdited(true);
                this.finish();
            }
        }
    }



}