package com.appme.story.engine.app.fragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.appme.story.R;
import com.appme.story.engine.app.folders.fileTree.FileTree;
import com.appme.story.engine.app.folders.fileTree.project.ProjectFileContract;
import com.appme.story.engine.app.folders.fileTree.holder.FolderHolder;
import com.appme.story.engine.app.folders.fileTree.model.TreeNode;
import com.appme.story.engine.app.folders.fileTree.view.AndroidTreeView;
import com.appme.story.settings.theme.ThemePreference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.widget.FrameLayout.LayoutParams;

/**
 * Created by Duy on 17-Jul-17.
 */

public class FolderStructureFragment extends Fragment implements ProjectFileContract.View {
    
    public static final String TAG = "FolderStructureFragment";
    private static final String EXTRA_TEXT = "text";
    private AppCompatActivity mActivity;
    private Context mContext;
    private Toolbar mToolbar;
    
    private final android.os.Handler mHandler = new android.os.Handler();
    private File mProjectFile;
    
    @Nullable
    private FileActionListener listener;
    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            FolderHolder.TreeItem i = (FolderHolder.TreeItem) value;
            if (listener != null && i.getFile().isFile()) {
                listener.onFileClick(i.getFile(), null);
            }
        }
    };
    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            FolderHolder.TreeItem i = (FolderHolder.TreeItem) value;
            if (listener != null && i.getFile().isFile()) {
                listener.onFileLongClick(i.getFile(), null);
            }
            return true;
        }
    };
    private ViewGroup mContainerView;
    private ProjectFileContract.Presenter presenter;
    @Nullable
    private AndroidTreeView mTreeView;
    private SharedPreferences mPref;

    public static FolderStructureFragment newInstance(String text, @NonNull File projectFile) {
        FolderStructureFragment fragment = new FolderStructureFragment();   
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        args.putSerializable(FileTree.PROJECT_FILE, projectFile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_folder_structure, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String text = getArguments().getString(EXTRA_TEXT);
        mProjectFile = (File) getArguments().getSerializable(FileTree.PROJECT_FILE);
       
        mActivity = (AppCompatActivity)getActivity();
        mContext = getActivity();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mActivity.setSupportActionBar(mToolbar);
            final ActionBar actionbar = getSupportActionBar();
            actionbar.setTitle(text); 
        }
        changeActionBarColor();
        mContainerView = view.findViewById(R.id.container);
        /*view.findViewById(R.id.img_refresh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refresh();
                }
            });*/

        display(mProjectFile, true);
        /*view.findViewById(R.id.img_expand_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mTreeView != null) mTreeView.expandAll();
                }
            });
        view.findViewById(R.id.img_collapse).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mTreeView != null) mTreeView.collapseAll();
                }
            });*/
        if (savedInstanceState != null) {
            if (mTreeView != null) {
                String state = savedInstanceState.getString("tState");
                if (!TextUtils.isEmpty(state)) {
                    mTreeView.restoreState(state);
                }
            }
        } else if (mTreeView != null) {
            String state = mPref.getString("tree_state", "");
            if (!state.isEmpty()) mTreeView.restoreState(state);
        }

        /*view.findViewById(R.id.img_add_dependencies).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.clickNewModule();
                }
            });*/
    }

    public ActionBar getSupportActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    private Drawable oldBackground;
    private void changeActionBarColor() {

        int color = ThemePreference.getPrimaryColor();
        Drawable colorDrawable = new ColorDrawable(color);

        if (oldBackground == null) {
            mToolbar.setBackgroundDrawable(colorDrawable);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, colorDrawable });
            mToolbar.setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = colorDrawable;
    }
    
    @Nullable
    private TreeNode createFileStructure(File projectFile) {
        File rootDir = projectFile;
        TreeNode root = new TreeNode(new FolderHolder.TreeItem(rootDir, rootDir, listener));
        try {
            root.addChildren(getNode(rootDir, rootDir));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }


    private ArrayList<TreeNode> getNode(File projectFile, File parent) {
        ArrayList<TreeNode> nodes = new ArrayList<>();
        try {
            if (parent.isDirectory()) {
                File[] child = parent.listFiles();

                if (child != null) {
                    ArrayList<File> dirs = new ArrayList<>();
                    ArrayList<File> files = new ArrayList<>();
                    for (File file : child) {
                        if (file.isFile()) files.add(file);
                        if (file.isDirectory()) dirs.add(file);
                    }
                    Collections.sort(dirs, new Comparator<File>() {
                            @Override
                            public int compare(File o1, File o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                    Collections.sort(files, new Comparator<File>() {
                            @Override
                            public int compare(File o1, File o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        });
                    for (File file : dirs) {
                        TreeNode node = new TreeNode(new FolderHolder.TreeItem(projectFile, file, listener));
                        if (file.isDirectory()) {
                            node.addChildren(getNode(projectFile, file));
                        }
                        nodes.add(node);
                    }
                    for (File file : files) {
                        TreeNode node = new TreeNode(new FolderHolder.TreeItem(projectFile, file, listener));
                        if (file.isDirectory()) {
                            node.addChildren(getNode(projectFile, file));
                        }
                        nodes.add(node);
                    }
                }
            } else {
                TreeNode node = new TreeNode(new FolderHolder.TreeItem(projectFile, parent, listener));
                nodes.add(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodes;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.expandAll:
//                mTreeView.expandAll();
//                break;
//
//            case R.id.collapseAll:
//                mTreeView.collapseAll();
//                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTreeView != null) {
            outState.putString("tState", mTreeView.getSaveState());
        }
    }


    @Override
    public void display(File projectFile, boolean expand) {
        this.mProjectFile = projectFile;
        refresh();
        if (expand && mTreeView != null) mTreeView.expandAll();
    }

    @Override
    public void refresh() {
        if (mProjectFile == null) return;
        TreeNode root = TreeNode.root();
        TreeNode fileStructure = createFileStructure(mProjectFile);
        if (fileStructure != null) {
            root.addChildren(fileStructure);
        }

        String saveState = null;
        if (mTreeView != null) {
            saveState = mTreeView.getSaveState();
        }

        mTreeView = new AndroidTreeView(getContext(), root);
        mTreeView.setDefaultAnimation(false);
        mTreeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        mTreeView.setDefaultViewHolder(FolderHolder.class);
        mTreeView.setDefaultNodeClickListener(nodeClickListener);
        mTreeView.setDefaultNodeLongClickListener(nodeLongClickListener);
        if (saveState != null) {
            mTreeView.restoreState(saveState);
        }
        mContainerView.removeAllViews();
        View view = mTreeView.getView();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mContainerView.addView(view, params);
    }

    @Override
    public void setPresenter(ProjectFileContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.listener = (FileActionListener) getActivity();
        } catch (ClassCastException ignored) {
        }
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void onDestroyView() {
        if (mTreeView != null) {
            String saveState = mTreeView.getSaveState();
            mPref.edit().putString("tree_state", saveState).apply();
        }
        super.onDestroyView();
    }


    public interface FileActionListener {
        /**
         * This method will be call when user click file or folder
         *
         * @param file
         * @param callBack
         */
        void onFileClick(File file, @Nullable Callback callBack);

        void onFileLongClick(File file, @Nullable Callback callBack);

        boolean clickRemoveFile(File file, Callback callBack);

        boolean clickCreateNewFile(File file, Callback callBack);

        void clickNewModule();
    }

    public interface Callback {
        void onSuccess(File file);

        void onFailed(@Nullable Exception e);
    }
}

