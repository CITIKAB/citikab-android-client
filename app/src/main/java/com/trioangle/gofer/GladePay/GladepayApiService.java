package com.trioangle.gofer.GladePay;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;

public interface GladepayApiService {

    @PUT("payment")
    Call<JsonObject> initiateTransactions(@Body JsonObject body);


    @PUT("payment")
     Call<JsonObject> validateTransaction(@Body JsonObject body);

    @PUT("payment")
    Call<JsonObject> PaymentwithTOkenTransaction(@Body JsonObject body);

}
