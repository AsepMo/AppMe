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

public class MemoryTask {

    private static volatile MemoryTask Instance = null;
    private Context context;
    private OnMemoryTaskListener mOnMemoryTaskListener;

    public static MemoryTask getInstance() {
        MemoryTask localInstance = Instance;
        if (localInstance == null) {
            synchronized (MemoryTask.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new MemoryTask(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private MemoryTask(Context context) {
        this.context = context;     
    }

    public static MemoryTask with(Context context) {
        return new MemoryTask(context);
    }

    public MemoryTask startScanning() {
        if (mOnMemoryTaskListener != null) {
            mOnMemoryTaskListener.onMemoryScanning("Apk File Scanning In Memory");
        }
        new CountDownTimer(1000, 1000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {  

                FileScanningTask appMe = new FileScanningTask(AppController.getContext(), FileScanningItem.APPLIST_MEMORY, Storage.getInstance().getExternalStorageDirectory(), FileScanningTask.APK);
                appMe.setOnFileScanningTaskListener(new FileScanningTask.OnFileScanningTaskListener(){
                        @Override
                        public void onPreExecute() {
                            SoundPoolManager.getInstance().playSound(R.raw.sound_add);                   
                        }

                        @Override
                        public void onSuccess(ArrayList<FileScanningItem> result) {
                            SoundPoolManager.getInstance().playSound(R.raw.sound_done);

                            if (mOnMemoryTaskListener != null) {
                                mOnMemoryTaskListener.onSuccess(result);
                            }
                        }

                        @Override
                        public void onProgressUpdate(String items) {

                        } 

                        @Override
                        public void onFailed(String message) {
                            SoundPoolManager.getInstance().playSound(R.raw.sound_error);

                            if (mOnMemoryTaskListener != null) {
                                mOnMemoryTaskListener.onFailed(message);
                            }
                        } 

                        @Override
                        public void isEmpty() {
                            SoundPoolManager.getInstance().playSound(R.raw.sound_error);

                            if (mOnMemoryTaskListener != null) {
                                mOnMemoryTaskListener.isEmpty("Apk File Not Found In Memory");
                            }
                        } 
                    });
                appMe.execute();
            }
        }.start();	
        return this;
    }

    public void setOnMemoryTaskListener(OnMemoryTaskListener mOnMemoryTaskListener) {
        this.mOnMemoryTaskListener = mOnMemoryTaskListener;
    }

    public interface OnMemoryTaskListener {
        void onMemoryScanning(String message);
        void onSuccess(ArrayList<FileScanningItem> result);
        void onFailed(String message);
        void isEmpty(String message);
    }
}
