package com.trioangle.gofer.sidebar;
/**
 * @package com.trioangle.gofer
 * @subpackage Side_Bar
 * @category Profile
 * @author Trioangle Product Team
 * @version 1.5
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.trioangle.gofer.R;
import com.trioangle.gofer.backgroundtask.ImageCompressAsyncTask;
import com.trioangle.gofer.configs.RunTimePermission;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.main.RiderProfile;
import com.trioangle.gofer.helper.Constants;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ImageListener;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonKeys;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.utils.RuntimePermissionDialogFragment;
import com.trioangle.gofer.views.customize.CustomDialog;

import com.trioangle.gofer.views.facebookAccountKit.FacebookAccountKitActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.RequestBody;

import static com.trioangle.gofer.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY;
import static com.trioangle.gofer.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY;
import static com.trioangle.gofer.utils.CommonKeys.FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogI;
import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.Enums.REQ_GET_RIDER_PROFILE;
import static com.trioangle.gofer.utils.Enums.REQ_UPDATE_PROFILE;
import static com.trioangle.gofer.utils.Enums.REQ_UPLOAD_PROFILE_IMG;
import static net.hockeyapp.android.utils.Util.isValidEmail;
/* ************************************************************
   Rider profile details page
    *********************************************************** */

public class Profile extends AppCompatActivity implements ServiceListener, ImageListener, RuntimePermissionDialogFragment.RuntimePermissionRequestedCallback {

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
    RunTimePermission runTimePermission;
    public @Inject
    Gson gson;

    public @InjectView(R.id.savebutton)
    Button savebutton; // Save button
    public @InjectView(R.id.mobilenumber)
    TextView mobilenumber;  // Mobile number
    public @InjectView(R.id.profile_image)
    CircleImageView profile_image;  // Profile image view
    public @InjectView(R.id.mobile_code)
    CountryCodePicker ccp;  // Country code picker
    public String firstnamegettext;
    public String lastnamegettext;
    public String emailgettext;  // First, Last, Email text string
    public @InjectView(R.id.emaitext)
    EditText emaitext;
    public @InjectView(R.id.emaitext1)
    EditText emaitext1;  // Email Edit text
    public @InjectView(R.id.input_layout_first)
    TextInputLayout input_layout_first;
    public @InjectView(R.id.input_layout_last)
    TextInputLayout input_layout_last;
    public @InjectView(R.id.emailName)
    TextInputLayout emailName;
    public @InjectView(R.id.input_first)
    EditText input_first;
    public @InjectView(R.id.input_last)
    EditText input_last;
    public Bitmap bm;// Image bitmap
    public String imagepath;  // Image file path
    public String imageInSD;  // Store image in SD Card
    public File image;   // image file
    public String imageUr;  // Profile image url
    protected boolean isInternetAvailable;  // Check Network available or not
    private File imageFile = null;
    private Uri imageUri;

    @OnClick(R.id.movileclick)
    public void movileclick() {
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
    }

    @OnClick(R.id.transprantview)
    public void mobileCodeClick() {
        FacebookAccountKitActivity.openFacebookAccountKitActivity(this);
    }


    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @OnClick(R.id.savebutton)
    public void savebutton() {
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        if (!isInternetAvailable) {
            commonMethods.showMessage(Profile.this, dialog, getString(R.string.no_connection));
        } else {

            firstnamegettext = input_first.getText().toString();
            lastnamegettext = input_last.getText().toString();
            emailgettext = emaitext.getText().toString();


            if (!validateFirst()) {
                return;
            }
            if (!validateLast()) {
                return;
            }
            if (!validateEmail()) {
                emailName.setError(getString(R.string.error_msg_email));
                return;
            }

            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                firstnamegettext = URLEncoder.encode(firstnamegettext, "UTF-8");
                lastnamegettext = URLEncoder.encode(lastnamegettext, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            /**
             *  Update Rider profile
             */

            hideSoftKeyboard();

            emaitext1.setFocusableInTouchMode(true);
            emaitext1.setFocusable(true);
            emaitext1.requestFocus();

            // Update profile API Call
            updateProfile();
        }
    }

    @OnClick(R.id.profile_image)
    public void profileImage() {// Profile click listener
        //marshMallowPermission.getPhotoFromCamera();
        //checkAllPermission(Constants.PERMISSIONS_PHOTO);
        pickProfileImg();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        // Check network available or not
        isInternetAvailable = commonMethods.isOnline(getApplicationContext());

        input_first.addTextChangedListener(new NameTextWatcher(input_first));
        input_last.addTextChangedListener(new NameTextWatcher(input_last));
        emaitext.addTextChangedListener(new NameTextWatcher(emaitext));

        String profiledetails = String.valueOf(sessionManager.getProfileDetail()); // Get Profile details from JSON

        if (profiledetails == null || profiledetails.isEmpty() || profiledetails.equals("null")) {
            if (!isInternetAvailable) {
                commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
            } else {
                getRiderDetails(); // Get JSON data From profile API
            }
        } else {
            loaddata(profiledetails); // Load Profile details
        }

    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Back button pressed
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sessionManager.setProfileDetail("");
        sessionManager.setPhoneNumber("");
        overridePendingTransition(R.anim.ub__slide_in_left, R.anim.ub__slide_out_right);
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
            case REQ_UPDATE_PROFILE:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    sessionManager.setPhonenumberEdited(false);
                    onSuccessUpdateProf(jsonResp);
                } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
                    commonMethods.hideProgressDialog();
                    commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
                }
                break;
            case REQ_UPLOAD_PROFILE_IMG:
                if (jsonResp.isSuccess()) {
                    commonMethods.hideProgressDialog();
                    onSuccessProf(jsonResp);
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
        commonMethods.hideProgressDialog();
    }

    /**
     * Update rider profile details API called
     */
    public void updateProfile() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.updateProfile(imageUr, firstnamegettext, lastnamegettext, ccp.getSelectedCountryCodeWithPlus().replaceAll("\\+", ""), mobilenumber.getText().toString(), emailgettext, sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_UPDATE_PROFILE, this));
    }

    public void onSuccessGetProf(JsonResponse jsonResp) {
        if (jsonResp.getStatusCode().matches("1")) {
            sessionManager.setProfileDetail(jsonResp.getStrResponse().toString());
            RiderProfile riderProfile = gson.fromJson(jsonResp.getStrResponse(), RiderProfile.class);
            sessionManager.setWalletAmount(riderProfile.getWalletAmount());
            loaddata(jsonResp.getStrResponse().toString());
        } else if (jsonResp.getStatusCode().matches("2")) {
            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
        }
    }

    public void onSuccessProf(JsonResponse jsonResp) {
        sessionManager.setProfileDetail(jsonResp.getStrResponse().toString());
        String profile_imageget = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "image_url", String.class);

        imageUr = profile_imageget;
        Picasso.with(getApplicationContext()).load(imageUr)
                .into(profile_image);
    }

    public void onSuccessUpdateProf(JsonResponse jsonResp) {
        sessionManager.setProfileDetail(jsonResp.getStrResponse().toString());
        String profile_imageget = (String) commonMethods.getJsonValue(jsonResp.getStrResponse(), "profile_image", String.class);

        imageUr = profile_imageget;
        Picasso.with(getApplicationContext()).load(imageUr)
                .into(profile_image);
        commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
    }

    /**
     * Check Camera and gallery image data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


            switch (requestCode) {
                case 1:
                    if (resultCode == Activity.RESULT_OK) {
                        onImageCapturedFromCamera(data);
                    }
                    break;
                case Constants.REQUEST_CODE_GALLERY:
                    if (resultCode == Activity.RESULT_OK) {
                    onSelectFromGalleryResult(data);}
                    break;
                    case CommonKeys.ACTIVITY_REQUEST_CODE_START_FACEBOOK_ACCOUNT_KIT: {
                        /*if(resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_NEW_USER){
                            updateRiderPhoneNumber(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY),data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
                        }else if (resultCode == CommonKeys.FACEBOOK_ACCOUNT_KIT_RESULT_OLD_USER){
                            commonMethods.showMessage(this, dialog, data.getStringExtra(FACEBOOK_ACCOUNT_KIT_MESSAGE_KEY));

                        }*/
                        if(resultCode == RESULT_OK){
                            updateRiderPhoneNumber(data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_KEY),data.getStringExtra(FACEBOOK_ACCOUNT_KIT_PHONE_NUMBER_COUNTRY_CODE_KEY));
                        }
                        break;
                    }
                default:
                    break;
            }

    }

    private void updateRiderPhoneNumber(String phoneNumber, String countryCode) {
        if (phoneNumber != null ) {
            mobilenumber.setText(phoneNumber);
            ccp.setCountryForPhoneCode(Integer.parseInt(countryCode));
            savebutton.setEnabled(true);
        }
    }


    /**
     * convert Image bitmap to image path
     */
    public String imageWrite(Bitmap bitmap) {

        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, "slectimage.png");
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageInSD = getString(R.string.img_src);

        return imageInSD;

    }

    /**
     * Load rider profile data
     */
    public void loaddata(String profiledetails) {
        try {
            JSONObject jsonObj = new JSONObject(profiledetails);
            String first_name = jsonObj.getString("first_name");
            String last_name = jsonObj.getString("last_name");
            String mobile_number = jsonObj.getString("mobile_number");
            String email_id = jsonObj.getString("email_id");
            String user_thumb_image = jsonObj.getString("profile_image");
            sessionManager.setCountryCode(jsonObj.getString("country_code"));
            if (sessionManager.getCountryCode() != null && !sessionManager.getCountryCode().equals(""))
                ccp.setCountryForPhoneCode(Integer.parseInt(sessionManager.getCountryCode().replaceAll("\\W", "")));


            sessionManager.setPhoneNumber(jsonObj.getString("mobile_number"));
            input_first.setText(first_name);
            mobilenumber.setText(mobile_number);
            input_last.setText(last_name);
            if (!"".equals(email_id))
                emaitext.setText(email_id);
            imageUr = user_thumb_image;

            Picasso.with(getApplicationContext()).load(imageUr)
                    .into(profile_image);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get Rider profile API called
     */
    public void getRiderDetails() {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.getRiderProfile(sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_GET_RIDER_PROFILE, this));

    }

    /**
     * Validate Email
     */
    private boolean validateEmail() {
        String email = emaitext.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            // emailName.setError(getString(R.string.error_msg_email));
            //requestFocus(emaitext);
            return false;
        } else {
            emailName.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Validate first name
     */
    private boolean validateFirst() {
        if (input_first.getText().toString().trim().isEmpty()) {
            input_layout_first.setError(getString(R.string.error_msg_firstname));
            //requestFocus(input_first);
            return false;
        } else {
            input_layout_first.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Validate last name
     */
    private boolean validateLast() {
        if (input_last.getText().toString().trim().isEmpty()) {
            input_layout_last.setError(getString(R.string.error_msg_lastname));
            //requestFocus(input_last);
            return false;
        } else {
            input_layout_last.setErrorEnabled(false);
        }
        return true;
    }


    /*public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissionStatus(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }*/

    /**
     * Camera permission
     */
    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permission = runTimePermission.onRequestPermissionsResult(permissions, grantResults);
        DebuggableLogV("permission", "permission" + permission);
        DebuggableLogV("permission", "grantResults" + grantResults);
        if (permission != null && !permission.isEmpty()) {
            runTimePermission.setFirstTimePermission(true);
            String[] dsf = new String[permission.size()];
            permission.toArray(dsf);
            checkAllPermission(dsf);
        } else {
            pickProfileImg();
        }
    }
*/
    @Override
    public void onImageCompress(String filePath, RequestBody requestBody) {
        commonMethods.hideProgressDialog();
        if (!TextUtils.isEmpty(filePath) && requestBody != null) {


            commonMethods.showProgressDialog(this, customDialog);
            apiService.uploadImage(requestBody, sessionManager.getAccessToken()).enqueue(new RequestCallback(REQ_UPLOAD_PROFILE_IMG, this));
        }
    }

    /*private void checkAllPermission(String[] permission) {
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(this, permission);
        DebuggableLogV("blockedPermission", "blockedPermission" + blockedPermission);
        if (blockedPermission != null && !blockedPermission.isEmpty()) {
            boolean isBlocked = runTimePermission.isPermissionBlocked(this, blockedPermission.toArray(new String[blockedPermission.size()]));
            if (isBlocked) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        showEnablePermissionDailog(0, getString(R.string.please_enable_permissions));
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this, permission, 300);
            }
        } else {
            // pickProfileImg();
            //checkGpsEnable();

        }
    }*/

    /*private void showEnablePermissionDailog(final int type, String message) {
        if (!customDialog.isVisible()) {
            customDialog = new CustomDialog(message, getString(R.string.ok), new CustomDialog.btnAllowClick() {
                @Override
                public void clicked() {
                    if (type == 0) callPermissionSettings();
                    else
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 101);
                }
            });
            customDialog.show(getSupportFragmentManager(), "");
        }
    }*/

    /*private void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 300);
    }*/

    public void pickProfileImg() {
        View view = getLayoutInflater().inflate(R.layout.camera_dialog_layout, null);
        LinearLayout lltCamera = (LinearLayout) view.findViewById(R.id.llt_camera);
        LinearLayout lltLibrary = (LinearLayout) view.findViewById(R.id.llt_library);

        final Dialog bottomSheetDialog = new Dialog(this, R.style.MaterialDialogSheet);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        if (bottomSheetDialog.getWindow() == null) return;
        bottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.show();

        lltCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                verifyAccessPermission(new String[] {RuntimePermissionDialogFragment.CAMERA_PERMISSION,RuntimePermissionDialogFragment.WRITE_EXTERNAL_STORAGE_PERMISSION},RuntimePermissionDialogFragment.cameraAndGallaryCallBackCode,0);
                bottomSheetDialog.dismiss();

            }
        });

        lltLibrary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                verifyAccessPermission(new String[] {RuntimePermissionDialogFragment.WRITE_EXTERNAL_STORAGE_PERMISSION}, RuntimePermissionDialogFragment.externalStoreageCallbackCode,0);
                bottomSheetDialog.dismiss();
            }
        });
    }

    public void verifyAccessPermission(String [] requestPermissionFor, int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        RuntimePermissionDialogFragment.checkPermissionStatus(this, getSupportFragmentManager(), this,requestPermissionFor,requestCodeForCallbackIdentificationCode,requestCodeForCallbackIdentificationCodeSubDivision);
    }

    public void pickImageFromCamera() {
        /*Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile = commonMethods.cameraFilePath();
        Uri imageUri = FileProvider.getUriForFile(Profile.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);

        try {
            List<ResolveInfo> resolvedIntentActivities = Profile.this.getPackageManager().queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;
                grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            cameraIntent.putExtra("return-data", true);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, 1);*/
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 1);
        //commonMethods.refreshGallery(Profile.this, imageFile);
    }

    private void pickImageFromGallary() {
        imageFile = commonMethods.getDefaultFileName(Profile.this);
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, Constants.REQUEST_CODE_GALLERY);
    }

    @Override
    public void permissionGranted(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        switch (requestCodeForCallbackIdentificationCode) {
            case RuntimePermissionDialogFragment.cameraAndGallaryCallBackCode:
                pickImageFromCamera();
                break;

            case RuntimePermissionDialogFragment.externalStoreageCallbackCode:
                pickImageFromGallary();
                break;

            default:
                break;
        }
    }

    @Override
    public void permissionDenied(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
    }

    private void onImageCapturedFromCamera(Intent data) {
        //startCropImage();
        // bm = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        //profile_image.setImageBitmap(bm);
                    /*String myBase64Image;
                    myBase64Image = encodeToBase64(bm, Bitmap.CompressFormat.JPEG, 100);*/
        //imagepath = imageWrite(bm);
                    /*imageUri = Uri.fromFile(imageFile);
                    imagepath = imageUri.getPath();*/

        Bitmap photo = (Bitmap) data.getExtras().get("data");
        // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
        Uri tempUri = getImageUri(getApplicationContext(), photo);
        String realPath = getRealPathFromURI(tempUri);
        imagepath = getRealPathFromURI(tempUri);
        DebuggableLogI("camera image real path", realPath);
        DebuggableLogI("camera image path", imagepath);
        // CALL THIS METHOD TO GET THE ACTUAL PATH
        //File finalFile = new File(getRealPathFromURI(tempUri));


        if (!TextUtils.isEmpty(imagepath)) {
            // commonMethods.showProgressDialog(this, customDialog);
            new ImageCompressAsyncTask(Profile.this, imagepath, this).execute();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 1000, 1000,true);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }



    /**
     * Get gallery data
     */
    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        bm = null;
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bm = BitmapFactory.decodeFile(picturePath);
            profile_image.setImageBitmap(bm);
            //imagepath = imageWrite(bm);

            imagepath = picturePath;

            if (isInternetAvailable) {
                new ImageCompressAsyncTask(Profile.this, imagepath, this).execute();
            } else {
                commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
            }

        }

    }

    /**
     * Name edit text listener
     */
    private class NameTextWatcher implements TextWatcher {

        private View view;

        private NameTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            DebuggableLogV("beforeTextChanged", "beforeTextChanged");
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //setting save button active when all dates are given
            if (validateFirst() && validateLast() && validateEmail()) {
                savebutton.setEnabled(true);
                savebutton.setBackgroundColor(getResources().getColor(R.color.button_color));
            } else {
                savebutton.setEnabled(false);
                savebutton.setBackgroundColor(getResources().getColor(R.color.button_disable_color));
            }
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_first:
                    validateFirst();
                    break;
                case R.id.input_last:
                    validateLast();
                    break;
                case R.id.emaitext:
                    validateEmail();
                    break;

                case R.id.mobilenumber:
                    // validatePhone();
                    break;
                default:
                    break;

            }
        }
    }
}
