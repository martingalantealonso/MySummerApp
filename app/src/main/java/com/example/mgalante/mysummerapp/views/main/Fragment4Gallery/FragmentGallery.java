package com.example.mgalante.mysummerapp.views.main.Fragment4Gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.adapter.ClickListenerGallery;
import com.example.mgalante.mysummerapp.adapter.GalleryRecyclerViewAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mgalante on 20/04/17.
 */

public class FragmentGallery extends Fragment implements ClickListenerGallery {

    private DatabaseReference mImagesDatabaseReference;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

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

        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, view);

        startLoadingImages();

        return view;
    }

    private void startLoadingImages() {

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        final GalleryRecyclerViewAdapter galleryRecyclerViewAdapter = new GalleryRecyclerViewAdapter(getContext(), mImagesDatabaseReference, FirebaseAuth.getInstance().getCurrentUser().getUid(), this);

        mGalleryRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mGalleryRecyclerView.setAdapter(galleryRecyclerViewAdapter);
    }


    @Override
    public void clickImageGallery(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {
        Toast.makeText(getContext(), "BAAAH", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImagesDatabaseReference = FirebaseDatabase.getInstance().getReference().child(getResources().getString(R.string.reference_gallery_database));

    }
}
