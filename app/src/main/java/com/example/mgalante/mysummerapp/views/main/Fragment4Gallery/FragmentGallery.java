package com.example.mgalante.mysummerapp.views.main.Fragment4Gallery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.adapter.ClickListenerGallery;
import com.example.mgalante.mysummerapp.adapter.GalleryRecyclerViewAdapter;
import com.example.mgalante.mysummerapp.entities.ImageModel;
import com.example.mgalante.mysummerapp.views.main.FullScreenImageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mgalante on 20/04/17.
 */

public class FragmentGallery extends Fragment implements FragmentGalleryContract.View, ClickListenerGallery {

    private static final String KEY_RECYCLER_STATE = "mGalleryRecyclerView";
    private DatabaseReference mImagesDatabaseReference;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private FragmentGalleryPresenter mFragmentGalleryPresenter;
    private static Bundle mBundleRecyclerViewState;
    GalleryRecyclerViewAdapter galleryRecyclerViewAdapter;


    @BindView(R.id.gallery_recycler_view)
    RecyclerView mGalleryRecyclerView;

    public FragmentGallery() {
        // Required empty public constructor
    }

    public static FragmentGallery newInstance() {
        Bundle args = new Bundle();
        FragmentGallery fragment = new FragmentGallery();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gallery_old, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        if (mFragmentGalleryPresenter == null) {
            mFragmentGalleryPresenter = new FragmentGalleryPresenter();
        }
        mFragmentGalleryPresenter.attach(getContext(), this);

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        galleryRecyclerViewAdapter = new GalleryRecyclerViewAdapter(getContext(), mImagesDatabaseReference, FirebaseAuth.getInstance().getCurrentUser().getUid(), this);

        updateDatabaseImages();
        startLoadingImages();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mGalleryRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);

    }

    private void updateDatabaseImages() {
        mFragmentGalleryPresenter.updateDatabaseImages();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_gallery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.reference_gallery_database));

    }

    @Override
    public void clickImageGallery(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {
        // Toast.makeText(getContext(), "BAAAH\n" + urlPhotoClick + "\n" + urlPhotoUser, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), FullScreenImageActivity.class);
        intent.putExtra("nameUser", nameUser);
        intent.putExtra("urlPhotoUser", urlPhotoUser);
        intent.putExtra("urlPhotoClick", urlPhotoClick);

        startActivity(intent);
    }

    @Override
    public void clickIconDownload(View view, int position, ImageModel imageModel) {

    }


    private void startLoadingImages() {

        //mGalleryRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mGalleryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mGalleryRecyclerView.setAdapter(galleryRecyclerViewAdapter);
    }


    @Override
    public void setPresenter(FragmentGalleryContract.Presenter presenter) {

    }

    @Override
    public void onValuePushedSuccess() {

    }
}
