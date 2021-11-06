package com.github.template.application;

import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.github.template.R;
import com.github.template.engine.app.settings.AppCompatPreferenceActivity;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private final static String ACTION_SETTINGS_ABOUT = "com.github.template.application.SETTINGS_ABOUT";

    public static void start(Context c) {
        Intent mIntent = new Intent(c, SettingsActivity.class);
        c.startActivity(mIntent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String action = getIntent().getAction();

        if (action != null && action.equals(ACTION_SETTINGS_ABOUT)) {
            addPreferencesFromResource(R.xml.settings_about);

            String versionName;
            try {
                versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (Exception e) {
                versionName = getString(R.string.version_number_unknown);
            }
            findPreference("version").setSummary(versionName);

            findPreference("licences").setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        WebView webView = new WebView(getApplicationContext());
                        webView.loadUrl("file:///android_asset/html/licenses.html");

                        AlertDialog dialog = new AlertDialog.Builder(SettingsActivity.this).create();
                        dialog.setTitle(R.string.open_source_licences);
                        dialog.setView(webView);
                        dialog.show();

                        return true;
                    }
                });
        } else {
            addPreferencesFromResource(R.xml.settings);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

