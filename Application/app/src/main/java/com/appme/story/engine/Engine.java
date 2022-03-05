package com.appme.story.engine;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Gravity;
import android.widget.Toast;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.folders.Storage;
import com.appme.story.engine.app.tasks.MemoryTask;
import com.appme.story.engine.app.tasks.SdcardTask;
import com.appme.story.engine.app.models.FileScanningItem;

public class Engine {
    
    private static volatile Engine Instance = null;
    private Context context;
    private OnScanningTaskListener mOnScanningTaskListener;
    private OnMemoryScanningTaskListener mOnMemoryScanningTaskListener;
    private OnSdCardScanningTaskListener mOnSdCardScanningTaskListener;
    
    public static Engine getInstance() {
        Engine localInstance = Instance;
        if (localInstance == null) {
            synchronized (Engine.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new Engine(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private Engine(Context context) {
        this.context = context;
        FolderMe.with(context).initFolder();     
    }

    public static Engine with(Context context) {
        return new Engine(context);
    }
    
    public Engine getMemoryScanning() {
        MemoryTask.with(context).startScanning()
            .setOnMemoryTaskListener(new MemoryTask.OnMemoryTaskListener(){
                @Override
                public void onMemoryScanning(String message) {
                    if (mOnMemoryScanningTaskListener != null) {
                        mOnMemoryScanningTaskListener.onMemoryScanning(message);
                    }
                }

                @Override
                public void onSuccess(ArrayList<FileScanningItem> result) {
                    if (mOnMemoryScanningTaskListener != null) {
                        mOnMemoryScanningTaskListener.onMemoryScanningSuccess(result);
                    }
                }

                @Override
                public void onFailed(String message) {
                    if (mOnMemoryScanningTaskListener != null) {
                        mOnMemoryScanningTaskListener.onMemoryScanningFailed(message);
                    }
                } 

                @Override
                public void isEmpty(String message) {
                    if (mOnMemoryScanningTaskListener != null) {
                        mOnMemoryScanningTaskListener.onMemoryIsEmpty(message);
                    }
                } 
            });
        return this;
    }

    public Engine getSdCardScanning() {
        SdcardTask.with(context).startScanning()
            .setOnSdCardTaskListener(new SdcardTask.OnSdCardTaskListener(){
                @Override
                public void onSdCardScanning(String message) {
                    if (mOnSdCardScanningTaskListener != null) {
                        mOnSdCardScanningTaskListener.onSdCardScanning(message);
                    }
                }

                @Override
                public void onSuccess(ArrayList<FileScanningItem> result) {
                    if (mOnSdCardScanningTaskListener != null) {
                        mOnSdCardScanningTaskListener.onSdCardScanningSuccess(result);
                    }
                }

                @Override
                public void onFailed(String message) {
                    if (mOnSdCardScanningTaskListener != null) {
                        mOnSdCardScanningTaskListener.onSdCardScanningFailed(message);
                    }
                } 

                @Override
                public void isEmpty(String message) {
                    if (mOnSdCardScanningTaskListener != null) {
                        mOnSdCardScanningTaskListener.onSdCardIsEmpty(message);
                    }
                } 
            });
        return this;
    }

    public void startScanning() { 
        if (mOnScanningTaskListener != null) {
            mOnScanningTaskListener.onMemoryScanning("Apk File Scanning In Memory");
        }
        MemoryTask.with(context).startScanning()
            .setOnMemoryTaskListener(new MemoryTask.OnMemoryTaskListener(){
                @Override
                public void onMemoryScanning(String message) {

                }

                @Override
                public void onSuccess(ArrayList<FileScanningItem> result) {

                    if (mOnScanningTaskListener != null) {
                        mOnScanningTaskListener.onMemoryScanningSuccess(result);
                    }
                    new CountDownTimer(2000, 2000){
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() { 
                            if (mOnScanningTaskListener != null) {
                                mOnScanningTaskListener.onSdCardScanning("Apk File Scanning In Sdcard");
                            }
                            SdcardTask.with(context).startScanning()
                                .setOnSdCardTaskListener(new SdcardTask.OnSdCardTaskListener(){
                                    @Override
                                    public void onSdCardScanning(String message) {

                                    }

                                    @Override
                                    public void onSuccess(ArrayList<FileScanningItem> result) {

                                        if (mOnScanningTaskListener != null) {
                                            mOnScanningTaskListener.onSdCardScanningSuccess(result);
                                        }
                                    }

                                    @Override
                                    public void onFailed(String message) {
                                        if (mOnScanningTaskListener != null) {
                                            mOnScanningTaskListener.onSdCardScanningFailed(message);
                                        }
                                    } 

                                    @Override
                                    public void isEmpty(String message) {
                                        if (mOnScanningTaskListener != null) {
                                            mOnScanningTaskListener.onSdCardIsEmpty(message);
                                        }
                                    } 
                                }); 
                        }
                    }.start();
                }

                @Override
                public void onFailed(String message) {
                    if (mOnScanningTaskListener != null) {
                        mOnScanningTaskListener.onMemoryScanningFailed(message);
                    }
                } 

                @Override
                public void isEmpty(String message) {
                    if (mOnScanningTaskListener != null) {
                        mOnScanningTaskListener.onMemoryIsEmpty(message);
                    }
                    new CountDownTimer(2000, 2000){
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() { 
                            if (mOnScanningTaskListener != null) {
                                mOnScanningTaskListener.onSdCardScanning("Apk File Scanning In Sdcard");
                            }
                            SdcardTask.with(context).startScanning()
                                .setOnSdCardTaskListener(new SdcardTask.OnSdCardTaskListener(){
                                    @Override
                                    public void onSdCardScanning(String message) {

                                    }

                                    @Override
                                    public void onSuccess(ArrayList<FileScanningItem> result) {

                                        if (mOnScanningTaskListener != null) {
                                            mOnScanningTaskListener.onSdCardScanningSuccess(result);
                                        }
                                    }

                                    @Override
                                    public void onFailed(String message) {
                                        if (mOnScanningTaskListener != null) {
                                            mOnScanningTaskListener.onSdCardScanningFailed(message);
                                        }
                                    } 

                                    @Override
                                    public void isEmpty(String message) {
                                        if (mOnScanningTaskListener != null) {
                                            mOnScanningTaskListener.onSdCardIsEmpty(message);
                                        }
                                    } 
                                }); 
                        }
                    }.start();
                } 
            });
    }
   
    public void createShortCut() {
        if (!isShortCut(context, "isShortcutMe")) {
            setIsShortCut(context, "isShortcutMe");
            createShortCutMe();
        }
    }

    public void createShortCut(String file) {
        createShortCutMe(file);
    }

    public void createShortCut(File file) {      
        createShortCutMe(file);
    }

    public void createShortCutMe() {   
        ShortCut.with(context).createShortCutMe(new File(FolderMe.getVideoIntro()));
    }

    public void createShortCutMe(String file) {   
        ShortCut.with(context).createShortCutMe(new File(file));
    }

    public void createShortCutMe(File file) {   
        ShortCut.with(context).createShortCutMe(file);
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
    
    public void setOnScanningTaskListener(OnScanningTaskListener mOnScanningTaskListener) {
        this.mOnScanningTaskListener = mOnScanningTaskListener;
    }

    public void setOnSdCardScanningTaskListener(OnScanningTaskListener mOnScanningTaskListener) {
        this.mOnScanningTaskListener = mOnScanningTaskListener;
    }

    public void setOnMemoryScanningTaskListener(OnScanningTaskListener mOnScanningTaskListener) {
        this.mOnScanningTaskListener = mOnScanningTaskListener;
    }

    public interface OnScanningTaskListener {
        void onMemoryScanning(String message);
        void onMemoryScanningSuccess(ArrayList<FileScanningItem> result);
        void onMemoryScanningFailed(String message);
        void onMemoryIsEmpty(String message);
        void onSdCardScanning(String message);
        void onSdCardScanningSuccess(ArrayList<FileScanningItem> result);
        void onSdCardScanningFailed(String message);
        void onSdCardIsEmpty(String message);
    }

    public interface OnSdCardScanningTaskListener {
        void onSdCardScanning(String message);
        void onSdCardScanningSuccess(ArrayList<FileScanningItem> result);
        void onSdCardScanningFailed(String message);
        void onSdCardIsEmpty(String message);
    }

    public interface OnMemoryScanningTaskListener {
        void onMemoryScanning(String message);
        void onMemoryScanningSuccess(ArrayList<FileScanningItem> result);
        void onMemoryScanningFailed(String message);
        void onMemoryIsEmpty(String message);
    }
}
