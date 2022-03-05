package com.appme.story.engine.app.folders.fileTree.utils;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

public class FUtils {
    
    public static void deleteFolder(final File folder) throws IOException {
        if (!folder.exists()) {
            return;
        }
        File[] files = folder.listFiles();
        if (files != null) { // i.e. is a directory.
            for (final File file : files) {
                deleteFolder(file);
            }
        }
        if (!folder.delete()) {
            throw new IOException(String.format("Could not delete folder %s", folder));
        }
    }

    public static void emptyFolder(final File folder) throws IOException {
        deleteFolder(folder);
        if (!folder.mkdirs()) {
            throw new IOException(String.format("Could not create empty folder %s", folder));
        }
    }

    public static void copyFile(File from, File to) throws IOException {
        to = new File(to, from.getName());
        if (from.isDirectory()) {
            if (!to.exists()) {
                if (!to.mkdirs()) {
                    throw new IOException(String.format("Could not create directory %s", to));
                }
            }

            File[] children = from.listFiles();
            if (children != null) {
                for (File child : children) {
                    copyFile(child, to);
                }
            }
        } else if (from.isFile()) {
           // Files.copy(from, to);
        }
    }
    
    public static boolean hasExtension(File file, String... exts) {
        for (String ext : exts) {
            if (file.getPath().toLowerCase().endsWith(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String fileExt(String url) {
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    public static boolean canRead(File file) {
        String[] exts = new String[]{".java", ".txt", ".xml"};
        return file.canRead() && hasExtension(file, exts);
    }

    public static String ext(String path) {
        if (path.contains(".")) {
            return path.substring(path.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }


    public static boolean canEdit(File file) {
        return file.canWrite() && hasExtension(file, ".java", ".xml", ".txt");
    }
    
    public static String readFileToString(File file) {
        String result = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();

            result = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
