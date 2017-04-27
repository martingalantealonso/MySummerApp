package com.example.mgalante.mysummerapp.views.main;

import android.content.Context;

import com.example.mgalante.mysummerapp.common.BasePresenter;
import com.example.mgalante.mysummerapp.common.BaseView;

/**
 * Created by mgalante on 30/03/17.
 */

public interface FragmentMainContract {

    interface View extends BaseView<Presenter> {
        void updateView(String futureDay, String remainingHours, String remainingMinutes, String remainingSeconds);
    }

    interface Presenter extends BasePresenter<Context, View> {
        void getCountdownText(String futureDate);
    }

}
