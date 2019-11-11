package com.trioangle.gofer.GladePay;

import android.app.Activity;
import android.util.Log;

import com.google.gson.JsonParser;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class Gladepay {

    private static final String LOG_TAG = Gladepay.class.getSimpleName();
    /**
     * merchantId
     */
    private String merchantId;

    /**
     * merchantKey
     */
    private String merchantKey;

    /**
     * isLive boolean status if live mode or demo mode
     */
    private boolean isLive;



    private GladepayApiService gladepayApiService;


    JsonParser jsonParser = new JsonParser();

    PaymentTransactionManager paymentTransactionManager;

    PTransaction transaction;
    TransactionCallback transactionCallback;



    /**
     * This initializes the Merchant Id and the merchant Key as well as sets the mode
     * If LIVE or DEMO
     * @param merchantId
     * @param merchantKey
     * @param isLive    true - LIVE
     *                  false - DEMO
     *
     */
    public Gladepay(String merchantId, String merchantKey, boolean isLive)
    {

        System.out.println("GladePay");
        this.merchantId = merchantId;
        this.merchantKey = merchantKey;
        this.isLive = isLive;

        try {

            gladepayApiService = new ApiClient(this.merchantId, this.merchantKey, this.isLive).getGladepayApiService();

        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException"+e.getMessage());
            e.printStackTrace();
        } catch (KeyStoreException e) {
            System.out.println("KeyStoreException"+e.getMessage());
            e.printStackTrace();
        } catch (KeyManagementException e) {
            System.out.println("KeyManagementException"+e.getMessage());
            e.printStackTrace();
        }
    }
    public void chargeCard(Activity activity, Charge charge, TransactionCallback transactionCallback) {
        Log.i("GLADEPAY_CLASS: ", "CHARGECARD");
        chargeThisCard(activity, charge, transactionCallback);
    }
    private void chargeThisCard(Activity activity, Charge charge, TransactionCallback transactionCallback) {
        try {
            Log.i("GLADEPAY_CLASS: ", "CHARGE-THIS-CARD");
            PaymentTransactionManager transactionManager = new PaymentTransactionManager(activity, charge, transactionCallback);
            transactionManager.chargeCard();

        } catch (Exception ae) {
            assert transactionCallback != null;
            transactionCallback.onError(ae, null);
        }
    }


    private interface BaseCallback {
    }

    public interface TransactionCallback extends BaseCallback {
        void onSuccess(PTransaction transaction);
        void beforeValidate(PTransaction transaction);

        void onError(Throwable error, PTransaction transaction);
    }


}