package com.example.mgalante.mysummerapp.adapter;

import android.view.View;

/**
 * Created by Alessandro Barreto on 27/06/2016.
 */
public interface ClickListenerChatFirebase {

    /**
     * @param view
     * @param position
     */
    void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick);

    /**
     * @param view
     * @param position
     */
    void clickImageMapChat(View view, int position, String latitude, String longitude);

}
