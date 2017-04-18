package com.example.mgalante.mysummerapp.entities.users.current;

import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;

import java.util.List;

/**
 * Created by mgalante on 18/04/17.
 */

public class GetCurrentUserPresenter implements GetCurrentUserContract.Presenter, GetCurrentUserContract.OnGetCurrentUserListener {
    private GetCurrentUserContract.View mView;
    private GetCurrentUserIterator mGetCurrentUserIterator;

    public GetCurrentUserPresenter(GetCurrentUserContract.View mView) {
        this.mView = mView;
        this.mGetCurrentUserIterator = new GetCurrentUserIterator(this);
    }

    @Override
    public void getCurrentUser() {
        mGetCurrentUserIterator.getCurrentUserFromFirebase();
    }

    @Override
    public void getCurrentUserPayments() {
        mGetCurrentUserIterator.getCurrentUserPaymentsFromFirebase();
    }

    @Override
    public void onGetCurrentUserSuccess(User user) {
        mView.onGetCurrentUserSuccess(user);
    }

    @Override
    public void onGetCurrentUserPaymentsSuccess(List<PaymentModel> payments) {
        mView.onGetCurrentUserPaymentsSuccess(payments);
    }
}
