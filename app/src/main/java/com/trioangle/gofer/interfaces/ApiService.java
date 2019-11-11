package com.trioangle.gofer.interfaces;
/**
 * @package com.trioangle.gofer
 * @subpackage interfaces
 * @category ApiService
 * @author Trioangle Product Team
 * @version 1.0
 **/

import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/*****************************************************************
 ApiService
 ****************************************************************/

public interface ApiService {

    //Check user Mobile Number
    @GET("numbervalidation")
    Call<ResponseBody> numbervalidation(@Query("mobile_number") String mobile_number, @Query("country_code") String country_code, @Query("user_type") String user_type, @Query("language") String language, @Query("forgotpassword") String forgotpassword);

    //Forgot password
    @GET("forgotpassword")
    Call<ResponseBody> forgotpassword(@Query("mobile_number") String mobile_number, @Query("country_code") String country_code, @Query("user_type") String user_type, @Query("password") String password, @Query("device_type") String device_type, @Query("device_id") String device_id);

    //Login
    @GET("login")
    Call<ResponseBody> login(@Query("mobile_number") String mobile_number, @Query("country_code") String country_code, @Query("user_type") String user_type, @Query("password") String password, @Query("device_type") String device_type, @Query("device_id") String device_id, @Query("language") String language);


    // Social login
    @GET("socialsignup")
    Call<ResponseBody> socialsignup(@QueryMap HashMap<String, String> hashMap);

    // Old Social login
    @GET("socialsignup")
    Call<ResponseBody> socialoldsignup(@Query("google_id") String google_id, @Query("fb_id") String fb_id, @Query("device_type") String device_type, @Query("device_id") String device_id, @Query("language") String language);

    // Register
    @GET("register")
    Call<ResponseBody> register(@QueryMap HashMap<String, String> hashMap);

    // Get Rider Profile
    @GET("get_rider_profile")
    Call<ResponseBody> getRiderProfile(@Query("token") String token);

    // Get Driver details
    @GET("driver_details")
    Call<ResponseBody> getDriverDetails(@Query("token") String token, @Query("trip_id") String trip_id);

    // Get Driver details
    @GET("trip_rating")
    Call<ResponseBody> tripRating(@Query("token") String token, @Query("trip_id") String trip_id, @Query("rating") String rating, @Query("rating_comments") String rating_comments, @Query("user_type") String user_type);

    // Update Device ID for Push notification
    @GET("search_cars")
    Call<ResponseBody> searchCars(@QueryMap HashMap<String, String> hashMap);

    @GET("request_cars")
    Call<ResponseBody> sendRequest(@QueryMap HashMap<String, String> hashMap);

    @GET("save_schedule_ride")
    Call<ResponseBody> scheduleRide(@QueryMap HashMap<String, String> hashMap);

    // Update location with lat,lng
    @GET("updateriderlocation")
    Call<ResponseBody> updateLocation(@QueryMap HashMap<String, String> hashMap);

    // Update Work, Home location
    @GET("update_rider_location")
    Call<ResponseBody> uploadRiderLocation(@QueryMap HashMap<String, String> hashMap);

    // Get Trip Details
    @GET("get_rider_trips")
    Call<ResponseBody> getRiderTrips(@Query("token") String token, @Query("user_type") String type);

    // Get Scheduled Rides
    @GET("get_schedule_rides")
    Call<ResponseBody> getScheduleRides(@Query("token") String token);

    // Change Paypal CurrencyModelList
    @GET("paypal_currency_conversion")
    Call<ResponseBody> paypalCurrency(@Query("token") String token, @Query("currency_code") String currency_code, @Query("amount") String amount);

    // Add wallet amount using paypal
    @GET("add_wallet")
    Call<ResponseBody> addWalletMoneyUsingPaypal(@Query("token") String token, @Query("paykey") String paykey,  @Query("amount") String amount);

    // Add wallet amount using stripe card
    @GET("add_wallet")
    Call<ResponseBody> addWalletMoneyUsingStripe(@Query("token") String token, @Query("payment_type") String paymentType, @Query("amount") String amount);

    // Add wallet amount using Gladepay
    @GET("add_wallet")
    Call<ResponseBody> addWalletMoneyUsingStripe(@Query("token") String token, @Query("amount") String chargedAmount,@Query("cardToken") String cardToken,@Query("brand") String brand,@Query("mask")String mask);

    @GET("after_payment")
    Call<ResponseBody> after_paymentUsingGladePay(@Query("token") String token, @Query("amount") String chargedAmount,@Query("cardToken") String cardToken,@Query("mask")String mask, @Query("trip_id") String tripid,@Query("txnRef")String txnRef);

    // After Payment
    @GET("after_payment")
    Call<ResponseBody> afterPayment(@Query("token") String token, @Query("paykey") String paykey, @Query("trip_id") String amount);

    // Proceed To Card Payment
    @GET("after_payment")
    Call<ResponseBody> proceedToCardPayment(@Query("token") String token, @Query("trip_id") String amount);

    // Update Rider Profile
    @GET("update_rider_profile")
    Call<ResponseBody> updateProfile(@Query("profile_image") String profile_image, @Query("first_name") String first_name, @Query("last_name") String last_name, @Query("country_code") String country_code, @Query("mobile_number") String mobile_number, @Query("email_id") String email_id, @Query("token") String token);

    //Upload Profile Image
    @POST("upload_profile_image")
    Call<ResponseBody> uploadImage(@Body RequestBody RequestBody, @Query("token") String token);

    // Log out Rider
    @GET("logout")
    Call<ResponseBody> logOut(@Query("token") String token, @Query("user_type") String user_type);

    // Get Currency
    @GET("currency_list")
    Call<ResponseBody> currencyList(@Query("token") String token);

    // Get Currency
    @GET("language")
    Call<ResponseBody> updateLanguage(@Query("token") String token, @Query("language") String language);

    // Update Currency
    @GET("update_user_currency")
    Call<ResponseBody> updateCurrency(@Query("token") String token, @Query("currency_code") String currency_code);

    // Cancel selected order
    @GET("cancel_trip")
    Call<ResponseBody> cancelTrip(@Query("cancel_reason") String cancelReason, @Query("cancel_comments") String cancelMessage, @Query("trip_id") String trip_id, @Query("user_type") String user_type, @Query("token") String token);

    // Cancel selected order
    @GET("schedule_ride_cancel")
    Call<ResponseBody> cancelScheduleTrip(@Query("cancel_reason") String cancelReason, @Query("cancel_comments") String cancelMessage, @Query("trip_id") String trip_id, @Query("user_type") String user_type, @Query("token") String token);

    // Change Mobile Number
    @GET("update_device")
    Call<ResponseBody> updateDevice(@Query("token") String token, @Query("user_type") String userType, @Query("device_type") String device_type, @Query("device_id") String device_id);

    // Get Promo Details
    @GET("promo_details")
    Call<ResponseBody> promoDetails(@Query("token") String token);

    // Add Promo Code
    @GET("add_promo_code")
    Call<ResponseBody> addPromoDetails(@Query("token") String token, @Query("code") String code);

    // add card details
    @GET("add_card_details")
    Call<ResponseBody> addCard(@Query("stripe_id") String stripeId, @Query("token") String token);

    // SoS alert
    @GET("sosalert")
    Call<ResponseBody> sosalert(@Query("token") String token, @Query("latitude") String latitude, @Query("longitude") String longitude);

    // Add Emergency Contact
    @GET("sos")
    Call<ResponseBody> sos(@Query("token") String token, @Query("mobile_number") String mobile_number, @Query("action") String action, @Query("name") String name, @Query("country_code") String country_code, @Query("id") String id);

    // this api called to resume the trip from MainActivity while Rider get-in to app
    @GET("incomplete_trip_details")
    Call<ResponseBody> getInCompleteTripsDetails(@Query("token") String token);


    @GET("get_referral_details")
    Call<ResponseBody> getReferralDetails(@Query("token") String token);

    // get Trip invoice Details  Rider
    @GET("get_invoice")
    Call<ResponseBody> getInvoice(@Query("token") String token,@Query("trip_id") String TripId,@Query("user_type") String userType);

    //Force Update API
    @GET("check_version")
    Call<ResponseBody> checkVersion(@Query("version") String code, @Query("user_type") String type, @Query("device_type") String deviceType);

//GladePAy initialize
    @GET("glade_pay_key")
    Call<ResponseBody> glade_pay_key(@Query("token") String token);

}
