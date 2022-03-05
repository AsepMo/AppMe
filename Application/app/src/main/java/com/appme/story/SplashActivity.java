package com.appme.story;

import android.annotation.TargetApi;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v4.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import com.appme.story.application.Application;
import com.appme.story.application.ApplicationStarter;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.Api;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.commons.activity.ActionBarActivity;
import com.appme.story.engine.app.analytics.Analytics;
import com.appme.story.engine.app.models.FileScanningItem;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.listeners.OnRequestHandlerListener;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.engine.graphics.SystemBarTintManager;
import com.appme.story.settings.theme.ThemePreference;

public class SplashActivity extends ActionBarActivity {
    public static final String TAG = SplashActivity.class.getSimpleName();
    public static final String ACTION_RESTART = "ACTION_RESTART";
    private ImageView mAppIcon;
    private TextView mAppMessage;
    private TextView mCopyRight;
    public static void start(Context c) {
        Intent mIntent = new Intent(c, SplashActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(mIntent);
    }
    
    public static void restart(Context c) {
        Intent mIntent = new Intent(c, SplashActivity.class);
        mIntent.setAction(ACTION_RESTART);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(mIntent);
    }
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        setUpDefaultStatusBar();
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_splash);
		
		Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
        if (mToolbar == null) {
            ActionBar mActionBar = getSupportActionBar();
            mActionBar.setTitle(null);
            
            setSupportActionBar(mToolbar);
        }
        final TextView mAppName = (TextView) findViewById(R.id.app_title);
        mAppName.setText(getString(R.string.app_name));
        
        mAppIcon = (ImageView) findViewById(R.id.splash_app_icon);
        mAppIcon.setImageResource(R.drawable.apk_v2);

        mAppMessage = (TextView) findViewById(R.id.app_message);
        mAppMessage.setText("Welcome To AppMe");
        
        mCopyRight = (TextView) findViewById(R.id.app_copy_right);
        final String copyrights = String.format(getString(R.string.app_copy_right), Calendar.getInstance().get(Calendar.YEAR));
        mCopyRight.setText(copyrights);
        
        String action = getIntent().getAction();
        if (action != null && action.equals(ACTION_RESTART)) {
            setLogger("Restart Application ");
            switchActivity(SplashActivity.this, mAppMessage, "Restart Application ", ApplicationActivity.class);              
        }else{
            getAppMe().setPermission(SplashActivity.this, Application.requestPermissionStorage, new Application.OnActionPermissionListener(){
                @Override
                public void onGranted() {            
                    getAnalytics().setServerIP();
                    getAnalytics().setAnalytisActivity(new Analytics.OnFirstTimeListener(){
                            @Override
                            public void onFirsTime() {
                                setLogger("initialized");
                                getFolderMe().initFolder();
                                getAppMe().setAppMeBackup(SplashActivity.this);
                                getAppMe().setInitialize(SplashActivity.this);
                                switchActivity(SplashActivity.this, mAppMessage, "This First Time Launching ", ApplicationStarter.class);   
                            }
                            @Override
                            public void onSecondTime() {
                                setLogger("Open AppMe");
                                getFolderMe().initFolder();
                                getAppMe().setInitialize(SplashActivity.this);                                    
                                setFileScanning();                               
                            }
                        });         
                }

                @Override
                public void onDenied(String permission) {
                    setLogger(permission);
                }
			});	
        }
        
        
    }
    
    public void setFileScanning() {
        boolean isExist = FileScanningItem.isExist(FileScanningItem.KEY_APPLIST_JSON);
        if (isExist) {           
            switchActivity(SplashActivity.this, mAppMessage, "Start Activity", ApplicationActivity.class);                     
        } else {
            setRequestHandler(new OnRequestHandlerListener(){
                    @Override
                    public void onHandler() {
                        mAppMessage.setText("Apk File Scanning In Memory"); 
                        Engine engineMe = Engine.with(SplashActivity.this);
                        engineMe.startScanning();
                        engineMe.setOnScanningTaskListener(new Engine.OnScanningTaskListener(){
                                @Override
                                public void onMemoryScanning(String message) {

                                }

                                @Override
                                public void onMemoryScanningSuccess(ArrayList<FileScanningItem> result) {
                                    mAppMessage.setText("Apk File Scanning In Memory Success");                     
                                }

                                @Override
                                public void onMemoryScanningFailed(String message) {
                                    mAppMessage.setText("Apk File Scanning In Memory Failed");  
                                    //Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();       
                                } 

                                @Override
                                public void onMemoryIsEmpty(String message) {
                                    mAppMessage.setText("Apk File In Memory is Empty"); 
                                   // Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();       
                                } 

                                @Override
                                public void onSdCardScanning(String message) {
                                    mAppMessage.setText("Apk File Scanning In Sdcard"); 
                                    //Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();                              
                                }

                                @Override
                                public void onSdCardScanningSuccess(ArrayList<FileScanningItem> result) {
                                    mAppMessage.setText("Apk File Scanning In Sdcard Success"); 
                                    setRequestHandler(new OnRequestHandlerListener(){
                                            @Override
                                            public void onHandler() {
                                                
                                                switchActivity(SplashActivity.this, mAppMessage, "Start Activity", ApplicationActivity.class);                                                
                                            }
                                        });              

                                }

                                @Override
                                public void onSdCardScanningFailed(String message) {
                                    mAppMessage.setText("Apk File Scanning In Sdcard Failed");  
                                    //Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();       
                                } 

                                @Override
                                public void onSdCardIsEmpty(String message) {
                                    mAppMessage.setText("Apk File In SdCard Is Empty"); 
                                    //Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();       
                                } 
                            });
                    }
                });              
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setUpStatusBar() {
        int color = ScreenUtils.getStatusBarColor(ThemePreference.getPrimaryColor());
        if(Api.hasLollipop()){
            getWindow().setStatusBarColor(color);
        }
        else if(Api.hasKitKat()){
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setTintColor(color);
            systemBarTintManager.setStatusBarTintEnabled(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setUpDefaultStatusBar() {
        int color = ContextCompat.getColor(this, android.R.color.black);
        if(Api.hasLollipop()){
            getWindow().setStatusBarColor(color);
        }
        else if(Api.hasKitKat()){
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
/*don't forget to subscribe my YouTube channel for more Tutorial and mod*/
/*
https://youtube.com/channel/UC_lCMHEhEOFYgJL6fg1ZzQA */
