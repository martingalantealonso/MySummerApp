package com.example.mgalante.mysummerapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.mgalante.mysummerapp.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alessandro Barreto on 23/06/2016.
 */
public class Util {

    public static final String URL_STORAGE_REFERENCE = "gs://pantinapp-c16a0.appspot.com";
    public static final String FOLDER_STORAGE_IMG = "chat_photos";

    public static void initToast(Context c, String message){
        Toast.makeText(c,message, Toast.LENGTH_SHORT).show();
    }

    public  static boolean verificaConexao(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        conectado = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return conectado;
    }

    public static String local(String latitudeFinal, String longitudeFinal){
        return "https://maps.googleapis.com/maps/api/staticmap?center="+latitudeFinal+","+longitudeFinal+"&zoom=18&size=280x280&markers=color:red|"+latitudeFinal+","+longitudeFinal;
    }

    public static List<User> getAllUsersFromFirebase() {
        final List<User> users = new ArrayList<>();

        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                            User user = dataSnapshotChild.getValue(User.class);
                            if (!TextUtils.equals(user.uid,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                users.add(user);
                            }
                        }
                        // All users are retrieved except the one who is currently logged
                        // in device.

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Unable to retrieve the users.
                    }
                });
        return users;
    }

}
