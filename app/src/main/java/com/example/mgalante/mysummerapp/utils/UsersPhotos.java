package com.example.mgalante.mysummerapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Created by mgalante on 5/04/17.
 */

public class UsersPhotos {

    public static void addUserImageToCache(Context context, final String uid, String photoUrl) {
        Glide.with(context).load(photoUrl).asBitmap().into(new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                CacheStore.getInstance().saveCacheFile(uid, resource);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {

            }
        });
    }

}
