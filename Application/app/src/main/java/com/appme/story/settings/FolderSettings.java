package com.appme.story.settings;

import android.app.Activity;
import android.app.Application;
import android.preference.PreferenceManager;

import com.appme.story.AppController;

public class FolderSettings extends AbstractFolderSettings {

    private final AppController application;
    public FolderSettings(AppController application) {
        this.application = application;
    }
    
    public static FolderSettings getSettings(Activity activity) {
        return getSettings(activity.getApplication());
    }

    public static FolderSettings getSettings(Application application) {
        return ((AppController) application).folderPreferences;
	}

    public void load() {
        load(PreferenceManager.getDefaultSharedPreferences(application));
    }

    public void save() {
        save(PreferenceManager.getDefaultSharedPreferences(application));
    }

    public void saveDeferred() {
        saveDeferred(PreferenceManager.getDefaultSharedPreferences(application));
    }
}

