package com.example.mgalante.mysummerapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
        if (model.getFile() != null) {
            Log.i("TEST GALLERY", model.toString());
            viewHolder.setIvGalleryPhoto(model.getFile().getUrl_file());
        }
    }


    public class MyGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivGalleryPhoto;

        MyGalleryViewHolder(View itemView) {
            super(itemView);

            ivGalleryPhoto = (ImageView) itemView.findViewById(R.id.ivItemGridImage);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ImageModel file = getItem(position);
            mClickListenerGallery.clickImageGallery(v, position, file.getUserModel().getName(), file.getUserModel().getPhotoUrl(), file.getFile().getUrl_file());
        }

        public void setIvGalleryPhoto(String url) {
            if (ivGalleryPhoto == null) return;
            Log.i("TEST setGALLERY", url);

            Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {

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

        }
    }

}