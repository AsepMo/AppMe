package com.appme.story.engine.app.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.appme.story.R;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.app.commons.abouts.AboutPageLayout;
import com.appme.story.settings.theme.ThemePreference;

public class AppAboutFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";
    private ApplicationActivity mActivity;
    private Context mContext;
    private Toolbar mToolbar;
    
    public static AppAboutFragment newInstance(String text) {
        AppAboutFragment fragment = new AppAboutFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }
    
    private AboutPageLayout mAbout;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_about, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final String text = getArguments().getString(EXTRA_TEXT);
        mActivity = (ApplicationActivity)getActivity();
        mContext = getActivity();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mActivity.setSupportActionBar(mToolbar);
            final ActionBar actionbar = getSupportActionBar();
            actionbar.setTitle(text); 
        }
        changeActionBarColor();
        mAbout = (AboutPageLayout)view.findViewById(R.id.app_about);
        
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_application, menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_about:
                return true;
            case R.id.action_settings:
                mActivity.getAppPreference();
                return true;    
            case R.id.action_exit:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
    
    public ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }
    
    private Drawable oldBackground;
    private void changeActionBarColor() {

        int color = ThemePreference.getPrimaryColor();
        Drawable colorDrawable = new ColorDrawable(color);

        if (oldBackground == null) {
            mToolbar.setBackgroundDrawable(colorDrawable);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, colorDrawable });
            mToolbar.setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = colorDrawable;
    }
    
}


