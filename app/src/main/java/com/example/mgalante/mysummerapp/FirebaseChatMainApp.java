package com.example.mgalante.mysummerapp;

import android.app.Application;
import android.os.Environment;

import com.example.mgalante.mysummerapp.utils.Util;
import com.orhanobut.logger.Logger;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;


public class FirebaseChatMainApp extends Application {
    private static boolean sIsChatActivityOpen = false;

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        FirebaseChatMainApp.sIsChatActivityOpen = isChatActivityOpen;
    }

    @Override
    public void onCreate() {
        super.onCreate();
       // Fabric.with(this, new Crashlytics());

        FlowManager.init(new FlowConfig.Builder(this).build());

        //File folder = new File(Environment.getExternalStorageDirectory() + "/yourDirectoryName");
        File folder = new File(Environment.getExternalStorageDirectory() + Util.FOLDER_SD_IMAGES2);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }else{
            Logger.d("Folder already created:" +folder.getAbsolutePath());
        }
        if (success) {
            // Do something on success
            Logger.d("Folder successfully created:" +folder.getAbsolutePath());
        } else {
            Logger.d("Folder creation failed: "+folder.getAbsolutePath());
        }
    }
}
