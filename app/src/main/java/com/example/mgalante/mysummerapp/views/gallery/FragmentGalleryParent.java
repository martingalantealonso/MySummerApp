package com.example.mgalante.mysummerapp.views.gallery;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mgalante.mysummerapp.R;
import com.example.mgalante.mysummerapp.views.main.Fragment4Gallery.FragmentGallery;

/**
 * Created by mgalante on 3/05/17.
 */

public class FragmentGalleryParent extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parent, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        TabLayout mTabLayout=(TabLayout)view.findViewById(R.id.tablayout);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    public static class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                return FragmentMediaThumb.newInstance();
            }

            return FragmentGallery.newInstance();

        }

        @Override
        public CharSequence getPageTitle(int position) {

            if(position==0){
                return "Cloud";
            }
            else if(position==1){
                return "Stored";
            }
            return super.getPageTitle(position);
        }
    }



}
