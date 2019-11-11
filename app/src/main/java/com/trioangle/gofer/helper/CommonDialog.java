package com.trioangle.gofer.helper;
/**
 * @package com.trioangle.gofer
 * @subpackage helper
 * @category CommonDialog
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.views.main.MainActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/* ************************************************************
                CommonDialog
Common dialog for firebase service its show dialog like (Arrive now , Begin trip, Payment)
*************************************************************** */
public class CommonDialog extends Activity {

    public @Inject
    SessionManager sessionManager;

    public TextView message;

    @OnClick(R.id.ok_btn_id)
    public void onclik() {

//        1 -> trip cancelled by driver
        if (getIntent().getIntExtra("type", 0) == 0) {
            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(true);
        } else {
            sessionManager.setIsrequest(false);
            sessionManager.setIsTrip(false);
            sessionManager.setDriverAndRiderAbleToChat(false);
            CommonMethods.stopFirebaseChatListenerService(this);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_common_dialog);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        message = (TextView) findViewById(R.id.message);
        message.setText(getIntent().getStringExtra("message"));

    }

}
