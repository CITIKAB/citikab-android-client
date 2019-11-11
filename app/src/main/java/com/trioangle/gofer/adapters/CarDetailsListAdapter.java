package com.trioangle.gofer.adapters;

/**
 * @package com.trioangle.gofer
 * @subpackage cardetails
 * @category cardetailslist Adapter
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.main.NearestCar;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;

/* ************************************************************
                Car details adapter
get car category list from adapter
*************************************************************** */

public class CarDetailsListAdapter extends RecyclerView.Adapter<CarDetailsListAdapter.ViewHolder> {

    public @Inject
    SessionManager sessionManager;// To save and get data from localsharedpreference
    public Context context;
    public int selectedItem = -1;  // Check selected item from list
    public boolean selectcar = true;  // Set selected car
    public boolean first = true;  // Check car is first
    private ArrayList<NearestCar> modelItems;


    /**
     * Constructor for Car details list adapter
     */
    public CarDetailsListAdapter(Context context, ArrayList<NearestCar> Items) {
        this.modelItems = Items;
        this.context = context;
        AppController.getAppComponent().inject(this);
    }

    /**
     * View holder
     */
    @Override
    public CarDetailsListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.car_details, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Bind View holder
     */
    @Override
    public void onBindViewHolder(final CarDetailsListAdapter.ViewHolder viewHolder, final int i) {
        final NearestCar currentItem = getItem(i);


        DebuggableLogE("check 69", String.valueOf(modelItems));


        viewHolder.car_min.setText(currentItem.getMinTime());
        // check car min is no cabs or not
        if (!currentItem.getMinTime().equals("No cabs")) {

            if (selectcar) {

                viewHolder.itemView.setSelected(true);
                //selectedItem =viewHolder.getLayoutPosition();
                selectcar = false;
            } else {
                viewHolder.itemView.setSelected(false);
            }
        }else {
            viewHolder.car_min.setText(context.getResources().getString(R.string.nocabs));
        }

        // set default select car
        viewHolder.itemView.setSelected(selectedItem == i);

        if (first && currentItem.getCarIsSelected()) {
            viewHolder.itemView.setSelected(true);
            first = false;
        }

        System.out.println("currentItem.getFareEstimation() : "+currentItem.getFareEstimation());
        viewHolder.car_amount.setText(sessionManager.getCurrencySymbol() + currentItem.getFareEstimation());
        viewHolder.car_name.setText(currentItem.getCarName());

        if (viewHolder.itemView.isSelected()) {
            selectedItem = viewHolder.getLayoutPosition();
//            viewHolder.car_amount.setTextColor(context.getResources().getColor(R.color.text_black));
//            viewHolder.car_min.setTextColor(context.getResources().getColor(R.color.text_black));
//            viewHolder.car_name.setTextColor(context.getResources().getColor(R.color.text_black));
            //viewHolder.carimage.setBackground(context.getResources().getDrawable(R.drawable.car));
            Picasso.with(context).load(currentItem.getCarActiveImage()).error(R.drawable.car).into(viewHolder.carimage);

        } else {
//            viewHolder.car_amount.setTextColor(context.getResources().getColor(R.color.text_light_gray));
//            viewHolder.car_min.setTextColor(context.getResources().getColor(R.color.text_light_gray));
//            viewHolder.car_name.setTextColor(context.getResources().getColor(R.color.text_light_gray));
            //viewHolder.carimage.setBackground(context.getResources().getDrawable(R.drawable.cartwo));
            Picasso.with(context).load(currentItem.getCarImage()).error(R.drawable.car).into(viewHolder.carimage);
        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectedItem);
                selectedItem = viewHolder.getLayoutPosition();
                notifyItemChanged(selectedItem);
            }
        });
    }

    @Override
    public int getItemCount() {

        return modelItems.size();
    }

    /**
     * Get Car details for given position
     */
    private NearestCar getItem(int position) {
        return modelItems.get(position);
    }

    /**
     * Reload list
     */
    public void notifyDataChanged() {
        selectedItem = -1;
        selectcar = true;
        first = true;
        notifyDataSetChanged();
    }

    /**
     * View holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView carimage;
        private TextView car_min;
        private TextView car_amount;
        private TextView car_name;

        public ViewHolder(View view) {
            super(view);
            car_name = (TextView) view.findViewById(R.id.car_name);
            car_min = (TextView) view.findViewById(R.id.car_min);
            car_amount = (TextView) view.findViewById(R.id.car_amount);
            carimage = (ImageView) view.findViewById(R.id.car_image);
        }
    }

}
