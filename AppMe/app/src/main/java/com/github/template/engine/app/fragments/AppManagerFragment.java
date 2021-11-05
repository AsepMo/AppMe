package com.github.template.engine.app.fragments;

import android.support.annotation.Nullable;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import com.github.template.R;
import com.github.template.engine.app.base.BaseFragment;
import com.github.template.engine.app.adapters.AppManagerAdapter;
import com.github.template.engine.app.models.AppInfo;
import com.github.template.engine.app.utils.StorageUtil;
import com.github.template.engine.app.utils.Utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AppManagerFragment extends BaseFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static final String TAG = AppManagerFragment.class.getSimpleName();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Record_Fragment.
     */
    public static AppManagerFragment newInstance(int position) {
        AppManagerFragment f = new AppManagerFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    public AppManagerFragment() {
    }
    
    private Context mContext;
    public static final int REFRESH_BT = 111;
    private int position; // 0:应用软件，2 系统软件
    private AppManagerAdapter mAutoStartAdapter;
    
    private ListView listview;
    private TextView topText;
    private List<AppInfo> userAppInfos = null;
    private List<AppInfo> systemAppInfos = null;
    private View mProgressBarLayout;
    private ImageView mProgressBar;
    private TextView mProgressBarText;

    private Method mGetPackageSizeInfoMethod;

    private AsyncTask<Void, Integer, List<AppInfo>> task;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.fragment_app_manager, container, false);
        listview = (ListView) view.findViewById(R.id.listview);
        topText = (TextView)view.findViewById(R.id.topText);
        mProgressBarLayout = view.findViewById(R.id.progress_bar_layout);
        mProgressBar = (ImageView)view.findViewById(R.id.progressBar);
        Utils.setProgressVisibility(mProgressBar, true);
        mProgressBarText = (TextView)view.findViewById(R.id.progressBarText);
        
        mContext = getActivity();
        try {
            mGetPackageSizeInfoMethod = mContext.getPackageManager().getClass().getMethod(
                    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        fillData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
        task.cancel(true);
    }

    private void fillData() {

        if (position == 0) {
            topText.setText("");

        } else {
            topText.setText("卸载下列软件，会影响正常使用");

        }


        task = new AsyncTask<Void, Integer, List<AppInfo>>() {
            private int mAppCount = 0;

            @Override
            protected List<AppInfo> doInBackground(Void... params) {
                PackageManager pm = mContext.getPackageManager();
                List<PackageInfo> packInfos = pm.getInstalledPackages(0);
                publishProgress(0, packInfos.size());
                List<AppInfo> appinfos = new ArrayList<AppInfo>();
                for (PackageInfo packInfo : packInfos) {
                    publishProgress(++mAppCount, packInfos.size());
                    final AppInfo appInfo = new AppInfo();
                    Drawable appIcon = packInfo.applicationInfo.loadIcon(pm);
                    appInfo.setAppIcon(appIcon);

                    int flags = packInfo.applicationInfo.flags;

                    int uid = packInfo.applicationInfo.uid;

                    String size = packInfo.applicationInfo.sourceDir;
                    appInfo.setUid(uid);

                    if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        appInfo.setUserApp(false);//系统应用
                    } else {
                        appInfo.setUserApp(true);//用户应用
                    }
                    if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                        appInfo.setInRom(false);
                    } else {
                        appInfo.setInRom(true);
                    }
                    String appName = packInfo.applicationInfo.loadLabel(pm).toString();
                    appInfo.setAppName(appName);
                    String packname = packInfo.packageName;
                    appInfo.setPackname(packname);
                    String version = packInfo.versionName;
                    appInfo.setVersion(version);                                  
                    appInfo.setPkgSize(new File(size).length());
                    String sourceDir = packInfo.applicationInfo.sourceDir;
                    appInfo.setSourceDir(sourceDir);
                    try {
                        mGetPackageSizeInfoMethod.invoke(mContext.getPackageManager(), new Object[]{
                                packname,
                                new IPackageStatsObserver.Stub() {
                                    @Override
                                    public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                                        synchronized (appInfo) {
                                            //appInfo.setPkgSize(pStats.cacheSize + pStats.codeSize + pStats.dataSize);
                                        }
                                    }
                                }
                        });
                    } catch (Exception e) {
                    }

                    appinfos.add(appInfo);
                }
                return appinfos;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                try {
                    mProgressBarText.setText(getString(R.string.scanning_m_of_n, values[0], values[1]));
                } catch (Exception e) {

                }

            }

            @Override
            protected void onPreExecute() {
                try {
                    showProgressBar(true);
                    mProgressBarText.setText(R.string.scanning);
                } catch (Exception e) {

                }

                //    loading.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<AppInfo> result) {

                super.onPostExecute(result);


                try {

                    showProgressBar(false);

                    userAppInfos = new ArrayList<>();
                    systemAppInfos = new ArrayList<>();
                    long allSize = 0;
                    for (AppInfo a : result) {
                        if (a.isUserApp()) {
                            allSize += a.getPkgSize();
                            userAppInfos.add(a);
                        } else {
                            systemAppInfos.add(a);
                        }
                    }
                    if (position == 0) {
                        topText.setText(getString(R.string.software_top_text, userAppInfos.size(), StorageUtil.convertStorage(allSize)));
                        mAutoStartAdapter = new AppManagerAdapter(mContext, userAppInfos);
                        listview.setAdapter(mAutoStartAdapter);

                    } else {

                        mAutoStartAdapter = new AppManagerAdapter(mContext, systemAppInfos);
                        listview.setAdapter(mAutoStartAdapter);

                    }
                } catch (Exception e) {

                }
            }

        };
        task.execute();


    }


    private boolean isProgressBarVisible() {
        return mProgressBarLayout.getVisibility() == View.VISIBLE;
    }

    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBarLayout.setVisibility(View.VISIBLE);
        } else {
            mProgressBarLayout.startAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out));
            mProgressBarLayout.setVisibility(View.GONE);
        }
    }

}

