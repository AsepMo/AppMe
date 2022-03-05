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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ImageView;

import java.util.Calendar;

import com.appme.story.R;
import com.appme.story.engine.Api;
import com.appme.story.engine.app.commons.activity.ActionBarActivity;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.engine.app.folders.AssetManager;
import com.appme.story.engine.app.listeners.OnRequestHandlerListener;
import com.appme.story.engine.graphics.SystemBarTintManager;
import com.appme.story.engine.widget.SplashScreen;
import com.appme.story.settings.theme.Theme;
import com.appme.story.settings.theme.ThemePreference;
import com.appme.story.settings.theme.ThemePreferenceFragment;

public class ApplicationStarter extends ActionBarActivity {

    public static String TAG = ApplicationStarter.class.getSimpleName();
    private Toolbar mToolbar;
    private TextView mCopyRight;
    private SharedPreferences sharedPreferences;
    private Drawable icon = null;
    private SplashScreen mSplashScreen;
    private int SPLASH_TIME_OUT = 5000;
    private Handler mHandler = new Handler();
    private Runnable mRunner = new Runnable() {

        /*
         * Showing splash screen with a timer. This will be useful when you
         * want to show case your app logo / company
         */

        @Override
        public void run() {
            // This method will be executed once the timer is over
            // Start SplashScreen
            mSplashScreen.start();
            mSplashScreen.setOnSplashScreenListener(new SplashScreen.OnSplashScreenListener(){
                    @Override
                    public void OnStartActivity(final SplashScreen mSplash) {
                        /*boolean isRemove = getFolderMe().remove(getFolderMe().getExternalFileDir("all"));
                         if(!isRemove){
                         Toast.makeText(ApplicationStarter.this, "Clean Directory", Toast.LENGTH_SHORT).show();                                         
                         }*/
                        getAppMe().setPermission(ApplicationStarter.this, Application.requestPermissionShortcut, new Application.OnActionPermissionListener(){
                                @Override
                                public void onGranted() {   
                                    getEngineMe().createShortCut();                                                              
                                }

                                @Override
                                public void onDenied(String permission) {

                                }
                            }); 
                        mSplash.createShortCut();        
                        switchActivity(ApplicationStarter.this, ApplicationInstaller.class);
                    }
                });
        }  
	};
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        setUpDefaultStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_starter);
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
                    switchActivity(ApplicationStarter.this, "Back To Home", ApplicationActivity.class);
                }
            });
        if (mToolbar == null) {
            getSupportActionBar().setTitle(null);
            setSupportActionBar(mToolbar);     
        }
        
        final TextView mAppName = (TextView) findViewById(R.id.app_title);
        mAppName.setText(getString(R.string.app_name));
     
        mCopyRight = (TextView) findViewById(R.id.app_copy_right);
        final String copyrights = String.format(getString(R.string.app_copy_right), Calendar.getInstance().get(Calendar.YEAR));
        mCopyRight.setText(copyrights);
        
        // Toolbar is Gone
        mToolbar.setVisibility(View.GONE);

        mSplashScreen = (SplashScreen)findViewById(R.id.splash_screen);
        AssetManager.with(ApplicationStarter.this).extract("video").setOnAssetManagerListener(new AssetManager.OnAssetManagerListener(){
               
                @Override
                public void onStart(String message) {
                }
                
                @Override
                public void onSuccess(String path) {
                    setRequestHandler(new OnRequestHandlerListener(){
                            @Override
                            public void onHandler() {

                                mHandler.postDelayed(mRunner, SPLASH_TIME_OUT);
                            }
                        });              
                }

                @Override
                public void onFail(String path) {                 
                }
            });

        changeActionBarColor();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume:");  
        mHandler.postDelayed(mRunner, SPLASH_TIME_OUT);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause:"); 
        mHandler.removeCallbacks(mRunner);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy:"); 
        mHandler.removeCallbacks(mRunner);
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
    
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        switchActivity(ApplicationStarter.this, "Back To App Info", ApplicationActivity.class);
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

