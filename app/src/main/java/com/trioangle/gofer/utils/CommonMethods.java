package com.trioangle.gofer.utils;
/**
 * @package com.trioangle.gofer
 * @subpackage utils
 * @category CommonMethods
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.app.ActivityManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.trioangle.gofer.GladePay.Card;
import com.trioangle.gofer.GladePay.Charge;
import com.trioangle.gofer.GladePay.Gladepay;
import com.trioangle.gofer.GladePay.GladepaySdk;
import com.trioangle.gofer.GladePay.PTransaction;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.helper.Constants;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.sidebar.payment.AddWalletActivity;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.firebaseChat.ActivityChat;
import com.trioangle.gofer.views.firebaseChat.FirebaseChatNotificationService;
import com.trioangle.gofer.views.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static com.trioangle.gofer.utils.CommonKeys.last4;

/*****************************************************************
 CommonMethods
 ****************************************************************/
public class CommonMethods {

    public static PopupWindow popupWindow = null;
    public static CustomDialog progressDialog;
    public @Inject
    SessionManager sessionManager;

    public Charge charge;
    private PTransaction transaction1;

    public CommonMethods() {
        AppController.getAppComponent().inject(this);
    }



    public void showProgressDialog(AppCompatActivity mActivity, CustomDialog customDialog) {
        if (mActivity == null || customDialog == null || (customDialog.getDialog() != null && customDialog.getDialog().isShowing()))
            return;
        progressDialog = new CustomDialog(true);
        progressDialog.show(mActivity.getSupportFragmentManager(), "");
    }

    public void hideProgressDialog() {
        if (progressDialog == null || progressDialog.getDialog() == null || !progressDialog.getDialog().isShowing())
            return;
        progressDialog.dismissAllowingStateLoss();
        progressDialog = null;
    }
    //Show Dialog with message
    public void showMessage(Context context, AlertDialog dialog, String msg) {
        if (context != null && dialog != null && !((Activity) context).isFinishing()) {
            dialog.setMessage(msg);
            dialog.show();
        }
    }
    //Create and Get Dialog
    public AlertDialog getAlertDialog(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        return dialog;
    }

    ////////////////// CAMERA ///////////////////////////////////
    public File getDefaultFileName(Context context) {
        File imageFile;
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent) { // External storage path
            imageFile = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.FILE_NAME + System.currentTimeMillis() + ".png");
        } else {  // Internal storage path
            imageFile = new File(context.getFilesDir() + File.separator + Constants.FILE_NAME + System.currentTimeMillis() + ".png");
        }
        return imageFile;
    }

    public static Drawable getCardImage(String brand, Resources getRes) {
        System.out.println("brand"+brand);
        Drawable card;
        switch (brand) {
            case "Visa": {
            //    layerDrawable.findDrawableByLayerId(R.id.card_visa);
                card = getRes.getDrawable(R.drawable.card_visa_resize);
                break;
            }
            case "MasterCard": {
                card = getRes.getDrawable(R.drawable.card_master_resize);
                break;
            }
            case "Discover": {
                card = getRes.getDrawable(R.drawable.card_discover_resize);
                break;
            }
            case "Amex":
            case "American Express": {
                card = getRes.getDrawable(R.drawable.card_amex_resize);
                break;
            }
            case "JCB":
            case "JCP": {
                card = getRes.getDrawable(R.drawable.card_jcp_resize);
                break;
            }
            case "Diner":
            case "Diners": {
                card = getRes.getDrawable(R.drawable.card_diner_resize);
                break;
            }
            case "Verve": {
                card = getRes.getDrawable(R.drawable.card_verve_resize);
                break;
            }
            case "UnionPay":
            case "Union": {
                card = getRes.getDrawable(R.drawable.card_unionpay_resize);
                break;
            }
            default: {
                card = getRes.getDrawable(R.drawable.card_resize);
                break;
            }
        }

        return card;
        /*if (brand.contains("Visa")) {
            card = getRes.getDrawable(R.drawable.card_visa);
        } else if (brand.contains("MasterCard")) {
            card = getRes.getDrawable(R.drawable.card_master);
        } else if (brand.contains("Discover")) {
            card = getRes.getDrawable(R.drawable.card_discover);
        } else if (brand.contains("Amex") || brand.contains("American Express")) {
            card = getRes.getDrawable(R.drawable.card_amex);
        } else if (brand.contains("JCB") || brand.contains("JCP")) {
            card = getRes.getDrawable(R.drawable.card_jcp);
        } else if (brand.contains("Diner") || brand.contains("Diners")) {
            card = getRes.getDrawable(R.drawable.card_diner);
        } else if ("Union".contains(brand) || "UnionPay".contains(brand)) {
            card = getRes.getDrawable(R.drawable.card_unionpay);
        } else {
            card = getRes.getDrawable(R.drawable.card_basic);
        }*/
        // return getRes.getDrawable(R.drawable.card_basic);

    }


    public boolean isNotEmpty(Object s) {
        if (s == null) {
            return false;
        }
        if ((s instanceof String) && (((String) s).trim().length() > 0)) {
            return true;
        }
        if (s instanceof ArrayList) {
            return !((ArrayList<?>) s).isEmpty();
        }
        if (s instanceof Map) {
            return !((Map<?, ?>) s).isEmpty();
        }
        if (s instanceof List) {
            return !((List<?>) s).isEmpty();
        }

        if (s instanceof Object[]) {
            return ((Object[]) s).length != 0;
        }
        return false;
    }

   /* public boolean isTaped() {
        if (SystemClock.elapsedRealtime() - Constants.mLastClickTime < 1000) return true;
        Constants.mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }*/

    public File cameraFilePath() {
        return new File(getDefaultCameraPath(), "Gofer_" + System.currentTimeMillis() + ".png");
    }

    public String getDefaultCameraPath() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (path.exists()) {
            File test1 = new File(path, "Camera/");
            if (test1.exists()) {
                path = test1;
            } else {
                File test2 = new File(path, "100MEDIA/");
                if (test2.exists()) {
                    path = test2;
                } else {
                    File test3 = new File(path, "100ANDRO/");
                    if (test3.exists()) {
                        path = test3;
                    } else {
                        test1.mkdirs();
                        path = test1;
                    }
                }
            }
        } else {
            path = new File(path, "Camera/");
            path.mkdirs();
        }
        return path.getPath();
    }

    public void refreshGallery(Context context, File file) {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file); //out is your file you saved/deleted/moved/copied
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////// CAMERA ///////////////////////////////////
  /*  public File getDefaultFileName(Context context) {
        File imageFile;
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (isSDPresent) { // External storage path
            imageFile = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.FILE_NAME + System.currentTimeMillis() + ".png");
        } else {  // Internal storage path
            imageFile = new File(context.getFilesDir() + File.separator + Constants.FILE_NAME + System.currentTimeMillis() + ".png");
        }
        return imageFile;
    }*/

    public void clearFileCache(String path) {
        try {
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path);
                if (file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void dropDownMenu(AppCompatActivity mActivity, View v, int[] images, final String[] listItems, final DropDownClickListener listener) {
        LayoutInflater layoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        *//*View popupView = layoutInflater.inflate(R.layout.drop_down_menu_list, null);
        ListView listView = (ListView) popupView.findViewById(R.id.drop_down_list);

        DropDownMenuAdapter adapter = new DropDownMenuAdapter(mActivity, R.layout.drop_down_menu_row, images, listItems);
        listView.setAdapter(adapter);
        listView.setSelected(false);*//*

        Rect displayFrame = new Rect();
        v.getWindowVisibleDisplayFrame(displayFrame);

        int displayFrameWidth = displayFrame.right - displayFrame.left;
        int[] loc = new int[2];
        v.getLocationInWindow(loc);

        if (popupWindow == null) {
            int width = (int) (120 * Resources.getSystem().getDisplayMetrics().density);
            popupWindow = new PopupWindow(popupView, width, WindowManager.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        if (!popupWindow.isShowing()) {
            popupView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.popup_anim_in));
            int margin = (displayFrameWidth - (loc[0] + v.getWidth()));
            int xOff = (displayFrameWidth - margin - popupWindow.getWidth()) - loc[0];
            popupWindow.showAsDropDown(v, (int) (xOff / 0.8), 0);
            popupWindow.setAnimationStyle(R.anim.popup_anim_in);

        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (popupWindow != null) popupWindow = null;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
                listener.onDropDrownClick(listItems[position]);
            }
        });
    }

    public boolean isSupportTransition() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }*/

    public static void startChatActivityFrom(Activity mActivity) {
        Intent startActivityIntent = new Intent(mActivity, ActivityChat.class);
        startActivityIntent.putExtra(CommonKeys.FIREBASE_CHAT_ACTIVITY_SOURCE_ACTIVITY_TYPE_CODE, CommonKeys.FIREBASE_CHAT_ACTIVITY_REDIRECTED_FROM_RIDER_OR_DRIVER_PROFILE);
        mActivity.startActivity(startActivityIntent);
    }

    public boolean isOnline(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    public Object getJsonValue(String jsonString, String key, Object object) {
        Object object1 = object;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(key)) object1 = jsonObject.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return new Object();
        }
        return object1;
    }

    public static String getAppVersionNameFromGradle(Context context) {
        String versionName;
        try {
            versionName = AppController.getContext().getPackageManager().getPackageInfo(AppController.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "0.0";
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getAppPackageName() {
        String packageName;
        try {
            packageName = AppController.getContext().getPackageName();
        } catch (Exception e) {
            packageName = "";
            e.printStackTrace();
        }
        return packageName;
    }

    public static void DebuggableLogE(String tag, String message) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CommonKeys.isLoggable) {
            Log.e(tag, message);
        }
    }

    public static void DebuggableLogE(String tag, String message, Throwable tr) {
        try {
            if (CommonKeys.isLoggable) {
                Log.e(tag, message, tr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void DebuggableLogI(String tag, @Nullable String message) {
        try {
            if (CommonKeys.isLoggable) {
                Log.i(tag, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void DebuggableLogD(String tag, String message) {
        try {
            if (CommonKeys.isLoggable) {
                Log.d(tag, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void DebuggableLogV(String tag, String message) {
        try {
            if (CommonKeys.isLoggable) {
                Log.v(tag, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void DebuggableToast(Context mContext, String message) {
        try {
            if (CommonKeys.isLoggable) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showUserMessage(View view, Context mContext, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public static void showUserMessage(Context mContext, String message) {
        try {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void gotoMainActivityFromChatActivity(Activity mActivity) {
        Intent mainActivityIntent = new Intent(mActivity, MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(mainActivityIntent);

        mActivity.finish();
    }

    public static void startFirebaseChatListenerService(Activity mActivity) {
        mActivity.startService(new Intent(mActivity, FirebaseChatNotificationService.class));
    }

    public static void stopFirebaseChatListenerService(Context mContext) {
        mContext.stopService(new Intent(mContext, FirebaseChatNotificationService.class));
    }

    public static boolean isMyBackgroundServiceRunning(Class<?> serviceClass, Context mContext) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void showUserMessage(@Nullable String message) {
        try {
            if (!TextUtils.isEmpty(message)) {
                Toast.makeText(AppController.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showServerInternalErrorMessage(Context context) {
        showUserMessage(context.getResources().getString(R.string.internal_server_error));

    }

    public static void copyContentToClipboard(Context mContext, String textToBeCopied){
        ClipboardManager cManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData cData = ClipData.newPlainText("text", textToBeCopied);
        if (cManager != null && !TextUtils.isEmpty(textToBeCopied)) {
            cManager.setPrimaryClip(cData);
            showUserMessage(mContext.getResources().getString(R.string.referral_code_copied));
        }else{
            showUserMessage(mContext.getResources().getString(R.string.referral_code_not_copied));
        }

    }

    public static void openPlayStore(Context context){
        final String appPackageName = getAppPackageName(); // getPackageName() from Context or Activity object
        try {
            Intent playstoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
            playstoreIntent.setPackage("com.android.vending");
            context.startActivity(playstoreIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


}

