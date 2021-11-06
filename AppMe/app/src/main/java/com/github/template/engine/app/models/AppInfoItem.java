package com.github.template.engine.app.models;

import android.content.pm.ApplicationInfo;

import com.github.template.engine.app.utils.Tuple;

public class AppInfoItem {
    public ApplicationInfo applicationInfo;
    public String label;
    public Long date;
    public Long size = -1L;
    public Tuple sha;
}

