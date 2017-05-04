package com.example.mgalante.mysummerapp.views.filemanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.database.entities.DataBaseImageSentModel;
import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.FileModel;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
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
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    //region store image in the sent directory

                    Glide.with(mContext).load(file).asBitmap().thumbnail(0.1f) // display the original image reduced to 10% of the size
                            .into(new SimpleTarget<Bitmap>() {

                                @Override
                                public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            File file = new File(
                                                   Util.FOLDER_SD_PICTURES_IMAGES_SENT
                                                            + imageModel.getFileModel().getName_file());
                                            try {
                                                file.createNewFile();
                                                FileOutputStream ostream = new FileOutputStream(file);
                                                resource.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                                ostream.close();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {

                                }
                            });

                    //endregion

                    Logger.i("onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //FileModel fileModel = new FileModel("img", downloadUrl.toString(), name, "");
                    final FileModel fileModel = new FileModel("img", downloadUrl.toString(), file.getLastPathSegment(), "");
                    imageModel.setFileModel(fileModel);
                    imageModel.setTimeStamp(String.valueOf(Calendar.getInstance().getTime().getTime()));
                    final String key = databaseReference.child(databaseReference.getKey()).push().getKey();
                    databaseReference.child(key).setValue(imageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Logger.i("onSuccess push value");
                            DataBaseImageSentModel dataBaseImageSentModel = new DataBaseImageSentModel(key, file.getPath());
                            dataBaseImageSentModel.save();
                            onPushValueSuccess();
                        }
                    });

                }
            });
        }
    }

    @Override
    public String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @Override
    public void onPushValueSuccess() {
        mView.onValuePushedSuccess();
    }
}
