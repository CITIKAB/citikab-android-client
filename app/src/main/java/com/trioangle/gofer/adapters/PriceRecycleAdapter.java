package com.trioangle.gofer.adapters;
/**
 * @package com.trioangle.goferdriver.rating
 * @subpackage rating
 * @category CommentsRecycleAdapter
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.custompalette.FontCache;
import com.trioangle.gofer.datamodels.trip.InvoiceModel;
import com.trioangle.gofer.utils.CommonMethods;

import java.util.ArrayList;
import java.util.HashMap;

/* ************************************************************
                CommentsRecycleAdapter
Its used to view the feedback comments with rider screen page function
*************************************************************** */
public class PriceRecycleAdapter extends RecyclerView.Adapter<PriceRecycleAdapter.ViewHolder> {
    private ArrayList<InvoiceModel> pricelist;
    private Context context;


    public PriceRecycleAdapter(Context context, ArrayList<InvoiceModel> pricelist) {
        this.pricelist = pricelist;
        this.context=context;
    }

    @Override
    public PriceRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.price_layout, viewGroup, false);


        return new ViewHolder(view);
    }

    /*
   *  Get rider feedback list bind
   */
    @Override
    public void onBindViewHolder(PriceRecycleAdapter.ViewHolder viewHolder, int i) {


        viewHolder.faretxt.setText(pricelist.get(i).getKey());
        viewHolder.fareAmt.setText(pricelist.get(i).getValue().replace("\"",""));
       /* if (pricelist.get(i).getKey().equals("Base fare"))
        {
            viewHolder.isbase.setVisibility(View.VISIBLE);

        }
        if (pricelist.get(i).getKey().equals("Total trip fare")){
            viewHolder.fareAmt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.faretxt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.rltprice.setBackground(context.getResources().getDrawable(R.drawable.buttoontopboarder));
        }
        if (pricelist.get(i).getKey().equals("Payable Amount")||pricelist.get(i).getKey().equals("Pay Amount")){
            viewHolder.rltprice.setBackground(context.getResources().getDrawable(R.drawable.d_buttomtopboarder));
            viewHolder.fareAmt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.faretxt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.fareAmt.setTextColor(context.getResources().getColor(R.color.price_recycler_green_color));
            viewHolder.faretxt.setTextColor(context.getResources().getColor(R.color.price_recycler_green_color));
        }*/




        if (pricelist.get(i).getBar().equals("1")){
            viewHolder.rltprice.setBackground(context.getResources().getDrawable(R.drawable.d_topboarder));
            System.out.println("Key check feedback : "+pricelist.get(i).getKey());
        }else{
            viewHolder.rltprice.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        
                

        if (pricelist.get(i).getColour().equals("black")){
            viewHolder.fareAmt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.faretxt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.fareAmt.setTextColor(context.getResources().getColor(R.color.price_recycler_black_color));
            viewHolder.faretxt.setTextColor(context.getResources().getColor(R.color.price_recycler_black_color));
        }

        if (pricelist.get(i).getColour().equals("green")){
            viewHolder.fareAmt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.faretxt.setTypeface(FontCache.getTypeface(context.getResources().getString(R.string.fonts_UBERMedium),context));
            viewHolder.fareAmt.setTextColor(context.getResources().getColor(R.color.price_recycler_green_color));
            viewHolder.faretxt.setTextColor(context.getResources().getColor(R.color.price_recycler_green_color));
        }



       // if ()



    }

    @Override
    public int getItemCount() {
        return pricelist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView faretxt;
        private TextView fareAmt;
        private TextView isbase;
        private RelativeLayout rltprice;

        public ViewHolder(View view) {
            super(view);

            faretxt = (TextView) view.findViewById(R.id.faretxt);
            fareAmt = (TextView) view.findViewById(R.id.fareAmt);
            isbase = (TextView) view.findViewById(R.id.baseview);
            rltprice = (RelativeLayout) view.findViewById(R.id.rltprice);

        }
    }


}
