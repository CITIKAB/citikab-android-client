package com.trioangle.gofer.views.signinsignup;
/**
 * @package com.trioangle.gofer
 * @subpackage signin_signup
 * @category SSRegisterActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.signinsignup.SigninResult;
import com.trioangle.gofer.helper.Constants;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.*;
import com.trioangle.gofer.views.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.gofer.views.customize.CustomDialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.trioangle.gofer.views.main.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;

import static com.trioangle.gofer.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static com.trioangle.gofer.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.Enums.REQ_SIGNUP;
//import static com.trioangle.gofer.utils.Enums.REQ_VALIDATE_NUMBER;

/* ************************************************************
   Sign in and sign up using social media (FaceBook and Google plus)
   ************************************************************ */
public class SSRegisterActivity extends AppCompatActivity implements ServiceListener {

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

    public @InjectView(R.id.input_first)
    EditText input_first;
    public @InjectView(R.id.input_last)
    EditText input_last;
    public @InjectView(R.id.input_email)
    EditText input_email;
    public @InjectView(R.id.input_password)
    EditText input_password;
    public @InjectView(R.id.input_phone)
    EditText input_phone;
    public @InjectView(R.id.input_layout_first)
    TextInputLayout input_layout_first;
    public @InjectView(R.id.input_layout_last)
    TextInputLayout input_layout_last;
    public @InjectView(R.id.input_layout_email)
    TextInputLayout input_layout_email;
    public @InjectView(R.id.input_layout_password)
    TextInputLayout input_layout_password;
    public @InjectView(R.id.input_layout_phone)
    TextInputLayout input_layout_phone;
    public @InjectView(R.id.signinterms)
    TextView signinterms;
    @InjectView(R.id.ccp)
    public CountryCodePicker ccp;

    @InjectView(R.id.backArrow)
    public ImageView backArrow;

    public String firstname;
    public String lastname;
    public String email;
    public String fbid;
    public String gpid;
    public String profile;
    public String Termpolicy;

    protected boolean isInternetAvailable;
    public SigninResult signinResult;
    private String facebookKitVerifiedMobileNumber="",facebookVerifiedMobileNumberCountryCode="";


    /**
     * Check is email validation
     */
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @OnClick(R.id.socialdetailnext)
    public void next() {
        submitForm();
    }

    @OnClick(R.id.socialdetailback)
    public void back() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sssocial_details);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        getMobileNumerAndCountryCodeFromIntent();
        dialog = commonMethods.getAlertDialog(this);
        setLocale();

        firstname = sessionManager.getFirstName();
        lastname = sessionManager.getLastName();
        email = sessionManager.getemail();
        profile = sessionManager.getProfilepicture();
        fbid = sessionManager.getFacebookId();
        gpid = sessionManager.getGoogleId();

        input_layout_phone.setHint(getResources().getString(R.string.mobile_s));
        input_layout_password.setHint(getResources().getString(R.string.password));
        input_layout_email.setHint(getResources().getString(R.string.email_s));
        input_layout_first.setHint(getResources().getString(R.string.first));
        input_layout_last.setHint(getResources().getString(R.string.last));


        input_first.setHint(getResources().getString(R.string.first));

        input_first.setText(firstname);
        input_last.setText(lastname);
        input_email.setText(email);


        Termpolicy = CommonKeys.termPolicyUrl;//Demo Url for Terms and policy

        /** Setting mobile number depends upon country code**/

       /* PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(ccp.getSelectedCountryCode()));


        String  exampleNumber= String.valueOf(phoneNumberUtil.getExampleNumber(isoCode).getNationalNumber());

        int phoneLength=exampleNumber.length();
        input_phone.setFilters( new InputFilter[] {
                new InputFilter.LengthFilter(phoneLength)});*/
        //Text listner
        /*input_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                DebuggableLogI("Gofer", "Textchange");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (input_phone.getText().toString().startsWith("0")) {
                    input_phone.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                DebuggableLogI("Gofer", "Textchange");
            }
        });*/


        /**
         *  Custom text view for sign up link
         */
        customTextView(signinterms);

        String laydir = getString(R.string.layout_direction);
        if ("1".equals(laydir)) {
            ccp.changeDefaultLanguage(CountryCodePicker.Language.ARABIC);
            ccp.setCurrentTextGravity(CountryCodePicker.TextGravity.RIGHT);
            ccp.setGravity(Gravity.START);
        }

        input_first.addTextChangedListener(new NameTextWatcher(input_first));
        input_last.addTextChangedListener(new NameTextWatcher(input_last));
        input_email.addTextChangedListener(new NameTextWatcher(input_email));
        //input_phone.addTextChangedListener(new NameTextWatcher(input_phone));
        input_password.addTextChangedListener(new NameTextWatcher(input_password));
    }

    /**
     * Validating form
     */

    public void setLocale() {
        String lang = sessionManager.getLanguage();

        if (!lang.equals("")) {

            String langC = sessionManager.getLanguageCode();
            Locale locale = new Locale(langC);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics());
            //recreate();
            DebuggableLogV("locale", "localesetted " + locale);
        } else {
            sessionManager.setLanguage("English");
            sessionManager.setLanguageCode("en");
            setLocale();
            recreate();
            //Intent intent = new Intent(SigninSignupActivity.this, SigninSignupActivity.class);
            Intent intent = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }


    }

    private void submitForm() {

        if (!validateFirst()) {
            return;
        }
        if (!validateLast()) {
            return;
        }
        if (!validateEmail()) {
            input_layout_email.setError(getString(R.string.error_msg_email));
            return;
        }
        if (!validatePassword()) {
            input_layout_password.setError(getString(R.string.error_msg_password));
            return;
        }
        /*if (!validatePhone()) {
            input_layout_phone.setError(getString(R.string.error_msg_phone));
            return;
        }*/

        /*sessionManager.setFirstName(input_first.getText().toString());
        sessionManager.setLastName(input_last.getText().toString());
        sessionManager.setEmail(input_email.getText().toString());
        sessionManager.setPassword(input_password.getText().toString());
        sessionManager.setCountryCode(ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""));
        sessionManager.setTemporaryPhonenumber(input_phone.getText().toString());*/

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        } else {
            registerWithType();
        }

    }




    /**
     * validate first name
     */
    private boolean validateFirst() {
        if (input_first.getText().toString().trim().isEmpty()) {
            input_layout_first.setError(getString(R.string.error_msg_firstname));
            requestFocus(input_first);
            return false;
        } else {
            input_layout_first.setErrorEnabled(false);
        }

        return true;
    }

    private void getMobileNumerAndCountryCodeFromIntent() {


        if (getIntent() != null) {
            facebookKitVerifiedMobileNumber = getIntent().getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY);
            facebookVerifiedMobileNumberCountryCode = getIntent().getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY);

            input_phone.setText(facebookKitVerifiedMobileNumber);
            input_phone.setEnabled(false);

            ccp.setCountryForPhoneCode(Integer.parseInt(facebookVerifiedMobileNumberCountryCode));
            ccp.setCcpClickable(false);
        }


    }
    /**
     * validate last name
     */

    private boolean validateLast() {
        if (input_last.getText().toString().trim().isEmpty()) {
            input_layout_last.setError(getString(R.string.error_msg_lastname));
            requestFocus(input_last);
            return false;
        } else {
            input_layout_last.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * validate email
     */

    private boolean validateEmail() {
        String email = input_email.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            //  input_layout_email.setError(getString(R.string.error_msg_email));
            requestFocus(input_email);
            return false;
        } else {
            input_layout_email.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * validate phone number
     */
    private boolean validatePhone() {
        if (input_phone.getText().toString().trim().isEmpty() || input_phone.getText().toString().length() < 6) {
            // input_layout_phone.setError(getString(R.string.error_msg_phone));
            //requestFocus(input_phone);
            return false;
        } else {
            input_layout_phone.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * validate password
     */
    private boolean validatePassword() {
        if (input_password.getText().toString().trim().isEmpty() || input_password.getText().toString().length() < 5) {
            //input_layout_password.setError(getString(R.string.error_msg_password));
            requestFocus(input_password);
            return false;
        } else {
            input_layout_password.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Change request focus
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        switch (jsonResp.getRequestCode()){
            /*case REQ_VALIDATE_NUMBER:{
                if (jsonResp.isSuccess()) {
                    onSuccessMob(jsonResp);

                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    // commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    backArrow.setVisibility(View.VISIBLE);
                    if (jsonResp.getStatusMsg().equals(ApiSessionAppConstants.NEW_USER_MOBILE_NUMBER_MESSAGE_SEND_FAILED_USED_IN_DEMOS)) {
                        Integer otp = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, Integer.class);

                        sessionManager.setSignuptype(1);
                        sessionManager.setIssignin(0);
                        callFacebookAccountKitForPhoneNumberVerification();
                *//*Intent intent = new Intent(getApplicationContext(), SSOTPActivity.class);
                intent.putExtra("otp", Integer.toString(otp));
                startActivity(intent);
                overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);*//*
                    } else {
                        commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    }
                }
                break;
            }*/
            case REQ_SIGNUP:{
                commonMethods.hideProgressDialog();
                clearSocialCredintials();
                if (jsonResp.isSuccess()) {
                    onSuccessRegister(jsonResp);
                } else {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    backArrow.setVisibility(View.VISIBLE);
                }
                break;

            }
        }

    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg()))
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
    }

    public void onSuccessMob(JsonResponse jsonResp) {
        String statusCode = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.STATUS_CODE, String.class);
        if (statusCode.matches(ApiSessionAppConstants.NEW_USER)) {
            backArrow.setVisibility(View.VISIBLE);

            String otp = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), Constants.OTP, String.class);
            
            sessionManager.setSignuptype(1);
            sessionManager.setIssignin(0);
            // old OTP verification process
            /*Intent intent = new Intent(getApplicationContext(), SSOTPActivity.class);
            intent.putExtra("otp", otp);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);*/

            //new method for OTP verification -> Facebook Account kit
            callFacebookAccountKitForPhoneNumberVerification();

        } else if (statusCode.matches(ApiSessionAppConstants.OLD_USER)) {

            backArrow.setVisibility(View.VISIBLE);
            sessionManager.setIssignin(1);
            commonMethods.showMessage(this, dialog, getString(R.string.number_exists));

            /*
            Intent intent = new Intent(getApplicationContext(), SSPassword.class);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);*/

        }
    }

    /**
     * Custom text view to link
     */
    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                getResources().getString(R.string.sigin_terms1));
        spanTxt.append(getResources().getString(R.string.sigin_terms2));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String url = Termpolicy + "terms_of_service";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        }, spanTxt.length() - getResources().getString(R.string.sigin_terms2).length(), spanTxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanTxt.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_text_color)), spanTxt.length() - getResources().getString(R.string.sigin_terms2).length(), spanTxt.length(), 0);
        spanTxt.append(getResources().getString(R.string.sigin_terms3));
        spanTxt.append(getResources().getString(R.string.sigin_terms4));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String url = Termpolicy + "privacy_policy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }, spanTxt.length() - getResources().getString(R.string.sigin_terms4).length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_text_color)), spanTxt.length() - getResources().getString(R.string.sigin_terms4).length(), spanTxt.length(), 0);
        spanTxt.append(".");
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    /**
     * Check phone number
     */
    /*public void checkPhoneNumber() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.numbervalidation(sessionManager.getTemporaryPhonenumber(),
                sessionManager.getCountryCode(),
                sessionManager.getType(), sessionManager.getLanguageCode(), "").enqueue(new RequestCallback(REQ_VALIDATE_NUMBER,this));

    }*/


    /**
     * Back button pressed
     */
    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        final BottomSheetDialog dialog = new BottomSheetDialog(SSRegisterActivity.this);
        dialog.setContentView(R.layout.dialogsignup_cancel);

        Button confirm = (Button) dialog.findViewById(R.id.signup_cancel_confirm);
        Button cancel = (Button) dialog.findViewById(R.id.signup_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
            }
        });
        dialog.show();
    }


    /**
     * Text watcher for validate
     */
    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogI("Gofer", "Textchange");
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogI("Gofer", "Textchange");
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_first:
                    validateFirst();
                    break;
                case R.id.input_last:
                    validateLast();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                /*case R.id.input_phone:
                    validatePhone();
                    break;*/
                default:
                    break;
            }
        }
    }

    private void callFacebookAccountKitForPhoneNumberVerification(){
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
    }

    private void registerWithType() {
        CommonMethods.DebuggableLogI("google id",sessionManager.getGoogleId()+"test");
        CommonMethods.DebuggableLogI("fb id",sessionManager.getFacebookId()+"test");
        if(!sessionManager.getGoogleId().equals("") && !sessionManager.getFacebookId().equals("")){
            CommonMethods.DebuggableLogI("register type","Social");
socialSignup();

        }else{
            CommonMethods.DebuggableLogI("register type","normal");
            normalSignup();
        }
    }

    private void normalSignup() {

        commonMethods.showProgressDialog(this, customDialog);
        HashMap<String, String> locationHashMap;
        locationHashMap = new HashMap<>();
        locationHashMap.put("new_user", "0");
        locationHashMap.put("user_type", sessionManager.getType());
        locationHashMap.put("mobile_number", facebookKitVerifiedMobileNumber);
        locationHashMap.put("country_code", facebookVerifiedMobileNumberCountryCode);
        locationHashMap.put("first_name", input_first.getText().toString());
        locationHashMap.put("last_name", input_last.getText().toString());
        locationHashMap.put("password", input_password.getText().toString());
        locationHashMap.put("email_id", input_email.getText().toString());
        locationHashMap.put("device_type", sessionManager.getDeviceType());
        locationHashMap.put("device_id", sessionManager.getDeviceId());
        locationHashMap.put("language", sessionManager.getLanguageCode());

        /**
         *  Signup API called
         */
        //apiService.socialsignup(locationHashMap).enqueue(new RequestCallback(REQ_SIGNUP, this));
        apiService.register(locationHashMap).enqueue(new RequestCallback(REQ_SIGNUP, this));
    }


    public void socialSignup(){



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
        commonMethods.showProgressDialog(this, customDialog);
        HashMap<String, String> locationHashMap;
        locationHashMap = new HashMap<>();
        locationHashMap.put("new_user", "1");
        locationHashMap.put("user_type", sessionManager.getType());
        locationHashMap.put("mobile_number", facebookKitVerifiedMobileNumber);
        locationHashMap.put("country_code", facebookVerifiedMobileNumberCountryCode);
        locationHashMap.put("first_name", input_first.getText().toString());
        locationHashMap.put("last_name", input_last.getText().toString());
        locationHashMap.put("password", input_password.getText().toString());
        locationHashMap.put("email_id", input_email.getText().toString());
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
        //apiService.socialsignup(locationHashMap).enqueue(new RequestCallback(REQ_SIGNUP, this));
        apiService.register(locationHashMap).enqueue(new RequestCallback(REQ_SIGNUP, this));


    }
    public void clearSocialCredintials(){
        sessionManager.setFacebookId("");
        sessionManager.setGoogleId("");
        sessionManager.setFirstName("");
        sessionManager.setLastName("");
        sessionManager.setEmail("");
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
        sessionManager.setCountryCode(sessionManager.getTemporaryCountryCode());
        sessionManager.setPhoneNumber(sessionManager.getTemporaryPhonenumber());


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }


}
