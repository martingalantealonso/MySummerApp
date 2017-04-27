package com.example.mgalante.mysummerapp.views.filemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.example.mgalante.mysummerapp.entities.users.User;
import com.example.mgalante.mysummerapp.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FileManagerActivity extends AppCompatActivity implements FileManagerContract.View {

    private FileManagerPresenter presenter;
    private int resources;
    private static ArrayList<Uri> imageUris;

    private DatabaseReference mGalleryPhotosReference;

    private User userModel;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            displayFinishDialog("Please, sing in the application");
        } else {
            userModel=new User();
            userModel.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            userModel.setPhotoUrl(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()));
            userModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        if (presenter == null) {
            presenter = new FileManagerPresenter();
        }
        presenter.attach(getApplicationContext(), this);
        resources = 0;
        imageUris = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                // handleSendImage(intent); // Handle single image being sent
                imageUris = presenter.handleSendImage(intent);
                resources = imageUris.size();
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                //handleSendMultipleImages(intent); // Handle multiple images being sent
                imageUris = presenter.handleSendMultipleImages(intent);
                resources = imageUris.size();
            }
        }/* else {
            // Handle other intents, such as being started from the home screen
        }*/


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(Util.URL_STORAGE_REFERENCE).child(Util.FOLDER_STORAGE_IMG_GALLERY);
        mGalleryPhotosReference = FirebaseDatabase.getInstance().getReference().child(Util.FOLDER_STORAGE_IMG_GALLERY);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUris != null) {
                    for (Uri imageUir : imageUris) {
                        ImageModel imageModel = new ImageModel();
                        imageModel.setUserModel(userModel);
                        // presenter.sendFileFromGalleryTofirebase(storageRef, imageUir, mGalleryPhotosReference,);
                        presenter.sendGalleryPhotoToFirebase(storageRef, imageUir, mGalleryPhotosReference, imageModel);
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(FileManagerContract.Presenter presenter) {

    }

    @Override
    public void onValuePushedSuccess() {

    }

    @Override
    public void displayFinishDialog(String message) {
        new AlertDialog.Builder(FileManagerActivity.this)
                .setTitle("Upss... :(")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(getDrawable(R.drawable.vd_sentiment_very_dissatisfied))
                .show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_IMAGE = "section_image";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_IMAGE, imageUris.get(sectionNumber - 1).toString());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_file_manager, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            textView.setText(getArguments().getString(ARG_SECTION_IMAGE));
            //final TouchImageView imageHolder = (TouchImageView) rootView.findViewById(R.id.touch_image_view);
            final ImageView imageHolder = (ImageView) rootView.findViewById(R.id.touch_image_view);
           /* Glide.with(this).load(Uri.parse(getArguments().getString(ARG_SECTION_IMAGE)))
                    .into(imageHolder);*/

            Glide.with(this).load(Uri.parse(getArguments().getString(ARG_SECTION_IMAGE))).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {

                @Override
                public void onLoadStarted(Drawable placeholder) {

                }

                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    imageHolder.setImageBitmap(resource);
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    Toast.makeText(getContext(), "Erro, tente novamente", Toast.LENGTH_LONG).show();
                    Log.e("Erro Glide", e.getMessage() + ": " + getArguments().getString(ARG_SECTION_IMAGE));
                }
            });

            //imageHolder.setImageURI(Uri.parse(getArguments().getString(ARG_SECTION_IMAGE)));
            //Picasso.with(getContext()).load(Uri.parse(getArguments().getString(ARG_SECTION_IMAGE))).into(imageHolder);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return resources;
        }

       /* @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        imageUris = null;
    }
}