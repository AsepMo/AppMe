package com.github.template.engine.app.listeners;

public abstract class StopReceiveDisconnectedListener implements OnNetworkConnectionChangeListener {
    private boolean mIsReadyReceiveConnectedListener;

    public boolean isReadyReceiveConnectedListener() {
        return mIsReadyReceiveConnectedListener;
    }

    @Deprecated
    public boolean stopReceiveDisconnectedListener() {
        return true;
    }

    @Override
    public void onConnected(String message) {
        mIsReadyReceiveConnectedListener = true;
        onNetworkConnected();
    }

    public void onNetworkConnected() {

    }
}


