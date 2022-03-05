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
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.appme.story.R;
import com.appme.story.engine.Api;
import com.appme.story.engine.app.commons.activity.ActionBarActivity;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.engine.app.fragments.BinaryInstallerFragment;
import com.appme.story.engine.graphics.SystemBarTintManager;
import com.appme.story.settings.theme.Theme;
import com.appme.story.settings.theme.ThemePreference;
import com.appme.story.settings.theme.ThemePreferenceFragment;

public class ApplicationInstaller extends ActionBarActivity {

	public static String TAG = ApplicationInstaller.class.getSimpleName();

    public static void start(Context c) {
        Intent mIntent = new Intent(c, ApplicationInstaller.class);
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(mIntent);
    }

    private LinearLayout mHeaderInstaller;
    private ImageView appIcon;
	private TextView appName;
	private TextView packageName;
	private TextView versionName;
	private TextView versionCode;
	private TextView author;
	
    private Toolbar mToolbar;
    private SharedPreferences sharedPreferences;
    private Drawable icon = null;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        setUpDefaultStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_installation);
        sharedPreferences = getSharedPreferences(Theme.THEME_PREFERENCES, Context.MODE_PRIVATE);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        if (mToolbar == null) {
            getSupportActionBar().setTitle(null);
            setSupportActionBar(mToolbar);     
        }

        final TextView mAppName = (TextView) findViewById(R.id.app_title);
        mAppName.setText(getString(R.string.app_name));
    
         mHeaderInstaller = (LinearLayout)findViewById(R.id.header_app_installer);
		 appIcon = (ImageView)findViewById(R.id.app_icon);
		 appIcon.setImageResource(R.mipmap.ic_launcher);

		// output
		StringBuilder sb1 = (new StringBuilder()).append("Name   :").append("   ").append("AppMe");
		appName = (TextView)findViewById(R.id.app_name);
		appName.setText(sb1.toString());
		StringBuilder sb2 = (new StringBuilder()).append("PackageName   :").append("   ").append("com.appme.story");
		packageName = (TextView)findViewById(R.id.package_name);
		packageName.setText(sb2.toString());
		
		StringBuilder sb3 = (new StringBuilder()).append("VersionName :").append("   ").append("1.0");
		versionName = (TextView)findViewById(R.id.version_name);
		versionName.setText(sb3.toString());
		StringBuilder sb4 = (new StringBuilder()).append("VersionCode :").append("   ").append("1");
		versionCode = (TextView)findViewById(R.id.version_code);
		versionCode.setText(sb4.toString());
		StringBuilder sb5 = (new StringBuilder()).append("Author   :").append("   ").append("AsepMo");
		author = (TextView)findViewById(R.id.author);
		author.setText(sb5.toString());
		switchFragment(new BinaryInstallerFragment());
        
        changeActionBarColor();
    }

    private Drawable oldBackground;
    private void changeActionBarColor() {

        int color = ThemePreference.getPrimaryColor();
        Drawable colorDrawable = new ColorDrawable(color);

        if (oldBackground == null) {
            mToolbar.setBackgroundDrawable(colorDrawable);
            mHeaderInstaller.setBackgroundDrawable(colorDrawable);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, colorDrawable });
            mToolbar.setBackgroundDrawable(td);
            mHeaderInstaller.setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = colorDrawable;

        setUpDefaultStatusBar();
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
