package com.github.template.engine.app.models;

import android.graphics.drawable.Drawable;

public class AppCleanerItem {

    private long mCacheSize;
    private String mPackageName, mApplicationName;
    private Drawable mIcon;

    public AppCleanerItem(String packageName, String applicationName, Drawable icon, long cacheSize) {
        mCacheSize = cacheSize;
        mPackageName = packageName;
        mApplicationName = applicationName;
        mIcon = icon;
    }

    public Drawable getApplicationIcon() {
        return mIcon;
    }

    public String getApplicationName() {
        return mApplicationName;
    }

    public long getCacheSize() {
        return mCacheSize;
    }

    public String getPackageName() {
        return mPackageName;
    }
}
