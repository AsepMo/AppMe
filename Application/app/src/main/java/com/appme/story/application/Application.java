package com.appme.story.application;

import android.Manifest;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.preference.PreferenceManager;
import android.os.PowerManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Gravity;
import android.widget.Toast;

import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.POWER_SERVICE;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.commons.Constant;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.models.FileScanningItem;
import com.appme.story.engine.app.tasks.FileScanningTask;
import com.appme.story.engine.app.analytics.permissions.PermissionsManager;
import com.appme.story.engine.app.analytics.permissions.PermissionsResultAction;
import com.appme.story.engine.app.analytics.AppsPackNames;
import com.appme.story.engine.widget.soundPool.SoundPoolManager;
import com.appme.story.engine.widget.soundPool.ISoundPoolLoaded;

public class Application {
    
    private static final String TAG = Application.class.getSimpleName();
    private static volatile Application Instance = null;
    private Context context;
    private int SPLASH_TIME_OUT = 2000;
    private SharedPreferences mSP;
    private ActivityManager am;

    private OnUpdateListener mOnUpdateListener;
    public static Application getInstance() {
        Application localInstance = Instance;
        if (localInstance == null) {
            synchronized (Application.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new Application(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private Application(Context context) {
        this.context = context;
        FolderMe.with(context).initFolder();
        mSP = PreferenceManager.getDefaultSharedPreferences(context);
        am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public static Application with(Context context) {
        return new Application(context);
    }

    public void setInitialize(AppCompatActivity mActivity) {
        PackageManager packageManager = mActivity.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(mActivity.getPackageName(), PackageManager.GET_PERMISSIONS);
            File appFile = new File(packageInfo.applicationInfo.sourceDir);

            FileScanningItem mAppMe = new FileScanningItem();
            mAppMe.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            mAppMe.setAppThumbnail(packageInfo.applicationInfo.loadIcon(packageManager));
            mAppMe.setAppSize(FileUtils.byteCountToDisplaySize(appFile.length()));
            mAppMe.setPackageName(packageInfo.packageName);
            mAppMe.setVersionName(packageInfo.versionName);
            mAppMe.setVersionCode(packageInfo.versionCode);
            mAppMe.setAppUpdate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(appFile.lastModified())).toString());
            mAppMe.setAppLocation(packageInfo.applicationInfo.sourceDir);
            mAppMe.setAppListNumber(1);
            mAppMe.initialise(mAppMe);
            // Fill file stats (code, cache and data size)
            //getPackageStats(packageManager, packageInfo);

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void setAppMeBackup(final AppCompatActivity mActivity) {      
        new CountDownTimer(2000, 2000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
               /* final PackageManager packageManager = mActivity.getPackageManager();
                try {
                    final PackageInfo packageInfo = packageManager.getPackageInfo(mActivity.getPackageName(), PackageManager.GET_PERMISSIONS);
                    boolean backup = AppUtil.backupApk(mActivity, packageInfo.applicationInfo.sourceDir);
                    if (backup) {
                        Toast.makeText(mActivity, packageInfo.applicationInfo.loadLabel(packageManager).toString() + "  Berhasil Di Backup", Toast.LENGTH_SHORT).show();
                    }
                }catch (NameNotFoundException e) {
                    e.printStackTrace();
                }*/
            }  
        }.start();
    }
    
    public void setPermission(final Activity act, final String[] permissions, final OnActionPermissionListener mOnActionPermissionListener) {      
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(act, permissions, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    new CountDownTimer(SPLASH_TIME_OUT, SPLASH_TIME_OUT){
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {  
                            soundPlay(R.raw.sound_done);
                            mOnActionPermissionListener.onGranted();
                        }
                    }.start();
                }


                @Override
                public void onDenied(final String permission) {
                    new CountDownTimer(SPLASH_TIME_OUT, SPLASH_TIME_OUT){
                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {  
                            soundPlay(R.raw.sound_error);
                            mOnActionPermissionListener.onDenied(permission);
                        }
                    }.start();
                }
            });
    }

    public void launchYouTube() {
        try {
            Application.getInstance().launch(AppsPackNames.YOUTUBE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Start app from package name if user has added it to launchable app list
     */
    public void launch(String packageName) throws Exception {

        Log.d(TAG, "Starting app " + packageName);
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);                  
        context.startActivity(intent);

    }

    public void launch(String packageName, SharedPreferences mSP) throws Exception {
        for (String pkgName : mSP.getStringSet(Constant.APP_LIST, new HashSet<String>(0))) {
            if (packageName.equals(pkgName)) {
                //launch app
                Log.d(TAG, "Starting app " + packageName);
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
                KeyguardManager km = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
                final KeyguardManager.KeyguardLock kl=km.newKeyguardLock("WebRemoteDroid");
                kl.disableKeyguard();

                PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
                PowerManager.WakeLock wl=pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, TAG);
                wl.acquire();
                context.startActivity(intent);

                //let the app have some time to start before removing wakelock and keyguard
                Thread.sleep(5000);
                wl.release();
                kl.reenableKeyguard();

                return;
            }
        }

        throw new Exception("App " + packageName + " is not authorized to be started remotely (or is an invalid package name)");
    }

    private int findPIDbyPackageName(String packagename) {
        int result = -1;

        if (am != null) {
            for (ActivityManager.RunningAppProcessInfo pi : am.getRunningAppProcesses()) {
                if (pi.processName.equalsIgnoreCase(packagename)) {
                    result = pi.pid;
                }
                if (result != -1) break;
            }
        } else {
            result = -1;
        }

        return result;
    }

    private boolean isPackageRunning(String packagename) {
        return findPIDbyPackageName(packagename) != -1;
    }

    /**
     * Kill processes from a given package name
     * @param packagename
     * @return
     */
    public boolean killPackageProcesses(String packagename) {
        boolean result = false;

        if (am != null) {
            am.killBackgroundProcesses(packagename);
            result = !isPackageRunning(packagename);
        } else {
            result = false;
        }

        return result;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,  @NonNull int[] grantResults) {
        Log.i(TAG, "Activity-onRequestPermissionsResult() PermissionsManager.notifyPermissionsChange()");
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    public void setTransitionListener(final AppCompatActivity activity, final String message, final Class<?> mClass) {
        new CountDownTimer(2000, 2000){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();       

                Intent mIntent = new Intent(activity, mClass);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(mIntent);
                activity.finish();
            }  
        }.start();
    }

    public void soundPlay(int sound) {
        SoundPoolManager.getInstance().playSound(sound);
    }


    public void soundRelease() {
        SoundPoolManager.getInstance().release();
    }


    public static String[] requestPermissionConnection = new String[]
    {
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_NETWORK_STATE,
        Manifest.permission.CHANGE_WIFI_STATE
    };

    public static String[] requestPermissionStorage = new String[]
    {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static String[] requestPermissionShortcut = new String[]
    {
        Manifest.permission.INSTALL_SHORTCUT,
        Manifest.permission.UNINSTALL_SHORTCUT
    };

    public void setOnUpdateListener(OnUpdateListener mOnUpdateListener) {
        this.mOnUpdateListener = mOnUpdateListener;
    }

    public void exitApplication(Context c) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(intent);
    }

    public interface OnActionPermissionListener {
        void onGranted();
        void onDenied(String permission);
    }

    public interface OnApplicationTaskListener {
        void onPreExecute();
        void onSuccess(ArrayList<FileScanningItem> result);
        void onFailed();
        void isEmpty();
    }

    public interface OnUpdateListener {
        void onNetworkChecker(String message);
        void onUpdate(String versionName, int versionCode);
    }
    
}
