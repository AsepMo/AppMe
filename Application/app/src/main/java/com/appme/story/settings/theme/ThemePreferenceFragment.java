package com.appme.story.settings.theme;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.widget.AppCompatDrawableManager;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.appme.story.R;
import com.appme.story.application.ApplicationPreferences;
import com.appme.story.engine.graphics.MaterialColorPreference;
import com.appme.story.settings.theme.Theme;

public class ThemePreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    
    private ApplicationPreferences mActivity;
    private Context mContext;
	private MaterialColorPreference mColorPrimary;
    private MaterialColorPreference mColorAccent;
    

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
        // Set an empty screen so getPreferenceScreen doesn't return null -
        // so we can create fake headers from the get-go.
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getPreferenceManager().getContext()));

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_application);
        
     }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mActivity = (ApplicationPreferences)getActivity();
        mColorPrimary = (MaterialColorPreference)getPreferenceManager().findPreference("primaryColor");
        mColorPrimary.onAttact(mActivity);
        mColorAccent = (MaterialColorPreference)getPreferenceManager().findPreference("accentColor");
        mColorAccent.onAttact(mActivity);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ThemePreference.EXTRA_RECREATE, true);
        super.onSaveInstanceState(outState);
    }

  /*  @Override
    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if (state.getBoolean(Settings.EXTRA_RECREATE)) {
            mActivity.setResult(RESULT_FIRST_USER);
        }
    }*/
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PreferenceKeys preferenceKeys = new PreferenceKeys(getResources());
        if (key.equals(preferenceKeys.night_mode_pref_key)) {
            SharedPreferences themePreferences = getActivity().getSharedPreferences(Theme.THEME_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor themeEditor = themePreferences.edit();
            //We tell our MainLayout to recreate itself because mode has changed
            themeEditor.putBoolean(Theme.RECREATE_ACTIVITY, true);

            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(preferenceKeys.night_mode_pref_key);
            if (checkBoxPreference.isChecked()) {
                //Comment out this line if not using Google Analytics
                themeEditor.putString(Theme.THEME_SAVED, Theme.DARKTHEME);
                Toast.makeText(getActivity(),"Night Mode Is On",Toast.LENGTH_SHORT).show();
            } else {
                themeEditor.putString(Theme.THEME_SAVED, Theme.LIGHTTHEME);
                Toast.makeText(getActivity(),"Night Mode Is Off",Toast.LENGTH_SHORT).show();
            }
            themeEditor.apply();
            mActivity.recreate();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}

