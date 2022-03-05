package com.appme.story.engine.app.tasks;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import com.appme.story.engine.app.models.FileScanningItem;

public class FileScanningTask extends AsyncTask<File, String, ArrayList<FileScanningItem>> {

    public static final String TAG = FileScanningTask.class.getSimpleName();
    
    private Context mContext;
    private ArrayList<FileScanningItem> appList;
    private FileScanningItem mAppMe;
    private int count = 0; 
    private String filePath;
    private String[] extensions;
    public static String APK = ".apk", MP3 = ".mp3", MP4 = ".mp4";
    
    private OnFileScanningTaskListener mOnFileSacnningTaskListener;
    
    public FileScanningTask(Context context, String fileName, String filePath, String ext) {
        this.mContext = context; 
        this.filePath = filePath;
        this.extensions = new String[]{ext};
        mAppMe = new FileScanningItem(context, fileName);  
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute(); 
        if(mOnFileSacnningTaskListener != null){
            mOnFileSacnningTaskListener.onPreExecute();
        }
    }

    @Override
    protected void onProgressUpdate(String[] values) {
        super.onProgressUpdate(values);
        if(mOnFileSacnningTaskListener != null){
            mOnFileSacnningTaskListener.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected ArrayList<FileScanningItem> doInBackground(File...file) {
        appList = new ArrayList<FileScanningItem>();
        File mFolder = new File(filePath);
        if (mFolder.exists()) {
            listOfFile(mFolder);
        }
        return appList;
    }
    
    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onCancelled(ArrayList<FileScanningItem> result) {
        super.onCancelled(result);
    }

    @Override
    protected void onPostExecute(ArrayList<FileScanningItem> result) {
        super.onPostExecute(result);
        if (result.size() < 1) {
            if(mOnFileSacnningTaskListener != null){
                mOnFileSacnningTaskListener.isEmpty();
            }
        } else {
            if(mOnFileSacnningTaskListener != null){
                mOnFileSacnningTaskListener.onSuccess(result);
            }
        }
    }

    private void listOfFile(File dir) {
        File[] list = dir.listFiles();

        for (File file : list) {
            if (file.isDirectory()) {
                if (!new File(file, ".nomedia").exists() && !file.getName().startsWith(".")) {
                    Log.w("LOG", "IS DIR " + file);
                    listOfFile(file);
                }
            } else {
                String path = file.getAbsolutePath();
                //String[] extensions = new String[]{".apk"};

                for (String ext : extensions) {
                    if (path.endsWith(ext)) {
                        String[] split = path.split("/");
                        String mTitle = split[split.length - 1];
                        count++;
                        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy  hh:mm a");
                        String date = format.format(file.lastModified());
                        
                        mAppMe  = new FileScanningItem();
                        mAppMe.setAppListNumber(count);
                        mAppMe.setAppName(mTitle);
                        mAppMe.setAppLocation(file.getAbsolutePath());                  
                        mAppMe.setAppIcon(file.getAbsolutePath());   
                        mAppMe.setAppSize(FileUtils.byteCountToDisplaySize(file.length()));  
                        mAppMe.setAppUpdate(date);
                        appList.add(mAppMe);
                    
                        try {
                            mAppMe.saveToFile(appList);

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            if (mOnFileSacnningTaskListener != null) {
                                mOnFileSacnningTaskListener.onFailed(e.getMessage());
                            }
                        }
                        Log.i("LOG", "ADD " + mAppMe.getAppName() + " " + mAppMe.getAppUpdate());
                    }
                }
            }
        }
        Log.d("LOG", appList.size() + " DONE");
    }
    
    public void setOnFileScanningTaskListener(OnFileScanningTaskListener mOnFileSacnningTaskListener){
        this.mOnFileSacnningTaskListener = mOnFileSacnningTaskListener;
    }

    public interface OnFileScanningTaskListener{
        void onPreExecute();
        void onProgressUpdate(String items);
        void onSuccess(ArrayList<FileScanningItem> result);
        void onFailed(String message);
        void isEmpty();
    }
    
}
