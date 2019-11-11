package com.trioangle.gofer.utils;
/**
 * @package com.trioangle.gofer
 * @subpackage utils
 * @category RequestCallback
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.helper.Constants;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.views.signinsignup.SigninSignupActivity;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*****************************************************************
 RequestCallback
 ****************************************************************/
public class RequestCallback implements Callback<ResponseBody> {

    public @Inject
    JsonResponse jsonResp;
    public @Inject
    Context context;
    public @Inject
    SessionManager sessionManager;
    public @Inject
    ApiService apiService;
    public @Inject
    CommonMethods commonMethods;
    private ServiceListener listener;
    private int requestCode = 0;
    private String requestData = "";

    public RequestCallback() {
        AppController.getAppComponent().inject(this);
    }

    public RequestCallback(ServiceListener listener) {
        AppController.getAppComponent().inject(this);
        this.listener = listener;
    }

    public RequestCallback(int requestCode, ServiceListener listener) {
        AppController.getAppComponent().inject(this);
        this.listener = listener;
        this.requestCode = requestCode;
    }

    public RequestCallback(int requestCode, ServiceListener listener, String requestData) {
        AppController.getAppComponent().inject(this);
        this.listener = listener;
        this.requestCode = requestCode;
        this.requestData = requestData;
    }

    @Override
    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
        this.listener.onSuccess(getSuccessResponse(call, response), requestData);
    }

    @Override
    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
        this.listener.onFailure(getFailureResponse(call, t), requestData);
    }

    private JsonResponse getFailureResponse(Call<ResponseBody> call, Throwable t) {
        try {
            jsonResp.clearAll();
            if (call != null && call.request() != null) {
                jsonResp.setMethod(call.request().method());
                jsonResp.setRequestCode(requestCode);
                jsonResp.setUrl(call.request().url().toString());
                LogManager.i(call.request().toString());
            }
            jsonResp.setOnline(isOnline());
            if(isOnline()){
                jsonResp.setStatusMsg(context.getResources().getString(R.string.internal_server_error));
            }else{
                jsonResp.setStatusMsg(context.getResources().getString(R.string.network_failure));
            }
            jsonResp.setErrorMsg(t.getMessage());
            jsonResp.setSuccess(false);
            requestData = (context != null && !isOnline()) ? context.getResources().getString(R.string.network_failure) : t.getMessage();
            LogManager.e(t.getMessage());
            LogManager.e(String.valueOf(requestCode));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResp;
    }

    private JsonResponse getSuccessResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        try {
            jsonResp.clearAll();
            if (call != null && call.request() != null) {
                jsonResp.setMethod(call.request().method());
                jsonResp.setRequestCode(requestCode);
                jsonResp.setUrl(call.request().url().toString());
                LogManager.i(call.request().toString());
            }
            if (response != null) {
                jsonResp.setResponseCode(response.code());
                jsonResp.setSuccess(false);
                jsonResp.setStatusMsg(context.getResources().getString(R.string.internal_server_error));
                if (response.isSuccessful() && response.body() != null) {
                    String strJson = response.body().string();
                    jsonResp.setStrResponse(strJson);
                    jsonResp.setStatusMsg(getStatusMsg(strJson));
                    jsonResp.setStatusCode(getStatusCode(strJson));
                    if (jsonResp.getStatusMsg().equalsIgnoreCase("Token Expired")) {
                        jsonResp.setStatusMsg(context.getResources().getString(R.string.internal_server_error));
                        //String urls = call.request().url().toString();
                        // urls.replace(oldToken, sessionManager.getToken());
                        // call.clone().request().header(sessionManager.getToken());
                        //call.clone().enqueue(this);

                        /*OkHttpClient okHttpClient = new OkHttpClient();

                        Request request = new Request.Builder().url(urls).build();
                        okHttpClient.newCall(request).enqueue(new RequestCallback());*/

                        /*Request.Builder requestBuilder;
                        Request requests = new Request.Builder().url(urls).build();

                        requestBuilder = request.newBuilder()
                                .header("Authorization", sessionManager.getToken())
                                .method(request.method(), request.body());

                        requestBuilder.build();

                        requestBuilder.build()..execute();*/

                        //jsonResp.setStatusMsg("");
                    }
                    jsonResp.setSuccess(isSuccess(strJson));
                    LogManager.e(strJson);
                } else {

                    String strJson = response.errorBody().string();
                    jsonResp.setErrorMsg(response.errorBody().string());
                    if (response.errorBody() != null) {


                        jsonResp.setStrResponse(strJson);
                        jsonResp.setStatusMsg(getStatusMsg(strJson));
                        jsonResp.setStatusCode(getStatusCode(strJson));
                        if (jsonResp.getStatusMsg().equalsIgnoreCase("Token Expired")) {
                            jsonResp.setStatusMsg("");
                        }


                        if (response.code() == 401 || response.code() == 404) {
                            sessionManager.clearToken();
                            sessionManager.clearAll();

                            Intent intent = new Intent(context, SigninSignupActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                            ((Activity) context).finishAffinity();
                        }
                    }
                }
                jsonResp.setRequestData(requestData);
                jsonResp.setOnline(isOnline());
                requestData = (context != null && !isOnline()) ? context.getResources().getString(R.string.network_failure) : "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResp;
    }

    private boolean isOnline() {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    private boolean isSuccess(String jsonString) {
        boolean isSuccess = false;
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                String statusCode = (String) commonMethods.getJsonValue(jsonString, Constants.STATUS_CODE, String.class);
                isSuccess = !TextUtils.isEmpty(statusCode) && ("1".equals(statusCode) || "2".equals(statusCode));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    private String getStatusMsg(String jsonString) {
        String statusMsg = "";
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                statusMsg = (String) commonMethods.getJsonValue(jsonString, Constants.STATUS_MSG, String.class);
                if ("Token Expired".equalsIgnoreCase(statusMsg)) {
                    String token = (String) commonMethods.getJsonValue(jsonString, Constants.REFRESH_ACCESS_TOKEN, String.class);
                    sessionManager.setToken(token);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusMsg;
    }

    private String getStatusCode(String jsonString) {
        String statuscode = "";
        String statusMsg = "";
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                statuscode = (String) commonMethods.getJsonValue(jsonString, Constants.STATUS_CODE, String.class);
                statusMsg = (String) commonMethods.getJsonValue(jsonString, Constants.STATUS_MSG, String.class);
                if ("Token Expired".equalsIgnoreCase(statusMsg)) {
                    String token = (String) commonMethods.getJsonValue(jsonString, Constants.REFRESH_ACCESS_TOKEN, String.class);
                    sessionManager.setToken(token);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statuscode;
    }

}
