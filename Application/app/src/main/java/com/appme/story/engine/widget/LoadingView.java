package com.appme.story.engine.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.BitmapFactory;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.AttributeSet;
import android.util.Log;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appme.story.R;

public class LoadingView extends RelativeLayout implements View.OnClickListener {
    
    public static String TAG = LoadingView.class.getSimpleName();
    public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    private Activity mActivity;
    private Context mContext;
    private View mLoadingFrame;
    //private View mGearFrame;
    private View mIconFrame;
    
    //private ImageView mGearProgressLeft;
    //private ImageView mGearProgressRight;
    private ImageView mIconLoading;
    private TextView mTextLoading;
    private TextView mCurrentStatus;
    private TextView mCurrentLine;
    
    private Integer shortAnimDuration;
    private OnLoadingViewListener mOnLoadingViewListener;
    public LoadingView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        setBackgroundColor(Color.TRANSPARENT);  
        mActivity = (Activity)context;   
        shortAnimDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setKeepScreenOn(true);
        
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mLoadingFrame = inflater.inflate(R.layout.layout_loading_view, this, false);
        mLoadingFrame.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mLoadingFrame);

        mIconFrame = mLoadingFrame.findViewById(R.id.loading_icon_layout);
        mIconLoading = (ImageView)mLoadingFrame.findViewById(R.id.icon_loading);
        mIconLoading.setOnClickListener(this);
        
        mTextLoading = (TextView)mLoadingFrame.findViewById(R.id.loading_info);
        mCurrentStatus = (TextView)mLoadingFrame.findViewById(R.id.current_status);
        mCurrentLine = (TextView)mLoadingFrame.findViewById(R.id.current_line);
         
    }

    public void setLoadingText(String title){
        if(title != null){
            mTextLoading.setText(title);
        }  
    }
    
    public void setLoadingIcon(int resourseId){
        if(resourseId != 0){
            mIconLoading.setImageResource(resourseId);
        }
    }
    
    public void setCurrentStatus(String currentStatus){
        if(currentStatus != null){
            mCurrentStatus.setText(currentStatus);
        }  
    }
    
    public void setCurrentLine(String currentLine){
        if(currentLine != null){
            mCurrentLine.setText(currentLine);
        }  
    }
    
    
    public void start(){
        LoadingUtils.with(mContext).setProgressVisibility(mIconLoading, true);
    }
    
    @Override
    public void onClick(View view) {
    }

    
    public void crossFade(final View toHide, View toShow) {

        toShow.setAlpha(0.0f);
        toShow.setVisibility(View.VISIBLE);

        toShow.animate()
            .alpha(1.0f)
            .setDuration(shortAnimDuration)
            .setListener(null);

        toHide.animate()
            .alpha(0.0f)
            .setDuration(shortAnimDuration)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    toHide.setVisibility(View.GONE);
                }
            });
    }
    
    public void setOnSplashScreenListener(OnLoadingViewListener mOnLoadingViewListener){
        this.mOnLoadingViewListener = mOnLoadingViewListener;
    }
    
    public interface OnLoadingViewListener{
        void OnStartActivity();     
    }
}
