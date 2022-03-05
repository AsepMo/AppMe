package com.appme.story.application;

import android.annotation.TargetApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.support.v4.content.ContextCompat;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.TextView;

import com.appme.story.R;
import com.appme.story.engine.Api;
import com.appme.story.engine.app.commons.activity.ActionBarActivity;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.engine.app.fragments.ApplicationFragment;
import com.appme.story.engine.graphics.SystemBarTintManager;
import com.appme.story.receiver.SendBroadcast;
import com.appme.story.settings.theme.Theme;
import com.appme.story.settings.theme.ThemePreference;
import com.appme.story.settings.theme.ThemePreferenceFragment;

public class ApplicationMonitor extends ActionBarActivity {

    public static String TAG = ApplicationMonitor.class.getSimpleName();
    private Toolbar mToolbar;
    private SharedPreferences sharedPreferences;
    private Drawable icon = null;
    public final static String BASE = "com.appme.story";
    public final static String ACTION_MONITOR_ACTIVITY = BASE + ".application.MONITOR_ACTIVITY";
    public final static String ACTION_MONITOR_RECEIVER = BASE + ".application.MONITOR_RECEIVER";
    public final static String ACTION_MONITOR_PACKAGE_RECEIVER = BASE + ".application.MONITOR_PACKAGE_RECEIVER";
    public final static String ACTION_MONITOR_SCREEN = BASE + ".application.MONITOR_SCREEN";
    public final static String ACTION_MONITOR_SDCARD = BASE + ".application.MONITOR_SDCARD";
    public final static String ACTION_MONITOR_MEMORY = BASE + ".application.MONITOR_MEMORY";
    public final static String ACTION_MONITOR_LOGGER = BASE + ".application.MONITOR_LOGGER";

    //APP SERVER
    public final static String ACTION_MESSAGE_SERVER = BASE + ".application.MESSAGE_SERVER";
    public final static String ACTION_CAMERA_SERVER = BASE + ".application.CAMERA_SERVER";
    public final static String ACTION_SCREEN_SERVER = BASE +  ".application.SCREEN_SERVER";
    public final static String ACTION_FILE_SERVER = BASE + ".application.FILE_SERVER";
    public final static String ACTION_WEB_SERVER = BASE + ".application.WEB_SERVER";
    public final static String ACTION_RC_SERVER = BASE + ".application.RC_SERVER";
    public final static String ACTION_LOGCAT_SERVER = BASE + ".application.LOGCAT_SERVER";

    public final static String ACTION_MESSAGE_CLIENT = BASE + ".application.MESSAGE_CLIENT";
    public final static String ACTION_CAMERA_CLIENT = BASE + ".application.CAMERA_CLIENT";
    public final static String ACTION_SCREEN_CLIENT = BASE + ".application.SCREEN_CLIENT";
    public final static String ACTION_FILE_CLIENT = BASE + ".application.FILE_CLIENT";
    public final static String ACTION_WEB_CLIENT = BASE + ".application.WEB_CLIENT";
    public final static String ACTION_RC_CLIENT = BASE + ".application.RC_CLIENT";
    public final static String ACTION_LOGCAT_CLIENT = BASE + ".application.LOGCAT_CLIENT";

    public final static String EXTRA_TYPE = "EXTRA_TYPE";
    public final static String EXTRA_PATH = "EXTRA_PATH";

    public static void start(Context c, String action) {
        Intent mIntent = new Intent(c, ApplicationMonitor.class);
        mIntent.setAction(action);
        c.startActivity(mIntent);
    }

    public static void startOutPutRecorder(Context c, String path) {
        Intent mIntent = new Intent(c, ApplicationMonitor.class);
        mIntent.setAction(ApplicationMonitor.ACTION_MONITOR_SCREEN);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra(ApplicationMonitor.EXTRA_TYPE, SendBroadcast.TYPE.SCREEN_RECORD);
        mIntent.putExtra(ApplicationMonitor.EXTRA_TYPE, path);
        c.startActivity(mIntent);
    }
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        setUpStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_info_details);
        sharedPreferences = getSharedPreferences(Theme.THEME_PREFERENCES, Context.MODE_PRIVATE);
        
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        if (sharedPreferences.getString(Theme.THEME_SAVED, Theme.LIGHTTHEME).equals(Theme.LIGHTTHEME)) {
            icon = AppCompatResources.getDrawable(this, R.drawable.abc_ic_ab_back_material);     
            icon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        } else {
            icon = AppCompatResources.getDrawable(this, R.drawable.abc_ic_ab_back_material);     
            icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);          
        }
        mToolbar.setNavigationIcon(icon);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    switchActivity(ApplicationMonitor.this,  ApplicationActivity.class);
                }
            });
        if (mToolbar == null) {
            getSupportActionBar().setTitle(null);
            setSupportActionBar(mToolbar);     
        }
        
        final TextView mAppName = (TextView) findViewById(R.id.app_title);
        mAppName.setText(getString(R.string.app_name));
        String action = getIntent().getAction();
        String title = "";
        String subTitle = "";
        if (action != null && action.equals(ACTION_MONITOR_ACTIVITY)) {
            title = "Monitor ";
            subTitle = "Memory";
            switchFragment(ApplicationFragment.newInstance(title + subTitle)); 
        } else if (action != null && action.equals(ACTION_MONITOR_RECEIVER)) {
            title = "Monitor ";
            subTitle = "Memory";
            switchFragment(ApplicationFragment.newInstance(title + subTitle)); 
        } else if (action != null && action.equals(ACTION_MONITOR_PACKAGE_RECEIVER)) {
            title = "Monitor";
            subTitle = "Package Receiver";
            //  switchFragment(MonitorReceiverFragment.newInstance("Monitor Receiver"));
        } else if (action != null && action.equals(ACTION_MONITOR_SCREEN)) {
            title = "Monitor ";
            subTitle = "Memory";
            
            String type = getIntent().getStringExtra(EXTRA_TYPE);
            String path = getIntent().getStringExtra(EXTRA_PATH);
            switchFragment(ApplicationFragment.newInstance(title + subTitle)); 
        } else if (action != null && action.equals(ACTION_MONITOR_SDCARD)) {
            title = "Monitor ";
            subTitle = "Memory";
            switchFragment(ApplicationFragment.newInstance(title + subTitle)); 
        } else if (action != null && action.equals(ACTION_MONITOR_MEMORY)) {
            title = "Monitor ";
            subTitle = "Memory";
            switchFragment(ApplicationFragment.newInstance(title + subTitle)); 
        } else if (action != null && action.equals(ACTION_MESSAGE_SERVER)) {
            title = "App Server";
            subTitle = "Message Server";
            // switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_CAMERA_SERVER)) {
            title = "App Server";
            subTitle = "Camera Server";
            // switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_SCREEN_SERVER)) {
            title = "App Server";
            subTitle = "Screen Server";
            //switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_FILE_SERVER)) {
            title = "App Server";
            subTitle = "File Server";
            // switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_WEB_SERVER)) {
            title = "App Server";
            subTitle = "Web Server";
            // switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_RC_SERVER)) {
            title = "App Server";
            subTitle = "Remote Server";
            //switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_LOGCAT_SERVER)) {
            title = "App Server";
            subTitle = "Logcat Server";
            //switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_MESSAGE_CLIENT)) {
            title = "App Client";
            subTitle = "Message Client";
            // switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
        } else if (action != null && action.equals(ACTION_CAMERA_CLIENT)) {
            title = "App Client";
            subTitle = "Camera Client";
            //switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
        } else if (action != null && action.equals(ACTION_SCREEN_CLIENT)) {
            title = "App Client";
            subTitle = "Screen Client";
            //switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
        } else if (action != null && action.equals(ACTION_FILE_CLIENT)) {
            title = "App Client";
            subTitle = "File Client";
            //switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
        } else if (action != null && action.equals(ACTION_WEB_CLIENT)) {
            title = "App Client";
            subTitle = "Web Client";
            //  switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
        } else if (action != null && action.equals(ACTION_RC_CLIENT)) {
            title = "App Client";
            subTitle = "Remote Client";
            //switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
        } else if (action != null && action.equals(ACTION_LOGCAT_CLIENT)) {
            title = "App Client";
            subTitle = "Logcat Client";
            //switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
        }
 
        changeActionBarColor();
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

        setUpStatusBar();
    }
    
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        switchActivity(ApplicationMonitor.this, ApplicationActivity.class);
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

