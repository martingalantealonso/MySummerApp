package com.example.mgalante.mysummerapp.entities.users.all;


import com.example.mgalante.mysummerapp.entities.users.User;

import java.util.List;

/**
 * Author: Kartik Sharma
 * Created on: 8/28/2016 , 11:06 AM
 * Project: FirebaseChat
 */

public interface GetUsersContract {
    interface View {
        void onGetAllUsersSuccess(List<User> users);

        void onGetAllUsersFailure(String message);

        void onGetAllUsersPayments(Double payments);

    }

    interface Presenter {
        void getAllUsers();

        void getAllUsersPayments();
    }

    interface Interactor {
        void getAllUsersFromFirebase();

        void getAllUsersPaymentsFromFirebase();
    }

    interface OnGetAllUsersListener {
        void onGetAllUsersSuccess(List<User> users);

        void onGetAllUsersFailure(String message);

        void onGetAllUsersPayments(Double payments);
    }

}
