package com.example.mgalante.mysummerapp.common;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.FileModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.views.main.Fragment3Calculator.FragmentCalculatorContract;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;


/**
 * Created by mgalante on 24/04/17.
 */

public class BasePresenterImpl implements BasePresenter, FragmentCalculatorContract.OnFileSentToFirebaseListener {

    private BaseView mBaseView;
    private static final String TAG = "BasePresenterImpl";
    private Context mContext;
    private View mView;

    public BasePresenterImpl(BaseView mBaseView) {
        this.mBaseView = mBaseView;
    }

    @Override
    public void attach(Object context, Object view) {
        this.mContext = (Context) context;
        this.mView = (View) view;
    }

    @Override
    public void sendFileToFirebase(Context mContext, StorageReference storageReference, final File file, final DatabaseReference databaseReference, final User userModel, @Nullable final ChatModel chatModel, @Nullable final PaymentModel paymentModel) {
        if (storageReference != null) {
            Uri photoURI = FileProvider.getUriForFile(mContext, "com.example.android.fileprovider", file);
            UploadTask uploadTask = storageReference.putFile(photoURI);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel fileModel = new FileModel("img", downloadUrl.toString(), file.getName(), file.length() + "");
                    if (chatModel != null) {
                        ChatModel chatModelValue = new ChatModel(userModel, "", Calendar.getInstance().getTime().getTime() + "", fileModel);
                        databaseReference.push().setValue(chatModelValue);
                    } else if (paymentModel != null) {
                        paymentModel.setFile(fileModel);
                        databaseReference.push().setValue(paymentModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                onPushValueSuccess();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onPushValueSuccess() {
        mBaseView.onValuePushedSuccess();
    }
}
