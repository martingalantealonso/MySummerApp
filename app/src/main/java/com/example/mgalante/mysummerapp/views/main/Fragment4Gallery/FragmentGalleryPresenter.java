package com.example.mgalante.mysummerapp.views.main.Fragment4Gallery;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.database.entities.DataBaseFileModel;
import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.FileModel;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        FirebaseDatabase.getInstance().getReference().child(mContext.getResources().getString(R.string.reference_gallery_database)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> dataSnapshots = dataSnapshot.getChildren().iterator();
                List<ImageModel> images = new ArrayList<>();
                while (dataSnapshots.hasNext()) {
                    DataSnapshot dataSnapshotChild = dataSnapshots.next();
                    ImageModel imageModel = dataSnapshotChild.getValue(ImageModel.class);
                    FileModel fileModel = imageModel.getFileModel();
                    String id = imageModel.getTimeStamp();
                    if (id != null) {
                        //DataBaseFileModel(int id, String type, String url_file, String name_file, String size_file)
                        DataBaseFileModel dataBaseFileModel = new DataBaseFileModel(Integer.parseInt(id.substring(id.length() - 9)), fileModel.getType(), fileModel.getUrl_file(), fileModel.getName_file(), fileModel.getSize_file());
                        dataBaseFileModel.setKey(dataSnapshotChild.getKey());
                        dataBaseFileModel.save();
                        Logger.i("Stored reference for: " + dataBaseFileModel.toString());

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
       /*
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

        }*/
    }
}
