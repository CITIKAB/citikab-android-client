package com.trioangle.gofer.views.signinsignup;
/**
 * @package com.trioangle.gofer
 * @subpackage signin_signup
 * @category SSForgotMyEmail
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.views.main.MainActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

/* ************************************************************
   Reset password using email
    *********************************************************** */
public class SSForgotMyEmail extends AppCompatActivity {

    public @Inject
    SessionManager sessionManager;

    @OnClick(R.id.next)
    public void next() {
        sessionManager.setIsrequest(false);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssforgot_my_email);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

    }

    /**
     * Back button to close
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);

    }
}
