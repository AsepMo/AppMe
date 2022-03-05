package com.appme.story.engine.app.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.appme.story.engine.app.folders.AssetManager;
import com.appme.story.engine.app.utils.ZipUtils;
import com.appme.story.engine.app.utils.MediaStoreUtils;

public final class AssetExtractionTask extends AsyncTask<File, Void, List<String>> {

    private Context activity;
    private Handler mHandler;

    public AssetExtractionTask(final Context activity, Handler handler) {
        this.activity = activity;
        this.mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
        mHandler.obtainMessage(AssetManager.MSG_EXTRACT_ARCHIVE).sendToTarget();      
    }

    @Override
    protected List<String> doInBackground(File... files) {
        final List<String> failed = new ArrayList<>();
        final String ext = FilenameUtils.getExtension(files[0].getName());
        try {
            if (ext.equals("zip")) {
                ZipUtils.unpackZip(files[0], files[1]);
            }
        } catch (Exception e) {
            failed.add(Arrays.toString(files));
        }

        if (files[1].canRead()) {
            for (File file : files[1].listFiles()) {
                MediaStoreUtils.addFileToMediaStore(file.getPath(), activity);
            }
        }
        return failed;
    }

    @Override
    protected void onPostExecute(final List<String> failed) {
        super.onPostExecute(failed);
        this.finish(failed);
    }

    @Override
    protected void onCancelled(final List<String> failed) {
        super.onCancelled(failed);
        this.finish(failed);
    }

    private void finish(final List<String> failed) {

        if (failed.isEmpty()) {
            boolean remove = AssetManager.remove(activity, activity.getExternalFilesDir("web") + "/android.zip"); 
            if (remove)
                mHandler.obtainMessage(AssetManager.MSG_EXTRACT_ARCHIVE_SUCCESS).sendToTarget();

        }
        if (activity != null && !failed.isEmpty()) {

        }
    }
}

