package com.appme.story.engine.app.tasks;

import android.content.Context;
import android.os.CountDownTimer;
import java.util.ArrayList;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.folders.Storage;
import com.appme.story.engine.app.tasks.FileScanningTask;
import com.appme.story.engine.app.models.FileScanningItem;
import com.appme.story.engine.widget.soundPool.SoundPoolManager;

public class SdcardTask {

    private static volatile SdcardTask Instance = null;
    private Context context;
    private OnSdCardTaskListener mOnSdCardTaskListener;

    public static SdcardTask getInstance() {
        SdcardTask localInstance = Instance;
        if (localInstance == null) {
            synchronized (SdcardTask.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new SdcardTask(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private SdcardTask(Context context) {
        this.context = context;     
    }

    public static SdcardTask with(Context context) {
        return new SdcardTask(context);
    }

    public SdcardTask startScanning() {
        if (mOnSdCardTaskListener != null) {
            mOnSdCardTaskListener.onSdCardScanning("Apk File Scanning In SdCard");
        }
        new CountDownTimer(1000, 1000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {  
                FileScanningTask appMe = new FileScanningTask(AppController.getContext(), FileScanningItem.APPLIST_STORAGE, Storage.getInstance().getInternalStorageDirectory(), FileScanningTask.APK);
                appMe.setOnFileScanningTaskListener(new FileScanningTask.OnFileScanningTaskListener(){
                        @Override
                        public void onPreExecute() {
                            SoundPoolManager.getInstance().playSound(R.raw.sound_add);                                        
                        }

                        @Override
                        public void onSuccess(ArrayList<FileScanningItem> result) {
                            SoundPoolManager.getInstance().playSound(R.raw.sound_done);                

                            if (mOnSdCardTaskListener != null) {
                                mOnSdCardTaskListener.onSuccess(result);
                            }
                        }

                        @Override
                        public void onProgressUpdate(String items) {

                        } 

                        @Override
                        public void onFailed(String message) {
                            SoundPoolManager.getInstance().playSound(R.raw.sound_error);                

                            if (mOnSdCardTaskListener != null) {
                                mOnSdCardTaskListener.onFailed(message);
                            }
                        } 

                        @Override
                        public void isEmpty() {
                            SoundPoolManager.getInstance().playSound(R.raw.sound_error);                

                            if (mOnSdCardTaskListener != null) {
                                mOnSdCardTaskListener.isEmpty("Apk File Not Found In SdCard");
                            }
                        } 
                    });
                appMe.execute();
            }
        }.start();	
        return this;
    }

    public void setOnSdCardTaskListener(OnSdCardTaskListener mOnSdCardTaskListener) {
        this.mOnSdCardTaskListener = mOnSdCardTaskListener;
    }

    public interface OnSdCardTaskListener {
        void onSdCardScanning(String message);
        void onSuccess(ArrayList<FileScanningItem> result);
        void onFailed(String message);
        void isEmpty(String message);
    }

}
