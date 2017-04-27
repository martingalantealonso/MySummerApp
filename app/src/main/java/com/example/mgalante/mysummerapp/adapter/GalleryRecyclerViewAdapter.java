package com.example.mgalante.mysummerapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.database.entities.DataBaseFileModel;
import com.example.mgalante.mysummerapp.entities.FileModel;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.example.mgalante.mysummerapp.utils.MyVerticalMovingStyle;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gjiazhe.scrollparallaximageview.ScrollParallaxImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by mgalante on 20/04/17.
 */

public class GalleryRecyclerViewAdapter extends FirebaseRecyclerAdapter<ImageModel, GalleryRecyclerViewAdapter.MyGalleryViewHolder> {

    private ClickListenerGallery mClickListenerGallery;
    private Context context;
    private String userId;


    public GalleryRecyclerViewAdapter(Context context, DatabaseReference ref, String uid, ClickListenerGallery mClickListenerGallery) {
        super(ImageModel.class, R.layout.item_image, GalleryRecyclerViewAdapter.MyGalleryViewHolder.class, ref);
        this.context = context;
        this.userId = uid;
        this.mClickListenerGallery = mClickListenerGallery;
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

        public void setIvGalleryPhoto(ImageModel imageModel) {
            if (ivGalleryPhoto == null) return;
            Log.i("TEST setGALLERY", imageModel.getFileModel().getUrl_file());

            // 1º IF IMAGE WAS SENT BY USER
            if (imageModel.getUserModel().getUid() == FirebaseAuth.getInstance().getCurrentUser().getUid()) {


                // 1.1 SEARCH FOR IMAGE IN FILES

                // 1.1.1 IF FOUND -> DISPLAY
                // 1.1.2 ELSE -> ?

            }
            // 2 IF IMAGES WASN'T SENT BY USER
            else {

                // 2.1 SEARCH FOR IMAGE IN FILES

                // 2.1.1 IF EXIST -> DISPLAY

                // 2.1.2 IF NOT   -> ASK FOR DOWNLOAD

            }


            Glide.with(context).load(imageModel.getFileModel().getUrl_file()).asBitmap().thumbnail(0.1f) // display the original image reduced to 10% of the size
                    .into(new SimpleTarget<Bitmap>() {

                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            //TODO STORE IF REQUIRED
                            //CacheStore.getInstance().saveCacheFile(uid, resource);
                            ivGalleryPhoto.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {

                        }
                    });

            ivGalleryPhoto.setOnClickListener(this);

            FileModel fileModel = imageModel.getFileModel();
            String id = imageModel.getTimeStamp();
            if (id != null) {
                //DataBaseFileModel(int id, String type, String url_file, String name_file, String size_file)
                DataBaseFileModel dataBaseFileModel = new DataBaseFileModel(Integer.parseInt(id.substring(id.length() - 9)), fileModel.getType(), fileModel.getUrl_file(), fileModel.getName_file(), fileModel.getSize_file());
                dataBaseFileModel.toString();
                dataBaseFileModel.save();
            }
        }
    }
}