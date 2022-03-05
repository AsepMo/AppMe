package com.appme.story.engine.app.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class TheTask extends AsyncTask<Void, Void, Void>
    {
        private String mTips;
        private Context context;
        private Runnable mTargetTask;
        private ProgressDialog mDialog;
    
        public TheTask(Context mContext,String tips, Runnable task)
        {
            context = mContext;
            mTips = tips;
            mTargetTask = task;
        }

        @Override
        protected void onPreExecute()
        {
            mDialog = new ProgressDialog(context);
            mDialog.setMessage(mTips);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            mTargetTask.run();
            try{
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            mDialog.dismiss();
        }
    }


