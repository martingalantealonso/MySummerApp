package com.example.mgalante.mysummerapp.adapter;

import android.view.View;

/**
 * Created by mgalante on 21/04/17.
 */

public interface ClickListenerPayment {

    void clickListenerPayment(
            View view,
            int position,
            String nameUser,
            String urlPhotoUser,
            String urlPhotoClick
    );
}
