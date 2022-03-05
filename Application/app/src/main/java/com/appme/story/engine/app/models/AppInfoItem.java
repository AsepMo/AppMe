package com.appme.story.engine.app.models;

import android.content.pm.ApplicationInfo;

import com.appme.story.engine.app.utils.Tuple;

public class AppInfoItem {
    public ApplicationInfo applicationInfo;
    public String label;
    public Long date;
    public Long size = -1L;
    public Tuple sha;
}

