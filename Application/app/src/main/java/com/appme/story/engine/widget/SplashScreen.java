package com.appme.story.engine.widget;

import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.Color;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.CountDownTimer;
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
import android.view.animation.AnimationUtils;
import android.widget.VideoView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.application.ApplicationActivity;
import com.appme.story.engine.ShortCut;
import com.appme.story.engine.app.folders.AssetManager;
import com.appme.story.engine.app.folders.FolderMe;

public class SplashScreen extends RelativeLayout {

    public static String TAG = SplashScreen.class.getSimpleName();
    public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    private Activity mActivity;
    private Context mContext;
    private View mSplashFrame;
    private VideoView mVideoView;
    private ImageView mImageView;

    private Animation mFadeIn;
    private Animation mFadeInScale;
    private Animation mFadeOut;

    private Integer shortAnimDuration;
    private OnSplashScreenListener mOnSplashScreenListener;
    public SplashScreen(Context context) {
        super(context);
        init(context, null);
    }

    public SplashScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SplashScreen(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unused")
    public SplashScreen(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        mSplashFrame = inflater.inflate(R.layout.splash_screen_layout, this, false);
        mSplashFrame.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mSplashFrame);

        mVideoView = (VideoView) mSplashFrame.findViewById(R.id.video_view);
        mImageView = (ImageView) mSplashFrame.findViewById(R.id.image_view);
    }

    public void start() {
        new CountDownTimer(1200, 1200) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {              
                initVideoView(FolderMe.getVideoLoading(), mFirst);   
                
                initAnim();
                setListener();
            }
        }.start();          
    }
    
    
    private void initVideoView(String video, OnCompletionListener OnVideoComplete) {
        try {

            mVideoView.setVideoURI(Uri.fromFile(new File(video)));
            mVideoView.requestFocus();
            mVideoView.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mVideoView.start();              
                    }
                });
            mVideoView.setOnCompletionListener(OnVideoComplete);   

        } catch (IllegalArgumentException ignored) {

        } catch (SecurityException ignored) {

        } catch (IllegalStateException ignored) {

        } 
    }

    private MediaPlayer.OnCompletionListener mFirst = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mp) {
            mVideoView.pause();
            crossFade(mVideoView, mImageView); 
            setImageSplash(); 
        }
    };

    private MediaPlayer.OnCompletionListener mSecond = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mp) {
            mVideoView.pause();
            crossFade(mVideoView, mImageView); 
            setImageSplash();
            delayEnter();
        }
    };

    private void delayEnter() {
        new CountDownTimer(1200, 1200) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {              
                if (mOnSplashScreenListener != null) {
                    mOnSplashScreenListener.OnStartActivity(SplashScreen.this);
                }
            }
        }.start();
    }
    
    public void createShortCut() {
        ShortCut.with(mContext).createShortCut();
    }
    
    private void setImageSplash() {
        int index = new Random().nextInt(2);
        if (index == 1) {
            mImageView.setImageResource(R.drawable.cover_youtube);
        } else {
            mImageView.setImageResource(R.drawable.cover_youtube);
        }
    }

    private void initAnim() {
        mFadeIn = AnimationUtils.loadAnimation(mActivity, R.anim.welcome_fade_in);
        mFadeIn.setDuration(500);
        mFadeInScale = AnimationUtils.loadAnimation(mActivity, R.anim.welcome_fade_in_scale);
        mFadeInScale.setDuration(2000);
        mFadeOut = AnimationUtils.loadAnimation(mActivity, R.anim.welcome_fade_out);
        mFadeOut.setDuration(500);
        mImageView.startAnimation(mFadeIn);
    }

    public void setListener() {

        mFadeIn.setAnimationListener(new AnimationListener() {

                public void onAnimationStart(Animation animation) {

                }

                public void onAnimationRepeat(Animation animation) {

                }

                public void onAnimationEnd(Animation animation) {
                    mImageView.startAnimation(mFadeInScale);
                }
            });
        mFadeInScale.setAnimationListener(new AnimationListener() {

                public void onAnimationStart(Animation animation) {

                }

                public void onAnimationRepeat(Animation animation) {

                }

                public void onAnimationEnd(Animation animation) {
                    //startActivity(MainActivity.class);
                    crossFade(mImageView, mVideoView);
                    initVideoView(FolderMe.getVideoIntro(), mSecond);
                    mImageView.startAnimation(mFadeOut);

                }
            });
        mFadeOut.setAnimationListener(new AnimationListener() {

                public void onAnimationStart(Animation animation) {

                }

                public void onAnimationRepeat(Animation animation) {

                }

                public void onAnimationEnd(Animation animation) {
                    // startActivity(MainActivity.class);
                }
            });
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

    public void setOnSplashScreenListener(OnSplashScreenListener mOnSplashScreenListener) {
        this.mOnSplashScreenListener = mOnSplashScreenListener;
    }

    public interface OnSplashScreenListener {
        void OnStartActivity(SplashScreen mSplashScreen);     
    }
}

