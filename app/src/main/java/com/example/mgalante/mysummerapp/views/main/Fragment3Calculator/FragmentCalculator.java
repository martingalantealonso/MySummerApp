package com.example.mgalante.mysummerapp.views.main.Fragment3Calculator;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.utils.CacheStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mgalante on 31/03/17.
 */

public class FragmentCalculator extends Fragment {

    @BindView(R.id.calculator_main_holder)
    LinearLayout mMainHolder;
    @BindView(R.id.calculator_txtv_spent)
    TextView mSpentTextView;
    @BindView(R.id.calculator_user_photo)
    CircleImageView mUserPhoto;

    public FragmentCalculator() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        ButterKnife.bind(this, view);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        Bitmap userCachePhoto = CacheStore.getInstance().getCacheFile(user.getUid());
        if (userCachePhoto != null) {
            Log.i("Calculator", "userPhoto loaded from cache");
            setUserPhoto(userCachePhoto);
        } else {
            Log.i("Calculator", "userPhoto loaded from url");
            //Glide.with(this).load(user.getPhotoUrl()).into(mUserPhoto);
            Glide.with(this).load(user.getPhotoUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {

                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    setUserPhoto(resource);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {

                }
            });
        }


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setUserPhoto(Bitmap resource) {
        mUserPhoto.setImageBitmap(resource);
        Palette palette = Palette.from(resource).generate();
        //mMainHolder.setBackgroundColor(palette.getDarkMutedColor(Color.DKGRAY));
        //getView().setBackgroundColor(palette.getDarkMutedColor(Color.DKGRAY));

    }
}
