package com.trioangle.gofer.sidebar.payment;

/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.payment
 * @category PaymentPage
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trioangle.gofer.GladePay.GladePaycardActivity;
import com.trioangle.gofer.GladePay.GladepaySdk;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.Enums;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.addCardDetails.AddCardActivity;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonKeys.Brand;
import static com.trioangle.gofer.utils.CommonKeys.PAYMENT_CASH;
import static com.trioangle.gofer.utils.CommonKeys.PAYMENT_GLADEPAY;
import static com.trioangle.gofer.utils.CommonKeys.REQUEST_CODE_OPEN_ADD_CARD_ACTIVITY;
import static com.trioangle.gofer.utils.CommonKeys.REQ_GladePayonPaymentPage;

import static com.trioangle.gofer.utils.CommonKeys.isGladePayinPaymentpage;
import static com.trioangle.gofer.utils.CommonKeys.isLiveServer;
import static com.trioangle.gofer.utils.CommonKeys.last4;
import static com.trioangle.gofer.utils.CommonKeys.merchantId;
import static com.trioangle.gofer.utils.CommonKeys.merchantKey;
import static com.trioangle.gofer.utils.Enums.REQ_ADD_CARD;
import static com.trioangle.gofer.utils.Enums.REQ_ADD_PROMO;
import static com.trioangle.gofer.utils.Enums.REQ_GET_PROMO;

/* ************************************************************
    Rider can select the payment method
    *********************************************************** */
public class PaymentPage extends AppCompatActivity implements ServiceListener {

    public AlertDialog dialog2;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    CustomDialog customDialog;

    public @InjectView(R.id.wallet_amt)
    TextView wallet_amt;
    public @InjectView(R.id.promo_count)
    TextView promo_count;

    public @InjectView(R.id.tv_add_or_change_card)
    TextView addCreditOrDebitCardTextView;


    public @InjectView(R.id.tv_alreadyAvailableCardNumber)
    TextView alreadyAvailableCardNumber;
    public @InjectView(R.id.arrow)
    ImageView arrow;
    public @InjectView(R.id.wallet_tickimg)
    ImageView wallettick_img;
    public @InjectView(R.id.paypal_tickimg)
    ImageView paypaltick_img;
    public @InjectView(R.id.cash_tickimg)
    ImageView cashtick_img;
    public @InjectView(R.id.imgView_alreadyAvailableCard_tickimg)
    ImageView cardtick_img;
    public @InjectView(R.id.imgView_alreadyAvailableCardimg)
    ImageView imgViewAlreadyAvailableCardimg;
    public @InjectView(R.id.cash)
    RelativeLayout cashLayout;
    public @InjectView(R.id.wallet)
    RelativeLayout wallet;
    public @InjectView(R.id.alreadyAvailableCreditOrDebitCard)
    RelativeLayout alreadyAvailableCardDetails;
    public @InjectView(R.id.promo)
    RelativeLayout promo;
    public @InjectView(R.id.rlt_promotions)
    RelativeLayout completePromotionsLayoutIncludingTitle;
    public @InjectView(R.id.rlt_wallet)
    RelativeLayout completeWalletLayoutIncludingTitle;
    public BottomSheetDialog dialog;
    public EditText input_promo_code;
    public Button cancel_promo_btn;
    public Button add_promo_btn;
    protected boolean isInternetAvailable;
    private String Gladepayamount;

    public @InjectView(R.id.Gladepay)
    RelativeLayout Gladepaylayout;

    @OnClick(R.id.Gladepay)
    public void payPalclick() {
        /**
         * Payment method paypal clicked
         */
        if(isInternetAvailable)
        {
            Intent gladepaycardActivity = new Intent(PaymentPage.this, GladePaycardActivity.class);
            gladepaycardActivity.putExtra("type", "paymentpage");
            gladepaycardActivity.putExtra("amount", Gladepayamount);
            startActivityForResult(gladepaycardActivity, REQ_GladePayonPaymentPage);
        }
        else
        {
            commonMethods.showMessage(PaymentPage.this, dialog2, getString(R.string.no_connection));
        }

      /*  showPaymentTickAccordingToTheSelection();
        sessionManager.setWalletPaymentMethod(CommonKeys.PAYMENT_GLADEPAYwithCard);
        sessionManager.setPaymentMethod(CommonKeys.PAYMENT_GLADEPAYwithCard);
        onBackPressed();*/
    }
    @OnClick(R.id.cash)
    public void cash() {
        /**
         * Payment method cashLayout click
         */
        showPaymentTickAccordingToTheSelection();
       /* if(sessionManager.getCardValue() !=null && !sessionManager.getCardValue().isEmpty())
            sessionManager.setWalletPaymentMethod(CommonKeys.PAYMENT_GLADEPAYwithToken);
        else sessionManager.setWalletPaymentMethod(CommonKeys.PAYMENT_GLADEPAYwithCard);*/
        //   sessionManager.setWalletPaymentMethod(PAYMENT_GLADEPAY);
        sessionManager.setPaymentMethod(CommonKeys.PAYMENT_CASH);
        onBackPressed();
    }
    @OnClick(R.id.addCreditOrDebitCard)
    public void creditOrDebitCard() {
        if ((commonMethods.isOnline(this))) {
            Intent stripe = new Intent(getApplicationContext(), GladePaycardActivity.class);
            stripe.putExtra("amount", Gladepayamount);
            startActivity(stripe);
            // stripe.putExtra("ActionbarHeading", addCreditOrDebitCardTextView.getText().toString());
            //   startActivityForResult(stripe, REQUEST_CODE_OPEN_ADD_CARD_ACTIVITY);
        } else {
            showPaymentTickAccordingToTheSelection();
            sessionManager.setWalletPaymentMethod(PAYMENT_GLADEPAY);
            sessionManager.setPaymentMethod(PAYMENT_GLADEPAY);
            onBackPressed();
        }
        //startActivity();
    }
    @OnClick(R.id.alreadyAvailableCreditOrDebitCard)
    public void selectPaymentAsCard() {
        showPaymentTickAccordingToTheSelection();
        sessionManager.setWalletPaymentMethod(PAYMENT_GLADEPAY);
        sessionManager.setPaymentMethod(PAYMENT_GLADEPAY);
        onBackPressed();
    }
    @OnClick(R.id.add_promo)
    public void addPromoclick() {
        /**
         *  Add promo code
         */
        addPromo();
    }
    @OnClick(R.id.wallet)
    public void wallet() {
        /**
         * Payment method wallet added
         */
        if (!wallettick_img.isEnabled()) {
            wallettick_img.setVisibility(View.VISIBLE);
            wallettick_img.setEnabled(true);
            sessionManager.setIsWallet(true);
        } else {
            sessionManager.setIsWallet(false);
            wallettick_img.setEnabled(false);
            wallettick_img.setVisibility(View.GONE);
        }
    }
    @OnClick(R.id.promo)
    public void promo() {
        /**
         * Promocode add click
         */
        Intent promo_details = new Intent(getApplicationContext(), PromoAmountActivity.class);
        startActivity(promo_details);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }
    @OnClick(R.id.arrow)
    public void arrow() {
        onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // common declerations
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_page);
        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this);
        dialog2 = commonMethods.getAlertDialog(this);
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        Gladepaylayout.setVisibility(View.VISIBLE);
        showPaymentTickAccordingToTheSelection();
        //start activity views according to the type
        proceedActivityAccordingToType(getIntent().getIntExtra(CommonKeys.TYPE_INTENT_ARGUMENT_KEY, CommonKeys.StatusCode.startPaymentActivityForView), getIntent().getExtras().getString("amount"));
    }
    private void proceedActivityAccordingToType(@CommonKeys.StatusCode int type, String amount) {
        System.out.println("amount" + amount);
        if (amount != null && !amount.isEmpty()) {
            Gladepayamount = amount;
        }
        switch (type) {
            case CommonKeys.StatusCode.startPaymentActivityForView: {
                setVisibilityForCompletelyViewActivity();
                break;
            }
            case CommonKeys.StatusCode.startPaymentActivityForAddMoneyToWallet: {
                startPaymentActivityForAddMoneyToWallet();
            }
            case CommonKeys.StatusCode.startPaymentActivityForChangePaymentOption: {
                startPaymentActivityForChangePaymentOption();
                break;
            }
            case CommonKeys.StatusCode.startPaymentActivityForChangePaymentOptionGLadepay: {
                startPaymentActivityForChangePaymentRequestcar();
                break;
            }
            default:
                break;
        }
        // getPromoCode();
    }
    private void startPaymentActivityForChangePaymentRequestcar() {
       /* if (sessionManager.getPaymentMethod().equalsIgnoreCase(PAYMENT_GLADEPAY)) {
           // alreadyAvailableCardDetails.setVisibility(View.VISIBLE);
          //  Gladepaylayout.setVisibility(View.GONE);
        }*/
        if (sessionManager.getIsWallet() != null && sessionManager.getIsWallet()) {
            wallettick_img.setEnabled(true);
            wallettick_img.setVisibility(View.VISIBLE);
        } else {
            wallettick_img.setEnabled(false);
            wallettick_img.setVisibility(View.GONE);
        }
        //Gladepaylayout.setVisibility(View.GONE);
    }

    private void startPaymentActivityForAddMoneyToWallet() {
        cashLayout.setVisibility(View.GONE);
        hideWalletAndPromotionsLayout();
        if (sessionManager.getWalletPaymentMethod().equalsIgnoreCase(PAYMENT_GLADEPAY)) {
            paypaltick_img.setVisibility(View.GONE);
            cardtick_img.setVisibility(View.VISIBLE);
        } else {
            paypaltick_img.setVisibility(View.VISIBLE);
            cardtick_img.setVisibility(View.GONE);
        }
      /*  else
        {
            paypaltick_img.setVisibility(View.GONE);
            cardtick_img.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume Walletamount" + sessionManager.getWalletAmount());
        wallet_amt.setText(sessionManager.getCurrencySymbol() + sessionManager.getWalletAmount());
        getPromoCode();
        //  wallet_amt.setText(sessionManager.getCurrencySymbol() + sessionManager.getWalletAmount());
    }

    private void startPaymentActivityForChangePaymentOption() {
        hideWalletAndPromotionsLayout();
    }

    private void hideWalletAndPromotionsLayout() {
        completeWalletLayoutIncludingTitle.setVisibility(View.GONE);
        completePromotionsLayoutIncludingTitle.setVisibility(View.GONE);
    }

    public void showPaymentTickAccordingToTheSelection() {
        switch (sessionManager.getPaymentMethod()) {
           /* case CommonKeys.PAYMENT_GLADEPAYwithCard: {
                cardtick_img.setVisibility(View.GONE);
                cashtick_img.setVisibility(View.GONE);
                paypaltick_img.setVisibility(View.VISIBLE);
              //  initGladePay();
                break;
            }*/
            case PAYMENT_GLADEPAY: {
                cardtick_img.setVisibility(View.VISIBLE);
                cashtick_img.setVisibility(View.GONE);
                paypaltick_img.setVisibility(View.GONE);
                break;
            }
         /*   case CommonKeys.PAYMENT_CARD: {
                cardtick_img.setVisibility(View.VISIBLE);
                cashtick_img.setVisibility(View.GONE);
                paypaltick_img.setVisibility(View.GONE);
                break;
            }*/
            case CommonKeys.PAYMENT_CASH: {
                cardtick_img.setVisibility(View.GONE);
                cashtick_img.setVisibility(View.VISIBLE);
                paypaltick_img.setVisibility(View.GONE);
                break;
            }
            default: {
                sessionManager.setPaymentMethod(CommonKeys.PAYMENT_CASH);
                sessionManager.setIsWallet(true);
                cardtick_img.setVisibility(View.GONE);
                paypaltick_img.setVisibility(View.GONE);
                cashtick_img.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    public void setVisibilityForCompletelyViewActivity() {
        //set wallet symbol and balance
        wallet_amt.setText(sessionManager.getCurrencySymbol() + sessionManager.getWalletAmount());
        if (sessionManager.getPromoCount() == 0) {
            promo.setVisibility(View.GONE);
        } else if (sessionManager.getPromoCount() > 0) {
            promo.setVisibility(View.VISIBLE);
            promo_count.setText(String.valueOf(sessionManager.getPromoCount()));
        } else {
            promo.setVisibility(View.GONE);
        }


        if (sessionManager.getIsWallet() != null && sessionManager.getIsWallet()) {
            wallettick_img.setEnabled(true);
            wallettick_img.setVisibility(View.VISIBLE);
        } else {
            wallettick_img.setEnabled(false);
            wallettick_img.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  //Back button pressed
        //  startActivity(new Intent(PaymentPage.this, MainActivity.class));
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    /**
     * on Activity closed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    // method to check if the device is connected to network

    /**
     * Add promo code
     */
    public void addPromo() {

        dialog = new BottomSheetDialog(PaymentPage.this);
        dialog.setContentView(R.layout.add_promo);
        cancel_promo_btn = (Button) dialog.findViewById(R.id.cancel_promo);
        add_promo_btn = (Button) dialog.findViewById(R.id.add_promo);
        input_promo_code = (EditText) dialog.findViewById(R.id.input_promo_code);
        input_promo_code.clearFocus();

        /**
         * Call promo code check API
         */
        add_promo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_promo_code.getText().length() > 0) {
                    String promo_code = input_promo_code.getText().toString();

                    isInternetAvailable = commonMethods.isOnline(getApplicationContext());
                    if (isInternetAvailable) {
                        addPromoCode(promo_code);
                    } else {
                        commonMethods.showMessage(PaymentPage.this, dialog2, getString(R.string.no_connection));
                    }

                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    commonMethods.showMessage(PaymentPage.this, dialog2, getString(R.string.please_enter_promo));
                }
            }
        });
        cancel_promo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Verify promo code API called
     */
    public void getPromoCode() {
        if (isInternetAvailable) {
            commonMethods.showProgressDialog(this, customDialog);
            apiService.promoDetails(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_GET_PROMO, this));
        } else {
            commonMethods.showMessage(PaymentPage.this, dialog2, getString(R.string.no_connection));
        }
    }

    public void addPromoCode(String code) {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.addPromoDetails(sessionManager.getAccessToken(), code).enqueue(new RequestCallback(REQ_ADD_PROMO, this));
    }

    public void addCardDetail(String payKey) {
        //onBackPressed();
        //commonMethods.showProgressDialog(PaymentActivity.this, customDialog);
        if (!TextUtils.isEmpty(payKey)) {
            commonMethods.showProgressDialog(this, customDialog);
            apiService.addCard(payKey, sessionManager.getAccessToken()).enqueue(new RequestCallback(Enums.REQ_ADD_CARD, this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_GladePayonPaymentPage) {
            if (isGladePayinPaymentpage) {
                System.out.println("isGladePay" + isGladePayinPaymentpage);
                sessionManager.setWalletPaymentMethod(PAYMENT_GLADEPAY);
                sessionManager.setPaymentMethod(PAYMENT_GLADEPAY);
                System.out.println("Walletamt"+sessionManager.getWalletAmount());
                showPaymentTickAccordingToTheSelection();

                //  System.out.println("isGladePay waletAmount"+sessionManager.getWalletAmount());
                // wallet_amt.setText(sessionManager.getCurrencySymbol() + sessionManager.getWalletAmount());
                //  onBackPressed();
            }
            // addCardDetail(data.getStringExtra("token"));
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(this, dialog2, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            // Get Promo Details
            case REQ_GET_PROMO:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessPromo(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog2, jsonResp.getStatusMsg());
                }
                break;

            // Add Promo Details
            case REQ_ADD_PROMO:
            case REQ_ADD_CARD:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessPromo(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog2, jsonResp.getStatusMsg());
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
            commonMethods.showMessage(this, dialog2, jsonResp.getStatusMsg());
        }
    }

    public void onSuccessPromo(JsonResponse jsonResp) {
        try {
            JSONObject response = new JSONObject(jsonResp.getStrResponse());
            if (response.has("promo_details")) {
                int promocount = response.getJSONArray("promo_details").length();
                sessionManager.setPromoDetail(response.getString("promo_details"));
                sessionManager.setPromoCount(promocount);
            }
         /*   if (response.has("stripe_key")) {
                stripePublishKey = response.getString("stripe_key");
            }*/
            if (response.has("last4") && !TextUtils.isEmpty(response.getString("last4")) && !response.getString("last4").equalsIgnoreCase("null")) {
                String alreadyAvailableCardLast4Digits = response.getString("last4");
                String alreadyAddedCardbrand = response.getString("brand");
                sessionManager.setCardValue(alreadyAvailableCardLast4Digits);
                sessionManager.setCardBrand(alreadyAddedCardbrand);
                Brand=response.getString("brand");
                last4=response.getString("last4");
                alreadyAvailableCardDetails.setVisibility(View.VISIBLE);
                imgViewAlreadyAvailableCardimg.setImageDrawable(CommonMethods.getCardImage(alreadyAddedCardbrand, getResources()));
                //setCardImage(brand);
                alreadyAvailableCardNumber.setText(alreadyAvailableCardLast4Digits);
                // addCreditOrDebitCardTextViewLabelToChangeCard();
            } else {
                alreadyAvailableCardDetails.setVisibility(View.GONE);
                // addCreditOrDebitCardTextViewLabelToAddCard();
            }

        } catch (JSONException j) {
            j.printStackTrace();
        }

        if (sessionManager.getPromoCount() == 0) {
            promo.setVisibility(View.GONE);
        } else if (sessionManager.getPromoCount() > 0) {
            promo.setVisibility(View.VISIBLE);
            promo_count.setText(String.valueOf(sessionManager.getPromoCount()));
        } else {
            promo.setVisibility(View.GONE);
        }
    }

  /*  public void addCreditOrDebitCardTextViewLabelToAddCard(){
        addCreditOrDebitCardTextView.setText(getString(R.string.credit_or_debit_card));
    }

    public void addCreditOrDebitCardTextViewLabelToChangeCard(){
        addCreditOrDebitCardTextView.setText(getString(R.string.change_credit_or_debit_card));

    }*/

}
