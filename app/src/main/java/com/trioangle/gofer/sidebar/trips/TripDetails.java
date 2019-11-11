package com.trioangle.gofer.sidebar.trips;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.trips
 * @category TripDetails
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.adapters.ViewPagerAdapter;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.trip.InvoiceModel;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sendrequest.DriverRatingActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.trioangle.gofer.sidebar.trips.Past.tripDetailModels;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;

/* ************************************************************
    Selected Trip details
    *********************************************************** */
public class TripDetails extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    public static int postion;
    public @Inject
    SessionManager sessionManager;
    //This is our tablayout
    public @InjectView(R.id.tabLayout2)
    TabLayout tabLayouttripdetails;
    //This is our viewPager
    public @InjectView(R.id.pager2)
    ViewPager viewPagertripdetails;
    public @InjectView(R.id.carname)
    TextView carname;
    public @InjectView(R.id.addressone)
    TextView addressone;
    public @InjectView(R.id.adresstwo)
    TextView adresstwo;
    public @InjectView(R.id.driver_name)
    TextView driver_name;
    public @InjectView(R.id.datetime)
    TextView datetime;
    public @InjectView(R.id.amountcard)
    TextView amountcard;
    public @InjectView(R.id.mapimage)
    ImageView mapimage;
    public @InjectView(R.id.profile_image)
    CircleImageView profile_image;
    public @InjectView(R.id.profilelayout)
    RelativeLayout profilelayout;

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    public @InjectView(R.id.btnrate)
    Button btnrate;
    @OnClick(R.id.btnrate)
    public void rate() {
        Intent rating = new Intent(getApplicationContext(), DriverRatingActivity.class);
        rating.putExtra("imgprofile", tripDetailModels.get(postion).getDriverThumbImage());
        rating.putExtra("tripid", tripDetailModels.get(postion).getId());
        rating.putExtra("back", 1);
        startActivity(rating);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);
        AppController.getAppComponent().inject(this);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        postion = intent.getIntExtra("postion", 0);

        setupViewPager(viewPagertripdetails);

        tabLayouttripdetails.setupWithViewPager(viewPagertripdetails);

        Picasso.with(getApplicationContext()).load(tripDetailModels.get(postion).getStaticMapURL())
                .placeholder(R.drawable.mapimg).error(R.mipmap.ic_launcher)
                .into(mapimage);
        carname.setText(tripDetailModels.get(postion).getVehicleName());
        addressone.setText(tripDetailModels.get(postion).getPickupLocation());
        adresstwo.setText(tripDetailModels.get(postion).getDropLocation());

        datetime.setText(tripDetailModels.get(postion).getCreatedAt());
        amountcard.setText(sessionManager.getCurrencySymbol() + tripDetailModels.get(postion).getTotalFare());
        driver_name.setText(getResources().getString(R.string.your_ride_with) + " " + tripDetailModels.get(postion).getDriverName());

        if (tripDetailModels.get(postion).getStatus().equalsIgnoreCase("Rating")){
            btnrate.setVisibility(View.VISIBLE);
        }else {
            btnrate.setVisibility(View.GONE);
        }
        String profileurl = tripDetailModels.get(postion).getDriverThumbImage();


        if(profileurl!=null&&!profileurl.equals("")){
            Picasso.with(getApplicationContext()).load(profileurl)
                    .into(profile_image);
        }



        ArrayList<InvoiceModel> invoiceModels = tripDetailModels.get(postion).getInvoice();
        if (invoiceModels.size()<=0){
            profilelayout.setVisibility(View.GONE);
        }


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Receipt(), getString(R.string.receipt));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPagertripdetails.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        DebuggableLogI("Gofer", "Tab");
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        DebuggableLogI("Gofer", "Tab");
    }
}
