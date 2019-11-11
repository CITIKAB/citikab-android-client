package com.trioangle.gofer.dependencies.component;
/**
 * @package com.trioangle.gofer
 * @subpackage dependencies.component
 * @category AppComponent
 * @author Trioangle Product Team
 * @version 1.0
 **/


import com.trioangle.gofer.GladePay.GladePaycardActivity;
import com.trioangle.gofer.GladePay.PaymentTransactionManager;
import com.trioangle.gofer.GladePay.request.CardChargeRequestBody;
import com.trioangle.gofer.ScheduleRideDetailActivity;
import com.trioangle.gofer.adapters.CarDetailsListAdapter;
import com.trioangle.gofer.adapters.DataAdapter;
import com.trioangle.gofer.adapters.UpcomingAdapter;
import com.trioangle.gofer.backgroundtask.ImageCompressAsyncTask;
import com.trioangle.gofer.configs.RunTimePermission;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.database.AddFirebaseDatabase;
import com.trioangle.gofer.dependencies.module.AppContainerModule;
import com.trioangle.gofer.dependencies.module.ApplicationModule;
import com.trioangle.gofer.dependencies.module.NetworkModule;
import com.trioangle.gofer.helper.CommonDialog;
import com.trioangle.gofer.map.drawpolyline.DownloadTask;
import com.trioangle.gofer.pushnotification.MyFirebaseInstanceIDService;
import com.trioangle.gofer.pushnotification.MyFirebaseMessagingService;
import com.trioangle.gofer.sendrequest.CancelYourTripActivity;
import com.trioangle.gofer.sendrequest.DriverNotAcceptActivity;
import com.trioangle.gofer.sendrequest.DriverRatingActivity;
import com.trioangle.gofer.sendrequest.PaymentAmountPage;
import com.trioangle.gofer.sendrequest.SendingRequestActivity;
import com.trioangle.gofer.sidebar.AddHome;
import com.trioangle.gofer.sidebar.DriverContactActivity;
import com.trioangle.gofer.sidebar.EnRoute;
import com.trioangle.gofer.sidebar.FareBreakdown;
import com.trioangle.gofer.sidebar.Profile;
import com.trioangle.gofer.sidebar.ProfileMobileVerify;
import com.trioangle.gofer.sidebar.Setting;
import com.trioangle.gofer.sidebar.currency.CurrencyListAdapter;
import com.trioangle.gofer.sidebar.language.LanguageAdapter;
import com.trioangle.gofer.sidebar.payment.AddWalletActivity;
import com.trioangle.gofer.sidebar.payment.PaymentPage;
import com.trioangle.gofer.sidebar.payment.PromoAmountActivity;
import com.trioangle.gofer.sidebar.payment.PromoAmountAdapter;
import com.trioangle.gofer.sidebar.referral.ShowReferralOptions;
import com.trioangle.gofer.sidebar.trips.Past;
import com.trioangle.gofer.sidebar.trips.Receipt;
import com.trioangle.gofer.sidebar.trips.TripDetails;
import com.trioangle.gofer.sidebar.trips.Upcoming;
import com.trioangle.gofer.sidebar.trips.YourTrips;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.gofer.views.addCardDetails.AddCardActivity;
import com.trioangle.gofer.views.emergency.EmergencyContact;
import com.trioangle.gofer.views.emergency.SosActivity;
import com.trioangle.gofer.views.firebaseChat.ActivityChat;
import com.trioangle.gofer.views.firebaseChat.AdapterFirebaseRecylcerview;
import com.trioangle.gofer.views.firebaseChat.FirebaseChatHandler;
import com.trioangle.gofer.views.main.MainActivity;
import com.trioangle.gofer.views.peakPricing.PeakPricing;
import com.trioangle.gofer.views.search.PlaceSearchActivity;
import com.trioangle.gofer.views.signinsignup.*;
import com.trioangle.gofer.views.splash.SplashActivity;

import javax.inject.Singleton;

import dagger.Component;


/*****************************************************************
 App Component
 ****************************************************************/
@Singleton
@Component(modules = {NetworkModule.class, ApplicationModule.class, AppContainerModule.class})
public interface AppComponent {
    // SERVICE


    // ACTIVITY

    void inject(SplashActivity splashActivity);

    void inject(AddCardActivity addCardActivity);

    void inject(MainActivity mainActivity);

    void inject(ScheduleRideDetailActivity scheduleRideDetailActivity);

    void inject(SendingRequestActivity sendingRequestActivity);

    void inject(DriverNotAcceptActivity driverNotAcceptActivity);

    void inject(PlaceSearchActivity mainActivity);

    void inject(SigninSignupActivity signin_signup_activity);

    void inject(SSMobileActivity ssMobileActivity);

    void inject(SSOTPActivity ssotpActivity);

    void inject(SSSocialActivity ssotpActivity);

    void inject(SSForgotMyEmail ssForgotMyEmail);

    void inject(SSResetPassword ssResetPassword);

    void inject(SSRegisterActivity ssSocialDetailsActivity);

    void inject(SignupNameActivity signupNameActivity);

    void inject(SSPassword ssPassword);

    void inject(DriverContactActivity driverContactActivity);

    void inject(AddHome addHome);

    void inject(PaymentPage paymentPage);

    void inject(PaymentAmountPage paymentAmountPage);

    void inject(FareBreakdown fareBreakdown);

    void inject(AddWalletActivity addWalletActivity);

    void inject(PromoAmountActivity promoAmountActivity);

    void inject(YourTrips yourTrips);

    void inject(TripDetails tripDetails);

    void inject(EnRoute enRoute);

    void inject(SosActivity sos_activity);

    void inject(DriverRatingActivity driverRatingActivity);

    void inject(CancelYourTripActivity cancelYourTripActivity);

    void inject(CommonDialog commonDialog);

    void inject(Setting setting);

    void inject(Profile profile);

    void inject(ProfileMobileVerify profileMobileVerify);

    void inject(EmergencyContact emergencyContact);

    void inject(ActivityChat activityChat);

    void inject(FacebookAccountKitActivity facebookAccountKitActivity);

    void inject(SSLoginActivity loginActivity);

    void inject(PeakPricing peakPricing);

    void inject(ShowReferralOptions showReferralOptions);

    // Fragments
    void inject(Past past);

    void inject(Upcoming upcoming);

    void inject(Receipt receipt);

    // Utilities
    void inject(RunTimePermission runTimePermission);

    void inject(SessionManager sessionManager);

    void inject(CommonMethods commonMethods);

    void inject(RequestCallback requestCallback);

    // Adapters

    void inject(UpcomingAdapter upcomingAdapter);

    void inject(DataAdapter dataAdapter);

    void inject(PromoAmountAdapter promoAmountAdapter);

    void inject(CurrencyListAdapter currencyListAdapter);

    void inject(LanguageAdapter languageAdapter);

    void inject(CarDetailsListAdapter carDetailsListAdapter);

    void inject(MyFirebaseMessagingService myFirebaseMessagingService);

    void inject(MyFirebaseInstanceIDService myFirebaseInstanceIDService);

    void inject(FirebaseChatHandler firebaseChatHandler);

    void inject(AdapterFirebaseRecylcerview adapterFirebaseRecylcerview);


    // AsyncTask
    void inject(ImageCompressAsyncTask imageCompressAsyncTask);
    void inject(DownloadTask downloadTask);

    void inject(AddFirebaseDatabase firebaseDatabase);


    void inject(GladePaycardActivity gladePaycardActivity);


    void inject(CardChargeRequestBody cardChargeRequestBody);

    void inject(PaymentTransactionManager paymentTransactionManager);
}
