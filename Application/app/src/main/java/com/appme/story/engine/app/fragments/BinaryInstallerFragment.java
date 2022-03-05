package com.appme.story.engine.app.fragments;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.appme.story.R;
import com.appme.story.SplashActivity;
import com.appme.story.engine.app.commons.Constant;
import com.appme.story.engine.app.folders.FileChecker;
import com.appme.story.engine.app.folders.AssetManager;
import com.appme.story.engine.widget.LoadingView;
import com.appme.story.engine.widget.stepperview.VerticalStepperItemView;
import com.appme.story.service.ServiceUtils;
import com.appme.story.service.InstallService;

public class BinaryInstallerFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_EXTRA_URL = "EXTRA_URL";
    private static final String TAG = BinaryInstallerFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    /*public static WebBrowserFragment loadWeb(String url) {
     WebBrowserFragment f = new WebBrowserFragment();
     Bundle b = new Bundle();
     b.putString(ARG_EXTRA_URL, url);
     f.setArguments(b);

     return f;
     }*/

    public BinaryInstallerFragment() {
    }

    private View rootView;
    private Activity mActivity;
    private Context mContext;
    private VerticalStepperItemView mSteppers[] = new VerticalStepperItemView[4];
	private int mActivatedColorRes = R.color.md_deep_purple_400;
	private LoadingView mLoading0;
    private LoadingView mLoading1;
    private LoadingView mLoading2;
    private LoadingView mLoading3;

    private BroadcastReceiver processStatusReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_app_installation, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mActivity = getActivity();
        mContext = getActivity();

        processStatusReceiver = new ProcessStatus(mActivity);

        mSteppers[0] = view.findViewById(R.id.stepper_0);
        mSteppers[1] = view.findViewById(R.id.stepper_1);
        mSteppers[2] = view.findViewById(R.id.stepper_2);
        mSteppers[3] = view.findViewById(R.id.stepper_3);



        VerticalStepperItemView.bindSteppers(mSteppers);
        for (VerticalStepperItemView stepper : mSteppers) {
            stepper.setActivatedColorResource(mActivatedColorRes);
		}
        mLoading0 = (LoadingView)view.findViewById(R.id.check_root);
        mLoading0.start();

        mLoading1 = (LoadingView)view.findViewById(R.id.installation);
        mLoading1.start();

        mLoading2 = (LoadingView)view.findViewById(R.id.syncronized);
        mLoading2.start();

        mLoading3 = (LoadingView)view.findViewById(R.id.start_activity);
        mLoading3.start();

        startInstallService();
        registerBroadcastReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void startInstallService() {
        ServiceUtils.killAllServices(mActivity);
        Intent mServiceIntent = new Intent(getContext(), InstallService.class);
        mServiceIntent.setAction(Constant.ACTION.START_SERVICE);      
        mActivity.startService(mServiceIntent);
    }

    public void registerBroadcastReceiver() {
        IntentFilter statusIntentFilter = new IntentFilter(Constant.BROADCAST_ACTION);
        mActivity.registerReceiver(processStatusReceiver, statusIntentFilter);
    }

    /*private boolean fromNotification() {
     return getIntent().hasExtra("from_notification") && getIntent().getBooleanExtra("from_notification", false);
     }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(processStatusReceiver);
    }

    private class ProcessStatus extends BroadcastReceiver {

        private Activity mActivity;
        private ProcessStatus(Activity activity) {
            this.mActivity = activity;
        }

        public void onReceive(Context context, Intent intent) {
            String statusKey = "";
            String statusData = "";
            if (intent.hasExtra(Constant.STATUS_KEY)) {
                statusKey = intent.getStringExtra(Constant.STATUS_KEY);
            }
            if (intent.hasExtra(Constant.STATUS_MESSAGE)) {
                statusData = intent.getStringExtra(Constant.STATUS_MESSAGE);
            }
            switch (statusKey) {
                case "check_index_file":
                    mLoading0.setLoadingText("Waiting for Install");
                    mLoading0.setLoadingIcon(R.drawable.action_loading);
                    mLoading0.setCurrentStatus(statusData);
                    mLoading0.setCurrentLine("Please wait..");

                    FileChecker mFileChecker1 = FileChecker.with(mContext);
                    mFileChecker1.initChecker();
                    mFileChecker1.setStartActivity(new FileChecker.OnStartActivityListener(){
                            @Override
                            public void onStart(FileChecker fileChecker) {
                                fileChecker.removeCallback();
                                ServiceUtils.getInstance().onCheckIndexFile();                         
                            }
                        });
                    break;
                case "file_exist":
                    mLoading0.setLoadingText("Waiting for Install");
                    mLoading0.setLoadingIcon(R.drawable.action_loading);
                    mLoading0.setCurrentStatus(statusData);
                    mLoading0.setCurrentLine("Please wait..");

                    FileChecker mFileChecker2 = FileChecker.with(mContext);
                    mFileChecker2.initChecker();
                    mFileChecker2.setStartActivity(new FileChecker.OnStartActivityListener(){
                            @Override
                            public void onStart(FileChecker fileChecker) {
                                fileChecker.removeCallback();
                                ServiceUtils.getInstance().onStartActivity();
                            }
                        });

                    break;
                case "file_not_found":
                    mLoading0.setLoadingText("Waiting for Install");
                    mLoading0.setLoadingIcon(R.drawable.action_loading);
                    mLoading0.setCurrentStatus(statusData);
                    mLoading0.setCurrentLine("Please wait..");  

                    FileChecker mFileChecker3 = FileChecker.with(mContext);
                    mFileChecker3.initChecker();
                    mFileChecker3.setStartActivity(new FileChecker.OnStartActivityListener(){
                            @Override
                            public void onStart(FileChecker fileChecker) {
                                fileChecker.removeCallback();                            
                                ServiceUtils.getInstance().onExtractAssets();
                            }
                        });
                    break;
                case "extract_assets_to_storage":
                    mLoading0.setLoadingText("Waiting for Install");
                    mLoading0.setLoadingIcon(R.drawable.action_loading);
                    mLoading0.setCurrentStatus(statusData);
                    mLoading0.setCurrentLine("Please wait..");
                    
                    
                    break;                        
                case "extract_assets_to_storage_success":
                    mLoading0.setLoadingText("Waiting for Install");
                    mLoading0.setLoadingIcon(R.drawable.action_loading);
                    mLoading0.setCurrentStatus(statusData);
                    mLoading0.setCurrentLine("Please wait..");
                    
                    FileChecker mFileChecker4 = FileChecker.with(mContext);
                    mFileChecker4.initChecker();
                    mFileChecker4.setStartActivity(new FileChecker.OnStartActivityListener(){
                            @Override
                            public void onStart(FileChecker fileChecker) {
                                fileChecker.removeCallback();                              
                                ServiceUtils.getInstance().onInstallIndexFile();
                            }
                        });
                    break;                 
                
                case "install_index_file":
                    mLoading1.setLoadingText("Waiting for Syncrone");
                    mLoading1.setLoadingIcon(R.drawable.action_loading);
                    mLoading1.setCurrentStatus(statusData);
                    mLoading1.setCurrentLine("Please wait.."); 
                    mSteppers[0].nextStep();
                    
                    FileChecker mFileChecker5 = FileChecker.with(mContext);
                    mFileChecker5.initChecker();
                    mFileChecker5.setStartActivity(new FileChecker.OnStartActivityListener(){
                            @Override
                            public void onStart(FileChecker fileChecker) {
                                fileChecker.removeCallback(); 
                            }
                        });
                    break;
                case "syncron_index_file":
                    mLoading2.setLoadingText("Waiting for Syncrone");
                    mLoading2.setLoadingIcon(R.drawable.action_loading);
                    mLoading2.setCurrentStatus(statusData);
                    mLoading2.setCurrentLine("Please wait.."); 
                    mSteppers[1].nextStep();
                    FileChecker mFileChecker6 = FileChecker.with(mContext);
                    mFileChecker6.initChecker();
                    mFileChecker6.setStartActivity(new FileChecker.OnStartActivityListener(){
                            @Override
                            public void onStart(FileChecker fileChecker) {
                                ServiceUtils.getInstance().onSyncronData();
                            }
                        });
                    break;      
                case "start_activity":
                    mLoading3.setLoadingText("Waiting for Activity");
                    mLoading3.setLoadingIcon(R.drawable.action_loading);
                    mLoading3.setCurrentStatus(statusData);
                    mLoading3.setCurrentLine("Please wait.."); 
                    mSteppers[2].nextStep();
                    FileChecker mFileChecker = FileChecker.with(mContext);
                    mFileChecker.initChecker();
                    mFileChecker.setStartActivity(new FileChecker.OnStartActivityListener(){
                            @Override
                            public void onStart(FileChecker fileChecker) {
                                mLoading3.setLoadingText("Waiting for Activity");
                                mLoading3.setLoadingIcon(R.drawable.action_loading);
                                mLoading3.setCurrentStatus("Go To Home");
                                mLoading3.setCurrentLine("Please wait.."); 
                                ServiceUtils.getInstance().onStartActivity();
                            }
                        });
                    break;

                case "start_activity_with_error":
                    Toast.makeText(context, "An error occurred. Generated source may be incomplete.", Toast.LENGTH_SHORT).show();
                    setError(rootView , 1 , "INSTALLATION ERROR");

                    break;

                case "exit_process_on_error":
                    mActivity.finish();             
                    break;

                case "exit":
                    mActivity.finish();              
                    break;

            }
        }
    }

    public void setError(View view , int step, String msg) {
        if (mSteppers[step].getErrorText() != null) {
            mSteppers[step].setErrorText(null);
            Snackbar.make(view, "Finish!", Snackbar.LENGTH_LONG).show();
        } else {
            mSteppers[step].setErrorText(msg);
        }
	}
}
