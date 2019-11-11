package com.trioangle.gofer.views.emergency;


/**
 * @package com.trioangle.gofer
 * @subpackage emergency
 * @category Emergency contact
 * @author Trioangle Product Team
 * @version 1.7
 */


import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.trioangle.gofer.R;
import com.trioangle.gofer.adapters.EmergencyContactAdapter;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.datamodels.JsonResponse;
import com.trioangle.gofer.datamodels.main.EmergencyContactModel;
import com.trioangle.gofer.datamodels.main.EmergencyContactResult;
import com.trioangle.gofer.interfaces.ApiService;
import com.trioangle.gofer.interfaces.ServiceListener;
import com.trioangle.gofer.network.AppController;
import com.trioangle.gofer.utils.CommonMethods;
import com.trioangle.gofer.utils.RequestCallback;
import com.trioangle.gofer.utils.RuntimePermissionDialogFragment;
import com.trioangle.gofer.views.customize.CustomDialog;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogV;
import static com.trioangle.gofer.utils.CommonMethods.showUserMessage;


/* ************************************************************************
                To add Emergency contacts that are to be send during
                while sos click
*************************************************************************** */

public class EmergencyContact extends AppCompatActivity implements ServiceListener,RuntimePermissionDialogFragment.RuntimePermissionRequestedCallback {

    public static final int PICKCONTACT = 1;
    public android.support.v7.app.AlertDialog dialog;
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
    public @InjectView(R.id.alertmsg)
    TextView alertmsg;
    public @InjectView(R.id.fivecontacts)
    TextView fivecontacts;
    public @InjectView(R.id.remove)
    TextView remove;
    public @InjectView(R.id.imagelayout)
    RelativeLayout imagelayout;
    public @InjectView(R.id.addcontactlayout)
    LinearLayout addcontactlayout;
    public @InjectView(R.id.addcontact)
    Button addcontact;
    public String number = "";
    public String name = "";
    public LinearLayoutManager linearLayoutManager;
    public EmergencyContactResult emergencyContactResult;
    public ArrayList<EmergencyContactModel> emergencyContactModels = new ArrayList<>();
    public RecyclerView contactviews;
    public EmergencyContactAdapter adapter;
    protected boolean isInternetAvailable;

    @OnClick(R.id.back)
    public void back() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        ButterKnife.inject(this);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        contactviews = (RecyclerView) findViewById(R.id.contactlist);
        contactviews.setHasFixedSize(true);
        contactviews.setNestedScrollingEnabled(true);
        linearLayoutManager = new LinearLayoutManager(this);
        contactviews.setLayoutManager(linearLayoutManager);

        isInternetAvailable = commonMethods.isOnline(getApplicationContext());
        updateEmergency();

        //Action for add contact button
        addcontact.setOnClickListener(v -> verifyAccessPermission(new String[] {RuntimePermissionDialogFragment.CONTACT_PERMISSION}, RuntimePermissionDialogFragment.contactCallbackCode,0));
    }

    public void verifyAccessPermission(String[] requestPermissionFor, int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        RuntimePermissionDialogFragment.checkPermissionStatus(this, getSupportFragmentManager(), this,requestPermissionFor, requestCodeForCallbackIdentificationCode,requestCodeForCallbackIdentificationCodeSubDivision);
    }

    public void startReadContactIntent() {
        Intent intent1 = new Intent(Intent.ACTION_PICK);
        intent1.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent1, PICKCONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        ContentResolver resolver = getContentResolver();
        Cursor contacts = null;
        try {
            contacts = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (contacts.moveToFirst()) {


                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();
                int numberColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                number = cursor.getString(numberColumn);
                name = cursor.getString(nameColumn);
                cursor.close();
                Toast.makeText(getApplicationContext(),number+" "+name,Toast.LENGTH_LONG).show();


                contacts.close();

            } else {
                showUserMessage(this,getString(R.string.no_contacts_selected));
            }
        }catch (NullPointerException e ){
          number="";
        } catch (Exception e) {
            e.printStackTrace();
        }
        number = number.replaceAll("-", "");
        if (number.startsWith("+")) {
            number = number.replaceAll("\\D", "");
            number = number.replaceAll(" ", "");


        } else {
            number = sessionManager.getCountryCode() + number;

        }


        if (!"".equals(number)) {
            if ("*".contains(number) || "#".contains(number)) {
                Toast.makeText(getApplicationContext(), "Invalid Number", Toast.LENGTH_LONG).show();
            } else {
                if (isInternetAvailable) {
                    number = number.replaceAll(" ", "");

                    if(resultCode!=0){
                        emergencyDetails(number, name, "update", "");
                    }

                    number = "";
                    name = "";
                } else {
                    commonMethods.showMessage(this, dialog, getString(R.string.no_connection));

                }
            }
        }
    }

    /**
     * View Emergency Contacts details
     */
    public void updateEmergency() {
        if (isInternetAvailable) {
            number = number.replaceAll(" ", "");
            emergencyDetails(number, name, "view", "");
        } else {
            commonMethods.showMessage(this, dialog, getString(R.string.no_connection));
        }
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        if (jsonResp.isSuccess()) {
            commonMethods.hideProgressDialog();
            onSuccessSOS(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.hideProgressDialog();

            commonMethods.showMessage(this, dialog, jsonResp.getStatusMsg());
           /* if (jsonResp.getStatusMsg().equals("Mobile Number Exist")) {
                //Toast.makeText(getApplicationContext(),statusmessage,Toast.LENGTH_SHORT).show();
            } else {
                imagelayout.setVisibility(View.VISIBLE);
                contactviews.setVisibility(View.GONE);
                alertmsg.setVisibility(View.VISIBLE);
                addcontactlayout.setVisibility(View.VISIBLE);
            }*/
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        DebuggableLogV("SOS", "onFailure");
    }

    /**
     * To add , update and delete emergency contacts using type
     */
    public void emergencyDetails(String number, String name, String action, String id) {
        commonMethods.showProgressDialog(this, customDialog);
        apiService.sos(sessionManager.getAccessToken(), number, action, name, sessionManager.getCountryCode(), id).enqueue(new RequestCallback(this));

    }

    public void onSuccessSOS(JsonResponse jsonResp) {

        emergencyContactResult = gson.fromJson(jsonResp.getStrResponse(), EmergencyContactResult.class);
        int count = emergencyContactResult.getContactCount();



        if (count == 0) {
            imagelayout.setVisibility(View.VISIBLE);
            alertmsg.setVisibility(View.VISIBLE);
            fivecontacts.setVisibility(View.VISIBLE);
            addcontact.setVisibility(View.VISIBLE);
            remove.setVisibility(View.INVISIBLE);
            contactviews.setVisibility(View.GONE);

        } else if (count == 5) {
            imagelayout.setVisibility(View.GONE);
            alertmsg.setVisibility(View.GONE);
            fivecontacts.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
            addcontact.setVisibility(View.GONE);
            contactviews.setVisibility(View.VISIBLE);
        } else {
            imagelayout.setVisibility(View.GONE);
            alertmsg.setVisibility(View.GONE);
            fivecontacts.setVisibility(View.VISIBLE);
            addcontact.setVisibility(View.VISIBLE);
            contactviews.setVisibility(View.VISIBLE);
            remove.setVisibility(View.INVISIBLE);
        }
        emergencyContactModels.clear();
        ArrayList<EmergencyContactModel> contactArray = emergencyContactResult.getContactDetails();
        emergencyContactModels.addAll(contactArray);

        adapter = new EmergencyContactAdapter(emergencyContactModels, EmergencyContact.this);
        contactviews.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener((number, name, id, positionz) -> emergencyDetails(number, name, "delete", id));
    }

    @Override
    public void permissionGranted(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {
        if (requestCodeForCallbackIdentificationCode == RuntimePermissionDialogFragment.contactCallbackCode){
            startReadContactIntent();
        }
    }

    @Override
    public void permissionDenied(int requestCodeForCallbackIdentificationCode, int requestCodeForCallbackIdentificationCodeSubDivision) {

    }
}
