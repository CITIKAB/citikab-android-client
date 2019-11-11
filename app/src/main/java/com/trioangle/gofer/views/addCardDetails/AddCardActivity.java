package com.trioangle.gofer.views.addCardDetails;
/**
 * @package com.gofereats
 * @subpackage views.subviews
 * @category Stripe Activity
 * @author Trioangle Product Team
 * @version 0.6
 */


/* ************************************************************
                Showing and Adding the payment methods
    *********************************************************** */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.views.customize.CustomDialog;

import javax.inject.Inject;

import static com.trioangle.gofer.utils.CommonKeys.REQUEST_CODE_PAYMENT;


public class AddCardActivity extends AppCompatActivity {

    public @Inject
    CommonMethods commonMethods;
    public @Inject
    CustomDialog customDialog;
    public @Inject
    SessionManager sessionManager;

    public TextView headingTextView;
    public ImageView arrow;
    public Button addpayment;
    public CardMultilineWidget stripe;
    private String stripePublishKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        AppController.getAppComponent().inject(this);


        addpayment = findViewById(R.id.addpayment);
        stripe = findViewById(R.id.stripe);
        arrow = findViewById(R.id.arrow);
        headingTextView = findViewById(R.id.tv_heading);


        stripePublishKey = getIntent().getStringExtra("stripePublishKey");
        headingTextView.setText(getIntent().getStringExtra("ActionbarHeading"));
        System.out.println("stripePublishKeyy " + stripePublishKey);

        /**
         * On back pressed
         */
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        /**
         * Pay through  Stripe Payment
         */
        addpayment.setOnClickListener(view -> {

            Card cardToSave = stripe.getCard();
            if (cardToSave != null) {

                commonMethods.showProgressDialog(AddCardActivity.this, customDialog);
                final Stripe stripe = new Stripe(getApplicationContext(), stripePublishKey);
                stripe.createToken(cardToSave, new TokenCallback() {
                    public void onSuccess(Token token) {
                        commonMethods.hideProgressDialog();
                        // Send token to your server
                        //Toast.makeText(getApplicationContext(),"Token "+token.getId().toString(),Toast.LENGTH_LONG).show();
                        Intent intent = getIntent();
                        intent.putExtra("token", token.getId());
                        CommonMethods.DebuggableLogD("token", token.getId());
                        setResult(1234, intent);
                        finish();//finishing activity
                    }

                    public void onError(Exception error) {
                        // Show localized error message
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("token", "");
        setResult(REQUEST_CODE_PAYMENT, intent);
        finish();
    }
}
