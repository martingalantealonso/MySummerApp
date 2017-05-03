package com.example.mgalante.mysummerapp.views.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mgalante.mysummerapp.FirebaseChatMainApp;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.common.BaseActivity;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.entities.users.all.GetUsersContract;
import com.example.mgalante.mysummerapp.entities.users.all.GetUsersPresenter;
import com.example.mgalante.mysummerapp.entities.users.current.GetCurrentUserContract;
import com.example.mgalante.mysummerapp.entities.users.current.GetCurrentUserPresenter;
import com.example.mgalante.mysummerapp.utils.CacheStore;
import com.example.mgalante.mysummerapp.utils.Constants;
import com.example.mgalante.mysummerapp.utils.SharedPrefUtil;
import com.example.mgalante.mysummerapp.utils.Util;
import com.example.mgalante.mysummerapp.views.gallery.FragmentGalleryParent;
import com.example.mgalante.mysummerapp.views.main.Fragment2Chat.FragmentChat;
import com.example.mgalante.mysummerapp.views.main.Fragment3Calculator.FragmentCalculator;
import com.example.mgalante.mysummerapp.views.main.Fragment4Gallery.FragmentGallery;
import com.example.mgalante.mysummerapp.views.settings.SettingsActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mgalante.mysummerapp.utils.UsersPhotos.addUserImageToCache;

public class MainActivity extends BaseActivity implements GetUsersContract.View, GetCurrentUserContract.View {

    private static final int LOUVRE_REQUEST_CODE = 1;
    private GetCurrentUserPresenter mGetCurrentUserPresenter;
    private GetUsersPresenter mGetUsersPresenter;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FragmentMain fragmentMain;
    private FragmentChat fragmentChat;
    private FragmentCalculator fragmentCalculator;
    private FragmentGallery fragmentGallery;
    private FragmentGalleryParent fragmentGalleryParent;

    Fragment fragment;


    @BindView((R.id.drawer_layout))
    DrawerLayout drawerLayout;
    @BindView(R.id.navview)
    NavigationView navView;
    @BindView(R.id.appbar)
    Toolbar appbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragmentMain = new FragmentMain();
        fragmentChat = new FragmentChat();
        fragmentCalculator = new FragmentCalculator();
        fragmentGallery = new FragmentGallery();
        fragmentGalleryParent = new FragmentGalleryParent();

        mGetCurrentUserPresenter = new GetCurrentUserPresenter(this);
        mGetUsersPresenter = new GetUsersPresenter(this);

        //Util.getAllUsersFromFirebase();
        setupWindowAnimations();
        getWindow().setBackgroundDrawable(getDrawable(R.drawable.mainbackground_image));

        mFirebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mFirebaseAuth.getCurrentUser();

        setSupportActionBar(appbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.vd_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            fragment = FragmentMain.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }

        navView.setNavigationItemSelectedListener(
                //region OnNavigationItemSelectedListener
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                        Fragment fragment = null;

                        switch (menuItem.getItemId()) {
                            case R.id.menu_seccion_1:
                                //fragment = new FragmentMain();
                                fragment = fragmentMain;
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_seccion_2:
                                //fragment = new FragmentChat();
                                fragment = fragmentChat;
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_seccion_3:
                                fragment = fragmentCalculator;
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_seccion_4:
                                /*fragment = fragmentGallery;
                                fragmentTransaction = true;*/
                              /*  Louvre.init(MainActivity.this)
                                        .setRequestCode(LOUVRE_REQUEST_CODE)
                                        .setMaxSelection(10)
                                        .open();*/
                                /*Intent intentGallery = new Intent(getApplicationContext(), MediaThumbMainActivity.class);
                                startActivity(intentGallery);*/

                                //fragment = new FragmentMediaThumb();
                                fragment = fragmentGalleryParent;
                                fragmentTransaction = true;
                                break;
                            case R.id.menu_opcion_1:
                                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                                intent.putExtra("uidUser", user.getUid());
                                intent.putExtra("nameUser", user != null ? user.getDisplayName() : "My Profile");
                                intent.putExtra("urlPhotoUser", !String.valueOf(user.getPhotoUrl()).equals("") && !String.valueOf(user.getPhotoUrl()).equals("null") ? String.valueOf(user.getPhotoUrl()) : Util.DEFAULT_NULL_IMAGE);
                                //intent.putExtra("urlPhotoUser", String.valueOf(user.getPhotoUrl()));
                                startActivity(intent);
                                break;
                            case R.id.menu_opcion_2:
                                Log.i("NavigationView", "Pulsada opción 2");
                                singOut();
                                break;
                        }

                        if (fragmentTransaction) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();

                            menuItem.setChecked(true);
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        drawerLayout.closeDrawers();

                        return true;
                    }
                }
                //endregion
        );

        View headerView = navView.getHeaderView(0);
        TextView mUserTxtView = (TextView) headerView.findViewById(R.id.user_name);
        if (user != null) {
            mUserTxtView.setText(user.getDisplayName());
        }
        CircleImageView mUserPhoto = (CircleImageView) headerView.findViewById(R.id.imageViewUserPhoto);
        Bitmap userCachePhoto = null;
        if (user != null) {
            userCachePhoto = CacheStore.getInstance().getCacheFile(user.getUid());
            if (userCachePhoto != null) {
                Log.i(getPackageName(), "userPhoto loaded from cache");
                mUserPhoto.setImageBitmap(userCachePhoto);
            } else {
                Log.i(getPackageName(), "userPhoto loaded from url");
                Glide.with(this).load(user.getPhotoUrl()).into(mUserPhoto);
            }
        }
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Surfing some waves...");
        progressDialog.show();
        getCurrentUser();
        getUsers();
        progressDialog.dismiss();
    }

    private void setupWindowAnimations() {
        Fade fade = new Fade();
        fade.setDuration(1000);
        getWindow().setEnterTransition(fade);
    }

    private void singOut() {
        AuthUI.getInstance().signOut(this);
        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseChatMainApp.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseChatMainApp.setChatActivityOpen(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    private void getCurrentUser() {
        mGetCurrentUserPresenter.getCurrentUser();
        mGetCurrentUserPresenter.getCurrentUserPayments();
    }

    @Override
    public void onGetCurrentUserSuccess(User user) {
        if (user == null) {
            addFireUserToDatabase(getApplicationContext(), mFirebaseAuth.getCurrentUser());
        } else {
            SharedPreferences prefs = getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.preference_user_name), String.valueOf(user.getName()));
            editor.putString(getString(R.string.preference_user_photoUrl), String.valueOf(user.getPhotoUrl()));
            editor.putString(getString(R.string.preference_user_uid), String.valueOf(user.getUid()));
            editor.putString(getString(R.string.preference_user_paymentsSum), String.valueOf(user.getPaymentsSum()));
            editor.apply();
            Log.i("onGetCurrUserSuccess2", user.toString());
            //setUserPhotoProfile(prefs.getString(getString(R.string.preference_user_photoUrl), String.valueOf(user.getPhotoUrl())));
            //addUserImageToCache(this, user.getUid(), user.getPhotoUrl());
        }
    }

    @Override
    public void onGetCurrentUserPaymentsSuccess(List<PaymentModel> payments) {

        Double paymentSum = 0.0;
        for (PaymentModel payment : payments) {
            paymentSum = paymentSum + payment.getAmount();
        }
        SharedPreferences prefs = getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.payments_sum), String.valueOf(paymentSum));
        Log.i("onGetCurrUserPaytsSucs", paymentSum.toString());
    }

    private void getUsers() {
        mGetUsersPresenter.getAllUsers();
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {
        Log.i("TEST", "onGetAllUsersSuccess");
        for (User user : users) {
            if (CacheStore.getInstance().getCacheFile(user.getUid()) == null) {
                addUserImageToCache(this, user.getUid(), user.getPhotoUrl());
            }
        }
    }

    @Override
    public void onGetAllUsersFailure(String message) {

    }

    @Override
    public void onGetAllUsersPayments(Double payments) {

    }

    public void addFireUserToDatabase(Context context, final FirebaseUser firebaseUser) {
        User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), String.valueOf(firebaseUser.getPhotoUrl()),
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
                         /*     if (String.valueOf(firebaseUser.getPhotoUrl()).equals(null)) {
                              Intent intent = new Intent();
                                intent.setType("image*//*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
                          } */
                        } else {
                            // failed to add user
                        }
                    }
                });
    }
}
