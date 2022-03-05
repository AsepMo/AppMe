package com.appme.story.engine.app.folders;

import android.annotation.TargetApi;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.provider.DocumentFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.util.Enumeration;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

import com.appme.story.R;
import com.appme.story.AppController;
import com.appme.story.engine.app.utils.IntentUtils;

public class FileMe {

    private static final String TAG = FileMe.class.getSimpleName();

    public static final String APK = ".apk", MP4 = ".mp4", MP3 = ".mp3", JPG = ".jpg", JPEG = ".jpeg", PNG = ".png", DOC = ".doc", DOCX = ".docx", XLS = ".xls", XLSX = ".xlsx", PDF = ".pdf";
    public static final String INDEX_HTML = "index.html";
    public final static int 
    KILOBYTE = 1024,
    MEGABYTE = KILOBYTE * 1024,
    GIGABYTE = MEGABYTE * 1024,
    MAX_BYTE_SIZE = KILOBYTE / 2,
    MAX_KILOBYTE_SIZE = MEGABYTE / 2,
    MAX_MEGABYTE_SIZE = GIGABYTE / 2;
    private static final double FILE_APP_ICON_SCALE = 0.2;
	public static final String MIME_TYPE_ANY = "*/*";
    private static volatile FileMe Instance = null;
    private Context context;
	private static final int BUFFER = 8192;

    private FileMe(Context context) {
        this.context = context;
    }

    public static FileMe getInstance() {
        FileMe localInstance = Instance;
        if (localInstance == null) {
            synchronized (FileMe.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new FileMe(AppController.getContext());
                }
            }
        }
        return localInstance;
    }

    public static FileMe with(Context context) {
        return new FileMe(context);
    }

    public FileMe ScriptMe(String dir, String file, String message) {
        File fileToEdit = new File(dir, file);
        if (!fileToEdit.exists()) {
            try {
                fileToEdit.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileToEdit);
            outputStream.write(message.getBytes());

        } catch (IOException e) {
            Log.e(TAG, "Unable to write to storage", e);

        } finally {
            close(outputStream);
        }
        return this;
    }


    /**
     * Requires Permission: Manifest.permission.WRITE_EXTERNAL_STORAGE
     */
    public FileMe WebMe(String dir, String fileName, String text) {
        File file = new File(dir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(text.getBytes());

        } catch (IOException e) {
            Log.e(TAG, "Unable to write to storage", e);

        } finally {
            close(outputStream);
        }
        return this;
    }

	public FileMe getUnZip(Handler mHandler) {
        try {
            String filePath = FolderMe.getInstance().getExternalFileDir("web") + "/android.zip";        
            String folderPath = FolderMe.getInstance().getWebFolder().getAbsolutePath() + "/";
            FileInputStream is = new FileInputStream(new File(filePath));
            if (unzip(is, folderPath, mHandler)) {

            }
        } catch (IOException e) {
            
        }
		return this;
	}

    public static boolean isExists() {
        return doesFileExists(INDEX_HTML);
    }

	public static boolean unzip(InputStream zipFileName, String outputDirectory, Handler handler) {

        try {
            ZipInputStream in = new ZipInputStream(zipFileName);

            ZipEntry entry = in.getNextEntry();
            while (entry != null) {

                File file = new File(outputDirectory);
                if (!file.exists()) {
                    file.mkdir();
                }

                if (entry.isDirectory()) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - 1);

                    file = new File(outputDirectory + File.separator + name);
                    file.mkdir();

                } else {
                    file = new File(outputDirectory + File.separator
                                    + entry.getName());
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    int b;
                    while ((b = in.read()) != -1) {
                        out.write(b);
                    }
                    out.close();
                }
                entry = in.getNextEntry();
            }
            in.close();


            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
            return false;
        }
    }

	public static void createZip(String[] files, String zipFile) {
        try {
            FileOutputStream dest = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
														  dest));

            for (String s : files) {
                File file = new File(s);

                if (file.isDirectory()) {
                    zipSubFolder(out, file, file.getParent().length());
                } else {
                    zipFile(out, file);
                }
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unpackZip(File zipFile, File location) {
        try {
            // Extract entries while creating required sub-directories
            ZipFile zf = new ZipFile(zipFile);
            Enumeration<?> e = zf.entries();

            while (e.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                File destinationFilePath = new File(location, entry.getName());

                // create directories if required.
                destinationFilePath.getParentFile().mkdirs();

                // if the entry is directory, leave it. Otherwise extract it.
                if (!entry.isDirectory()) {
                    // Get the InputStream for current entry of the zip file
                    // using InputStream getInputStream(Entry entry) method.
                    BufferedInputStream bis = new BufferedInputStream(zf.getInputStream(entry));

                    int b;
                    byte buffer[] = new byte[BUFFER];

                    // read the current entry from the zip file, extract it and
                    // write the extracted file.
                    FileOutputStream fos = new FileOutputStream(destinationFilePath);
                    BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);

                    while ((b = bis.read(buffer, 0, 1024)) != -1) {
                        bos.write(buffer, 0, b);
                    }

                    bos.flush();
                    bos.close();
                    bis.close();
                }
            }

            zf.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void zipSubFolder(ZipOutputStream out, File folder,
                                     int basePathLength) throws IOException {
        File[] fileList = folder.listFiles();

        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, basePathLength);
            } else {
                BufferedInputStream origin;
                byte data[] = new byte[BUFFER];
                String unmodifiedFilePath = file.getPath();
                String relativePath = unmodifiedFilePath
					.substring(basePathLength);

                FileInputStream fi = new FileInputStream(unmodifiedFilePath);
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }

    private static void zipFile(ZipOutputStream out, File file)
	throws IOException {
        BufferedInputStream origin;
        byte data[] = new byte[BUFFER];
        String str = file.getPath();

        FileInputStream fi = new FileInputStream(str);
        origin = new BufferedInputStream(fi, BUFFER);
        ZipEntry entry = new ZipEntry(str.substring(str.lastIndexOf("/") + 1));
        out.putNextEntry(entry);
        int count;
        while ((count = origin.read(data, 0, BUFFER)) != -1) {
            out.write(data, 0, count);
        }
        origin.close();
    }
    /**
     * Checks the all path until it finds it and return immediately.
     *
     * @param value must be only the binary name
     * @return if the value is found in any provided path
     */
    private static boolean doesFileExists(String value) {
        boolean result = false;
        File file = new File(FolderMe.FOLDER_WEB_EDITOR + "/" + value);
        result = file.exists();
        if (result) {
            Log.d(TAG, file + " contains index.html binary");
        }
        return result;
    }
    /**
     * Requires Permission: Manifest.permission.READ_EXTERNAL_STORAGE
     */
    public static void readFromStorage(String filePath, String fileName, TextView editor) {
        File file = new File(filePath, fileName);
        BufferedReader inputStream = null;
        FileInputStream input = null;
        String test = "";
        try {
            input = new FileInputStream(file);
            inputStream = new BufferedReader(new InputStreamReader(input));
            test = inputStream.readLine();
            editor.setText(test);
        } catch (IOException e) {
            Log.e(TAG, "Unable to read from storage", e);

        } finally {
            close(input);
            close(inputStream);
        }
    }

    private static void close(@Nullable Closeable closeable) {
        if (closeable == null) {return;}
        try {
            closeable.close();
        } catch (IOException ignored) {}
    }

    public static String getApkFilePath(Context context, String downLoadUrl) {
        File externalFile = context.getExternalFilesDir(null);
        String filePath = externalFile.getAbsolutePath();
        String fileName;
        if (downLoadUrl.endsWith(".apk")) {
            int index = downLoadUrl.lastIndexOf("/");
            if (index != -1) {
                fileName = downLoadUrl.substring(index);
            } else {
                fileName = context.getPackageName() + ".apk";
            }
        } else {
            fileName = context.getPackageName() + ".apk";
        }

        File file = new File(filePath, fileName);
        return file.getAbsolutePath();
    }

    public static Intent openApkFile(Context context, File outputFile) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", outputFile);
        } else {
            uri = Uri.fromFile(outputFile);
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }


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

    public static final FileFilter DEFAULT_FILE_FILTER = new FileFilter()
    {

        @Override
        public boolean accept(File pathname) {
            return pathname.isHidden() == false;
        }
    };

    /**
     * Compares files by name, where directories come always first
     */
    public static class FileNameComparator implements Comparator<File> {
        protected final static int 
        FIRST = -1,
        SECOND = 1;
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() || rhs.isDirectory()) {
                if (lhs.isDirectory() == rhs.isDirectory())
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                else if (lhs.isDirectory()) return FIRST;
                else return SECOND;
            }
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }       
    }

    /**
     * Compares files by extension. 
     * Falls back to sort by name if extensions are the same or one of the objects is a Directory
     * @author Michal
     *
     */
    public static class FileExtensionComparator extends FileNameComparator {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() || rhs.isDirectory())
                return super.compare(lhs, rhs);

            String ext1 = getFileExtension(lhs),
                ext2 = getFileExtension(rhs);

            if (ext1.equals(ext2))
                return super.compare(lhs, rhs);
            else
                return ext1.compareToIgnoreCase(ext2);
        }
    }

    public static class FileSizeComparator extends FileNameComparator {
        private final boolean ascending = false;

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.isDirectory() || rhs.isDirectory())
                return super.compare(lhs, rhs);

            if (lhs.length() > rhs.length())
                return ascending ? SECOND : FIRST;
            else if (lhs.length() < rhs.length())
                return ascending ? FIRST : SECOND;
            else return super.compare(lhs, rhs);
        }
    }


    public static String formatFileSize(File file) {
        return formatFileSize(file.length());       
    }

    public static String formatFileSize(long size) {
        if (size < MAX_BYTE_SIZE)
            return String.format(Locale.ENGLISH, "%d bytes", size);
        else if (size < MAX_KILOBYTE_SIZE)
            return String.format(Locale.ENGLISH, "%.2f kb", (float)size / KILOBYTE);
        else if (size < MAX_MEGABYTE_SIZE)
            return String.format(Locale.ENGLISH, "%.2f mb", (float)size / MEGABYTE);
        else 
            return String.format(Locale.ENGLISH, "%.2f gb", (float)size / GIGABYTE);
    }

    public static String formatFileSize(Collection<File> files) {
        return formatFileSize(getFileSize(files));
    }

    public static long getFileSize(File... files) {
        if (files == null) return 0l;
        long size=0;
        for (File file : files) {
            if (file.isDirectory())
                size += getFileSize(file.listFiles());
            else size += file.length();
        }
        return size;
    }

    public static long getFileSize(Collection<File> files) {
        return getFileSize(files.toArray(new File[files.size()]));
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
	}
    /**
     * Gets extension of the file name excluding the . character
     */
    public static String getFileExtension(String fileName) {
        if (fileName.contains("."))
            return fileName.substring(fileName.lastIndexOf('.') + 1);
        else 
            return "";
    }

    public static String getFileMimeType(File file) {
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file));
        if (type == null) return "*/*";
        return type;
    }

    public static int countFilesIn(Collection<File> roots) {
        int result=0;
        for (File file : roots)
            result += countFilesIn(file);
        return result;
    }

    public static int countFilesIn(File root) {
        if (root.isDirectory() == false) return 1;
        File[] files = root.listFiles();
        if (files == null) return 0;

        int n = 0;

        for (File file : files) {
            if (file.isDirectory())
                n += countFilesIn(file);
            else
                n ++;
        }
        return n;
    }


    //time conversion
    public static String timeConversion(long value) {
        String songTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            songTime = String.format("%02d:%02d", mns, scs);
        }
        return songTime;
    }

    public static Bitmap createFileIcon(File file, Context context, boolean homescreen) {
        final Bitmap bitmap;
        final Canvas canvas;
        if (file.isDirectory()) {
            // load Folder bitmap
            Bitmap folderBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_home_folder);

            bitmap = Bitmap.createBitmap(folderBitmap.getWidth(), folderBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(folderBitmap, 0, 0, null);
        } else {
            Bitmap folderBitmap = BitmapFactory.decodeResource(context.getResources(), homescreen ?R.drawable.icon_home_file: R.drawable.icon_home_file);

            bitmap = Bitmap.createBitmap(folderBitmap.getWidth(), folderBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            canvas.drawBitmap(folderBitmap, 0, 0, null);

            Drawable appIcon = IntentUtils.getAppIconForFile(file, context);
            if (appIcon != null) {
                Rect bounds = canvas.getClipBounds();
                int shrinkage = (int)(bounds.width() * FILE_APP_ICON_SCALE);
                bounds.left += shrinkage;
                bounds.right -= shrinkage;
                bounds.top += shrinkage * 1.5;
                bounds.bottom -= shrinkage * 0.5;
                appIcon.setBounds(bounds);
                appIcon.draw(canvas);
            }
        }

        // add shortcut symbol
        if (homescreen)
            canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_home_shortcut), 0, 0, null);

        return  bitmap;
    }

}
