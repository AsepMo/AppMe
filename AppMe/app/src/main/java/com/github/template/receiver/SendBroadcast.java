package com.github.template.receiver;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.List;

import com.github.template.AppController;

public class SendBroadcast {

    public static String TAG = SendBroadcast.class.getSimpleName();
    private static final String BASE = "com.github.template.process";
    private static final String BASE_ACTION = "com.github.template.services.ScreenMonitorService.";
    public static final String PROCESS_BROADCAST_ACTION = BASE + ".BROADCAST";
    public static final String PROCESS_STATUS_KEY = BASE +".STATUS_KEY";
    public static final String PROCESS_STATUS_MESSAGE = BASE + ".STATUS_MESSAGE";
    public static final String PROCESS_DIR = BASE + ".DIR";
    public static final String ACTION_QUERY_STATUS = BASE + "ACTION_QUERY_STATUS";
    public static final String ACTION_QUERY_STATUS_RESULT = BASE + "ACTION_QUERY_STATUS_RESULT";

    public static final String EXTRA_SERVICE = "EXTRA_SERVICE";
    public static final String EXTRA_RESULT_CODE = "resultCode";
    public static final String EXTRA_RESULT_INTENT = "resultIntent";    
    public static final String EXTRA_QUERY_RESULT_RECORDING = BASE + "EXTRA_QUERY_RESULT_RECORDING";
    public static final String EXTRA_QUERY_RESULT_PAUSING = BASE + "EXTRA_QUERY_RESULT_PAUSING";

    public static final String CHANNEL_WHATEVER = "channel_whatever";
    public static final int NOTIFY_ID = 9906;

    public static final String SERVICE_IS_READY = "SERVICE_IS_READY";
    public static final String START_RECORDING = "START_RECORDING";
    public static final String START_ACTIVITY = "START_ACTIVITY";
    public static final String PAUSE_RECORDING = "PAUSE_RECORDING";
    public static final String RESUME_RECORDING = "RESUME_RECORDING";
    public static final String STOP_RECORDING = "STOP_RECORDING";
    public static final String START_ACTIVITY_WITH_ERROR = "START_ACTIVITY_WITH_ERROR";
    public static final String EXIT_RECORDING_ON_ERROR = "EXIT_RECORDING_ON_ERROR";
    public static final String FINISH_RECORDING = "FINISH_RECORDING";
    public static final String SCREEN_SHOT_IS_DONE = "SCREEN_SHOT_IS_DONE";
    public static final String SCREEN_RECORD_IS_DONE = "SCREEN_RECORD_IS_DONE";
    public static final String SERVICE_IS_SHUTDOWN = "SERVICE_IS_SHUTDOWN";
    private static volatile SendBroadcast Instance = null;
    private Context mContext;

    public static SendBroadcast getInstance() {
        SendBroadcast localInstance = Instance;
        if (localInstance == null) {
            synchronized (SendBroadcast.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new SendBroadcast(AppController.getAppContext());
                }
            }
        }
        return localInstance;
    }

    private SendBroadcast(Context context) {
        this.mContext = context;
    }

    public static SendBroadcast with(Context context) {
        return new SendBroadcast(context);
    }

    public interface ACTION {
        String START_SERVICE = BASE_ACTION + ".ACTION_START";      
        String STOP = BASE_ACTION + ".ACTION_STOP";
        String SCREEN_CAPTURE = BASE_ACTION + ".ACTION_SCREEN_CAPTURE";
        String SCREEN_SHOT = BASE_ACTION + ".ACTION_SCREEN_SHOT";
        String SCREEN_RECORD = BASE_ACTION + ".ACTION_SCREEN_RECORD";
        String SCREEN_SHOT_DONE = BASE_ACTION + ".ACTION_SCREEN_SHOT_DONE";
        String PAUSE = BASE + ".ACTION_PAUSE";
        String RESUME = BASE + ".ACTION_RESUME";        
        String SHUTDOWN_SERVICE = BASE_ACTION + ".ACTION_SHUTDOWN_SERVICE";
    }

    public interface TYPE {
        String SCREEN_PLAY = "SCREEN_PLAY";   
        String SCREEN_SHOT = "SCREEN_SHOT";   
        String SCREEN_RECORD = "SCREEN_RECORD";    
    }

    public void broadcastStatus(String statusKey) {

        Intent localIntent = new Intent(SendBroadcast.PROCESS_BROADCAST_ACTION)
            .putExtra(SendBroadcast.PROCESS_STATUS_KEY, statusKey);
        mContext.sendBroadcast(localIntent);
    }

    public void broadcastStatus(String statusKey, String statusData) {

        Intent localIntent = new Intent(SendBroadcast.PROCESS_BROADCAST_ACTION)
            .putExtra(SendBroadcast.PROCESS_STATUS_KEY, statusKey)
            .putExtra(SendBroadcast.PROCESS_STATUS_MESSAGE, statusData);
        mContext.sendBroadcast(localIntent);
    }

    public void broadcastStatus(String statusKey, String statusData, String dir) {

        Intent localIntent = new Intent(SendBroadcast.PROCESS_BROADCAST_ACTION)
            .putExtra(SendBroadcast.PROCESS_STATUS_KEY, statusKey)
            .putExtra(SendBroadcast.PROCESS_STATUS_MESSAGE, statusData)
            .putExtra(SendBroadcast.PROCESS_DIR, dir);     
        mContext.sendBroadcast(localIntent);
    }


    public void broadcastStatus(String statusKey, String statusData, int resultCode, Intent resultData) {

        Intent localIntent = new Intent(SendBroadcast.PROCESS_BROADCAST_ACTION)
            .putExtra(SendBroadcast.PROCESS_STATUS_KEY, statusKey)
            .putExtra(SendBroadcast.PROCESS_STATUS_MESSAGE, statusData)
            .putExtra(SendBroadcast.EXTRA_RESULT_CODE, resultCode)
            .putExtra(SendBroadcast.EXTRA_RESULT_INTENT, resultData);
        mContext.sendBroadcast(localIntent);
    }

    public static void killAllProcessorServices(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            String processName = context.getPackageName() + ":service";
            if (next.processName.equals(processName)) {
                android.os.Process.killProcess(next.pid);
                break;
            }
        }
    }

    public static boolean isProcessorServiceRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo next : runningAppProcesses) {
            String processName = context.getPackageName() + ":service";
            if (next.processName.equals(processName)) {
                return true;
            }
        }
        return false;
    }
}

