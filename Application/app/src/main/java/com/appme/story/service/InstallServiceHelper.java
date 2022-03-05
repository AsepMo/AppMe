package com.appme.story.service;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.appme.story.engine.app.utils.ExceptionHandler;

import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("unused")
public class InstallServiceHelper {
    
    public InstallService installService;
    public Handler UIHandler;
    public String mFilePath;
    public String mFileName;
    public String mIndexFile;
    public String mFolder;
    
    public ExceptionHandler exceptionHandler;

    public void broadcastStatus(String status) {
        installService.broadcastStatus(status);
    }

    public void broadcastStatus(String statusKey, String statusData) {
        installService.broadcastStatus(statusKey, statusData);
    }

    protected class ToastRunnable implements Runnable {

        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run() {
            Toast.makeText(installService.getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
        }
    }

    public class ProgressStream extends OutputStream {
        public ProgressStream() {

        }

        public void write(@NonNull byte[] data, int i1, int i2) {
            String str = new String(data);
            str = str.replace("\n", "").replace("\r", "");
            if (!str.equals("")) {
                broadcastStatus("progress_stream", str);
            }
        }

        @Override
        public void write(int arg0) throws IOException {

        }
    }
    
}
