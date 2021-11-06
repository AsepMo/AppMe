package com.github.template.engine.app.fragments;

import android.support.annotation.Nullable;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.widget.Toast;

import com.github.template.R;
import com.github.template.engine.app.base.BaseFragment;
import com.github.template.engine.widget.MonitorLayout;

public class AppMonitorFragment extends BaseFragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static final String TAG = AppMonitorFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    public static AppMonitorFragment newInstance(int position) {
        AppMonitorFragment f = new AppMonitorFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    public AppMonitorFragment() {
    }

    private int position; // 0:，2 
    private MonitorLayout mSdcardMonitor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        return inflater.inflate(R.layout.fragment_app_monitor, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSdcardMonitor = (MonitorLayout)view.findViewById(R.id.sdcard_monitor);

    }

    @Override
    public void onResume() {
        super.onResume();
        //  mSdcardMonitor.start();
    }


    @Override
    public void onPause() {
        super.onPause();
        //mSdcardMonitor.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mSdcardMonitor.onDestroy();
    }
}

