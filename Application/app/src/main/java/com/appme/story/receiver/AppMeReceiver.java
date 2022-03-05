package com.appme.story.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class AppMeReceiver extends BroadcastReceiver {

    public static String TAG = AppMeReceiver.class.getSimpleName();

    private OnSendBroadcastListener listener;
    public interface OnSendBroadcastListener {
        void onServiceReady(String message);
        void onStartService(String message);
        void onStartServer(String message);
        void onStartActivity(String message);
        void onNetworkStatus(String message);
        void onPauseService(String message);
        void onResumeService(String message);
        void onStopService(String message);
        void onOpenBrowser(String message);
        void onServiceShutDown(String message);
    }

    public void setOnSendBroadcastListener(OnSendBroadcastListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context pContext, Intent pIntent) {
        String statusKey = "";
        String statusData = "";
        if (pIntent.hasExtra(SendBroadcast.PROCESS_STATUS_KEY)) {
            statusKey = pIntent.getStringExtra(SendBroadcast.PROCESS_STATUS_KEY);
        }
        if (pIntent.hasExtra(SendBroadcast.PROCESS_STATUS_MESSAGE)) {
            statusData = pIntent.getStringExtra(SendBroadcast.PROCESS_STATUS_MESSAGE);
        }

        switch (statusKey) {
            case SendBroadcast.SERVICE_IS_READY:
                listener.onServiceReady(statusData);
                break;
            case SendBroadcast.START_SERVICE:
                listener.onStartService(statusData);
                break;      
            case SendBroadcast.START_SERVER:
                listener.onStartServer(statusData);
                break;  
            case SendBroadcast.START_ACTIVITY:
                listener.onStartActivity(statusData);
                break;   
            case SendBroadcast.NETWORK_STATUS:
                listener.onNetworkStatus(statusData);
                break;   
            case SendBroadcast.OPEN_BROWSER:
                listener.onOpenBrowser(statusData);
                break;    
            case SendBroadcast.PAUSE_SERVICE:
                listener.onStopService(statusData);
                break;       
            case SendBroadcast.RESUME_SERVICE:
                listener.onResumeService(statusData);
                break;   
            case SendBroadcast.STOP_SERVICE:
                listener.onStopService(statusData);
                break;     
            case SendBroadcast.SERVICE_IS_SHUTDOWN:
                listener.onServiceShutDown(statusData);
                break;  
        }
        
    } 
    
}
