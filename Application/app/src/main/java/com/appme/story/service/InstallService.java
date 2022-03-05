package com.appme.story.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.os.IBinder;
import android.os.CountDownTimer;
import android.util.Log;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.appme.story.R;
import com.appme.story.SplashActivity;
import com.appme.story.application.Application;
import com.appme.story.engine.app.commons.Constant;
import com.appme.story.engine.app.folders.FileChecker;
import com.appme.story.engine.app.utils.ExceptionHandler;
import com.appme.story.engine.app.utils.Notify;
import com.appme.story.engine.app.folders.AssetManager;
import android.support.v7.app.AppCompatActivity;


@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
public class InstallService extends Service {
    public static String TAG = InstallService.class.getSimpleName();

    public String mFileName = "/index.html";
    public String mFilePath;

    public String mIndexFile;
    public String mFolder;
    public ExceptionHandler exceptionHandler;

    public Handler UIHandler;

    public Notify processNotify;

    public String checker = "cfr";

    public int STACK_SIZE;
    public boolean IGNORE_LIBS;
    private int TIME_LOAD = 5000;
    public void onCreate() {
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        /**
         * Initialize a handler for posting runnables that have to run on the UI thread
         */
        UIHandler = new Handler();

        /**
         * Receive action from the intent and decide whether to start or stop the existing process
         */
        if (intent.getAction().equals(Constant.ACTION.START_SERVICE)) {
            startForeground(Constant.PROCESS_NOTIFICATION_ID, buildNotification());
            broadcastStatus("check_index_file", "Check Index File");
        } else if (intent.getAction().equals(Constant.ACTION.CHECK_INDEX_FILE)) {                
            FileChecker.checkFile(InstallService.this);
        } else if (intent.getAction().equals(Constant.ACTION.EXTRACT_ASSETS)) {
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
            AssetManager.with(this).extract("web").setOnAssetManagerListener(new AssetManager.OnAssetManagerListener(){
                    @Override
                    public void onStart(String message) {
                        
                    }

                    @Override
                    public void onSuccess(String path) {
                        new CountDownTimer(TIME_LOAD, TIME_LOAD){
                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                Message msg = new Message();
                                msg.what = 2;
                                handler.sendMessage(msg);                                 
                            }  
                        }.start();
                     }

                    @Override
                    public void onFail(String path) {   
                        
                    }
                });
            
        } else if (intent.getAction().equals(Constant.ACTION.INSTALL_INDEX_FILE)) {            
            Message msg = new Message();
            msg.what = 4;
            handler.sendMessage(msg);
            new CountDownTimer(TIME_LOAD, TIME_LOAD){
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    Message msg = new Message();
                    msg.what = 5;
                    handler.sendMessage(msg);                                 
                }  
            }.start();
        } else if (intent.getAction().equals(Constant.ACTION.SYNCRON_DATA)) {       
            new CountDownTimer(TIME_LOAD, TIME_LOAD){
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    new Thread() {
                        @Override
                        public void run() {
                            publishProgress("start_activity");
                            broadcastStatus("start_activity", "Start Activity");                       
                        }
                    }.start();                              
                }  
            }.start();
        
        } else if (intent.getAction().equals(Constant.ACTION.START_ACTIVITY)) {   
            new CountDownTimer(TIME_LOAD, TIME_LOAD){
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    //Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();       
                    SplashActivity.start(getApplicationContext());  
                    publishProgress("start_activity");
                }  
            }.start();

        } else if (intent.getAction().equals(Constant.ACTION.STOP_SERVICE)) {       
            killSelf();
        }



        return START_NOT_STICKY;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    broadcastStatus("check_index_file", "Install Index File");                              
                    break;
                case 1:
                    broadcastStatus("extract_assets_to_storage", "Extract Assets To Storage");               
                    break;             
                case 2:
                    //broadcastStatus("extract_assets_to_storage", "Extract Assets To Storage Success..");       
                    new CountDownTimer(TIME_LOAD, TIME_LOAD){
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                            broadcastStatus("extract_assets_to_storage_success", "Extract Assets To Storage Success..");                               
                        }  
                    }.start();
                    break; 
                case 3:
                    broadcastStatus("start_activity_with_error", "Extract Assets To Storage Failed..");               
                    break;       
                case 4:
                    broadcastStatus("install_index_file", "Install Index File");               
                    break; 
                case 5:
                    broadcastStatus("syncron_index_file", "Syncron Data");               
                    break;                 
                default:
                    break;
            }
        }

    };

    public void publishProgress(String progressText) {
        switch (progressText) {
            
            case "start_activity": {
                    decompileDone();                  
                    kill();
                    break;
                }
            case "start_activity_with_error": {
                    decompileDone();
                    UIHandler.post(new ToastRunnable("Decompilation completed with errors. This incident has been reported to the developer."));
                    kill();
                    break;
                }
            case "exit_process_on_error":
                broadcastStatus(progressText);
                UIHandler.post(new ToastRunnable("The app you selected cannot be decompiled. Please select another app."));
                kill();
                break;
            default:
                break;
        }
    }

    private void decompileDone() {	
		//showCompletedNotification();
    }

    /*private void showCompletedNotification() {

        Intent resultIntent = new Intent(getApplicationContext(), SplashActivity.class);
        //resultIntent.putExtra("from_notification", true);
        //resultIntent.putExtra("source_dir", mFolder);

        PendingIntent resultPendingIntent =
            PendingIntent.getActivity(InstallService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Install Completed.")
            .setContentText("Tap to start activity")
            .setSmallIcon(R.drawable.stat_action_done)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            .setContentIntent(resultPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true);

        mNotifyManager.notify(2, mBuilder.build());
    }*/
    

    public void broadcastStatus(String status) {
        sendNotification(status, "");
        Intent localIntent = new Intent(Constant.BROADCAST_ACTION)
            .putExtra(Constant.STATUS_KEY, status);
        sendBroadcast(localIntent);
    }

    public void broadcastStatus(String statusKey, String statusData) {
        sendNotification(statusKey, statusData);
        Intent localIntent = new Intent(Constant.BROADCAST_ACTION)
            .putExtra(Constant.STATUS_KEY, statusKey)
            .putExtra(Constant.STATUS_MESSAGE, statusData);
        sendBroadcast(localIntent);
    }

    private void sendNotification(String statusKey, String statusData) {
        switch (statusKey) {
            case "check_index_file":
                processNotify.updateTitleText("Check Index file", "Processing ...");
                break;
            case "file_exist":
                processNotify.updateTitleText("File Exist", "Processing ...");
                break;
            case "file_not_found":
                processNotify.updateTitleText("File Not Found", "Processing ...");
                break;
            case "extract_assets_to_storage":
                processNotify.updateTitleText("Extract Assets To Storage", "Processing ...");
                break; 
            case "extract_assets_to_storage_success":
                processNotify.updateTitleText("Extract Assets To Storage Success", "Processing ...");
                break; 
            case "extract_assets_to_storage_failed":
                processNotify.updateTitleText("Extract Assets To Storage Failed", "Processing ...");
                break;    
            case "install_index_file":
                processNotify.updateTitleText("Install Index File", "Processing ...");
                break;
            case "syncron_index_file":
                processNotify.updateTitleText("Syncron Data", "Processing ...");
                break;        
            case "start_activity":
                processNotify.cancel();
                break;
            case "start_activity_with_error":
                processNotify.cancel();
                break;
            case "exit_process_on_error":
                processNotify.cancel();
                break;          
            case "exit":
                try {
                    processNotify.cancel();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                break;

            default:
                processNotify.updateText(statusData);
        }
    }

    private Notification buildNotification() {

        Intent stopIntent = new Intent(this, InstallService.class);
        stopIntent.setAction(Constant.ACTION.STOP_SERVICE);

        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, 0);

        NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Decompiling")
            .setContentText("Processing the apk")
            .setSmallIcon(R.drawable.stat_action_running)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
            .setOngoing(true)
            .addAction(R.drawable.ic_action_kill, "Stop decompiler", pendingStopIntent)
            .setAutoCancel(false);

        mBuilder.setProgress(0, 0, true);

        Notification notification = mBuilder.build();

        mNotifyManager.notify(Constant.PROCESS_NOTIFICATION_ID, notification);

        processNotify = new Notify(mNotifyManager, mBuilder, Constant.PROCESS_NOTIFICATION_ID);

        return notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            kill();
            processNotify.cancel();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void kill() {
        stopForeground(true);
        stopSelf();
    }

    private class ToastRunnable implements Runnable {

        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
        }
    }

    private void killSelf() {
        broadcastStatus("exit");
        stopForeground(true);
        try {
            NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyManager.cancel(Constant.PROCESS_NOTIFICATION_ID);
            ServiceUtils.killAllServices(this);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        stopSelf();
    }
}
