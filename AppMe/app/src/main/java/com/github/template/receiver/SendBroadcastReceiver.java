package com.github.template.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class SendBroadcastReceiver extends BroadcastReceiver {

    public static String TAG = SendBroadcastReceiver.class.getSimpleName();

    private OnSendBroadcastListener listener;
    public interface OnSendBroadcastListener {
        void onServiceReady(String message);
        void onScreenShotDone(String message);
        void onScreenRecordDone(String message);
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
            case SendBroadcast.SCREEN_SHOT_IS_DONE:
                listener.onScreenShotDone(statusData);
                break;  
            case SendBroadcast.SCREEN_RECORD_IS_DONE:
                listener.onScreenRecordDone(statusData);
                break;       
            case SendBroadcast.SERVICE_IS_SHUTDOWN:
                listener.onServiceShutDown(statusData);
                break;  

        }

    }

}



