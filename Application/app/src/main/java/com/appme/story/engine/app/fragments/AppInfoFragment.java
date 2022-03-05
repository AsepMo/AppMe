package com.appme.story.engine.app.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.app.Activity;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Handler;
import android.text.Spannable;
import android.text.format.Formatter;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.ImageView;

import static android.content.pm.PackageManager.GET_SIGNATURES;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.appme.story.R;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.application.ApplicationInfoActivity;
import com.appme.story.engine.app.models.AppInfoItem;
import com.appme.story.engine.app.adapters.AppInfoAdapter;
import com.appme.story.engine.app.tasks.AppInfoTask;
import com.appme.story.engine.app.utils.AppInfoUtils;
import com.appme.story.settings.theme.ThemePreference;

public class AppInfoFragment extends Fragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<List<AppInfoItem>> {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static final String TAG = AppInfoFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    private static final String EXTRA_TEXT = "text";
    private ApplicationActivity mActivity;
    private Context mContext;
    private Toolbar mToolbar;

    public static AppInfoFragment newInstance(String text) {
        AppInfoFragment fragment = new AppInfoFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    

    public AppInfoFragment() {
    }

    private String message;
    private ListView mListView;
    private View mProgressBarLayout;
    private ImageView mProgressBar;
    private static Collator sCollator = Collator.getInstance();

    private static final int[] sSortMenuItemIdsMap = {R.id.action_sort_domain,
            R.id.action_sort_name,R.id.action_sort_pkg,
            R.id.action_sort_installation,R.id.action_sort_sharedid,
            R.id.action_sort_size,R.id.action_sort_sha};

    private static final int SORT_DOMAIN = 0;
    private static final int SORT_NAME = 1;
    private static final int SORT_PKG = 2;
    private static final int SORT_INSTALLATION = 3;
    private static final int SORT_SHAREDID =4;
    private static final int SORT_SIZE = 5;
    private static final int SORT_SHA = 6;
    public static final String INSTANCE_STATE_SORT_BY = "sort_by";

    private AppInfoAdapter mAdapter;
    private List<AppInfoItem> mItemList = new ArrayList<>();
    private int mItemSizeRetrievedCount;
    private int mSortBy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        message = getArguments().getString(EXTRA_TEXT);
        
        mActivity = (ApplicationActivity)getActivity();      
       /* ActionBar actionBar = mActivity.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);

        SearchView searchView = new SearchView(actionBar.getThemedContext());
        searchView.setOnQueryTextListener(this);
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,                                                                         ViewGroup.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(searchView, layoutParams);*/
        
        if (savedInstanceState != null) {
            mSortBy = savedInstanceState.getInt(INSTANCE_STATE_SORT_BY);
        }else mSortBy=SORT_NAME;
        
        
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        return inflater.inflate(R.layout.fragment_app_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mActivity = (ApplicationActivity)getActivity();
        mContext = getActivity();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mActivity.setSupportActionBar(mToolbar);
            final ActionBar actionbar = getSupportActionBar();
            actionbar.setTitle(message); 
        }
        changeActionBarColor();
        
        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout);
        mProgressBar = (ImageView)view.findViewById(R.id.progressBar);
        AppInfoUtils.setProgressVisibility(mProgressBar, true);
        
        mListView = (ListView)view.findViewById(android.R.id.list);
        mAdapter = new AppInfoAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setFastScrollEnabled(true);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        getLoaderManager().initLoader(0, null, this);
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
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_STATE_SORT_BY, mSortBy);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public Loader<List<AppInfoItem>> onCreateLoader(int pInt, Bundle pBundle) {
        showProgressBar(true);
        
        return new AppInfoTask(mActivity);
    }

    @Override
    public void onLoaderReset(Loader<List<AppInfoItem>> applicationItems) {
        mItemList = null;
        mAdapter.setDefaultList(null);
        
        showProgressBar(false);
    }

    @Override
    public void onLoadFinished(Loader<List<AppInfoItem>> loader, List<AppInfoItem> applicationItems) {
        mItemList = applicationItems;
        sortApplicationList();
        mAdapter.setDefaultList(mItemList);
        if (Build.VERSION.SDK_INT <26) startRetrievingPackagesSize();
        else {
            for (AppInfoItem item : mItemList) {
                item.size = (long) -1 * item.applicationInfo.targetSdkVersion;
                try {
                    item.sha = AppInfoUtils.apkPro(mActivity.getPackageManager().getPackageInfo(item.applicationInfo.packageName, GET_SIGNATURES));
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
        }
       
        showProgressBar(false);
    }
    
    
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
           
        Intent intent = new Intent(getActivity(), ApplicationInfoActivity.class);
        intent.putExtra(AppInfoDetailFragment.EXTRA_PACKAGE_NAME, mAdapter.getItem(i).applicationInfo.packageName);
        startActivity(intent);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_app_info, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(sSortMenuItemIdsMap[mSortBy]).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                getLoaderManager().restartLoader(0, null, this);
                return true;
            case R.id.action_sort_name:
                setSortBy(SORT_NAME);
                item.setChecked(true);
                return true;
            case R.id.action_sort_pkg:
                setSortBy(SORT_PKG);
                item.setChecked(true);
                return true;
            case R.id.action_sort_domain:
                setSortBy(SORT_DOMAIN);
                item.setChecked(true);
                return true;
            case R.id.action_sort_installation:
                setSortBy(SORT_INSTALLATION);
                item.setChecked(true);
                return true;
            case R.id.action_sort_sharedid:
                setSortBy(SORT_SHAREDID);
                item.setChecked(true);
                return true;
            case R.id.action_sort_sha:
                setSortBy(SORT_SHA);
                item.setChecked(true);
                return true;
            case R.id.action_sort_size:
                setSortBy(SORT_SIZE);
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sort main list if provided value is valid.
     *
     * @param sort Must be one of SORT_*
     */
    private void setSortBy(int sort) {
        mSortBy = sort;
        sortApplicationList();

        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();

        if (mListView != null)
            checkFastScroll();
    }

    private void checkFastScroll() {
        mListView.setFastScrollEnabled(mSortBy == SORT_NAME);
    }

    public void sortApplicationList() {
        Collections.sort(mItemList, new Comparator<AppInfoItem>() {
                @Override
                public int compare(AppInfoItem item1, AppInfoItem item2) {
                    switch (mSortBy) {
                        case SORT_NAME:
                            return sCollator.compare(item1.label, item2.label);
                        case SORT_PKG:
                            return item1.applicationInfo.packageName.compareTo(item2.applicationInfo.packageName);
                        case SORT_DOMAIN:
                            boolean isSystem1 = (item1.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                            boolean isSystem2 = (item2.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                            return AppInfoUtils.compareBooleans(isSystem1, isSystem2);
                        case SORT_INSTALLATION:
                            //Sort in decreasing order
                            return -item1.date.compareTo(item2.date);
                        case SORT_SIZE:
                            return -item1.size.compareTo(item2.size);
                        case SORT_SHAREDID:
                            return item2.applicationInfo.uid - item1.applicationInfo.uid;
                        case SORT_SHA:
                            try {
                                return item1.sha.compareTo(item2.sha);
                            } catch (NullPointerException e) {

                            }
                        default:
                            return 0;
                    }
                }
            });
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mAdapter.getFilter().filter(s);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    private void startRetrievingPackagesSize() {
        for (AppInfoItem item : mItemList)
            getItemSize(item);
    }

    private void getItemSize(final AppInfoItem item) {
        try {
            Method getPackageSizeInfo = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            getPackageSizeInfo.invoke(mActivity.getPackageManager(), item.applicationInfo.packageName, new IPackageStatsObserver.Stub() {
                    @Override
                    public void onGetStatsCompleted(final PackageStats pStats, final boolean succeeded)
                    throws RemoteException {
                        mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (succeeded)
                                        item.size = pStats.codeSize + pStats.cacheSize + pStats.dataSize
                                            + pStats.externalCodeSize + pStats.externalCacheSize + pStats.externalDataSize
                                            + pStats.externalMediaSize + pStats.externalObbSize;
                                    else
                                        item.size = -1L;
                                    try {
                                        item.sha = AppInfoUtils.apkPro(mActivity.getPackageManager().getPackageInfo(item.applicationInfo.packageName, GET_SIGNATURES));
                                    } catch (PackageManager.NameNotFoundException e) {
                                    }

                                    incrementItemSizeRetrievedCount();
                                }
                            });
                    }
                });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            incrementItemSizeRetrievedCount();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            incrementItemSizeRetrievedCount();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            incrementItemSizeRetrievedCount();
        }
    }

    private void incrementItemSizeRetrievedCount() {
        mItemSizeRetrievedCount++;

        if (mItemSizeRetrievedCount == mItemList.size())
            mAdapter.notifyDataSetChanged();
    }
   
    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBarLayout.setVisibility(View.VISIBLE);
        } else {
            mProgressBarLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mProgressBarLayout.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

}

