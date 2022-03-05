package com.appme.story.engine.app.fragments;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.Arrays;
import java.util.ArrayList;

import com.appme.story.R;
import com.appme.story.application.Application;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.application.ApplicationPreferences;
import com.appme.story.engine.app.adapters.DrawerAdapter;
import com.appme.story.engine.app.models.DrawerItem;
import com.appme.story.engine.app.models.SimpleItem;
import com.appme.story.engine.app.models.SpaceItem;
import com.appme.story.engine.app.listeners.OnRecyclerScrollListener;
import com.appme.story.settings.theme.ThemePreference;

public class NavigationDrawerFragment extends Fragment implements DrawerAdapter.OnItemSelectedListener, View.OnClickListener  {

    private static final String TAG = NavigationDrawerFragment.class.getSimpleName();
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_NAV_DRAWER_MESSAGE = "ARG_NAV_DRAWER_MESSAGE";
    //private String message;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NavigationDrawerFragment newInstance(int number) {
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NAV_DRAWER_MESSAGE, number);
        Log.d(TAG, "newInstance: id = " + number);
        fragment.setArguments(args);
        return fragment;
    }

    public NavigationDrawerFragment() {
    }
    
    private ApplicationActivity mActivity;
    private Context mContext;
    public static final int POS_APP_INFO = 0;
    public static final int POS_APP_MANAGER = 1;
    public static final int POS_APP_MONITOR = 2;
    public static final int POS_APP_CLEANER = 3;
    public static final int POS_APP_CLIENT = 5;
    public static final int POS_APP_SERVER = 6;
    public static final int POS_APP_MORE = 7;

    public static final boolean DEBUG = false;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private DrawerAdapter mDrawerAdapter;
    private RelativeLayout mHeaderDrawer;
    private RelativeLayout mFooterDrawer;
    private RecyclerView mDrawerListMenu;
    private OnRecyclerScrollListener mOnRecyclerScrollListener;
    private View mFooter;
    private OnNavigationDrawerListener mOnNavigationDrawerListener;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Log.d(TAG, "onCreateView: onCreateView, id = " + message);
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = (ApplicationActivity)getActivity();
        mContext = getActivity();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        mDrawerAdapter = new DrawerAdapter(Arrays.asList(
                                               createItemFor(POS_APP_INFO).setChecked(true),
                                               createItemFor(POS_APP_MANAGER),
                                               createItemFor(POS_APP_MONITOR),
                                               createItemFor(POS_APP_CLEANER),
                                               new SpaceItem(48),
                                               createItemFor(POS_APP_CLIENT),
                                               createItemFor(POS_APP_SERVER),
                                               createItemFor(POS_APP_MORE)));
        mDrawerAdapter.setListener(this);
        
        mHeaderDrawer = (RelativeLayout) view.findViewById(R.id.header_drawer);      
        mFooterDrawer = (RelativeLayout) view.findViewById(R.id.footer_drawer);
        
        mDrawerListMenu = (RecyclerView)view.findViewById(R.id.menu_list_drawer_recycler);
        mDrawerListMenu.setNestedScrollingEnabled(false);
        mDrawerListMenu.setLayoutManager(new LinearLayoutManager(mContext));
        mDrawerListMenu.setAdapter(mDrawerAdapter);
        mFooter = view.findViewById(R.id.footer_layout);
        mOnRecyclerScrollListener = new OnRecyclerScrollListener() {
            @Override
            public void show() {

                mFooter.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//                mFooter.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }

            @Override
            public void hide() {

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFooter.getLayoutParams();
                int fabMargin = lp.bottomMargin;
                mFooter.animate().translationY(mFooter.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
            }
        };
        mDrawerListMenu.addOnScrollListener(mOnRecyclerScrollListener);
        
        view.findViewById(R.id.menu_drawer_about).setOnClickListener(this);
        view.findViewById(R.id.menu_drawer_profile).setOnClickListener(this);       
        view.findViewById(R.id.menu_drawer_settings).setOnClickListener(this);
        view.findViewById(R.id.menu_drawer_exit).setOnClickListener(this);
       
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
        if (!mFromSavedInstanceState) { 
             setSelected(mCurrentSelectedPosition); 
         } 
            
        mDrawerAdapter.setSelected(POS_APP_INFO);
        changeColor();
    }

    private Drawable oldBackground;
    private void changeColor() {

        int color = ThemePreference.getPrimaryColor();
        Drawable colorDrawable = new ColorDrawable(color);

        if (oldBackground == null) {
            mHeaderDrawer.setBackgroundDrawable(colorDrawable);
            mFooterDrawer.setBackgroundDrawable(colorDrawable);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, colorDrawable });
            mHeaderDrawer.setBackgroundDrawable(td);   
            mFooterDrawer.setBackgroundDrawable(td);   
            td.startTransition(200);
        }

        oldBackground = colorDrawable;
    }
   
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onItemSelected(int position) {
        if (mOnNavigationDrawerListener != null) {
            mOnNavigationDrawerListener.onItemSelected(position);
        }
    }

    @Override
    public void onClick(View v) {
        String title = "";
        switch (v.getId()) {
            case R.id.menu_drawer_about:
                title = "About";
                mActivity.getAboutPage();
                break;
            case R.id.menu_drawer_settings:
                title = "Settings";
                mActivity.getAppPreference();
                break;
            case R.id.menu_drawer_profile:
                title = "Folder In Storage";
                mActivity.getFolderStructure();
                break; 
            case R.id.menu_drawer_exit:
                title = "Exit";
                mActivity.getExit();
                break;

        }

        //Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
    }

    public void setSelected(int position) {
        this.mCurrentSelectedPosition = position;
    }

    public void setOnNavigationDrawerListener(OnNavigationDrawerListener mListener) {
        this.mOnNavigationDrawerListener = mListener;
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
            .withIconTint(color(R.color.icons))
            .withTextTint(color(R.color.text))
            .withSelectedIconTint(color(R.color.app_selected_color_tint))
            .withSelectedTextTint(color(R.color.app_selected_color_tint));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(mContext, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(mContext, res);
    }

    public interface OnNavigationDrawerListener {
        void onItemSelected(int position);
    }
}
