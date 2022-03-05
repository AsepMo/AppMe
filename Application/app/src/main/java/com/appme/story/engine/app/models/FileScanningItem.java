
package com.appme.story.engine.app.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.apache.commons.io.FileUtils;

import com.appme.story.engine.app.folders.FolderMe;

import java.util.ArrayList;
import java.io.Serializable;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

public class FileScanningItem implements Serializable {

    public static String TAG = FileScanningItem.class.getSimpleName();
      /**
     * Contains all possible places to check binaries
     */
    private static final String[] pathList;

    public static String FOLDER = FolderMe.getFolderScanning();
    public static String APPLIST = "app_list.json";
    public static String APPLIST_STORAGE = "app_list_storage.json";
    public static String APPLIST_MEMORY = "app_list_memory.json";
   
    static {
        pathList = new String[]{FOLDER + "/"};
    }

    /**
     * The binary which grants the root privileges
     */
    public static final String KEY_APPLIST_JSON = APPLIST_MEMORY;
    
    private static Context mContext;
    private static String mFileName;

    private String mAppName;
    private String mAppIcon;
	private Drawable mAppThumbnail;
    private String mAppUpdate;
    private String mAppSize;
    private String mAppLocation;
	private String mPackageName;
    private String mVersionName;
	private int mVersionCode;
	private int mAppListNumber;
	
    public String getAppName() {
        return mAppName;
    }

    public void setAppName(String title) {
        this.mAppName = title;
    }

	public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }
	
    public String getAppIcon() {
        return mAppIcon;
    }

    public void setAppIcon(String icon) {
        this.mAppIcon = icon;
    }
	
	public Drawable getAppThumbnail() {
        return mAppThumbnail;
    }

    public void setAppThumbnail(Drawable thumbnail) {
        this.mAppThumbnail = thumbnail;
    }
	
	public String getVersionName() {
        return mVersionName;
    }

    public void setVersionName(String versionName) {
        this.mVersionName = versionName;
    }
	
	public int getVersionCode() {
        return mVersionCode;
    }

    public void setVersionCode(int versionCode) {
        this.mVersionCode = versionCode;
    }
	
    public String getAppUpdate() {
        return mAppUpdate;
    }

    public void setAppUpdate(String update) {
        this.mAppUpdate = update;
    }

    public String getAppSize() {
        return mAppSize;
    }

    public void setAppSize(String size) {
        this.mAppSize = size;
    }

    public String getAppLocation() {
        return mAppLocation;
    }

    public void setAppLocation(String location) {
        this.mAppLocation = location;
    }
    
    public int getAppListNumber() {
        return mAppListNumber;
    }

    public void setAppListNumber(int number) {
        this.mAppListNumber = number;
    }
    
    public static final String TITLE = "app_title";
    public static final String LOCATION = "app_location";
    public static final String THUMBNAILS = "app_thumbnail";
    public static final String SIZE = "app_size";
    public static final String LAST_MODIFIED = "app_last_modified";
    public static final String NUMBER = "app_list_number";
   
    public FileScanningItem(){}

    public FileScanningItem(Context context, String filename)
    {
        mContext = context;
        mFileName = filename;
    }
    
	public void initialise(FileScanningItem appMe)
    {
        try
        {
            JSONObject json = new JSONObject();
            json.put("id", appMe.getAppListNumber());      
            json.put("app_name", appMe.getAppName());
            json.put("package_name", appMe.getPackageName());
            json.put("version_name", appMe.getVersionName());
            json.put("version_code", appMe.getVersionCode());    
            json.put("app_size", appMe.getAppSize());
            json.put("app_icon", appMe.getAppThumbnail());
            json.put("app_path", appMe.getAppLocation());
            json.put("app_update", appMe.getAppUpdate());

            String filePath = FOLDER + "/app_data_initialise.json";
            File file = new File(filePath);
            FileUtils.writeStringToFile(file, json.toString());
        }
        catch (IOException | JSONException e)
        {
            e.printStackTrace();
        }
    }
    
    public FileScanningItem(JSONObject jsonObject) throws JSONException
    {
        mAppListNumber = jsonObject.getInt(NUMBER);
        mAppName = jsonObject.getString(TITLE);
        mAppIcon = jsonObject.getString(THUMBNAILS);
        mAppSize = jsonObject.getString(SIZE);
        mAppUpdate = jsonObject.getString(LAST_MODIFIED);  
        mAppLocation = jsonObject.getString(LOCATION);  
    }

    public JSONObject toJSON() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NUMBER, mAppListNumber);
        jsonObject.put(TITLE, mAppName);
        jsonObject.put(SIZE, mAppSize);      
        jsonObject.put(THUMBNAILS, mAppIcon);
        jsonObject.put(LOCATION, mAppLocation);
        jsonObject.put(LAST_MODIFIED, mAppUpdate);     
        return jsonObject;
    }


    public static JSONArray toJSONArray(ArrayList<FileScanningItem> items) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        for (FileScanningItem item : items)
        {
            JSONObject jsonObject = item.toJSON();
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }
    
    
    public void saveToFile(ArrayList<FileScanningItem> items) throws JSONException, IOException
    {

        File file = new File(FOLDER, mFileName);
        file.getParentFile().mkdirs();
        try
        {
            //FileOutputStream fos = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(toJSONArray(items).toString());
            osw.write('\n');
            osw.flush();
            fos.flush();
            fos.getFD().sync();
            fos.close();

            Log.d(TAG, toJSONArray(items).toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, "Exception writing to file", e);
        }
    }
    
    public static ArrayList<FileScanningItem> loadFromFile(String file) throws IOException, JSONException
    {
        ArrayList<FileScanningItem> items = new ArrayList<FileScanningItem>();
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        File files = new File(FOLDER, file);
        try
        {
            fileInputStream = new FileInputStream(files);
            StringBuilder builder = new StringBuilder();
            String line;
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            while ((line = bufferedReader.readLine()) != null)
            {
                builder.append(line);
            }

            JSONArray jsonArray = (JSONArray) new JSONTokener(builder.toString()).nextValue();
            for (int i = 0; i < jsonArray.length(); i++)
            {
                FileScanningItem item = new FileScanningItem(jsonArray.getJSONObject(i));
                items.add(item);
            }


        }
        catch (FileNotFoundException fnfe)
        {
            //do nothing about it
            //file won't exist first time app is run
        }
        finally
        {
            if (bufferedReader != null)
            {
                bufferedReader.close();
            }
            if (fileInputStream != null)
            {
                fileInputStream.close();
            }

        }
        return items;
    }
    
    public static boolean isExist(String file) {
        return doesFileExists(file);
    }

    /**
     * Checks the all path until it finds it and return immediately.
     *
     * @param value must be only the binary name
     * @return if the value is found in any provided path
     */
    private static boolean doesFileExists(String value) {
        boolean result = false;
        for (String path : pathList) {
            File file = new File(path + "/" + value);
            result = file.exists();
            if (result) {
                Log.d(TAG, path + " contains" + value + "binary");
                break;
            }
        }
        return result;
    }
    
    public static ArrayList<FileScanningItem> listOfFile(File dir, String[] extensions) {
        ArrayList<FileScanningItem> appList = new ArrayList<FileScanningItem>();
        File[] list = dir.listFiles();
        int count = 0;
        for (File file : list) {
            if (file.isDirectory()) {
                if (!new File(file, ".nomedia").exists() && !file.getName().startsWith(".")) {
                    Log.w("LOG", "IS DIR " + file);
                    listOfFile(file, extensions);
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

                        FileScanningItem mAppMe  = new FileScanningItem();
                        mAppMe.setAppListNumber(count);
                        mAppMe.setAppName(mTitle);
                        //mAppMe.setAppUri(Uri.parse(file.getAbsolutePath()));
                        mAppMe.setAppLocation(file.getAbsolutePath());                  
                        mAppMe.setAppIcon(file.getAbsolutePath());   
                        mAppMe.setAppSize(FileUtils.byteCountToDisplaySize(file.length()));  
                        mAppMe.setAppUpdate(date);
                        appList.add(mAppMe);

                        try {
                            mAppMe.saveToFile(appList);

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();                          
                        }
                        Log.i("LOG", "ADD " + mAppMe.getAppName() + " " + mAppMe.getAppUpdate());
                    }
                }
            }
        }
        Log.d("LOG", appList.size() + " DONE");
        return appList;
    }
}
