package com.appme.story.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.appme.story.R;

import com.appme.story.R;
import com.appme.story.engine.app.models.IgnoreItem;
import com.appme.story.engine.app.commons.db.DbIgnoreExecutor;
import com.appme.story.engine.app.utils.MonitorUsageUtil;

public class MonitorUsageIgnore extends AppCompatActivity {

    private IgnoreAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_app_usage_ignore);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.app_monitor_ignore);
        }

        RecyclerView mList = findViewById(R.id.list);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new IgnoreAdapter(this);
        mList.setAdapter(mAdapter);

        new MyAsyncTask(this).execute();
    }

    @SuppressLint("StaticFieldLeak")
    class MyAsyncTask extends AsyncTask<Void, Void, List<IgnoreItem>> {

        private WeakReference<Context> mContext;

        MyAsyncTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        protected List<IgnoreItem> doInBackground(Void... voids) {
            return DbIgnoreExecutor.getInstance().getAllItems();
        }

        @Override
        protected void onPostExecute(List<IgnoreItem> ignoreItems) {
            if (mContext.get() != null && ignoreItems.size() > 0) {
                for (IgnoreItem item : ignoreItems) {
                    item.mName = MonitorUsageUtil.parsePackageName(mContext.get().getPackageManager(), item.mPackageName);
                }
                mAdapter.setData(ignoreItems);
            }
        }
    }

    class IgnoreAdapter extends RecyclerView.Adapter<IgnoreAdapter.IgnoreViewHolder> {

        private List<IgnoreItem> mData;
        private Context mContext;
        public IgnoreAdapter(Context context) {
            this.mContext = context;
            mData = new ArrayList<>();
        }

        void setData(List<IgnoreItem> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public IgnoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_monitor_activity_ignore, parent, false);
            return new IgnoreViewHolder(view);
        }

        @Override
        public void onBindViewHolder(IgnoreViewHolder holder, int position) {
            IgnoreItem item = mData.get(position);
            holder.mCreated.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date(item.mCreated)));
            holder.mName.setText(item.mName);
            holder.mIcon.setImageDrawable(MonitorUsageUtil.getPackageIcon(mContext, item.mPackageName));                    
            holder.setOnClickListener(item);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class IgnoreViewHolder extends RecyclerView.ViewHolder {

            private ImageView mIcon;
            private TextView mName;
            private TextView mCreated;

            IgnoreViewHolder(View itemView) {
                super(itemView);
                mIcon = itemView.findViewById(R.id.app_image);
                mName = itemView.findViewById(R.id.app_name);
                mCreated = itemView.findViewById(R.id.app_time);
            }

            void setOnClickListener(final IgnoreItem item) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DbIgnoreExecutor.getInstance().deleteItem(item);
                        new MyAsyncTask(MonitorUsageIgnore.this).execute();
                    }
                });
            }
        }
    }


}
