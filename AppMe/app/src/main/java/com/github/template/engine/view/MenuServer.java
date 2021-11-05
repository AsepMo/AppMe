package com.github.template.engine.view;

import android.annotation.TargetApi;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.TextViewCompat;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import com.github.template.R;
import com.github.template.application.MonitorActivity;
import com.github.template.engine.app.utils.AppUtil;
import com.github.template.receiver.SendBroadcast;

public class MenuServer extends RelativeLayout {

    public static String TAG = MenuServer.class.getSimpleName();

    private Activity mActivity;
    private Context mContext;
    private LayoutInflater mInflater;
    private View mFrameLayout;

    private LinearLayout mLinearLayout;

    private boolean mIsRTL = false;
    private Typeface mCustomFont;

    public MenuServer(Context context) {
        super(context);
        init(context, null);
    }

    public MenuServer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MenuServer(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mFrameLayout = mInflater.inflate(R.layout.layout_menu_server, this, false);
        addView(mFrameLayout); 

        mLinearLayout = (LinearLayout)mFrameLayout.findViewById(R.id.menu_server);
    }

    public void setCustomFont(String path) {
        //TODO: check if file exists
        mCustomFont = Typeface.createFromAsset(mContext.getAssets(), path);
    }

    public void addMessage(String title) {
        Element emailElement = new Element();
        emailElement.setTitle(title);
        emailElement.setIconDrawable(R.drawable.ic_call_received);
        emailElement.setIconTint(R.color.textColorPrimary);

        Intent intent = new Intent(mContext, MonitorActivity.class);
        intent.setAction(MonitorActivity.ACTION_MESSAGE_SERVER);
        emailElement.setIntent(intent);

        addItem(emailElement);
    }

    public void addScreen(String title) {
        Element emailElement = new Element();
        emailElement.setTitle(title);
        emailElement.setIconDrawable(R.drawable.ic_cellphone_android);
        emailElement.setIconTint(R.color.textColorPrimary);

        Intent intent = new Intent(mContext, MonitorActivity.class);
        intent.setAction(MonitorActivity.ACTION_SCREEN_SERVER);
        emailElement.setIntent(intent);

        addItem(emailElement);
    }

    public void addCamera(String title) {
        Element emailElement = new Element();
        emailElement.setTitle(title);
        emailElement.setIconDrawable(R.drawable.ic_camera_wireless);
        emailElement.setIconTint(R.color.textColorPrimary);

        Intent intent = new Intent(mContext, MonitorActivity.class);
        intent.setAction(MonitorActivity.ACTION_CAMERA_SERVER);
        emailElement.setIntent(intent);

        addItem(emailElement);
    }

    public void addFileTransfer(String title) {
        Element emailElement = new Element();
        emailElement.setTitle(title);
        emailElement.setIconDrawable(R.drawable.ic_file_export);
        emailElement.setIconTint(R.color.textColorPrimary);

        Intent intent = new Intent(mContext, MonitorActivity.class);
        intent.setAction(MonitorActivity.ACTION_MONITOR_SDCARD);
        emailElement.setIntent(intent);

        addItem(emailElement);
    }

    public void addWebServer(String title) {
        Element emailElement = new Element();
        emailElement.setTitle(title);
        emailElement.setIconDrawable(R.drawable.ic_web);
        emailElement.setIconTint(R.color.textColorPrimary);

        Intent intent = new Intent(mContext, MonitorActivity.class);
        intent.setAction(MonitorActivity.ACTION_WEB_SERVER);
        emailElement.setIntent(intent);

        addItem(emailElement);
    }

    public void addRemoteControl(String title) {
        Element emailElement = new Element();
        emailElement.setTitle(title);
        emailElement.setIconDrawable(R.drawable.ic_remote);
        emailElement.setIconTint(R.color.textColorPrimary);

        Intent intent = new Intent(mContext, MonitorActivity.class);
        intent.setAction(MonitorActivity.ACTION_RC_SERVER);
        emailElement.setIntent(intent);

        addItem(emailElement);
    }


    public void addLog(String title) {
        Element emailElement = new Element();
        emailElement.setTitle(title);
        emailElement.setIconDrawable(R.drawable.ic_math_log);
        emailElement.setIconTint(R.color.textColorPrimary);

        Intent intent = new Intent(mContext, MonitorActivity.class);
        intent.setAction(MonitorActivity.ACTION_LOGCAT_SERVER);
        emailElement.setIntent(intent);

        addItem(emailElement);
    }

    public void addItem(Element element) {
        mLinearLayout.addView(createItem(element));
        mLinearLayout.addView(getSeparator(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.about_separator_height)));
    }

    public void addGroup(String name) {

        TextView textView = new TextView(mContext);
        textView.setText(name);
        TextViewCompat.setTextAppearance(textView, R.style.about_groupTextAppearance);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (mCustomFont != null) {
            textView.setTypeface(mCustomFont);
        }

        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.about_group_text_padding);
        textView.setPadding(padding, padding, padding, padding);


        if (mIsRTL) {
            textView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        } else {
            textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        }
        textView.setLayoutParams(textParams);

        mLinearLayout.addView(textView);
    }

    /**
     * Turn on the RTL mode.
     *
     * @param value
     * @return this AboutPage instance for builder pattern support
     */
    public void isRTL(boolean value) {
        this.mIsRTL = value;
    }

    private View createItem(final Element element) {
        LinearLayout wrapper = new LinearLayout(mContext);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setClickable(true);

        if (element.getOnClickListener() != null) {
            wrapper.setOnClickListener(element.getOnClickListener());
        } else if (element.getIntent() != null) {
            wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            mContext.startActivity(element.getIntent());
                        } catch (Exception e) {
                            Log.i(TAG, e.getMessage());
                        }
                    }
                });

        }

        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
        wrapper.setBackgroundResource(outValue.resourceId);

        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.about_text_padding);
        wrapper.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapper.setLayoutParams(wrapperParams);


        TextView textView = new TextView(mContext);
        TextViewCompat.setTextAppearance(textView, R.style.about_elementTextAppearance);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textParams);
        if (mCustomFont != null) {
            textView.setTypeface(mCustomFont);
        }

        ImageView iconView = null;

        if (element.getIconDrawable() != null) {
            iconView = new ImageView(mContext);
            int size = mContext.getResources().getDimensionPixelSize(R.dimen.about_icon_size);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
            iconView.setLayoutParams(iconParams);
            int iconPadding = mContext.getResources().getDimensionPixelSize(R.dimen.about_icon_padding);
            iconView.setPadding(iconPadding, 0, iconPadding, 0);

            if (Build.VERSION.SDK_INT < 21) {
                Drawable drawable = VectorDrawableCompat.create(iconView.getResources(), element.getIconDrawable(), iconView.getContext().getTheme());
                iconView.setImageDrawable(drawable);
            } else {
                iconView.setImageResource(element.getIconDrawable());
            }

            Drawable wrappedDrawable = DrawableCompat.wrap(iconView.getDrawable());
            wrappedDrawable = wrappedDrawable.mutate();
            if (element.getAutoApplyIconTint()) {
                int currentNightMode = mContext.getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
                    if (element.getIconTint() != null) {
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, element.getIconTint()));
                    } else {
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, R.color.about_item_icon_color));
                    }
                } else if (element.getIconNightTint() != null) {
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, element.getIconNightTint()));
                } else {
                    DrawableCompat.setTint(wrappedDrawable, AppUtil.getThemeAccentColor(mContext));
                }
            }

        } else {
            int iconPadding = mContext.getResources().getDimensionPixelSize(R.dimen.about_icon_padding);
            textView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        }


        textView.setText(element.getTitle());


        if (mIsRTL) {

            final int gravity = element.getGravity() != null ? element.getGravity() : Gravity.END;

            wrapper.setGravity(gravity | Gravity.CENTER_VERTICAL);
            //noinspection ResourceType
            textParams.gravity = gravity | Gravity.CENTER_VERTICAL;
            wrapper.addView(textView);
            if (element.getIconDrawable() != null) {
                wrapper.addView(iconView);
            }

        } else {
            final int gravity = element.getGravity() != null ? element.getGravity() : Gravity.START;
            wrapper.setGravity(gravity | Gravity.CENTER_VERTICAL);
            //noinspection ResourceType
            textParams.gravity = gravity | Gravity.CENTER_VERTICAL;
            if (element.getIconDrawable() != null) {
                wrapper.addView(iconView);
            }
            wrapper.addView(textView);
        }

        return wrapper;
    }

    private View getSeparator() {
        return mInflater.inflate(R.layout.about_page_separator, null);
    }

}
