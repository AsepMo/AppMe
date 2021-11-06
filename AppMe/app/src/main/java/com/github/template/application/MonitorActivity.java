package com.github.template.application;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;

import com.github.template.R;
import com.github.template.AppController;
import com.github.template.receiver.SendBroadcast;
import com.github.template.engine.app.fragments.MonitorReceiverFragment;
import com.github.template.engine.app.fragments.MonitorActivityFragment;
import com.github.template.engine.app.fragments.MonitorScreenFragment;
import com.github.template.engine.app.fragments.MonitorMemoryFragment;
import com.github.template.engine.app.fragments.MonitorSdcardFragment;
import com.github.template.engine.app.fragments.MonitorLogFragment;
import com.github.template.engine.app.fragments.ScreenServerFragment;
import com.github.template.engine.app.fragments.ScreenClientFragment;

public class MonitorActivity extends AppCompatActivity {

    public static String TAG = MonitorActivity.class.getSimpleName();
    public final static String ACTION_MONITOR_ACTIVITY = "com.github.template.application.MONITOR_ACTIVITY";
    public final static String ACTION_MONITOR_RECEIVER = "com.github.template.application.MONITOR_RECEIVER";
    public final static String ACTION_MONITOR_PACKAGE_RECEIVER = "com.github.template.application.MONITOR_PACKAGE_RECEIVER";
    public final static String ACTION_MONITOR_SCREEN = "com.github.template.application.MONITOR_SCREEN";
    public final static String ACTION_MONITOR_SDCARD = "com.github.template.application.MONITOR_SDCARD";
    public final static String ACTION_MONITOR_MEMORY = "com.github.template.application.MONITOR_MEMORY";
    public final static String ACTION_MONITOR_LOGGER = "com.github.template.application.MONITOR_LOGGER";
   
    //APP SERVER
    public final static String ACTION_MESSAGE_SERVER = "com.github.template.application.MESSAGE_SERVER";
    public final static String ACTION_CAMERA_SERVER = "com.github.template.application.CAMERA_SERVER";
    public final static String ACTION_SCREEN_SERVER = "com.github.template.application.SCREEN_SERVER";
    public final static String ACTION_FILE_SERVER = "com.github.template.application.FILE_SERVER";
    public final static String ACTION_WEB_SERVER = "com.github.template.application.WEB_SERVER";
    public final static String ACTION_RC_SERVER = "com.github.template.application.RC_SERVER";
    public final static String ACTION_LOGCAT_SERVER = "com.github.template.application.LOGCAT_SERVER";
    
    public final static String ACTION_MESSAGE_CLIENT = "com.github.template.application.MESSAGE_CLIENT";
    public final static String ACTION_CAMERA_CLIENT = "com.github.template.application.CAMERA_CLIENT";
    public final static String ACTION_SCREEN_CLIENT = "com.github.template.application.SCREEN_CLIENT";
    public final static String ACTION_FILE_CLIENT = "com.github.template.application.FILE_CLIENT";
    public final static String ACTION_WEB_CLIENT = "com.github.template.application.WEB_CLIENT";
    public final static String ACTION_RC_CLIENT = "com.github.template.application.RC_CLIENT";
    public final static String ACTION_LOGCAT_CLIENT = "com.github.template.application.LOGCAT_CLIENT";
    
    public final static String EXTRA_TYPE = "EXTRA_TYPE";
    public final static String EXTRA_PATH = "EXTRA_PATH";
    
    public static void start(Context c, String action) {
        Intent mIntent = new Intent(c, MonitorActivity.class);
        mIntent.setAction(action);
        c.startActivity(mIntent);
    }

    public static void startOutPutRecorder(Context c, String path) {
        Intent mIntent = new Intent(c, MonitorActivity.class);
        mIntent.setAction(MonitorActivity.ACTION_MONITOR_SCREEN);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra(MonitorActivity.EXTRA_TYPE, SendBroadcast.TYPE.SCREEN_RECORD);
        mIntent.putExtra(MonitorActivity.EXTRA_TYPE, path);
        c.startActivity(mIntent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        String action = getIntent().getAction();
        String title = "";
        String subTitle = "";
        if (action != null && action.equals(ACTION_MONITOR_ACTIVITY)) {
            title = "Monitor";
            subTitle = "Activity";
            switchFragment(new MonitorActivityFragment());
        } else if (action != null && action.equals(ACTION_MONITOR_RECEIVER)) {
            title = "Monitor";
            subTitle = "Receiver";
            switchFragment(new MonitorReceiverFragment());
        } else if (action != null && action.equals(ACTION_MONITOR_PACKAGE_RECEIVER)) {
            title = "Monitor";
            subTitle = "Package Receiver";
            //  switchFragment(MonitorReceiverFragment.newInstance("Monitor Receiver"));
        } else if (action != null && action.equals(ACTION_MONITOR_SCREEN)) {
            title = "Monitor";
            subTitle = "Screen";
       String type = getIntent().getStringExtra(EXTRA_TYPE);
            String path = getIntent().getStringExtra(EXTRA_PATH);
            switchFragment(MonitorScreenFragment.newInstance(type, path));
        } else if (action != null && action.equals(ACTION_MONITOR_SDCARD)) {
            title = "Monitor";
            subTitle = "Sdcard";
            switchFragment(new MonitorSdcardFragment());
        } else if (action != null && action.equals(ACTION_MONITOR_MEMORY)) {
            title = "Monitor";
            subTitle = "Memory";
            switchFragment(new MonitorMemoryFragment());
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
            switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_FILE_SERVER)) {
            title = "App Server";
            subTitle = "File Server";
            switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_WEB_SERVER)) {
            title = "App Server";
            subTitle = "Web Server";
            switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_RC_SERVER)) {
            title = "App Server";
            subTitle = "Remote Server";
            switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_LOGCAT_SERVER)) {
            title = "App Server";
            subTitle = "Logcat Server";
            switchFragment(new ScreenServerFragment());
        } else if (action != null && action.equals(ACTION_MESSAGE_CLIENT)) {
            title = "App Client";
            subTitle = "Message Client";
           // switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
        } else if (action != null && action.equals(ACTION_CAMERA_CLIENT)) {
            title = "App Client";
            subTitle = "Camera Client";
            switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
        } else if (action != null && action.equals(ACTION_SCREEN_CLIENT)) {
            title = "App Client";
            subTitle = "Screen Client";
            switchFragment(ScreenClientFragment.newInstance(AppController.getServerIP()));
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

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setSubtitle(subTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume:");  
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause:");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy:");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.v(TAG, "onBackPressed:");
    }
}

