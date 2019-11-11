package com.trioangle.gofer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.widget.Toast;
import com.facebook.accountkit.*;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;

import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.R;
import com.trioangle.gofer.views.signinsignup.SignupNameActivity;



public class FacebookAccountKitHelper {



    public static void openFacebookAccountKitActivity(Activity activity, SessionManager sessionManager){
        final Intent intent = new Intent(activity, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        PhoneNumber phoneNumber = new PhoneNumber(sessionManager.getCountryCode(),sessionManager.getTemporaryPhonenumber());
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.setInitialPhoneNumber(phoneNumber).build());

        activity.startActivityForResult(intent, CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT);
        activity.overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }


    public static void  setFacebookAccountKitPhonenumberInSession(SessionManager sessionManager) {
        final String returnablePhoneNumber;
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                String accountKitId, phoneNumbers, countryCode;

                // Get Account Kit ID
                accountKitId = account.getId();

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                phoneNumbers = phoneNumber.getPhoneNumber().toString();

                //verifyPhoneNumber(phoneNumbers,accountKitId);
                //sessionManager.setFacebookAccountKitPhonenumberInSession(phoneNumbers.replace("+", ""));





                //String email = account.getEmail();


            }

            @Override
            public void onError(final AccountKitError error) {
                Log.e("AccountKit", error.toString());
                // Handle Error
            }
        });
    }

    public static void  setFacebookAccountKitPhonenumberAsNewPhoneNumber(SessionManager sessionManager) {
        final String returnablePhoneNumber;
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                String accountKitId, phoneNumbers, countryCode;

                // Get Account Kit ID
                accountKitId = account.getId();

                // Get phone number
                PhoneNumber phoneNumber = account.getPhoneNumber();
                phoneNumbers = phoneNumber.getPhoneNumber().toString();

                //verifyPhoneNumber(phoneNumbers,accountKitId);
                sessionManager.setPhoneNumber(phoneNumbers.replace("+", ""));





                //String email = account.getEmail();


            }

            @Override
            public void onError(final AccountKitError error) {
                Log.e("AccountKit", error.toString());
                // Handle Error
            }
        });
    }

    public static Boolean getFacebookAccountKitStatus(Intent data, SessionManager sessionManager){
        AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
        String toastMessage;
        if (loginResult.getError() != null) {
            toastMessage = loginResult.getError().getErrorType().getMessage();
            // showErrorActivity(loginResult.getError());
            return false;
        } else if (loginResult.wasCancelled()) {
            toastMessage = "Login Cancelled";
            return false;
        } else {
            //setFacebookAccountKitPhonenumberInSession(sessionManager);
            return true;
        }

        // Surface the result to your user in an appropriate way.
        /*Toast.makeText(
                this,
                toastMessage,
                Toast.LENGTH_LONG)
                .show();
        CommonMethods.DebuggableLogI("facebook account kit status", toastMessage);*/
    }

    public static void openSignupNameActivityAfterSucceedMobileVerificationUsingFacebookAccountKit(Context mContext) {
        Intent intent = new Intent(mContext, SignupNameActivity.class);
        mContext.startActivity(intent);
    }
}
