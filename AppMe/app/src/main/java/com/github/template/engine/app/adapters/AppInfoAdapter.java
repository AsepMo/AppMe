package com.github.template.engine.app.adapters;

import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.usage.UsageStatsManager;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.format.Formatter;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static android.content.pm.PackageManager.GET_SIGNATURES;

import com.github.template.R;
import com.github.template.engine.app.models.AppInfoItem;

public class AppInfoAdapter extends BaseAdapter implements SectionIndexer, Filterable {
    
    public static final String sections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final DateFormat sSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    public static final Spannable.Factory sSpannableFactory = Spannable.Factory.getInstance();
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private PackageManager mPackageManager;
    private Filter mFilter;
    private String mConstraint;
    private List<AppInfoItem> mDefaultList;
    private List<AppInfoItem> mAdapterList;

    private static int mColorBackground;
    private static int mColorBackgroundPrimary;
    private static int mColorBackgroundSecondary;
    private static int mColorSharedUserEnable;
    private static int mColorSharedUserDisable;
    private static int mColorStopped;
    private static int mColorOrange;
    private static int mColorPrimary;
    private static int mColorSecondary;
    private static int mColorRed;
    
    public AppInfoAdapter(Activity activity) {
        mActivity = activity;
        mLayoutInflater = activity.getLayoutInflater();
        mPackageManager = activity.getPackageManager();

        mColorBackground = ContextCompat.getColor(mActivity, R.color.apps_background);
        mColorBackgroundPrimary = ContextCompat.getColor(mActivity, R.color.apps_list_background_primary);
        mColorBackgroundSecondary = ContextCompat.getColor(mActivity, R.color.apps_list_background_secondary);
        mColorStopped = ContextCompat.getColor(mActivity, R.color.apps_stopped);
        mColorOrange = ContextCompat.getColor(mActivity, R.color.apps_orange);
        mColorPrimary = ContextCompat.getColor(mActivity, R.color.apps_textColorPrimary);
        mColorSecondary = ContextCompat.getColor(mActivity, R.color.apps_textColorSecondary);
        mColorRed = ContextCompat.getColor(mActivity, R.color.apps_red);
    
    }

    public void setDefaultList(List<AppInfoItem> list) {
        mDefaultList = list;
        mAdapterList = list;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null)
            mFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String constraint = charSequence.toString().toLowerCase();
                    mConstraint = constraint;
                    FilterResults filterResults = new FilterResults();
                    if (constraint.length() == 0) {
                        filterResults.count = 0;
                        filterResults.values = null;
                        return filterResults;
                    }

                    List<AppInfoItem> list = new ArrayList<>(mDefaultList.size());
                    for (AppInfoItem item : mDefaultList) {
                        if (item.label.toLowerCase().contains(constraint) ||
                            item.applicationInfo.packageName.contains(constraint))
                            list.add(item);
                    }

                    filterResults.count = list.size();
                    filterResults.values = list;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    if (filterResults.values == null)
                        mAdapterList = mDefaultList;
                    else
                        mAdapterList = (List<AppInfoItem>) filterResults.values;

                    notifyDataSetChanged();
                }
            };
        return mFilter;
    }

    @Override
    public int getCount() {
        return mAdapterList == null ? 0 : mAdapterList.size();
    }

    @Override
    public AppInfoItem getItem(int i) {
        return mAdapterList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.main_list_item, viewGroup, false);
            holder = new ViewHolder();
            holder.mLayout = (LinearLayout) view.findViewById(R.id.layout_app_info);
            holder.icon = (ImageView) view.findViewById(R.id.icon);
            holder.label = (TextView) view.findViewById(R.id.label);
            holder.packageName = (TextView) view.findViewById(R.id.packageName);
            holder.version = (TextView) view.findViewById(R.id.version);
            holder.isSystemApp = (TextView) view.findViewById(R.id.isSystem);
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.size = (TextView) view.findViewById(R.id.size);
            holder.sharedid=(TextView) view.findViewById(R.id.shareid);
            holder.issuer=(TextView) view.findViewById(R.id.issuer);
            holder.sha=(TextView) view.findViewById(R.id.sha);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
            holder.iconLoader.cancel(true);
        }

        holder.mLayout.setBackgroundColor(i % 2 == 0 ? mColorBackgroundPrimary : mColorBackgroundSecondary);

        AppInfoItem item = mAdapterList.get(i);
        ApplicationInfo info = item.applicationInfo;
        if (!info.enabled) holder.mLayout.setBackgroundColor(mColorBackground);//holder.icon.setImageAlpha(50);//view.setBackgroundColor(Color.LTGRAY);

        holder.sharedid.setText(Integer.toString(info.uid));
        try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(info.packageName, 0);
            holder.version.setText(packageInfo.versionName);
            holder.version.setTextColor(i % 2 == 0 ? mColorSecondary : mColorPrimary);
            Date date = new Date(packageInfo.firstInstallTime);
            holder.date.setText(sSimpleDateFormat.format(date));
            holder.date.setTextColor(i % 2 == 0 ? mColorSecondary : mColorPrimary);
            if (packageInfo.sharedUserId != null) holder.sharedid.setTextColor(mColorSharedUserEnable);
            else holder.sharedid.setTextColor(mColorSharedUserDisable);
            holder.issuer.setText((String)item.sha.getFirst());
            holder.sha.setText((String)item.sha.getSecond());
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            //Do nothing
        }

        holder.iconLoader = new IconAsyncTask(holder.icon, info);
        holder.iconLoader.execute();

        if (mConstraint != null && item.label.toLowerCase().contains(mConstraint))
            holder.label.setText(getHighlightedText(item.label));
        else
            holder.label.setText(item.label);
    
        if (mConstraint != null && info.packageName.contains(mConstraint))
            holder.packageName.setText(getHighlightedText(info.packageName));
        else
            holder.packageName.setText(info.packageName);
        if ((info.flags & ApplicationInfo.FLAG_STOPPED) != 0) holder.packageName.setTextColor(Color.BLUE);
        else holder.packageName.setTextColor(i % 2 == 0 ? mColorSecondary : mColorPrimary);

        if ((info.flags & ApplicationInfo.FLAG_HARDWARE_ACCELERATED) == 0) holder.version.setText("_"+holder.version.getText());
        if ((info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) holder.version.setText("debug"+holder.version.getText());
        if ((info.flags & ApplicationInfo.FLAG_TEST_ONLY) != 0) holder.version.setText("~"+holder.version.getText());


        if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) holder.isSystemApp.setText(mActivity.getString(R.string.app_system));
        else holder.isSystemApp.setText(mActivity.getString(R.string.app_user));
        if (Build.VERSION.SDK_INT >= 23) {
            UsageStatsManager mUsageStats;
            mUsageStats = mActivity.getSystemService(UsageStatsManager.class);
            if (mUsageStats.isAppInactive(info.packageName))holder.version.setTextColor(Color.GREEN);
            else holder.version.setTextColor(i % 2 == 0 ? mColorSecondary : mColorPrimary);
        }

        //holder.isSystemApp.setText(holder.isSystemApp.getText()+ getCategory(info.category, (char) 'c'));
        if ((info.flags & ApplicationInfo.FLAG_PERSISTENT) != 0) holder.isSystemApp.setTextColor(Color.MAGENTA);
        else holder.isSystemApp.setTextColor(i % 2 == 0 ? mColorSecondary : mColorPrimary);
        if ((info.flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0) holder.isSystemApp.setText(holder.isSystemApp.getText()+"#");
        if ((info.flags & ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA) == 0) holder.label.setTextColor(Color.RED);
        else holder.label.setTextColor(i % 2 == 0 ? mColorSecondary : mColorPrimary);
        if ((info.flags & ApplicationInfo.FLAG_SUSPENDED) != 0) holder.isSystemApp.setText(holder.isSystemApp.getText()+"°");
        if ((info.flags & ApplicationInfo.FLAG_MULTIARCH) != 0) holder.isSystemApp.setText(holder.isSystemApp.getText()+"X");
        if ((info.flags & ApplicationInfo.FLAG_HAS_CODE) == 0) holder.isSystemApp.setText(holder.isSystemApp.getText()+"0");
        if ((info.flags & ApplicationInfo.FLAG_VM_SAFE_MODE) != 0) holder.isSystemApp.setText(holder.isSystemApp.getText()+"?");
        //if ((info.flags & ApplicationInfo.FLAG_EXTRACT_NATIVE_LIBS) == 0) holder.isSystemApp.setText(holder.isSystemApp.getText()+"0");
        if (mPackageManager.checkPermission(Manifest.permission.READ_LOGS,info.packageName)== PackageManager.PERMISSION_GRANTED) holder.date.setTextColor(mColorOrange);
        else holder.date.setTextColor(i % 2 == 0 ? mColorSecondary : mColorPrimary);

        if (Build.VERSION.SDK_INT >=26)  {
            holder.size.setText(item.size+"sdk");
            if ((info.flags & ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC) !=0) holder.size.setTextColor(mColorOrange);
            else holder.size.setTextColor(i % 2 == 0 ? mColorSecondary : mColorPrimary);
        }
        else if (item.size != -1L)
            holder.size.setText(Formatter.formatFileSize(mActivity, item.size));

        return view;
    }

    public Spannable getHighlightedText(String s) {
        Spannable spannable = sSpannableFactory.newSpannable(s);
        int start = s.toLowerCase().indexOf(mConstraint);
        int end = start + mConstraint.length();
        spannable.setSpan(new BackgroundColorSpan(0xFFB7B7B7), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < this.getCount(); i++) {
            String item = mAdapterList.get(i).label;
            if (item.length() > 0) {
                if (item.charAt(0) == sections.charAt(section))
                    return i;
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] sectionsArr = new String[sections.length()];
        for (int i = 0; i < sections.length(); i++)
            sectionsArr[i] = "" + sections.charAt(i);

        return sectionsArr;
    }

    class IconAsyncTask extends AsyncTask<Void, Integer, Drawable> {

        ImageView imageView;
        ApplicationInfo info;

        IconAsyncTask(ImageView imageView, ApplicationInfo info) {
            this.imageView = imageView;
            this.info = info;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Drawable doInBackground(Void... voids) {
            if (!isCancelled())
                return info.loadIcon(mPackageManager);
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            imageView.setImageDrawable(drawable);
            imageView.setVisibility(View.VISIBLE);
        }
    }
    
    public static class ViewHolder {
        LinearLayout mLayout;
        ImageView icon;
        TextView label;
        TextView packageName;
        TextView version;
        TextView isSystemApp;
        TextView date;
        TextView size;
        TextView sharedid;
        TextView issuer;
        TextView sha;
        IconAsyncTask iconLoader;
    }
    
}
