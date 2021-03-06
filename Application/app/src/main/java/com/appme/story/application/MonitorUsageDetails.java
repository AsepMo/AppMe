package com.appme.story.application;

import android.Manifest;
import android.annotation.TargetApi;
import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.appme.story.R;
import com.appme.story.engine.Api;
import com.appme.story.engine.app.commons.activity.ActionBarActivity;
import com.appme.story.engine.app.commons.manager.DataManager;
import com.appme.story.engine.app.commons.db.DbIgnoreExecutor;
import com.appme.story.engine.app.models.AppItem;
import com.appme.story.engine.app.utils.MonitorUsageUtil;
import com.appme.story.engine.app.utils.BitmapUtil;
import com.appme.story.engine.app.utils.SortEnum;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.settings.theme.Theme;
import com.appme.story.settings.theme.ThemePreference;

public class MonitorUsageDetails extends ActionBarActivity {

    public static String TAG = MonitorUsageDetails.class.getSimpleName();
    
    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_DAY = "day";

    private Toolbar mToolbar;
    private MyAdapter mAdapter;
    private TextView mTime;
    private String mPackageName;
    private TextView mData;
    private ProgressBar mProgress;
    private int mDay;
    
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_monitor_app_usage_detail);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                supportFinishAfterTransition();
                Log.d(">>>>====----> Detail", "onOptionsItemSelected.android.R.id.home");           
            }
        });
        if (mToolbar == null) {
            ActionBar mActionBar = getSupportActionBar();
            mActionBar.setTitle(null);
            setSupportActionBar(mToolbar);
        }
        TextView mToolbarTitle = (TextView)findViewById(R.id.app_title);
        mToolbarTitle.setText(R.string.app_monitor_detail);
    
        mProgress = findViewById(R.id.progressBar);

        Intent intent = getIntent();
        if (intent != null) {
            mPackageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);
            mDay = intent.getIntExtra(EXTRA_DAY, 0);
            // package name
            TextView mPackage = findViewById(R.id.pkg_name);
            mPackage.setText(mPackageName);
            // icon
            ImageView imageView = findViewById(R.id.icon);
            Drawable icon = MonitorUsageUtil.getPackageIcon(this, mPackageName);
            imageView.setImageDrawable(icon);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDetail();
                }
            });
            // name
            TextView name = findViewById(R.id.name);
            name.setText(MonitorUsageUtil.parsePackageName(getPackageManager(), mPackageName));
            // time
            mTime = findViewById(R.id.time);
            // action
            final Button mOpenButton = findViewById(R.id.open);
            final Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(mPackageName);
            if (LaunchIntent == null) {
                mOpenButton.setClickable(false);
                mOpenButton.setAlpha(0.5f);
            } else {
                mOpenButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(LaunchIntent);
                    }
                });
            }
            // list
            RecyclerView mList = findViewById(R.id.list);
            mList.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new MyAdapter(this);
            mList.setAdapter(mAdapter);
            // load
            new MyAsyncTask(this).execute(mPackageName);
            // data
            mData = findViewById(R.id.data);
            new MyDataAsyncTask().execute(mPackageName);
            // color
            final int defaultButtonFilterColor = getResources().getColor(R.color.colorPrimary);
            Bitmap bitmap = BitmapUtil.drawableToBitmap(MonitorUsageUtil.getPackageIcon(MonitorUsageDetails.this, mPackageName));
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@NonNull Palette palette) {
                    Palette.Swatch swatch = palette.getVibrantSwatch(); // ?????????????????????????????????
                    int color = defaultButtonFilterColor;
                    if (swatch != null) {
                        color = swatch.getRgb();
                    }
                    try {
                        mToolbar.setBackgroundDrawable(new ColorDrawable(color));
                        Window window = getWindow();
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(color);
                    } catch (Exception e) {
                        // ignore
                    }
                    mOpenButton.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                    mProgress.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_monitor_activity_detail, menu);
        // menu.removeItem(R.id.more);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ignore:
                if (!TextUtils.isEmpty(mPackageName)) {
                    DbIgnoreExecutor.getInstance().insertItem(mPackageName);
                    setResult(1);
                    Toast.makeText(this, R.string.app_monitor_ignore_success, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.more:
                openDetail();
                return true;
            case android.R.id.home:
                supportFinishAfterTransition();
                Log.d(">>>>====----> Detail", "onOptionsItemSelected.android.R.id.home");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openDetail() {
        Intent intent = new Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + mPackageName));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(">>>>====----> Detail", "onBackPressed");
        supportFinishAfterTransition();
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private List<AppItem> mData;
        private Context mContext;
        public MyAdapter(Context context) {
            this.mContext = context;
            mData = new ArrayList<>();
        }

        void setData(List<AppItem> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_monitor_activity_detail, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            AppItem item = mData.get(position);
            String desc = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).format(new Date(item.mEventTime));
            if (item.mEventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                holder.mLayout.setPadding(dpToPx(16), 0, dpToPx(16), dpToPx(4));
            } else if (item.mEventType == -1) {
                holder.mLayout.setPadding(dpToPx(16), dpToPx(4), dpToPx(16), dpToPx(4));
                desc = MonitorUsageUtil.formatMilliSeconds(item.mUsageTime);
            } else if (item.mEventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                holder.mLayout.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), 0);
            }
            holder.mEvent.setText(String.format("%s %s", getPrefix(item.mEventType), desc));
        }

        private String getPrefix(int event) {
            switch (event) {
                case 1:
                    return "??? ";
                case 2:
                    return "??? ";
                default:
                    return "???  ";
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView mEvent;
            TextView mSign;
            LinearLayout mLayout;

            MyViewHolder(View itemView) {
                super(itemView);
                mEvent = itemView.findViewById(R.id.event);
                mSign = itemView.findViewById(R.id.sign);
                mLayout = itemView.findViewById(R.id.layout);
            }
        }
    }

    class MyDataAsyncTask extends AsyncTask<String, Void, Long[]> {

        @Override
        protected Long[] doInBackground(String... strings) {
            long totalWifi = 0;
            long totalMobile = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);
                int targetUid = MonitorUsageUtil.getAppUid(getPackageManager(), mPackageName);
                long[] range = MonitorUsageUtil.getTimeRange(SortEnum.getSortEnum(mDay));
                try {
                    if (networkStatsManager != null) {
                        NetworkStats networkStats = networkStatsManager.querySummary(ConnectivityManager.TYPE_WIFI, "", range[0], range[1]);
                        if (networkStats != null) {
                            while (networkStats.hasNextBucket()) {
                                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                                networkStats.getNextBucket(bucket);
                                if (bucket.getUid() == targetUid) {
                                    totalWifi += bucket.getTxBytes() + bucket.getRxBytes();
                                }
                            }
                        }
                        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        if (ActivityCompat.checkSelfPermission(MonitorUsageDetails.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                            NetworkStats networkStatsM = networkStatsManager.querySummary(ConnectivityManager.TYPE_MOBILE, tm.getSubscriberId(), range[0], range[1]);
                            if (networkStatsM != null) {
                                while (networkStatsM.hasNextBucket()) {
                                    NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                                    networkStatsM.getNextBucket(bucket);
                                    if (bucket.getUid() == targetUid) {
                                        totalMobile += bucket.getTxBytes() + bucket.getRxBytes();
                                    }
                                }
                            }
                        }
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            return new Long[]{totalWifi, totalMobile};
        }

        @Override
        protected void onPostExecute(Long[] aLong) {
            if (mData != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mData.setText(String.format(Locale.getDefault(), getResources().getString(R.string.app_wifi_data), MonitorUsageUtil.humanReadableByteCount(aLong[0]), MonitorUsageUtil.humanReadableByteCount(aLong[1])));
                } else {
                    mData.setVisibility(View.GONE);
                }
            }
            mProgress.setVisibility(View.GONE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class MyAsyncTask extends AsyncTask<String, Void, List<AppItem>> {

        private WeakReference<Context> mContext;

        MyAsyncTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected List<AppItem> doInBackground(String... strings) {
            return DataManager.getInstance().getTargetAppTimeline(mContext.get(), strings[0], mDay);
        }

        @Override
        protected void onPostExecute(List<AppItem> appItems) {
            if (mContext.get() != null) {
                List<AppItem> newList = new ArrayList<>();
                long duration = 0;
                for (AppItem item : appItems) {
                    if (item.mEventType == UsageEvents.Event.USER_INTERACTION || item.mEventType == UsageEvents.Event.NONE) {
                        continue;
                    }
                    duration += item.mUsageTime;
                    if (item.mEventType == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                        AppItem newItem = item.copy();
                        newItem.mEventType = -1;
                        newList.add(newItem);
                    }
                    newList.add(item);
                }
                mTime.setText(String.format(getResources().getString(R.string.app_monitor_times), MonitorUsageUtil.formatMilliSeconds(duration), appItems.get(appItems.size() - 1).mCount));
                mAdapter.setData(newList);
            }
        }
    }
   
    @Override
    public void setUpStatusBar() {
        
    }

    
    @Override
    public void setUpDefaultStatusBar() {
        
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
