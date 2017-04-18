package com.example.mgalante.mysummerapp.entities.users.current;

import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;

import java.util.List;

/**
 * Created by mgalante on 18/04/17.
 */

public interface GetCurrentUserContract {

    interface View {
        void onGetCurrentUserSuccess(User user);

        void onGetCurrentUserPaymentsSuccess(List<PaymentModel> payments);

    }

    interface Presenter {

        void getCurrentUser();

        void getCurrentUserPayments();
    }

    interface Interactor {

        void getCurrentUserFromFirebase();

        void getCurrentUserPaymentsFromFirebase();

    }

    interface OnGetCurrentUserListener {
        void onGetCurrentUserSuccess(User user);

        void onGetCurrentUserPaymentsSuccess(List<PaymentModel> payments);
    }

}
