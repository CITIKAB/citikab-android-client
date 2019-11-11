package com.trioangle.gofer.sidebar.payment;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.payment
 * @category DataAdapter
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;

import java.util.ArrayList;

import javax.inject.Inject;
/* ************************************************************
    Promocode list adapter
    *********************************************************** */

public class PromoAmountAdapter extends RecyclerView.Adapter<PromoAmountAdapter.ViewHolder> {
    public @Inject
    SessionManager sessionManager;
    public ArrayList<PromoAmountModel> promodetailarraylist;
    public Context context;

    public PromoAmountAdapter(ArrayList<PromoAmountModel> promodetailarraylist, Context context) {
        this.promodetailarraylist = promodetailarraylist;
        this.context = context;
        AppController.getAppComponent().inject(this);
    }

    @Override
    public PromoAmountAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.promo_amount_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Bind Promocode and amount details
     */
    @Override
    public void onBindViewHolder(final PromoAmountAdapter.ViewHolder viewHolder, final int i) {

        viewHolder.promo_amount.setText(sessionManager.getCurrencySymbol() + promodetailarraylist.get(i).getPromoAmount() + " " + context.getResources().getString(R.string.off));
        viewHolder.promo_amt_txt.setText(context.getResources().getString(R.string.free_trip) + " " + sessionManager.getCurrencySymbol() + promodetailarraylist.get(i).getPromoAmount() + " " + context.getResources().getString(R.string.from) + " " + promodetailarraylist.get(i).getPromoCode());
        viewHolder.promo_exp.setText(promodetailarraylist.get(i).getPromoExp());

    }

    @Override
    public int getItemCount() {
        return promodetailarraylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView promo_amount;
        private TextView promo_amt_txt;
        private TextView promo_exp;

        public ViewHolder(View view) {
            super(view);

            promo_amount = (TextView) view.findViewById(R.id.promo_amount);
            promo_amt_txt = (TextView) view.findViewById(R.id.promo_amt_txt);
            promo_exp = (TextView) view.findViewById(R.id.promo_exp);


        }
    }


}
