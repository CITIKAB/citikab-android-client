package com.trioangle.gofer.adapters;
/**
 * @package com.trioangle.gofer
 * @subpackage emergency
 * @category Emergency contact adapter
 * @author Trioangle Product Team
 * @version 1.7
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.datamodels.main.EmergencyContactModel;

import java.util.ArrayList;

/* ************************************************************************
                Adapter for  Emergency contacts
*************************************************************************** */

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder> {
    public ArrayList<EmergencyContactModel> getEmergencyContactDetails;
    public Context context;
    public int alterpostion;
    public String id1;
    private LayoutInflater inflater;
    private OnItemClickListener mItemClickListener;

    /**
     * EmergencyContactAdapter Constructor to intialize getEmergencyContactDetails and Context context
     *
     * @param getEmergencyContactDetails array list
     * @param context                    context of the emergency context
     */


    public EmergencyContactAdapter(ArrayList<EmergencyContactModel> getEmergencyContactDetails, Context context) {
        this.getEmergencyContactDetails = getEmergencyContactDetails;
        this.context = context;
        inflater = LayoutInflater.from(context);
        for (int i = 0; i < getEmergencyContactDetails.size(); i++) {

        }
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public EmergencyContactAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.emergency_contact_detail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EmergencyContactAdapter.ViewHolder holder, final int position) {

        holder.testname.setText(getEmergencyContactDetails.get(position).getName());
        id1 = getEmergencyContactDetails.get(position).getId();
        holder.testphonenumber.setText(getEmergencyContactDetails.get(position).getMobileNumber());
        holder.deletemenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.inflate(R.menu.emergency_delete);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO Auto-generated method stub
                        int id = item.getItemId();
                        if (id == R.id.delete_menu) {
                            alterpostion = position;
                            String mobilenumber = getEmergencyContactDetails.get(position).getMobileNumber().replaceAll(" ", "");
                            if (mItemClickListener != null) {
                                mItemClickListener.onItemClickListener(mobilenumber, getEmergencyContactDetails.get(position).getName(), getEmergencyContactDetails.get(position).getId(), String.valueOf(position));
                            }
                        }
                        return true;
                    }
                });

                popupMenu.show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return getEmergencyContactDetails.size();
    }

    public interface OnItemClickListener {
        void onItemClickListener(String number, String name, String id, String positionz);
    }

    /**
     * View Holder class for emergency contact
     */

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView testname;
        private TextView testphonenumber;
        private ImageView deletemenu;

        public ViewHolder(View view) {
            super(view);

            testname = (TextView) view.findViewById(R.id.nameofcontact);
            testphonenumber = (TextView) view.findViewById(R.id.numberofcontact);
            deletemenu = (ImageView) view.findViewById(R.id.deletemenu);

        }
    }

}