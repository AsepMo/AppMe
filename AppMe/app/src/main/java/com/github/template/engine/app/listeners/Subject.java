package com.github.template.engine.app.listeners;

import com.github.template.receiver.NetworkBroadcastReceiver;

public interface Subject {
    void registerObserver(OnNetworkConnectionChangeListener listener);

    void unregisterObserver(OnNetworkConnectionChangeListener listener);

    void notifyNetworkObserverChange(NetworkBroadcastReceiver.NetworkState networkState);
}


