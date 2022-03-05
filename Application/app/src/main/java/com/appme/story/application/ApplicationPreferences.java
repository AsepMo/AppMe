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
import com.appme.story.SplashActivity;
import com.appme.story.engine.Api;
import com.appme.story.engine.app.commons.activity.ActionBarActivity;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.engine.graphics.SystemBarTintManager;
import com.appme.story.settings.theme.Theme;
import com.appme.story.settings.theme.ThemePreference;
import com.appme.story.settings.theme.ThemePreferenceFragment;

public class ApplicationPreferences extends ActionBarActivity {

    public static String TAG = ApplicationPreferences.class.getSimpleName();
    public static void start(Context mContext) {
        Intent mApplication = new Intent(mContext , ApplicationPreferences.class);
        mContext.startActivity(mApplication);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    
    private Toolbar mToolbar;
    private boolean mRecreate = false;
    private int actionBar;
    private SharedPreferences sharedPreferences;
    private Drawable icon = null;
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        setUpStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_settings);
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
                    SplashActivity.restart(ApplicationPreferences.this);
                    //switchActivity(ApplicationPreferences.this, "Restart", SplashActivity.class);
                }
            });
        if (mToolbar == null) {
            getSupportActionBar().setTitle(null);
            setSupportActionBar(mToolbar);     
        }
        
            
        final TextView mAppName = (TextView) findViewById(R.id.app_title);
        mAppName.setText(getString(R.string.app_name));
        
        switchFragment(new ThemePreferenceFragment());
        changeActionBarColor(0);
        actionBar = ThemePreference.getPrimaryColor();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
                // close this activity when the user clicks on the back button (action bar)
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
	}

    @Override
    public void recreate() {
        mRecreate = true;
        super.recreate();
        Theme.getInstance().getTheme(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume:");  
        Theme.getInstance().getTheme(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause:");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //switchActivity(ApplicationPreferences.this, "Restart", SplashActivity.class);
        SplashActivity.restart(ApplicationPreferences.this);
    }

    
    private Drawable oldBackground;
    public void changeActionBarColor(int newColor) {

        int color = newColor != 0 ? newColor : ThemePreference.getPrimaryColor();
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

}
