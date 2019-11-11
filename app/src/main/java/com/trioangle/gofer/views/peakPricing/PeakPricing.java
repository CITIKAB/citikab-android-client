package com.trioangle.gofer.views.peakPricing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;

import javax.inject.Inject;

public class PeakPricing extends AppCompatActivity {
    public @InjectView(R.id.tv_peak_price_percentage)
    TextView tvPeakPricePercentage;

    public @InjectView(R.id.tv_minimum_fare)
    TextView tvMinimumFare;

    public @InjectView(R.id.tv_per_minutes)
    TextView tvPerMinutes;

    public @InjectView(R.id.tv_per_distance)
    TextView tvPerDistance;

    public @Inject
    SessionManager sessionManager;

    @OnClick(R.id.tv_accept_higherPrice)
    public void acceptHigherPriceButtonClick() {
        setResult(CommonKeys.PEAK_PRICE_ACCEPTED);
        finish();
    }

    @OnClick(R.id.tv_tryLater_higherPrice)
    public void declinedPeakPriceButtonClick() {
        setResult(CommonKeys.PEAK_PRICE_DENIED);
        finish();
    }

    @OnClick(R.id.imgvu_close_icon)
    public void closeActivity(){
        declinedPeakPriceButtonClick();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peek_pricing);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);


        try {
            tvPeakPricePercentage.setText((getIntent().getStringExtra(CommonKeys.KEY_PEAK_PRICE) + "x"));
            tvMinimumFare.setText((sessionManager.getCurrencySymbol()+getIntent().getStringExtra(CommonKeys.KEY_MIN_FARE)));
            tvPerMinutes.setText((sessionManager.getCurrencySymbol() +getIntent().getStringExtra(CommonKeys.KEY_PER_MINUTES)));
            tvPerDistance.setText((sessionManager.getCurrencySymbol() +getIntent().getStringExtra(CommonKeys.KEY_PER_KM)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
