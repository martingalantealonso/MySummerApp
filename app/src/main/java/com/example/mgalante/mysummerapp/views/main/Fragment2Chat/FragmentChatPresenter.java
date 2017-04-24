package com.example.mgalante.mysummerapp.views.main.Fragment2Chat;

import android.content.Context;
import android.support.annotation.Nullable;

import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Created by mgalante on 3/04/17.
 */

public class FragmentChatPresenter implements FragmentChatContract.Presenter {

    private FragmentChatContract.View mView;
    private Context mContext;

    public FragmentChatPresenter() {
    }

    @Override
    public void attach(Context context, FragmentChatContract.View view) {
        this.mContext = context;
        this.mView = view;
    }

    @Override
    public void sendFileToFirebase(Context mContext, StorageReference storageReference, File file, DatabaseReference databaseReference, User userModel, @Nullable ChatModel chatModel, @Nullable PaymentModel paymentModel) {

    }

}
