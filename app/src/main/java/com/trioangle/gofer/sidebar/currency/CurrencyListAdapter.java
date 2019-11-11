package com.trioangle.gofer.sidebar.currency;

/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar.currency
 * @category CurrencyListAdapter
 * @author Trioangle Product Team
 * @version 1.1
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.settings.CurrencyListModel;
import com.trioangle.gofer.network.AppController;

import java.util.ArrayList;

import javax.inject.Inject;

import static com.trioangle.gofer.sidebar.Setting.alertDialogStores1;
import static com.trioangle.gofer.sidebar.Setting.currencyclick;


@SuppressLint("ViewHolder")
public class CurrencyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final String TAG = null;
    public final int TYPE_Explore = 0;
    public final int TYPE_LOAD = 1;
    public int lastCheckedPosition = -1;
    public Context context;
    public String currency;
    public @Inject
    SessionManager sessionManager;
    public OnLoadMoreListener loadMoreListener;
    public boolean isLoading = false;
    public boolean isMoreDataAvailable = true;
    private ArrayList<CurrencyListModel> modelItems;

    public CurrencyListAdapter(Context context, ArrayList<CurrencyListModel> Items) {
        this.context = context;
        this.modelItems = Items;
        AppController.getAppComponent().inject(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_Explore) {
            return new MovieHolder(inflater.inflate(R.layout.currency_item_view, parent, false));
        } else {
            return new LoadHolder(inflater.inflate(R.layout.row_load, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
            isLoading = true;
            loadMoreListener.onLoadMore();
        }

        if (getItemViewType(position) == TYPE_Explore) {
            ((MovieHolder) holder).bindData(modelItems.get(position), position);
        }
        //No else part needed as load holder doesn't bind any data
    }

    @Override
    public int getItemViewType(int position) {
        if (modelItems.get(position).getCode().equals("load")) {
            return TYPE_LOAD;
        } else {
            return TYPE_Explore;
        }
    }

    @Override
    public int getItemCount() {
        return modelItems.size();
    }

    /* VIEW HOLDERS */

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    /* notifyDataSetChanged is final method so we can't override it
         call adapter.notifyDataChanged(); after update the list
         */
    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    static class LoadHolder extends RecyclerView.ViewHolder {
        public LoadHolder(View itemView) {
            super(itemView);
        }
    }

    public class MovieHolder extends RecyclerView.ViewHolder {
        public TextView currencyname;
        public TextView currencysymbol;
        public RadioButton radiobutton;
        public RelativeLayout selectcurrency;

        public MovieHolder(View itemView) {
            super(itemView);
            currencyname = (TextView) itemView.findViewById(R.id.currencyname_txt);
            currencysymbol = (TextView) itemView.findViewById(R.id.currencysymbol_txt);
            radiobutton = (RadioButton) itemView.findViewById(R.id.radioButton1);
            selectcurrency = (RelativeLayout) itemView.findViewById(R.id.selectcurrency);
        }

        public void bindData(CurrencyListModel movieModel, int position) {

            String currencycode;
            currencycode = sessionManager.getCurrencyCode();



            currencyname.setText(movieModel.getCode());
            currencysymbol.setText(Html.fromHtml(movieModel.getSymbol()));

            //currency=movieModel.getCurrencyName()+""+movieModel.getCurrencySymbol();

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked},
                            new int[]{android.R.attr.state_checked}
                    },
                    new int[]{

                            context.getResources().getColor(R.color.radio_button_black)
                            , context.getResources().getColor(R.color.radio_button_blue),
                    }
            );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radiobutton.setButtonTintList(colorStateList);
            }


            radiobutton.setChecked(false);

            if (movieModel.getCode().equals(currencycode)) {
                radiobutton.setChecked(true);
            }
            if (lastCheckedPosition == position) {



            }

            selectcurrency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent x = new Intent(context,Room_details.class);
                    // context.startActivity(x);

                    currency = currencyname.getText().toString() + " (" + currencysymbol.getText().toString() + ")";
                    sessionManager.setCurrencyCode(currencyname.getText().toString());
                    sessionManager.setCurrencySymbol(currencysymbol.getText().toString());


                    lastCheckedPosition = getAdapterPosition();
                    radiobutton.setChecked(true);

                    //new SettingActivity.Updatecurrency().execute();
/*
                    if(alertDialogStores!=null) {
                        alertDialogStores.cancel();
                    }*/
                    currencyclick = true;
                    if (alertDialogStores1 != null) {
                        alertDialogStores1.cancel();
                    }
                   /* if(alertDialogStores2!=null) {
                        alertDialogStores2.cancel();
                    }*/

                }
            });
        }
    }


}
