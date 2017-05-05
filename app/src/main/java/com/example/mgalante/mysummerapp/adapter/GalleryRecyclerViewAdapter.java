package com.example.mgalante.mysummerapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.example.mgalante.mysummerapp.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

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
        super(ImageModel.class, R.layout.item_image_v2, GalleryRecyclerViewAdapter.MyGalleryViewHolder.class, ref);
        this.context = context;
        this.userId = uid;
        this.mClickListenerGallery = mClickListenerGallery;
        this.preferences = context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        this.downloadImage = false;
    }

    @Override
    public MyGalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_v2, parent, false);
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

        ImageView ivGalleryPhoto, ivDownloadIcon;
        TextView mTextViewName;


        MyGalleryViewHolder(View itemView) {
            super(itemView);

            ivGalleryPhoto = (ImageView) itemView.findViewById(R.id.ivItemGridImage);
            ivDownloadIcon = (ImageView) itemView.findViewById(R.id.item_image_image_icon);
            mTextViewName = (TextView) itemView.findViewById(R.id.item_image_text_name);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ImageModel file = getItem(position);
            switch (v.getId()) {
                case R.id.ivItemGridImage:
                    mClickListenerGallery.clickImageGallery(v, position, file.getUserModel().getName(), file.getUserModel().getPhotoUrl(), file.getFileModel().getUrl_file());
                    break;
                case R.id.item_image_image_icon:
                    mClickListenerGallery.clickIconDownload(v, position, file);
                    break;
            }
        }

        public void setIvGalleryPhoto(final ImageModel imageModel) {
            if (ivGalleryPhoto == null) return;
            Log.i("TEST setGALLERY", imageModel.getFileModel().getUrl_file());
            String filePath = imageModel.getFileModel().getUrl_file();


            // 1º IF IMAGE WAS SENT BY USER
            if (imageModel.getUserModel().getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                //region sent by user
                final File file = new File(Util.FOLDER_SD_PICTURES_IMAGES_SENT + imageModel.getFileModel().getName_file());
                // 1.1 SEARCH FOR IMAGE IN FILES
                if (file.exists()) {
                    Logger.d("User File exists:" + file.getAbsolutePath());
                    // 1.1.1 IF FOUND -> DISPLAY
                    filePath = file.getAbsolutePath();
                    downloadImage = false;
                    ivDownloadIcon.setVisibility(View.GONE);
                    mTextViewName.setText(file.getName());
                } else {
                    Logger.d("User File does not exists:" + file.getAbsolutePath());
                    // 1.1.2 ELSE -> ? shit
                    if (preferences.getBoolean(context.getString(R.string.preference_store_image_gallery), true)) {
                        downloadImage = true;
                    }
                }

                Glide.with(context).load(filePath).asBitmap().thumbnail(0.1f).override(50, 50) // display the original image reduced to 10% of the size
                        .into(new SimpleTarget<Bitmap>() {

                            @Override
                            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                //CacheStore.getInstance().saveCacheFile(uid, resource);
                                ivGalleryPhoto.setImageBitmap(resource);
                                if (downloadImage) {
                                    new DownloadFileFromURL(Util.FOLDER_SD_PICTURES_IMAGES_SENT
                                            + imageModel.getFileModel().getName_file(), imageModel.getFileModel().getUrl_file()).execute();
                                }
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {

                            }
                        });
                //endregion
            }
            // 2 IF IMAGE WASN'T SENT BY USER
            else {
                //region No sent by user
                // 2.1 SEARCH FOR IMAGE IN "App Folder" FILES
                final File file = new File(Util.FOLDER_SD_PICTURES_IMAGES + imageModel.getFileModel().getName_file());
                if (file.exists()) {
                    // 2.1.1 IF EXIST -> DISPLAY
                    Logger.d("Non User File exists:" + file.getAbsolutePath());
                    filePath = file.getAbsolutePath();
                    downloadImage = false;
                    ivDownloadIcon.setVisibility(View.GONE);
                    mTextViewName.setText(file.getName());
                } else {
                    // 2.1.2 IF NOT   -> ASK FOR DOWNLOAD
                    Logger.d("Non User File does not exists:" + file.getAbsolutePath());
                    if (preferences.getBoolean(context.getString(R.string.preference_store_image_gallery), true)) {
                        downloadImage = true;
                    }
                    ivDownloadIcon.setVisibility(View.VISIBLE);
                    mTextViewName.setText(imageModel.getFileModel().getName_file());
                }

                //Glide.with(context).load(imageModel.getFileModel().getUrl_file()).asBitmap().thumbnail(0.1f) // display the original image reduced to 10% of the size
                Glide.with(context).load(filePath).asBitmap().thumbnail(0.1f).override(50, 50)// display the original image reduced to 10% of the size
                        .into(new SimpleTarget<Bitmap>() {

                            @Override
                            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                //CacheStore.getInstance().saveCacheFile(uid, resource);
                                ivGalleryPhoto.setImageBitmap(resource);

                                if (downloadImage) {
                                    new DownloadFileFromURL(Util.FOLDER_SD_PICTURES_IMAGES + imageModel.getFileModel().getName_file(), imageModel.getFileModel().getUrl_file()).execute();
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

            ivDownloadIcon.setOnClickListener(this);

        }

        private void swapAnimation(@DrawableRes int drawableResId) {

            final Drawable avd = AnimatedVectorDrawableCompat.create(context, drawableResId);
            ivDownloadIcon.setImageDrawable(avd);
            ((Animatable) avd).start();
        }


        class DownloadFileFromURL extends AsyncTask<String, Bitmap, String> {

            String filePath;
            String resource;
            Boolean success;

            public DownloadFileFromURL(String s, String resource) {
                this.filePath = s;
                this.resource = resource;
            }

            /**
             * Before starting background thread
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                System.out.println("Starting download");
                swapAnimation(R.drawable.avd_downloading_begin);
            }

            /**
             * Downloading file in background thread
             */
            @Override
            protected String doInBackground(String... f_url) {
                int count;
                try {
                    System.out.println("Downloading");


                    //region code
               /* URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file

                OutputStream output = new         FileOutputStream(root+"/downloadedfile.jpg");
                byte data[] = new byte[1024];

                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;

                    // writing data to file
                    output.write(data, 0, count);

                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();*/

                    //endregion

                    File file = new File(filePath);
                    try {

                        java.net.URL url = new java.net.URL(resource);
                        HttpURLConnection connection = (HttpURLConnection) url
                                .openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);

                        success = file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }

                return null;
            }


            /**
             * After completing background task
             **/
            @Override
            protected void onPostExecute(String file_url) {
                System.out.println("Downloaded");
                if (success) {
                    swapAnimation(R.drawable.avd_downloading_finish);
                    ivDownloadIcon.animate().alpha(0f).setDuration(5000).start();
                }
            }

        }


    }


}