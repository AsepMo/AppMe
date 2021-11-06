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
import com.github.template.service.ScreenRecord;
import com.github.template.engine.app.utils.NetworkStateUtil;
import com.github.template.engine.widget.MjpegView;
import com.github.template.AppController;

public class ScreenClientFragment extends Fragment {

    public static String TAG = ScreenClientFragment.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String EXTRA_URL = "EXTRA_URL";
    
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    public static ScreenClientFragment newInstance(String url) {
        ScreenClientFragment f = new ScreenClientFragment();
        Bundle b = new Bundle();
        b.putString(EXTRA_URL, url);
        f.setArguments(b);

        return f;
    }
    
    private Context mContext;
    private MjpegView mJpegView;
    private String mVideoUrl;
    private String mVideo = "/screen_stream.mjpeg";
    String VIDEO_URL = "http://201.166.63.44/axis-cgi/mjpg/video.cgi";
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVideoUrl = getArguments().getString(EXTRA_URL);
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_client, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mJpegView = (MjpegView)view.findViewById(R.id.surface);
        startVideo();
    }
    

    @Override
    public void onResume() {
        super.onResume();
        startVideo();
    }

    @Override
    public void onPause() {
        super.onPause();
        mJpegView.stopPlayback();
    }


    private void startVideo() {
        mJpegView.stopPlayback();
        mJpegView.startPlayback(mVideoUrl + mVideo);
        mJpegView.setOnMjpegCompletedListener(new MjpegView.OnMjpegCompletedListener() {
                @Override 
                public void onCompeleted() {
                    Toast.makeText(mContext, "OnCompleted.", Toast.LENGTH_LONG).show();
                    mJpegView.startPlayback(mVideoUrl + mVideo);
                }

                @Override 
                public void onFailure(String error) {

                    Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
                }
            });
    }
}


