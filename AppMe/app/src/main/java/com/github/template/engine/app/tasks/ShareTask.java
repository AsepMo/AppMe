package com.github.template.engine.app.tasks;

import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import com.github.template.R;
import com.github.template.engine.app.adapters.ShareAdapter;
import com.github.template.engine.app.dialogs.ShareDialogFragment;

public class ShareTask extends AsyncTask<String, String, Void> {
    
    private Activity contextc;
    private int fab_skin;
    private ArrayList<Uri> arrayList;
    private ArrayList<Intent> targetShareIntents = new ArrayList<>();
    private ArrayList<String> arrayList1 = new ArrayList<>();
    private ArrayList<Drawable> arrayList2 = new ArrayList<>();

    public ShareTask(Activity context, ArrayList<Uri> arrayList, int fab_skin) {
        this.contextc = context;
        this.arrayList = arrayList;
        this.fab_skin = fab_skin;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String mime = strings[0];
        Intent shareIntent = new Intent();
        boolean bluetooth_present = false;
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType(mime);
        PackageManager packageManager = contextc.getPackageManager();
        List<ResolveInfo> resInfos = packageManager.queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                arrayList2.add(resInfo.loadIcon(packageManager));
                arrayList1.add(resInfo.loadLabel(packageManager).toString());
                if (packageName.contains("android.bluetooth")) bluetooth_present = true;
                Intent intent = new Intent();
                System.out.println(resInfo.activityInfo.packageName + "\t" + resInfo.activityInfo.name);
                intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.setType(mime);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayList);
                intent.setPackage(packageName);
                targetShareIntents.add(intent);

            }
        }
        if (!bluetooth_present && appInstalledOrNot("com.android.bluetooth", packageManager)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.android.bluetooth", "com.android.bluetooth.opp.BluetoothOppLauncherActivity"));
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.setType(mime);
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayList);
            intent.setPackage("com.android.bluetooth");
            targetShareIntents.add(intent);
            arrayList1.add(contextc.getResources().getString(R.string.bluetooth));
            arrayList2.add(contextc.getResources().getDrawable(R.drawable.ic_bluetooth_settings));
        }
        return null;
    }

    private boolean appInstalledOrNot(String uri, PackageManager pm) {
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public void onPostExecute(Void v) {
        if (!targetShareIntents.isEmpty()) {
            AppCompatActivity act = (AppCompatActivity)contextc;
            ShareDialogFragment shareDialogFragment = ShareDialogFragment.newInstance(targetShareIntents, arrayList2, arrayList1); 
            shareDialogFragment.show(act.getSupportFragmentManager(), ShareDialogFragment.TAG);
        } else {
            Toast.makeText(contextc, R.string.no_app_found, Toast.LENGTH_SHORT).show();
        }
    }
}
