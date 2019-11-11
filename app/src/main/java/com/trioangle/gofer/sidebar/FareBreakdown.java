package com.trioangle.gofer.sidebar;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar
 * @category FareBreakdown
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.main.NearestCar;
import com.trioangle.gofer.network.AppController;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/* ************************************************************
   Price break for selected car details
    *********************************************************** */
public class FareBreakdown extends AppCompatActivity {

    public @Inject
    SessionManager sessionManager;
    public @InjectView(R.id.amount1)
    TextView amount1;
    public @InjectView(R.id.amount2)
    TextView amount2;
    public @InjectView(R.id.amount3)
    TextView amount3;
    public @InjectView(R.id.amount4)
    TextView amount4;
    public ArrayList<NearestCar> searchlist;
    public int position;

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_breakdown);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);

        searchlist = (ArrayList<NearestCar>) getIntent().getSerializableExtra("list");
        position = getIntent().getIntExtra("position", 0);

        /**
         *  Show price breakdown for selected car before send request
         */
        amount1.setText(sessionManager.getCurrencySymbol() + searchlist.get(position).getBaseFare());
        amount2.setText(sessionManager.getCurrencySymbol() + searchlist.get(position).getBaseFare());
        amount3.setText(sessionManager.getCurrencySymbol() + searchlist.get(position).getPerMin());
        amount4.setText(sessionManager.getCurrencySymbol() + searchlist.get(position).getPerKm());
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
