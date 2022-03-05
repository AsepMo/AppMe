package com.appme.story.engine.app.analytics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.application.Application;
import com.appme.story.engine.Engine;
import com.appme.story.engine.app.folders.FileMe;
import com.appme.story.engine.app.folders.FolderMe;
import com.appme.story.engine.app.models.FileScanningItem;
import com.appme.story.engine.app.utils.NetWorkUtils;

public class Analytics {

    private static final String TAG = Analytics.class.getSimpleName();
    private static volatile Analytics Instance = null;
    private Context context;
	private SharedPreferences mSharedPreference;
	/** An intent for launching the system settings. */
    private static final Intent sSettingsIntent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
    private OnNetworkState mOnNetworkState;
    
    public static Analytics getInstance() {
        Analytics localInstance = Instance;
        if (localInstance == null) {
            synchronized (Analytics.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new Analytics(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private Analytics(Context context) {
        this.context = context;
		mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);

        FolderMe.with(context).initFolder();
    }

    public static Analytics with(Context context) {
        return new Analytics(context);
    }

	public Analytics setAnalytisActivity(OnFirstTimeListener mOnFirstTimeListener) {
        /**** START APP ****/
		boolean isFirstStart = mSharedPreference.getBoolean("firstStart", true);
        if (isFirstStart) {
            SharedPreferences.Editor e = mSharedPreference.edit();
            e.putBoolean("firstStart", false);
            e.apply();
            if (mOnFirstTimeListener != null) {
				mOnFirstTimeListener.onFirsTime();
			}
		} else {
			if (mOnFirstTimeListener != null) {
				mOnFirstTimeListener.onSecondTime();
			}
		}
		return this;
	}

    public void checkNetwork(final TextView tv) {
        if (!NetWorkUtils.getNetworkStatus(context)) { 
            if(mOnNetworkState != null){
                mOnNetworkState.onNetworkConnected();
            }
            //ToastUtils.show(context, "Network Is Connected");
            return;
        }else{
            if(mOnNetworkState != null){
                mOnNetworkState.onNetworkDisConnected();
            }
            //ToastUtils.show(context, "Network.Is DisConnected");            
        }
    }
    
    public void setServerIP()
    {
        try
        {
            JSONObject json = new JSONObject();
            json.put("title", "IP Address Analytics");
            json.put("ip_address", AppController.getServerIP());
            String serverJson = FolderMe.getServerIP();
            File serverIP = new File(serverJson);
            serverIP.getParentFile().mkdirs();
            FileUtils.writeStringToFile(serverIP, json.toString());
        }
        catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }
    }
    
    public String getServerIP()
    {
        try
        {
            String serverJson = FolderMe.getServerIP();           
            File serverIP = new File(serverJson);
            JSONObject json = new JSONObject(FileUtils.readFileToString(serverIP));
            return json.getString("ip_address");
        }
        catch (IOException | JSONException e)
        {
            return null;
        }
	}
    
    public void setOnNetworkStatusListener(OnNetworkState mOnNetworkState){
        this.mOnNetworkState = mOnNetworkState;
    }
    
	public interface OnFirstTimeListener {
		void onFirsTime();
		void onSecondTime();
	}
    
    public interface OnNetworkState{
        void onNetworkConnected();
        void onNetworkDisConnected();
    }
}
