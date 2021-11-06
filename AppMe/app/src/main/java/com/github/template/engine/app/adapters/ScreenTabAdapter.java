package com.github.template.engine.app.adapters;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.github.template.engine.app.fragments.AppCleanerFragment;
import com.github.template.engine.app.fragments.AppManagerFragment;

public class ScreenTabAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private String[] titles = { "Audio Record","History Record" };

    public ScreenTabAdapter(FragmentManager fm) {
        super(fm);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int position) {
        // TODO Auto-generated method stub
        switch(position){
            case 0:{
                    return AppManagerFragment.newInstance(position);
                }
            case 1:{
                    return AppManagerFragment.newInstance(position);
                }
        }
        return null;
    }


    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}

