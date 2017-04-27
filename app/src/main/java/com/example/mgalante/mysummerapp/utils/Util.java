package com.example.mgalante.mysummerapp.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.FileModel;
import com.example.mgalante.mysummerapp.entities.PaymentModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Alessandro Barreto on 23/06/2016.
 */
public class Util {

    private final static String TAG = "PantinAppTag";

    public static final String URL_STORAGE_REFERENCE = "gs://pantinapp-c16a0.appspot.com";
    public static final String FOLDER_STORAGE_IMG = "chat_photos";
    public static final String FOLDER_STORAGE_IMG_GALLERY = "gallery_photos";
    public static final String FOLDER_STORAGE_IMG_PAYMENTS = "payments_photos";
    public static final String FOLDER_STORAGE_IMG_USER = "user_photos";
    public static final String DEFAULT_NULL_IMAGE = "https://firebasestorage.googleapis.com/v0/b/pantinapp-c16a0.appspot.com/o/magrathea2.png?alt=media&token=08a67b44-e623-4836-81c0-d21c06499045";
    public static final String FOLDER_SD_IMAGES = "/PantinClassic/PantinGallery/";
    public static final String FOLDER_SD_IMAGES2 = "/PantinClassic/PantinGallery";

    public static void initToast(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

  /*  public static boolean verificaConexao(Context context) {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        conectado = conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
        return conectado;
    }

    public static String local(String latitudeFinal, String longitudeFinal) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitudeFinal + "," + longitudeFinal + "&zoom=18&size=280x280&markers=color:red|" + latitudeFinal + "," + longitudeFinal;
    }*/

    public static void updateUserToDatabase(Context context, User firebaseUser) {
        User user = new User(firebaseUser.getUid(), firebaseUser.getName(),
                firebaseUser.getPhotoUrl(),
                new SharedPrefUtil(context).getString(Constants.ARG_FIREBASE_TOKEN), firebaseUser.getPaymentsSum());
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

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        //v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.MATCH_PARENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void sendFileFirebase(StorageReference storageReference, Uri file, final DatabaseReference databaseReference, final User userModel, @Nullable final ChatModel chatModel, @Nullable final PaymentModel paymentModel) {
        if (storageReference != null) {
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel fileModel = new FileModel("img", downloadUrl.toString(), name, "");
                    if (chatModel != null) {
                        ChatModel chatModelValue = new ChatModel(userModel, "", Calendar.getInstance().getTime().getTime() + "", fileModel);
                        databaseReference.push().setValue(chatModelValue);
                    } else if (paymentModel != null) {
                        paymentModel.setFile(fileModel);
                        databaseReference.push().setValue(paymentModel);
                    }
                }
            });
        } else {
            //IS NULL
        }
    }

    public static void sendFileFirebase(Context mContext, StorageReference storageReference, final File file, final DatabaseReference databaseReference, final User userModel, @Nullable final ChatModel chatModel, @Nullable final PaymentModel paymentModel) {
        if (storageReference != null) {
            Uri photoURI = FileProvider.getUriForFile(mContext, "com.example.android.fileprovider", file);
            UploadTask uploadTask = storageReference.putFile(photoURI);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel fileModel = new FileModel("img", downloadUrl.toString(), file.getName(), file.length() + "");
                    if (chatModel != null) {
                        ChatModel chatModelValue = new ChatModel(userModel, "", Calendar.getInstance().getTime().getTime() + "", fileModel);
                        databaseReference.push().setValue(chatModelValue);
                    } else if (paymentModel != null) {
                        paymentModel.setFile(fileModel);
                        databaseReference.push().setValue(paymentModel);
                    }
                }
            });
        }
    }

}
