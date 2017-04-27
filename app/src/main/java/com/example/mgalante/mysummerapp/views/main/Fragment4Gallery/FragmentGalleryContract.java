package com.example.mgalante.mysummerapp.views.main.Fragment4Gallery;

import android.content.Context;

import com.example.mgalante.mysummerapp.common.BasePresenter;
import com.example.mgalante.mysummerapp.common.BaseView;

/**
 * Created by mgalante on 25/04/17.
 */

public interface FragmentGalleryContract {

    interface View extends BaseView<Presenter> {
    }


    interface Presenter extends BasePresenter<Context, View> {

        void updateDatabaseImages();
    }
}
