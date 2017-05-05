package com.example.mgalante.mysummerapp.views.main.Fragment3Calculator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.adapter.ClickListenerChatFirebase;
import com.example.mgalante.mysummerapp.adapter.ClickListenerPayment;
import com.example.mgalante.mysummerapp.adapter.PaymentsListArrayAdapter;
import com.example.mgalante.mysummerapp.adapter.UserListArrayAdapter;
import com.example.mgalante.mysummerapp.common.BasePresenter;
import com.example.mgalante.mysummerapp.common.BasePresenterImpl;
import com.example.mgalante.mysummerapp.common.BaseView;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.entities.users.all.GetUsersContract;
import com.example.mgalante.mysummerapp.entities.users.all.GetUsersPresenter;
import com.example.mgalante.mysummerapp.entities.users.current.GetCurrentUserContract;
import com.example.mgalante.mysummerapp.entities.users.current.GetCurrentUserPresenter;
import com.example.mgalante.mysummerapp.utils.CacheStore;
import com.example.mgalante.mysummerapp.utils.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.offset;
import static android.app.Activity.RESULT_OK;
import static com.example.mgalante.mysummerapp.utils.Util.collapse;
import static com.example.mgalante.mysummerapp.utils.Util.expand;

/**
 * Created by mgalante on 31/03/17.
 */

//TODO DELETE RECEIPT IMAGE(reference) AFTER UPLOAD IT
public class FragmentCalculator extends Fragment implements ClickListenerChatFirebase, GetUsersContract.View, GetCurrentUserContract.View, ClickListenerPayment, BaseView, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "PantinCalculator";
    private static final int IMAGE_GALLERY_REQUEST = 1;
    private static final int IMAGE_CAMERA_REQUEST = 2;
    private boolean isEditTextVisible;
    private Animatable mAnimatable;

    private SharedPreferences prefs;
    private File filePathImageCamera;
    private Uri selectedImageUri;

    public static User userModel;
    private GetUsersPresenter mGetUsersPresenter;
    private GetCurrentUserPresenter mGetCurrentUserPresenter;

    private BasePresenter basePresenter;

    private LinearLayoutManager mLinearLayoutManager;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private StaggeredGridLayoutManager mStaggeredLayoutManagerPayments;
    private GridLayoutManager mGridLayoutManagerPayments;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mPaymentsDatabaseReference;
    private UserListArrayAdapter adapter;

    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference storageRef;
    private StorageReference imageCameraRef;
    private StorageReference imageGalleryRef;

    private Double paymentSum;
    private Double totalPaymentSum;


    //region BindViews
    @BindView(R.id.calculator_main_holder)
    LinearLayout mMainHolder;
    @BindView(R.id.payments_detail_view)
    LinearLayout mPaymentsDetailHolder;
    @BindView(R.id.calculator_image_name_holder)
    LinearLayout mImageNameHolder;
    @BindView(R.id.llEditTextHolder)
    LinearLayout llTextHolder;
    @BindView(R.id.calculator_user_photo)
    CircleImageView mUserPhoto;
    @BindView(R.id.user_list_detail)
    RecyclerView mRecyclerView;
    @BindView(R.id.user_payments_list_detail)
    RecyclerView mRecyclerViewPayments;

    @BindView(R.id.main_appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.backgroundViewShadow)
    View mBackgroundViewShadow;

    @BindView(R.id.calculator_txtv_spent)
    TextView mSpentTextView;
    @BindView(R.id.calculator_txtv_total_sum)
    TextView mTotalSumTextView;
    @BindView(R.id.input_payment_amount)
    TextInputLayout mtilPAmount;
    @BindView(R.id.input_payment_title)
    TextInputLayout mtilPTitle;
    @BindView(R.id.input_payment_description)
    TextInputLayout mtilPDescription;
    @BindView(R.id.edtxt_payment_amount)
    EditText mPaymentAmount;
    @BindView(R.id.edtxt_payment_title)
    EditText mPaymentTitle;
    @BindView(R.id.edtxt_payment_description)
    EditText mPaymentDescription;
    @BindView(R.id.edtxt_file_name)
    EditText mPaymentFileName;
    @BindView(R.id.btn_camera)
    Button mCameraButton;
    @BindView(R.id.btn_gallery)
    Button mGalleryButton;
    @BindView(R.id.btn_delete_file)
    ImageButton mCancelButton;
    @BindView(R.id.btn_accept_payment)
    FloatingActionButton mFloatingActionButton;
    //endregion

    public FragmentCalculator() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        mGetUsersPresenter = new GetUsersPresenter(this);
        mGetCurrentUserPresenter = new GetCurrentUserPresenter(this);

        basePresenter = new BasePresenterImpl(this);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userModel = new User(user.getDisplayName(), String.valueOf(user.getPhotoUrl()), user.getUid());
        userModel.setPaymentsSum(Double.parseDouble(prefs.getString(getString(R.string.payments_sum), "0.0")));
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(getResources().getString(R.string.USERS));

        mPaymentsDatabaseReference = mFirebaseDatabase.getReference().child(getResources().getString(R.string.reference_payments));

        mLinearLayoutManager = new LinearLayoutManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.experimental_fragment_calculator, container, false);
        ButterKnife.bind(this, view);

        mAppBarLayout.addOnOffsetChangedListener(this);

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

        mSpentTextView.setText(String.valueOf(prefs.getString(getString(R.string.payments_sum), "00.0") + "€"));
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        //mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL); // 2 -> number of columns
        mStaggeredLayoutManagerPayments = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        mGridLayoutManagerPayments = new GridLayoutManager(getContext(),4,LinearLayoutManager.HORIZONTAL,false);
        //mGridLayoutManagerPayments = new GridLayoutManager(getContext(),1);

        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

        initializeUsers();

        // ImagePickerButton shows an image picker to upload a image for a message
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPayment();
            }
        });

        mUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPaymentsDetailHolder.getVisibility() == View.GONE) {
                    mBackgroundViewShadow.animate().alpha(1.0f);
                    expand(mPaymentsDetailHolder);
                } else {
                    mBackgroundViewShadow.animate().alpha(0.0f);
                    collapse(mPaymentsDetailHolder);
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaymentFileName.setText("");
                mImageNameHolder.setVisibility(View.GONE);
                if (filePathImageCamera != null) {
                    mGalleryButton.setVisibility(View.VISIBLE);
                    filePathImageCamera = null;
                }
                if (selectedImageUri != null) {
                    mCameraButton.setVisibility(View.VISIBLE);
                    selectedImageUri = null;
                }
            }
        });
        return view;
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

                //region don't know for what was this anymore
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
                //endregion

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

                    hidePaymentView(cx, cy);

                   /* mFloatingButton.setImageResource(R.drawable.icn_morph_reverse);
                    mAnimatable = (Animatable) (mFloatingButton).getDrawable();
                    mAnimatable.start();*/
                }

                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG_PAYMENTS);

        if (requestCode == IMAGE_CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (filePathImageCamera != null && filePathImageCamera.exists()) {
                imageCameraRef = storageRef.child(filePathImageCamera.getName());
                mImageNameHolder.setVisibility(View.VISIBLE);
                //mPaymentFileName.setText(String.format("%s_camera", filePathImageCamera.getName()));
                mPaymentFileName.setText(filePathImageCamera.getName());
                // sendFileFirebase(imageCameraRef, filePathImageCamera);
                mGalleryButton.setVisibility(View.GONE);

            }
        }

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    imageGalleryRef = storageRef.child(selectedImageUri.getLastPathSegment());
                    mImageNameHolder.setVisibility(View.VISIBLE);
                    //mPaymentFileName.setText(String.format("%s_gallery",selectedImageUri.getLastPathSegment()));
                    mPaymentFileName.setText(selectedImageUri.getLastPathSegment());
                    // sendFileFirebase(imageCameraRef, filePathImageCamera);
                    mCameraButton.setVisibility(View.GONE);

                    //sendFileFirebase(storageRef, selectedImageUri, mMessagesDatabaseReference, userModel, new ChatModel(), null);
                }
            }
        }

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

    //region stupid commented things
    /*
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
    }*/
    //endregion

    @Override
    public void onGetCurrentUserSuccess(User user) {

    }

    @Override
    public void onGetCurrentUserPaymentsSuccess(List<PaymentModel> payments) {

        paymentSum = 0.0;
        for (PaymentModel payment : payments) {
            paymentSum = paymentSum + payment.getAmount();
        }
        mSpentTextView.setText(String.valueOf(String.format("%.2f", paymentSum) + "€"));

        mRecyclerViewPayments.setLayoutManager(mGridLayoutManagerPayments);
        //mRecyclerViewPayments.setLayoutManager(mGridLayoutManagerPayments);
        PaymentsListArrayAdapter adapter = new PaymentsListArrayAdapter(getContext(), payments);
        mRecyclerViewPayments.setAdapter(adapter);
     /*   DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRecyclerViewPayments.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        mRecyclerViewPayments.addItemDecoration(horizontalDecoration);*/

    }

    @Override
    public void onGetAllUsersSuccess(List<User> users) {

        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);
        adapter = new UserListArrayAdapter(getContext(), this, users);
        mRecyclerView.setAdapter(adapter);

        totalPaymentSum = 0.0;
        for (User user : users) {
            totalPaymentSum = totalPaymentSum + user.getPaymentsSum();
        }

    }

    @Override
    public void onGetAllUsersFailure(String message) {
    }

    @Override
    public void onGetAllUsersPayments(Double payments) {
        mTotalSumTextView.setText(String.valueOf(payments + "€"));
        totalPaymentSum = payments;
    }

    private void initializeUsers() {

        //With recyclerview adapter
        mGetCurrentUserPresenter.getCurrentUserPayments();
        mGetUsersPresenter.getAllUsers();
        mGetUsersPresenter.getAllUsersPayments();


        // with firebaserecyclerview adapter
        /*final PaymentsFirebaseAdapter paymentsFirebaseAdapter = new PaymentsFirebaseAdapter(getContext(), mPaymentsDatabaseReference, userModel.getUid(), this);
        paymentsFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = paymentsFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mRecyclerViewPayments.scrollToPosition(positionStart);
                    mGetCurrentUserPresenter.getCurrentUserPayments();
                }
            }
        });
        mRecyclerViewPayments.setLayoutManager(mStaggeredLayoutManagerPayments);
        mRecyclerViewPayments.setAdapter(paymentsFirebaseAdapter);
        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRecyclerViewPayments.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.horizontal_divider);
        horizontalDecoration.setDrawable(horizontalDivider);
        mRecyclerViewPayments.addItemDecoration(horizontalDecoration);
        mGetUsersPresenter.getAllUsers();*/

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

    private void addPayment() {

        //1 check if payment amount is valid
        if (mPaymentAmount.getText().length() <= 0) {
            mtilPAmount.setError(getString(R.string.error_wrong_amount));
            return;
        } else {
            mtilPAmount.setErrorEnabled(false);
        }

        // 2 check if payment title is valid
        if (mPaymentTitle.getText().length() <= 0) {
            mPaymentTitle.setError(getString(R.string.error_wrong_title));
            return;
        } else {
            mtilPTitle.setErrorEnabled(false);
        }

        final PaymentModel model = new PaymentModel(userModel, mPaymentTitle.getText().toString(), mPaymentDescription.getText().toString(), Double.parseDouble(mPaymentAmount.getText().toString()), Calendar.getInstance().getTime().getTime() + "", null);

        if (filePathImageCamera != null && filePathImageCamera.exists()) {
            basePresenter.sendFilefromCameraToFirebase(getContext(), imageCameraRef, filePathImageCamera, mPaymentsDatabaseReference, userModel, null, model);
        } else if (selectedImageUri != null) {
            basePresenter.sendFileFromGalleryTofirebase(imageGalleryRef, selectedImageUri, mPaymentsDatabaseReference, userModel, null, model);
        } else {
            mPaymentsDatabaseReference.push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    // Sum payment to user
                    if (String.valueOf(userModel.getPaymentsSum()).equals("0.0")) {
                        userModel.setPaymentsSum(Double.parseDouble(mPaymentAmount.getText().toString()));
                    } else {
                        userModel.setPaymentsSum(userModel.getPaymentsSum() + Double.parseDouble(mPaymentAmount.getText().toString()));
                    }

                    Util.updateUserToDatabase(getActivity(), userModel, paymentSum);

                    mPaymentAmount.setText("");
                    mPaymentTitle.setText("");
                    mPaymentDescription.setText("");

                    Snackbar.make(getView(), getString(R.string.payment_success), Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.snack_accent))
               /* .setAction(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("Snackbar", "Pulsada acción snackbar!");
                    }
                })*/
                            .show();

                    mGetCurrentUserPresenter.getCurrentUserPayments();
                    mGetUsersPresenter.getAllUsersPayments();

                    adapter.notifyDataSetChanged();
                }
            });
        }


        //mGetCurrentUserPresenter.getCurrentUserPayments();


        hidePaymentView(llTextHolder.getRight(), llTextHolder.getTop());


    }

    private void hidePaymentView(int cx, int cy) {
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
    }

    private void setUserPhoto(Bitmap resource) {
        mUserPhoto.setImageBitmap(resource);
        //Palette palette = Palette.from(resource).generate();
        //mMainHolder.setBackgroundColor(palette.getDarkMutedColor(Color.DKGRAY));
        //getView().setBackgroundColor(palette.getDarkMutedColor(Color.DKGRAY));
    }

    private void launchCamera() {
        Log.d(TAG, "launchCamera");
        String photoName = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        filePathImageCamera = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), photoName + "camera.jpg");
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(getContext(),
                "com.example.android.fileprovider",
                filePathImageCamera);
        it.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(it, IMAGE_CAMERA_REQUEST);
    }

    @Override
    public void clickListenerPayment(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {
        Toast.makeText(getContext(), "HEY", Toast.LENGTH_LONG).show();
    }

    @Override
    public void setPresenter(Object presenter) {

    }

    @Override
    public void onValuePushedSuccess() {
        Log.i(TAG, "HAAAAA!!");
      /*  mGetCurrentUserPresenter.getCurrentUserPayments();
        mGetUsersPresenter.getAllUsersPayments();*/

        // Sum payment to user
        if (String.valueOf(userModel.getPaymentsSum()).equals("0.0")) {
            userModel.setPaymentsSum(Double.parseDouble(mPaymentAmount.getText().toString()));
        } else {
            userModel.setPaymentsSum(userModel.getPaymentsSum() + Double.parseDouble(mPaymentAmount.getText().toString()));
        }
        Util.updateUserToDatabase(getActivity(), userModel, paymentSum);

        Snackbar.make(getView(), getString(R.string.payment_success), Snackbar.LENGTH_LONG).show();

        mPaymentAmount.setText("");
        mPaymentTitle.setText("");
        mPaymentDescription.setText("");

        initializeUsers();

        filePathImageCamera = null;

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        // handleAlphaOnTitle(percentage);
        // handleToolbarTitleVisibility(percentage);
    }
}
