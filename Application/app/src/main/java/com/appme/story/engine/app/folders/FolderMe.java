package com.appme.story.engine.app.folders;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.CountDownTimer;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.appme.story.R;
import com.appme.story.AppController;

public class FolderMe {

    private static volatile FolderMe sInstance = null;
    private Context context;
    private boolean isFolder = false;
    private String mFolder = null;

    /** Note that this is a symlink on the Android M preview. */
    @SuppressLint("SdCardPath")
    public static String EXTERNAL_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String APPME_FOLDER = getAppMeFolder();

    public static String FOLDER = getAppMeFolder();
    //Folder For PackageArchive
    public static String FOLDER_APK = FOLDER + "/1.Apk";
    public static String FOLDER_APK_BACKUP = FOLDER_APK + "/backup";
    
    //Folder For Image
    public static String FOLDER_IMAGE = FOLDER + "/2.Image";
    //Folder For Audio
    public static String FOLDER_AUDIO = FOLDER + "/3.Audio";
    public static String FOLDER_AUDIO_RECORDER = FOLDER_AUDIO + "/Recorder";
    public static String FOLDER_AUDIO_DOWNLOAD = FOLDER_AUDIO + "/Download";
    public static String FOLDER_AUDIO_CONVERT = FOLDER_AUDIO + "/Convert";
    //Folder For Video
    public static String FOLDER_VIDEO = FOLDER + "/4.Video";
    public static String FOLDER_VIDEO_RECORDER = FOLDER_VIDEO + "/Recorder";
    public static String FOLDER_VIDEO_DOWNLOAD = FOLDER_VIDEO + "/Download";
    public static String FOLDER_VIDEO_CONVERTED = FOLDER_VIDEO + "/Trimmer";
    //Folder For YouTube
    public static String FOLDER_YOUTUBE = FOLDER_VIDEO + "/Youtube";
    public static String FOLDER_YOUTUBE_ANALYTICS = FOLDER_YOUTUBE + "/Analytics";
    public static String FOLDER_YOUTUBE_DOWNLOAD = FOLDER_YOUTUBE + "/Download";
    //Folder For Ebook
    public static String FOLDER_EBOOK = FOLDER + "/5.Ebook";
    //Folder For Script
    public static String FOLDER_SCRIPTME = FOLDER + "/6.ScriptMe";
    //Folder For Archive
    public static String FOLDER_ARCHIVE = FOLDER + "/7.Archive";
	public static String FOLDER_ARCHIVE_EXTRACTION = FOLDER_ARCHIVE + "/Extracted";
	public static String FOLDER_ARCHIVE_ARCHIVES = FOLDER_ARCHIVE + "/Archives";
	
    
    /*====================*/
    /*== Default Folder ==*/
    /*====================*/
    //AppMe Folder In External Storage
    public static String getAppMeFolder() {
        File folder = new File(EXTERNAL_DIR + "/" + getContext().getString(R.string.app_name));
		if (!folder.exists()) {
			folder.mkdirs();
		}	
        return folder.getAbsolutePath();
    }

    //AppMe Folder In External Storage
    public static String getExternalFolder(String mFolder) {
        File folder = new File(FolderMe.getInstance().getExternalFileDir(mFolder));
		if (!folder.exists()) {
			folder.mkdirs();
		}	
        return folder.getAbsolutePath();
    }
    
    public static File getApkFolder(){
        File apkFolder = new File(FOLDER_APK);
        if (!apkFolder.exists()) {
            apkFolder.mkdirs();
        }   
        return apkFolder;
    }

    public static File getBackUpFolder(){
        File apkFolder = new File(FOLDER_APK_BACKUP);
        if (!apkFolder.exists()) {
            apkFolder.mkdirs();
        }   
        return apkFolder;
    }
    
    public static File getVideoFolder(){
        File videoFolder = new File(FOLDER_VIDEO);
        if (!videoFolder.exists()) {
            videoFolder.mkdirs();
        }   
        return videoFolder;
    }
    
    /*public static String getAppMeArchive(){
        return getBackUpFolder().getAbsolutePath() + getContext().getPackageName() + "_v" + AppUtil.getPackageVersionName(getContext()) + ".apk";
    }*/
    
    public static String getVideoLoading(){
        return HOME_WEB_PATH + "/loading_sound_effects.mp4";
    }
    
    public static String getVideoIntro(){
        return HOME_WEB_PATH + "/video_intro.mp4";
    }
    
    //AppMe Folder In Internal Storage
    public static String getInternalFolder(String mFolder) {
        File folder = new File(FolderMe.getInstance().getInternalFileDir(mFolder));
		if (!folder.exists()) {
			folder.mkdirs();
		}	
        return folder.getAbsolutePath();
    }
	
    public static String getFolderAnalytics(){
        String folder = getContext().getExternalFilesDir("analytics").getAbsolutePath();
        
        return folder;
    }
    
    public static String getFolderScanning(){
        String folder = getContext().getExternalFilesDir("scanning").getAbsolutePath();

        return folder;
    }
    
	//WebServer Folder
	public static final String HOME_WEB_PATH = FOLDER_SCRIPTME + "/web";
    public static final String FOLDER_WEB_CLIENT = HOME_WEB_PATH + "/client";
    public static final String FOLDER_WEB_EDITOR = HOME_WEB_PATH + "/editor";
    public static final String FOLDER_FILE_TRANSFER = HOME_WEB_PATH + "/folders";

    public static File getWebFolder(){
        File mHome_Web_Path = new File(HOME_WEB_PATH);
		if (!mHome_Web_Path.exists()) {
			mHome_Web_Path.mkdirs();
		}	
        return mHome_Web_Path;
    }
    
    public static File getFileTransfer(){
        File mHome_Web_Path = new File(FOLDER_FILE_TRANSFER);
        if (!mHome_Web_Path.exists()) {
            mHome_Web_Path.mkdirs();
        }   
        return mHome_Web_Path;
    }
	
    public static String getServerIP(){
        return HOME_WEB_PATH + "/ip-address.json";
    }
    
    public static Context getContext() {
        return AppController.getContext();
    }

    public static FolderMe getInstance() {
        FolderMe localInstance = sInstance;
        if (localInstance == null) {
            synchronized (FolderMe.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new FolderMe(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private FolderMe(Context context) {
        this.context = context; 
		if (externalAvailable()) {
            File nomedia = new File(getAppMeFolder(), ".nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists()) {
                try {
                    nomedia.createNewFile();
                } catch (IOException io) {
                    io.getMessage();
                }
            }

            String ExternalFolder = getExternalFileDir(null);
            File mExternalDir = new File(ExternalFolder);
            mExternalDir.getParentFile().mkdirs();
            if (!mExternalDir.exists()) {
                mExternalDir.mkdirs();
            }

            String home = getHomeDir();
            File mHome = new File(home);
            mHome.getParentFile().mkdirs();
            if (!mHome.exists()) {
                mHome.mkdirs();
            }

            String user = getUserDir();
            File mUser = new File(user);
            mUser.getParentFile().mkdirs();
            if (!mUser.exists()) {
                mUser.mkdirs();
            }  
        }    
    }

    public static FolderMe with(Context context) {
        return new FolderMe(context);
    }

    public void initFolder() {		
		FolderMe.with(getContext())
                    .setFolderScanning()
					.setFolderApk()
					.setFolderImage()
					.setFolderScriptMe()
					.setFolderScriptMe_Web()
					.setFolderAudio()
					.setFolderEbook()
					.setFolderVideo()
					.setFolderArchive();	  
    }

    public FolderMe setFolder(String folder) {
        this.isFolder = !TextUtils.isEmpty(folder);
        this.mFolder = folder;
        File mFolderMe = new File(folder);
        if (!mFolderMe.exists()) {
            mFolderMe.mkdirs();
        }
        return this;
    }

    public FolderMe setFolderScanning() {
        String folder = getContext().getExternalFilesDir("scanning").getAbsolutePath();
        if (externalAvailable()) {

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }
    
    public FolderMe setFolderApk() {
        if (externalAvailable()) {

            File mFolderMe = new File(FOLDER_APK);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public static File getFolderApk() {
        String type = FOLDER_APK;
        if (externalAvailable()) {
            File root = Environment.getExternalStorageDirectory();
            File appRoot = new File(root, APPME_FOLDER);
            FileMeUtil.mkdir(appRoot, getContext());

            if (TextUtils.isEmpty(type)) {
                return appRoot;
            }

            File dir = new File(appRoot, type);
            FileMeUtil.mkdir(dir, getContext());

            return dir;
        }
        throw new RuntimeException("External storage device is not available.");
    }

    public FolderMe setFolderImage() {
        if (externalAvailable()) {
            
            File mFolderMe = new File(FOLDER_IMAGE);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public static File getFolderImage() {
        String type = FOLDER_IMAGE;
        if (externalAvailable()) {
            File root = Environment.getExternalStorageDirectory();
            File appRoot = new File(root, APPME_FOLDER);
            FileMeUtil.mkdir(appRoot, getContext());

            if (TextUtils.isEmpty(type)) {
                return appRoot;
            }

            File dir = new File(appRoot, type);
            FileMeUtil.mkdir(dir, getContext());

            return dir;
        }
        throw new RuntimeException("External storage device is not available.");
    }
    
    public FolderMe setFolderScriptMe() {
        if (externalAvailable()) {

            File mFolderMe = new File(FOLDER_SCRIPTME);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }
	
    public static File getFolderScriptMe() {
        String type = FOLDER_SCRIPTME;
        if (externalAvailable()) {
            File root = Environment.getExternalStorageDirectory();
            File appRoot = new File(root, APPME_FOLDER);
            FileMeUtil.mkdir(appRoot, getContext());

            if (TextUtils.isEmpty(type)) {
                return appRoot;
            }

            File dir = new File(appRoot, type);
            FileMeUtil.mkdir(dir, getContext());

            return dir;
        }
        throw new RuntimeException("External storage device is not available.");
    }
    
	public FolderMe setFolderScriptMe_Web() {
        if (externalAvailable()) {

            File mFolderMe = new File(HOME_WEB_PATH);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }
	
	public static File getFolderScriptMe_Web() {
        String type = HOME_WEB_PATH;
        if (externalAvailable()) {
            File root = new File(getAppMeFolder());
            File appRoot = new File(root, HOME_WEB_PATH);
            FileMeUtil.mkdir(appRoot, getContext());

            if (TextUtils.isEmpty(type)) {
                return appRoot;
            }

            File dir = new File(appRoot, type);
            FileMeUtil.mkdir(dir, getContext());

            return dir;
        }
        throw new RuntimeException("External storage device is not available.");
    }
    
	
    public FolderMe setFolderAudio() {
        if (externalAvailable()) {

            File mFolderMe = new File(FOLDER_AUDIO);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public static File getFolderAudio() {
        String type = FOLDER_AUDIO;
        if (externalAvailable()) {
            File root = Environment.getExternalStorageDirectory();
            File appRoot = new File(root, APPME_FOLDER);
            FileMeUtil.mkdir(appRoot, getContext());

            if (TextUtils.isEmpty(type)) {
                return appRoot;
            }

            File dir = new File(appRoot, type);
            FileMeUtil.mkdir(dir, getContext());

            return dir;
        }
        throw new RuntimeException("External storage device is not available.");
    }
    
    public FolderMe setFolderAudio_Converted() {
        String folder = FOLDER_AUDIO_CONVERT;
        if (externalAvailable()) {

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public FolderMe setFolderVideo() {
        String folder = FOLDER_VIDEO;
        if (externalAvailable()) {

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public static File getFolderVideo() {
        String type = FOLDER_VIDEO;
        if (externalAvailable()) {
            File root = Environment.getExternalStorageDirectory();
            File appRoot = new File(root, APPME_FOLDER);
            FileMeUtil.mkdir(appRoot, getContext());

            if (TextUtils.isEmpty(type)) {
                return appRoot;
            }

            File dir = new File(appRoot, type);
            FileMeUtil.mkdir(dir, getContext());

            return dir;
        }
        throw new RuntimeException("External storage device is not available.");
    }
    
    public FolderMe setFolderVideoConverted() {
        String folder = FOLDER_VIDEO_CONVERTED;
        if (externalAvailable()) {

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public FolderMe setFolderYoutube() {
        String folder = FOLDER_YOUTUBE;
        if (externalAvailable()) {

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public FolderMe setFolderYoutube_Analytics() {
        String folder = FOLDER_YOUTUBE_ANALYTICS;
        if (externalAvailable()) {

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public FolderMe setFolderYoutube_Download() {
        String folder = FOLDER_YOUTUBE_DOWNLOAD;
        if (externalAvailable()) {

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }


    public FolderMe setFolderEbook() {
        String folder = FOLDER_EBOOK;
        if (externalAvailable()) {

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public FolderMe setFolderArchive() {
        String folder = FOLDER_ARCHIVE;
        if (externalAvailable()) {

            File mFolderMe = new File(folder);
            if (!mFolderMe.exists()) {
                FileMeUtil.mkdir(mFolderMe, getContext());
            }
        }
        return this;
    }

    public FolderMe setExternalFileDir(String folder) { 
        if (externalAvailable()) {
            File nomedia = new File(getContext().getExternalFilesDir(folder).getAbsolutePath(), ".nomedia");
            nomedia.getParentFile().mkdirs();
            if (!nomedia.exists()) {
                try {
                    nomedia.createNewFile();
                } catch (IOException io) {
                    io.getMessage();
                }
            }
        }
        return this;
    }

    public String getHomeDir() {
        return getContext().getFilesDir().getAbsolutePath() + "/home";
    }

    public String getUserDir() {
        return getContext().getFilesDir().getAbsolutePath() + "/user";
    }

    public String getExternalCacheDir() {
        return getContext().getExternalCacheDir().getAbsolutePath();
    }

    public String getInternalCacheDir() {
        return getContext().getCacheDir().getAbsolutePath();
    }

    public String getExternalFileDir(String folder) {
        return getContext().getExternalFilesDir(folder).getAbsolutePath();
    }

    public String getInternalFileDir(String folder) {
        return FolderMe.getInstance().getFileDir(AppController.getContext(), folder).getAbsolutePath();
    }

    public static File getFileDir(Context context) {
        return getFileDir(context, null);
    }

    public static File getFileDir(Context context, @Nullable String type) {
        File root = context.getFilesDir();
        if (TextUtils.isEmpty(type)) {
            return root;
        } else {
            File dir = new File(root, type);
            FileMeUtil.mkdir(dir, getContext());
            return dir;
        }
    }

    public static boolean externalAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static File getExternalDir(Context context, @Nullable String type) {
        if (externalAvailable()) {
            if (TextUtils.isEmpty(type)) {
                return context.getExternalFilesDir(null);
            }

            File dir = context.getExternalFilesDir(type);
            if (dir == null) {
                dir = context.getExternalFilesDir(null);
                dir = new File(dir, type);
                FileMeUtil.mkdir(dir, getContext());         
            }

            return dir;
        }
        throw new RuntimeException("External storage device is not available.");
    }

    public static File getRootDir(@Nullable String type) {
        if (externalAvailable()) {
            File root = Environment.getExternalStorageDirectory();
            File appRoot = new File(root, APPME_FOLDER);
            FileMeUtil.mkdir(appRoot, getContext());
          

            if (TextUtils.isEmpty(type)) {
                return appRoot;
            }

            File dir = new File(appRoot, type);
            FileMeUtil.mkdir(dir, getContext());

            return dir;
        }
        throw new RuntimeException("External storage device is not available.");
    }


    /**
     *get the internal or outside sd card path
     * @param is_removale true is is outside sd card
     * */
    public static String getExternalStorageDirectory(Context mContext, boolean is_removale) {
        return Environment.getExternalStorageDirectory().getAbsolutePath();   
    }

    public static String getInternalStorageDirectory(Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	/*public boolean remove(String folder) { 
	   boolean isRemove = FileMe.remove(AppController.getContext(), folder);
	   return isRemove;
	}*/
}
