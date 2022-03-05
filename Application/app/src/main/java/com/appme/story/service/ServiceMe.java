package com.appme.story.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.List;

import com.appme.story.AppController;
import com.appme.story.engine.app.commons.Constant;
import com.appme.story.receiver.SendBroadcast;

public class ServiceMe {
    
    public static final String TAG = "ServiceMe";
    
    private static volatile ServiceMe Instance = null;

    private Context context;
    public static final int START_SERVICE = 0;
    public static final int START_ACTIVITY = 1;
    public static final int START_SERVER = 2;
    public static final int PAUSE_SERVICE = 3;
    public static final int RESUME_SERVICE = 4;
    public static final int NETWORK_STATUS = 5;
    public static final int SHUTDOWN_SERVICE = 6;
    public static final int OPEN_BROWSER = 7;
    private Intent mServiceIntent;
    public static ServiceMe getInstance() {
        ServiceMe localInstance = Instance;
        if (localInstance == null) {
            synchronized (ServiceMe.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ServiceMe(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private ServiceMe(Context context) {
        this.context = context;

    }

    public static ServiceMe with(Context context) {
        return new ServiceMe(context);
    }

    public void setIntent(Intent intent) {
        mServiceIntent = intent;   
    }

    public void setClass(Class<?> mClass) {
        mServiceIntent = new Intent(context, mClass);   
    }
    
    public void setAction(Integer status) {

        String action = "";      
        switch (status) {               
            case START_SERVER:
                action = SendBroadcast.ACTION.START_SERVER;
                break;
            case START_ACTIVITY:
                action = SendBroadcast.ACTION.START_ACTIVITY;
                break;      
            case PAUSE_SERVICE:
                action = SendBroadcast.ACTION.PAUSE_SERVICE;
                break;
            case RESUME_SERVICE:
                action = SendBroadcast.ACTION.RESUME_SERVICE;
                break;
            case SHUTDOWN_SERVICE:
                action = SendBroadcast.ACTION.SHUTDOWN_SERVICE;
                break; 
            case NETWORK_STATUS:
                action = SendBroadcast.ACTION.NETWORK_STATUS;
                break;
            case OPEN_BROWSER:
                action = SendBroadcast.ACTION.OPEN_BROWSER;
                break;    
        }
        mServiceIntent.setAction(action);          
    }

    public void setExtra(String message) {
        mServiceIntent.putExtra(SendBroadcast.EXTRA_SERVICE, message);
    }

    public void setExtra(String extra, String message) {
        mServiceIntent.putExtra(extra, message);
    }

    public void setExtra(String extra, int result) {
        mServiceIntent.putExtra(extra, result);
    }

    public void setExtra(String extra, int result, Intent intent) {
        mServiceIntent.putExtra(extra, result);
        mServiceIntent.putExtra(extra, intent);
    }

    public void startService() {
        context.startService(mServiceIntent);
    }
}
