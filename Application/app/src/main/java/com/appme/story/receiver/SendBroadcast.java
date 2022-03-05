package com.appme.story.receiver;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.support.v4.content.LocalBroadcastManager;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;

import java.io.File;
import java.util.List;

import com.appme.story.AppController;
import com.appme.story.service.AppMeService;
import static com.appme.story.engine.app.commons.Constant.ACTION_ACCESSIBILITY_ACTION;
import static com.appme.story.engine.app.commons.Constant.EXTRA_ACTION;

public class SendBroadcast {

    public static String TAG = SendBroadcast.class.getSimpleName();
    private static final String BASE = "com.appme.story.process";
    private static final String BASE_ACTION = "com.appme.story.service.AppMeService.";
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
    public static final String START_SERVICE = "START_SERVICE";
    public static final String START_ACTIVITY = "START_ACTIVITY";
    public static final String START_SERVER = "START_SERVER";
    public static final String NETWORK_STATUS = "NETWORK_STATUS";
    public static final String PAUSE_SERVICE = "PAUSE_SERVICE";
    public static final String RESUME_SERVICE = "RESUME_SERVICE";
    public static final String STOP_SERVICE = "STOP_SERVICE";
    public static final String OPEN_BROWSER = "OPEN_BROWSER";
    public static final String SERVICE_IS_SHUTDOWN = "SERVICE_IS_SHUTDOWN";
    private static volatile SendBroadcast Instance = null;
    private Context context;

    public static SendBroadcast getInstance() {
        SendBroadcast localInstance = Instance;
        if (localInstance == null) {
            synchronized (SendBroadcast.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new SendBroadcast(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private SendBroadcast(Context context) {
        this.context = context;
    }

    public static SendBroadcast with(Context context) {
        return new SendBroadcast(context);
    }

    public interface ACTION {
        String START_SERVICE = BASE_ACTION + ".ACTION_START";     
        String START_ACTIVITY = BASE_ACTION + ".ACTION_START_ACTIVITY";   
        String START_SERVER = BASE_ACTION + ".ACTION_START_SERVER";     
        String NETWORK_STATUS = BASE_ACTION + ".ACTION_NETWORK_STATUS"; 
        String STOP_SERVICE = BASE_ACTION + ".ACTION_STOP";
        String PAUSE_SERVICE = BASE_ACTION + ".ACTION_PAUSE";
        String RESUME_SERVICE = BASE_ACTION + ".ACTION_RESUME";   
        String OPEN_BROWSER = BASE_ACTION + ".ACTION_OPEN_BROWSER";   
        String SHUTDOWN_SERVICE = BASE_ACTION + ".ACTION_SHUTDOWN_SERVICE";
        String PLAY_TOGGLE = BASE_ACTION + ".ACTION_PLAY_TOGGLE";
        String PLAY_LAST = BASE_ACTION + ".ACTION_PLAY_LAST";
        String PLAY_NEXT = BASE_ACTION + ".ACTION_PLAY_NEXT";
        String STOP_PLAYING_SERVICE = BASE_ACTION + ".ACTION_STOP_SERVICE";
        
    }

    public interface TYPE {
        String SCREEN_PLAY = "SCREEN_PLAY";   
        String SCREEN_SHOT = "SCREEN_SHOT";   
        String SCREEN_RECORD = "SCREEN_RECORD";    
    }
    
    public void broadcastStatus(String statusKey) {

        Intent localIntent = new Intent(SendBroadcast.PROCESS_BROADCAST_ACTION)
            .putExtra(SendBroadcast.PROCESS_STATUS_KEY, statusKey);
        context.sendBroadcast(localIntent);
    }

    public void broadcastStatus(String statusKey, String statusData) {

        Intent localIntent = new Intent(SendBroadcast.PROCESS_BROADCAST_ACTION)
            .putExtra(SendBroadcast.PROCESS_STATUS_KEY, statusKey)
            .putExtra(SendBroadcast.PROCESS_STATUS_MESSAGE, statusData);
        context.sendBroadcast(localIntent);
    }

    public void broadcastStatus(String statusKey, String statusData, String dir) {

        Intent localIntent = new Intent(SendBroadcast.PROCESS_BROADCAST_ACTION)
            .putExtra(SendBroadcast.PROCESS_STATUS_KEY, statusKey)
            .putExtra(SendBroadcast.PROCESS_STATUS_MESSAGE, statusData)
            .putExtra(SendBroadcast.PROCESS_DIR, dir);     
        context.sendBroadcast(localIntent);
    }


    public void broadcastStatus(String statusKey, String statusData, int resultCode, Intent resultData) {

        Intent localIntent = new Intent(SendBroadcast.PROCESS_BROADCAST_ACTION)
            .putExtra(SendBroadcast.PROCESS_STATUS_KEY, statusKey)
            .putExtra(SendBroadcast.PROCESS_STATUS_MESSAGE, statusData)
            .putExtra(SendBroadcast.EXTRA_RESULT_CODE, resultCode)
            .putExtra(SendBroadcast.EXTRA_RESULT_INTENT, resultData);
        context.sendBroadcast(localIntent);
    }

   /* public static void sendAccessibilityAction(Context context, int action) {
        sendAccessibilityAction(context, action, null);
    }

    public static void sendAccessibilityAction(final Context context, final int action, final Runnable onComplete) {
        setComponentEnabled(context, PowerMenuService.class, true);

        boolean isAccessibilityServiceEnabled = isAccessibilityServiceEnabled(context);

        if (!isAccessibilityServiceEnabled && hasWriteSecureSettingsPermission(context)) {
            String services = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            final String finalServices = services == null ? "" : services;

            String powerMenuService = new ComponentName(context, PowerMenuService.class).flattenToString();

            if (!finalServices.contains(powerMenuService)) {
                try {
                    Settings.Secure.putString(context.getContentResolver(),
                                              Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                                              finalServices.isEmpty()
                                              ? powerMenuService
                                              : finalServices + ":" + powerMenuService);
                } catch (Exception ignored) {}
            }

            newHandler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        Intent intent = new Intent(ACTION_ACCESSIBILITY_ACTION);
                        intent.putExtra(EXTRA_ACTION, action);
                        sendBroadcast(context, intent);

                        try {
                            Settings.Secure.putString(context.getContentResolver(),
                                                      Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                                                      finalServices);
                        } catch (Exception ignored) {}

                        if (onComplete != null) onComplete.run();
                    }
                }, 100);
        } else if (isAccessibilityServiceEnabled) {
            Intent intent = new Intent(ACTION_ACCESSIBILITY_ACTION);
            intent.putExtra(EXTRA_ACTION, action);
            sendBroadcast(context, intent);

            if (onComplete != null) onComplete.run();
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        String accessibilityServices = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        ComponentName component = new ComponentName(context, PowerMenuService.class);

        return accessibilityServices != null
            && (accessibilityServices.contains(component.flattenToString())
            || accessibilityServices.contains(component.flattenToShortString()));
    }*/
    
    public static void registerReceiver(Context context, BroadcastReceiver receiver, String... actions) {
        unregisterReceiver(context, receiver);

        IntentFilter intentFilter = new IntentFilter();
        for(String action : actions) {
            intentFilter.addAction(action);
        }

        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);
    }

    public static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    public static void sendBroadcast(Context context, String action) {
        sendBroadcast(context, new Intent(action));
    }

    public static void sendBroadcast(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void setComponentEnabled(Context context, Class<?> clazz, boolean enabled) {
        ComponentName component = new ComponentName(context, clazz);
        context.getPackageManager().setComponentEnabledSetting(component, enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);                                                                                                                    
    }
    
    public static Handler newHandler() {
        return new Handler(Looper.getMainLooper());
    }

    public static CharSequence getAppName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager());
    }
    
    public static boolean hasWriteSecureSettingsPermission(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
    }

    public static void startForegroundService(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Settings.canDrawOverlays(context))
                context.startForegroundService(intent);
        } else
            context.startService(intent);
    }
    
    public static void killAllService(Context context) {
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

    public static boolean isServiceRunning() {
        return AppMeService.isRunning();
    }
    
    
}

