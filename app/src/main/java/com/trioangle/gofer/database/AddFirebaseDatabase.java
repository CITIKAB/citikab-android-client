package com.trioangle.gofer.database;

import android.text.TextUtils;
import com.google.firebase.database.*;
import com.trioangle.gofer.configs.SessionManager;
import com.trioangle.gofer.network.AppController;

import javax.inject.Inject;

import static com.trioangle.gofer.utils.CommonMethods.DebuggableLogE;

public class AddFirebaseDatabase {
    @Inject
    SessionManager sessionManager;

    private DatabaseReference mFirebaseDatabase;
    private ValueEventListener mSearchedDriverReferenceListener;
    private Query query;
    private String TAG = "Android_Debug";
    private IFirebaseReqListener firebaseReqListener;

    public AddFirebaseDatabase() {
        AppController.getAppComponent().inject(this);
    }

    public void createRequestTable(IFirebaseReqListener firebaseReqListener) {
        this.firebaseReqListener = firebaseReqListener;
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference(FirebaseDbKeys.Rider);
        mFirebaseDatabase.child(sessionManager.getUserId()).child(FirebaseDbKeys.TripId).setValue("0");
        query = mFirebaseDatabase.child(sessionManager.getUserId());
        addRequestChangeListener();
    }

    public void removeRequestTable() {
        mFirebaseDatabase.getDatabase().getReference(FirebaseDbKeys.Rider).removeValue();
        query.removeEventListener(mSearchedDriverReferenceListener);
        mFirebaseDatabase.removeEventListener(mSearchedDriverReferenceListener);
        mSearchedDriverReferenceListener = null;
    }

    private void addRequestChangeListener() {
        mSearchedDriverReferenceListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DebuggableLogE(TAG, "Database Updated Successfully");
                if (dataSnapshot.hasChild("trip_id")) {
                    String id = dataSnapshot.child("trip_id").getValue(String.class);
                    System.out.println("get Trip id " + id);
                    if (id != null && !TextUtils.isEmpty(id) && !id.equalsIgnoreCase("0")) {
                        if(!id .equalsIgnoreCase(sessionManager.getTripId())){
                            sessionManager.setTripId(id);
                        firebaseReqListener.RequestListener(id);}
                    }
                } else {
                    query.removeEventListener(this);
                    mFirebaseDatabase.removeEventListener(this);
                    mFirebaseDatabase.onDisconnect();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                DebuggableLogE(TAG, "Failed to read user", error.toException());
            }
        });
    }
}
