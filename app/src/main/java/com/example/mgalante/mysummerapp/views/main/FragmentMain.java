package com.example.mgalante.mysummerapp.views.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.adapter.ClickListenerGallery;
import com.example.mgalante.mysummerapp.adapter.GalleryRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mgalante on 30/03/17.
 */

public class FragmentMain extends Fragment implements FragmentMainContract.View, ClickListenerGallery {


    private FragmentPresenter presenter;
    private DatabaseReference mImagesDatabaseReference;
    private LinearLayoutManager mLinearLayoutManager;


    @BindView(R.id.gallery_recycler_view)
    RecyclerView mGalleryRecyclerView;
    @BindView(R.id.days_number)
    TextView mRemainingDays;
    @BindView(R.id.hours_number)
    TextView mRemainingHours;
    @BindView(R.id.minutes_number)
    TextView mRemainingMinutes;
    @BindView(R.id.seconds_number)
    TextView mRemainingSeconds;

    public FragmentMain() {
        // Required empty public constructor
    }

    public static FragmentMain newInstance() {
        Bundle args = new Bundle();
        FragmentMain fragment = new FragmentMain();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        if (presenter == null) {
            presenter = new FragmentPresenter();
        }
        presenter.attach(getContext(), this);
        mRemainingDays.setText(timeRemaining(new Date()));

        startLoadingImages();
        return view;
    }

    private void startLoadingImages() {

        StaggeredGridLayoutManager staggeredGridLayoutManagerVertical =
                new StaggeredGridLayoutManager(
                        2, //The number of Columns in the grid
                        LinearLayoutManager.VERTICAL);

        final GalleryRecyclerViewAdapter galleryRecyclerViewAdapter = new GalleryRecyclerViewAdapter(getContext(), mImagesDatabaseReference, FirebaseAuth.getInstance().getCurrentUser().getUid(), this);
       /* galleryRecyclerViewAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int imageCount = galleryRecyclerViewAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (imageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mGalleryRecyclerView.scrollToPosition(positionStart);
                }
            }
        });*/
        mGalleryRecyclerView.setLayoutManager(staggeredGridLayoutManagerVertical);
        mGalleryRecyclerView.setAdapter(galleryRecyclerViewAdapter);
        Log.i("TEST GALLERY", galleryRecyclerViewAdapter.getItemCount() + "");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.reference_gallery_database));
        //mImagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.MESSAGE));
        mLinearLayoutManager = new LinearLayoutManager(getContext());
    }

    //region Countdown
    public String timeRemaining(Date then) {
        Date now = new Date();
        long diff = then.getTime() - now.getTime();
        String remaining = "";
        if (diff >= 86400000) {
            long days = diff / 86400000;
            String day;
            day = (days > 1) ? "days" : "day";
            String remining = "" + days + day;
            remining = "" + days;
            diff -= days * 86400000;
        }
        //... similar math for hours, minutes
        return remaining;
    }


    /**
     * The time (in ms) interval to update the countdown TextView.
     */
    private static final int COUNTDOWN_UPDATE_INTERVAL = 500;

    private Handler countdownHandler;

    /**
     * Stops the  countdown timer.
     */
    private void stopCountdown() {
        if (countdownHandler != null) {
            countdownHandler.removeCallbacks(updateCountdown);
            countdownHandler = null;
        }
    }

    /**
     * (Optionally stops) and starts the countdown timer.
     */
    private void startCountdown() {
        stopCountdown();

        countdownHandler = new Handler();
        updateCountdown.run();
    }

    /**
     * Updates the countdown.
     */
    private Runnable updateCountdown = new Runnable() {
        @Override
        public void run() {
            try {
                presenter.getCountdownText("28-8-2017");
                //mTextView.setText(getCountdownText(getContext(), deStringToDate("28-4-2017")));
            } finally {
                countdownHandler.postDelayed(updateCountdown, COUNTDOWN_UPDATE_INTERVAL);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        startCountdown();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopCountdown();
    }

    //endregion

    @Override
    public void setPresenter(FragmentMainContract.Presenter presenter) {
    }

    @Override
    public void updateView(String futureDay, String remainingHours, String remaininMinutes, String remainingSeconds) {
        mRemainingDays.setText(futureDay);
        mRemainingHours.setText(remainingHours);
        mRemainingMinutes.setText(remaininMinutes);
        mRemainingSeconds.setText(remainingSeconds);
    }

    @Override
    public void clickImageGallery(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {

    }

}
