package com.appme.story.engine;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;

import java.io.File;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.app.commons.Constant;
import com.appme.story.engine.app.utils.IntentUtils;

public class ShortCut {
    
    //private static volatile ShortCut Instance = null;
    private Context mActivity;
    
    private ShortCut(Context mActivity) {
        this.mActivity = mActivity;
    }

    public static ShortCut with(Context mActivity) {
        return new ShortCut(mActivity);
    }
    
    public void createShortCut() {
        Intent mIntent = new Intent(mActivity, ApplicationActivity.class);
        createShortCut(mIntent);
    }

    public void createShortCut(Intent mIntent) {
        if (!isShortCut(mActivity, "duplicate")) {
            createShortCut(mIntent, R.mipmap.ic_launcher, "duplicate");      
        }
    }

    public void createShortCut(Intent mIntent, String title, String category) {
        if (!isShortCut(mActivity, category)) {
            createShortCut(mIntent, R.mipmap.ic_launcher, title, category);      
        }
    }
      
    private void createShortCut(Intent mIntent, int icon, String category) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, AppController.getContext().getString(R.string.app_name));
        intent.putExtra(category, false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(AppController.getContext().getResources(), icon)); 
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mIntent);
        mActivity.sendBroadcast(intent);
        setIsShortCut(mActivity, category);
    }
    
    private void createShortCut(Intent mIntent, int icon, String title, String category) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        intent.putExtra(category, false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(AppController.getContext().getResources(), icon)); 
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, mIntent);
        mActivity.sendBroadcast(intent);
        setIsShortCut(mActivity, category);
    }
    
    public void createShortCutMe(File file) {
        IntentUtils.createShortcut(mActivity, file);
    }
    
    public static void setIsShortCut(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, true);
        editor.commit();
    }

    public static Boolean isShortCut(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("SharedPref", Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }
    
}
