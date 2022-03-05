package com.appme.story.engine.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.appme.story.AppController;

public class LoadingUtils {
    
    private static final String TAG = LoadingUtils.class.getSimpleName();
    private static volatile LoadingUtils Instance = null;
    private Context context;
    private Integer shortAnimDuration;
    
    public static LoadingUtils getInstance() {
        LoadingUtils localInstance = Instance;
        if (localInstance == null) {
            synchronized (LoadingUtils.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new LoadingUtils(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private LoadingUtils(Context context) {
        this.context = context;
        shortAnimDuration = context.getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    public static LoadingUtils with(Context context) {
        return new LoadingUtils(context);
    }
    
    public void setGearLoading(final ImageView GearProgressLeft, final ImageView GearProgressRight){
        final RotateAnimation GearProgressLeftAnim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        GearProgressLeftAnim.setRepeatCount(Animation.INFINITE);
        GearProgressLeftAnim.setDuration((long) 2 * 1500);
        GearProgressLeftAnim.setInterpolator(new LinearInterpolator());

        final RotateAnimation GearProgressRightAnim = new RotateAnimation(360.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        GearProgressRightAnim.setRepeatCount(Animation.INFINITE);
        GearProgressRightAnim.setDuration((long) 1500);
        GearProgressRightAnim.setInterpolator(new LinearInterpolator());

        GearProgressLeft.post(new Runnable() {
                @Override
                public void run() {
                    GearProgressLeft.setAnimation(GearProgressLeftAnim);
                }
            });
        GearProgressLeft.post(new Runnable() {
                @Override
                public void run() {
                    GearProgressRight.setAnimation(GearProgressRightAnim);
                }
            });
        
    }
    
    public void setProgressVisibility(final ImageView imgBuffer, boolean Visible) {
        if (Visible) {
            imgBuffer.setVisibility(View.VISIBLE);
            setImageBuffer(imgBuffer);
        } else {
            imgBuffer.animate()
                .alpha(0.0f)
                .setDuration(shortAnimDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        imgBuffer.setVisibility(View.GONE);
                    }
                });
            
        }
    }

    public static void setImageBuffer(final ImageView imageView){
        final RotateAnimation GearProgressLeftAnim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        GearProgressLeftAnim.setRepeatCount(Animation.INFINITE);
        GearProgressLeftAnim.setDuration((long) 2 * 300);
        GearProgressLeftAnim.setInterpolator(new LinearInterpolator());
        imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setAnimation(GearProgressLeftAnim);
                }
            });
    }
    
}
