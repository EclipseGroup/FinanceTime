package com.eclipsegroup.dorel.financetime.main.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eclipsegroup.dorel.financetime.PageFragment;
import com.eclipsegroup.dorel.financetime.R;
import com.eclipsegroup.dorel.financetime.tabs.SlidingTabLayout;

public class MainPageFragment extends Fragment {


    private ViewPager mPager;
    private SlidingTabLayout mTabs;
    private FragmentPagerAdapter adapter;
    private static final Integer MAIN_PAGE = 0;

    public MainPageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_main_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(new PagerAdapter(getChildFragmentManager()));

        mTabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        mTabs.setViewPager(mPager); /* Guess you choose which PagerAdapter to control */
    }

    class PagerAdapter extends FragmentPagerAdapter{

        String[] tabs;

        public PagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.main_tabs);
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.getInstance(position, MAIN_PAGE, getActivity());
        }

        @Override
        public CharSequence getPageTitle(int position){
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 5;
        }
    }
}
