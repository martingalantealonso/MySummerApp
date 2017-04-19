package com.example.mgalante.mysummerapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.mgalante.mysummerapp.entities.users.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Alessandro Barreto on 23/06/2016.
 */
public class Util {

    public static final String URL_STORAGE_REFERENCE = "gs://pantinapp-c16a0.appspot.com";
    public static final String FOLDER_STORAGE_IMG = "chat_photos";
    public static final String FOLDER_STORAGE_IMG_USER = "user_photos";

    public static void initToast(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

  /*  public static boolean verificaConexao(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        conectado = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return conectado;
    }

    public static String local(String latitudeFinal, String longitudeFinal) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitudeFinal + "," + longitudeFinal + "&zoom=18&size=280x280&markers=color:red|" + latitudeFinal + "," + longitudeFinal;
    }*/

    public static void updateUserToDatabase(Context context, User firebaseUser) {
        User user = new User(firebaseUser.getUid(), firebaseUser.getName(),
                firebaseUser.getPhotoUrl().toString(),
                new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN), firebaseUser.getPaymentsSum());
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // successfully added user
                        } else {
                            // failed to add user
                        }
                    }
                });
    }

}
