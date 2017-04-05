package com.example.mgalante.mysummerapp.views.main.Fragment2Chat;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mgalante.mysummerapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mgalante on 28/03/17.
 */

public class MessageAdapter extends ArrayAdapter<FriendlyMessage> {

    private static final String CHAT_DIRECTORY = "PantinClassic/PantinChat/";
    private Context mContext;
    private SharedPreferences settings;
    private FirebaseStorage storage;
    private FirebaseAuth mFirebaseAuth;
    private boolean sdDisponible = false;
    private boolean sdAccesoEscritura = false;

    private LruCache<String, Bitmap> mMemoryCache;

    public MessageAdapter(Context context, int resource, List<FriendlyMessage> objects) {
        super(context, resource, objects);
        mContext = context;
        // Create a storage reference from our app
        storage = FirebaseStorage.getInstance();
        settings = context.getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        checkSdState();
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.chat_message, parent, false);
        }

        LinearLayout messageHolder = (LinearLayout) convertView.findViewById(R.id.main_message_holder);
        final ImageView photoImageView = (ImageView) convertView.findViewById(R.id.photoImageView);
        ImageView userPhoto = (ImageView) convertView.findViewById(R.id.chat_user_img);
        ImageView nonUserPhoto = (ImageView) convertView.findViewById(R.id.chat_non_user_img);
        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView authorTextView = (TextView) convertView.findViewById(R.id.nameTextView);

        final FriendlyMessage message = getItem(position);

        //region merda
       /* FirebaseUser user = mFirebaseAuth.getCurrentUser();
        final String imageKey = user.getUid();


        if (message.getName() != null) {
            final Bitmap bitmap = getBitmapFromMemCache(imageKey);
            if (message.getName().equals(user.getDisplayName())) {
                //If the message was sent by the current user
                if (bitmap != null) {
                    userPhoto.setVisibility(View.VISIBLE);
                    userPhoto.setImageBitmap(bitmap);
                    nonUserPhoto.setVisibility(View.GONE);
                }
            }else{
                if (bitmap != null) {
                    nonUserPhoto.setVisibility(View.VISIBLE);
                    nonUserPhoto.setImageBitmap(bitmap);
                    userPhoto.setVisibility(View.GONE);
                }
            }
        }*/
        //endregion

        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            messageTextView.setVisibility(View.GONE);
            photoImageView.setVisibility(View.VISIBLE);

            // 1º get the reference
            StorageReference httpsReference = storage.getReferenceFromUrl(message.getPhotoUrl());
            // 2º get the metada
            httpsReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {

                    // region 3º We check if the image exist in files
                    try {

                        File storagePath = new File(Environment.getExternalStorageDirectory(), CHAT_DIRECTORY);
                        File file = new File(storagePath.getAbsolutePath(), storageMetadata.getName() + ".jpg");

                        if (file.exists()) {
                            //Get the file and display it in the view
                            Log.i("IsImage", "file.exists()");

                            Uri imageUri = Uri.fromFile(file);
                            Glide.with(photoImageView.getContext())
                                    .load(imageUri)
                                    .into(photoImageView);
                        } else {
                            Log.i("IsImage", "file.NOTexists()");

                            Glide.with(photoImageView.getContext())
                                    .load(message.getPhotoUrl())
                                    .into(photoImageView);

                            //4º IF not exist, check for storage option
                            if (settings.getBoolean("store_chat_images", false)) {
                                //Storage the image and displayIt
                                Log.i("IsImage", "file.storage()");

                                if (!storagePath.exists()) {
                                    storagePath.mkdirs();
                                }

                                DownloadManager mgr = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

                                Uri downloadUri = Uri.parse(storageMetadata.getDownloadUrl().toString());
                                DownloadManager.Request request = new DownloadManager.Request(
                                        downloadUri);

                                request.setAllowedNetworkTypes(
                                        DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                        .setAllowedOverRoaming(false).setTitle("Downloading Pantin Images")
                                        .setDescription("Something useful. No, really.")
                                        .setDestinationInExternalPublicDir(CHAT_DIRECTORY, storageMetadata.getName() + ".jpg");

                                mgr.enqueue(request);
                            }
                        }

                    } catch (Exception ex) {
                        Log.e("Ficheros", "Error al escribir fichero a tarjeta SD");
                        Log.e("Ficheros", ex.getMessage());
                    }
                    //endregion

                }
            });
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.GONE);
            messageTextView.setText(message.getText());
        }

        //region LayoutRules
        authorTextView.setText(String.format("%s:", message.getName()));

        String author = message.getName();
        SharedPreferences userDetails = mContext.getSharedPreferences("appPreferences", MODE_PRIVATE);
        String Uname = userDetails.getString("name", null);

        if (author != null && Uname != null && author.equals(Uname)) {
            // If the message was sent by this user, design it differently
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            //authorTextView.setTextColor(convertView.getResources().getColor(R.color.lblFromName));
            messageHolder.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            params.removeRule(RelativeLayout.ALIGN_PARENT_END);

            messageHolder.setLayoutParams(params);
        }
        //endregion

        return convertView;
    }

    private void checkSdState() {
        //Comprobamos el estado de la memoria externa (tarjeta SD)
        String estado = Environment.getExternalStorageState();

        if (estado.equals(Environment.MEDIA_MOUNTED)) {
            sdDisponible = true;
            sdAccesoEscritura = true;
        } else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            sdDisponible = true;
            sdAccesoEscritura = false;
        } else {
            sdDisponible = false;
            sdAccesoEscritura = false;
        }

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

}
