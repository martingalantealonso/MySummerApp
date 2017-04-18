package com.example.mgalante.mysummerapp.views.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.transition.Fade;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.BaseActivity;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.entities.users.all.GetUsersPresenter;
import com.example.mgalante.mysummerapp.utils.CacheStore;
import com.example.mgalante.mysummerapp.utils.Constants;
import com.example.mgalante.mysummerapp.utils.SharedPrefUtil;
import com.example.mgalante.mysummerapp.views.main.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {

    public static User userModel;

    private GetUsersPresenter mGetUsersPresenter;

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;
    private static final int RC_SIGNED = 2;
    public static final String ARG_TYPE = "type";
    public static final String TYPE_CHATS = "type_chats";
    public static final String TYPE_ALL = "type_all";
    private String mUsername;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public FirebaseRemoteConfig mFirebaseRemoteConfig;

    @BindView(R.id.sign_in_button_splash)
    Button mSignInButton;
    @BindView(R.id.splash_img_backgroud)
    ImageView mImageView;
    @BindView(R.id.email)
    EditText mEmailEdt;
    @BindView(R.id.password)
    EditText mPasswordEdt;
    @BindView(R.id.mainTitle)
    TextView mMainTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        setupWindowAnimations();


        //AuthUI.getInstance().signOut(this);

        makeVisibilityGone();


        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mImageView.setAnimation(animFadeIn);

        mUsername = ANONYMOUS;

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            //signed in
                            SharedPreferences prefs = getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("email", user.getEmail());
                            editor.putString("display_name", user.getDisplayName());
                            editor.putString(getString(R.string.firebase_user_id), user.getUid());
                            editor.apply();

                            userModel = new User(user.getDisplayName(), user.getPhotoUrl().toString(), user.getUid());

                          /*  User mUser = new User(user.getUid(), user.getDisplayName(), user.getPhotoUrl().toString(), user.getToken(true).toString());
                            FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("user")
                                    .setValue(mUser);*/

                            addUserToDatabase(getApplicationContext(), user);
                            if (CacheStore.getInstance().getCacheFile(user.getUid()) == null) {
                                addUserImageToCache(user.getUid(), user.getPhotoUrl().toString());
                            }
                            onSignedInIntialize(user.getDisplayName());
                        } else {
                            //signed out
                            mUsername = ANONYMOUS;
                            makeVisibilityVisible();
                        }
                    }
                }, 2000);
            }
        };

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                .build(),
                        RC_SIGN_IN);
            }
        });
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setExitTransition(fade);
    }

    private void makeVisibilityGone() {
        mSignInButton.setVisibility(View.GONE);
        mEmailEdt.setVisibility(View.GONE);
        mPasswordEdt.setVisibility(View.GONE);
    }

    private void makeVisibilityVisible() {
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        mSignInButton.setAnimation(animFadeIn);
        mSignInButton.setVisibility(View.VISIBLE);
        mEmailEdt.setAnimation(animFadeIn);
        //mEmailEdt.setVisibility(View.VISIBLE);
        mPasswordEdt.setAnimation(animFadeIn);
        //mPasswordEdt.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show();
                // Intent intent = new Intent(this, MainActivity.class);
                // startActivityForResult(intent, RC_SIGNED);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signed in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == RC_SIGNED) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void onSignedInIntialize(String displayName) {
        mUsername = displayName;
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, RC_SIGNED);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void addUserToDatabase(Context context, FirebaseUser firebaseUser) {
        User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getPhotoUrl().toString(),
                new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN));

/*        User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getPhotoUrl().toString(),
                new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN), Double.parseDouble(String.valueOf(getSharedPreferences(getString(R.string.payments_sum), 0))));*/

        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.ARG_USERS)
                .child(firebaseUser.getUid())
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

    private void addUserImageToCache(final String uid, String photoUrl) {
        Glide.with(this).load(photoUrl).asBitmap().into(new SimpleTarget<Bitmap>() {

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
