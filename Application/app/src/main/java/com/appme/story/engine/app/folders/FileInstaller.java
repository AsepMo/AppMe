package com.appme.story.engine.app.folders;

import android.os.Handler;

import com.appme.story.service.InstallService;
import com.appme.story.service.InstallServiceHelper;

@SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
public class FileInstaller extends InstallServiceHelper {

    public FileInstaller(InstallService installService) {
        this.installService = installService;
        this.UIHandler = installService.UIHandler;
        this.mFilePath = installService.mFilePath;
        this.mFileName = installService.mFileName;
        this.exceptionHandler = installService.exceptionHandler;
        this.mIndexFile = installService.mIndexFile;
        this.mFolder = installService.mFolder;

    }

    public void install() {
        ThreadGroup group = new ThreadGroup("CHECK INDEX FILE");
        Runnable runProcess = new Runnable() {
            @Override
            public void run() {
                if (FileChecker.isExists()) {
                    broadcastStatus("file_exist", "File Exist");                
                } else {
                    broadcastStatus("file_not_found", "File Not Found");                         
                } 
            }
        };
        Thread extractionThread = new Thread(group, runProcess, "CHECK INDEX FILE", installService.STACK_SIZE);
        extractionThread.setPriority(Thread.MAX_PRIORITY);
        extractionThread.setUncaughtExceptionHandler(exceptionHandler);
        extractionThread.start();
    }

    public void startIndexInstaller() {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                broadcastStatus("file_not_found", "File Not Found"); 
            }
         },1000);
    }


}
