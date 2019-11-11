package com.trioangle.gofer.views.firebaseChat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.trioangle.gofer.pushnotification.NotificationUtils;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;

public class FirebaseChatNotificationService extends Service implements FirebaseChatHandler.FirebaseChatHandlerInterface {
    FirebaseChatHandler firebaseChatHandler;
    NotificationUtils notificationUtils;

    public FirebaseChatNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // initializing firebaseChatHandler
        try{if(firebaseChatHandler!=null){
            firebaseChatHandler.unRegister();
        }}catch (Exception e){
            e.printStackTrace();
        }
        firebaseChatHandler = new FirebaseChatHandler(this, CommonKeys.FirebaseChatserviceTriggeredFrom.backgroundService);
        notificationUtils = new NotificationUtils(this);
        CommonMethods.DebuggableLogE("service started", "started");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        firebaseChatHandler.unRegister();
    }

    @Override
    public void pushMessage(FirebaseChatModelClass firebaseChatModelClass) {
        CommonMethods.DebuggableLogE("service started", "message received & " + firebaseChatModelClass.type);
        if( firebaseChatModelClass.type!=null && firebaseChatModelClass.type.equals(CommonKeys.FIREBASE_CHAT_TYPE_DRIVER)) {
            notificationUtils.generateFirebaseChatNotification(this, firebaseChatModelClass.message);
        }
    }
}
