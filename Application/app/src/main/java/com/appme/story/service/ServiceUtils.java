package com.appme.story.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.List;

import com.appme.story.AppController;
import com.appme.story.engine.app.commons.Constant;
import com.appme.story.receiver.SendBroadcast;

public class ServiceUtils {

    private static final String TAG = ServiceUtils.class.getSimpleName();

    private static volatile ServiceUtils Instance = null;
    private Context context;


    public static ServiceUtils getInstance() {
        ServiceUtils localInstance = Instance;
        if (localInstance == null) {
            synchronized (ServiceUtils.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ServiceUtils(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private ServiceUtils(Context context) {
        this.context = context;         
    }

    public static ServiceUtils with(Context context) {
        return new ServiceUtils(context);
    }

    public static void killAllServices(Context context) {
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

    public static boolean isServiceRunning(Context context) {
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

    public void onStartService() {
        ServiceUtils.killAllServices(context);
        Intent mServiceIntent = new Intent(context, InstallService.class);
        mServiceIntent.setAction(Constant.ACTION.START_SERVICE);      
        context.startService(mServiceIntent);
    }
    
    public void onResumeService() {
        ServiceUtils.killAllServices(context);
        Intent mServiceIntent = new Intent(context, InstallService.class);
        mServiceIntent.setAction(Constant.ACTION.START_SERVICE);      
        context.startService(mServiceIntent);
    }
    

    public void onCheckIndexFile() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, InstallService.class);
            mServiceIntent.setAction(Constant.ACTION.CHECK_INDEX_FILE);      
            context.startService(mServiceIntent);
        }
    }

    public void onExtractAssets() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, InstallService.class);
            mServiceIntent.setAction(Constant.ACTION.EXTRACT_ASSETS);      
            context.startService(mServiceIntent);
        }
    }

    public void onInstallIndexFile() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, InstallService.class);
            mServiceIntent.setAction(Constant.ACTION.INSTALL_INDEX_FILE);      
            context.startService(mServiceIntent);
        }
    }
    
    public void onSyncronData() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, InstallService.class);
            mServiceIntent.setAction(Constant.ACTION.SYNCRON_DATA);      
            context.startService(mServiceIntent);
        }
    }
    
    public void onStartActivity() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, InstallService.class);
            mServiceIntent.setAction(Constant.ACTION.START_ACTIVITY);      
            context.startService(mServiceIntent);
        }
    }

    public void onStopService() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, InstallService.class);
            mServiceIntent.setAction(Constant.ACTION.STOP_SERVICE);      
            context.startService(mServiceIntent);
        }
    }

    public void launchAppMeService() {
        ServiceUtils.killAllServices(context);
        Intent mServiceIntent = new Intent(context, AppMeService.class);
        mServiceIntent.putExtra(SendBroadcast.EXTRA_SERVICE, "Service Is Running");
        context.startService(mServiceIntent);
    }

    public void onStartAppMeService() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, AppMeService.class);
            mServiceIntent.setAction(SendBroadcast.ACTION.START_SERVICE);
            context.startService(mServiceIntent);
        }
    }

    public void onPauseAppMeService() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, AppMeService.class);
            mServiceIntent.setAction(SendBroadcast.ACTION.PAUSE_SERVICE);
            context.startService(mServiceIntent);
        }
    }

    public void onResumeAppMeService() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, AppMeService.class);
            mServiceIntent.setAction(SendBroadcast.ACTION.RESUME_SERVICE);
            context.startService(mServiceIntent);
        }
    }

    public void onStopAppMeService() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, AppMeService.class);
            mServiceIntent.setAction(SendBroadcast.ACTION.STOP_SERVICE);
            context.startService(mServiceIntent);
        }
    }

    public void onShutdownAppMeService() {
        if (isServiceRunning(context)) {
            Intent mServiceIntent = new Intent(context, AppMeService.class);
            mServiceIntent.setAction(SendBroadcast.ACTION.SHUTDOWN_SERVICE);
            context.startService(mServiceIntent);
        }
    }

    public void onStartService(String message, int resultCode, Intent resultData) {
        ServiceUtils.killAllServices(context);
        Intent mServiceIntent = new Intent(context, AppMeService.class);
        mServiceIntent.putExtra(SendBroadcast.EXTRA_RESULT_CODE, resultCode);
        mServiceIntent.putExtra(SendBroadcast.EXTRA_RESULT_INTENT, resultData);        
        mServiceIntent.putExtra(SendBroadcast.EXTRA_SERVICE, message);
        context.startService(mServiceIntent);
    }
    
    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }

}
