package com.example.mgalante.mysummerapp.views.main.Fragment3Calculator;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.adapter.ClickListenerChatFirebase;
import com.example.mgalante.mysummerapp.adapter.UserListArrayAdapter;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.entities.users.all.GetUsersContract;
import com.example.mgalante.mysummerapp.entities.users.all.GetUsersPresenter;
import com.example.mgalante.mysummerapp.utils.CacheStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mgalante on 31/03/17.
 */

public class FragmentCalculator extends Fragment implements ClickListenerChatFirebase, GetUsersContract.View {

    private GetUsersPresenter mGetUsersPresenter;

    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private DatabaseReference mUsersDatabaseReference;
    FirebaseDatabase mFirebaseDatabase;

    @BindView(R.id.calculator_main_holder)
    LinearLayout mMainHolder;
    @BindView(R.id.calculator_txtv_spent)
    TextView mSpentTextView;
    @BindView(R.id.calculator_user_photo)
    CircleImageView mUserPhoto;
    @BindView(R.id.user_list_detail)
    RecyclerView mRecyclerView;

    public FragmentCalculator() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

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

        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        //mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL); // 2 -> number of columns
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

        initializeUsers();
        return view;
    }

    private void initializeUsers() {

        mGetUsersPresenter.getAllUsers();

/*
        final UserListAdapter userListAdapter = new UserListAdapter(getContext(), mUsersDatabaseReference, this);
        userListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = userListAdapter.getItemCount();
                *//*int lastVisiblePosition = mStaggeredLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mRecyclerView.scrollToPosition(positionStart);
                }*//*
            }
        });

        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        mRecyclerView.setAdapter(userListAdapter);*/
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGetUsersPresenter = new GetUsersPresenter(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(getResources().getString(R.string.USERS));

        mLinearLayoutManager = new LinearLayoutManager(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calculator, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.calculator_menu_seccion_1:
                // previously invisible view


                // get the center for the clipping circle
                /*int cx = (myView.getLeft() + myView.getRight()) / 2;
                int cy = (myView.getTop() + myView.getBottom()) / 2;*/
/*
                int cx = (mPaymentHolder.getRight());
                int cy = (mPaymentHolder.getTop() );

                // get the final radius for the clipping circle
                int finalRadius = Math.max(mPaymentHolder.getWidth()+300, mPaymentHolder.getHeight()+300);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(mPaymentHolder, cx, cy, 0, finalRadius);
                anim.setDuration(500);
                // make the view visible and start the animation
                *//*Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                mPaymentHolder.setBackgroundColor(color);*//*
                mPaymentHolder.setVisibility(View.VISIBLE);
                anim.start();*/

                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void setUserPhoto(Bitmap resource) {
        mUserPhoto.setImageBitmap(resource);
        //Palette palette = Palette.from(resource).generate();
        //mMainHolder.setBackgroundColor(palette.getDarkMutedColor(Color.DKGRAY));
        //getView().setBackgroundColor(palette.getDarkMutedColor(Color.DKGRAY));

    }

    @Override
    public void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {

    }

    @Override
    public void clickImageMapChat(View view, int position, String latitude, String longitude) {

    }

    @Override
    public void clickUserDetail(View view, int position, User user) {
        Toast.makeText(getContext(), user.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {

        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        UserListArrayAdapter adapter = new UserListArrayAdapter(getContext(), this, users);
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    public void onGetAllUsersFailure(String message) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                reveal();
                //ViewUtils.removeGlobalListeners(getView(), this);
                getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);

            }
        });
    }

    void reveal() {
        View view = getView();

        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        Animator animator =
                ViewAnimationUtils.createCircularReveal(view, 0, 0, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(500);
        animator.start();
    }

}
