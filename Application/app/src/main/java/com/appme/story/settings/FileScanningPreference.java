package com.appme.story.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Comparator;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.engine.app.folders.FileMe;
import com.appme.story.engine.app.folders.FolderMe;
import com.stericson.RootTools.RootTools;

public class FileScanningPreference {
    
    private static final String 
    NAME = "SharedPref",
    PREF_START_FOLDER = "start_folder",
    PREF_START_FILE = "start_file",
    PREF_SORT_BY = "sort_by";
    
    public static final int
    SORT_BY_NAME = 0,
    SORT_BY_TYPE = 1,
    SORT_BY_SIZE = 2;

    private final static int DEFAULT_SORT_BY = SORT_BY_NAME;
    
    String startFolder;
    String startFile;
    int sortBy;
    
    public static final String SD_CARD_ROOT = FolderMe.FOLDER;
    public static final String EXTENSION = FileMe.APK;
    
    private FileScanningPreference() {}
    private static SharedPreferences getPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private static SharedPreferences.Editor getEditor(Context context) {
        return getPrefs(context).edit();
    }
    
    public FileScanningPreference setStartFolder(String startFolder)
    {
        this.startFolder = startFolder;
        return this;
    }
    
    public FileScanningPreference setSortBy(int sortBy)
    {
        if (sortBy < 0 || sortBy > 2)
            throw new InvalidParameterException(String.valueOf(sortBy)+" is not a valid id of sorting order");

        this.sortBy = sortBy;
        return this;
    }
    
    public String getStartFolder()
    {
        return startFolder;
	}
    
    public FileScanningPreference setStartFile(String startFile)
    {
        this.startFile = startFile;
        return this;
    }
    
    public String getStartFile(){
        
        return startFile;
    }
    
    public static void setWorkingFile(Context context, String value) {
        getEditor(context).putString(PREF_START_FILE, value).commit();
    }
    
    public static String getWorkingFile(Context context) {
        return getPrefs(context).getString(PREF_START_FILE, EXTENSION);
    }

    public static void setWorkingFolder(Context context, String value) {
        getEditor(context).putString(PREF_START_FOLDER, value).commit();
    }
    
    public static String getWorkingFolder(Context context) {
        return getPrefs(context).getString(PREF_START_FOLDER, SD_CARD_ROOT);
    }

    public static void setSavedPaths(Context context, StringBuilder stringBuilder) {
        getEditor(context).putString("savedPaths", stringBuilder.toString()).commit();
    }
    
    public static String[] getSavedPaths(Context context) {
        return getPrefs(context).getString("savedPaths", "").split(",");
    }
    
    public static void setIsShortCut(Context context, Boolean isLogin) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isShortCut", isLogin);
        editor.commit();
    }

    public static Boolean isShortCut(Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getBoolean("isShortCut", false);
    }
    
    public static boolean showThumbnail() {
        return getPrefs(AppController.getContext()).getBoolean("showpreview", true);
    }

    public static boolean showHiddenFiles() {
        return getPrefs(AppController.getContext()).getBoolean("displayhiddenfiles", true);
    }

    public static boolean rootAccess() {
        return getPrefs(AppController.getContext()).getBoolean("enablerootaccess", false) && RootTools.isAccessGiven();
    }

    public static boolean reverseListView() {
        return getPrefs(AppController.getContext()).getBoolean("reverseList", false);
    }

    public static String getDefaultDir() {
        return getPrefs(AppController.getContext()).getString(PREF_START_FOLDER, SD_CARD_ROOT);
    }

    public static int getListAppearance() {
        return Integer.parseInt(getPrefs(AppController.getContext()).getString("viewmode", "1"));
    }

    public static int getSortType() {
        return Integer.parseInt(getPrefs(AppController.getContext()).getString("sort", "1"));
    }

    private void saveToSharedPreferences(SharedPreferences sharedPreferences)
    {
        sharedPreferences.edit()
            .putString(PREF_START_FOLDER, startFolder)
            .putInt(PREF_SORT_BY, sortBy)
            .apply();
    }

    public void saveChangesAsync(final Context context)
    {
        new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    saveChanges(context);

                }
            }).run();
    }

    public void saveChanges(Context context)
    {
        saveToSharedPreferences(context.getSharedPreferences(NAME, Context.MODE_PRIVATE));
    }

    
    public int getSortBy()
    {
        return sortBy;
    }

    public Comparator<File> getFileSortingComparator()
    {
        switch (sortBy)
        {
            case SORT_BY_SIZE:
                return new FileMe.FileSizeComparator();

            case SORT_BY_TYPE:
                return new FileMe.FileExtensionComparator();

            default:
                return new FileMe.FileNameComparator();
        }
    }

    private void loadFromSharedPreferences(SharedPreferences sharedPreferences)
    {
        String startPath = sharedPreferences.getString(PREF_START_FOLDER, null);
        String startFiles = sharedPreferences.getString(PREF_START_FILE, null); 
        if (startPath == null)
        {
            if (Environment.getExternalStorageDirectory().list() != null)
                startFolder = FolderMe.FOLDER;
            else 
                startFolder = SD_CARD_ROOT;
        }
        else this.startFolder = startPath;
        
        this.startFile = startFiles;
        this.sortBy = sharedPreferences.getInt(PREF_SORT_BY, DEFAULT_SORT_BY);
    }
     
    public static FileScanningPreference loadPreferences(Context context)
    {
        FileScanningPreference instance = new FileScanningPreference();       
        instance.rootAccess();
       // instance.setWorkingFile(context, FileMe.MP4);
        instance.loadFromSharedPreferences(context.getSharedPreferences(NAME, Context.MODE_PRIVATE));
        return instance;
	}
}
