package com.trioangle.gofer.adapters;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.trips
 * @category DataAdapter
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.trip.TripDetailModel;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sendrequest.DriverRatingActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
/* ************************************************************
    Trip details list adapter
    *********************************************************** */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    public @Inject
    SessionManager sessionManager;
    public ArrayList<TripDetailModel> tripdetailarraylist;
    public Context context;

    public onItemRatingClickListner onItemRatingClickListner;

    public DataAdapter(ArrayList<TripDetailModel> tripdetailarraylist, Context context) {
        this.tripdetailarraylist = tripdetailarraylist;
        this.context = context;
        AppController.getAppComponent().inject(this);
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cards_layout, viewGroup, false);
        ButterKnife.inject(this, view);
        return new ViewHolder(view);
    }

    public void setOnItemRatingClickListner(onItemRatingClickListner onItemRatingClickListner) {
        this.onItemRatingClickListner = onItemRatingClickListner;
    }

    public interface onItemRatingClickListner{
        void setRatingClick(int position);
    }

    /**
     * Bind trip details
     */
    @Override
    public void onBindViewHolder(final DataAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.tv_country.setText(context.getResources().getString(R.string.trip_id) + tripdetailarraylist.get(i).getId());
        viewHolder.carname.setText(tripdetailarraylist.get(i).getVehicleName());
        viewHolder.btnRate.setVisibility(View.GONE);
        if (tripdetailarraylist.get(i).getStatus().equals("Rating")) {
            viewHolder.btnRate.setVisibility(View.VISIBLE);
            viewHolder.status.setText(context.getString(R.string.Rating));
        } else if (tripdetailarraylist.get(i).getStatus().equals("Cancelled")) {
            viewHolder.status.setText(context.getString(R.string.Cancelled));
        } else if (tripdetailarraylist.get(i).getStatus().equals("Completed")) {
            viewHolder.status.setText(context.getString(R.string.completed));
        } else if (tripdetailarraylist.get(i).getStatus().equals("Payment")) {
            viewHolder.status.setText(context.getString(R.string.payment));
        } else if (tripdetailarraylist.get(i).getStatus().equals("Begin trip")) {
            viewHolder.status.setText(context.getString(R.string.begin_trip));
        } else if (tripdetailarraylist.get(i).getStatus().equals("End trip")) {
            viewHolder.status.setText(context.getString(R.string.end_trip));
        } else if (tripdetailarraylist.get(i).getStatus().equals("Scheduled")) {
            viewHolder.status.setText(context.getString(R.string.scheduled));
        } else {
            viewHolder.status.setText(tripdetailarraylist.get(i).getVehicleName());
        }
        viewHolder.amountcard.setText(sessionManager.getCurrencySymbol() + tripdetailarraylist.get(i).getTotalFare());

        Picasso.with(context).load(tripdetailarraylist.get(i).getStaticMapURL())
                .into(viewHolder.imageView);

        viewHolder.btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rating = new Intent(context, DriverRatingActivity.class);
                rating.putExtra("imgprofile", tripdetailarraylist.get(i).getDriverThumbImage());
                rating.putExtra("tripid", tripdetailarraylist.get(i).getId());
                rating.putExtra("back", 1);
                context.startActivity(rating);
            }
        });

        viewHolder.rltdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemRatingClickListner.setRatingClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripdetailarraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_country;
        private TextView carname;
        private TextView status;
        private TextView amountcard;
        private ImageView imageView;
        private Button btnRate;
        private CardView rltdata;

        public ViewHolder(View view) {
            super(view);

            tv_country = (TextView) view.findViewById(R.id.datetime);
            carname = (TextView) view.findViewById(R.id.carname);
            status = (TextView) view.findViewById(R.id.status);
            amountcard = (TextView) view.findViewById(R.id.amountcard);
            imageView = (ImageView) view.findViewById(R.id.imageView);
            btnRate = (Button) view.findViewById(R.id.btnrate);
            rltdata = (CardView) view.findViewById(R.id.rltdata);


        }
    }


}
