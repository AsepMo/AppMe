package com.github.template;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import com.github.template.application.ApplicationActivity;

public class SplashActivity extends AppCompatActivity {
    
    public static String TAG = SplashActivity.class.getSimpleName();
    /** An intent for launching the system settings. */
    private static final Intent sSettingsIntent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
    
    private Handler mHandler = new Handler();
    private Runnable mRunner = new Runnable(){
        @Override
        public void run() {
            ApplicationActivity.start(SplashActivity.this);
            SplashActivity.this.finish();
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**** START APP ****/
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean isFirstStart = SP.getBoolean("firstStart", true);
        if (isFirstStart) {
            SharedPreferences.Editor e = SP.edit();
            e.putBoolean("firstStart", false);
            e.apply();
            startActivity(sSettingsIntent);
        }
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        mHandler.postDelayed(mRunner, 2000); 
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        mHandler.removeCallbacks(mRunner);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        mHandler.removeCallbacks(mRunner);
    }
}
