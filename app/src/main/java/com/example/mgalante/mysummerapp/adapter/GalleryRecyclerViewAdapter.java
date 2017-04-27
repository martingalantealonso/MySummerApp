package com.example.mgalante.mysummerapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.example.mgalante.mysummerapp.utils.MyVerticalMovingStyle;
import com.example.mgalante.mysummerapp.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gjiazhe.scrollparallaximageview.ScrollParallaxImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by mgalante on 20/04/17.
 */

public class GalleryRecyclerViewAdapter extends FirebaseRecyclerAdapter<ImageModel, GalleryRecyclerViewAdapter.MyGalleryViewHolder> {

    private ClickListenerGallery mClickListenerGallery;
    private Context context;
    private String userId;
    private SharedPreferences preferences;
    private Boolean downloadImage;


    public GalleryRecyclerViewAdapter(Context context, DatabaseReference ref, String uid, ClickListenerGallery mClickListenerGallery) {
        super(ImageModel.class, R.layout.item_image, GalleryRecyclerViewAdapter.MyGalleryViewHolder.class, ref);
        this.context = context;
        this.userId = uid;
        this.mClickListenerGallery = mClickListenerGallery;
        this.preferences = context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        this.downloadImage = false;
    }

    @Override
    public MyGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new MyGalleryViewHolder(view);
    }

    @Override
    protected void populateViewHolder(MyGalleryViewHolder viewHolder, ImageModel model, int position) {
        if (model.getFileModel() != null) {
            Log.i("TEST GALLERY", model.toString());
            viewHolder.setIvGalleryPhoto(model);
        }
    }

    public class MyGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ScrollParallaxImageView ivGalleryPhoto;

        MyGalleryViewHolder(View itemView) {
            super(itemView);

            ivGalleryPhoto = (ScrollParallaxImageView) itemView.findViewById(R.id.ivItemGridImage);
            ivGalleryPhoto.setParallaxStyles(new MyVerticalMovingStyle());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ImageModel file = getItem(position);
            mClickListenerGallery.clickImageGallery(v, position, file.getUserModel().getName(), file.getUserModel().getPhotoUrl(), file.getFileModel().getUrl_file());
        }

        public void setIvGalleryPhoto(final ImageModel imageModel) {
            if (ivGalleryPhoto == null) return;
            Log.i("TEST setGALLERY", imageModel.getFileModel().getUrl_file());
            String filePath = imageModel.getFileModel().getUrl_file();

            // 1º IF IMAGE WAS SENT BY USER
            if (imageModel.getUserModel().getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                //region sent by user
                File file = new File(Environment.getExternalStorageDirectory() + Util.FOLDER_SD_IMAGES + imageModel.getFileModel().getName_file());
                // 1.1 SEARCH FOR IMAGE IN FILES
                if (file.exists()) {
                    Logger.d("User File exists:" + file.getAbsolutePath());
                    // 1.1.1 IF FOUND -> DISPLAY
                    filePath = file.getAbsolutePath();
                } else {
                    Logger.d("User File does not exists:" + file.getAbsolutePath());
                    // 1.1.2 ELSE -> ? shit

                }





                //endregion

            }
            // 2 IF IMAGE WASN'T SENT BY USER
            else {
                //region No sent by user
                // 2.1 SEARCH FOR IMAGE IN "App Folder" FILES
                File file = new File(Environment.getExternalStorageDirectory() + Util.FOLDER_SD_IMAGES + imageModel.getFileModel().getName_file());
                if (file.exists()) {
                    // 2.1.1 IF EXIST -> DISPLAY
                    Logger.d("Non User File exists:" + file.getAbsolutePath());
                    filePath = file.getAbsolutePath();
                } else {
                    // 2.1.2 IF NOT   -> ASK FOR DOWNLOAD
                    Logger.d("Non User File does not exists:" + file.getAbsolutePath());
                    if (preferences.getBoolean(context.getString(R.string.preference_store_image_gallery), true)) {
                        downloadImage = true;
                    }
                }

                //Glide.with(context).load(imageModel.getFileModel().getUrl_file()).asBitmap().thumbnail(0.1f) // display the original image reduced to 10% of the size
                Glide.with(context).load(filePath).asBitmap().thumbnail(0.1f) // display the original image reduced to 10% of the size
                        .into(new SimpleTarget<Bitmap>() {

                            @Override
                            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                //TODO STORE IF REQUIRED
                                //CacheStore.getInstance().saveCacheFile(uid, resource);
                                ivGalleryPhoto.setImageBitmap(resource);

                                if (downloadImage) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            File file = new File(
                                                    Environment.getExternalStorageDirectory() + Util.FOLDER_SD_IMAGES
                                                            + imageModel.getFileModel().getName_file());
                                            try {
                                                file.createNewFile();
                                                FileOutputStream ostream = new FileOutputStream(file);
                                                resource.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                                ostream.close();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {

                            }
                        });
//endregion
            }
            Logger.d("GlideImage loaded from: " + filePath);

            ivGalleryPhoto.setOnClickListener(this);
        }
    }
}