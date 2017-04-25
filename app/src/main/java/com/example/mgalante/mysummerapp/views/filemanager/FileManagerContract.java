package com.example.mgalante.mysummerapp.views.filemanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.mgalante.mysummerapp.common.BasePresenter;
import com.example.mgalante.mysummerapp.common.BaseView;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by mgalante on 25/04/17.
 */

public interface FileManagerContract {

    interface View extends BaseView<Presenter> {
        void displayFinishDialog(String message);
    }

    interface Presenter extends BasePresenter<Context, View> {

        ArrayList<Uri> handleSendImage(Intent intent);

        ArrayList<Uri> handleSendMultipleImages(Intent intent);

        void sendGalleryPhotoToFirebase(StorageReference storageReference, Uri file, final DatabaseReference databaseReference,  final ImageModel imageModel);

    }

    interface OnFileSentToFirebaseListener{
        void onPushValueSuccess();
    }
}
