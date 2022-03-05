package com.appme.story.engine.app.folders;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.AsyncTask;
import android.util.Log;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.service.InstallService;
import com.appme.story.service.InstallServiceHelper;
import com.appme.story.engine.app.tasks.AssetExtractionTask;

public class AssetManager {

    private static final String TAG = AssetManager.class.getSimpleName();

    private static volatile AssetManager Instance = null;
    private Context context;
	private static final String ASSETS_WEB_FILENAME = "android.zip";
    private static final String ASSETS_VIDEO_FILENAME = "video.zip";

    private static final int MSG_ASSETS_WEB_COPY_SUCCESS = 100;
    private static final int MSG_ASSETS_VIDEO_COPY_SUCCESS = 101;

    private static final int MSG_ASSETS_WEB_COPY_FAILED = 102;
    private static final int MSG_ASSETS_VIDEO_COPY_FAILED = 103;
    
    public static final int MSG_EXTRACT_ARCHIVE = 104;
    public static final int MSG_EXTRACT_ARCHIVE_SUCCESS = 105;
    public static final int MSG_EXTRACT_ARCHIVE_FAILED = 106;
    private OnAssetManagerListener mOnAssetManagerListener;

    public static AssetManager getInstance() {
        AssetManager localInstance = Instance;
        if (localInstance == null) {
            synchronized (AssetManager.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new AssetManager(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    private AssetManager(Context context) {
        this.context = context;         
    }

    public static AssetManager with(Context context) {
        return new AssetManager(context);
    }
	public AssetManager extract(String assets) {
        // prepare env directory
        String envDir = AppController.getContext().getExternalFilesDir("web").getAbsolutePath(); 
        String message = "Extract Asset To Storage Started";
        cleanDirectory(envDir);
        if (mOnAssetManagerListener != null) {
            mOnAssetManagerListener.onStart(message);
        }
        // extract assets
        if (!extractDir(AppController.getContext(), assets, "")) {

        } 
        
        return this;
    }

    private Handler mAssetHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ASSETS_WEB_COPY_SUCCESS: {                      
                        String folderPath = context.getExternalFilesDir("web").getAbsolutePath(); 
                        File src = new File(folderPath, ASSETS_WEB_FILENAME);
                        final AssetExtractionTask task = new AssetExtractionTask(context, mAssetHandler);
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, src, FolderMe.getWebFolder());                      
                        break;
                    }
                case MSG_ASSETS_VIDEO_COPY_SUCCESS: {                      
                        String folderPath = context.getExternalFilesDir("video").getAbsolutePath(); 
                        File src = new File(folderPath, ASSETS_VIDEO_FILENAME);
                        final AssetExtractionTask task = new AssetExtractionTask(context, mAssetHandler);
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, src, FolderMe.getWebFolder());                      
                        break;
                    }     
                case MSG_ASSETS_WEB_COPY_FAILED: {
                        String message = "Extract Web To Storage Failed..";                   
                        if (mOnAssetManagerListener != null) {
                            mOnAssetManagerListener.onFail(message);
                        }
                        // Toast.makeText(AppController.getContext(), "Extract Failed", Toast.LENGTH_SHORT).show();
                        break;
                    }
                case MSG_ASSETS_VIDEO_COPY_FAILED: {
                        String message = "Extract Video To Storage Failed..";                   
                        if (mOnAssetManagerListener != null) {
                            mOnAssetManagerListener.onFail(message);
                        }
                        // Toast.makeText(AppController.getContext(), "Extract Failed", Toast.LENGTH_SHORT).show();
                        break;
                    }     
                case MSG_EXTRACT_ARCHIVE: {
                        String message = "Extract Archive";                   
                        if (mOnAssetManagerListener != null) {
                            mOnAssetManagerListener.onFail(message);
                        }
                        // Toast.makeText(AppController.getContext(), "Extract Failed", Toast.LENGTH_SHORT).show();
                        break;
                    }         
                case MSG_EXTRACT_ARCHIVE_SUCCESS: {
                        String message = "Extract To Storage Success.."; 
                        if (mOnAssetManagerListener != null) {
                            mOnAssetManagerListener.onSuccess(message);
                        }
                        // Toast.makeText(AppController.getContext(), "Extract Failed", Toast.LENGTH_SHORT).show();
                        break;
                    } 
                case MSG_EXTRACT_ARCHIVE_FAILED: {
                        String message = "Ext To Storage Failed..";                   
                        if (mOnAssetManagerListener != null) {
                            mOnAssetManagerListener.onFail(message);
                        }
                        // Toast.makeText(AppController.getContext(), "Extract Failed", Toast.LENGTH_SHORT).show();
                        break;
                    }         
            }
        }
    };


    /**
     * Recursive remove all from directory
     *
     * @param path path to directory
     */ 
    public static void cleanDirectory(String path) {
        File file = new File(path);
        cleanDirectory(file);
    }

    public static void cleanDirectory(File path) {
        if (path == null) return;
        if (path.exists()) {
            File[] list = path.listFiles();
            if (list == null) return;
            for (File f : list) {
                if (f.isDirectory()) cleanDirectory(f);
                f.delete();
            }
        }
    }

    public static boolean remove(Context c, String path) {
        File fEnvDir = new File(path);
        if (!fEnvDir.exists()) {
            return false;
        }
        cleanDirectory(fEnvDir);
        return true;
    }

    public void setOnAssetManagerListener(OnAssetManagerListener mOnAssetManagerListener) {
        this.mOnAssetManagerListener = mOnAssetManagerListener;
    }

    public interface OnAssetManagerListener {
        void onStart(String message);
        void onSuccess(String message);
        void onFail(String message);
    }
    /**
     * Extract file to env directory
     *
     * @param c         context
     * @param rootAsset root asset name
     * @param path      path to asset file
     * @return false if error
     */
    private boolean extractFile(Context c, String rootAsset, String path) {
        android.content.res.AssetManager assetManager = c.getAssets();
        InputStream in = null;
        OutputStream out = null;
        boolean success = false;
        try {
            in = assetManager.open(rootAsset + path);
            String fullPath = FolderMe.getInstance().getExternalFileDir(rootAsset) + path;
            out = new FileOutputStream(fullPath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(in);
            close(out);
        }
        if (success) {
            if (rootAsset.equals("web")) {
                mAssetHandler.obtainMessage(MSG_ASSETS_WEB_COPY_SUCCESS).sendToTarget();
            } else {
                mAssetHandler.obtainMessage(MSG_ASSETS_VIDEO_COPY_SUCCESS).sendToTarget();
            }
        } else { 
        if (rootAsset.equals("web")) {
                mAssetHandler.obtainMessage(MSG_ASSETS_WEB_COPY_SUCCESS).sendToTarget();
            } else {
                mAssetHandler.obtainMessage(MSG_ASSETS_VIDEO_COPY_SUCCESS).sendToTarget();
            }
       }
        return true;
    }

    /**
     * Extract path to env directory
     *
     * @param c         context
     * @param rootAsset root asset name
     * @param path      path to asset directory
     * @return false if error
     */
    private boolean extractDir(Context c, String rootAsset, String path) {
        android.content.res.AssetManager assetManager = c.getAssets();
        try {
            String[] assets = assetManager.list(rootAsset + path);
            if (assets.length == 0) {
                if (!extractFile(c, rootAsset, path)) return false;
            } else {
                String fullPath = FolderMe.getInstance().getExternalFileDir(rootAsset) + path;
                File dir = new File(fullPath);
                if (!dir.exists()) dir.mkdir();
                for (String asset : assets) {
                    if (!extractDir(c, rootAsset, path + "/" + asset)) return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Recursive set permissions to directory
     *
     * @param path path to directory
     */
    private static void setPermissions(File path) {
        if (path == null) return;
        if (path.exists()) {
            path.setReadable(true, false);
            path.setExecutable(true, false);
            File[] list = path.listFiles();
            if (list == null) return;
            for (File f : list) {
                if (f.isDirectory()) setPermissions(f);
                f.setReadable(true, false);
                f.setExecutable(true, false);
            }
        }
    }

	/**
     * Closeable helper
     *
     * @param c closable object
     */
    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
