package com.trioangle.gofer.pushnotification;
/**
 * @package com.trioangle.gofer
 * @subpackage pushnotification
 * @category FirebaseMessagingService
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.helper.CommonDialog;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sendrequest.PaymentAmountPage;
import com.trioangle.gofer.sendrequest.SendingRequestActivity;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.views.main.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import static com.trioangle.gofer.sendrequest.SendingRequestActivity.isSendingRequestisLive;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;

/* ************************************************************
   Firebase Messaging Service to base push notification message to activity
   *************************************************************** */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    public @Inject
    SessionManager sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        AppController.getAppComponent().inject(this);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        DebuggableLogE(TAG, "From: " + remoteMessage.getFrom());
        DebuggableLogE(TAG, "From: " + remoteMessage);

        if (remoteMessage == null)
            return;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            DebuggableLogE(TAG, "Data Payload: " + remoteMessage.getData().toString());


            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
                if (remoteMessage.getNotification() != null) {
                    DebuggableLogE(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());

                }

            } catch (Exception e) {
                DebuggableLogE(TAG, "Exception: " + e.getMessage());
            }
        }
    }


    private void handleDataMessage(JSONObject json) {
        DebuggableLogE(TAG, "push json: " + json.toString());

        try {
            sessionManager.setPushJson(json.toString());

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                DebuggableLogE(TAG, "IF: " + json.toString());
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", "message");
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                if (json.getJSONObject("custom").has("accept_request")) {
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(true);
                    if (!isSendingRequestisLive) {
                        Intent rating = new Intent(getApplicationContext(), SendingRequestActivity.class);
                        rating.putExtra("loadData", "loadData");
                        rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(rating);
                    }
                    sessionManager.setTripStatus("accept_request");
                    sessionManager.setDriverAndRiderAbleToChat(true);

                } else if (json.getJSONObject("custom").has("arrive_now")) {

                    MainActivity.isMainActivity = true;
                    /*if (!MainActivity.isMainActivity) {
                        Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogs.putExtra("message", getString(R.string.driverarrrive));
                        dialogs.putExtra("type", 0);
                        startActivity(dialogs);
                    }*/

                    String trip_id = json.getJSONObject("custom").getJSONObject("arrive_now").getString("trip_id");
                    sessionManager.setTripId(trip_id);
                    sessionManager.setTripStatus("arrive_now");
                    sessionManager.setDriverAndRiderAbleToChat(true);

                } else if (json.getJSONObject("custom").has("begin_trip")) {

                    if (!MainActivity.isMainActivity) {


                        Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogs.putExtra("message", getString(R.string.yourtripbegin));
                        dialogs.putExtra("type", 0);
                        startActivity(dialogs);


                    }

                    String trip_id = json.getJSONObject("custom").getJSONObject("begin_trip").getString("trip_id");
                    sessionManager.setTripId(trip_id);
                    sessionManager.setTripStatus("begin_trip");
                    sessionManager.setDriverAndRiderAbleToChat(true);

                } else if (json.getJSONObject("custom").has("end_trip")) {
                    String trip_id = json.getJSONObject("custom").getJSONObject("end_trip").getString("trip_id");
                    sessionManager.setTripId(trip_id);
                    sessionManager.setTripStatus("end_trip");
                    sessionManager.clearDriverNameRatingAndProfilePicture();
                    sessionManager.setDriverAndRiderAbleToChat(false);
                    CommonMethods.stopFirebaseChatListenerService(this);
                    startActivity(new Intent(getApplicationContext(), PaymentAmountPage.class));
                    /*Intent rating = new Intent(getApplicationContext(), DriverRatingActivity.class);
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    rating.putExtra("imgprofile", json.getJSONObject("custom").getJSONObject("end_trip").getString("driver_thumb_image"));
                    startActivity(rating);*/
                } else if (json.getJSONObject("custom").has("cancel_trip")) {

                    sessionManager.clearTripID();
                    sessionManager.clearDriverNameRatingAndProfilePicture();
                    sessionManager.setDriverAndRiderAbleToChat(false);
                    CommonMethods.stopFirebaseChatListenerService(this);
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(false);
                    if (!MainActivity.isMainActivity) {


                        Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogs.putExtra("message", getString(R.string.yourtripcancelledbydriver));
                        dialogs.putExtra("type", 1);
                        startActivity(dialogs);


                    }
                } else {
                    if (json.getJSONObject("custom").has("custom_message")) {
                        // statusDialog("Your Trip Begins Now...");
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                        String message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data");
                        String title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title");

                        notificationUtils.generateNotification(getApplicationContext(), message, title);
                    }
                }
                // play notification sound
                // notificationUtils.playNotificationSound();
                // notificationUtils.generateNotification(getApplicationContext(),message);
            } else {
                DebuggableLogE(TAG, "ELSE: " + json.toString());

                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", "message");
                if (!"".equals(sessionManager.getAccessToken())) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                }

                if (json.getJSONObject("custom").has("accept_request")) {
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(true);
                    if (!isSendingRequestisLive) {
                        Intent rating = new Intent(getApplicationContext(), SendingRequestActivity.class);
                        rating.putExtra("loadData", "loadData");
                        rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(rating);
                    }
                    sessionManager.setTripStatus("accept_request");

                }/*else if(json.getJSONObject("custom").has("no_cars")) {
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(true);
                    Intent rating=new Intent(getApplicationContext(), SendingRequestActivity.class);
                    rating.putExtra("loadData","loadData");
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(rating);

                   *//* Intent pushNotifications = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotifications.putExtra("message", "message");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotifications);*//*
                }*/ else if (json.getJSONObject("custom").has("arrive_now")) {
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(true);
                    Intent rating = new Intent(getApplicationContext(), MainActivity.class);
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(rating);
                    if (!MainActivity.isMainActivity) {


                        Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogs.putExtra("message", getString(R.string.driverarrrive));
                        dialogs.putExtra("type", 0);
                        startActivity(dialogs);


                    }

                    String trip_id = json.getJSONObject("custom").getJSONObject("arrive_now").getString("trip_id");
                    sessionManager.setTripId(trip_id);
                    sessionManager.setTripStatus("arrive_now");

                } else if (json.getJSONObject("custom").has("begin_trip")) {
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(true);
                    Intent rating = new Intent(getApplicationContext(), MainActivity.class);
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(rating);
                    if (!MainActivity.isMainActivity) {


                        Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogs.putExtra("message", getString(R.string.yourtripbegin));
                        dialogs.putExtra("type", 0);
                        startActivity(dialogs);


                    }

                    String trip_id = json.getJSONObject("custom").getJSONObject("begin_trip").getString("trip_id");
                    sessionManager.setTripId(trip_id);
                    sessionManager.setTripStatus("begin_trip");

                } else if (json.getJSONObject("custom").has("end_trip")) {
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(true);
                    sessionManager.setDriverAndRiderAbleToChat(false);
                    CommonMethods.stopFirebaseChatListenerService(this);
                    String trip_id = json.getJSONObject("custom").getJSONObject("end_trip").getString("trip_id");
                    sessionManager.setTripId(trip_id);
                    sessionManager.setTripStatus("end_trip");
                    startActivity(new Intent(getApplicationContext(), PaymentAmountPage.class));
                    /*Intent rating = new Intent(getApplicationContext(), DriverRatingActivity.class);
                    rating.putExtra("imgprofile", json.getJSONObject("custom").getJSONObject("end_trip").getString("driver_thumb_image"));
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(rating);*/
                } else if (json.getJSONObject("custom").has("cancel_trip")) {
                    sessionManager.clearTripID();

                    sessionManager.setDriverAndRiderAbleToChat(false);
                    Intent rating = new Intent(getApplicationContext(), MainActivity.class);
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(rating);
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(false);
                    //statusDialog("Your trip cancelled by driver",1);
                    if (!MainActivity.isMainActivity) {

                        Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogs.putExtra("message", getString(R.string.yourtripcancelledbydriver));
                        dialogs.putExtra("type", 1);
                        startActivity(dialogs);


                    }
                } else if (json.getJSONObject("custom").has("trip_payment")) {

                    sessionManager.clearTripID();
                    sessionManager.setDriverAndRiderAbleToChat(false);
                    Intent rating = new Intent(getApplicationContext(), MainActivity.class);
                    rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(rating);
                    sessionManager.setIsrequest(false);
                    sessionManager.setIsTrip(false);
                    //statusDialog("Your trip cancelled by driver",1);
                    if (!MainActivity.isMainActivity) {

                        Intent dialogs = new Intent(getApplicationContext(), CommonDialog.class);
                        dialogs.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogs.putExtra("message", getString(R.string.paymentcompleted));
                        //dialogs.putExtra("driverImage",json.getJSONObject("custom").getJSONObject("trip_payment").getString("driver_thumb_image"));
                        dialogs.putExtra("type", 1);
                        startActivity(dialogs);


                    }
                } else {
                    if (json.getJSONObject("custom").has("custom_message")) {
                        sessionManager.setIsrequest(false);
                        sessionManager.setIsTrip(false);
                        Intent rating = new Intent(getApplicationContext(), MainActivity.class);
                        rating.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(rating);
                        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                        notificationUtils.playNotificationSound();
                        String message = json.getJSONObject("custom").getJSONObject("custom_message").getString("message_data");
                        String title = json.getJSONObject("custom").getJSONObject("custom_message").getString("title");

                        notificationUtils.generateNotification(getApplicationContext(), message, title);
                    }
                }
                // play notification sound
                // notificationUtils.playNotificationSound();
                // notificationUtils.generateNotification(getApplicationContext(),message);

            }
        } catch (JSONException e) {
            DebuggableLogE(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            DebuggableLogE(TAG, "Exception: " + e.getMessage());
        }
    }


}
