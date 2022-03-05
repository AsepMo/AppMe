package com.appme.story.engine.app.analytics;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.appme.story.BuildConfig;

public class AnalyticsManager {
    private static Context sAppContext = null;

    public final static String TAG = AnalyticsManager.class.getSimpleName();

    private static boolean canSend() {
        return sAppContext != null && !BuildConfig.DEBUG ;
    }

    public static synchronized void intialize(Context context) {
        sAppContext = context;
    }

    public static void setProperty(String propertyName, String propertyValue){
        
    }

    public static void logEvent(String eventName){
        
    }

    public static void logEvent(String eventName, Bundle params){
        
    }

    public static void setCurrentScreen(Activity activity, String screenName){
        
    }
}



