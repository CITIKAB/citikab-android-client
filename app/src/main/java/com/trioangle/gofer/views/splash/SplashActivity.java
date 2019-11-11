package com.trioangle.gofer.views.splash;
/**
 * @package com.trioangle.gofer
 * @subpackage splash
 * @category SplashActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.main.MainActivity;
import com.trioangle.gofer.views.signinsignup.SigninSignupActivity;
import net.hockeyapp.android.UpdateManager;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.util.Locale;

/* ************************************************************
   Splash screen for rider
    *********************************************************** */
public class SplashActivity extends AppCompatActivity implements ServiceListener {


    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    ApiService apiService;

    public @Inject
    CommonMethods commonMethods;

    public AlertDialog dialog;

    public Animation scaledown;
    public String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getAppComponent().inject(this);

        dialog = commonMethods.getAlertDialog(this);
        sessionManager.setType("rider");
        sessionManager.setDeviceType("2");
        sessionManager.setIsUpdateLocation(0);

        userid = sessionManager.getAccessToken();
        System.out.print("userid===" + userid);
        scaledown = AnimationUtils.loadAnimation(this, R.anim.ub__fade_out_scale_down);


        setLocale();

        callForceUpdateAPI();


        /**
         *  Splash screen time delay
         */


    }

    private void callForceUpdateAPI() {

        if (!commonMethods.isOnline(getApplicationContext())) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        } else {
            apiService.checkVersion(CommonMethods.getAppVersionNameFromGradle(this), sessionManager.getType(), CommonKeys.DeviceTypeAndroid).enqueue(new RequestCallback(this));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    public void setLocale() {
        String lang = sessionManager.getLanguage();

        if (!lang.equals("")) {
            String langC = sessionManager.getLanguageCode();
            Locale locale = new Locale(langC);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            SplashActivity.this.getResources().updateConfiguration(config, SplashActivity.this.getResources().getDisplayMetrics());
        } else {
            sessionManager.setLanguage("English");
            sessionManager.setLanguageCode("en");
        }


    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            try {
                JSONObject response = new JSONObject(jsonResp.getStrResponse());
                if (response.has("force_update")) {
                    Boolean foreceUpdate = response.getBoolean("force_update");
                    if (!foreceUpdate) {
                        moveToNextScreen();
                    } else {
                        showSettingsAlert();
                    }
                }
            } catch (JSONException j) {
                j.printStackTrace();
            }

        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        alertDialog.setCancelable(false);
        //alertDialog.setTitle("Update");
        alertDialog.setMessage("Please update our app to enjoy the latest features!");
        alertDialog.setPositiveButton("Visit play store",
                (dialog, which) -> {
                    CommonMethods.openPlayStore(this);
                    this.finish();
                });
        alertDialog.show();
    }

    public void moveToNextScreen() {
        if (userid != null && !"".equals(userid)) {
            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);
            Intent x = new Intent(getApplicationContext(), MainActivity.class);
            Bundle b = new Bundle();
            b.putSerializable("PickupDrop2", "fdsfdsaf");
            x.putExtras(b);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
            startActivity(x, bndlanimation);
            finish();
        } else {
            Intent x = new Intent(getApplicationContext(), SigninSignupActivity.class);
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.cb_fade_in, R.anim.cb_face_out).toBundle();
            startActivity(x, bndlanimation);
            //startActivity(x);
            finish();
        }
    }
}
