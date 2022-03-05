package com.appme.story.engine.app.fragments;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appme.story.R;
import com.appme.story.engine.app.models.AppCleanerItem;
import com.appme.story.engine.app.adapters.AppCleanerAdapter;
import com.appme.story.engine.app.utils.AppInfoUtils;
import com.appme.story.engine.widget.DividerDecoration;
import com.appme.story.engine.widget.RecyclerView;
import com.appme.story.service.AppCleanerService;
import com.appme.story.settings.theme.ThemePreference;

import java.util.List;

public class AppCleanerFragment extends Fragment implements AppCleanerService.OnActionListener {

    private static final int REQUEST_STORAGE = 0;

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    
    private static final String EXTRA_TEXT = "text";
    private AppCompatActivity mActivity;
    private Context mContext;
    private Toolbar mToolbar;
    
    private AppCleanerService mCleanerService;
    private AppCleanerAdapter mAppsListAdapter;
    private TextView mEmptyView;
    private SharedPreferences mSharedPreferences;
    private ProgressDialog mProgressDialog;
    private View mProgressBarLayout;
    private ImageView mProgressBar;
    
    private TextView mProgressBarText;
    private LinearLayoutManager mLayoutManager;
    private Menu mOptionsMenu;

    private boolean mAlreadyScanned = false;
    private boolean mAlreadyCleaned = false;
    private String mSearchQuery;

    private String mSortByKey;
    private String mCleanOnAppStartupKey;
    private String mExitAfterCleanKey;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCleanerService = ((AppCleanerService.CleanerServiceBinder) service).getService();
            mCleanerService.setOnActionListener(AppCleanerFragment.this);

            updateStorageUsage();

            if (!mCleanerService.isCleaning() && !mCleanerService.isScanning()) {
                if (mSharedPreferences.getBoolean(mCleanOnAppStartupKey, false) &&
                        !mAlreadyCleaned) {
                    mAlreadyCleaned = true;

                    cleanCache();
                } else if (!mAlreadyScanned) {
                    mCleanerService.scanCache();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mCleanerService.setOnActionListener(null);
            mCleanerService = null;
        }
    };

    public static AppCleanerFragment newInstance(String text) {
        AppCleanerFragment fragment = new AppCleanerFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        mSortByKey = getString(R.string.app_sort_by_key);
        mCleanOnAppStartupKey = getString(R.string.app_clean_on_startup_key);
        mExitAfterCleanKey = getString(R.string.app_exit_after_clean_key);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mAppsListAdapter = new AppCleanerAdapter();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setTitle(R.string.app_cleaning_cache);
        mProgressDialog.setMessage(getString(R.string.app_cleaning_in_progress));
        mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });

        getActivity().getApplication().bindService(new Intent(getActivity(), AppCleanerService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_app_cleaner, container, false);
        final String text = getArguments().getString(EXTRA_TEXT);
        mActivity = (AppCompatActivity)getActivity();
        mContext = getActivity();
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mActivity.setSupportActionBar(mToolbar);
            final ActionBar actionbar = getSupportActionBar();
            actionbar.setTitle(text);            
        }
        changeActionBarColor();
        mEmptyView = (TextView) rootView.findViewById(R.id.empty_view);

        mLayoutManager = new LinearLayoutManager(getActivity());

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAppsListAdapter);
        recyclerView.setEmptyView(mEmptyView);
        recyclerView.addItemDecoration(new DividerDecoration(getActivity()));

        mProgressBarLayout = rootView.findViewById(R.id.progress_bar_layout);
        mProgressBar = (ImageView)rootView.findViewById(R.id.progressBar);
        AppInfoUtils.setProgressVisibility(mProgressBar, true);
        mProgressBarText = (TextView) rootView.findViewById(R.id.progressBarText);

        return rootView;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mOptionsMenu = menu;

        inflater.inflate(R.menu.menu_application_cleaner, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchQuery = query;

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (ViewCompat.isLaidOut(searchView) && mSearchQuery != null) {
                    String oldText = mSearchQuery;

                    mSearchQuery = newText;

                    if (!oldText.equals(newText)) {
                        mAppsListAdapter.sortAndFilter(getActivity(), getSortBy(), newText);
                    }
                }

                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        if (mSearchQuery == null) {
                            mSearchQuery = "";
                        }

                        mAppsListAdapter.setShowHeaderView(false);

                        mEmptyView.setText(R.string.app_no_such_application);

                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mSearchQuery = null;

                        mAppsListAdapter.clearFilter();

                        mAppsListAdapter.setShowHeaderView(true);

                        if (mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                            mLayoutManager.scrollToPosition(0);
                        }

                        mEmptyView.setText(R.string.app_empty_cache);

                        return true;
                    }
                });

        if (mSearchQuery != null) {
            MenuItemCompat.expandActionView(searchItem);

            searchView.setQuery(mSearchQuery, false);
        }

        updateOptionsMenu();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clean:
                if (mCleanerService != null && !mCleanerService.isScanning() &&
                        !mCleanerService.isCleaning() && mCleanerService.getCacheSize() > 0) {
                    mAlreadyCleaned = false;

                    cleanCache();
                }
                return true;

            case R.id.action_refresh:
                if (mCleanerService != null && !mCleanerService.isScanning() &&
                        !mCleanerService.isCleaning()) {
                    mCleanerService.scanCache();
                }
                return true;

            case R.id.action_sort_by_app_name:
                setSortBy(AppCleanerAdapter.SortBy.APP_NAME);
                updateOptionsMenu();
                return true;

            case R.id.action_sort_by_cache_size:
                setSortBy(AppCleanerAdapter.SortBy.CACHE_SIZE);
                updateOptionsMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        updateOptionsMenu();
    }

    @Override
    public void onDestroyOptionsMenu() {
        mOptionsMenu = null;
    }

    @Override
    public void onResume() {
        updateStorageUsage();

        updateOptionsMenu();

        if (mCleanerService != null) {
            if (mCleanerService.isScanning() && !isProgressBarVisible()) {
                showProgressBar(true);
            } else if (!mCleanerService.isScanning() && isProgressBarVisible()) {
                showProgressBar(false);
            }

            if (mCleanerService.isCleaning() && !mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        getActivity().getApplication().unbindService(mServiceConnection);

        super.onDestroy();
    }

    private void updateOptionsMenu() {
        if (mOptionsMenu != null) {
            mOptionsMenu.findItem(R.id.action_sort_by_app_name).setVisible(
                    getSortBy() == AppCleanerAdapter.SortBy.CACHE_SIZE);
            mOptionsMenu.findItem(R.id.action_sort_by_cache_size).setVisible(
                    getSortBy() == AppCleanerAdapter.SortBy.APP_NAME);
        }
    }

    private void updateStorageUsage() {
        if (mAppsListAdapter != null) {
            StatFs stat = new StatFs(Environment.getDataDirectory().getAbsolutePath());

            long totalMemory = (long) stat.getBlockCount() * (long) stat.getBlockSize();
            long medMemory = mCleanerService != null ? mCleanerService.getCacheSize() : 0;
            long lowMemory = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
                    !Environment.isExternalStorageEmulated()) {
                stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

                totalMemory += (long) stat.getBlockCount() * (long) stat.getBlockSize();
                lowMemory += (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }

            long highMemory = totalMemory - medMemory - lowMemory;

            mAppsListAdapter.updateStorageUsage(totalMemory, lowMemory, medMemory, highMemory);
        }
    }

    private AppCleanerAdapter.SortBy getSortBy() {
        try {
            return AppCleanerAdapter.SortBy.valueOf(mSharedPreferences.getString(mSortByKey,
                    AppCleanerAdapter.SortBy.CACHE_SIZE.toString()));
        } catch (ClassCastException e) {
            return AppCleanerAdapter.SortBy.CACHE_SIZE;
        }
    }

    private void setSortBy(AppCleanerAdapter.SortBy sortBy) {
        mSharedPreferences.edit().putString(mSortByKey, sortBy.toString()).apply();

        if (mCleanerService != null && !mCleanerService.isScanning() &&
                !mCleanerService.isCleaning()) {
            mAppsListAdapter.sortAndFilter(getActivity(), sortBy, mSearchQuery);
        }
    }

    private boolean isProgressBarVisible() {
        return mProgressBarLayout.getVisibility() == View.VISIBLE;
    }

    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBarLayout.setVisibility(View.VISIBLE);
        } else {
            mProgressBarLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_out));
            mProgressBarLayout.setVisibility(View.GONE);
        }
    }

    private void showStorageRationale() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.setTitle(R.string.app_rationale_title);
        dialog.setMessage(getString(R.string.app_rationale_storage));
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }

    private void cleanCache() {
        if (!AppCleanerService.canCleanExternalCache(getActivity())) {
            if (shouldShowRequestPermissionRationale(PERMISSIONS_STORAGE[0])) {
                showStorageRationale();
            } else {
                requestPermissions(PERMISSIONS_STORAGE, REQUEST_STORAGE);
            }
        } else {
            mCleanerService.cleanCache();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCleanerService.cleanCache();
            } else {
                showStorageRationale();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onScanStarted(Context context) {
        if (isAdded()) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            mProgressBarText.setText(R.string.action_scanning);
            showProgressBar(true);
        }
    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {
        if (isAdded()) {
            mProgressBarText.setText(getString(R.string.action_scanning_m_of_n, current, max));
        }
    }

    @Override
    public void onScanCompleted(Context context, List<AppCleanerItem> apps) {
        mAppsListAdapter.setItems(getActivity(), apps, getSortBy(), mSearchQuery);

        if (isAdded()) {
            updateStorageUsage();

            showProgressBar(false);
        }

        mAlreadyScanned = true;
    }

    @Override
    public void onCleanStarted(Context context) {
        if (isAdded()) {
            if (isProgressBarVisible()) {
                showProgressBar(false);
            }

            if (!getActivity().isFinishing()) {
                mProgressDialog.show();
            }
        }
    }

    @Override
    public void onCleanCompleted(Context context, boolean succeeded) {
        if (succeeded) {
            mAppsListAdapter.trashItems();
        }

        if (isAdded()) {
            updateStorageUsage();

            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        Toast.makeText(context, succeeded ? R.string.app_cleaned : R.string.app_toast_could_not_clean,
                Toast.LENGTH_LONG).show();

        if (succeeded && getActivity() != null && !mAlreadyCleaned &&
                mSharedPreferences.getBoolean(mExitAfterCleanKey, false)) {
            getActivity().finish();
        }
    }
}

