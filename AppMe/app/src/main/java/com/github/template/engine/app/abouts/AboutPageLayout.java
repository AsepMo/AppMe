package com.github.template.engine.app.abouts;

import android.annotation.TargetApi;
import android.support.v7.app.AppCompatDelegate;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

import com.github.template.R;

public class AboutPageLayout extends RelativeLayout {

    public AboutPageLayout(Context context) {
        super(context);
        init(context, null);
    }

    public AboutPageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AboutPageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        setBackgroundColor(Color.BLACK);
        simulateDayNight(/* DAY */ 0);
        Element adsElement = new Element();
        adsElement.setTitle("Advertise with us");

        View aboutPage = new AboutPage(context)
            .isRTL(false)
            .setImage(R.drawable.dummy_image)
            .addItem(new Element().setTitle("Version 6.2"))
            .addItem(adsElement)
            .addGroup("Connect with us")
            .addEmail("elmehdi.sakout@gmail.com")
            .addWebsite("http://medyo.github.io/")
            .addFacebook("the.medy")
            .addTwitter("medyo80")
            .addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
            .addPlayStore("com.ideashower.readitlater.pro")
            .addInstagram("medyo80")
            .addGitHub("medyo")
            .addItem(getCopyRightsElement(context))
            .create();
        addView(aboutPage);
    }
    
    public Element getCopyRightsElement(final Context context) {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(context.getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    public void simulateDayNight(int currentSetting) {
        final int DAY = 0;
        final int NIGHT = 1;
        final int FOLLOW_SYSTEM = 3;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        if (currentSetting == DAY && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO);
        } else if (currentSetting == NIGHT && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES);
        } else if (currentSetting == FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
