package com.trioangle.gofer.GladePay;

import android.os.Build;
import android.util.Log;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trioangle.gofer.BuildConfig;
import com.trioangle.gofer.GladePay.utils.TLSSocketFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API Client Class
 */
public class ApiClient {

    /**
     * merchantId
     */
    private static String merchantId;

    /**
     * merchantKey
     */
    private static String merchantKey;

    /**
     * isLive boolean status if live mode or demo mode
     */
    private static boolean isLive;

    /**
     * API_BASE_URL to represent the initialized BASE URL
     */
    private static String API_BASE_URL;

    /**
     * DEMO BASE URL
     */
    private static final String DEMO_BASE_URL = "https://demo.api.gladepay.com/";

    /**
     * LIVE BASE URL
     */
    private static final String LIVE_BASE_URL = "https://api.gladepay.com/";


    /**
     * GladepayApiService
     */
    private GladepayApiService gladepayApiService;

    public ApiClient(String merchantId, String merchantKey, boolean isLive) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        ApiClient.merchantId = merchantId;
        ApiClient.merchantKey = merchantKey;
        ApiClient.isLive = isLive;

        if(ApiClient.isLive){
            ApiClient.API_BASE_URL = this.LIVE_BASE_URL;
        }else {
            ApiClient.API_BASE_URL = this.DEMO_BASE_URL;
        }


        Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                    .create();

        TLSSocketFactory tlsV1point2factory = new TLSSocketFactory();
        OkHttpClient okHttpClient = new OkHttpClient
                    .Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Interceptor.Chain chain) throws IOException {
                            Request original = chain.request();
                            // Add headers so we get Android version and Gladepay Library version
                            Request.Builder builder = original.newBuilder()
                                    .header("User-Agent", "Android_" + Build.VERSION.SDK_INT + "_Gladepay_" + BuildConfig.VERSION_NAME)
                                    .header("X-Gladepay-Build", String.valueOf(BuildConfig.VERSION_CODE))
                                    .header("Accept", "application/json")
                                    .header("content-type:", "application/json")
                                    .header("mid", ApiClient.merchantId)
                                    .header("key", ApiClient.merchantKey)
                                    .method(original.method(), original.body());
                            Request request = builder.build();

                            return chain.proceed(request);
                        }
                    })
                    .sslSocketFactory(tlsV1point2factory, tlsV1point2factory.getX509TrustManager())
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiClient.API_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            Log.i(getClass().getName() + "RESPONSE-URL API-CLIENT", ApiClient.API_BASE_URL);

            gladepayApiService = retrofit.create(GladepayApiService.class);
    }

    public GladepayApiService getGladepayApiService() {
        return gladepayApiService;
    }
}
