package com.appme.story.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.appme.story.engine.app.analytics.Analytics;
import com.appme.story.engine.app.utils.NetStatusUtil;
import com.appme.story.service.ServiceMe;

public class NetworkStateReceiver extends BroadcastReceiver {

    public static final String TAG = NetworkStateReceiver.class.getSimpleName();
    
    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
       
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            int networkstate = NetStatusUtil.getNetWorkState(context);
            switch (networkstate) {
                case NetStatusUtil.NETWORK_WIFI:
                    
                    
                    //Toast.makeText(context, "Wifi Connected", Toast.LENGTH_SHORT).show();
                    break;
                case NetStatusUtil.NETWORK_MOBILE:
                    Analytics.getInstance().setServerIP();
                    
                    
                    //Toast.makeText(context, "Network Connected", Toast.LENGTH_SHORT).show();
                    break;
                case NetStatusUtil.NETWORK_NONE:
                    //Toast.makeText(context, "Network DisConnected", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            
        }
    }

}
