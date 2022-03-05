package com.appme.story;

import android.support.v7.app.AppCompatDelegate;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.appme.story.application.Application;
import com.appme.story.application.ApplicationMain;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.analytics.Analytics;
import com.appme.story.engine.app.analytics.CrashHandler;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.models.AppItem;
import com.appme.story.engine.app.commons.manager.DataManager;
import com.appme.story.engine.app.commons.db.DbHistoryExecutor;
import com.appme.story.engine.app.commons.db.DbIgnoreExecutor;
import com.appme.story.engine.app.utils.PreferenceManager;
import com.appme.story.engine.app.utils.NetWorkUtils;
import com.appme.story.engine.widget.soundPool.SoundPoolManager;
import com.appme.story.engine.widget.soundPool.ISoundPoolLoaded;
import com.appme.story.service.MonitorUsageService;
import com.appme.story.settings.FolderSettings;
import com.appme.story.settings.FileScanningPreference;

public class AppController extends ApplicationMain {
    private static AppController sAppController;
    private Analytics mAnalytics = null;
    private Application mApplication = null;
    private FolderMe mFolderMe = null;
	private Engine mEngineMe = null;
    private static FileScanningPreference appPreferences = null;
    public FolderSettings folderPreferences = new FolderSettings(this);
    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        sAppController = this;        
    }
    
    public static synchronized AppController getInstance() {
        return sAppController;
    }
    
     @Override
    public void initAnalytics() {
        super.initAnalytics();
    }

    @Override
    public void initCrashHandler() {
        super.initCrashHandler();
        CrashHandler.init(this);
    }

    @Override
    public void initConfig() {
        super.initConfig();
        PreferenceManager.init(this);
        getContext().startService(new Intent(getContext(), MonitorUsageService.class));
        DbIgnoreExecutor.init(getContext());
        DbHistoryExecutor.init(getContext());
        DataManager.init();
        addDefaultIgnoreAppsToDB();
    }

    @Override
    public void initFolder() {
        super.initFolder();
        folderPreferences.load();
        if (!folderPreferences.isInitialized())
        {
            folderPreferences.setInitialized(true);
            File mFolderMe = new File(FolderMe.FOLDER);
            if (mFolderMe != null) folderPreferences.setStorePath(mFolderMe.getPath());
            folderPreferences.save();
        }                
    }

    @Override
    public void initSoundManager() {
        super.initSoundManager();
        SoundPoolManager.CreateInstance();
        List<Integer> sounds = new ArrayList<Integer>();
        sounds.add(R.raw.sound_add);
        sounds.add(R.raw.sound_done);
        sounds.add(R.raw.sound_error);
        sounds.add(R.raw.sound_beep);
        sounds.add(R.raw.sound_click);
        sounds.add(R.raw.sound_jumping);
        sounds.add(R.raw.sound_jumping_failed);
        sounds.add(R.raw.sound_start_task);
        sounds.add(R.raw.sound_success_task);
        SoundPoolManager.getInstance().setSounds(sounds);
        try {
            SoundPoolManager.getInstance().InitializeSoundPool(getContext(), new ISoundPoolLoaded() {
                    @Override
                    public void onSuccess() {
                        //SoundPoolManager.getInstance().playSound(R.raw.sound_success_task);
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }

        SoundPoolManager.getInstance().setPlaySound(true);
        
    }
   
    public Analytics getAnalytics() {
        if(mAnalytics == null){
            mAnalytics = Analytics.with(getContext());
        }
        return mAnalytics;
    }

    public Application getAppMe() {
        if(mApplication == null){
            mApplication = Application.with(getContext());
        }
        return mApplication;
    }

    public FolderMe getFolderMe(){
        if(mFolderMe == null){
            mFolderMe = FolderMe.with(getContext());
        }
        return mFolderMe;
    }

    public Engine getEngineMe(){
        if(mEngineMe == null){
            mEngineMe = Engine.with(getContext());
        }
        return mEngineMe;
    }
    
    public static FileScanningPreference getFileScanningPreferences()
    {
        if (appPreferences == null)
            appPreferences = FileScanningPreference.loadPreferences(getContext());
        return appPreferences;
    }
	
    public static String getServerIP() {
        return "http://" + NetWorkUtils.getLocalIpAddress() + ":8090";
    }
    
    private void addDefaultIgnoreAppsToDB() {
        new Thread(new Runnable() {
                @Override
                public void run() {
                    List<String> mDefaults = new ArrayList<>();
                    mDefaults.add("com.android.settings");
                    mDefaults.add(getApplicationContext().getPackageName());
                    for (String packageName : mDefaults) {
                        AppItem item = new AppItem();
                        item.mPackageName = packageName;
                        item.mEventTime = System.currentTimeMillis();
                        DbIgnoreExecutor.getInstance().insertItem(item);
                    }
                }
            }).run();
    }
}
