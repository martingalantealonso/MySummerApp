package com.example.mgalante.mysummerapp;

import android.app.Application;

import com.example.mgalante.mysummerapp.utils.Util;
import com.orhanobut.logger.Logger;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;

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

        this.initializeLeakDetection();

        //region App Folders creation
        //File folder = new File(Environment.getExternalStorageDirectory() + "/yourDirectoryName");
        File folder = new File(Util.FOLDER_SD_PICTURES_IMAGES);
        File folderSent = new File(Util.FOLDER_SD_PICTURES_IMAGES_SENT);
        boolean success = false;
        boolean successSent = false;
        if (!folder.exists()) {
            success = folder.mkdirs();
        } else {
            Logger.d("Folder already created:" + folder.getAbsolutePath());
        }
        if (!folderSent.exists()) {
            successSent = folderSent.mkdirs();
        } else {
            Logger.d("Folder already created:" + folderSent.getAbsolutePath());
        }
        if (success && successSent) {
            // Do something on success
            Logger.d("Folders successfully created:" + folder.getAbsolutePath() + "\n" + folderSent.getAbsolutePath());
        } else {
            Logger.d("Folder creation failed: " + folder.getAbsolutePath() + "\n" + folderSent.getAbsolutePath());
        }
        //endregion
    }

    private void initializeLeakDetection(){
        if(BuildConfig.DEBUG){
            if(LeakCanary.isInAnalyzerProcess(this)){
                return;
            }
            LeakCanary.install(this);
        }
    }
}
