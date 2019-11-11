package com.trioangle.gofer.sidebar.payment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
import com.trioangle.gofer.GladePay.GladepayApiService;
import com.trioangle.gofer.GladePay.GladepaySdk;
import com.trioangle.gofer.GladePay.PTransaction;
import com.trioangle.gofer.GladePay.PaymentTransactionManager;
import com.trioangle.gofer.GladePay.exceptions.ExpiredAccessCodeException;
import com.trioangle.gofer.GladePay.request.CardChargeRequestBody;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.main.RiderProfile;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.addCardDetails.AddCardActivity;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit2.Call;

import static com.trioangle.gofer.utils.CommonKeys.NUMBER_VALIDATION_API_RESULT_NEW_USER;
import static com.trioangle.gofer.utils.CommonKeys.REQUEST_CODE_PAYMENT;
import static com.trioangle.gofer.utils.CommonKeys.card_token;
import static com.trioangle.gofer.utils.CommonKeys.isGladePayinWalletpage;
import static com.trioangle.gofer.utils.CommonKeys.isPaymentwithToken;
import static com.trioangle.gofer.utils.CommonKeys.last4;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.Enums.REQ_ADD_CARD;
import static com.trioangle.gofer.utils.Enums.REQ_ADD_WALLET;
import static com.trioangle.gofer.utils.Enums.REQ_PAYPAL_CURRENCY;


public class AddWalletActivity extends AppCompatActivity implements ServiceListener {

    private static final String TAG = "paymentExample";
    private static PayPalConfiguration config;
    private static String CONFIG_ENVIRONMENT;
    // note that these credentials will differ between live & sandbox environments.
    private static String CONFIG_CLIENT_ID;

    private CardChargeRequestBody chargeRequestBody;

    public AlertDialog dialog;
    public @Inject
    SessionManager sessionManager;
    private PTransaction transaction1;
    GladepayApiService gladepayApiService;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    CustomDialog customDialog;
    public @Inject
    Gson gson;
    public @InjectView(R.id.wallet_amt)
    TextView wallet_amt;
    public BottomSheetDialog dialog2;
    public TextView paymentmethod_type;
    public ImageView paymentmethod_img;
    public EditText walletamt;
    public Button add_money;
    public String paypal_mode;
    public String paypal_app_id;
    protected boolean isInternetAvailable;
    ProgressDialog dialog1;
    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }
    public Charge charge;
    private PTransaction transaction;
    PaymentTransactionManager paymentTransactionManager;
    @OnClick(R.id.add_amount)
    public void addamount() {
        walletamt.setText("");
        dialog2.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

        /**
         * common loader and internet available or not check
         */
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());


        /**
         *  Load PayPal config
         */
        //loadPayPal();

        /**
         *  Wallet Add amount dialog
         */
        addAmount();
        wallet_amt.setText(sessionManager.getCurrencySymbol() + sessionManager.getWalletAmount());

    }
    public void addwalletAmount()
    {
        if (isInternetAvailable) {
            addWalletMoneyUsingStripe();
        }else {
            commonMethods.showMessage(AddWalletActivity.this, dialog, getString(R.string.no_connection));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddWalletActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    /**
     * PayPal configuration called
     */
    public void loadPayPal(int paypal_mode,String paypal_app_id) {


        //paypal_app_id = paypal_key;
        //paypal_app_id=getResources().getString(R.string.paypal_client_id);

        if (0==paypal_mode) {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
        } else {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
        }

        CONFIG_CLIENT_ID = paypal_app_id;




        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(CONFIG_CLIENT_ID)
                // The following are only used in PayPalFuturePaymentActivity.
                .merchantName(getResources().getString(R.string.wallet))
                .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
                .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

        config.acceptCreditCards(false);

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
     * Add wallet amount called
     */
    public void addAmount() {
        RelativeLayout paymentmethod;
        TextView currency_symbol_wallet;
        TextView change;

        dialog2 = new BottomSheetDialog(AddWalletActivity.this);
        dialog2.setContentView(R.layout.add_wallet_amount);
        // paymentmethod = (RelativeLayout) dialog2.findViewById(R.id.paymentmethod);
        // paymentmethod.setVisibility(View.GONE);
        currency_symbol_wallet = (TextView) dialog2.findViewById(R.id.currency_symbol_wallet);
        paymentmethod_type = (TextView) dialog2.findViewById(R.id.paymentmethod_type);
        change = (TextView) dialog2.findViewById(R.id.change);
        add_money = (Button) dialog2.findViewById(R.id.add_money);
        walletamt = (EditText) dialog2.findViewById(R.id.walletamt);
        currency_symbol_wallet.setText(sessionManager.getCurrencySymbol());
        paymentmethod_img = (ImageView) dialog2.findViewById(R.id.paymentmethod_img);
        walletamt.setText("");
        add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=walletamt.getText().toString().trim();

                if (walletamt.getText().length() > 0)
                {
                    double walletamts=Double.parseDouble(s.trim());
                    if(walletamts>=50) {

                        if(commonMethods.isOnline(getApplicationContext()))
                        {
                            if (sessionManager.getWalletPaymentMethod().equals(CommonKeys.PAYMENT_GLADEPAY)) {
                                isPaymentwithToken = true;
                                System.out.println("isPaymentwithToken" + isPaymentwithToken);
                          /*  dialog1 = new ProgressDialog(AddWalletActivity.this);
                            dialog1.setMessage("Performing transaction... please wait");
                            dialog1.show();
                            dialog1.setCancelable(false);*/
                                if(commonMethods.isOnline(getApplicationContext())) {
                                    startAFreshCharge(walletamt.getText().toString());
                                }
                                else
                                {
                                    commonMethods.showMessage(AddWalletActivity.this, dialog, getString(R.string.no_connection));
                                }
                            } else
                            {
                                Intent AddcardIntent = new Intent(AddWalletActivity.this, GladePaycardActivity.class);
                                //  startAFreshCharge(walletamt.getText().toString());
                                AddcardIntent.putExtra("type", "walletpage");
                                AddcardIntent.putExtra("amount", walletamt.getText().toString());
                                isGladePayinWalletpage=true;
                                startActivity(AddcardIntent);
                            }
                        }
                        else
                        {
                            commonMethods.showMessage(AddWalletActivity.this, dialog, getString(R.string.no_connection));
                        }
                        //   String s=walletamt.getText().toString().trim();
                        System.out.println("WalletPaymentmethod" + sessionManager.getWalletPaymentMethod());

                        //  System.out.println("FirstValue"+s.charAt(0)>0);

                          /*  if(sessionManager.getCardValue()!=null && !sessionManager.getCardValue().isEmpty())
                            {
                                isPaymentwithToken = true;
                                System.out.println("isPaymentwithToken" + isPaymentwithToken);
                                dialog1 = new ProgressDialog(AddWalletActivity.this);
                                dialog1.setMessage("Performing transaction... please wait");
                                dialog1.show();
                                dialog1.setCancelable(false);
                                startAFreshCharge(walletamt.getText().toString());
                            }
                           else
                            {
                                Intent AddcardIntent = new Intent(AddWalletActivity.this, GladePaycardActivity.class);
                                //  startAFreshCharge(walletamt.getText().toString());
                                AddcardIntent.putExtra("type", "walletpage");
                                AddcardIntent.putExtra("amount", walletamt.getText().toString());
                                startActivity(AddcardIntent);
                            }*/
                            //
                            //  chargeCard();
                        /* else if (sessionManager.getWalletPaymentMethod().equals(CommonKeys.PAYMENT_GLADEPAYwithCard)) {
                            Intent AddcardIntent = new Intent(AddWalletActivity.this, GladePaycardActivity.class);
                            //  startAFreshCharge(walletamt.getText().toString());
                            AddcardIntent.putExtra("type", "walletpage");
                            AddcardIntent.putExtra("amount", walletamt.getText().toString());
                            startActivity(AddcardIntent);
                            //   finish();
                       *//* if (commonMethods.isOnline(getApplicationContext())) {
                            changeCurrencyPayment(walletamt.getText().toString());
                        } else {
                            commonMethods.showMessage(AddWalletActivity.this, dialog, getString(R.string.no_connection));
                        }*//*
                        }*//*else if (sessionManager.getWalletPaymentMethod().equals(CommonKeys.PAYMENT_CARD)){
                        if (commonMethods.isOnline(getApplicationContext())) {
                            addWalletMoneyUsingStripe(walletamt.getText().toString());
                        } else {
                            commonMethods.showMessage(AddWalletActivity.this, dialog, getString(R.string.no_connection));
                        }
                    }*/

                        dialog2.dismiss();
                    }
                    else {
                        Toast.makeText(AddWalletActivity.this, getResources().getString(R.string.enter_wallet_amount), Toast.LENGTH_SHORT).show();
                        //commonMethods.showMessage(AddWalletActivity.this, dialog, "Please enter the wallet amount");
                    }
                } else {
                    Toast.makeText(AddWalletActivity.this, getResources().getString(R.string.enter_wallet_amount), Toast.LENGTH_SHORT).show();
                    //commonMethods.showMessage(AddWalletActivity.this, dialog, "Please enter the wallet amount");
                }
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=walletamt.getText().toString().trim();
               if(walletamt.getText().length()>0)
               {
                   double walletamts=Double.parseDouble(s.trim());
                   if(walletamts>=50)
                   {
                       if(commonMethods.isOnline(getApplicationContext())){
                           Intent intent = new Intent(getApplicationContext(), PaymentPage.class);
                           intent.putExtra("type", "walletpage");
                           isGladePayinWalletpage = true;
                           intent.putExtra("amount", walletamt.getText().toString());
                           intent.putExtra(CommonKeys.TYPE_INTENT_ARGUMENT_KEY, CommonKeys.StatusCode.startPaymentActivityForAddMoneyToWallet);
                           startActivity(intent);
                           overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
                       }
                       else
                       {
                           commonMethods.showMessage(AddWalletActivity.this, dialog, getString(R.string.no_connection));
                       }
                   }
                   else
                   {
                       Toast.makeText(AddWalletActivity.this, getResources().getString(R.string.enter_wallet_amount), Toast.LENGTH_SHORT).show();
                   }
               }
               else
                   Toast.makeText(AddWalletActivity.this, getResources().getString(R.string.enter_wallet_amount), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Card retrivecardLastdigits()
    {
        Card card = null;
        System.out.println("last4"+last4);
        System.out.println();
        card = new Card.CardBuilder(sessionManager.getCardValue(), 0, 0, "").build();
        return card;
    };

    private void startAFreshCharge(String s) {
        // initialize the charge
        charge = new Charge();
        charge.setCard(retrivecardLastdigits());
        dialog1 = new ProgressDialog(AddWalletActivity.this);
        dialog1.setMessage("Performing transaction... please wait");
        dialog1.setCancelable(false);
        dialog1.show();
        System.out.println("amount"+Integer.parseInt(s));
        charge.setAmount(Integer.parseInt(s));
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
        transaction = null;
        GladepaySdk.chargeCard(AddWalletActivity.this, charge, new Gladepay.TransactionCallback() {
            // This is called only after transaction is successful
            @Override
            public void onSuccess(PTransaction transaction) {
                AddWalletActivity.this.transaction = transaction;

              //  mTextError.setText(" ");
                addwalletAmount();
                dismissDialog();
               // Toast.makeText(AddWalletActivity.this, transaction.getTransactionRef(), Toast.LENGTH_LONG).show();


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
                AddWalletActivity.this.transaction = transaction;
              //  Toast.makeText(AddWalletActivity.this, transaction.getTransactionRef(), Toast.LENGTH_LONG).show();
             //   updateTextViews();
            }
            @Override
            public void onError(Throwable error, PTransaction transaction) {
                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error
                AddWalletActivity.this.transaction = transaction;
                dismissDialog();
                if (error instanceof ExpiredAccessCodeException) {
                    AddWalletActivity.this.startAFreshCharge(walletamt.getText().toString().trim());
                    AddWalletActivity.this.chargeCard();
                    return;
                }
                if (transaction.getTransactionRef() != null) {

                    Toast.makeText(AddWalletActivity.this, transaction.getTransactionRef() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                  //  mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getTransactionRef(), error.getClass().getSimpleName(), error.getMessage()));
                } else {
                    Toast.makeText(AddWalletActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                 //   mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));
                }
               // updateTextViews();
            }
        });
    }
    private void dismissDialog() {
        if ((dialog1!= null) && dialog1.isShowing()) {
            dialog1.dismiss();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Method"+sessionManager.getWalletPaymentMethod()+"cardValue"+sessionManager.getCardValue());
       /* if (sessionManager.getWalletPaymentMethod().equals("")) {
            sessionManager.setWalletPaymentMethod(CommonKeys.PAYMENT_GLADEPAY);
        }
*/
       isGladePayinWalletpage=false;
        wallet_amt.setText(sessionManager.getCurrencySymbol() + sessionManager.getWalletAmount());
        if(sessionManager.getWalletPaymentMethod().equals(CommonKeys.PAYMENT_GLADEPAY))
        {
                paymentmethod_type.setText(sessionManager.getCardValue());
                paymentmethod_img.setImageDrawable(getResources().getDrawable(R.drawable.card));
        }
        else {
            //   sessionManager.setWalletPaymentMethod(CommonKeys.PAYMENT_GLADEPAY);
            paymentmethod_img.setImageDrawable(getResources().getDrawable(R.drawable.gladepay));
            paymentmethod_type.setText(getResources().getString(R.string.gladepay));
        }
          /*  if(sessionManager.getCardValue()!=null &&!sessionManager.getCardValue().isEmpty())
            {
                paymentmethod_type.setText(sessionManager.getCardValue());
                paymentmethod_img.setImageDrawable(getResources().getDrawable(R.drawable.card));
            } */
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
            case REQ_ADD_WALLET:
                if (jsonResp.isSuccess()) {
                    /*{"status_message":"Amount Added Successfully","status_code":"1","wallet_amount":"250.00"}*/
                    String walletamount =(String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "wallet_amount", String.class);
                 /*  Integer prev_walletAmount= Integer.valueOf(wallet_amt.getText().toString().trim());
                    System.out.println("lastWalletAmount"+prev_walletAmount+Integer.parseInt(walletamount));
                    String lastwalletamount= String.valueOf(prev_walletAmount+walletamount);*/
                    sessionManager.setWalletAmount(walletamount);
                    paymentmethod_type.setText(sessionManager.getCardValue());
                    wallet_amt.setText(sessionManager.getCurrencySymbol() + sessionManager.getWalletAmount());
                   // dismissDialog();
                    commonMethods.hideProgressDialog();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    DebuggableLogV("jsonResp.getStatusMsg()", "" + jsonResp.getStatusMsg());
                }
                break;
            case REQ_ADD_CARD:
                if(jsonResp.isSuccess()){
                    String walletamount = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "wallet_amount", String.class);
                    sessionManager.setWalletAmount(walletamount);
                    wallet_amt.setText(sessionManager.getCurrencySymbol() + sessionManager.getWalletAmount());
                    commonMethods.hideProgressDialog();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                    DebuggableLogV("jsonResp.getStatusMsg()", "" + jsonResp.getStatusMsg());
                }

            default:
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        DebuggableLogV(TAG, "onFailure");
    }

    /**
     * Change paypal currency
     */
    public void changeCurrencyPayment(String paypalamount) {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.paypalCurrency(sessionManager.getAccessToken(), sessionManager.getCurrencyCode(), paypalamount).enqueue(new RequestCallback(REQ_PAYPAL_CURRENCY, this));
    }
    /**
     * Add Wallet Amount
     */
    public void addWalletMoneyUsingPaypal(String paykey, String paypalamount) {
//        commonMethods.showProgressDialog(this, customDialog);
        apiService.addWalletMoneyUsingPaypal(sessionManager.getAccessToken(), paykey, paypalamount).enqueue(new RequestCallback(REQ_ADD_WALLET, this));
    }
    public void addWalletMoneyUsingStripe() {
       // GladePayResponseModel gladePayResponseModel=new GladePayResponseModel();
        String gladepaydetail = sessionManager.getGladepaydetail();
        System.out.println("gladepaydetails"+gladepaydetail);
            try {
                JSONObject jsonObj = new JSONObject(gladepaydetail);
                System.out.println("response"+jsonObj.toString());
                GladePayResponseModel gladePayResponseModel = gson.fromJson(jsonObj.toString(), GladePayResponseModel.class);
                sessionManager.setCardValue(gladePayResponseModel.getCard().getMask());
                sessionManager.setCardBrand(gladePayResponseModel.getCard().getBrand());
                last4=gladePayResponseModel.getCard().getMask();
                card_token=gladePayResponseModel.getCardToken();
                apiService.addWalletMoneyUsingStripe(sessionManager.getAccessToken(),gladePayResponseModel.getChargedAmount(),gladePayResponseModel.getCardToken(),gladePayResponseModel.getCard().getBrand(),gladePayResponseModel.getCard().getMask()).enqueue(new RequestCallback(REQ_ADD_WALLET,this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
     //   commonMethods.showProgressDialog(this, customDialog);
    }



    /**
     * Paypal payment
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
        loadPayPal(paypal_mode,paypal_app_id);
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

    private PayPalPayment getThingToBuy(String paymentIntent, String amount, String currencycode) {

        return new PayPalPayment(new BigDecimal(amount), currencycode, getResources().getString(R.string.payment_name),
                paymentIntent);
    }


    /**
     * Check paypal payment completed or not
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==5)
        {
            addwalletAmount();
        }
   /*     if (requestCode == REQUEST_CODE_PAYMENT) {
           *//* if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        DebuggableLogI(TAG, confirm.toJSONObject().toString(4));
                        DebuggableLogI(TAG, confirm.toJSONObject().getJSONObject("response").get("id").toString());
                        DebuggableLogI(TAG, confirm.getPayment().toJSONObject().toString(4));
                        *//**//**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         *//**//*
                     //   paymentCompleted(confirm.toJSONObject().getJSONObject("response").get("id").toString());
                        // displayResultText("PaymentConfirmation info received from PayPal");

                    } catch (JSONException e) {
                        DebuggableLogE(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            }*//* else if (resultCode == Activity.RESULT_CANCELED) {
                DebuggableLogI(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                DebuggableLogI(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }*/
    }


    /**
     * Paypal payment completed to update transcation id
     */
    public void paymentCompleted(String payKey) {

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (isInternetAvailable) {
            addWalletMoneyUsingPaypal(payKey, walletamt.getText().toString());
        } else {
            commonMethods.showMessage(AddWalletActivity.this, dialog, getString(R.string.no_connection));
        }
    }
}
