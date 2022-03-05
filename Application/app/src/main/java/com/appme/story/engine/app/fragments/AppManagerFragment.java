package com.appme.story.engine.app.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import com.appme.story.R;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.app.commons.fragments.BaseNavPagerFragment;
import com.appme.story.engine.app.commons.fragments.BaseNavigationFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppManagerFragment extends BaseNavPagerFragment {

    private static final String EXTRA_TEXT = "text";
  
    private ApplicationActivity mActivity;
    public static BaseNavigationFragment newInstance(String text) {
        BaseNavigationFragment fragment = new AppManagerFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }


    public AppManagerFragment() {
        // Required empty public constructor
    }

    private String message;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // override the method
        message = getArguments().getString(EXTRA_TEXT);
        mActivity = (ApplicationActivity)getActivity();
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle(message);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("ApkMe")
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item){
                    mActivity.getApkBackup();
                    return true;
                }
            }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }
    
    @Override
    protected String[] getTitles() {
        return new String[]{"Installed", "System"};
    }

    @Override
    protected Fragment getFragment(int position) {
        String title = getTitles()[position];
        Fragment fragment = null;
        if (title.equals("Installed")) {
            fragment = ManagerFragment.newInstance(0);
        } else if (title.equals("System")) {
            fragment = ManagerFragment.newInstance(1);
        } 
        return fragment;
    }


}
