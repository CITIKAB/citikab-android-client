package com.trioangle.gofer.views.signinsignup;
/**
 * @package com.trioangle.gofer
 * @subpackage signin_signup
 * @category SigninSignupActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.firebase.iid.FirebaseInstanceId;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.pushnotification.MyFirebaseInstanceIDService;
import com.trioangle.gofer.pushnotification.NotificationUtils;
import com.trioangle.gofer.sidebar.currency.CurrencyModel;
import com.trioangle.gofer.sidebar.language.LanguageAdapter;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.views.facebookAccountKit.FacebookAccountKitActivity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.trioangle.gofer.sidebar.Setting.langclick;
import static com.trioangle.gofer.utils.CommonKeys.*;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
/* ************************************************************
   Sign in and sign up home page
   ************************************************************ */

public class SigninSignupActivity extends AppCompatActivity {

    public static android.app.AlertDialog alertDialogStores;
    private static String TAG = "SigninSignupActivity";
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @InjectView(R.id.connectsocial)
    TextView connectsocial;

    /*public @InjectView(R.id.mobile_number)
    TextView mobile_number;*/
    public @InjectView(R.id.languagetext)
    TextView LanguageText;
    public @InjectView(R.id.language)
    TextView language;
    public @InjectView(R.id.mobilenumber_lyout)
    RelativeLayout mobilenumber_lyout;
    public @InjectView(R.id.splash_logo)
    ImageView splash_logo;
    public RecyclerView languageView;
    public LanguageAdapter LanguageAdapter;
    public List<CurrencyModel> languagelist;
    protected boolean isInternetAvailable;

    public AlertDialog dialog;

    @OnClick(R.id.languagetext)
    public void lang() {
        languagelist();
        LanguageText.setClickable(false);
    }

    /*@OnClick(R.id.transprantview)
    public void trans() {
        if (sessionManager.getDeviceId() != null) {
            Intent intent = new Intent(getApplicationContext(), SSMobileActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        } else {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if (refreshedToken != null) {
                sessionManager.setDeviceId(refreshedToken);
                Intent intent = new Intent(getApplicationContext(), SSMobileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
            } else {
                dialogfunction("Unable to get Device Id. Please try again later...");
            }
        }

    }*/

    @OnClick(R.id.signin)
    public void signIn() {
        if (sessionManager.getDeviceId() != null) {
            openLoginActivity();
        } else {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if (refreshedToken != null) {
                sessionManager.setDeviceId(refreshedToken);
                openLoginActivity();
            } else {
                dialogfunction("Unable to get Device Id. Please try again later...");
            }
        }
    }

    @OnClick(R.id.signup)
    public void signUp() {
        if (sessionManager.getDeviceId() != null) {
            openFacebookAccountKitActivity();
        } else {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if (refreshedToken != null) {
                sessionManager.setDeviceId(refreshedToken);
                openFacebookAccountKitActivity();
            } else {
                dialogfunction("Unable to get Device Id. Please try again later...");
            }
        }
    }

    private void openFacebookAccountKitActivity() {

        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
    }

    public void openRegisterActivity(String phoneNumber, String countryCode) {
        Intent signin = new Intent(getApplicationContext(), SSRegisterActivity.class);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY, phoneNumber);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY, countryCode);
        startActivity(signin);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        finish();
    }


    public void openLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), SSLoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @OnClick(R.id.connectsocial)
    public void connect() {
        if (sessionManager.getDeviceId() != null) {
            Intent intent = new Intent(getApplicationContext(), SSSocialActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        } else {
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            if (refreshedToken != null) {
                sessionManager.setDeviceId(refreshedToken);
                Intent intent = new Intent(getApplicationContext(), SSSocialActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
            } else {
                dialogfunction("Unable to get Device Id. Please try again later...");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        dialog = commonMethods.getAlertDialog(this);

        /**
         * Start firebase push notification service
         */
        if (isInternetAvailable) {
            startService(new Intent(this, MyFirebaseInstanceIDService.class));
        } else {
            dialogfunction(getString(R.string.turnoninternet));
        }
        setLocale();


        sessionManager.setType("rider");
        sessionManager.setDeviceId(FirebaseInstanceId.getInstance().getToken());
        sessionManager.setDeviceType("2");

        DebuggableLogE(TAG, "Firebase reg id: " + sessionManager.getDeviceId());


        final boolean isAttachedToWindow = ViewCompat.isAttachedToWindow(splash_logo);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            splash_logo.setVisibility(View.VISIBLE);
            splash_logo.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     try {
                                         if (isAttachedToWindow) {
                                             doCircularReveal();
                                         }
                                     } catch (Exception e) {
                                         e.printStackTrace();
                                     }
                                 }
                             }
            );
        }


    }


    /**
     * Exit revel animation
     */
    public void doExitReveal() {


        // get the center for the clipping circle
        int centerX = (splash_logo.getLeft() + splash_logo.getRight()) / 2;
        int centerY = (splash_logo.getTop() + splash_logo.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = splash_logo.getWidth();

        // create the animation (the final radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(splash_logo,
                    centerX, centerY, initialRadius, 0);
        }
        anim.setDuration(1000);
        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                splash_logo.setVisibility(View.GONE);
            }
        });

        // start the animation
        anim.start();

    }

    /**
     * Circular revel animation
     */
    private void doCircularReveal() {

        // get the center for the clipping circle
        int centerX = (splash_logo.getLeft() + splash_logo.getRight()) / 2;
        int centerY = (splash_logo.getTop() + splash_logo.getBottom()) / 2;

        int startRadius = 0;
        // get the final radius for the clipping circle
        int endRadius = Math.max(splash_logo.getWidth(), splash_logo.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(splash_logo,
                    centerX, centerY, startRadius, endRadius);
        }
        anim.setDuration(1500);

        anim.start();
    }


    @Override
    public void onResume() {
        super.onResume();

        String lan = sessionManager.getLanguage();

        if (lan != null) {
            language.setText(lan);
            //setLocale();
        }


        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();       // bye

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            splash_logo.setVisibility(View.VISIBLE);
            splash_logo.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     doExitReveal();
                                 }
                             }
            );
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * To message in dialog ( internet not available)
     */
    public void dialogfunction(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //SigninSignupActivity.this.finish();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void languagelist() {

        languageView = new RecyclerView(SigninSignupActivity.this);
        languagelist = new ArrayList<>();
        loadlang();

        LanguageAdapter = new LanguageAdapter(this, languagelist);
        languageView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        languageView.setAdapter(LanguageAdapter);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.currency_header, null);
        TextView T = (TextView) view.findViewById(R.id.header);
        T.setText(getString(R.string.selectlanguage));


        alertDialogStores = new android.app.AlertDialog.Builder(SigninSignupActivity.this)
                .setCustomTitle(view)
                .setView(languageView)
                .setCancelable(true)
                .show();
        LanguageText.setClickable(true);

        alertDialogStores.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (langclick) {
                    langclick = false;
                    String lan = sessionManager.getLanguage();
                    language.setText(lan);
                    setLocale();
                    recreate();
                    //Intent intent = new Intent(SigninSignupActivity.this, SigninSignupActivity.class);
                    Intent intent = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                LanguageText.setClickable(true);

            }
        });
    }

    public void loadlang() {

        String[] languages = getResources().getStringArray(R.array.language);
        String[] langCode = getResources().getStringArray(R.array.languageCode);
        for (int i = 0; i < languages.length; i++) {
            CurrencyModel listdata = new CurrencyModel();
            listdata.setCurrencyName(languages[i]);
            listdata.setCurrencySymbol(langCode[i]);
            languagelist.add(listdata);

        }
    }

    public void setLocale() {
        String lang = sessionManager.getLanguage();

        if (!lang.equals("")) {

            String langC = sessionManager.getLanguageCode();
            Locale locale = new Locale(langC);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            SigninSignupActivity.this.getResources().updateConfiguration(config, SigninSignupActivity.this.getResources().getDisplayMetrics());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT && resultCode == RESULT_OK) {
           /* if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER) {
                openRegisterActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY), data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
            } else if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER) {
                commonMethods.showMessage(this, dialog, data.getStringExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY));

            }*/
            openRegisterActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY), data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
        }
    }

}
