package com.appme.story.engine.app.commons.activity;

import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.application.Application;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.Api;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.analytics.Analytics;
import com.appme.story.engine.app.analytics.AnalyticsManager;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.models.FileScanningItem;
import com.appme.story.engine.app.listeners.OnRequestHandlerListener;
import com.appme.story.receiver.RemoteLogger;
import com.appme.story.settings.theme.Theme;
import com.appme.story.settings.theme.ThemePreference;

public abstract class ActionBarActivity extends AppCompatActivity {
    public abstract void setUpStatusBar();
    public abstract void setUpDefaultStatusBar();
    public static final String TAG = ActionBarActivity.class.getSimpleName();

    private Context mContext;
    private boolean mRecreate = false;

    public int TIME_OUT = 2000;
    public int SPLASH_TIME_OUT = 5000;

    private ProgressDialog progress;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Theme.getInstance().setTheme(this);
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        progress = new ProgressDialog(this);
    }

    public AppController getAppController() {
        return AppController.getInstance();
    }

    public Application getAppMe() {
        if (getAppController() == null) return null;
        return getAppController().getAppMe();
    }

    public Analytics getAnalytics() {
        if (getAppController() == null) return null;
        return getAppController().getAnalytics();
    }

    public FolderMe getFolderMe() {
        if (getAppController() == null) return null;
        return getAppController().getFolderMe();
    }

    public Engine getEngineMe() {
        if (getAppController() == null) return null;
        return getAppController().getEngineMe();
    }

    public void switchActivity(final AppCompatActivity activity,  final Class<?> mClass) {      
        Intent mIntent = new Intent(activity, mClass);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(mIntent);
        activity.finish();       
    }

    public void switchActivity(final AppCompatActivity activity, final String message, final Class<?> mClass) {
        showProgress();
        new CountDownTimer(TIME_OUT, TIME_OUT){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();       
                hideProgress();
                Intent mIntent = new Intent(activity, mClass);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(mIntent);
                activity.finish();
            }  
        }.start();
    }

    public void switchActivity(final AppCompatActivity activity, final TextView mMessage, final String message, final Class<?> mClass) {
        mMessage.setText(message);     
        new CountDownTimer(TIME_OUT, TIME_OUT){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent mIntent = new Intent(activity, mClass);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(mIntent);
                activity.finish();
            }  
        }.start();
    }

    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.content_frame, fragment)
            .commit();
    }

    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();       
    }

    public void showToast(int message) {
        Toast.makeText(mContext, getString(message), Toast.LENGTH_SHORT).show();       
    }


    public void setLogger(String log) {
        RemoteLogger.sendBroadcast(AppController.getContext(), log);
    }

    public void setRequestHandler(final OnRequestHandlerListener mOnRequestHandlerListener) {
        new CountDownTimer(2000, 2000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (mOnRequestHandlerListener != null) {
                    mOnRequestHandlerListener.onHandler();
                }
            }  
        }.start();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        getAppMe().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void showProgress() {
        //progress.setTitle(R.string.web_file_extract);
        progress.setMessage("Starting..");
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        //progress.setOnCancelListener(this);
        progress.show();
    }

    public void showProgress(int message) {      
        showProgress(getString(message));
    }

    public void showProgress(String message) {
        //progress.setTitle(R.string.web_file_extract);
        progress.setMessage(message);
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        //progress.setOnCancelListener(this);
        progress.show();
    }

    public ProgressDialog getProgressDialog() {
        return progress;
    }

    public void hideProgress() {
        if (getProgressDialog() != null) {
            getProgressDialog().dismiss();
        }
    }

    /* protected void enableHomeButton(Toolbar toolbar) {
     setSupportActionBar(toolbar);
     getSupportActionBar().setHomeButtonEnabled(true);
     getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     }*/

    @Override
    public void recreate() {
        mRecreate = true;
        super.recreate();
        Theme.getInstance().getTheme(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Theme.getInstance().getTheme(this);
        AnalyticsManager.setCurrentScreen(this, getTag());
    }

    public void setAppTheme() {
        Theme.getInstance().setTheme(this);
        if (Api.hasLollipop()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        } else if (Api.hasKitKat()) {
            setTheme(R.style.AppTheme_Application_Translucent);
        }
    }


    public abstract String getTag();
}



