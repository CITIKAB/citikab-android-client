package com.trioangle.gofer.views.signinsignup;
/**
 * @package com.trioangle.gofer
 * @subpackage signin_signup
 * @category SignupNameActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;

/* ************************************************************
   Signup page for geting rider name
    *********************************************************** */
public class SignupNameActivity extends AppCompatActivity {

    public @Inject
    SessionManager sessionManager;

    public @InjectView(R.id.input_first)
    EditText input_first;
    public @InjectView(R.id.input_last)
    EditText input_last;
    public @InjectView(R.id.input_layout_first)
    TextInputLayout input_layout_first;
    public @InjectView(R.id.input_layout_last)
    TextInputLayout input_layout_last;
    public @InjectView(R.id.signinterms)
    TextView signinterms;

    public String Termpolicy;

    @OnClick(R.id.next)
    public void next() {
        submitForm();
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_name);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        Termpolicy = CommonKeys.termPolicyUrl;//Demo Url for Terms and policy

        customTextView(signinterms);
        input_first.addTextChangedListener(new NameTextWatcher(input_first));
        input_last.addTextChangedListener(new NameTextWatcher(input_last));

    }

    /**
     * Validating form
     */
    private void submitForm() {

        if (!validateFirst()) {
            return;
        }
        if (!validateLast()) {
            return;
        }


        String input_first_str = input_first.getText().toString();
        String input_last_str = input_last.getText().toString();

        sessionManager.setFirstName(input_first_str);
        sessionManager.setLastName(input_last_str);

        Intent x = new Intent(getApplicationContext(), SSPassword.class);
        startActivity(x);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
    }

    /**
     * Validate first name
     */
    private boolean validateFirst() {
        if (input_first.getText().toString().trim().isEmpty()) {
            input_layout_first.setError(getString(R.string.error_msg_firstname));
            requestFocus(input_first);
            return false;
        } else {
            input_layout_first.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Validate last name
     */
    private boolean validateLast() {
        if (input_last.getText().toString().trim().isEmpty()) {
            input_layout_last.setError(getString(R.string.error_msg_lastname));
            requestFocus(input_last);
            return false;
        } else {
            input_layout_last.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Edit text request focus
     */
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * Custom textview to show with link
     */
    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                getResources().getString(R.string.sigin_terms1));
        spanTxt.append(getResources().getString(R.string.sigin_terms2));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String url = Termpolicy + "terms_of_service";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }, spanTxt.length() - getResources().getString(R.string.sigin_terms2).length(), spanTxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanTxt.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_text_color)), spanTxt.length() - getResources().getString(R.string.sigin_terms2).length(), spanTxt.length(), 0);
        spanTxt.append(getResources().getString(R.string.sigin_terms3));
        spanTxt.append(getResources().getString(R.string.sigin_terms4));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String url = Termpolicy + "privacy_policy";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        }, spanTxt.length() - getResources().getString(R.string.sigin_terms4).length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_text_color)), spanTxt.length() - getResources().getString(R.string.sigin_terms4).length(), spanTxt.length(), 0);
        spanTxt.append(".");
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    /**
     * Testwatcher for validate
     */
    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogV("Gofer", "onTextChange");
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogV("Gofer", "onTextChange");
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_first:
                    validateFirst();
                    break;
                case R.id.input_last:
                    validateLast();
                    break;
                default:
                    break;

            }
        }
    }

}
