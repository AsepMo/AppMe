package com.github.template.engine.app.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Locale;

import com.github.template.R;
import com.github.template.AppController;
import com.github.template.service.ScreenRecord;
import com.github.template.service.MonitorScreenService;
import com.github.template.receiver.Remote;
import com.github.template.receiver.RemoteReceiver;
import com.github.template.engine.view.Element;
import com.github.template.engine.app.permission.PermissionsManager;
import com.github.template.engine.app.permission.PermissionsResultAction;
import com.github.template.engine.widget.AlertWindow;
import com.github.template.engine.widget.ScreenCapture;
import com.github.template.engine.widget.VideoBox;
import com.github.template.engine.widget.MonitorLayout;
import com.github.template.application.MonitorActivity;

public class MonitorScreenFragment extends Fragment implements RemoteReceiver.OnSendBroadcastListener {

    private static final String EXTRA_TYPE_SCREEN = "EXTRA_TYPE_SCREEN";
    private static final String EXTRA_PATH = "EXTRA_PATH";

    private static final String TAG = MonitorScreenFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    public static MonitorScreenFragment newInstance(String type, String file) {
        MonitorScreenFragment f = new MonitorScreenFragment();
        Bundle b = new Bundle();
        b.putString(EXTRA_TYPE_SCREEN, type);
        b.putString(EXTRA_PATH, file);
        f.setArguments(b);
        return f;
    }

    public MonitorScreenFragment() {
    }


    private String TYPE_SCREEN; // 0:，2 
    private String FILE_PATH;
    private int mResultCode;
    private Intent mResultData;

    private static final boolean DEBUG = false;
    private static final int REQUEST_CODE_SCREEN_SERVICE = 1;
    private static final int REQUEST_CODE_SCREEN_CAPTURE = 2;    
    private static final int REQUEST_CODE_SCREEN_SHOT = 59707;
    private static final String STATE_RESULT_CODE = "result_code";
    private static final String STATE_RESULT_DATA = "result_data";

    private Context mContext;
    private VideoBox mVideo;
    private RemoteReceiver processStatusReceiver;

    private boolean mServiceRunning;
    private boolean mRecording;

    private MenuItem mStartService;
    private MenuItem mMenuScreen;
    private MenuItem mStartRecord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mResultCode = savedInstanceState.getInt(STATE_RESULT_CODE);
            mResultData = savedInstanceState.getParcelable(STATE_RESULT_DATA);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        return inflater.inflate(R.layout.fragment_monitor_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        
        TYPE_SCREEN = getArguments().getString(EXTRA_TYPE_SCREEN);
        FILE_PATH = getArguments().getString(EXTRA_PATH);

        mVideo = (VideoBox)view.findViewById(R.id.ivVideo);
        setTypeScreen(Remote.TYPE.SCREEN_SHOT);
        
        processStatusReceiver = new RemoteReceiver();
        processStatusReceiver.setOnSendBroadcastListener(this);

        registerBroadcastReceiver();

    }

    private void setTypeScreen(final Remote.TYPE type){
        switch (type) {
            case SCREEN_PLAY:
                break;
            case SCREEN_SHOT:
                File file = new File(EXTRA_PATH);
                if (file.exists()) {
                    mVideo.setVideoThumbnail(file.getAbsolutePath());
                }
                break;
            case SCREEN_RECORD:
                File video = new File(EXTRA_PATH);
                if (video.exists()) {
                    mVideo.setVideoPath(video.getAbsolutePath());
                }
                break;  
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mResultData != null) {
            outState.putInt(STATE_RESULT_CODE, mResultCode);
            outState.putParcelable(STATE_RESULT_DATA, mResultData);
        }
    }

    @Override
    public void onServiceReady(final String message) {
        File file = new File(mContext.getExternalFilesDir(null), "screenshot.png");    
        if (file.exists())
            showImage(Uri.fromFile(file));
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScreenShotDone(final String message) {
        final File file = new File(message);
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
            new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, new PermissionsResultAction() {

                @Override
                public void onGranted() {
                    Log.i(TAG, "onGranted: Write Storage");

                    if (file.exists())
                        AppController.getInstance().showLauncherView(Uri.fromFile(file));         
                }

                @Override
                public void onDenied(String permission) {
                    Log.i(TAG, "onDenied: Write Storage: " + permission);
                    String message = String.format(Locale.getDefault(), getString(R.string.message_denied), permission);
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }
            });

        if (file.exists())
            showImage(Uri.fromFile(file));  
    }

    @Override
    public void onScreenRecordDone(String file) {
        File video = new File(file);
        if (video.exists())
            Toast.makeText(mContext, file, Toast.LENGTH_SHORT).show();

        mRecording = false;
    }

    @Override
    public void onServiceShutDown(String message) {

        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        mServiceRunning = false;
        getActivity().supportInvalidateOptionsMenu();
    }

    public void showImage(Uri uri) {
        // BEGIN_INCLUDE (create_show_image_dialog)
        if (uri != null) {

            // BEGIN_INCLUDE (show_image)
            // Loading the image is going to require some sort of I/O, which must occur off the UI
            // thread.  Changing the ImageView to display the image must occur ON the UI thread.
            // The easiest way to divide up this labor is with an AsyncTask.  The doInBackground
            // method will run in a separate thread, but onPostExecute will run in the main
            // UI thread.
            AsyncTask<Uri, Void, Bitmap> imageLoadAsyncTask = new AsyncTask<Uri, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Uri... uris) {

                    return getBitmapFromUri(uris[0]);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    mVideo.setImageBitmap(bitmap);
                }
            };
            imageLoadAsyncTask.execute(uri);
            // END_INCLUDE (show_image)

        }
    }

    /** Create a Bitmap from the URI for that image and return it.
     *
     * @param uri the Uri for the image to return.
     */
    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.v(TAG, "onResume:");
        if (!Remote.isServiceRunning(mContext)) {

        } 
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) Log.v(TAG, "onPause:");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(processStatusReceiver);
    }

    public void startScreenMonitor() {
        MediaProjectionManager mgr = (MediaProjectionManager)mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_SERVICE);        
    }

    public void startScreenCapture() {
        MediaProjectionManager mgr = (MediaProjectionManager)mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_CAPTURE);        
    }

    public void startScreenShot() {
        MediaProjectionManager mgr = (MediaProjectionManager)mContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mgr.createScreenCaptureIntent(), REQUEST_CODE_SCREEN_SHOT);        
    }

    public void registerBroadcastReceiver() {
        IntentFilter statusIntentFilter = new IntentFilter(Remote.PROCESS_BROADCAST_ACTION);
        mContext.registerReceiver(processStatusReceiver, statusIntentFilter);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mStartService = menu.findItem(R.id.action_start);
        mStartService.setVisible(true);
        mStartService.setTitle(mServiceRunning ? R.string.action_stop_service : R.string.action_start_service);
        mStartService.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        mMenuScreen = menu.findItem(R.id.action_menu_screen);
        mMenuScreen.setTitle(R.string.action_menu_screen);
        mMenuScreen.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);                     

        mStartRecord = menu.findItem(R.id.action_record);
        mStartRecord.setTitle(mRecording ? R.string.action_stop_record : R.string.action_start_record);
        mStartRecord.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);                     

        if (mServiceRunning) { 
            mMenuScreen.setEnabled(true);  
        } else {
            mMenuScreen.setEnabled(false);  
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_monitor_screen, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_start) {
            if (DEBUG) Log.v(TAG, "item:action_start");    
            mServiceRunning = !mServiceRunning;
            if (mServiceRunning) {
                startScreenMonitor();
            } else {
                ScreenRecord.getInstance().stopScreenMonitorService();     
            }

            getActivity().supportInvalidateOptionsMenu();
            return true;
        } else if (item.getItemId() == R.id.action_play) {
            if (DEBUG) Log.v(TAG, "item:action_start");            
            startScreenCapture();
            return true;
        } else if (item.getItemId() == R.id.action_shot) {
            if (DEBUG) Log.v(TAG, "item:action_shot");
            if (Remote.isServiceRunning(mContext)) {          
                ScreenRecord.getInstance().startScreenShotService(); 
            } else {
                startScreenCapture();
            }
            return true;
        } else if (item.getItemId() == R.id.action_record) {
            mRecording = !mRecording;
            if (mServiceRunning) {
                if (mRecording) {
                    ScreenRecord.getInstance().startScreenRecordService();   
                } else {
                    ScreenRecord.getInstance().stopRecordingService();        
                }
            } 
            getActivity().supportInvalidateOptionsMenu();
            return true;
        } else if (item.getItemId() == R.id.action_stream) {
            Intent intent = new Intent(mContext, MonitorActivity.class);
            intent.setAction(MonitorActivity.ACTION_MONITOR_SDCARD);
            startActivity(intent);    
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == REQUEST_CODE_SCREEN_SERVICE) {
            if (resultCode != Activity.RESULT_OK) {
                Log.i(TAG, "User cancelled");
                //Toast.makeText(getActivity(), R.string.record_user_cancelled, Toast.LENGTH_SHORT).show();
                return;
            }
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            Log.i(TAG, "Starting screen capture");
            mResultCode = resultCode;
            mResultData = resultData;
            ScreenRecord.getInstance().startScreenMonitorService("Service Is Ready", resultCode, resultData);                            
        } else if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                ScreenRecord.getInstance().startScreenCaptureService(resultCode, resultData);                
            }
        } else if (requestCode == REQUEST_CODE_SCREEN_SHOT) {
            if (resultCode == Activity.RESULT_OK) {
                //Services.getInstance().startScreenShotService(resultCode, resultData);                
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "Activity-onRequestPermissionsResult() PermissionsManager.notifyPermissionsChange()");
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
