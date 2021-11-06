package com.github.template.engine.app.network;

public final class AutoRefreshNetworkUtil {
    private AutoRefreshNetworkUtil() {
    }

    public static void removeAllRegisterNetworkListener() {
        CheckNetworkConnectionHelper
            .getInstance()
            .unregisterNetworkChangeListener();
    }
    
    public static void onDestroy() {
        CheckNetworkConnectionHelper
            .getInstance()
            .onDestroy();
    }
}


