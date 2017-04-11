package com.example.mgalante.mysummerapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

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

}
