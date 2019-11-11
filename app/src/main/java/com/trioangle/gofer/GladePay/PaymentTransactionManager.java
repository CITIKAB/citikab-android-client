package com.trioangle.gofer.GladePay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.trioangle.gofer.BuildConfig;
import com.trioangle.gofer.GladePay.apiui.AuthUrlWebView;
import com.trioangle.gofer.GladePay.apiui.AuthurlSingleton;
import com.trioangle.gofer.GladePay.apiui.OtpActivity;
import com.trioangle.gofer.GladePay.apiui.OtpSingleton;
import com.trioangle.gofer.GladePay.apiui.PINActivity;
import com.trioangle.gofer.GladePay.apiui.PinSingleton;
import com.trioangle.gofer.GladePay.exceptions.ProcessingException;
import com.trioangle.gofer.GladePay.request.CardChargeRequestBody;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.main.RiderProfile;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import butterknife.InjectView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.trioangle.gofer.utils.CommonKeys.card_token;
import static com.trioangle.gofer.utils.CommonKeys.isPaymentwithToken;
import static com.trioangle.gofer.utils.CommonKeys.gladepaycard_tokenusingAuth_pin;
import static com.trioangle.gofer.utils.CommonKeys.last4;

public class PaymentTransactionManager {

    private static final String LOG_TAG = PaymentTransactionManager.class.getSimpleName();
    private static boolean PROCESSING = false;
    private static String cardToken = null;
    private final Charge charge;
    private final Activity activity;
    private final PTransaction paymentTransaction;
    private final Gladepay.TransactionCallback transactionCallback;
    private GladepayApiService apiService;
    private final OtpSingleton osi = OtpSingleton.getInstance();
    private final PinSingleton psi = PinSingleton.getInstance();
    private final AuthurlSingleton asi = AuthurlSingleton.getInstance();
    public AlertDialog dialog1;
    public @Inject
    SessionManager sessionManager;
    InputStreamReader readStream;
    public @Inject
    Gson gson;
   private String messageError="";
   private String txnRef;
   private String auth_url;
  private   String auth_type="",apply_auth="",otp_message="";
    Card card;
    public @Inject
    CommonMethods commonMethods;

    private final Callback<JsonObject> serverCallback = new Callback<JsonObject>() {
        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            handleGladepayApiResponse(response);
        }
        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            Log.e(LOG_TAG, t.getMessage());
            notifyProcessingError(t);
        }
    };
    private CardChargeRequestBody chargeRequestBody;
    void chargeCard() {
       /* System.out.println("getCard"+charge.getCard());
        System.out.println("isValid getCard"+charge.getCard().isValid());*/
        try {
            System.out.println("isPaymentwithToken"+isPaymentwithToken);
            System.out.println("card number"+last4);
            initiateTransaction();
            if(isPaymentwithToken)
            {
                PayemntWithToken();
                return;
            }
            System.out.println("getCard"+charge.getCard());
            System.out.println("isValid getCard"+charge.getCard().isValid(activity.getApplicationContext()));
            if (charge.getCard() != null && charge.getCard().isValid(activity.getApplicationContext())) {
                sendChargeRequestToServer();
            } else{
                Toast.makeText(this.activity.getApplicationContext(), "Please Enter Valid Card Details", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ce) {
            Log.e(LOG_TAG, ce.getMessage(), ce);
            if (!(ce instanceof ProcessingException)) {
                turnOffProcessing();
            }
            transactionCallback.onError(ce, paymentTransaction);
        }
    }
    PaymentTransactionManager(Activity activity, Charge charge, Gladepay.TransactionCallback transactionCallback) {
        AppController.getAppComponent().inject(this);
        gladepaycard_tokenusingAuth_pin="";
        if (BuildConfig.DEBUG && (activity == null)) {
            throw new AssertionError("please activity cannot be null");
        }
        if (BuildConfig.DEBUG && (charge == null)) {
            throw new AssertionError("charge must not be null");
        }
        System.out.println("isPaymentwithToken"+isPaymentwithToken);
      /*  if(!isPaymentwithToken)
        {*/
            if (BuildConfig.DEBUG && (charge.getCard() == null)) {
                throw new AssertionError("please add a card to the charge before calling chargeCard");
            }
      /*  }*/
        if (BuildConfig.DEBUG && (transactionCallback == null)) {
            throw new AssertionError("transactionCallback must not be null");
        }

        this.activity = activity;
        this.charge = charge;
        this.transactionCallback = transactionCallback;
        this.paymentTransaction = new PTransaction();
    }
    private void sendChargeRequestToServer() {
        try {
            initiateChargeOnServer();
        }catch (Exception ce) {
            Log.e(LOG_TAG, ce.getMessage(), ce);
            notifyProcessingError(ce);
        }
    }

    private void initiateTransaction() throws ProcessingException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        if (PaymentTransactionManager.PROCESSING) {
            Toast.makeText(activity, "A transaction is currently being processed, please wait till it is completed before attempting a new charge.", Toast.LENGTH_SHORT).show();
            throw new ProcessingException();
        }
        turnOnProcessing();
        apiService = new ApiClient( GladepaySdk.getMerchantId(), GladepaySdk.getMerchantKey(), GladepaySdk.getLiveStatus()).getGladepayApiService();
        chargeRequestBody = new CardChargeRequestBody(charge);
        tLog("INIt transaction");
    }
    private void turnOnProcessing() {
        PaymentTransactionManager.PROCESSING = true;
    }

    private void turnOffProcessing() {
        PaymentTransactionManager.PROCESSING = false;
    }

    private void notifyProcessingError(Throwable t) {
        tLog("Error "+t.getMessage());
        turnOffProcessing();
        transactionCallback.onError(t, paymentTransaction);
    }
    private void initiateChargeOnServer(){
        if(commonMethods.isOnline(activity)) {
            tLog("InitiateChargeOnServer ChargeRequestServer");
            Call<JsonObject> call = apiService.initiateTransactions(chargeRequestBody.getInitiateParamsJsonObjects());
            call.enqueue(serverCallback);
        }else {
            commonMethods.showMessage(activity.getApplicationContext(), dialog1, activity.getApplicationContext().getString(R.string.no_connection));
        }

    }
    private void validateTransactionServer(String otp){
        if(commonMethods.isOnline(activity)) {
            tLog("validateTransactionServer ValidateRequestServer");
            Call<JsonObject> call = apiService.validateTransaction(chargeRequestBody.getParamsJsonObjects("validate", paymentTransaction.getTransactionRef(), otp));
            call.enqueue(serverCallback);
        }else {
            commonMethods.showMessage(activity.getApplicationContext(), dialog1, activity.getApplicationContext().getString(R.string.no_connection));
        }
    }
    private void initiateChargeOnServer(String txnRef,String pin)
    {
        if(commonMethods.isOnline(activity)) {
            tLog("InitiateChargeOnServer ChargeRequestServer");
            Call<JsonObject> call = apiService.initiateTransactions(chargeRequestBody.getParamsJsonObjects(txnRef,pin));
            call.enqueue(serverCallback);
        }else {
            commonMethods.showMessage(activity.getApplicationContext(), dialog1, activity.getApplicationContext().getString(R.string.no_connection));
        }
    }
    private void requeryAndverifyTransactionServer(){
        if(commonMethods.isOnline(activity)) {
            tLog("InitiateChargeOnServer requeryAndVerifyTransactionServer");
            Call<JsonObject> call = apiService.validateTransaction(chargeRequestBody.getParamsJsonObjects("verify", paymentTransaction.getTransactionRef(), null));
            call.enqueue(serverCallback);
            requeryCount++;
        } else {
            commonMethods.showMessage(activity.getApplicationContext(), dialog1, activity.getApplicationContext().getString(R.string.no_connection));
        }
    }
    private void PayemntWithToken(){
        if(commonMethods.isOnline(activity))
        {
            tLog("PayemntWithToken");
            Call<JsonObject> call = apiService.validateTransaction(chargeRequestBody.getParamsJsonObjects(card_token));
            call.enqueue(serverCallback);
        }
        else {
            commonMethods.showMessage(activity.getApplicationContext(), dialog1, activity.getApplicationContext().getString(R.string.no_connection));
        }
    }
    private int requeryCount = 0;

    private void handleGladepayApiResponse(Response<JsonObject> response) {
        tLog("handleGladepayApiResponse"+response.body());

        this.paymentTransaction.loadFromResponse(response.body());
        this.paymentTransaction.getStatusId();
        this.paymentTransaction.getTransactionRef();
        JsonObject responseJsonObjectBody;

        if(response == null){
            responseJsonObjectBody = new Gson().fromJson("{\"status\": \"error\"}", JsonObject.class);
        }else{
            responseJsonObjectBody = response.body();
        }

        if(this.paymentTransaction.hasStartedProcessingOnServer()){
            Log.i(getClass().getName()+" ON-RESPONSE: ",  ( new Gson().toJson(response.body())) );
            Log.i(getClass().getName()+" ON-RESPONSE STAT: ",  (this.paymentTransaction.getStatusId() ));
            Log.i(getClass().getName()+" ON-RESPONSE Message:",  ( response.message().toString()) );
            Log.i(getClass().getName()+" ON-RESPONSE E-BDY:",  "[ " + ( response.isSuccessful() +" ]") );
            Log.i(getClass().getName()+" ON-RESPONSE E-BDY:",  ( response.raw().body().toString()) );
        }

//        }else{
//            turnOffProcessing();
//            transactionCallback.onError(new Throwable("Server Processing On Server"), paymentTransaction);
//        }
        String outcome_status = responseJsonObjectBody.get("status").getAsString().toLowerCase();
        System.out.println("outcomeStatus"+outcome_status);

        switch (outcome_status){
            case "error":
                Log.i("RESPONSE-HR: ", "STATUS:    " + "ERROR");
                break;
            case "success":
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "SUCCESS");
                break;
            case "104":
                String message = responseJsonObjectBody.get("message").getAsString();

                if(message.length() >0 ) {
                    paymentTransaction.setMessage(message);
                    if(message.contains("Invalid Response"))
                    {
                        Log.i("RESPONSE-HR: ", "STATUS:    " + "104");
                        turnOffProcessing();
                        transactionCallback.onError(new Throwable(activity.getResources().getString(R.string.enter_valid_otp)), paymentTransaction);
                        return;
                    }
                    else
                    {
                        Log.i("RESPONSE-HR: ", "STATUS:    " + "104");
                        turnOffProcessing();
                        transactionCallback.onError(new Throwable(message), paymentTransaction);
                        return;
                    }
                }
                break;
            case "202":
                if(responseJsonObjectBody.has("apply_auth") || responseJsonObjectBody.has("auth_type")){

                    if(responseJsonObjectBody.has("apply_auth"))
                    {
                         apply_auth = responseJsonObjectBody.get("apply_auth").getAsString();
                    }
                    if(responseJsonObjectBody.has("auth_type"))
                    {
                         auth_type = responseJsonObjectBody.get("auth_type").getAsString();
                    }
                    if(apply_auth.equalsIgnoreCase("otp") || auth_type.equalsIgnoreCase("otp")){
                        otp_message=responseJsonObjectBody.get("validate").getAsString();
                        System.out.println("otp_message"+otp_message);
                        new OtpAsyncTask().execute();
                        return;
                    }
                    if(apply_auth.equalsIgnoreCase("PIN")){
                        txnRef=responseJsonObjectBody.get("txnRef").getAsString();
                        new PinAsyncTask().execute();
                       // requeryAndverifyTransactionServer();
                        return;
                    }

                   /* if(apply_auth.contains("3ds")){


                       *//* Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse(apply_auth));
                        activity.startActivity(viewIntent);*//*
                       // requeryAndverifyTransactionServer();
                      //  return;
                    }*/
                }
                if(responseJsonObjectBody.has("authURL"))
                {
                     auth_url = responseJsonObjectBody.get("authURL").getAsString();
                    System.out.println("apply_auth _url"+auth_url);
                    new AuthUrlAsyncTask().execute();
                  /*  Intent paymentwebview=new Intent(activity, AuthUrlWebView.class);
                    paymentwebview.putExtra("weburl",authURL);
                    activity.startActivity(paymentwebview);*/
                   /* Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse(authURL));
                    activity.startActivity(viewIntent);*/
                    return;
                }
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "202");
                break;
            case "200":
                if(paymentTransaction.hasStartedProcessingOnServer()) {
                    if (requeryCount >= 2) {
                        turnOffProcessing();
                        System.out.println("isPaymentwithToken"+isPaymentwithToken);
                        isPaymentwithToken=false;
                      //  GladePayResponseModel gladePayResponseModel = gson.fromJson(response.body(), GladePayResponseModel.class);
                        sessionManager.setGladepaydetail(responseJsonObjectBody.toString());
                        transactionCallback.onSuccess(paymentTransaction);
                        Log.i("RESPONSE-HR: ", "STATUS:    " + "200");
                        Log.i("RESPONSE-HR: ", "REQUERY-COUNT: > Number of Verification Exceeded");
                        requeryCount = 0;
                        return;
                    }
                }
                if(responseJsonObjectBody.has("cardToken") && responseJsonObjectBody.has("message")){
                     cardToken = responseJsonObjectBody.get("cardToken").getAsString();
                   /* if(responseJsonObjectBody.get("txnRef").getAsString().equalsIgnoreCase("failed"))
                    {
                        message=responseJsonObjectBody.get("txnRef").getAsString();
                        String status=responseJsonObjectBody.get("txnStatus").getAsString();
                        turnOffProcessing();
                        paymentTransaction.setMessage(message);
                        transactionCallback.onError(new Throwable(status),paymentTransaction);
                        return;
                    }*/
                   try {
                       message = responseJsonObjectBody.get("message").getAsString();
                       paymentTransaction.setMessage(message);
                       if(message.toLowerCase().contains("success") || message.toLowerCase().contains("approved") ){
                           gladepaycard_tokenusingAuth_pin=responseJsonObjectBody.get("cardToken").getAsString();
                           Log.i("RESPONSE-HR: ", "ABOUT-REQUERY&VERIFY -MESSAGE");
                           new CountDownTimer(5000, 5000) {
                               public void onFinish() {
                                   requeryAndverifyTransactionServer();
                                   //PayemntWithToken();
                               }
                               public void onTick(long millisUntilFinished) {
                               }
                           }.start();
                           return;
                       }
                   }catch (UnsupportedOperationException ex)
                   {
                       turnOffProcessing();
                       paymentTransaction.setMessage("Failed");
                       transactionCallback.onError(ex,paymentTransaction);
                       return;
                   }
                }
                if(responseJsonObjectBody.has("cardToken") && responseJsonObjectBody.has("bank_message"))
                {
                    System.out.println("bank_message"+responseJsonObjectBody.get("bank_message").toString().equalsIgnoreCase("null"));
                    if(responseJsonObjectBody.get("bank_message").toString().equalsIgnoreCase("null"))
                    {
                       /* boolean cardToken = responseJsonObjectBody.get("cardToken").getAsBoolean();
                        message = responseJsonObjectBody.get("message").getAsString();
                        paymentTransaction.setMessage(message);
                        if(!cardToken && message.toLowerCase().contains("Payment Completed")){*/
                        System.out.println("GladeCardToken"+gladepaycard_tokenusingAuth_pin);
                            turnOffProcessing();
                            sessionManager.setGladepaydetail(responseJsonObjectBody.toString());
                            transactionCallback.onSuccess(this.paymentTransaction);
                            return;
                       // }
                    }
                    else
                    {
                        message = responseJsonObjectBody.get("bank_message").getAsString();
                        System.out.println("bank_message"+message);
                        paymentTransaction.setMessage(message);
                        System.out.println("approved"+message.toLowerCase().contains("approved"));
                        if(message.toLowerCase().contains("approved")){
                            Log.i("RESPONSE-HR: ", "ABOUT-REQUERY&VERIFY -BANK_MESSAGE");
                            new CountDownTimer(5000, 5000) {
                                public void onFinish() {

                                    requeryAndverifyTransactionServer();
                                }
                                public void onTick(long millisUntilFinished) {
                                }
                            }.start();
                            return;
                        }
                    }
                   // String cardToken = responseJsonObjectBody.get("cardToken").getAsString();
                }
              /*  if(responseJsonObjectBody.has("cardToken") && responseJsonObjectBody.has("message")){
                    Boolean cardToken = responseJsonObjectBody.get("cardToken").getAsBoolean();
                    message = responseJsonObjectBody.get("message").getAsString();
                    paymentTransaction.setMessage(message);
                    if(!cardToken && message.toLowerCase().contains("Payment Completed")){
                        turnOffProcessing();
                        sessionManager.setGladepaydetail(responseJsonObjectBody.toString());
                        transactionCallback.onSuccess(this.paymentTransaction);
                        return;
                    }
                }*/
                break;
            case "205":
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "500");
                transactionCallback.onError(new Throwable("Payment Failed.can't proceed in this card"), paymentTransaction);
            case "500":
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "500");
                 messageError = responseJsonObjectBody.get("message").toString();
                turnOffProcessing();
                transactionCallback.onError(new Throwable(messageError), paymentTransaction);
                break;
            case "400":
          /*  {"status":400,"message":"Error processing request, please try again"}*/
                messageError = responseJsonObjectBody.get("message").toString();
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "400");
                if(messageError.toLowerCase().contains("Error processing request"))
                {
                    turnOffProcessing();
                    transactionCallback.onError(new Throwable(activity.getResources().getString(R.string.enter_valid_otp)), paymentTransaction);
                }
                else {
                    turnOffProcessing();
                    transactionCallback.onError(new Throwable(activity.getResources().getString(R.string.enter_valid_otp)), paymentTransaction);
                }
                break;
            case "401":
                Log.i("RESPONSE-HR: ", "STATUS:    "   + "401");
                messageError = responseJsonObjectBody.get("message").toString();
                turnOffProcessing();
                transactionCallback.onError(new Throwable(messageError), paymentTransaction);
                break;
            case "402":
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "402");
                messageError = responseJsonObjectBody.get("message").toString();
                turnOffProcessing();
                transactionCallback.onError(new Throwable(messageError), paymentTransaction);
                break;
            case "403":
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "403");
                messageError = responseJsonObjectBody.get("message").toString();
                turnOffProcessing();
                transactionCallback.onError(new Throwable(messageError), paymentTransaction);
                break;
            case "300":
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "300");
                messageError = responseJsonObjectBody.get("message").toString();
                turnOffProcessing();
                transactionCallback.onError(new Throwable(messageError), paymentTransaction);
                break;
            case "301":
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "301");
                messageError = responseJsonObjectBody.get("message").toString();
                turnOffProcessing();
                transactionCallback.onError(new Throwable(messageError), paymentTransaction);
                break;
            default:
                Log.i("RESPONSE-HR: ", "STATUS:    "  + "DEFAULT" +response.body().get("status").getAsString());
                messageError = responseJsonObjectBody.get("message").toString();
                turnOffProcessing();
                transactionCallback.onError(new Throwable(messageError), paymentTransaction);
        }
        Log.i("RESPONSE-HR: ", new Gson().toJson(responseJsonObjectBody.toString()));

       /* if (outcome_status.equalsIgnoreCase("202") || outcome_status.equalsIgnoreCase("success")) {
            turnOffProcessing();
            System.out.println("isPaymentwithToken"+isPaymentwithToken);
            isPaymentwithToken=false;
          //  GladePayResponseModel gladePayResponseModel = gson.fromJson(response.body(), GladePayResponseModel.class);
            sessionManager.setGladepaydetail(responseJsonObjectBody.toString());
            transactionCallback.onSuccess(this.paymentTransaction);
            return;
        }*/

    }
    private void tLog(String tag){
        Log.i(getClass().getSimpleName(), tag);
    }

    private class AuthUrlAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            Intent i = new Intent(activity, AuthUrlWebView.class);
            i.putExtra("weburl",auth_url);
            activity.startActivity(i);
            synchronized (asi) {
                try {
                    asi.wait();
                } catch (InterruptedException e) {
                    notifyProcessingError(new Exception("3DSecure  entry Interrupted"));
                }
            }
            return asi.getResponse().toString();
        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            if (response != null) {

                System.out.println("response"+response);
              //  jsonObject = new Gson().fromJson(response, JsonObject.class);
              //  System.out.println("jsonobjct"+jsonObject);
                handlegladepayresponse(response);
                //handleGladepayApiResponse(response);
               /* JsonObject jsonObject=new JsonObject(response);
                handleGladepayApiResponse(response);*/
//                chargeRequestBody.setToken(otp);
              //  PaymentTransactionManager.this.validateTransactionServer(otp); //.validate();
            } else {
                notifyProcessingError(new Exception("You did not completed your 3dsecure"));
            }
        }
    }
    private void handlegladepayresponse(String response) {
        JsonObject responsejsonObject=null;
        String message;
        responsejsonObject = new Gson().fromJson(response, JsonObject.class);
        if(responsejsonObject.has("cardToken") && responsejsonObject.has("bank_message"))
        {

            message = responsejsonObject.get("bank_message").getAsString();
            System.out.println("bank_message"+message);
            paymentTransaction.setMessage(message);
            System.out.println("approved"+message.toLowerCase().contains("approved"));
            if(message.toLowerCase().contains("approved")){
                String cardToken = responsejsonObject.get("cardToken").getAsString();
                turnOffProcessing();
                //  GladePayResponseModel gladePayResponseModel = gson.fromJson(response.body(), GladePayResponseModel.class);
                sessionManager.setGladepaydetail(responsejsonObject.toString());
                transactionCallback.onSuccess(paymentTransaction);
                return;
            }
             if(message.toLowerCase().contains("declined")){
                turnOffProcessing();
              String status = responsejsonObject.get("txnStatus").getAsString();
                //  GladePayResponseModel gladePayResponseModel = gson.fromJson(response.body(), GladePayResponseModel.class);
                sessionManager.setGladepaydetail(responsejsonObject.toString());
                transactionCallback.onError(new Throwable(status),paymentTransaction);
            }
        }
    }
    private class OtpAsyncTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Intent i = new Intent(activity, OtpActivity.class);
            i.putExtra("otp_message",otp_message);
            activity.startActivity(i);

            synchronized (osi) {
                try {
                    osi.wait();
                } catch (InterruptedException e) {
                    notifyProcessingError(new Exception("OTP entry Interrupted"));
                }
            }
            return osi.getOtp();
        }
        @Override
        protected void onPostExecute(String otp) {
            super.onPostExecute(otp);
            if (otp != null) {
//                chargeRequestBody.setToken(otp);
                PaymentTransactionManager.this.validateTransactionServer(otp); //.validate();
            } else {
                notifyProcessingError(new Exception("You did not provide an OTP"));
            }
        }
    }
    private class PinAsyncTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            Intent i = new Intent(activity, PINActivity.class);
            activity.startActivity(i);

            synchronized (psi) {
                try {
                    psi.wait();
                } catch (InterruptedException e) {
                    notifyProcessingError(new Exception("PIN entry Interrupted"));
                }
            }
            return psi.getPin();
        }
        @Override
        protected void onPostExecute(String pin) {
            super.onPostExecute(pin);
            System.out.println("pin"+pin);
            if (pin != null) {
                if(!pin.isEmpty())
                {
                    chargeRequestBody.addPin(pin);
                    PaymentTransactionManager.this.initiateChargeOnServer(txnRef,pin); //.validate();

                }
                else
                {
                    notifyProcessingError(new Exception("PIN is Required"));
                }
//                chargeRequestBody.setToken(otp);
            } else {
                notifyProcessingError(new Exception("You did not provide an OTP"));
            }
        }
    }
}
