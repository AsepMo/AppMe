package com.github.template.engine.app.dialogs;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.template.R;
import com.github.template.engine.app.adapters.ShareAdapter;

import java.util.ArrayList;
import java.io.File;

public class ShareDialogFragment extends BottomSheetDialogFragment {

  public static final String TAG = ShareDialogFragment.class.getSimpleName();
    
    private ImageButton mIcon;
    private TextView mTitle;
    private ImageButton mClose;
    private RecyclerView mRecyclerView;
    
    
    private static ArrayList<Intent> targetShareIntents = new ArrayList<>();
    private static ArrayList<String> arrayList1 = new ArrayList<>();
    private static ArrayList<Drawable> arrayList2 = new ArrayList<>();
    
    private ShareAdapter shareAdapter;
    public static ShareDialogFragment newInstance(ArrayList<Intent> target, ArrayList<Drawable> icon, ArrayList<String> title) {
        ShareDialogFragment fragment = new ShareDialogFragment();
        targetShareIntents = target;
        arrayList2= icon;
        arrayList1 = title;
        return fragment;
    }
    
    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }
        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.layout_app_share, null);
        mIcon = (ImageButton) contentView.findViewById(R.id.bottom_sheet_icon);
        mIcon.setImageResource(R.drawable.ic_back);
       
        mTitle = (TextView) contentView.findViewById(R.id.bottom_sheet_title);
        mTitle.setText("Share");
        mTitle.setTextColor(ContextCompat.getColor(dialog.getContext(),R.color.md_white_1000));

        mClose = (ImageButton) contentView.findViewById(R.id.bottom_sheet_close);
        mClose.setImageResource(R.drawable.ic_close_circle);
        mClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.bottom_sheet_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        
        shareAdapter = new ShareAdapter(getActivity(), targetShareIntents, arrayList1, arrayList2);
        mRecyclerView.setAdapter(shareAdapter);
        
        contentView.findViewById(R.id.ll_bottom_sheet_layout).setBackgroundColor(ContextCompat.getColor(dialog.getContext(), android.R.color.darker_gray));     
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

    }

}
