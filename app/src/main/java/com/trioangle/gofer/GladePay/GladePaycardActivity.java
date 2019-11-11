package com.trioangle.gofer.GladePay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.trioangle.gofer.GladePay.exceptions.ExpiredAccessCodeException;
import com.trioangle.gofer.GladePay.request.CardChargeRequestBody;
import com.trioangle.gofer.GladePay.utils.CardUtilities;
import com.trioangle.gofer.GladePay.utils.DateUtils;
import com.trioangle.gofer.GladePay.utils.StringUtilities;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sendrequest.PaymentAmountPage;
import com.trioangle.gofer.sidebar.payment.AddWalletActivity;
import com.trioangle.gofer.sidebar.payment.PaymentPage;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import okhttp3.internal.cache.CacheStrategy;


import static com.trioangle.gofer.utils.CommonKeys.PAYMENT_CASH;
import static com.trioangle.gofer.utils.CommonKeys.PAYMENT_GLADEPAY;
import static com.trioangle.gofer.utils.CommonKeys.card_token;
import static com.trioangle.gofer.utils.CommonKeys.gladepaycard_tokenusingAuth_pin;
import static com.trioangle.gofer.utils.CommonKeys.isGladePayinPaymentpage;
import static com.trioangle.gofer.utils.CommonKeys.isGladePayinWalletpage;
import static com.trioangle.gofer.utils.CommonKeys.last4;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogD;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.CommonMethods.getCardImage;
import static com.trioangle.gofer.utils.Enums.REQ_ADD_WALLET;
import static com.trioangle.gofer.utils.Enums.REQ_AFTER_PAY;

public class GladePaycardActivity extends AppCompatActivity implements ServiceListener {
public @InjectView(R.id.edt_card_number)
    EditText edt_cardNumber;
    public @InjectView(R.id.edt_cvc)
    EditText edt_CVC;
    public @InjectView(R.id.edt_expire_date)
    EditText edt_exp_date_month;
    public @InjectView(R.id.edt_expire_year)
    EditText edt_expire_year;
   /* public @InjectView(R.id.edt_pin)
    EditText edt_pin;*/
    public @InjectView(R.id.fab_verify)
    CardView fab_verify;
    public @InjectView(R.id.textError)
    TextView mTextError;
    public @InjectView(R.id.arrow)
    ImageView isBack;
    public Charge charge;
    ProgressDialog dialog;
    public AlertDialog dialog1;
    double amonut=0;
    private PTransaction transaction;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    ApiService apiService;
    public @Inject
    CommonMethods commonMethods;
    public @InjectView(R.id.txt_input_card_number)
    TextInputLayout txt_input_card_number;
    public @InjectView(R.id.input_layout_expire_date)
    TextInputLayout input_layout_expire_date;
    public @InjectView(R.id.input_layout_cvc)
    TextInputLayout input_layout_cvc;
    public @InjectView(R.id.txt_input_amount)
    TextInputLayout txt_input_amount;
    public @InjectView(R.id.txt_input_pin)
    TextInputLayout txt_input_pin;
    public @InjectView(R.id.edt_amount)
    EditText edt_amount;
    public @InjectView(R.id.edt_pin)
    EditText edt_pin;
    public @Inject
    Gson gson;
    @InjectView(R.id.one)
    EditText edtxOne;
    @InjectView(R.id.two)
    EditText edtxTwo;
    @InjectView(R.id.three)
    EditText edtxThree;
    @InjectView(R.id.four)
    EditText edtxFour;
    private boolean isDeletable = true;
    String pin="";
   public  @InjectView(R.id.pin_title)
           TextView pinTitle;

    public  @InjectView(R.id.rl_edittexts)
    RelativeLayout rl_edittexts;
    Card card;
    private String card_type;
    private String type;
    private Double card_amt=0.0;
    private boolean isInternetAvailable=false;

   private GladePayResponseModel gladePayResponseModel;

    @OnClick(R.id.arrow)
    public void Back()
    {
        onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glade_paycard);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        edt_cardNumber.addTextChangedListener(new CardTextWatcher(edt_cardNumber));
        edt_exp_date_month.addTextChangedListener(new CardTextWatcher(edt_exp_date_month));
        edt_expire_year.addTextChangedListener(new CardTextWatcher(edt_expire_year));
        edt_pin.addTextChangedListener(new CardTextWatcher(edt_pin));
        edt_CVC.addTextChangedListener(new CardTextWatcher(edt_CVC));
        edt_amount.addTextChangedListener(new CardTextWatcher(edt_amount));
        amonut=0;
      //  initPINTextviewListener();
        fab_verify.setEnabled(false);
        isInternetAvailable=commonMethods.isOnline(this);
        if(getIntent().getExtras()!=null)
        {
            System.out.println("Amount"+getIntent().getExtras().getString("amount"));
            if(getIntent().getExtras().getString("amount")!=null)
            {
                amonut= Double.parseDouble(getIntent().getExtras().getString("amount"));
            }
             type=getIntent().getExtras().getString("type");
        }
       edt_amount.setText(String.valueOf(amonut));
        fab_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card=retrieveAndPopulateCardFromForm();
                if(!card.isValid(GladePaycardActivity.this))
                {
                    Toast.makeText(GladePaycardActivity.this, "Invalid card", Toast.LENGTH_SHORT).show();
                    return;
                }
               /* if(edt_pin.getText().toString().trim().length()>4 && edt_pin.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(GladePaycardActivity.this, "Invalid pin", Toast.LENGTH_SHORT).show();
                    return;
                }*/
                if(edt_exp_date_month.getText().toString().trim().length()>2&& edt_expire_year.getText().toString().trim().length()>2)
                {
                    Toast.makeText(GladePaycardActivity.this, "Invalid Expire Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!edt_amount.getText().toString().isEmpty() && edt_amount.getText().toString()!=null)
                {
                    card_amt=Double.parseDouble(edt_amount.getText().toString());
                    if(card_amt<50)
                    {
                        Toast.makeText(GladePaycardActivity.this, "Please enter amount 50 or above", Toast.LENGTH_SHORT).show();
                        return;
                    }
                   // Toast.makeText(GladePaycardActivity.this, "Invalid Amount", Toast.LENGTH_SHORT).show();
                  //  return;
                }else
                {
                    Toast.makeText(GladePaycardActivity.this, "Invalid Amount", Toast.LENGTH_SHORT).show();
                      return;
                }

                try {
                    if(isInternetAvailable)
                    {
                        startAFreshCharge();
                    }
                    else
                    {
                        commonMethods.showMessage(GladePaycardActivity.this, dialog1, getString(R.string.no_connection));
                    }
                } catch (Exception e) {
                    GladePaycardActivity.this.mTextError.setText(String.format("An error occurred while charging card: %s %s", e.getClass().getSimpleName(), e.getMessage()));

                }
            }
        });
}

 /*   private void initPINTextviewListener() {
        edtxOne.addTextChangedListener(new OtpTextWatcher());
        edtxTwo.addTextChangedListener(new OtpTextWatcher());
        edtxThree.addTextChangedListener(new OtpTextWatcher());
        edtxFour.addTextChangedListener(new OtpTextWatcher());

        edtxOne.setOnKeyListener(new OtpTextBackWatcher());
        edtxTwo.setOnKeyListener(new OtpTextBackWatcher());
        edtxThree.setOnKeyListener(new OtpTextBackWatcher());
        edtxFour.setOnKeyListener(new OtpTextBackWatcher());


    }*/

    private class OtpTextWatcher implements TextWatcher {


        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogI("Gofer", "Textchange");
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogI("Gofer", "Textchange");
            if (edtxOne.isFocused()) {
                if (edtxOne.getText().toString().length() > 0)     //size as per your requirement
                {
                    edtxTwo.requestFocus();
                    edtxTwo.setSelectAllOnFocus(true);
                    //one.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                }
            } else if (edtxTwo.isFocused()) {
                if (edtxTwo.getText().toString().length() > 0)     //size as per your requirement
                {
                    edtxThree.requestFocus();
                    edtxThree.setSelectAllOnFocus(true);
                    //two.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                } else {
                    edtxOne.requestFocus();
                    edtxOne.setSelectAllOnFocus(true);
                    // edtxOne.setSelection(1);
                }
            } else if (edtxThree.isFocused()) {
                if (edtxThree.getText().toString().length() > 0)     //size as per your requirement
                {
                    edtxFour.requestFocus();
                    edtxFour.setSelectAllOnFocus(true);
                    //three.setBackgroundResource(R.drawable.d_buttomboardermobilenumber);
                } else {
                    edtxTwo.requestFocus();
                    edtxTwo.setSelectAllOnFocus(true);
                    //edtxTwo.setSelection(1);
                }
            } else if (edtxFour.isFocused()) {
                if (edtxFour.getText().toString().length() == 0) {
                    edtxThree.requestFocus();
                }
            }

            if (edtxOne.getText().toString().trim().length() > 0 && edtxTwo.getText().toString().trim().length() > 0 && edtxThree.getText().toString().trim().length() > 0 && edtxFour.getText().toString().trim().length() > 0) {
                pin = edtxOne.getText().toString().trim() + edtxTwo.getText().toString().trim() + edtxThree.getText().toString().trim() + edtxFour.getText().toString().trim();
                fab_verify.setCardBackgroundColor(getResources().getColor(R.color.text_black));
                fab_verify.setEnabled(true);
            } else {
                pin = "";
                fab_verify.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey400));
                fab_verify.setEnabled(false);
            }
          //  tvOTPErrorMessage.setVisibility(View.GONE);
        }

        public void afterTextChanged(Editable editable) {
            DebuggableLogI("Gofer", "Textchange");

        }
    }

    private class OtpTextBackWatcher implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            DebuggableLogD("keycode", keyCode + "");
            DebuggableLogD("keyEvent", event.toString());
            if (keyCode == KeyEvent.KEYCODE_DEL && isDeletable) {
                switch (v.getId()) {
                    case R.id.one: {
                        edtxOne.getText().clear();
                        break;
                    }
                    case R.id.two: {
                        edtxTwo.getText().clear();
                        edtxOne.requestFocus();
                     //   edtxOne.setSelectAllOnFocus(true);
                        break;
                    }
                    case R.id.three: {
                        edtxThree.getText().clear();
                        edtxTwo.requestFocus();
                      //  edtxTwo.setSelectAllOnFocus(true);
                        break;
                    }
                    case R.id.four: {
                        edtxFour.getText().clear();
                        edtxThree.requestFocus();
                      //  edtxThree.setSelectAllOnFocus(true);
                        //edtxThree.setSelection(1);
                        break;
                    }

                }
              //  countdownTimerForOTPBackpress();
                return true;
            }else{
                return false;
            }

        }
    }

    private void startAFreshCharge() {
        // initialize the charge
        charge = new Charge();
        charge.setCard(card);
        dialog = new ProgressDialog(GladePaycardActivity.this);
        dialog.setMessage("Performing transaction... please wait");
        dialog.show();
        dialog.setCancelable(false);
        System.out.println("amount"+Double.parseDouble(edt_amount.getText().toString().trim()));
        charge.setAmount(Double.parseDouble(edt_amount.getText().toString().trim()));
        System.out.println("amount"+Double.parseDouble(edt_amount.getText().toString().trim()));
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
        if (isInternetAvailable) {
            GladepaySdk.chargeCard(GladePaycardActivity.this, charge, new Gladepay.TransactionCallback() {
                // This is called only after transaction is successful
                @Override
                public void onSuccess(PTransaction transaction) {

                    GladePaycardActivity.this.transaction = transaction;
                    mTextError.setText(" ");
                    //  Toast.makeText(GladePaycardActivity.this, transaction.getTransactionRef(), Toast.LENGTH_LONG).show();
                    //    updateTextViews();
                    if (type.equalsIgnoreCase("walletpage")) {
                        System.out.println("Wallet Page");
                        addwalletAmount();
                        System.out.println("isWalletpage" + isGladePayinWalletpage);
                        // addwalletAmount();
                   /* Intent walletpage=new Intent(GladePaycardActivity.this, AddWalletActivity.class);
                    startActivity(walletpage);
                    finish();*/
                    }
                    if (type.equalsIgnoreCase("paypage")) {
                        addwalletAmount();
                        Intent mainPage = new Intent(GladePaycardActivity.this, MainActivity.class);
                        startActivity(mainPage);
                        finish();
                    }
                    if (type.equalsIgnoreCase("paymentpage")) {
                        addwalletAmount();
                        System.out.println("isWalletpage" + isGladePayinWalletpage);
                   /* if(isGladePayinWalletpage)
                    {
                        Intent walletpage=new Intent(GladePaycardActivity.this, PaymentPage.class);
                        startActivity(walletpage);
                        finish();
                    }*/

                   /* Intent walletpage=new Intent(GladePaycardActivity.this, PaymentPage.class);
                    startActivity(walletpage);
                    finish();*/
                    }
                    //               new verifyOnServer().execute(transaction.getTransactionRef());
                }

                // This is called only before requesting OTP
                // Save reference so you may send to server if
                // error occurs with OTP
                // No need to dismiss dialog
                @Override
                public void beforeValidate(PTransaction transaction) {
                    dismissDialog();
                    GladePaycardActivity.this.transaction = transaction;
                    //   Toast.makeText(GladePaycardActivity.this, transaction.getTransactionRef(), Toast.LENGTH_LONG).show();
                    //   updateTextViews();
                }
                @Override
                public void onError(Throwable error, PTransaction transaction) {
                    // If an access code has expired, simply ask your server for a new one
                    // and restart the charge instead of displaying error

                    GladePaycardActivity.this.transaction = transaction;
                    if (error instanceof ExpiredAccessCodeException) {
                        GladePaycardActivity.this.startAFreshCharge();
                        GladePaycardActivity.this.chargeCard();
                        return;
                    }
                    dismissDialog();
                    if (transaction.getTransactionRef() != null) {
                      //  Toast.makeText(GladePaycardActivity.this, getResources().getString(R.string.enter_valid_otp), Toast.LENGTH_LONG).show();
                          Toast.makeText(GladePaycardActivity.this,   error.getMessage(), Toast.LENGTH_LONG).show();
                          mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getTransactionRef(), error.getClass().getSimpleName(), error.getMessage()));
                    } else {
                      //  Toast.makeText(GladePaycardActivity.this, getResources().getString(R.string.enter_valid_otp), Toast.LENGTH_LONG).show();
                         Toast.makeText(GladePaycardActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                         mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));
                    }
                    // updateTextViews();
                }
            });
        } else {
            dismissDialog();
            commonMethods.showMessage(GladePaycardActivity.this, dialog1, getString(R.string.no_connection));
        }
    }


    public void afterPayment() {
        if (isInternetAvailable) {
            //commonMethods.showProgressDialog(this, customDialog);

            String gladepaydetail = sessionManager.getGladepaydetail();
            System.out.println("gladepaydetails"+gladepaydetail);
            try {
                JSONObject jsonObj = new JSONObject(gladepaydetail);

                System.out.println("response"+jsonObj.toString());

                 gladePayResponseModel = gson.fromJson(jsonObj.toString(), GladePayResponseModel.class);
                apiService.after_paymentUsingGladePay(sessionManager.getAccessToken(),gladePayResponseModel.getChargedAmount(),gladePayResponseModel.getCardToken(),gladePayResponseModel.getCard().getMask(),sessionManager.getTripId(),gladePayResponseModel.getTxnRef()).enqueue(new RequestCallback(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /*apiService.after_paymentUsingGladePay(sessionManager.getAccessToken(),payable_amt,card_token,card
                    sessionManager.getTripId()
            ).enqueue(new RequestCallback(REQ_AFTER_PAY, this));*/
        } else {
            commonMethods.showMessage(this, dialog1, getResources().getString(R.string.no_connection));
        }

    }
    public void addwalletAmount()
    {
        if (isInternetAvailable) {
            String gladepaydetail = sessionManager.getGladepaydetail();
            System.out.println("gladepaydetails"+gladepaydetail);
            System.out.println("cardToken after auth pin"+gladepaycard_tokenusingAuth_pin);
            try {
                JSONObject jsonObj = new JSONObject(gladepaydetail);
                System.out.println("gladepaydetails response"+jsonObj.toString());
                 gladePayResponseModel = gson.fromJson(jsonObj.toString(), GladePayResponseModel.class);
                 if(!gladepaycard_tokenusingAuth_pin.isEmpty())
                 {
                     gladePayResponseModel.setCardToken(gladepaycard_tokenusingAuth_pin);
                 }
                System.out.println("getcardtoken"+gladePayResponseModel.getCardToken());
                apiService.addWalletMoneyUsingStripe(sessionManager.getAccessToken(),gladePayResponseModel.getChargedAmount(),gladePayResponseModel.getCardToken(),gladePayResponseModel.getCard().getBrand(),gladePayResponseModel.getCard().getMask()).enqueue(new RequestCallback(this));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            commonMethods.showMessage(GladePaycardActivity.this, dialog1, getString(R.string.no_connection));
        }
    }

  /*  private void addwalletAmountUsingGladePay() {
        apiService.addWalletMoneyUsingStripe(sessionManager.getAccessToken(),gladePayResponseModel.getChargedAmount(),gladePayResponseModel.getCardToken(),gladePayResponseModel.getCard().getBrand(),gladePayResponseModel.getCard().getMask()).enqueue(new RequestCallback(REQ_ADD_WALLET,this));

    }*/
    private void updateTextViews() {
        if (transaction.getTransactionRef() != null) {
            Toast.makeText(this, "Reference: s"+transaction.getTransactionRef() + " " + transaction.getMessage(), Toast.LENGTH_SHORT).show();
           // mTextReference.setText(String.format("Reference: %s", transaction.getTransactionRef() + " " + transaction.getMessage()));
        } else {
            Toast.makeText(this,"No transaction" , Toast.LENGTH_SHORT).show();

           //    mTextReference.setText("No transaction");
        }
    }
    private void dismissDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    private Card retrieveAndPopulateCardFromForm() {
        //validate Fafields
        Card card;
        String cardNum = edt_cardNumber.getText().toString().trim();
        System.out.println("cardNumber"+cardNum);

        //build card object with ONLY the number, update the other fields later
        card = new Card.CardBuilder(cardNum, 0, 0, "").build();

        String cvc = edt_CVC.getText().toString().trim();
        //update the cvc field of the card
        card.setCvc(cvc);
        //validate expiry month;
        String sMonth = edt_exp_date_month.getText().toString().trim();
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = edt_expire_year.getText().toString().trim();
        int year = 0;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }
        card.setExpiryYear(year);

      /*  String spin = edt_pin.getText().toString().trim();
        int pin = 0;
        try {
            pin = Integer.parseInt(spin);
        } catch (Exception ignored) {
        }
        card.setPin(pin);*/
        System.out.println("FirstName"+sessionManager.getFirstName());
        card.setName(sessionManager.getFirstName()+" "+sessionManager.getLastName());
        System.out.println("card details"+card.getName());
        return card;

    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        dismissDialog();
        if (jsonResp.isSuccess()) {
            /*{"status_message":"Amount Added Successfully","status_code":"1","wallet_amount":"250.00"}*/
            String walletamount =(String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "wallet_amount", String.class);
                 /*  Integer prev_walletAmount= Integer.valueOf(wallet_amt.getText().toString().trim());
                    System.out.println("lastWalletAmount"+prev_walletAmount+Integer.parseInt(walletamount));
                    String lastwalletamount= String.valueOf(prev_walletAmount+walletamount);*/
            System.out.println("setWalletAmountonGladepay"+walletamount);
            sessionManager.setWalletAmount(walletamount);
           // wallet_amt.setText(sessionManager.getCurrencySymbol() + sessionManager.getWalletAmount());
            commonMethods.hideProgressDialog();
            if(isGladePayinWalletpage)
            {
                startActivity(new Intent(GladePaycardActivity.this,AddWalletActivity.class));
                sessionManager.setWalletPaymentMethod(PAYMENT_GLADEPAY);
                last4=gladePayResponseModel.getCard().getMask();
                card_token=gladePayResponseModel.getCardToken();
                sessionManager.setCardBrand(gladePayResponseModel.getCard().getBrand());
                sessionManager.setCardValue(gladePayResponseModel.getCard().getMask());
                finish();
            }
            else
            {
                isGladePayinPaymentpage=true;
                onBackPressed();
            }
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {

            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog1, jsonResp.getStatusMsg());
            DebuggableLogV("jsonResp.getStatusMsg()", "" + jsonResp.getStatusMsg());
        }
      //  Toast.makeText(this, jsonResp.getStrResponse(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        dismissDialog();
        Toast.makeText(this, jsonResp.getErrorMsg(), Toast.LENGTH_SHORT).show();
    }
    private class CardTextWatcher implements TextWatcher {
        boolean ignoreChanges = false;
        int latestChangeStart;
        int latestInsertionSize;
        String[] parts = new String[2];
        private View view;
        public CardTextWatcher(View view) {
            this.view=view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            DebuggableLogV("beforeTextChanged", "beforeTextChanged");
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(validateCVC()&&validatecard()&&validateExpireDate()&&validateExpireDateYear()&&validateAmount())
            {
                System.out.println("cardNumber"+Validate(edt_cardNumber.getText().toString().trim(),edt_cardNumber,txt_input_card_number));

                fab_verify.setEnabled(true);
                fab_verify.setCardBackgroundColor(getResources().getColor(R.color.text_black));
               /* pinTitle.setVisibility(View.VISIBLE);
                rl_edittexts.setVisibility(View.VISIBLE);
                txt_input_amount.setVisibility(View.VISIBLE);*/
            }
            else
            {
                System.out.println("cardNumber"+Validate(edt_cardNumber.getText().toString().trim(),edt_cardNumber,txt_input_card_number));
                fab_verify.setEnabled(false);
                fab_verify.setCardBackgroundColor(getResources().getColor(R.color.button_disable_color));
               /* pinTitle.setVisibility(View.GONE);
                rl_edittexts.setVisibility(View.GONE);
                txt_input_amount.setVisibility(View.GONE);*/
            }
           /* if(validateExpireDate()&& validatecard() && validateCVC())
            {
                pinTitle.setVisibility(View.VISIBLE);
                rl_edittexts.setVisibility(View.VISIBLE);
                fab_verify.setEnabled(true);
                fab_verify.setCardBackgroundColor(getResources().getColor(R.color.text_black));
            }
            else
            {
                pinTitle.setVisibility(View.GONE);
                rl_edittexts.setVisibility(View.GONE);
                fab_verify.setEnabled(false);
                fab_verify.setCardBackgroundColor(getResources().getColor(R.color.quantum_grey400));
               *//* fab_verify.setEnabled(false);
                fab_verify.setBackgroundColor(getResources().getColor(R.color.button_disable_color));*//*
            }*/
        }
        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId())
            {
                case R.id.edt_card_number:
                    //validatecard();
                  //  Validate(edt_cardNumber.getText().toString().trim(),edt_cardNumber,txt_input_card_number);
                    getCardType(edt_cardNumber.getText().toString().trim());
                    break;
                case R.id.edt_expire_date:
                    validateExpireDate();
                  //  Validate(edt_exp_date_month.getText().toString().trim(),edt_exp_date_month,input_layout_expire_date);
                    getexpiredate(edt_exp_date_month.getText().toString().trim());
                    break;
                case R.id.edt_expire_year:
                    validateExpireDateYear();
                   // Validate(edt_expire_year.getText().toString().trim(),edt_expire_year,input_layout_expire_date);
                    getexpiredyear(edt_expire_year.getText().toString().trim());
                    break;
                case R.id.edt_cvc:
                    validateCVC();
                    getcvc();
                  //  Validate(edt_CVC.getText().toString().trim(),edt_CVC,input_layout_cvc);
                    break;
                case R.id.edt_amount:
                    Validate(edt_amount.getText().toString().trim(),edt_amount,txt_input_amount);
                    break;
                case R.id.edt_pin:
                   // validatePin(edt_pin.getText().toString().trim());
                    Validate(edt_pin.getText().toString().trim(),edt_pin,txt_input_pin);
                    getPin();
                    break;
            }
          /* String  source=s.toString();
            int length=source.length();
            if(length>0)
            {
                Card  card = new Card.CardBuilder(source.trim(), 0, 0, "").build();

                card.setType(card.getType());

                System.out.println("Type"+card.getType());
                if(card.validNumber())
                {
                  *//*  for(int i=0;i<cardTypesList.size();i++){
                        String cardType=cardTypesList.get(i);
                        if (card.getType().equalsIgnoreCase(cardType)) {
                           edt_cardNumber.setCompoundDrawablesWithIntrinsicBounds(cardTypesListImage.get(i),0,0,0);
                        }
                    }*//*
                    edt_cardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(s.length())});
                }

            }*/
        }
    }
    private void getcvc() {

        if(edt_CVC.getText().toString().trim().length()>=3)
        {
            System.out.println("Card type"+Card.CardType.AMERICAN_EXPRESS.equalsIgnoreCase(card_type));
            if(Card.CardType.AMERICAN_EXPRESS.equalsIgnoreCase(card_type)) {
                System.out.println("American Express");
                edt_CVC.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});

            }
            else
            {
                System.out.println("CardType"+card_type);
                edt_CVC.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});

            }
        }
      /*  if((Card.CardType.AMERICAN_EXPRESS.equalsIgnoreCase(type)))
        {
            if(edt_CVC.getText().toString().trim().length()==4)
            {
                edt_CVC.setFilters(new InputFilter[] {new InputFilter.LengthFilter(edt_CVC.getText().toString().trim().length())});
                return;
            }
        }
        else if(edt_CVC.getText().toString().trim().length()==3)
        {
            edt_CVC.setFilters(new InputFilter[] {new InputFilter.LengthFilter(edt_CVC.getText().toString().trim().length())});
            return;
        }*/

    }

    private boolean validatepin() {
        if(edt_pin.getText().toString().trim().isEmpty())
        {
          //  edt_pin.setError("Please Enter your valid card number!");
            return false;
        }
       /* else
        {
            txt_input_pin.setErrorEnabled(false);
        }*/
        return true;
    }
    private boolean validateAmount() {
        if(edt_amount.getText().toString().trim().isEmpty() && edt_amount.getText().toString()==null)
        {
          //  edt_amount.setError("Please Enter your valid card number!");
            return false;
        }
      /*  else
        {
            txt_input_amount.setErrorEnabled(false);
        }*/
        return true;
    }
    /*private boolean validatePin(String pin)
    {
        if(pin.isEmpty() && pin.length()>3)
        {
            edt_pin.setError("Please Enter your valid pin");
            return false;
        }
        txt_input_pin.setErrorEnabled(false);
        return true;
    }*/

    private void getexpiredyear(String expiredate)
    {
        // System.out.println("pin length"+edt_exp_date_month.getText().toString().trim().length());
        //  int date=Integer.parseInt(expiredate.trim());
        if(expiredate.length()==2)
        {
            edt_expire_year.setFilters(new InputFilter[] {new InputFilter.LengthFilter(expiredate.length())});
        }
    }
    private void getexpiredate(String expiredate)
    {
       // System.out.println("pin length"+edt_exp_date_month.getText().toString().trim().length());
      //  int date=Integer.parseInt(expiredate.trim());
        if(expiredate.length()==2)
        {
            edt_exp_date_month.setFilters(new InputFilter[] {new InputFilter.LengthFilter(expiredate.length())});
        }
    }
    private void getPin()
    {
        System.out.println("pin length"+edt_pin.getText().toString().trim().length());
        if(edt_pin.getText().toString().trim().length()==4)
        {
            edt_pin.setFilters(new InputFilter[] {new InputFilter.LengthFilter(edt_pin.getText().toString().trim().length())});
        }
    }
   private boolean Validate(String text,EditText edt,TextInputLayout textInputLayout)
    {
        if(text.isEmpty() || text ==null) return false;

          //  edt.setError("Please Enter your valid details");


       /* else
        {
            textInputLayout.setErrorEnabled(false);
        }*/
        return true;
    }

    private boolean validatecard() {
        if(edt_cardNumber.getText().toString().trim().isEmpty())
        {
           // edt_cardNumber.setError("Please Enter your valid card number!");
            edt_CVC.setText("");
            return false;
        }
        return true;
    }

    private boolean validateExpireDate() {
        // edt_exp_date_month.setError("Please Enter your valid card expirationMonth!");
        return !edt_exp_date_month.getText().toString().trim().isEmpty();
      /*  else
        {
            input_layout_expire_date.setErrorEnabled(false);
        }*/
    }
    private boolean validateExpireDateYear() {
        if(edt_expire_year.getText().toString().trim().isEmpty())
        {
           // edt_expire_year.setError("Please Enter your valid card expirationYear!");
            return false;
        }
       /* else
        {
          //  input_layout_expire_date.setErrorEnabled(false);
        }*/
        return true;
    }
    private boolean validateCVC() {
        if(edt_CVC.getText().toString().trim().isEmpty())
        {
          //  edt_CVC.setError("Please Enter your valid card CVC!");
            return false;
        }
       /* else
        {
          //  input_layout_cvc.setErrorEnabled(false);
        }*/
        return true;
    }
    private void getCardType(String number) {
            card = new Card.CardBuilder(number, 0, 0, "").build();
            if(card.validNumber(this))
            {
                //card.setType(card.getType());
                 card_type=card.getType();
                System.out.println("card type"+card.getType());
                Drawable image=getCardImage(card_type,getResources());
                edt_cardNumber.setCompoundDrawablesWithIntrinsicBounds(null,null,image,null);
                edt_cardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(number.length())});
            }
            else
            {
                edt_cardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(18)});
                System.out.println("card invalid "+edt_cardNumber.getText().toString());
                edt_cardNumber.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.card_resize),null);
            }


           // txt_input_card_number.setErrorEnabled(false);

       // Card  card = new Card.CardBuilder(number, 0, 0, "").build();
        /* boolean isvaild = card.validNumber();
          if(!isvaild)
          {
            //  Drawable image=getCardImage(type,getResources());
              edt_cardNumber.setError("Please Enter your valid cardnumber!");
              edt_cardNumber.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.card_resize),null,null,null);
              return false;
          }*/

       /* txt_input_card_number.setErrorEnabled(false);
        card.setType(card.getType());
        String type=card.getType();
        System.out.println("type"+card.getType());
        Drawable image=getCardImage(type,getResources());
        edt_cardNumber.setCompoundDrawablesWithIntrinsicBounds(image,null,null,null);
        edt_cardNumber.setFilters(new InputFilter[] {new InputFilter.LengthFilter(number.length())});
        return true;*/
      //  Drawable drawable = ContextCompat.getDrawable(this,image);
      /*  image.setBounds(0, 0, (int)(image.getIntrinsicWidth()*0.5),
                (int)(image.getIntrinsicHeight()*0.5));
        ScaleDrawable sd = new ScaleDrawable(image, 0, 40, 40);
       *//* card = sd.getDrawable();*//*
        System.out.println("Image"+image);*/
     /*   image.setBounds(0, 0, (int) (image.getIntrinsicWidth() * 0.6),
                (int) (image.getIntrinsicHeight() * 0.6));
        ScaleDrawable sd = new ScaleDrawable(image, 0, 40, 40);*/
        // image.setBounds(40,40,40,40);




      /*  Drawable drawable = new BitmapDrawable(bitmap);
      LayerDrawable layerDrawable= layerDrawable.setDrawableByLayerId(R.id.dynamicItem, drawable);*/
       /* LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{getResources().getDrawable(R.drawable.card_master_resize)});
        layerDrawable.setLayerInset(0,10,10,10,10);*/
       // LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{getResources().getDrawable(R.drawable.card_master_resize)});
       // layerDrawable.findDrawableByLayerId(R.id.card_visa);

    }

}
