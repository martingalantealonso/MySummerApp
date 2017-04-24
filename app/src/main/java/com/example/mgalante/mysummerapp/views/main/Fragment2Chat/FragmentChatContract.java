package com.example.mgalante.mysummerapp.views.main.Fragment2Chat;

import android.content.Context;

import com.example.mgalante.mysummerapp.common.BasePresenter;
import com.example.mgalante.mysummerapp.common.BaseView;

/**
 * Created by mgalante on 3/04/17.
 */

public interface FragmentChatContract {
    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter<Context, View> {
    }

}