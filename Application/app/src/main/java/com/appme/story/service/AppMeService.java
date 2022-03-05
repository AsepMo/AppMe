package com.appme.story.service;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.app.Service;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.net.Uri;
import android.media.ToneGenerator;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RemoteViews;

import java.io.File;
import java.io.FileOutputStream;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.app.utils.NetWorkUtils;
import com.appme.story.receiver.SendBroadcast;

public class AppMeService extends Service {

    public static String TAG = AppMeService.class.getSimpleName();  
    private static AppMeService foregroundService;

    private static final boolean DEBUG = false;

    private final HandlerThread handlerThread = new HandlerThread(getClass().getSimpleName(), android.os.Process.THREAD_PRIORITY_BACKGROUND);
    private Handler handler;

    private WindowManager mWindowManager;

    private Handler mHandlerScreen;
    private Runnable mRunnerScreen = new Runnable(){
        @Override
        public void run() {

        }
    };

    private static boolean isRunning;
    private final ToneGenerator beeper = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
    private boolean isForeground = false;
    private BroadcastReceiver broadcastReceiver;
    
    private RemoteViews mContentViewBig, mContentViewSmall;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new IllegalStateException("Binding not supported. Go away.");
    }

    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    public Handler getHandler() {
        return handler;
    }

    @Override 
    public void onCreate() {
        super.onCreate();
        if (DEBUG) Log.v(TAG, "onCreate:");
        foregroundService = this;
        
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        isRunning = true;
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

       
        // Registering receiver for screen off messages
        final IntentFilter screenOnOffFilter = new IntentFilter();
        screenOnOffFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenOnOffFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_SCREEN_OFF)) {

                } else if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                }              
            }
        };

        registerReceiver(broadcastReceiver, screenOnOffFilter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.v(TAG, "onStartCommand:intent=" + intent);

        if (intent.getAction() == null) {
            String message = intent.getStringExtra(SendBroadcast.EXTRA_SERVICE);

            showNotification(); 
            beeper.startTone(ToneGenerator.TONE_PROP_ACK);
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.SERVICE_IS_READY, message);           
            isRunning = true;
        } else if (SendBroadcast.ACTION.START_SERVICE.equals(intent.getAction())) {
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.START_SERVICE, "Service Is Starter");           

        } else if (SendBroadcast.ACTION.PAUSE_SERVICE.equals(intent.getAction())) {       
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.PAUSE_SERVICE, "Service Is Paused");           

        } else if (SendBroadcast.ACTION.START_ACTIVITY.equals(intent.getAction())) {
            //SendBroadcast.getInstance().broadcastStatus(SendBroadcast.START_ACTIVITY, "Start Activity");           
            ApplicationActivity.start(this);
        } else if (SendBroadcast.ACTION.RESUME_SERVICE.equals(intent.getAction())) {
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.RESUME_SERVICE, "Service Is Resumed");           

        } else if (SendBroadcast.ACTION.OPEN_BROWSER.equals(intent.getAction())) {
            String mRootUrl = AppController.getServerIP();
            if (!TextUtils.isEmpty(mRootUrl)) {
                SendBroadcast.getInstance().broadcastStatus(SendBroadcast.OPEN_BROWSER, "Open Browser");                         
            } 
            ApplicationActivity.startChrome(this, mRootUrl);
            
        } else if (SendBroadcast.ACTION.SHUTDOWN_SERVICE.equals(intent.getAction())) {
            beeper.startTone(ToneGenerator.TONE_PROP_NACK);
            if (!isRunning) {
                showNotification();           
            }
            SendBroadcast.getInstance().broadcastStatus(SendBroadcast.SERVICE_IS_SHUTDOWN, "Service Is Shutdown");                                 
            stopForeground(true);
            stopSelf();
        }

        return(START_NOT_STICKY);

    }

    public static boolean isRunning() {
        return isRunning;
    }

    public boolean isConnected(){
        return NetWorkUtils.isConnected(this);
    }
    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        NotificationManager mgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ApplicationActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
        notification.setSmallIcon(R.drawable.apk_v2);  // the status icon
        notification.setWhen(System.currentTimeMillis());  // the time stamp
        notification.setContentIntent(contentIntent);  // The intent to send when the entry is clicked
        notification.setCustomContentView(getSmallContentView());
        notification.setCustomBigContentView(getBigContentView());
        notification.setPriority(NotificationCompat.PRIORITY_MAX);
        notification.setOngoing(true);

        Notification notif = notification.build();
        // Send the notification.
        if (isForeground) {
            mgr.notify(SendBroadcast.NOTIFY_ID, notif);
        } else {
            startForeground(SendBroadcast.NOTIFY_ID, notif);
            isForeground = true;
        }
        startForeground(SendBroadcast.NOTIFY_ID, notif);
    }

    private RemoteViews getSmallContentView() {
        if (mContentViewSmall == null) {
            mContentViewSmall = new RemoteViews(getPackageName(), R.layout.remote_view_music_player_small);
            setUpRemoteView(mContentViewSmall);
        }
        updateRemoteViews(mContentViewSmall);
        return mContentViewSmall;
    }

    private RemoteViews getBigContentView() {
        if (mContentViewBig == null) {
            mContentViewBig = new RemoteViews(getPackageName(), R.layout.remote_view_music_player);
            setUpRemoteView(mContentViewBig);
        }
        updateRemoteViews(mContentViewBig);
        return mContentViewBig;
    }

    private void setUpRemoteView(RemoteViews remoteView) {
        remoteView.setImageViewResource(R.id.image_view_close, R.drawable.ic_remote_view_close);
        remoteView.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_app_home);
        remoteView.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_google_chrome);

        remoteView.setOnClickPendingIntent(R.id.button_close, buildPendingIntent(SendBroadcast.ACTION.SHUTDOWN_SERVICE));
        remoteView.setOnClickPendingIntent(R.id.button_play_last, buildPendingIntent(SendBroadcast.ACTION.START_ACTIVITY));
        remoteView.setOnClickPendingIntent(R.id.button_play_next, buildPendingIntent(SendBroadcast.ACTION.OPEN_BROWSER));
        remoteView.setOnClickPendingIntent(R.id.button_play_toggle, buildPendingIntent(SendBroadcast.ACTION.PLAY_TOGGLE));
    }

    private void updateRemoteViews(RemoteViews remoteView) {
        String serverIP = AppController.getServerIP();
        if (serverIP != null) {
            remoteView.setTextViewText(R.id.text_view_name, getString(R.string.app_service_title));
            remoteView.setTextViewText(R.id.text_view_artist, getString(R.string.app_service_is_running));
        }
        remoteView.setImageViewResource(R.id.image_view_play_toggle, R.drawable.ic_monitor_screenshot);
        remoteView.setImageViewResource(R.id.image_view_album, R.drawable.apk_v2);
    }


    private PendingIntent buildPendingIntent(String action) {
        Intent i = new Intent(this, getClass());
        i.setAction(action);
        return(PendingIntent.getService(this, 0, i, 0));
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
       //foregroundServiceTaskHandler.getLooper().quit();
        stopForeground(true);      
    } 
}
