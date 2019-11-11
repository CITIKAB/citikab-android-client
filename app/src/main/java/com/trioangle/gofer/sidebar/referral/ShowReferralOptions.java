package com.trioangle.gofer.sidebar.referral;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sidebar.referral.model.ReferredFriendsModel;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;

import javax.inject.Inject;

import static com.trioangle.gofer.utils.CommonKeys.CompletedReferralArray;
import static com.trioangle.gofer.utils.CommonKeys.IncompleteReferralArray;

public class ShowReferralOptions extends AppCompatActivity implements ServiceListener {

    public AlertDialog dialog;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    Gson gson;
    public @Inject
    CustomDialog customDialog;

    public @InjectView(R.id.rv_in_completed_referrals)
    RecyclerView rvIncompletedReferrals;

    public @InjectView(R.id.rv_completed_referrals)
    RecyclerView rvCompletedReferrals;


    public @InjectView(R.id.constraintLayout_in_completed_friends)
    ConstraintLayout cvIncompleteFriends;

    public @InjectView(R.id.constraintLayout_completed_friends)
    ConstraintLayout cvCompleteFriends;


    public @InjectView(R.id.tv_referral_code)
    TextView tvReferralCode;
    public @InjectView(R.id.tv_copy)
    TextView tvCopy;
    @OnClick(R.id.tv_copy)
    public void copy() {
        connect();
    }


    public @InjectView(R.id.tv_total_earned)
    TextView tvTotalEarned;

    public @InjectView(R.id.tv_referral_benifit_text)
    TextView tvReferralBenifitStatement;

    public @InjectView(R.id.rlt_share)
    RelativeLayout rltShare;
    @OnClick(R.id.rlt_share)
    public void share(){
        shareMyReferralCode();
    }


    public @InjectView(R.id.tv_no_referrals_yet)
    TextView tvNoReferralsYet;

    private String referralCode = "";
    private ReferredFriendsModel referredFriendsModel;

    @OnClick(R.id.arrow)
    public void backPressed() {
        onBackPressed();
    }

    @OnClick(R.id.tv_referral_code)
    public void connect() {
        CommonMethods.copyContentToClipboard(this, referralCode);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_referral_options);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        initView();

    }

    public void shareMyReferralCode() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name)+" "+getResources().getString(R.string.referral));
        share.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.invite_msg) +" "+spannableString(referralCode));

        startActivity(Intent.createChooser(share, getResources().getString(R.string.share_code)));
    }

    private String  spannableString(String referralCode){
        SpannableString ss = new SpannableString(referralCode);
        ss.setSpan(new StyleSpan(Typeface.BOLD),0,referralCode.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss.toString();
    }

    private void initView() {
        getReferralInformationFromAPI();
    }

    private void getReferralInformationFromAPI() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.getReferralDetails(sessionManager.getAccessToken()).enqueue(new RequestCallback(this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (jsonResp.isSuccess()) {
            onSuccessResult(jsonResp.getStrResponse());
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());

        }
    }

    private void onSuccessResult(String strResponse) {
        referredFriendsModel = gson.fromJson(strResponse, ReferredFriendsModel.class);
        updateReferralCodeInUI();
        if ((referredFriendsModel.getPendingReferrals().size() != 0) || (referredFriendsModel.getCompletedReferrals().size() != 0)) {

            showReferralsNotAvailable(true);

            proceedCompleteReferralDetails();
            proceedIncompleteReferralDetails();
        } else {
            showReferralsNotAvailable(false);
        }
    }

    private void updateReferralCodeInUI() {
        referralCode = referredFriendsModel.getReferralCode();
        tvReferralCode.setText(referralCode);
        tvReferralBenifitStatement.setText(getString(R.string.max_referral_earning_statement, Html.fromHtml(referredFriendsModel.getReferralAmount()).toString()));
        tvTotalEarned.append(referredFriendsModel.getTotalEarning().toString());
    }

    private void showReferralsNotAvailable(Boolean show) {
        if(show){
            cvIncompleteFriends.setVisibility(View.VISIBLE);
            cvCompleteFriends.setVisibility(View.VISIBLE);
            tvNoReferralsYet.setVisibility(View.GONE);
        }else{
            cvIncompleteFriends.setVisibility(View.GONE);
            cvCompleteFriends.setVisibility(View.GONE);
            tvNoReferralsYet.setVisibility(View.VISIBLE);
        }

    }

    private void proceedIncompleteReferralDetails() {
        if (referredFriendsModel.getPendingReferrals().size() != 0) {
            rvIncompletedReferrals.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            rvIncompletedReferrals.setLayoutManager(layoutManager);
            rvIncompletedReferrals.setAdapter(new ReferralFriendsListRecyclerViewAdapter(this, referredFriendsModel.getPendingReferrals(),IncompleteReferralArray));
        } else {
            cvIncompleteFriends.setVisibility(View.GONE);
        }
    }

    private void proceedCompleteReferralDetails() {
        if (referredFriendsModel.getCompletedReferrals().size() != 0) {
            rvCompletedReferrals.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            rvCompletedReferrals.setLayoutManager(layoutManager);
            rvCompletedReferrals.setAdapter(new ReferralFriendsListRecyclerViewAdapter(this, referredFriendsModel.getCompletedReferrals(),CompletedReferralArray));
        } else {
            cvCompleteFriends.setVisibility(View.GONE);

        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {

    }
}
