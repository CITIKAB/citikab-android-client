package com.trioangle.gofer.GladePay;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.trioangle.gofer.BuildConfig;
import com.trioangle.gofer.GladePay.utils.Utilities;
import com.trioangle.gofer.utils.CommonKeys;

import static com.trioangle.gofer.utils.CommonKeys.isPaymentwithToken;


public final class GladepaySdk {

    /**
     * Value for the version code of this sdk
     */
    public static final int VERSION_CODE = BuildConfig.VERSION_CODE;
    /**
     * key for public key property in the AndroidManifest.xml
     */
    private static final String KEY_PUBLIC_KEY_PROP = "com.gladepay.android.PublicKey";
    public static Context applicationContext;
    /**
     * Flag to know if sdk has been initialized
     */
    private static boolean sdkInitialized;

    /**
     * Reference to the merchantkey
     */
    private static volatile String merchantKey;

    /**
     * Reference to the merchantId
     */
    private static volatile String merchantId;


    /**
     * Reference to the stores serverMode status
     */
    private static boolean liveStatus;

    /**
     * Initialize GladepaySDK with a callback when has been initialized successfully.
     *
     * @param applicationContext - Application Context
     * @param initializeCallback - callback to execute after initializing
     */
    private static synchronized void initialize(Context applicationContext, SdkInitializeCallback initializeCallback) {
        //do all the init work here

        //check if initialize callback is set and sdk is actually intialized
        if (initializeCallback != null && sdkInitialized) {
            initializeCallback.onInitialized();
            return;
        }

        //null check for applicationContext
        Utilities.Validate.validateNotNull(applicationContext, "applicationContext");

        //check for internet permissions
        Utilities.Validate.hasInternetPermission(applicationContext);

        sdkInitialized = true;
        GladepaySdk.applicationContext = applicationContext;

        if (initializeCallback != null) {
            initializeCallback.onInitialized();
        }
    }



    public static synchronized void initialize(Context context) {
        initialize(context,  null);
    }

    private static void loadFromManifest(Context context) {
        if (context == null) {
            return;
        }

        ApplicationInfo applicationInfo;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA
            );
        } catch (PackageManager.NameNotFoundException e) {
            return;
        }

        //check if we can get any metadata, return if not
        if (applicationInfo == null || applicationInfo.metaData == null) {
            return;
        }
    }
    public static boolean isSdkInitialized() {
        return sdkInitialized;
    }

    public static void chargeCard(Activity activity, Charge charge, Gladepay.TransactionCallback transactionCallback) {
        if (BuildConfig.DEBUG && (activity == null)) {
            throw new AssertionError("activity must not be null");
        }

        performChecks();

        //construct paystack object
        Gladepay gladepay = new Gladepay(GladepaySdk.getMerchantId(), GladepaySdk.getMerchantKey(), GladepaySdk.getLiveStatus());
        System.out.println("isPaymentwithToken"+isPaymentwithToken);
      /*  if(isPaymentwithToken)
        {
            PaymentTransactionManager transactionManager = new PaymentTransactionManager(activity, charge, transactionCallback);
            transactionManager.chargeCard();
        }
        else
        {*/
            gladepay.chargeCard(activity, charge, transactionCallback);
      /*  }*/
        //create token


    }

    private static void performChecks() {
        //validate that sdk has been initialized
        Utilities.Validate.validateSdkInitialized();

    }

    public static void setMerchantKey(String merchantKey) {
        GladepaySdk.merchantKey = merchantKey;
    }

    public static void setLiveStatus(boolean liveStatus) {
        GladepaySdk.liveStatus = liveStatus;
    }

    public static void setMerchantId(String merchantId) {
        GladepaySdk.merchantId = merchantId;
    }

    public static String getMerchantId() {
        return merchantId;
    }

    public static String getMerchantKey() {
        return merchantKey;
    }

    public static boolean getLiveStatus() {
        return liveStatus;
    }


    public interface SdkInitializeCallback {
        void onInitialized();
    }

}