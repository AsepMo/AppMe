package com.github.template.engine.app.folders;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.Color;
import android.os.Environment;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import android.widget.Toast;

import com.github.template.R;

public class FileSelector
{
	private static Activity context;
	private String[] extensions;
	private ArrayList<SelectedFile> itemsData = new ArrayList<>();
	public static final String APK = ".apk", ZIP = ".zip", MP4 = ".mp4", MP3 = ".mp3", JPG = ".jpg", JPEG = ".jpeg", PNG = ".png", DOC = ".doc", DOCX = ".docx", XLS = ".xls", XLSX = ".xlsx", PDF = ".pdf";
	
	public FileSelector(Activity context, String[] extensions) {
		this.context = context;
		this.extensions = extensions;
	}

	public interface OnSelectListener {
		void onSelect(String path);
	}
	
	public void selectFile(OnSelectListener listener) {
		listOfFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Apk"));
		dialogFileList(listener);
	}

	private void listOfFile(File dir) {
		File[] list = dir.listFiles();

		for (File file : list) {
			if (file.isDirectory()) {
				if (!new File(file, ".nomedia").exists() && !file.getName().startsWith(".")) {
					Log.w("LOG", "IS DIR " + file);
					listOfFile(file);
				}
			} else {
				String path = file.getAbsolutePath();

				for (String ext : extensions) {
					if (path.endsWith(ext)) {
						SelectedFile selectedFile = new SelectedFile();

						selectedFile.path = path;
						String[] split = path.split("/");
						selectedFile.name = split[split.length - 1];
						itemsData.add(selectedFile);
						Log.i("LOG", "ADD " + selectedFile.path + " " + selectedFile.name);
					}
				}
			}
		}
		Log.d("LOG", itemsData.size() + " DONE");
	}

	
	private static final String[] items = {
		Environment.getExternalStorageDirectory().getAbsolutePath(), "/storage/extSdCard/"};
	
	private void dialogFileList(OnSelectListener listener) {
		View contv = LayoutInflater.from(context).inflate(R.layout.layout_file_selector, null);
		
		RelativeLayout lytMain = (RelativeLayout)contv.findViewById(R.id.layout_file_selector);
		lytMain.setBackgroundResource(R.drawable.background_holo_blue);
		ImageView arrow = (ImageView)contv.findViewById(R.id.iv_back_vb);
		
		TextView textView = (TextView)contv.findViewById(R.id.tv_title_lh);
		textView.setText("APK FOLDER");
		textView.setTextColor(Color.WHITE);
		
        TextView cancel = (TextView)contv.findViewById(R.id.tv_preview_ar);
		
		ListView mFileList = (ListView)contv.findViewById(android.R.id.list);
		mFileList.setEmptyView(contv.findViewById(android.R.id.empty));
        
		TextView spin = (TextView)contv.findViewById(R.id.tv_album_ar);
		spin.setText("Package Archive");
        
	    final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(lytMain);
		dialog.setCancelable(true);
		dialog.show();

		FileAdapter mFileAdapter = new FileAdapter(context, dialog, listener, itemsData);
		mFileList.setAdapter(mFileAdapter);
		arrow.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					dialog.dismiss();
				}
			});
       cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    dialog.dismiss();
                }
			});     
	}
	
	public class SelectedFile {
		public String path = "";
		public String name = "";
	}
	
	private class FileAdapter extends ArrayAdapter<SelectedFile>{
		// Layout Inflater
		private LayoutInflater inflater;
		private ArrayList<SelectedFile> itemsData;
		private OnSelectListener listener;
		private Dialog dialog;
		// Name of the file
		public TextView nameLabel;

		// Size of the file
		public TextView detailLabel;

		// Icon of the file
		public ImageView icon;
		public FileAdapter(Context c,Dialog dialog, OnSelectListener listener, ArrayList<SelectedFile> itemsData) {
			super(c, R.layout.items_file_selector, itemsData);
			
			this.itemsData = itemsData;
			this.listener = listener;
			this.dialog = dialog;
			this.inflater = LayoutInflater.from(context);
			
		}

		@Override
		public int getCount()
		{
			// TODO: Implement this method
			return itemsData.size();
		}

		@Override
		public FileSelector.SelectedFile getItem(int position)
		{
			// TODO: Implement this method
			return null;
		}

		@Override
		public long getItemId(int position)
		{
			// TODO: Implement this method
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO: Implement this method
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View inflateView = inflater.inflate(R.layout.items_file_selector, parent, false);

			final SelectedFile selectedFile = itemsData.get(position);
			nameLabel = inflateView.findViewById(R.id.tvFileName);
			detailLabel = inflateView.findViewById(R.id.tvFileDetails);
			detailLabel.setTypeface(detailLabel.getTypeface(), Typeface.ITALIC);
			detailLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);		
			icon = inflateView.findViewById(R.id.imgFileIcon);
			
			nameLabel.setText(selectedFile.name);
			detailLabel.setText(selectedFile.path);
			icon.setImageResource(R.drawable.apk_v2);
			
			inflateView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						listener.onSelect(selectedFile.path);
					}
				});
			return inflateView;
		}
	}
	
	public static class MyTask extends AsyncTask<Void, Void, Void>
	{
        private String mTips;
        private Runnable mTargetTask;
        private ProgressDialog mDialog;

        public MyTask(String tips, Runnable task)
		{
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
		{
            mDialog.dismiss();
        }
    }
}
