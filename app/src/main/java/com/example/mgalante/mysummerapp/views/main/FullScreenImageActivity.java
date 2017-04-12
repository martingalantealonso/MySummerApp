package com.example.mgalante.mysummerapp.views.main;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.views.TouchImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.egslava.blurredview.BlurredImageView;

public class FullScreenImageActivity extends AppCompatActivity {

    private TouchImageView mImageView;
    private CircleImageView ivUser;
    private TextView tvUser;
    private ProgressDialog progressDialog;
    private BlurredImageView mMainHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        bindViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setValues();
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


    private void bindViews() {
        mMainHolder = (BlurredImageView) findViewById(R.id.full_main_holder);
        progressDialog = new ProgressDialog(this);
        mImageView = (TouchImageView) findViewById(R.id.imageView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivUser = (CircleImageView) toolbar.findViewById(R.id.avatar);
        tvUser = (TextView) toolbar.findViewById(R.id.title);
    }

    private void setValues() {
        final View content = this.findViewById(android.R.id.content).getRootView();
        String nameUser, urlPhotoUser, urlPhotoClick;
        nameUser = getIntent().getStringExtra("nameUser");
        urlPhotoUser = getIntent().getStringExtra("urlPhotoUser");
        urlPhotoClick = getIntent().getStringExtra("urlPhotoClick");
        Log.i("TAG", "imagem recebida " + urlPhotoClick);
        tvUser.setText(nameUser); // Name
        Glide.with(this).load(urlPhotoUser).centerCrop().override(40, 40).into(ivUser);

        Glide.with(this).load(urlPhotoClick).asBitmap().override(640, 640).fitCenter().into(new SimpleTarget<Bitmap>() {

            @Override
            public void onLoadStarted(Drawable placeholder) {
                progressDialog.setMessage("Loading image...");
                progressDialog.show();
            }

            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                progressDialog.dismiss();

                /*Palette.generateAsync(resource, new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        int bgColor = palette.getDominantColor(getResources().getColor(android.R.color.black));

                        mMainHolder.setBackgroundColor(bgColor);
                    }
                });*/
                //Set the blur background

                //Dali.create(getApplicationContext()).load(resource).blurRadius(12).into(mMainHolder);
                mMainHolder.setImageBitmap(resource);
                mImageView.setImageBitmap(resource);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                Toast.makeText(FullScreenImageActivity.this, "Erro, tente novamente", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

}
