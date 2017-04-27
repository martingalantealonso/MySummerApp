package com.example.mgalante.mysummerapp.views.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.utils.Constants;
import com.example.mgalante.mysummerapp.utils.SharedPrefUtil;
import com.example.mgalante.mysummerapp.utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orhanobut.logger.Logger;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 20;
    private static final String TAG = "TEST SETTINGS";
    private boolean mIsAvatarShown = true;
    private static final int IMAGE_GALLERY_REQUEST = 1;

    private FirebaseStorage storage;

    private ProgressDialog progressDialog;

    SharedPreferences preferences;

    @BindView(R.id.materialup_profile_image)
    CircleImageView mProfileImage;
    @BindView(R.id.app_bar)
    AppBarLayout appbarLayout;
    @BindView(R.id.settings_user_name)
    TextView txtvUserName;
    @BindView(R.id.settings_user_subtitle)
    TextView txtUserSubtitle;
    @BindView(R.id.switch_chat_images)
    Switch switchChatImages;
    @BindView(R.id.switch_gallery_images)
    Switch switchGalleryImages;

    private int mMaxScrollSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        storage = FirebaseStorage.getInstance();

        preferences = getApplicationContext().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
            }
        });

        switchGalleryImages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (switchGalleryImages.isChecked()) {
                    editor.putBoolean(getString(R.string.preference_store_image_gallery), true);
                } else {
                    editor.putBoolean(getString(R.string.preference_store_image_gallery), false);
                }
                editor.apply();
                Logger.d("Preferences: store gallery images "+preferences.getBoolean(getString(R.string.preference_store_image_gallery), false));
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        setValues();
    }

    private void setValues() {

        String nameUser, urlPhotoUser, urlPhotoClick;
        nameUser = getIntent().getStringExtra("nameUser");
        urlPhotoUser = getIntent().getStringExtra("urlPhotoUser");
        txtvUserName.setText(nameUser);
        txtUserSubtitle.setText("");

        Glide.with(this).load(urlPhotoUser).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {

            @Override
            public void onLoadStarted(Drawable placeholder) {
                progressDialog.setMessage("Surfing some waves...");
                progressDialog.show();
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                progressDialog.dismiss();
                mProfileImage.setImageBitmap(resource);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                Toast.makeText(SettingsActivity.this, "Error surfing, try again", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.gc();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            mProfileImage.animate()
                    .scaleY(0).scaleX(0)
                    .setDuration(200)
                    .start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            mProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG_USER);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
                    StorageReference imageGalleryRef = storageRef.child(name + "_gallery");
                    UploadTask uploadTask = imageGalleryRef.putFile(selectedImageUri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.i(TAG, "onSuccess sendFileFirebase");
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            User user = new User(getIntent().getExtras().getString("uidUser"), getIntent().getExtras().getString("nameUser"), String.valueOf(downloadUrl),
                                    new SharedPrefUtil(getApplicationContext()).getString(Constants.ARG_FIREBASE_TOKEN));
                            addUserToDatabase(getApplicationContext(), user);
                            // UsersPhotos.addUserImageToCache(getApplicationContext(), user.getUid(), user.getPhotoUrl());
                        }
                    });
                } else {
                    //URI IS NULL
                }
            }
        }
    }

    public void addUserToDatabase(final Context context, final User user) {

        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(user.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // successfully added user

                        } else {
                            // failed to add user
                        }
                    }
                });
    }
}
