package com.github.template.engine.app.listeners;

import android.content.Context;

public interface OnNetworkConnectionChangeListener {
    void onConnected(String message);

    void onDisconnected(String message);

    /**
     * @return current context for register all components in the same context
     * and notify connectivity listener
     */
    Context getContext();
}


