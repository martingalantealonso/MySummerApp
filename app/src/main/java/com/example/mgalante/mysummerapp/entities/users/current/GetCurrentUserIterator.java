package com.example.mgalante.mysummerapp.entities.users.current;

import android.text.TextUtils;
import android.util.Log;

import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mgalante on 18/04/17.
 */

public class GetCurrentUserIterator implements GetCurrentUserContract.Interactor {
    private static final String TAG = "GetCurrentUserInteractor";

    private GetCurrentUserContract.OnGetCurrentUserListener mOnGetCurrentUserListener;

    public GetCurrentUserIterator(GetCurrentUserContract.OnGetCurrentUserListener mOnGetCurrentUserListener) {
        this.mOnGetCurrentUserListener = mOnGetCurrentUserListener;
    }

    @Override
    public void getCurrentUserFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                User cUser = null;
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    User user = dataSnapshotChild.getValue(User.class);
                    if (TextUtils.equals(user.uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Log.i("Test", user.toString());
                        cUser = user;
                    }
                }
                mOnGetCurrentUserListener.onGetCurrentUserSuccess(cUser);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void getCurrentUserPaymentsFromFirebase() {
        FirebaseDatabase.getInstance().getReference().child(Constants.ARG_PAYMENTS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<PaymentModel> payments = new ArrayList<>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    PaymentModel paymentModel = dataSnapshotChild.getValue(PaymentModel.class);
                    if (TextUtils.equals(paymentModel.getUserModel().uid, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Log.i("Test", paymentModel.toString());
                        payments.add(paymentModel);
                    }
                }
                Log.i("TEST", "Number of payments: " + String.valueOf(payments.size()));
                mOnGetCurrentUserListener.onGetCurrentUserPaymentsSuccess(payments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
