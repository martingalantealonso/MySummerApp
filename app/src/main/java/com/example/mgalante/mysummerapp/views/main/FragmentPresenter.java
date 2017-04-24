package com.example.mgalante.mysummerapp.views.main;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mgalante on 30/03/17.
 */

public class FragmentPresenter implements FragmentMainContract.Presenter {

    private FragmentMainContract.View mView;
    private Context mContext;

    public FragmentPresenter() {
    }

    @Override
    public void attach(Context context, FragmentMainContract.View view) {
        this.mContext = context;
        this.mView = view;
    }

    @Override
    public void sendFileToFirebase(Context mContext, StorageReference storageReference, File file, DatabaseReference databaseReference, User userModel, @Nullable ChatModel chatModel, @Nullable PaymentModel paymentModel) {

    }

    @Override
    public void getCountdownText(String futureDateString) {

        StringBuilder countdownText = new StringBuilder();
        String sDays = "";
        String sHours = "";
        String sMinutes = "";
        String sSeconds = "";

        ArrayList<String[]> arrayRemaining = new ArrayList<>();

        Date futureDate = deStringToDate(futureDateString);
        // Calculate the time between now and the future date.
        long timeRemaining = futureDate.getTime() - new Date().getTime();

        // If there is no time between (ie. the date is now or in the past), do nothing
        if (timeRemaining > 0) {
            Resources resources = mContext.getResources();

            // Calculate the days/hours/minutes/seconds within the time difference.
            //
            // It's important to subtract the value from the total time remaining after each is calculated.
            // For example, if we didn't do this and the time was 25 hours from now,
            // we would get `1 day, 25 hours`.
            int days = (int) TimeUnit.MILLISECONDS.toDays(timeRemaining);
            timeRemaining -= TimeUnit.DAYS.toMillis(days);
            int hours = (int) TimeUnit.MILLISECONDS.toHours(timeRemaining);
            timeRemaining -= TimeUnit.HOURS.toMillis(hours);
            int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
            timeRemaining -= TimeUnit.MINUTES.toMillis(minutes);
            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(timeRemaining);

            // For each time unit, add the quantity string to the output, with a space.
            if (days > 0) {
                countdownText.append(resources.getQuantityString(R.plurals.days, days, days));
                countdownText.append(" ");
                arrayRemaining.add(new String[]{"days", resources.getQuantityString(R.plurals.days, days, days)});
                sDays = resources.getQuantityString(R.plurals.days, days, days);
            }
            if (days > 0 || hours > 0) {
                countdownText.append(resources.getQuantityString(R.plurals.hours, hours, hours));
                countdownText.append(" ");
                arrayRemaining.add(new String[]{"hours", resources.getQuantityString(R.plurals.hours, hours, hours)});
                sHours = resources.getQuantityString(R.plurals.hours, hours, hours);
            }
            if (days > 0 || hours > 0 || minutes > 0) {
                countdownText.append(resources.getQuantityString(R.plurals.minutes, minutes, minutes));
                countdownText.append(" ");
                arrayRemaining.add(new String[]{"minutes", resources.getQuantityString(R.plurals.minutes, minutes, minutes)});
                sMinutes=resources.getQuantityString(R.plurals.minutes, minutes, minutes);
            }
            if (days > 0 || hours > 0 || minutes > 0 || seconds > 0) {
                countdownText.append(resources.getQuantityString(R.plurals.seconds, seconds, seconds));
                countdownText.append(" ");
                arrayRemaining.add(new String[]{"seconds", resources.getQuantityString(R.plurals.seconds, seconds, seconds)});
                sSeconds = resources.getQuantityString(R.plurals.seconds, seconds, seconds);
            }
        }

        //mView.updateView(countdownText.toString());
        mView.updateView(sDays, sHours,sMinutes, sSeconds);
           }


    //Devuele un java.util.Date desde un String en formato dd-MM-yyyy
    //@param La fecha a convertir a formato date
    //@return Retorna la fecha en formato Date
    public static synchronized java.util.Date deStringToDate(String fecha) {
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd-MM-yyyy");
        Date fechaEnviar = null;
        try {
            fechaEnviar = formatoDelTexto.parse(fecha);
            return fechaEnviar;
        } catch (ParseException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
