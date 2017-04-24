package com.example.mgalante.mysummerapp.common;


import android.content.Context;
import android.support.annotation.Nullable;

import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public interface BasePresenter<T,V> {

    void attach(T context, V view);

    void sendFileToFirebase(Context mContext, StorageReference storageReference, final File file, final DatabaseReference databaseReference, final User userModel, @Nullable final ChatModel chatModel, @Nullable final PaymentModel paymentModel);

}
