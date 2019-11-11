package com.trioangle.gofer.views.signinsignup;
/**
 * @package com.trioangle.gofer
 * @subpackage signin_signup
 * @category SSSocialActivity
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.signinsignup.SigninResult;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.views.customize.CustomDialog;
import com.trioangle.gofer.views.facebookAccountKit.FacebookAccountKitActivity;
import com.trioangle.gofer.views.main.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static com.trioangle.gofer.utils.CommonKeys.*;
import static com.trioangle.gofer.utils.CommonMethods.*;
import static com.trioangle.gofer.utils.Enums.REQ_SIGNUP;

/* ************************************************************
   Sign in and sign up using social media (FaceBook and Google plus) home page
   ************************************************************ */
public class SSSocialActivity extends AppCompatActivity implements ServiceListener {

    //Google plus varibles
    private static final int RC_SIGN_IN = 0;
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
    public @InjectView(R.id.socialback)
    RelativeLayout socialback;
    public @InjectView(R.id.facebooklogin)
    RelativeLayout facebooklogin;
    public @InjectView(R.id.googlelogin)
    RelativeLayout googlelogin;
    public @InjectView(R.id.social)
    LinearLayout social;
    public @InjectView(R.id.choose)
    TextView choose;
    public AlertDialog alert;
    public Bundle parameters;
    public String fbEmail;
    public String fbFullName;
    public String fbFirstName;
    public String fbLastName;
    public String fbUserProfile;
    public String fbID;
    public int count = 1;
    public String googleEmail;
    public String googleFullName;
    public String googleUserProfile;
    public String googleID;
    public SigninResult loginResult;
    // Google API Client Declaration
    protected boolean isInternetAvailable;
    //Facebook Declaration
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    //GOOGLEPLUS;
    GoogleSignInClient mGoogleSignInClient;


    @OnClick(R.id.socialback)
    public void back() {
        onBackPressed();
    }

    @OnClick(R.id.facebooklogin)
    public void fblogin() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        } else {
            commonMethods.showProgressDialog(SSSocialActivity.this, customDialog);
            LoginManager.getInstance().logOut();//Logout Facebook
            LoginManager.getInstance().logInWithReadPermissions(SSSocialActivity.this, Arrays.asList("public_profile", "email"));
        }
    }

    @OnClick(R.id.googlelogin)
    public void gplogin() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        } else {
            count = 1;

            signIn();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sssocial);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(500);
            getWindow().getSharedElementReturnTransition().setDuration(500)
                    .setInterpolator(new DecelerateInterpolator());
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            social.setTransitionName("social");
            choose.setTransitionName("mobilenumber");
        }

        //Facebook Initialize
        faceBookInitialize();

        //GooglePlus Initialize
        googlePlusInitialize();

    }


    /**
     * Facebook SDK initialize
     */
    public void faceBookInitialize() {
        //Facebook Initialize
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                DebuggableLogI("Social", "FBToken");
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        /**
         *  Get Facebook key hash for devloper
         */
        getFbKeyHash(CommonMethods.getAppPackageName());

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        if (Profile.getCurrentProfile() == null) {
                            profileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                    displayMessage(profile2);
                                    profileTracker.stopTracking();
                                }
                            };
                            profileTracker.startTracking();
                        } else {
                            Profile profile = Profile.getCurrentProfile();
                            displayMessage(profile);

                        }

                        /**
                         *  Get facebook user details
                         */
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {

                                        fbEmail = object.optString("email");
                                        fbFullName = object.optString("name");
                                        fbID = object.optString("id");


                                        sessionManager.setEmail(fbEmail);
                                        sessionManager.setFacebookId(fbID);
                                        sessionManager.setGoogleId("GPID");
                                        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
                                        if (!isInternetAvailable) {
                                            commonMethods.showMessage(SSSocialActivity.this, dialog, getString(R.string.no_connection));
                                        } else {
                                            commonMethods.showProgressDialog(SSSocialActivity.this, customDialog);

                                            apiService.socialoldsignup("", sessionManager.getFacebookId(), sessionManager.getDeviceType(), sessionManager.getDeviceId(), sessionManager.getLanguageCode()).enqueue(new RequestCallback(REQ_SIGNUP, SSSocialActivity.this));

                                            //  new checkId().execute(url);
                                        }
                                    }
                                });

                        parameters = new Bundle();
                        parameters.putString("fields", "id,name,link,gender,birthday,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                        Bundle bundle = new Bundle();
                        bundle.putString("fields", "token_for_business");

                    }

                    @Override
                    public void onCancel() {
                        commonMethods.hideProgressDialog();

                    }

                    @Override
                    public void onError(FacebookException e) {
                        commonMethods.hideProgressDialog();
                        DebuggableLogE("Facebooksdk", "Login with Facebook failure", e);
                        Toast.makeText(getApplicationContext(), "An unknown network error has occured", Toast.LENGTH_LONG).show();

                    }


                });
    }

    /**
     * Create FB KeyHash
     */
    public void getFbKeyHash(String packageName) {

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));

                DebuggableLogE("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            DebuggableLogE("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            DebuggableLogE("no such an algorithm", e.toString());
        } catch (Exception e) {
            DebuggableLogE("exception", e.toString());
        }

    }

    /**
     * get Facebook Details
     */
    private void displayMessage(Profile profile) {

        DebuggableLogE("Facebook Profile", String.valueOf(profile));
        if (profile != null) {
            DebuggableLogE("Facebook Profile", profile.getName());
            DebuggableLogE("Facebook Profile", profile.getFirstName());
            DebuggableLogE("Facebook Profile", profile.getLastName());
            DebuggableLogE("Facebook Profile", profile.getId());
            DebuggableLogE("Facebook Profile", profile.getProfilePictureUri(400, 400).toString());
            fbFullName = profile.getName();
            fbFirstName = profile.getFirstName();
            fbLastName = profile.getLastName();
            fbID = profile.getId();
            fbUserProfile = profile.getProfilePictureUri(400, 400).toString();
            //fbUserProfile = profile.getProfilePictureUri(400, 400).toString();
            sessionManager.setFirstName(fbFirstName);
            sessionManager.setLastName(fbLastName);
            System.out.println("Fb Profile image One : " + fbUserProfile);
            sessionManager.setProfilepicture(fbUserProfile);

        }
    }

    /**
     * Call Facebook StartActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        commonMethods.hideProgressDialog();

        if (requestCode == RC_SIGN_IN) {


            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            mIntentInProgress = false;


        } else if (requestCode == ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT && resultCode == RESULT_OK) {
            /*if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER) {
                openRegisterActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY), data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
            } else if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER) {
                commonMethods.showMessage(this, dialog, data.getStringExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY));

            }*/

            openRegisterActivity(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY), data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            getProfileInformation(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google login", "signInResult:failed code=" + e.getStatusCode());

            getProfileInformation(null);
        }
    }

    /********************************************************************
     Google Signin Start
     ********************************************************************/

    public void googlePlusInitialize() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestIdToken(getString(R.string.google_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    /**
     * Google API client stop
     */
    protected void onStop() {
        super.onStop();

    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation(GoogleSignInAccount account) {


        commonMethods.hideProgressDialog();
        if (account != null) {
            googleID = account.getId();

            googleFullName = account.getDisplayName();
            if (account.getPhotoUrl() != null) googleUserProfile = account.getPhotoUrl().toString();
            else googleUserProfile = "";

            googleEmail = account.getEmail();
            String name = account.getAccount().name;

            googleUserProfile = googleUserProfile.replace("s96-c", "s400-c");
            System.out.println("googleUserProfile : " + googleUserProfile);
            String[] splitStr = googleFullName.split("\\s+");
            String firstName = splitStr[0];
            String lastName = "";
            for (int i = 1; i < splitStr.length; i++) {
                lastName = lastName + " " + splitStr[i];
            }
            if (lastName.equals("")) lastName = " ";

            sessionManager.setEmail(googleEmail);
            sessionManager.setFacebookId("FBID");
            sessionManager.setGoogleId(googleID);
            sessionManager.setFirstName(firstName);
            sessionManager.setLastName(lastName);
            sessionManager.setProfilepicture(googleUserProfile + "");

            System.out.println("firstName " + firstName);
            System.out.println("lastName " + lastName);
            isInternetAvailable = commonMethods.isOnline(getApplicationContext());
            if (!isInternetAvailable) {
                commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
            } else {

                if (count == 1) {
                    signOut();

                    commonMethods.showProgressDialog(SSSocialActivity.this, customDialog);

                    apiService.socialoldsignup(sessionManager.getGoogleId(), "", sessionManager.getDeviceType(), sessionManager.getDeviceId(), sessionManager.getLanguageCode()).enqueue(new RequestCallback(REQ_SIGNUP, SSSocialActivity.this));

                    // new checkId().execute(url);

                }
                count++;
            }
        }


    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onBackPressed() {

        commonMethods.hideProgressDialog();

        super.onBackPressed();
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);

    }

    // This fuctioned used turn on wifi and mobile data setting dialog
    protected void createNetErrorDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("You need internet connection for this app. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alert.dismiss();
                                alert.cancel();
                                Intent i = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(i);


                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SSSocialActivity.this.finish();
                            }
                        }
                );
        alert = builder.create();

        alert.show();
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (jsonResp.isSuccess()) {
            onSuccessLogin(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            if (jsonResp.getStatusMsg().equals("New User")) {
                openFacebookAccountKitActivity();
            } else {
                commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
            }

            DebuggableLogV("jsonResp.getStatusMsg()", "" + jsonResp.getStatusMsg());
        }
    }


    private void openFacebookAccountKitActivity() {
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);

    }


    public void openRegisterActivity(String phoneNumber, String countryCode) {
        Intent signin = new Intent(getApplicationContext(), SSRegisterActivity.class);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY, phoneNumber);
        signin.putExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY, countryCode);
        startActivity(signin);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);
        finish();
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        if (!TextUtils.isEmpty(jsonResp.getStatusMsg()))
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        commonMethods.hideProgressDialog();
    }

    public void onSuccessLogin(JsonResponse jsonResp) {
        loginResult = gson.fromJson(jsonResp.getStrResponse(), SigninResult.class);
        sessionManager.setCurrencySymbol(String.valueOf(Html.fromHtml(loginResult.getCurrencySymbol())));
        sessionManager.setCurrencyCode(loginResult.getCurrencyCode());
        sessionManager.setAcesssToken(loginResult.getToken());
        sessionManager.setWalletAmount(loginResult.getWalletAmount());
        sessionManager.setPaypalMode(loginResult.getPaypalMode());
        sessionManager.setPaypalAppId(loginResult.getPaypalAppId());
        sessionManager.setGoogleMapKey(loginResult.getGoogleMapKey());
        sessionManager.setFacebookAppId(loginResult.getFbId());
        sessionManager.setUserId(loginResult.getUserId());
        sessionManager.setIsrequest(false);
        commonMethods.hideProgressDialog();
        try {
            JSONObject response = new JSONObject(jsonResp.getStrResponse());
            if (response.has("promo_details")) {
                int promocount = response.getJSONArray("promo_details").length();

                sessionManager.setPromoDetail(response.getString("promo_details"));
                sessionManager.setPromoCount(promocount);
            }
        } catch (JSONException j) {
            j.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ub__slide_in_right, R.anim.ub__slide_out_left);

    }

    /**
     * Google Plus account permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 5:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    commonMethods.hideProgressDialog();

                    //signInWithGplus();  // Google Signup function
                } else {
                    // Permission Denied
                    Toast.makeText(getApplicationContext(), "Get Account permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

}
