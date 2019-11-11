package com.trioangle.gofer.network;

/**
 * @package com.trioangle.gofer
 * @subpackage network
 * @category AppController
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.trioangle.gofer.dependencies.component.AppComponent;
import com.trioangle.gofer.dependencies.component.DaggerAppComponent;
import com.trioangle.gofer.dependencies.module.ApplicationModule;
import com.trioangle.gofer.dependencies.module.NetworkModule;
import com.trioangle.gofer.utils.CommonKeys;

/* ************************************************************
Retrofit Appcomponent and Bufferknife Added
*************************************************************** */
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppComponent appComponent;
    private static AppController mInstance;

    public static AppComponent getAppComponent() {
        return appComponent;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getContext(){
        return mInstance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);  // Fire base initialize
        MultiDex.install(this);    // Multiple dex initialize
        mInstance = this;
        appComponent = DaggerAppComponent.builder().applicationModule(new ApplicationModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .networkModule(new NetworkModule(CommonKeys.apiBaseUrl)).build();

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}