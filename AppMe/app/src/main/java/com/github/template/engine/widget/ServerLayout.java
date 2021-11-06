package com.github.template.engine.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

import com.github.template.R;
import com.github.template.application.ApplicationActivity;
import com.github.template.engine.view.Element;
import com.github.template.engine.view.MenuServer;

public class ServerLayout extends RelativeLayout {

    public static String TAG = ServerLayout.class.getSimpleName();

    private Activity mActivity;
    private Context mContext;
    private LayoutInflater mInflater;
    private View mFrameLayout;
    private MenuServer mMenuServer;

    public ServerLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ServerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ServerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        setBackgroundColor(R.color.windowBackground);

        mContext = context;
        mActivity = (Activity)context;
    }

    // View events

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setKeepScreenOn(true);

        // Instantiate and add TextureView for rendering
        mInflater = LayoutInflater.from(getContext());
        mFrameLayout = mInflater.inflate(R.layout.layout_server, this, false);
        addView(mFrameLayout); 

        //mStorageMonitor = (StorageMonitor)mFrameLayout.findViewById(R.id.storage_monitor);
        mMenuServer = (MenuServer)mFrameLayout.findViewById(R.id.menu_server);
        Element adsElement1 = new Element();
        adsElement1.setTitle("Send Message And Picture");
        Element adsElement2 = new Element();
        adsElement2.setTitle("Sharing Your Screen, Apps And File");
        Element adsElement3 = new Element();
        adsElement3.setTitle("Monitoring Your Parent Phone And Controlling");
        
        mMenuServer.isRTL(false);
        mMenuServer.addItem(new Element().setTitle("Menu Server :"));
        mMenuServer.addMessage("Message");
        mMenuServer.addCamera("Camera");
        mMenuServer.addScreen("Screen");
        mMenuServer.addFileTransfer("File Transfer");
        mMenuServer.addWebServer("Web Server");
        mMenuServer.addRemoteControl("Remote Control");
        mMenuServer.addLog("Logcat");

        mMenuServer.addItem(adsElement1);
        mMenuServer.addItem(adsElement2);
        mMenuServer.addItem(adsElement3);
        mMenuServer.addItem(getCopyRightsElement());
    }

    public Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(mContext.getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, copyrights, Toast.LENGTH_SHORT).show();
                }
            });
        return copyRightsElement;
    }

    public void onPause(){
        //mStorageMonitor.onPause();
    }

    public void onDestroy(){
        //mStorageMonitor.onDestroy();
    }
}
