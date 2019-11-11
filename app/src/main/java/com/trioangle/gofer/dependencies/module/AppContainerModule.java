package com.trioangle.gofer.dependencies.module;
/**
 * @package com.trioangle.gofer
 * @subpackage dependencies.module
 * @category AppContainerModule
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.trioangle.gofer.configs.RunTimePermission;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.helper.Constants;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.views.customize.CustomDialog;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/*****************************************************************
 App Container Module
 ****************************************************************/
@Module(includes = com.trioangle.gofer.dependencies.module.ApplicationModule.class)
public class AppContainerModule {

    @Provides
    @Singleton
    public SharedPreferences providesSharedPreferences(Application application) {
        return application.getSharedPreferences(Constants.APP_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public CommonMethods providesCommonMethods() {
        return new CommonMethods();
    }

    @Provides
    @Singleton
    public SessionManager providesSessionManager() {
        return new SessionManager();
    }


    @Provides
    @Singleton
    public RunTimePermission providesRunTimePermission() {
        return new RunTimePermission();
    }

    @Provides
    @Singleton
    public Context providesContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    public ArrayList<String> providesStringArrayList() {
        return new ArrayList<>();
    }

    @Provides
    @Singleton
    public JsonResponse providesJsonResponse() {
        return new JsonResponse();
    }


    @Provides
    @Singleton
    public CustomDialog providesCustomDialog() {
        return new CustomDialog();
    }
}
