package com.example.mgalante.mysummerapp.common;


import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public interface BasePresenter<T,V> {

    void attach(T context, V view);

    void sendFilefromCameraToFirebase(Context mContext, StorageReference storageReference, final File file, final DatabaseReference databaseReference, final User userModel, @Nullable final ChatModel chatModel, @Nullable final PaymentModel paymentModel);

    void sendFileFromGalleryTofirebase(StorageReference storageReference, Uri file, final DatabaseReference databaseReference, final User userModel, @Nullable final ChatModel chatModel, @Nullable final PaymentModel paymentModel);

}
