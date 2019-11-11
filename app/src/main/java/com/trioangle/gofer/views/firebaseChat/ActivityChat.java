package com.trioangle.gofer.views.firebaseChat;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.pushnotification.NotificationUtils;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.views.customize.CustomDialog;

import javax.inject.Inject;

import static com.trioangle.gofer.utils.CommonMethods.startFirebaseChatListenerService;
import static com.trioangle.gofer.utils.CommonMethods.stopFirebaseChatListenerService;

public class ActivityChat extends AppCompatActivity implements FirebaseChatHandler.FirebaseChatHandlerInterface {

    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    CustomDialog customDialog;
    public @Inject
    Gson gson;

    public @InjectView(R.id.edt_new_msg)
    EditText newMessage;

    public @InjectView(R.id.rv_chat)
    RecyclerView rv;

    public @InjectView(R.id.tv_profile_rating)
    TextView driverRating;

    public @InjectView(R.id.tv_profile_name)
    TextView driverName;

    public @InjectView(R.id.imgvu_driver_profile)
    ImageView driverProfilePicture;

    public @InjectView(R.id.imgvu_emptychat)
    ImageView noChats;


    AdapterFirebaseRecylcerview adapterFirebaseRecylcerview;
    FirebaseChatHandler firebaseChatHandler;
    int sourceActivityCode;

    @OnClick (R.id.imgvu_back)
    public void backPressed(){
        this.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        sourceActivityCode = getIntent().getIntExtra(CommonKeys.FIREBASE_CHAT_ACTIVITY_SOURCE_ACTIVITY_TYPE_CODE, CommonKeys.FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_NOTIFICATION);

        updateDriverProfileOnHeader();
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapterFirebaseRecylcerview = new AdapterFirebaseRecylcerview(this);
        rv.setAdapter(adapterFirebaseRecylcerview);
        firebaseChatHandler = new FirebaseChatHandler(this, CommonKeys.FirebaseChatserviceTriggeredFrom.chatActivity);
        rv.setVisibility(View.GONE);
        noChats.setVisibility(View.VISIBLE);



    }

    private void updateDriverProfileOnHeader() {

        String driverProfilePic = sessionManager.getDriverProfilePic(), driverName = sessionManager.getDriverName(), driverRating = sessionManager.getDriverRating();
        if(!TextUtils.isEmpty(driverProfilePic)){
            Picasso.with(getApplicationContext()).load(driverProfilePic).error(R.drawable.car)
                    .into(driverProfilePicture);
        }

        if(!TextUtils.isEmpty(driverName)) {
            this.driverName.setText(driverName);
        }else{
            this.driverName.setText(getResources().getString(R.string.driver));
        }

        if (!(driverRating).equals("")) {
            this.driverRating.setText(driverRating);
        }else{
            this.driverRating.setVisibility(View.GONE);
        }
    }

    @Override
    public void pushMessage(FirebaseChatModelClass firebaseChatModelClass) {
        adapterFirebaseRecylcerview.updateChat(firebaseChatModelClass);
        rv.scrollToPosition(adapterFirebaseRecylcerview.getItemCount() - 1);
        rv.setVisibility(View.VISIBLE);
        noChats.setVisibility(View.GONE);
    }

    @OnClick(R.id.iv_send)
    public void sendMessage() {
        firebaseChatHandler.addMessage(newMessage.getText().toString().trim());
        newMessage.getText().clear();

    }

    /*private ArrayList createModelClassData(){

        for (int i =0; i<8; i++){
            chatsObject.add(new FirebaseChatModelClass("fine", CommonKeys.FIREBASE_CHAT_TYPE_RIDER));
            chatsObject.add(new FirebaseChatModelClass("message", CommonKeys.FIREBASE_CHAT_TYPE_DRIVER));
        }

        return chatsObject;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        stopFirebaseChatListenerService(this);
        NotificationUtils.clearNotifications(this);
    }

    @Override
    protected void onPause() {
        firebaseChatHandler.unRegister();
        if(!TextUtils.isEmpty(sessionManager.getTripId())&& sessionManager.isDriverAndRiderAbleToChat()&&(!CommonMethods.isMyBackgroundServiceRunning(FirebaseChatNotificationService.class, this))){
            startFirebaseChatListenerService(this);
        }


        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        /*if (sourceActivityCode == CommonKeys.FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_RIDER_OR_DRIVER_PROFILE) {
            super.onBackPressed();
        } else {
            CommonMethods.gotoMainActivityFromChatActivity(this);
        }*/
        super.onBackPressed();
    }




}
