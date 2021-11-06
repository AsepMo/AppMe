package com.github.template.engine.app.adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.CardView;
import android.content.Context;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.PopupMenu;

import com.github.template.R;
import com.github.template.engine.app.models.AppInfo;
import com.github.template.engine.app.utils.AppUtil;
import com.github.template.engine.app.utils.StorageUtil;
import com.github.template.engine.app.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

public class AppManagerAdapter extends BaseAdapter {

    public List<AppInfo> mlistAppInfo;
    private LayoutInflater infater = null;
    private Context mContext;
    public static List<Integer> clearIds;
    private Drawable icon;
    public AppManagerAdapter(Context context, List<AppInfo> apps) {
        infater = LayoutInflater.from(context);
        mContext = context;
        clearIds = new ArrayList<Integer>();
        this.mlistAppInfo = apps;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mlistAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mlistAppInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = infater.inflate(R.layout.items_app_manager, null);

            holder = new ViewHolder();
            holder.appIcon = (ImageView) convertView
                .findViewById(R.id.app_icon);
            holder.appName = (TextView) convertView
                .findViewById(R.id.app_name);
            holder.size = (TextView) convertView
                .findViewById(R.id.app_size);
            holder.btnMenu = (ImageButton) convertView
                .findViewById(R.id.btn_menu);
            holder.cardLayout = convertView
                .findViewById(R.id.cardview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final AppInfo rowItem = (AppInfo) getItem(position);
        if (rowItem != null) {

            holder.appIcon.setImageDrawable(rowItem.getAppIcon());
            holder.appName.setText(rowItem.getAppName());

            if (rowItem.isInRom()) {
                holder.size.setText(StorageUtil.convertStorage(rowItem.getPkgSize()));
            } else {
                holder.size.setText(StorageUtil.convertStorage(rowItem.getPkgSize()));
            }
        }
        icon = AppCompatResources.getDrawable(mContext, R.drawable.ic_menu_overflow_light);     
        icon.setColorFilter(R.color.icons, PorterDuff.Mode.SRC_ATOP);
        holder.btnMenu.setImageDrawable(icon);
        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mContext, v);
                    final AppInfo menu_item = (AppInfo) getItem(position);

                    popup.getMenuInflater().inflate(R.menu.menu_popup_archive, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int i = item.getItemId();
                                if (i == R.id.menu_open) {
                                    Intent i1 = mContext.getPackageManager().getLaunchIntentForPackage(rowItem.getPackname());
                                    if (i1 != null)
                                        mContext.startActivity(i1);
                                    else
                                        Toast.makeText(mContext, mContext. getString(R.string.not_allowed), Toast.LENGTH_LONG).show();                                
                                    return true;
                                } else if (i == R.id.menu_backup) {
                                    new AlertDialog.Builder(mContext, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                                        .setTitle(mContext.getString(R.string.menu_backup) + " Instalasi Aplikasi")
                                        .setMessage("Instalan " + menu_item.getAppName() + " Akan Di Backup")
                                        .setPositiveButton(mContext.getString(R.string.menu_backup), new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                boolean backup = AppUtil.backupApk(mContext, menu_item.getSourceDir(), "Apk");
                                                if (backup) {
                                                    Toast.makeText(mContext, menu_item.getAppName() + "  Berhasil Di Backup", Toast.LENGTH_SHORT).show();
                                                }
                                                dialog.dismiss();
                                            }
                                        }).setNegativeButton(mContext.getString(android.R.string.cancel), new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();                                    
                                    return true;
                                } else if (i == R.id.menu_uninstall) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    intent.setAction("android.intent.action.DELETE");
                                    intent.addCategory("android.intent.category.DEFAULT");
                                    intent.setData(Uri.parse("package:" + menu_item.getPackname()));
                                    mContext.startActivity(intent);
                                    return true;
                                } else if (i == R.id.menu_share) {
                                    ArrayList<File> arrayList2=new ArrayList<File>();
                                    arrayList2.add(new File(rowItem.getSourceDir()));
                                    AppCompatActivity act = (AppCompatActivity)mContext;
                                    Utils.shareFiles(act, arrayList2, R.color.colorAccent);                           
                                    return true;
                                } else if (i == R.id.menu_open_playstore) {
                                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                    try {
                                        intent1.setData(Uri.parse(String.format("market://details?id=%s", rowItem.getPackname())));
                                        mContext.startActivity(intent1);
                                    } catch (ActivityNotFoundException ifPlayStoreNotInstalled) {
                                        intent1.setData(Uri.parse(String.format("https://play.google.com/store/apps/details?id=%s", rowItem.getPackname())));
                                        mContext.startActivity(intent1);
                                    }
                                    return true;
                                } else if (i == R.id.menu_properties) {
                                    mContext.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(String.format("package:%s", rowItem.getPackname()))));
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        });

                    popup.show();                                    
                }
            });
        holder.cardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i1 = mContext.getPackageManager().getLaunchIntentForPackage(rowItem.getPackname());
                    if (i1 != null)
                        mContext.startActivity(i1);
                    else
                        Toast.makeText(mContext, mContext. getString(R.string.not_allowed), Toast.LENGTH_LONG).show();
                }
            });
        return convertView;
    }


    class ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView size;
        ImageButton btnMenu;
        View cardLayout;

        String packageName;
    }

}

