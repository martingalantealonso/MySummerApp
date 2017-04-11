package com.example.mgalante.mysummerapp.views.main.Fragment2Chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.adapter.ClickListenerChatFirebase;
import com.example.mgalante.mysummerapp.services.MyUploadService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.facebook.GraphRequest.TAG;

/**
 * Created by mgalante on 3/04/17.
 */

//TODO image click listener
//TODO add time
//TODO add notification
public class FragmentChat extends Fragment implements FragmentChatContract.View, ClickListenerChatFirebase {

    private FragmentChatPresenter presenter;

    public static final String ANONYMOUS = "anonymous";
    private static final String CHAT_DIRECTORY = "PantinClassic/PantinChat/";
    private static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    private static final int RC_TAKE_PICTURE = 101;
    public String mUsername;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private MessageAdapter mMessageAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser user;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    //private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosReference;
    public FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;

    private SharedPreferences prefs;


    @BindView(R.id.messageListView)
    ListView mMessageListView;
    @BindView(R.id.photoPickerButton)
    ImageButton mPhotoPickerButton;
    @BindView(R.id.messageEditText)
    EditText mMessageEditText;
    @BindView(R.id.sendButton)
    ImageButton mSendButton;
    @BindView(R.id.cameraButton)
    ImageButton mCameraButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);
        if (presenter == null) {
            presenter = new FragmentChatPresenter();
        }
        presenter.attach(getContext(), this);


        // Initialize message ListView and its adapter
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(getContext(), R.layout.chat_message_right, friendlyMessages,this);
        mMessageListView.setAdapter(mMessageAdapter);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                //    public FriendlyMessage(String text, String name, String senderUid, String photoUrl, String receiver, long timestamp) {
                //FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null);
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, user.getUid(), null, null, String.valueOf(Calendar.getInstance().getTime().getTime()), String.valueOf(user.getPhotoUrl()));
                mMessagesDatabaseReference.push().setValue(friendlyMessage);
                // Clear input box
                mMessageEditText.setText("");
            }
        });

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            onSignedInIntialize(user.getDisplayName());
        }


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsername = ANONYMOUS;

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child(getResources().getString(R.string.MESSAGE));
        mChatPhotosReference = mFirebaseStorage.getReference().child("chat_photos");

        prefs = getActivity().getApplicationContext().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat, menu);
        boolean isChecked = prefs.getBoolean("store_chat_images", false);
        MenuItem item = menu.findItem(R.id.chat_menu_seccion_1);
        item.setChecked(isChecked);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_menu_seccion_1:
                if (item.isCheckable() && item.isChecked()) {
                    item.setChecked(false);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("store_chat_images", false);
                    editor.apply();
                } else if (!item.isChecked()) {
                    item.setChecked(true);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("store_chat_images", true);
                    editor.apply();
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void onSignedInIntialize(String displayName) {
        mUsername = displayName;
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdapter.add(friendlyMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void deattachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    public void setPresenter(FragmentChatContract.Presenter presenter) {
    }

    @Override
    public void showMessage(String message) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedUri = data.getData();
            StorageReference photoRef = mChatPhotosReference.child(selectedUri.getLastPathSegment());

            //Upload file to Firebase Storage
            photoRef.putFile(selectedUri).addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUrl.toString());
                    mMessagesDatabaseReference.push().setValue(friendlyMessage);
                }
            });
        }
        if (requestCode == RC_TAKE_PICTURE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference photoRef = mChatPhotosReference.child(imageUri.getLastPathSegment());
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storagePath = new File(Environment.getExternalStorageDirectory(), CHAT_DIRECTORY);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storagePath      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // Save the File URI
        mFileUri = fileUri;

        // Clear the last download, if any
        //updateUI(mAuth.getCurrentUser());
        mDownloadUrl = null;

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        getContext().startService(new Intent(getContext(), MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .setAction(MyUploadService.ACTION_UPLOAD));

        // Show loading spinner
        //showProgressDialog(getString(R.string.progress_uploading));
    }


    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }

        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getContext(),
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);
        }

    }

    @Override
    public void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {

    }

    @Override
    public void clickImageMapChat(View view, int position, String latitude, String longitude) {

    }
}
