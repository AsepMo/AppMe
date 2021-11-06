package com.github.template.engine.app.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.design.widget.Snackbar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import com.github.template.R;
import com.github.template.AppController;
import com.github.template.application.ScreenSharePreference;
import com.github.template.engine.app.connections.ScreenServer;
import com.github.template.receiver.Remote;
import com.github.template.receiver.RemoteReceiver;
import com.github.template.service.ForegroundService;
import com.github.template.service.ScreenRecord;

public class ScreenServerFragment extends Fragment implements RemoteReceiver.OnRemoteReceiverListener {

    public static String TAG = ScreenServerFragment.class.getSimpleName();
    private Context mContext;
    private Activity mActivity;
    private static final int SCREEN_CAPTURE_REQUEST_CODE = 1;
    private static final int SETTINGS_REQUEST_CODE = 2;

    private TextView clientsCount;
    private TextView severAddress;
    private ToggleButton toggleStream;
    private MediaProjection.Callback projectionCallback;
    
    private RemoteReceiver mRemoteReceiver;
    private Snackbar portInUseSnackbar;
    private Menu mainMenu;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_server, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        mActivity = (Activity)getActivity();
        clientsCount = (TextView) view.findViewById(R.id.clientsCount);
        severAddress = (TextView) view.findViewById(R.id.severAddress);

        toggleStream = (ToggleButton) view.findViewById(R.id.toggleStream);
        toggleStream.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (toggleStream.isChecked()) tryStartStreaming();
                    else stopStreaming();
                }
            });

        projectionCallback = new MediaProjection.Callback() {
            @Override
            public void onStop() {
                stopStreaming();
            }
        };
        mRemoteReceiver = new RemoteReceiver();
        mRemoteReceiver.setOnRemoteReceiverListener(this);
        if (!AppController.isForegroundServiceRunning()) {
            final Intent foregroundService = new Intent(mContext, ForegroundService.class);
            foregroundService.putExtra(Remote.SERVICE_MESSAGE, Remote.SERVICE_MESSAGE_PREPARE_STREAMING);
            mContext.startService(foregroundService);
        }
        
        portInUseSnackbar = Snackbar.make(view.findViewById(R.id.mainView), R.string.snackbar_port_in_use, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.settings, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOptionsItemSelected(mainMenu.findItem(R.id.menu_settings));
                }
            })
            .setActionTextColor(Color.GREEN);
        ((TextView) portInUseSnackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.RED);


        

        setHasOptionsMenu(true);
        registerBroadcastReceiver();
    }
    
    public void registerBroadcastReceiver() {
        IntentFilter statusIntentFilter = new IntentFilter(Remote.SERVICE_ACTION);
        mContext.registerReceiver(mRemoteReceiver, statusIntentFilter, Remote.SERVICE_PERMISSION, null);
    }
    
    
    @Override
    public void onUpdateStatus() {
        updateServiceStatus();
    }

    @Override
    public void onStartStreaming() {
        tryStartStreaming();
    }

    @Override
    public void onStopStreaming() {
        stopStreaming();
    }

    @Override
    public void onServerIP(String message) {
        severAddress.setText(message);
        toggleStream.setEnabled(!portInUseSnackbar.isShown());
    }

    @Override
    public void onServerPort() {
        if (!portInUseSnackbar.isShown()) {
            portInUseSnackbar.show();
            toggleStream.setEnabled(!portInUseSnackbar.isShown());
        }
    }

    @Override
    public void onClientCount(String message) {     
        clientsCount.setText(message);
    }

    @Override
    public void onDisConnected() {
        severAddress.setText(getResources().getString(R.string.no_wifi_connected));
        toggleStream.setEnabled(false);
        stopStreaming();
    }

    @Override
    public void onResult() {
        if (portInUseSnackbar.isShown()) {
            portInUseSnackbar.dismiss();
            toggleStream.setEnabled(!portInUseSnackbar.isShown());
        }
    }

    @Override
    public void onExit() {
        mContext.stopService(new Intent(mContext, ForegroundService.class));
        mActivity.finish();
        System.exit(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateServiceStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ForegroundService.getHttpServerStatus() == ScreenServer.SERVER_ERROR_PORT_IN_USE
                && !portInUseSnackbar.isShown()) {
            portInUseSnackbar.show();
            toggleStream.setEnabled(!portInUseSnackbar.isShown());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (portInUseSnackbar.isShown()) {
            portInUseSnackbar.dismiss();
            toggleStream.setEnabled(!portInUseSnackbar.isShown());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (AppController.getMediaProjection() != null)
            AppController.getMediaProjection().unregisterCallback(projectionCallback);

        mContext.unregisterReceiver(mRemoteReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SCREEN_CAPTURE_REQUEST_CODE:
                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(mContext, getResources().getString(R.string.cast_permission_deny), Toast.LENGTH_SHORT).show();
                    toggleStream.setChecked(false);
                    return;
                }
                startStreaming(resultCode, data);
                break;
            case SETTINGS_REQUEST_CODE:
                final boolean isServerPortChanged = AppController.getApplicationSettings().updateSettings();
                if (isServerPortChanged) restartHTTPServer();
                break;
            default:
                Log.v(ScreenServerFragment.class.getSimpleName(),"Unknown request code: " + requestCode);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
         super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_screen_server, menu);
        mainMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                final Intent intentSettings = new Intent(mContext, ScreenSharePreference.class);
                startActivityForResult(intentSettings, SETTINGS_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateServiceStatus() {
        final Intent getStatus = new Intent(mContext, ForegroundService.class);
        getStatus.putExtra(Remote.SERVICE_MESSAGE, Remote.SERVICE_MESSAGE_GET_STATUS);
        mContext.startService(getStatus);
    }

    private void tryStartStreaming() {
        if (!AppController.isWiFIConnected()) return;
        if (ForegroundService.getHttpServerStatus() != ScreenServer.SERVER_OK) return;
        toggleStream.setChecked(true);
        if (AppController.isStreamRunning()) return;
        startActivityForResult(AppController.getProjectionManager().createScreenCaptureIntent(), SCREEN_CAPTURE_REQUEST_CODE);
    }

    private void startStreaming(final int resultCode, final Intent data) {
        AppController.setMediaProjection(resultCode, data);
        final MediaProjection mediaProjection = AppController.getMediaProjection();
        if (mediaProjection == null) return;
        mediaProjection.registerCallback(projectionCallback, null);

        final Intent startStreaming = new Intent(mContext, ForegroundService.class);
        startStreaming.putExtra(Remote.SERVICE_MESSAGE, Remote.SERVICE_MESSAGE_START_STREAMING);
        mContext.startService(startStreaming);

        if (AppController.getApplicationSettings().isMinimizeOnStream()) {
            final Intent minimiseMyself = new Intent(Intent.ACTION_MAIN);
            minimiseMyself.addCategory(Intent.CATEGORY_HOME);
            minimiseMyself.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(minimiseMyself);
        }
    }

    private void stopStreaming() {
        toggleStream.setChecked(false);
        if (!AppController.isStreamRunning()) return;

        final MediaProjection mediaProjection = AppController.getMediaProjection();
        if (mediaProjection == null) return;
        mediaProjection.unregisterCallback(projectionCallback);

        final Intent stopStreaming = new Intent(mContext, ForegroundService.class);
        stopStreaming.putExtra(Remote.SERVICE_MESSAGE, Remote.SERVICE_MESSAGE_STOP_STREAMING);
        mContext.startService(stopStreaming);
    }

    private void restartHTTPServer() {
        stopStreaming();
        final Intent restartHTTP = new Intent(mContext, ForegroundService.class);
        restartHTTP.putExtra(Remote.SERVICE_MESSAGE, Remote.SERVICE_MESSAGE_RESTART_HTTP);
        mContext.startService(restartHTTP);
    }
}


