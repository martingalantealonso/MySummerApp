package com.example.mgalante.mysummerapp.views.filemanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;

import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.FileModel;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mgalante on 25/04/17.
 */

public class FileManagerPresenter implements FileManagerContract.Presenter, FileManagerContract.OnFileSentToFirebaseListener {

    private static final String TAG = "FileManagerPresenter";
    private FileManagerContract.View mView;
    private Context mContext;

    public FileManagerPresenter() {
    }

    @Override
    public void attach(Context context, FileManagerContract.View view) {
        this.mContext = context;
        this.mView = view;
    }

    @Override
    public void sendFilefromCameraToFirebase(Context mContext, StorageReference storageReference, File file, DatabaseReference databaseReference, User userModel, @Nullable ChatModel chatModel, @Nullable PaymentModel paymentModel) {

    }

    @Override
    public void sendFileFromGalleryTofirebase(StorageReference storageReference, Uri file, DatabaseReference databaseReference, User userModel, @Nullable ChatModel chatModel, @Nullable PaymentModel paymentModel) {

    }

    @Override
    public ArrayList<Uri> handleSendImage(Intent intent) {

        ArrayList<Uri> imageUris = new ArrayList<>();
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        //intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
            imageUris.add(imageUri);
        }

        return imageUris;
    }

    @Override
    public ArrayList<Uri> handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = new ArrayList<>();
        if (imageUris != null) {
            imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            //TODO get remote config
            if (imageUris.size() > 10) {
                mView.displayFinishDialog("Please, select no more than 10 images");
            }
        }
        return imageUris;
    }

    @Override
    public void sendGalleryPhotoToFirebase(StorageReference storageReference, final Uri file, final DatabaseReference databaseReference, final ImageModel imageModel) {
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(file.getLastPathSegment() + name + "_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
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
                    //FileModel fileModel = new FileModel("img", downloadUrl.toString(), name, "");
                    FileModel fileModel = new FileModel("img", downloadUrl.toString(), file.getLastPathSegment(), "");
                    imageModel.setFileModel(fileModel);
                    imageModel.setTimeStamp(String.valueOf(Calendar.getInstance().getTime().getTime()));
                    databaseReference.push().setValue(imageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            onPushValueSuccess();
                        }
                    });

                }
            });
        }
    }


    @Override
    public void onPushValueSuccess() {
        mView.onValuePushedSuccess();
    }
}
