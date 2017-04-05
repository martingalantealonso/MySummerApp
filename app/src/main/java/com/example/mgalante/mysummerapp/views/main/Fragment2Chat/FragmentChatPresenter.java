package com.example.mgalante.mysummerapp.views.main.Fragment2Chat;

import android.content.Context;

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
}
