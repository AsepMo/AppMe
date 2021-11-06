package com.github.template.application;

import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.ArrayList;
import java.lang.ref.WeakReference;


import com.github.template.R;
import com.github.template.engine.app.settings.Settings;
import com.github.template.engine.app.adapters.ScreenTabAdapter;
import com.github.template.engine.app.adapters.DrawerAdapter;
import com.github.template.engine.app.models.DrawerItem;
import com.github.template.engine.app.models.SimpleItem;
import com.github.template.engine.app.models.SpaceItem;
import com.github.template.engine.app.fragments.AppInfoFragment;
import com.github.template.engine.app.fragments.AppManagerFragment;
import com.github.template.engine.app.fragments.AppMonitorFragment;
import com.github.template.engine.app.fragments.AppCleanerFragment;
import com.github.template.engine.app.fragments.AppClientFragment;
import com.github.template.engine.app.fragments.AppServerFragment;
import com.github.template.engine.app.fragments.AppMoreFragment;
import com.github.template.engine.widget.SlidingRootNav;
import com.github.template.engine.widget.SlidingRootNavBuilder;

public class ApplicationActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener, View.OnClickListener {

    public static String TAG = ApplicationActivity.class.getSimpleName();

    public static void start(Context c) {
        Intent mIntent = new Intent(c, ApplicationActivity.class);
        c.startActivity(mIntent);
    }

    private static final int POS_APP_INFO = 0;
    private static final int POS_APP_MANAGER = 1;
    private static final int POS_APP_MONITOR = 2;
    private static final int POS_APP_CLEANER = 3;
    private static final int POS_APP_CLIENT = 5;
    private static final int POS_APP_SERVER = 6;
    private static final int POS_MORE_APPS = 7;

    public static final boolean DEBUG = false;
    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme_NoActionBar);
        // get default preferences at start - we need this for setting the theme
        Settings.updatePreferences(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
            .withToolbarMenuToggle(mToolbar)
            .withMenuOpened(false)
            .withContentClickableWhenMenuOpened(false)
            .withSavedState(savedInstanceState)
            .withMenuLayout(R.layout.menu_left_drawer)
            .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                                                      createItemFor(POS_APP_INFO).setChecked(true),
                                                      createItemFor(POS_APP_MANAGER),
                                                      createItemFor(POS_APP_MONITOR),
                                                      createItemFor(POS_APP_CLEANER),
                                                      new SpaceItem(48),
                                                      createItemFor(POS_APP_CLIENT),
                                                      createItemFor(POS_APP_SERVER),
                                                      createItemFor(POS_MORE_APPS)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        findViewById(R.id.about_layout).setOnClickListener(this);
        findViewById(R.id.action_profile).setOnClickListener(this);       
        findViewById(R.id.settings_layout).setOnClickListener(this);
        findViewById(R.id.exit_layout).setOnClickListener(this);

        adapter.setSelected(POS_APP_INFO);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.v(TAG, "onResume:");  
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (DEBUG) Log.v(TAG, "onPause:");
    }

    @Override
    public void onItemSelected(int position) {

        if (position == POS_APP_INFO) {
            switchFragment(AppInfoFragment.newInstance(0));
        }
        if (position == POS_APP_MANAGER) {
            switchFragment(AppManagerFragment.newInstance(0));
        }
        if (position == POS_APP_MONITOR) {
            switchFragment(AppMonitorFragment.newInstance(0));
        }
        if (position == POS_APP_CLEANER) {
            switchFragment(new AppCleanerFragment());
        }
        if (position == POS_APP_CLIENT) {
            switchFragment(new AppClientFragment());
        }
        if (position == POS_APP_SERVER) {
            switchFragment(new AppServerFragment());
        }
        if (position == POS_MORE_APPS) {
            switchFragment(new AppMoreFragment());
        }
        slidingRootNav.closeMenu();

    }

    @Override
    public void onClick(View v) {
        String title = "";
        switch (v.getId()) {
            case R.id.about_layout:
                title = "About";
                break;
            case R.id.settings_layout:
                title = "Settings";
                SettingsActivity.start(ApplicationActivity.this);
                break;
            case R.id.action_profile:
                title = "Profile";
                break; 
            case R.id.exit_layout:
                title = "Exit";
                finish();
                break;

        }
        slidingRootNav.closeMenu();     
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
            .withIconTint(color(R.color.textColorSecondary))
            .withTextTint(color(R.color.textColorPrimary))
            .withSelectedIconTint(color(R.color.selected_color_tint))
            .withSelectedTextTint(color(R.color.selected_color_tint));
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
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    } 
}
