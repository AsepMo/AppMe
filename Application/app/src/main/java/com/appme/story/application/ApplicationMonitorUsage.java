package com.appme.story.application;

import android.annotation.TargetApi;
import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;
import android.transition.Fade;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.appme.story.R;
import com.appme.story.engine.Api;
import com.appme.story.engine.app.commons.activity.ActionBarActivity;
import com.appme.story.engine.app.commons.db.DbIgnoreExecutor;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.engine.app.utils.MonitorUsageUtil;
import com.appme.story.engine.app.utils.PreferenceManager;
import com.appme.story.engine.app.models.AppItem;
import com.appme.story.engine.app.fragments.MonitorActivityFragment;
import com.appme.story.engine.graphics.SystemBarTintManager;
import com.appme.story.service.AlarmService;
import com.appme.story.service.MonitorUsageService;
import com.appme.story.settings.theme.Theme;
import com.appme.story.settings.theme.ThemePreference;


public class ApplicationMonitorUsage extends ActionBarActivity {

    public static String TAG = ApplicationMonitorUsage.class.getSimpleName();
    
    private MonitorActivityFragment mMonitorActivity;
    private Toolbar mToolbar;
       
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        setUpDefaultStatusBar(); 
       super.onCreate(savedInstanceState);

        // https://guides.codepath.com/android/Shared-Element-Activity-Transition
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Fade(Fade.OUT));
        setContentView(R.layout.activity_monitor_app_usage);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    finish();       
                }
            });
        if (mToolbar == null) {
            ActionBar mActionBar = getSupportActionBar();
            mActionBar.setTitle(null);
            setSupportActionBar(mToolbar);
        }
        TextView mToolbarTitle = (TextView)findViewById(R.id.app_title);
        mToolbarTitle.setText(R.string.app_name);
        mMonitorActivity = MonitorActivityFragment.newInstance("Monitor Activity");
        switchFragment(mMonitorActivity);
        changeActionBarColor();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mMonitorActivity.onNewIntent(intent);
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

        setUpDefaultStatusBar();
    }
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setUpStatusBar() {
        int color = ScreenUtils.getStatusBarColor(ThemePreference.getPrimaryColor());
        if (Api.hasLollipop()) {
            getWindow().setStatusBarColor(color);
        } else if (Api.hasKitKat()) {
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setTintColor(color);
            systemBarTintManager.setStatusBarTintEnabled(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setUpDefaultStatusBar() {
        int color = ContextCompat.getColor(this, android.R.color.black);
        if (Api.hasLollipop()) {
            getWindow().setStatusBarColor(color);
        } else if (Api.hasKitKat()) {
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setTintColor(ScreenUtils.getStatusBarColor(color));
            systemBarTintManager.setStatusBarTintEnabled(true);
        }
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
