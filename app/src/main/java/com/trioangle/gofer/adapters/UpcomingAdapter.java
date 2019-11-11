package com.trioangle.gofer.adapters;

import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trioangle.gofer.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.trip.ScheduleDetail;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sendrequest.CancelYourTripActivity;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by trioangle on 7/11/18.
 */

public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.ViewHolder> implements ServiceListener {

    public AlertDialog dialog;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    CommonMethods commonMethods;
    public @Inject
    ApiService apiService;
    public @Inject
    CustomDialog customDialog;
    public @Inject
    Gson gson;
    protected boolean isInternetAvailable;
    private HashMap<String, String> scheduleHashMap;
    private ArrayList<ScheduleDetail> scheduleRideDetailsArrayList;
    private AppCompatActivity context;
    private TextView scheduleridetext;
    private SingleDateAndTimePicker singleDateAndTimePicker;
    private String presenttime;
    //private String date_time;
    private String ScheduleRidedate;


    public UpcomingAdapter(ArrayList<ScheduleDetail> scheduleRideDetailsArrayList, AppCompatActivity context) {
        this.scheduleRideDetailsArrayList = scheduleRideDetailsArrayList;
        this.context = context;
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this.context);
    }

    @Override
    public UpcomingAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.schedule_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UpcomingAdapter.ViewHolder holder, final int position) {

        holder.date_and_time.setText(scheduleRideDetailsArrayList.get(position).getScheduleDisplayDate());
        holder.trip_type.setText("Schedule Trip");
        holder.car_type.setText(scheduleRideDetailsArrayList.get(position).getCarName());
        holder.amount.setText(sessionManager.getCurrencySymbol() + scheduleRideDetailsArrayList.get(position).getFareEstimation());
        holder.pickupaddress.setText(scheduleRideDetailsArrayList.get(position).getPickupLocation());
        holder.destadddress.setText(scheduleRideDetailsArrayList.get(position).getDropLocation());
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CancelYourTripActivity.class);
                intent.putExtra("upcome", "upcome");
                intent.putExtra("scheduleID", scheduleRideDetailsArrayList.get(position).getId());
                context.startActivity(intent);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scheduleRide(position);//schedule ride date picker
                ScheduleMethod();
            }
        });

    }

    @Override
    public int getItemCount() {
        return scheduleRideDetailsArrayList.size();
    }


    private void scheduleRide(final int position) {
        final BottomSheetDialog dialog3 = new BottomSheetDialog(context);
        dialog3.setContentView(R.layout.activity_schedule_ride);
        scheduleridetext = (TextView) dialog3.findViewById(R.id.time_date);
        singleDateAndTimePicker = (SingleDateAndTimePicker) dialog3.findViewById(R.id.single_day_picker);
        final Button set_pickup_window = (Button) dialog3.findViewById(R.id.set_pickup_window);
        assert set_pickup_window != null;
        set_pickup_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.dismiss();


                isInternetAvailable = commonMethods.isOnline(context);
                if (!isInternetAvailable) dialogfunction();
                else {
                    scheduleHashMap = new HashMap<>();
                    scheduleHashMap.put("schedule_id", scheduleRideDetailsArrayList.get(position).getId());
                    scheduleHashMap.put("schedule_date", ScheduleRidedate);
                    scheduleHashMap.put("schedule_time", presenttime);
                    scheduleHashMap.put("token", sessionManager.getAccessToken());
                    scheduleRide();
                }


            }
        });

        dialog3.show();
    }

    /**
     * To check wheather connected or not
     *
     * @return true connected false not connected
     */


    public void dialogfunction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Turn on your Internet")
                .setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     * Schedule a ride wheel picker
     */
    private void ScheduleMethod() {

        Date mindate = Calendar.getInstance().getTime();           //Getting Today's date
        mindate.setMinutes(mindate.getMinutes() + 15);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mindate);


        singleDateAndTimePicker.setDefaultDate(mindate);
        singleDateAndTimePicker.selectDate(calendar);
        singleDateAndTimePicker.setDisplayDays(true);
        singleDateAndTimePicker.setMustBeOnFuture(true);
        singleDateAndTimePicker.mustBeOnFuture();                               //Setting date only to future
        singleDateAndTimePicker.setMinDate(mindate);                           // MIn date ie-Today date
        singleDateAndTimePicker.setMaxDate(scheduleRideDate(mindate));        //Max date upto 30 dates
        singleDateAndTimePicker.setStepMinutes(1);                           //Setting minute intervals to 1min
        singleDateAndTimePicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(String displayed, Date date) {
                /**
                 * When scrolling the picker
                 */
                scheduleRideDate(date);

            }
        });

    }


    private Date scheduleRideDate(Date dateToSelect) {
        final Calendar calendarMax = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);  //Normal date Format
        String newTime = "";
        try {
            Date d = df.parse(dateToSelect.toString());

            calendarMax.setTime(d);
            calendarMax.add(Calendar.MINUTE, 15);       //Setting Maximum Duration upto 15.
            calendarMax.add(Calendar.DATE, 30);          //Setting Maximum Date upto 30.
            newTime = df.format(calendarMax.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Date maxDate = calendarMax.getTime();


        /**
         * new time to compare
         */
        Date now = Calendar.getInstance().getTime();


        String input_date = dateToSelect.toString();

        String new_date = newTime;
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = df.parse(input_date);
            dt2 = df.parse(new_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /**
         * Getting the date format from normal present time
         */

        DateFormat format2 = new SimpleDateFormat("EEE, MMM dd 'at' hh:mm a");
        String finalDay = format2.format(dt1);

        /**
         * Getting normal time with seconds
         */
        DateFormat format4 = new SimpleDateFormat("HH:mm");
        presenttime = format4.format(dt1);
        /**
         * Getting the time format from 15 mins time
         */
        DateFormat format3 = new SimpleDateFormat("hh:mm a");
        String time_15 = format3.format(dt2);

        String time = time_15;


        DateFormat format5 = new SimpleDateFormat("yyyy-MM-dd");
        ScheduleRidedate = format5.format(dt1);

        String ans = finalDay + " - " + time;

        scheduleridetext.setText(ans);

        return maxDate;
    }

    /**
     * To call schedule ride edit api
     */
    public void scheduleRide() {
        commonMethods.showProgressDialog(context, customDialog);
        apiService.scheduleRide(scheduleHashMap).enqueue(new RequestCallback(this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            onSuccessSchedule(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(context, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(context, dialog, jsonResp.getStatusMsg());
        }
    }

    private void onSuccessSchedule(JsonResponse jsonResp) {
        try {
            JSONObject json = new JSONObject(String.valueOf(jsonResp.getStrResponse()));
            JSONArray scheduletrips_array = json.getJSONArray("schedule_rides");

            if (scheduletrips_array.length() > 0) {
                for (int i = 0; i < scheduletrips_array.length(); i++) {
                    JSONObject jsonObj = scheduletrips_array.getJSONObject(i);
                    String schedule_display = jsonObj.getString("schedule_display_date");
                    scheduleRideDetailsArrayList.get(i).setScheduleDisplayDate(schedule_display);
                }
                notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            commonMethods.hideProgressDialog();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date_and_time;
        private TextView trip_type;
        private TextView car_type;
        private TextView amount;
        private TextView pickupaddress;
        private TextView destadddress;
        private TextView cancel;
        private TextView edit;

        private LinearLayout cancel_lay;

        private ViewHolder(View view) {
            super(view);

            date_and_time = (TextView) view.findViewById(R.id.date_and_time);
            trip_type = (TextView) view.findViewById(R.id.trip_tupe);
            car_type = (TextView) view.findViewById(R.id.car_type);
            amount = (TextView) view.findViewById(R.id.amount);
            pickupaddress = (TextView) view.findViewById(R.id.pickupaddress);
            destadddress = (TextView) view.findViewById(R.id.destadddress);
            cancel_lay = (LinearLayout) view.findViewById(R.id.cancel_lay);
            cancel = (TextView) view.findViewById(R.id.cancel);
            edit = (TextView) view.findViewById(R.id.edit);


        }
    }
}