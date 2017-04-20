package com.example.mgalante.mysummerapp.adapter;

import android.view.View;

/**
 * Created by mgalante on 20/04/17.
 */

public interface ClickListenerGallery {

    void clickImageGallery(
            View view,
            int position,
            String nameUser,
            String urlPhotoUser,
            String urlPhotoClick
    );

}
