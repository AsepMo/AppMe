package com.github.template.engine.app.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.template.R;
import com.github.template.engine.app.utils.Utils;
import com.github.template.engine.widget.ServerLayout;

public class AppServerFragment extends Fragment {

    public static String TAG = AppServerFragment.class.getSimpleName();
    private Context mContext;
    private ServerLayout mServerLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_server, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mServerLayout = (ServerLayout)view.findViewById(R.id.app_server);
        
    }
}

