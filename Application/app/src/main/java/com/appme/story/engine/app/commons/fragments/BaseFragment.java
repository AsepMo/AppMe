package com.appme.story.engine.app.commons.fragments;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


@SuppressLint("NewApi")
public class BaseFragment extends Fragment {

    /** Class **/
    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /** Bundle Class **/
    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /** Action **/
    protected void startActivity(String action) {
        startActivity(action, null);
    }

    /** Bundle Action **/
    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 吐司
     * 
     * @param message
     */
    protected void showShort(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showLong(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();      
    }
}

