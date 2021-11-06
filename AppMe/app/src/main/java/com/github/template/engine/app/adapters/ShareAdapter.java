package com.github.template.engine.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.template.R;
import com.github.template.engine.app.dialogs.ShareDialogFragment;

import java.util.ArrayList;


public class ShareAdapter extends RecyclerArrayAdapter<Intent, ShareAdapter.ViewHolder> {
    private ShareDialogFragment dialog;
    private ArrayList<String> labels;
    private ArrayList<Drawable> drawables;
    private Context context;

    public void updateMatDialog(ShareDialogFragment b) {
        this.dialog = b;
    }

    public ShareAdapter(Context context, ArrayList<Intent> intents, ArrayList<String> labels,
                        ArrayList<Drawable> arrayList1) {
        addAll(intents);
        this.context = context;
        this.labels = labels;
        this.drawables = arrayList1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_app_share, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.render(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View rootView;

        private TextView textView;
        private ImageView imageView;

        ViewHolder(View view) {
            super(view);

            rootView = view;

            textView = ((TextView) view.findViewById(R.id.firstline));
            imageView = (ImageView) view.findViewById(R.id.icon);
        }

        void render(final int position) {
            if (drawables.get(position) != null)
                imageView.setImageDrawable(drawables.get(position));
            textView.setVisibility(View.VISIBLE);
            textView.setText(labels.get(position));
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null && dialog.isAdded()) dialog.dismiss();
                    context.startActivity(getItem(position));
                }
            });
        }
    }

}
