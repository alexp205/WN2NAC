package org.cook_team.wn2nac;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;

public class MainFragment extends android.support.v4.app.Fragment {

    private static EventBus bus = EventBus.getDefault();

    myPagerAdapter mAdapter;
    ViewPager mPager;

    static final int NUM_ITEMS = 3;

    public static class myPagerAdapter extends FragmentStatePagerAdapter {
        public myPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new StepWindooFragment();
                case 1:
                    return new StepMeasureFragment();
                case 2:
                    return new StepResultFragment();
            }
            return new StepWindooFragment();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            Wn2nacService.currentStep = position;
        }

        @Override
        public CharSequence getPageTitle (int position) {
            switch(position){
                case 0:
                    return "狀態";
                case 1:
                    return "量測";
                case 2:
                    return "傳送";
            }
            return "TITLE";
        }
    }

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mPager = (ViewPager) rootView.findViewById(R.id.mainView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new myPagerAdapter(getActivity().getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(Wn2nacService.currentStep);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bus.isRegistered(this)) bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    public void onEventMainThread(Wn2nacMeasure.MeasureFinishEvent event) {
        mPager.setCurrentItem(2);
    }
}