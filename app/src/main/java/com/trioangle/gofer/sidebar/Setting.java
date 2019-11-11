package com.trioangle.gofer.sidebar;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar
 * @category Setting
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.settings.CurrencyListModel;
import com.trioangle.gofer.datamodels.settings.CurrencyModelList;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sidebar.currency.CurrencyListAdapter;
import com.trioangle.gofer.sidebar.currency.CurrencyModel;
import com.trioangle.gofer.sidebar.language.LanguageAdapter;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.main.MainActivity;
import com.trioangle.gofer.views.signinsignup.SigninSignupActivity;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.Enums.REQ_CURRENCYLIST;
import static com.trioangle.gofer.utils.Enums.REQ_GET_RIDER_PROFILE;
import static com.trioangle.gofer.utils.Enums.REQ_LOGOUT;
import static com.trioangle.gofer.utils.Enums.REQ_UPDATE_CURR;
import static com.trioangle.gofer.utils.Enums.REQ_UPDATE_LANG;
import static com.trioangle.gofer.utils.Enums.REQ_UPDATE_LOCATION;

/* ************************************************************
   Rider setting page contain add home and work location and logout
    *********************************************************** */
public class Setting extends AppCompatActivity implements ServiceListener {

    public static Boolean langclick = false;
    public static Boolean currencyclick = false;
    public static android.app.AlertDialog alertDialogStores1;
    public static android.app.AlertDialog alertDialogStores2;
    public final AndroidHttpClient ANDROID_HTTP_CLIENT = AndroidHttpClient.newInstance(Setting.class.getName());
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

    public HashMap<String, String> locationHashMap;
    public @InjectView(R.id.nametext)
    TextView nametext;
    public @InjectView(R.id.textadress)
    TextView textadress;
    public @InjectView(R.id.worktext)
    TextView worktext;
    public @InjectView(R.id.rlt_work)
    RelativeLayout rltWork;
    public @InjectView(R.id.language)
    TextView language;
    public @InjectView(R.id.profile_image)
    CircleImageView profile_image;
    public @InjectView(R.id.hometext)
    TextView hometext;
    public @InjectView(R.id.home_lay)
    RelativeLayout home_lay;
    public @InjectView(R.id.currencylayout)
    RelativeLayout currencylayout;
    public @InjectView(R.id.languagelayout)
    RelativeLayout languagelayout;
    public @InjectView(R.id.currency_code)
    TextView currency_code;
    public RecyclerView recyclerView1;
    public RecyclerView languageView;
    public List<CurrencyModel> languagelist;
    public CurrencyListAdapter currencyListAdapter;
    public LanguageAdapter LanguageAdapter;
    public String currency;
    public String Language;
    public String LanguageCode;
    public ArrayList<CurrencyListModel> currencyList = new ArrayList<>();
    public String countrys = null;
    public String getAddress = null;
    public String lat;
    public String log;
    public String homegettext;
    public String workgettext;
    protected boolean isInternetAvailable;

    /**
     * delete cache data while delete
     */
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }

        return dir.delete();
    }

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @OnClick(R.id.workaddress)
    public void workaddress() {
        Intent intent = new Intent(getApplicationContext(), AddHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("workaddress", "workaddress");
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.rlt_work)
    public void goWorkAddress() {
        workaddress();
    }

    @OnClick(R.id.imglatout)
    public void profile() {
        Intent intent = new Intent(getApplicationContext(), Profile.class);
        startActivity(intent);
    }

    @OnClick(R.id.logoutlayout)
    public void logOut() {
        final Dialog dialog2 = new Dialog(Setting.this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.setContentView(R.layout.dialog_logout);

        TextView cancel = (TextView) dialog2.findViewById(R.id.signout_cancel); // set the custom dialog components - text, image and button
        TextView signout = (TextView) dialog2.findViewById(R.id.signout_signout);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        }); // if button is clicked, close the custom dialog

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                if (!isInternetAvailable) {
                    commonMethods.showMessage(Setting.this, dialog, getResources().getString(R.string.no_connection));
                } else {
                    logout();
                }
            }
        });
        dialog2.show();
    }

    /**
     * Home address text clicked
     */
    @OnClick(R.id.hometext)
    public void hometext() {
        Intent intent = new Intent(getApplicationContext(), AddHome.class);
        intent.putExtra("settinghome", "settinghome");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.home_lay)
    public void AddHome() {
        hometext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        dialog = commonMethods.getAlertDialog(this);

        /**
         *  To change home and work address
         */

        homegettext = sessionManager.getHomeAddress();
        workgettext = sessionManager.getWorkAddress();
        currency = sessionManager.getCurrencyCode();
        currency_code.setText(currency);
        String hometextstr = getIntent().getStringExtra("hometextstr");
        String worktextstr = getIntent().getStringExtra("worktextstr");
        language.setText("English");
        if (hometextstr == null) {
            hometext.setText(getString(R.string.addhome));
        }


        Animation bottomUp = AnimationUtils.loadAnimation(this,
                R.anim.bottom_up_lay);
        ViewGroup rlParentLay = (ViewGroup) findViewById(R.id.parent_lay);
        rlParentLay.startAnimation(bottomUp);
        rlParentLay.setVisibility(View.VISIBLE);




        if (worktextstr == null) {
            worktext.setText(getString(R.string.addwork));
        }
        if (hometextstr != null && !hometextstr.equals("")) {


            if (!isInternetAvailable) {
                commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
            } else {
                fetchLocation(homegettext, "homeclick");

            }

        }
        if (worktextstr != null && !worktextstr.equals("")) {
            if (!isInternetAvailable) {
                commonMethods.showMessage(this, dialog, getResources().getString(R.string.no_connection));
            } else {

                fetchLocation(workgettext, "workclick");
            }
        }


        /**
         *  Change currency button clicked
         */
       /* currencylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currencylayout.setClickable(false);
                currency_list(); // Show curtency list
            }
        });*/
        languagelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languagelayout.setClickable(false);
                languagelist(); // Show curtency list
            }
        });
        String profiledetails = sessionManager.getProfileDetail();
        if (profiledetails == null) {

            /**
             *  Get Rider profile
             */
            if (!isInternetAvailable) {
                commonMethods.showMessage(Setting.this, dialog, getResources().getString(R.string.no_connection));
            } else {
                getRiderDetails();
            }
        } else {
            loaddata(profiledetails);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ANDROID_HTTP_CLIENT.close();
    }

    /**
     * Back button to close
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
    }

    /**
     * Clear application data while logout
     */
    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!"lib".equals(s)) {
                    deleteDir(new File(appDir, s));
                    DebuggableLogI("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");

                    // clearApplicationData();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Language = sessionManager.getLanguage();
        if (Language != null) {
            language.setText(Language);
        }
        String profiledetails = sessionManager.getProfileDetail();
        DebuggableLogV("ProfileArraydetail", "ProfileArraydetail" + profiledetails);
        if (profiledetails == null) {

            /**
             *  Get Rider profile
             */
            if (!isInternetAvailable) {
                //commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
                commonMethods.showMessage(Setting.this, dialog, getResources().getString(R.string.no_connection));
            } else {
                getRiderDetails();
            }

        } else {
            loaddata(profiledetails);
        }

    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            if (!TextUtils.isEmpty(data))
                commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {

            // Get Rider Profile
            case REQ_GET_RIDER_PROFILE:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessGetProf(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;

            // Update Rider Location
            case REQ_UPDATE_LOCATION:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    String statuscode = jsonResp.getStatusCode();
                    if (statuscode.matches("1")) {
                        getRiderDetails();
                    }
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_CURRENCYLIST:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessGetCurr(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_UPDATE_CURR:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    //Integer wallet_amount = (Integer) commonMethods.getJsonValue(jsonResp.getStrResponse(), "wallet_amount", String.class);

                    String wallet_amount = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "wallet_amount", String.class);
                    float result = Float.parseFloat(wallet_amount);
                    int result1 = (int) result;
                    wallet_amount = Integer.toString(result1);

                    System.out.println("wallet amout : " + wallet_amount);
                    sessionManager.setWalletAmount(wallet_amount);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_UPDATE_LANG:
                if (jsonResp.isSuccess()) {
                    DebuggableLogI("Settings", "Language");
                    commonMethods.hideProgressDialog();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_LOGOUT:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessLogout();
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            default:
                commonMethods.hideProgressDialog();
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    public void onSuccessGetProf(JsonResponse jsonResp) {
        if (jsonResp.getStatusCode().matches("1")) {
            sessionManager.setProfileDetail(jsonResp.getStrResponse().toString());
            loaddata(jsonResp.getStrResponse().toString());
        } else if (jsonResp.getStatusCode().matches("2")) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());

        }
    }


    public void onSuccessLogout() {
        String lang = sessionManager.getLanguage();
        String langCode = sessionManager.getLanguageCode();

        sessionManager.clearToken();
        sessionManager.clearAll();
        // Clear local saved data
        clearApplicationData(); // Clear cache data

        sessionManager.setLanguage(lang);
        sessionManager.setLanguageCode(langCode);

        Intent intent = new Intent(getApplicationContext(), SigninSignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Show rider details and address
     */
    public void loaddata(String profiledetails) {
        try {
            JSONObject jsonObj = new JSONObject(profiledetails);
            String first_name = jsonObj.getString("first_name");
            String last_name = jsonObj.getString("last_name");
            String mobile_number = jsonObj.getString("mobile_number");
            String email_id = jsonObj.getString("email_id");
            String user_thumb_image = jsonObj.getString("profile_image");
            String country_code = jsonObj.getString("country_code");

            String home = jsonObj.getString("home");
            String work = jsonObj.getString("work");

            textadress.setText(home);
            worktext.setText(work);

            nametext.setText(first_name + " " + last_name + "\n" + "+" + country_code + mobile_number + "\n" + email_id);
            Picasso.with(getApplicationContext()).load(user_thumb_image)
                    .into(profile_image);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Logout user API called
     */
    public void logout() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.logOut(sessionManager.getAccessToken(), sessionManager.getType()).enqueue(new RequestCallback(REQ_LOGOUT, this));

    }

    /**
     * Get rider details
     */
    public void getRiderDetails() {
//        commonMethods.showProgressDialog(this, customDialog);
        apiService.getRiderProfile(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_GET_RIDER_PROFILE, this));

    }

    /**
     * Get Rider home and work address
     */
    public void updateRiderLoc() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.uploadRiderLocation(locationHashMap).enqueue(new RequestCallback(REQ_UPDATE_LOCATION, this));
    }


    /**
     * Fetch location from address if geocode or google
     */
    public void fetchLocation(String addresss, final String type) {
        getAddress = addresss;

        new AsyncTask<Void, Void, String>() {
            String locations = null;

            @Override
            protected String doInBackground(Void... params) {

                if (Geocoder.isPresent()) {
                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> address;

                        // May throw an IOException
                        address = geocoder.getFromLocationName(getAddress, 5);
                        if (address == null) {
                            return null;
                        }
                        Address location = address.get(0);

                        countrys = address.get(0).getCountryName();

                        location.getLatitude();
                        location.getLongitude();

                        lat = String.valueOf(location.getLatitude());
                        log = String.valueOf(location.getLongitude());
                        locations = lat + "," + log;
                    } catch (Exception ignored) {
                        // after a while, Geocoder start to throw "Service not availalbe" exception. really weird since it was working before (same device, same Android version etc..
                    }
                }

                if (locations != null) // i.e., Geocoder succeed
                {
                    return locations;
                } else // i.e., Geocoder failed
                {
                    return fetchLocationUsingGoogleMap();
                }
            }

            // Geocoder failed :-(
            // Our B Plan : Google Map
            private String fetchLocationUsingGoogleMap() {
                getAddress = getAddress.replaceAll(" ", "%20");
                String googleMapUrl = "http://maps.google.com/maps/api/geocode/json?address=" + getAddress + "&sensor=false" + "&key=" + CommonKeys.googleMapsKey;

                try {
                    JSONObject googleMapResponse = new JSONObject(ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
                            new BasicResponseHandler()));

                    // many nested loops.. not great -> use expression instead
                    // loop among all results


                    String longitute = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getString("lng");

                    String latitude = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getString("lat");

                    int len = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                            .getJSONArray("address_components").length();
                    for (int i = 0; i < len; i++) {
                        if (((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0).equals("country")) {
                            countrys = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(i).getString("long_name");

                        }
                    }
                            /*countrys = ((JSONArray)googleMapResponse.get("results")).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(3).getString("long_name");*/

                    return latitude + "," + longitute;


                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String location) {
                if (location != null) {
                    String[] parts = location.split(",");
                    Double lat = Double.valueOf(parts[0]);
                    Double lng = Double.valueOf(parts[1]);
                    LatLng latLng = new LatLng(lat, lng);

                    if ("homeclick".equals(type)) {
                        locationHashMap = new HashMap<>();
                        locationHashMap.put("latitude", String.valueOf(latLng.latitude));
                        locationHashMap.put("longitude", String.valueOf(latLng.longitude));
                        locationHashMap.put("home", homegettext);
                        locationHashMap.put("token", sessionManager.getAccessToken());
                        updateRiderLoc();
                    } else if ("workclick".equals(type)) {
                        locationHashMap = new HashMap<>();
                        locationHashMap.put("latitude", String.valueOf(latLng.latitude));
                        locationHashMap.put("longitude", String.valueOf(latLng.longitude));
                        locationHashMap.put("work", workgettext);
                        locationHashMap.put("token", sessionManager.getAccessToken());
                        updateRiderLoc();
                    }
                }
            }

            ;
        }.execute();
    }

    public void languagelist() {

        languageView = new RecyclerView(Setting.this);
        languagelist = new ArrayList<>();
        loadlang();

        LanguageAdapter = new LanguageAdapter(this, languagelist);
        languageView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        languageView.setAdapter(LanguageAdapter);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.currency_header, null);
        TextView T = (TextView) view.findViewById(R.id.header);
        T.setText(getString(R.string.selectlanguage));
        alertDialogStores2 = new android.app.AlertDialog.Builder(Setting.this)
                .setCustomTitle(view)
                .setView(languageView)
                .setCancelable(true)
                .show();
        languagelayout.setClickable(true);

        alertDialogStores2.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (langclick) {
                    langclick = false;
                    String langocde = sessionManager.getLanguageCode();
                    String lang = sessionManager.getLanguage();
                    language.setText(lang);
                    updateLanguage();
                    setLocale(langocde);
                    recreate();
                    Intent intent = new Intent(Setting.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

            }
        });
    }

    public void loadlang() {

        String[] languages = getResources().getStringArray(R.array.language);
        String[] langCode = getResources().getStringArray(R.array.languageCode);
        for (int i = 0; i < languages.length; i++) {
            CurrencyModel listdata = new CurrencyModel();
            listdata.setCurrencyName(languages[i]);
            listdata.setCurrencySymbol(langCode[i]);
            languagelist.add(listdata);

        }
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    // Load currency list deatils in dialog
    public void currency_list() {

        recyclerView1 = new RecyclerView(Setting.this);
        currencyListAdapter = new CurrencyListAdapter(this, currencyList);

        recyclerView1.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView1.setAdapter(currencyListAdapter);
        // loadcurrencylist(0);

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (isInternetAvailable) {
            currencyList();
        } else {
            commonMethods.showMessage(Setting.this, dialog, getResources().getString(R.string.no_connection));
        }
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.currency_header, null);
        alertDialogStores1 = new android.app.AlertDialog.Builder(Setting.this)
                .setCustomTitle(view)
                .setView(recyclerView1)
                .setCancelable(true)
                .show();
        currencylayout.setClickable(true);

        alertDialogStores1.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (currencyclick) {
                    currencyclick = false;
                    currency = sessionManager.getCurrencyCode();
                    // Toast.makeText(getApplicationContext(),"Dismiss dialog "+currency_codes,Toast.LENGTH_SHORT).show();
                    System.out.print("currency" + currency);
                    if (currency != null) {
                        currency_code.setText(currency);
                    } else {
                        currency_code.setText(getResources().getString(R.string.usd));
                    }
                    updateCurrency();
                }
            }
        });


    }

    // Get currency list from API
    public void currencyList() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.currencyList(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_CURRENCYLIST, this));

    }

    public void onSuccessGetCurr(JsonResponse jsonResp) {
        CurrencyModelList currencyModel = gson.fromJson(jsonResp.getStrResponse(), CurrencyModelList.class);
        ArrayList<CurrencyListModel> currencyListModel = currencyModel.getCurrencyList();
        currencyList.clear();
        currencyList.addAll(currencyListModel);
        currencyListAdapter.notifyDataChanged();

    }

    // After Get currency to Update currency
    public void updateCurrency() {
        commonMethods.showProgressDialog(this, customDialog);
        currency = sessionManager.getCurrencyCode();
        if (currency != null) {
            currency_code.setText(currency);
        } else {
            currency_code.setText(getResources().getString(R.string.usd));
        }
        apiService.updateCurrency(sessionManager.getAccessToken(), currency).enqueue(new RequestCallback(REQ_UPDATE_CURR, this));

    }

    public void updateLanguage() {
        // commonMethods.showProgressDialog(this, customDialog);
        Language = sessionManager.getLanguage();
        LanguageCode = sessionManager.getLanguageCode();

        if (Language != null) {
            language.setText(Language);
        } else {
            language.setText("English");
        }
        apiService.updateLanguage(sessionManager.getAccessToken(), LanguageCode).enqueue(new RequestCallback(REQ_UPDATE_LANG, this));

    }


}
