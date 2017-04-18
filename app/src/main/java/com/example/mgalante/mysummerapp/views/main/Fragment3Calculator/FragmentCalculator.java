package com.example.mgalante.mysummerapp.views.main.Fragment3Calculator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.adapter.ClickListenerChatFirebase;
import com.example.mgalante.mysummerapp.adapter.UserListArrayAdapter;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.entities.users.all.GetUsersContract;
import com.example.mgalante.mysummerapp.entities.users.all.GetUsersPresenter;
import com.example.mgalante.mysummerapp.utils.CacheStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mgalante on 31/03/17.
 */

public class FragmentCalculator extends Fragment implements ClickListenerChatFirebase, GetUsersContract.View {

    private boolean isEditTextVisible;
    private Animatable mAnimatable;

    public static User userModel;
    private GetUsersPresenter mGetUsersPresenter;

    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mPaymentsDatabaseReference;
    FirebaseDatabase mFirebaseDatabase;

    @BindView(R.id.calculator_main_holder)
    LinearLayout mMainHolder;
    @BindView(R.id.calculator_txtv_spent)
    TextView mSpentTextView;
    @BindView(R.id.calculator_user_photo)
    CircleImageView mUserPhoto;
    @BindView(R.id.user_list_detail)
    RecyclerView mRecyclerView;

    @BindView(R.id.llEditTextHolder)
    LinearLayout llTextHolder;
    @BindView(R.id.input_payment_amount)
    TextInputLayout mtilPAmount;
    @BindView(R.id.input_payment_description)
    TextInputLayout mtilPDescription;
    @BindView(R.id.edtxt_payment_amount)
    EditText mPaymentAmount;
    @BindView(R.id.edtxt_payment_description)
    EditText mPaymentDescription;
    @BindView(R.id.edtxt_file_name)
    EditText mPaymentFileName;
    @BindView(R.id.btn_camera)
    Button mCameraButton;
    @BindView(R.id.btn_gallery)
    Button mGalleryButton;
    @BindView(R.id.btn_accept_payment)
    FloatingActionButton mFloatingActionButton;

    public FragmentCalculator() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        ButterKnife.bind(this, view);

        llTextHolder.setVisibility(View.INVISIBLE);
        isEditTextVisible = false;

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

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayment();
            }
        });

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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userModel = new User(user.getDisplayName(), user.getPhotoUrl().toString(), user.getUid());
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(getResources().getString(R.string.USERS));
        mPaymentsDatabaseReference = mFirebaseDatabase.getReference().child(getResources().getString(R.string.reference_payments));

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

                int cx = llTextHolder.getRight();
                int cy = llTextHolder.getTop();
                //From center ->
                //int cx = (llTextHolder.getLeft() + llTextHolder.getRight()) / 2;
                //int cy = (llTextHolder.getTop() + llTextHolder.getBottom()) / 2;
                if (!isEditTextVisible) {
                    isEditTextVisible = true;
                    int finalRadius = Math.max(llTextHolder.getWidth(), llTextHolder.getHeight() + llTextHolder.getWidth());
                    Animator anim = ViewAnimationUtils.createCircularReveal(llTextHolder, cx, cy, 0, finalRadius);
                    anim.setDuration(800);
                    llTextHolder.setVisibility(View.VISIBLE);
                    anim.start();

                   /* mFloatingButton.setImageResource(R.drawable.icn_morp);
                    mAnimatable = (Animatable) mFloatingButton.getDrawable();
                    mAnimatable.start();*/
                } else {
                    int initialRadius = llTextHolder.getWidth();
                    Animator anim = ViewAnimationUtils.createCircularReveal(llTextHolder, cx, cy, initialRadius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            llTextHolder.setVisibility(View.INVISIBLE);
                        }
                    });
                    isEditTextVisible = false;
                    anim.start();

                   /* mFloatingButton.setImageResource(R.drawable.icn_morph_reverse);
                    mAnimatable = (Animatable) (mFloatingButton).getDrawable();
                    mAnimatable.start();*/
                }

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


    private void addPayment() {

        //1 check if payment amount is valid
        if (mPaymentAmount.getText().length() <= 0) {
            mtilPAmount.setError(getString(R.string.error_wrong_amount));
            return;
        } else {
            mtilPAmount.setErrorEnabled(false);
        }

        PaymentModel model = new PaymentModel(userModel, mPaymentDescription.getText().toString(), Double.parseDouble(mPaymentAmount.getText().toString()), Calendar.getInstance().getTime().getTime() + "", null);
        mPaymentsDatabaseReference.push().setValue(model);

        mPaymentAmount.setText("");
        mPaymentDescription.setText("");

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //reveal();
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
        animator.setDuration(1000);
        animator.start();
    }


}
