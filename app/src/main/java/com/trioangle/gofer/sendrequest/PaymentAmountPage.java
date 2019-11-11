package com.trioangle.gofer.sendrequest;
/**
 * @package com.trioangle.gofer
 * @subpackage sendrequest
 * @category PaymentAmountPage
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.trioangle.gofer.GladePay.Card;
import com.trioangle.gofer.GladePay.Charge;
import com.trioangle.gofer.GladePay.GladePayResponseModel;
import com.trioangle.gofer.GladePay.GladePaycardActivity;
import com.trioangle.gofer.GladePay.Gladepay;
import com.trioangle.gofer.GladePay.GladepaySdk;
import com.trioangle.gofer.GladePay.PTransaction;
import com.trioangle.gofer.R;
import com.trioangle.gofer.adapters.PriceRecycleAdapter;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.trip.InvoiceModel;
import com.trioangle.gofer.datamodels.trip.InvoicePaymentDetail;
import com.trioangle.gofer.datamodels.trip.PaymentDetails;
import com.trioangle.gofer.datamodels.trip.TripRatingResult;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.pushnotification.Config;
import com.trioangle.gofer.pushnotification.NotificationUtils;
import com.trioangle.gofer.sidebar.payment.AddWalletActivity;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonKeys.REQUEST_CODE_PAYMENT;
import static com.trioangle.gofer.utils.CommonKeys.card_token;
import static com.trioangle.gofer.utils.CommonKeys.isPaymentwithToken;
import static com.trioangle.gofer.utils.CommonKeys.last4;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.Enums.REQ_ADD_WALLET;
import static com.trioangle.gofer.utils.Enums.REQ_AFTER_PAY;
import static com.trioangle.gofer.utils.Enums.REQ_GET_INVOICE;
import static com.trioangle.gofer.utils.Enums.REQ_PAYPAL_CURRENCY;

/* ************************************************************
    After trip completed driver can send payment
    *************************************************************** */
public class PaymentAmountPage extends AppCompatActivity implements ServiceListener {

    private static final String TAG = "paymentExample";
    private  PayPalConfiguration config;
    private  String CONFIG_ENVIRONMENT;
    // note that these credentials will differ between live & sandbox environments.
    private  String CONFIG_CLIENT_ID;

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
    public @InjectView(R.id.btnSubmit)
    Button rate_submit;

    ProgressDialog dialog1;
    public float payable_amt = 0;

   /* public @InjectView(R.id.walletamountlayout)
    RelativeLayout walletamountlayout;
    public @InjectView(R.id.promoamountlayout)
    RelativeLayout promoamountlayout;
    public @InjectView(R.id.payableamountlayout)
    RelativeLayout payableamountlayout;
    public @InjectView(R.id.basefare_amount)
    TextView basefare_amount;
    public @InjectView(R.id.distance_fare)
    TextView distance_fare;
    public @InjectView(R.id.time_fare)
    TextView time_fare;
    public @InjectView(R.id.fee)
    TextView fee;
    public @InjectView(R.id.totalamount)
    TextView totalamount;
    public @InjectView(R.id.walletamount)
    TextView walletamount;
    public @InjectView(R.id.promoamount)
    TextView promoamount;
    public @InjectView(R.id.payableamount)
    TextView payableamount;*/

    public @InjectView(R.id.rvPrice)
    RecyclerView recyclerView;
    public String total_fare;
    public String paypal_mode;
    public String paypal_app_id;
    public String driver_payout;
    public String admin_payout;
    public String payment_method;
    public Charge charge;
    protected boolean isInternetAvailable;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public PaymentDetails paymentDetails;
    public AcceptedDriverDetails acceptedDriverDetails = new AcceptedDriverDetails();
    private ArrayList<HashMap<String, String>> priceList = new ArrayList<HashMap<String, String>>();

    private InvoicePaymentDetail invoicePaymentDetail;
    String driverImage;
    private PTransaction transaction1;

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @OnClick(R.id.btnSubmit)
    public void rateSubmit() {
        if (rate_submit.getText().toString().equals(getResources().getString(R.string.pay))) {

            afterPayment();
         /*   CommonMethods.DebuggableLogI("payment method",payment_method);
            if (payment_method.contains(CommonKeys.PAYMENT_GLADEPAY)) {
                if(sessionManager.getCardValue()!=null && !TextUtils.isEmpty(sessionManager.getCardValue()))
                {
                    isPaymentwithToken=true;
                    startAFreshCharge(String.valueOf(payable_amt));
                }
                else
                {
                    Intent AddcardIntent=new Intent(PaymentAmountPage.this, GladePaycardActivity.class);
                    //  startAFreshCharge(walletamt.getText().toString());
                    AddcardIntent.putExtra("type","paypage");
                    AddcardIntent.putExtra("amount",String.valueOf(payable_amt));
                    startActivity(AddcardIntent);
                }
                *//*if(!payment_method.contains("Card"))
                {
                    Intent AddcardIntent=new Intent(PaymentAmountPage.this, GladePaycardActivity.class);
                    //  startAFreshCharge(walletamt.getText().toString());
                    AddcardIntent.putExtra("type","paypage");
                    AddcardIntent.putExtra("amount",String.valueOf(payable_amt));
                    startActivity(AddcardIntent);
                }*//*
               // afterPayment();
            }*/ /*if(payment_method.contains(CommonKeys.PAYMENT_GLADEPAYwithToken))
            {
                if(payment_method.contains("Card"))
                {

                }
            }*/
              /*else if (payment_method.contains(CommonKeys.PAYMENT_PAYPAL)) {
                payPalButtonClick();
            }*/
        } else if (rate_submit.getText().toString().equals(getResources().getString(R.string.proceed))) {
            paymentCompleted("");
        }
    }

    ArrayList<InvoiceModel> invoiceModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_amount_page);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);


        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        if (isInternetAvailable){
            commonMethods.showProgressDialog(this,customDialog);
            apiService.getInvoice(sessionManager.getAccessToken(),sessionManager.getTripId(),sessionManager.getType()).enqueue(new RequestCallback(REQ_GET_INVOICE,this));
        }
        /*Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        invoiceModels.addAll((ArrayList<InvoiceModel>) bundle.getSerializable("invoiceModels"));
        //acceptedDriverDetails = (AcceptedDriverDetails) getIntent().getSerializableExtra("driverDetails"); //Obtaining data
        paymentDetails = (PaymentDetails) getIntent().getSerializableExtra("paymentDetails"); //Obtaining data*/
//
        //loaddata(getIntent().getStringExtra("AmountDetails"));
        // Receive push notification
        receivepushnotification();
    }

    private Card retrivecardLastdigits()
    {
        Card card = null;
        System.out.println("last4"+last4);
        card = new Card.CardBuilder(last4, 0, 0, "").build();
        return card;
    }
    private void startAFreshCharge(String s) {
        // initialize the charge
        charge = new Charge();
        charge.setCard(retrivecardLastdigits());
        dialog1 = new ProgressDialog(PaymentAmountPage.this);
        dialog1.setCancelable(false);
        dialog1.setMessage("Performing transaction... please wait");
        dialog1.show();
        System.out.println("amount"+Double.parseDouble(s));
        charge.setAmount(Double.parseDouble(s));
        System.out.println("Email"+sessionManager.getemail());
        charge.setEmail(sessionManager.getemail());
        charge.setReference("ChargedFromAndroid_" + Calendar.getInstance().getTimeInMillis());
        try {
            charge.putCustomField("Charged From", "Android SDK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        chargeCard();
    }
    private void chargeCard() {
       PaymentAmountPage.this.transaction1 = null;
        GladepaySdk.chargeCard(PaymentAmountPage.this, charge, new Gladepay.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(PTransaction transaction) {


                transaction1  = transaction;
                //  mTextError.setText(" ");
                afterPayment();
             //   Toast.makeText(PaymentAmountPage.this, transaction.getTransactionRef(), Toast.LENGTH_LONG).show();
                //   updateTextViews();
              /*  Intent walletpage=new Intent(AddWalletActivity.this, AddWalletActivity.class);
                startActivity(walletpage);*/
//                new verifyOnServer().execute(transaction.getTransactionRef());
            }
            // This is called only before requesting OTP
            // Save reference so you may send to server if
            // error occurs with OTP
            // No need to dismiss dialog
            @Override
            public void beforeValidate(PTransaction transaction) {
                transaction1 = transaction;
              //  Toast.makeText(PaymentAmountPage.this, transaction.getTransactionRef(), Toast.LENGTH_LONG).show();
                //   updateTextViews();
            }
            @Override
            public void onError(Throwable error, PTransaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                transaction1 = transaction;
                dismissDialog();
                if (transaction.getTransactionRef() != null) {
                  //     Toast.makeText(PaymentAmountPage.this, getResources().getString(R.string.enter_valid_otp), Toast.LENGTH_LONG).show();
                       Toast.makeText(PaymentAmountPage.this, transaction.getTransactionRef() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    //  mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getTransactionRef(), error.getClass().getSimpleName(), error.getMessage()));
                } else {
                   // Toast.makeText(PaymentAmountPage.this, getResources().getString(R.string.enter_valid_otp), Toast.LENGTH_LONG).show();

                      Toast.makeText(PaymentAmountPage.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    //   mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));
                }
                // updateTextViews();
            }
        });
    }
    private void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    /**
     * Load fare details for current trip
     */
    public void loaddata(String amountdetails) {
        try {
            String wallet_amount = "";
            String promo_amount = "";
            JSONObject jObj = new JSONObject(amountdetails);
            // payable_amt = Float.valueOf(paymentDetails.getTotalFare());


            RecyclerView.Adapter adapter = new PriceRecycleAdapter(this, invoiceModels);
            recyclerView.setAdapter(adapter);
            //payment_method = paymentDetails.getPaymentMethod();

            if (jObj.has("payment_details")) {                             // Check payment page come rating page or trips detils page
                payable_amt = Float.valueOf(invoicePaymentDetail.getTotalFare());

                total_fare = invoicePaymentDetail.getTotalFare();
                paypal_mode = invoicePaymentDetail.getPaypalMode();
                paypal_app_id = invoicePaymentDetail.getPaypalAppId();

                driver_payout = invoicePaymentDetail.getDriverPayout();
                //admin_payout = invoicePaymentDetail.getAccessFee();
                payment_method = invoicePaymentDetail.getPaymentMethod();
                JSONObject jsonObject = jObj.getJSONObject("payment_details");
                if (payable_amt <= 0) {                                               // Check payable amount is available or not ( sometimes wallet and promo to pay full trip amount that time payable amount is 0)
                    rate_submit.setText(getResources().getString(R.string.proceed)); // if payable amount is 0 then directly we can proceed to next step
                } else {
                    if (payment_method.contains(CommonKeys.PAYMENT_CASH)) {                           // if payment method is cashLayout then driver confirm the driver other wise rider pay the amount to another payment option
                        rate_submit.setEnabled(false);
                        rate_submit.setText(getResources().getString(R.string.waitfordriver));
                        rate_submit.setBackgroundColor(getResources().getColor(R.color.button_disable_darkgrey_color));
                    } else {
                        rate_submit.setText(getResources().getString(R.string.pay)); // Rider pay button
                    }
                }
            } else {
                total_fare = jObj.getString("total_fare");
                // promo_amount = jObj.getString("promo_amount");
                // admin_paypal_id= jObj.getString("admin_paypal_id");
                paypal_mode = jObj.getString("paypal_mode");
                paypal_app_id = jObj.getString("paypal_app_id");
                // driver_paypal_id= jObj.getString("driver_paypal_id");

                driver_payout = jObj.getString("driver_payout");
                // admin_payout = jObj.getString("access_fee");
                //  sessionManager.setWalletAmount(jObj.getString("remaining_wallet_amount").toString());
                // wallet_amount = jObj.getString("wallet_amount");
                payment_method = jObj.getString("payment_method");
                payable_amt = Float.valueOf(total_fare);

                // Check payable amount is available or not ( sometimes wallet and promo to pay full trip amount that time payable amount is 0)
                if (payable_amt <= 0) {
                    // if payable amount is 0 then directly we can proceed to next step
                    rate_submit.setText(getResources().getString(R.string.proceed));
                } else {

                    // if payment method is cashLayout then driver confirm the driver other wise rider pay the amount to another payment option
                    if (payment_method.contains("Cash")) {
                        //rate_submit.setEnabled(false);
                        rate_submit.setText(getResources().getString(R.string.waitfordriver));
                        rate_submit.setBackgroundColor(getResources().getColor(R.color.button_disable_darkgrey_color));
                    } else {
                        rate_submit.setText(getResources().getString(R.string.pay));
                    }
                }



            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void paypalCredentialsUpdate(int paypal_mode,String paypal_app_id) {

        if (0==paypal_mode) {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
        } else {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
        }

        CONFIG_CLIENT_ID = paypal_app_id;


        // Paypal configuration

        System.out.println("Confiq id : "+CONFIG_ENVIRONMENT);
        System.out.println("Confiq client id  : "+CONFIG_CLIENT_ID);
        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(CONFIG_CLIENT_ID)
                // The following are only used in PayPalFuturePaymentActivity.
                .merchantName(getResources().getString(R.string.app_name))
                .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
                .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

        config.acceptCreditCards(false);

        // Call payal service
        Intent intent = new Intent(this, PayPalService.class);


        boolean servicerRunningCheck = isMyServiceRunning(PayPalService.class);

        System.out.println("Service Running check : "+servicerRunningCheck);
        if(servicerRunningCheck){
            if(intent!=null){
                stopService(intent);
            }
        }
        servicerRunningCheck = isMyServiceRunning(PayPalService.class);

        System.out.println("Service Running check twice : "+servicerRunningCheck);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Back button pressed
     */
    @Override
    public void onBackPressed() {
        if (getIntent().getIntExtra("isBack",0)==1){
            super.onBackPressed();
        }else {
            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);
            /*if (CommonKeys.IS_ALREADY_IN_TRIP) {
                CommonKeys.IS_ALREADY_IN_TRIP = false;
            }*/
            CommonKeys.IS_ALREADY_IN_TRIP=true;
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    /**
     * Update Payment API called
     */
    public void changeCurrencyPayment(String paypalamount) {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.paypalCurrency(sessionManager.getAccessToken(),
                sessionManager.getCurrencyCode(),
                paypalamount).enqueue(new RequestCallback(REQ_PAYPAL_CURRENCY, this));

    }
    /**
     * Update Payment API called
     */
    public void afterPayment(String payKey) {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.afterPayment(sessionManager.getAccessToken(), payKey,
                sessionManager.getTripId()
        ).enqueue(new RequestCallback(REQ_AFTER_PAY, this));
    }
    public void afterPayment() {
        if (commonMethods.isOnline(getApplicationContext())) {
            commonMethods.showProgressDialog(this, customDialog);
            apiService.proceedToCardPayment(sessionManager.getAccessToken(),sessionManager.getTripId()).enqueue(new RequestCallback(REQ_AFTER_PAY,this));
            //  dismissDialog();
          /*  String gladepaydetail = sessionManager.getGladepaydetail();
            System.out.println("gladepaydetails"+gladepaydetail);
            try {
                JSONObject jsonObj = new JSONObject(gladepaydetail);
                System.out.println("response"+jsonObj.toString());
                GladePayResponseModel gladePayResponseModel = gson.fromJson(jsonObj.toString(), GladePayResponseModel.class);
                apiService.after_paymentUsingGladePay(sessionManager.getAccessToken(),gladePayResponseModel.getChargedAmount(),gladePayResponseModel.getCardToken(),gladePayResponseModel.getCard().getMask(),sessionManager.getTripId(),gladePayResponseModel.getTxnRef()).enqueue(new RequestCallback(REQ_AFTER_PAY,this));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            /*apiService.after_paymentUsingGladePay(sessionManager.getAccessToken(),payable_amt,card_token,card
                    sessionManager.getTripId()
            ).enqueue(new RequestCallback(REQ_AFTER_PAY, this));*/
        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
        }
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

            case REQ_GET_INVOICE:
                if (jsonResp.isSuccess()) {
                    getInvoice(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                }
                break;

            case REQ_PAYPAL_CURRENCY:
                if (jsonResp.isSuccess()) {
                    String amount = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "amount", String.class);
                    String currency_code = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "currency_code", String.class);
                    int paypal_mode = (int) commonMethods.getJsonValue(jsonResp.getStrResponse(), "paypal_mode", String.class);
                    String paypal_app_id = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "paypal_app_id", String.class);

                    payment(amount, currency_code,paypal_mode,paypal_app_id);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                }
                break;

            //Register Rider
            case REQ_AFTER_PAY:
                if (jsonResp.isSuccess()) {
                    CommonMethods.stopFirebaseChatListenerService(this);
                    sessionManager.setDriverAndRiderAbleToChat(false);
                    statusDialog(getString(R.string.paymentcompleted));
                } else {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    DebuggableLogV("jsonResp.getStatusMsg()", "" + jsonResp.getStatusMsg());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
        }
    }

    private void getInvoice(JsonResponse jsonResponse){
        invoicePaymentDetail=gson.fromJson(jsonResponse.getStrResponse(), InvoicePaymentDetail.class);
        invoiceModels.addAll(invoicePaymentDetail.getInvoice());
        System.out.println("invoicePaymentDetail "+invoicePaymentDetail.getDriverImage());
        loaddata(jsonResponse.getStrResponse());
        rate_submit.setVisibility(View.VISIBLE);
    }
    /**
     * show dialog for payment completed
     */
    public void statusDialog(String message) {
        if (!this.isFinishing()) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.addphoto_header, null);
            TextView tit = (TextView) view.findViewById(R.id.header);
            tit.setText(getResources().getString(R.string.paymentcompleted));
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setCustomTitle(view);
            builder.setTitle(message)
                    .setCancelable(false)
                    //.setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            sessionManager.setIsrequest(false);
                            sessionManager.setIsTrip(false);
                            if (CommonKeys.IS_ALREADY_IN_TRIP){
                                CommonKeys.IS_ALREADY_IN_TRIP=false;
                            }
                            Intent main = new Intent(getApplicationContext(), DriverRatingActivity.class);
                            main.putExtra("imgprofile",invoicePaymentDetail.getDriverImage());
                            startActivity(main);
                            finish();

                        }
                    })
                    .show();
        }
    }


    /**
     * Paypal button clicked
     */
    public void payPalButtonClick() {

        /*if(!loader.isShowing())
        {
           loader.show();
        }*/
        String paypalamount = "";
        if (payable_amt > 0) {
            paypalamount = String.valueOf(payable_amt);
        } else {
            paypalamount = total_fare;
        }


        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (isInternetAvailable) {
            //new Updatepayment().execute(url,"currency");
            changeCurrencyPayment(paypalamount);
        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
        }
    }

    /**
     * Paypal payable amount set
     */
    public void payment(String amount, String currencycode,int paypal_mode,String paypal_app_id) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.

        */
       paypalCredentialsUpdate(paypal_mode,paypal_app_id);

        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE, amount, currencycode);
        //PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_SALE);
        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        //addAppProvidedShippingAddress(thingToBuy);   /// Add shipping address
        //enableShippingAddressRetrieval(thingToBuy,true);  //  Enable retrieval of shipping addresses from buyer's PayPal account

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /**
     * Paypal payment details
     */
    private PayPalPayment getThingToBuy(String paymentIntent, String amount, String currencycode) {

        return new PayPalPayment(new BigDecimal(amount), currencycode, getResources().getString(R.string.payment_name),
                paymentIntent);
    }

    /**
     * Check paypal payment completed or not
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        DebuggableLogI(TAG, confirm.toJSONObject().toString(4));
                        DebuggableLogI(TAG, confirm.toJSONObject().getJSONObject("response").get("id").toString());
                        DebuggableLogI(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        paymentCompleted(confirm.toJSONObject().getJSONObject("response").get("id").toString());
                        // displayResultText("PaymentConfirmation info received from PayPal");

                    } catch (JSONException e) {
                        DebuggableLogE(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                DebuggableLogI(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                DebuggableLogI(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }


    /**
     * Paypal payment completed to update transaction id to server
     */
    public void paymentCompleted(String payKey) {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (isInternetAvailable) {
            afterPayment(payKey);
            //new Updatepayment().execute(url,"update");
        } else {
            commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
        }
    }

    /**
     * Receive push notification for payment completed
     */
    public void receivepushnotification() {

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // FCM successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    //String message = intent.getStringExtra("message");

                    String JSON_DATA = sessionManager.getPushJson();


                    if (JSON_DATA != null) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(JSON_DATA);
                            if (jsonObject.getJSONObject("custom").has("trip_payment")) {
                                statusDialog(getResources().getString(R.string.paymentcompleted));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();


        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onPause() {
        super.onPause();
         LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
