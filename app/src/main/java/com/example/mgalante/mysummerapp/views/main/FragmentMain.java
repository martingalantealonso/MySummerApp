package com.example.mgalante.mysummerapp.views.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mgalante.mysummerapp.R;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mgalante on 30/03/17.
 */

public class FragmentMain extends Fragment implements FragmentMainContract.View {


    private FragmentPresenter presenter;

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
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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

 /*   @Override
    public void updateView(String result) {
        mTextView.setText(result);
    }*/

    @Override
    public void setPresenter(FragmentMainContract.Presenter presenter) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void updateView(String futureDay, String remainingHours, String remaininMinutes, String remainingSeconds) {
        mRemainingDays.setText(futureDay);
        mRemainingHours.setText(remainingHours);
        mRemainingMinutes.setText(remaininMinutes);
        mRemainingSeconds.setText(remainingSeconds);
    }
}
