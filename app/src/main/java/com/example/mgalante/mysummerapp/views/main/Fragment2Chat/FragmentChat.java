package com.example.mgalante.mysummerapp.views.main.Fragment2Chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.adapter.ChatFirebaseAdapter;
import com.example.mgalante.mysummerapp.adapter.ClickListenerChatFirebase;
import com.example.mgalante.mysummerapp.entities.ChatModel;
import com.example.mgalante.mysummerapp.entities.FileModel;
import com.example.mgalante.mysummerapp.utils.Util;
import com.example.mgalante.mysummerapp.views.main.FullScreenImageActivity;
import com.google.android.gms.tasks.OnFailureListener;
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
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.example.mgalante.mysummerapp.views.splash.SplashActivity.userModel;
import static com.facebook.GraphRequest.TAG;

/**
 * Created by mgalante on 3/04/17.
 */

//TODO image click listener
//TODO add notification
//TODO Image Full Screen Activity
public class FragmentChat extends Fragment implements FragmentChatContract.View, ClickListenerChatFirebase {

    private FragmentChatPresenter presenter;

    static final String CHAT_REFERENCE = "chatmodel";

    public static final String ANONYMOUS = "anonymous";
    private static final String CHAT_DIRECTORY = "PantinClassic/PantinChat/";
    private static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;
    private static final int IMAGE_GALLERY_REQUEST = 3;
    private static final int IMAGE_CAMERA_REQUEST = 4;
    private static final int PLACE_PICKER_REQUEST = 5;
    private static final int RC_TAKE_PICTURE = 101;
    public String mUsername;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //File
    private File filePathImageCamera;

    private MessageAdapter mMessageAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser user;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    //private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mChatPhotosReference;
    public FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;


    private LinearLayoutManager mLinearLayoutManager;

    private SharedPreferences prefs;

    @BindView(R.id.photoPickerButton)
    ImageButton mPhotoPickerButton;
    @BindView(R.id.messageEditText)
    EditText mMessageEditText;
    @BindView(R.id.sendButton)
    ImageButton mSendButton;
    @BindView(R.id.cameraButton)
    ImageButton mCameraButton;
    @BindView(R.id.messageRecyclerView)
    RecyclerView rvListMessage;

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
        mSendButton.setEnabled(false);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
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
                    mSendButton.setImageResource((R.drawable.vd_send_blue));
                } else {
                    mSendButton.setEnabled(false);
                    mSendButton.setImageResource((R.drawable.vd_send));
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
                ChatModel model = new ChatModel(userModel, mMessageEditText.getText().toString(), Calendar.getInstance().getTime().getTime() + "", null);
                mMessagesDatabaseReference.push().setValue(model);

                // Clear input box
                mMessageEditText.setText("");
                mSendButton.setEnabled(false);
                mSendButton.setImageResource((R.drawable.vd_send));
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

    private void initializeMessages() {

        final ChatFirebaseAdapter firebaseAdapter = new ChatFirebaseAdapter(getContext(), mMessagesDatabaseReference, user.getUid(), this);
        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvListMessage.scrollToPosition(positionStart);
                }
            }
        });
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(firebaseAdapter);
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
        mLinearLayoutManager = new LinearLayoutManager(getContext());

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
        initializeMessages();
        //attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    // mMessageAdapter.add(friendlyMessage);
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

        StorageReference storageRef = storage.getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG);

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri);
                } else {
                    //URI IS NULL
                }
            }
        }
        if (requestCode == IMAGE_CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (filePathImageCamera != null && filePathImageCamera.exists()) {
                    StorageReference imageCameraRef = storageRef.child(filePathImageCamera.getName() + "_camera");
                    sendFileFirebase(imageCameraRef, filePathImageCamera);
                } else {
                    //IS NULL
                }
            }
        }
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

    /**
     * Envia o arvquivo para o firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final Uri file) {
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
                    ChatModel chatModel = new ChatModel(userModel, "", Calendar.getInstance().getTime().getTime() + "", fileModel);
                    mMessagesDatabaseReference.push().setValue(chatModel);
                }
            });
        } else {
            //IS NULL
        }

    }

    /**
     * Send file to the firebase
     */
    private void sendFileFirebase(StorageReference storageReference, final File file) {
        if (storageReference != null) {
            Uri photoURI = FileProvider.getUriForFile(getContext(),
                    "com.example.android.fileprovider",
                    file);
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
                    ChatModel chatModel = new ChatModel(userModel, "", Calendar.getInstance().getTime().getTime() + "", fileModel);
                    mMessagesDatabaseReference.push().setValue(chatModel);
                }
            });
        } else {
            //IS NULL
        }

    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        } else {
            // we already have permission, lets go ahead and call camera intent
            launchCamera();
        }
    }

    @Override
    public void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {
        Intent intent = new Intent(getContext(),FullScreenImageActivity.class);
        intent.putExtra("nameUser",nameUser);
        intent.putExtra("urlPhotoUser",urlPhotoUser);
        intent.putExtra("urlPhotoClick",urlPhotoClick);
        startActivity(intent);
    }

    @Override
    public void clickImageMapChat(View view, int position, String latitude, String longitude) {

    }


}
