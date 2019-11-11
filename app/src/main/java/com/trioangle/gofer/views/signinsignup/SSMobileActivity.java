package com.trioangle.gofer.views.signinsignup;
/**
 * @package com.trioangle.gofer
 * @subpackage signin_signup
 * @category SSMobileActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
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

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;


/* ************************************************************
   Sign in and sign up using mobile number
   ************************************************************ */

public class SSMobileActivity extends AppCompatActivity implements ServiceListener {

    public AlertDialog dialog;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    CustomDialog customDialog;

    public @InjectView(R.id.ccp)
    CountryCodePicker ccp;
    public @InjectView(R.id.back)
    RelativeLayout back;
    public @InjectView(R.id.mobilelayout)
    LinearLayout mobilelayout;
    public @InjectView(R.id.entermobileno)
    TextView entermobileno;
    public @InjectView(R.id.phone)
    EditText phone;
    /*public @InjectView(R.id.progressBar)
    ProgressBar progressBar;*/
    public @InjectView(R.id.backArrow)
    ImageView backArrow;
    protected boolean isInternetAvailable;

    //Phone button to cursor visible
    @OnTouch(R.id.phone)
    public boolean phon() {
        phone.setCursorVisible(true);
        return false;
    }

    @OnClick(R.id.next)
    public void next() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        } else {
            String phonestr = phone.getText().toString();
            if (phonestr.length() > 5) {
                sessionManager.setTemporaryPhonenumber(phone.getText().toString());
                sessionManager.setTemporaryCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
                checkEmail();
            } else {
                commonMethods.showMessage(this, dialog, getString(R.string.InvalidMobileNumber));
            }
        }
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilenumber);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);

        String laydir = getString(R.string.layout_direction);
        if ("1".equals(laydir)) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
            ccp.setCurrentTextGravity(CountryCodePicker.TextGravity.RIGHT);
            ccp.setGravity(Gravity.START);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(600);
            getWindow().getSharedElementReturnTransition().setDuration(600)
                    .setInterpolator(new DecelerateInterpolator());
            entermobileno.setTransitionName("mobilenumber");
            mobilelayout.setTransitionName("mobilelayout");
        }



        /*Text  Watcher for Phone edit text **/

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                DebuggableLogV("Gofer", "onTextChange");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DebuggableLogV("Gofer", "onTextChange");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (phone.getText().toString().startsWith("0")) {
                    phone.setText("");
                }
            }
        });

    }


    /**
     * Check email address
     */
    public void checkEmail() {
        commonMethods.showProgressDialog(this, customDialog);

        apiService.numbervalidation(phone.getText().toString(),
                ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""),
                sessionManager.getType(), sessionManager.getLanguageCode(), "").enqueue(new RequestCallback(this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();

        if (jsonResp.isSuccess()) {
            onSuccessMob(jsonResp);

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            // commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            backArrow.setVisibility(View.VISIBLE);
            if (jsonResp.getStatusMsg().equals(ApiSessionAppConstants.NEW_USER_MOBILE_NUMBER_MESSAGE_SEND_FAILED_USED_IN_DEMOS)) {
                Integer otp = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, Integer.class);

                sessionManager.setSignuptype(0);
                sessionManager.setIssignin(0);
                /*Intent intent = new Intent(getApplicationContext(), SSOTPActivity.class);
                intent.putExtra("otp", otp.toString());
                startActivity(intent);*/
                FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
                overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
            } else {
                commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            }
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        backArrow.setVisibility(View.VISIBLE);
    }

    public void onSuccessMob(JsonResponse jsonResp) {

        /*
         * code 1 signup -> new user
         * code 2 signin -> Already available user*/
        String statusCode = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.STATUS_CODE, String.class);
        if (statusCode.matches(ApiSessionAppConstants.NEW_USER)) {
            backArrow.setVisibility(View.VISIBLE);

            String otp = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, String.class);

            sessionManager.setSignuptype(0);
            sessionManager.setIssignin(0);
            FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
            /*Intent intent = new Intent(getApplicationContext(), SSOTPActivity.class);
            intent.putExtra("otp", otp);
            startActivity(intent);*/


        } else if (statusCode.matches(ApiSessionAppConstants.OLD_USER)) {

            backArrow.setVisibility(View.VISIBLE);
            sessionManager.setIssignin(1);
            Intent intent = new Intent(getApplicationContext(), SSPassword.class);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);

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
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommonMethods.DebuggableLogI("request code",String.valueOf(requestCode));
        CommonMethods.DebuggableLogI("result code",String.valueOf(resultCode));
        if (requestCode == CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT) { // confirm that this response matches your request
            if (resultCode == ApiSessionAppConstants.FACEBOOK_ACCOUNT_KIT_VERIFACATION_SUCCESS) {
                afterSucceedMobileVerificationUsingFacebookAccountKit();
            }
            // status message
        }
    }

    private void afterSucceedMobileVerificationUsingFacebookAccountKit() {
        Intent intent = new Intent(this, SignupNameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

}
