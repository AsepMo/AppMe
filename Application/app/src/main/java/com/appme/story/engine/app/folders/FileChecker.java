package com.appme.story.engine.app.folders;

import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;

import com.appme.story.AppController;
import com.appme.story.service.InstallService;

public class FileChecker {
    private static final String TAG = FileChecker.class.getSimpleName();

    private static volatile FileChecker Instance = null;
    private Context context;
    
    private OnStartActivityListener mOnStartActivityListener;
    public static FileChecker getInstance() {
        FileChecker localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileChecker.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new FileChecker(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private FileChecker(Context context) {
        this.context = context;         
    }

    public static FileChecker with(Context context) {
        return new FileChecker(context);
    }
    
    private Handler mHandler;
    private Runnable mStartActivity = new Runnable(){
        @Override
        public void run(){
            if(mOnStartActivityListener != null){
                mOnStartActivityListener.onStart(FileChecker.this);
            }
        }
    };
    
    public void initChecker()
    {
        mHandler = new Handler();
        mHandler.postDelayed(mStartActivity, 3000);
    }
    
    public void setStartActivity(OnStartActivityListener mOnStartActivityListener){
        this.mOnStartActivityListener = mOnStartActivityListener;
    }
    
    public static void checkFile(InstallService installService) {
        FileInstaller fileInstaller = new FileInstaller(installService);
        fileInstaller.install();
    }
    
    public static void install(InstallService installService) {
        FileInstaller fileInstaller = new FileInstaller(installService);
        fileInstaller.install();
    }
    
    public void removeCallback(){
        mHandler.removeCallbacks(mStartActivity);
    }
    
    /**
     * Contains all possible places to check binaries
     */
    private static final String[] pathList;

    private static final String KEY_INDEX_HTML = "index.html";

    static {
        pathList = new String[]{
            FolderMe.HOME_WEB_PATH + "/",
            FolderMe.FOLDER_WEB_CLIENT + "/",
            FolderMe.FOLDER_WEB_EDITOR + "/",
            //FolderMe.FOLDER_WEB_SERVER + "/",
            //FolderMe.FOLDER_FILE_SERVER + "/",
        };
    }

    public static boolean isExists() {
        return doesFileExists(KEY_INDEX_HTML);
    }

    /**
     * Checks the all path until it finds it and return immediately.
     *
     * @param value must be only the binary name
     * @return if the value is found in any provided path
     */
    private static boolean doesFileExists(String value) {
        boolean result = false;
        for (String path : pathList) {
            File file = new File(path + "/" + value);
            result = file.exists();
            if (result) {
                Log.d(TAG, path + " contains index.html binary");
                break;
            }
        }
        return result;
    }
    
    public interface OnStartActivityListener {
        void onStart(FileChecker fileChecker); 
    }
}
