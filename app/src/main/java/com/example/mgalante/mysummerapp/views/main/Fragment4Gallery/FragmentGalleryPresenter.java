package com.example.mgalante.mysummerapp.views.main.Fragment4Gallery;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mgalante.mysummerapp.database.entities.DataBaseFileModel;
import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.utils.Util;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by mgalante on 25/04/17.
 */

public class FragmentGalleryPresenter implements FragmentGalleryContract.Presenter {

    private FragmentGalleryContract.View mView;
    private Context mContext;


    @Override
    public void attach(Context context, FragmentGalleryContract.View view) {
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
    public void updateDatabaseImages() {
        String path = Environment.getExternalStorageDirectory().toString() + Util.FOLDER_SD_IMAGES;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File[] files = directory.listFiles();
        if (directory.listFiles() != null) {
            Log.d("Files", "Size: " + files.length);
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                DataBaseFileModel fileModel = new DataBaseFileModel("img", "", files[i].getName(), "");
                fileModel.save();
            }
        }else{
            Log.d("Files", "Merda");

        }
    }
}
