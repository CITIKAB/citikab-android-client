package com.trioangle.gofer.sidebar.referral;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.sidebar.referral.model.CompletedOrPendingReferrals;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

import static com.trioangle.gofer.utils.CommonKeys.CompletedReferralArray;
import static com.trioangle.gofer.utils.CommonKeys.IncompleteReferralArray;

public class ReferralFriendsListRecyclerViewAdapter extends RecyclerView.Adapter<ReferralFriendsListRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    List<CompletedOrPendingReferrals> referredFriendsModelArrayList;
    private int referralArrayType;

    public ReferralFriendsListRecyclerViewAdapter(Context mContext, @Nullable List<CompletedOrPendingReferrals> referredFriendsModelArrayList, int referralArrayType) {
        this.mContext = mContext;
        this.referredFriendsModelArrayList = referredFriendsModelArrayList;
        this.referralArrayType = referralArrayType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_adapter_referral_friends_status_single_row, parent, false);


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompletedOrPendingReferrals referredFriendsModelArrayListSingleObject = referredFriendsModelArrayList.get(position);

        holder.tvReferredFriend.setText(referredFriendsModelArrayListSingleObject.getName());
        holder.tvAmountGain.setText(referredFriendsModelArrayListSingleObject.getEarnableAmount());

        if (referralArrayType == IncompleteReferralArray) {
            if (referredFriendsModelArrayListSingleObject.getRemainingTrips() != 0 && referredFriendsModelArrayListSingleObject.getRemainingDays() != 0) {
                StringBuilder tripsAndDaysRemainingText = new StringBuilder(referredFriendsModelArrayListSingleObject.getRemainingDays() + " " + mContext.getString(R.string.days_left) + " | " + mContext.getString(R.string.need_to_complete) + " " + referredFriendsModelArrayListSingleObject.getRemainingTrips() + " " + mContext.getString(R.string.trips));
                holder.tvRemainingDaysAndTrips.setText(tripsAndDaysRemainingText);
            } else {
                holder.tvRemainingDaysAndTrips.setVisibility(View.GONE);
            }
        } else if (referralArrayType == CompletedReferralArray) {
            if (referredFriendsModelArrayListSingleObject.getStatus().equals("Expired")) {
                holder.tvRemainingDaysAndTrips.setTextColor(mContext.getResources().getColor(R.color.red_text_color));
                holder.tvRemainingDaysAndTrips.setText(mContext.getString(R.string.referral_expired));
            } else if (referredFriendsModelArrayListSingleObject.getStatus().equals("Completed")) {
                holder.tvRemainingDaysAndTrips.setVisibility(View.GONE);
                /*holder.tvRemainingDaysAndTrips.setTextColor(mContext.getResources().getColor(R.color.ub__green));
                holder.tvRemainingDaysAndTrips.setText(mContext.getString(R.string.referral_completed));*/
            } else {
                //empty else
            }
        } else {
            //empty else
        }


        try {
            Picasso.with(mContext).load(referredFriendsModelArrayListSingleObject.getProfileImage()).into(holder.profilePicture);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return referredFriendsModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilePicture;
        private TextView tvReferredFriend;
        private TextView tvRemainingDaysAndTrips;
        private TextView tvAmountGain;


        public ViewHolder(View view) {
            super(view);

            profilePicture = view.findViewById(R.id.circleImageView);
            tvReferredFriend = view.findViewById(R.id.tv_referral_friend_name);
            tvRemainingDaysAndTrips = view.findViewById(R.id.tv_remaining_days_and_trips);
            tvAmountGain = view.findViewById(R.id.tv_amount_gain);

        }
    }
}
