package com.appme.story.application;

import android.annotation.TargetApi;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.CardView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.preference.PreferenceManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static com.appme.story.engine.app.fragments.NavigationDrawerFragment.POS_APP_INFO;
import static com.appme.story.engine.app.fragments.NavigationDrawerFragment.POS_APP_MANAGER;
import static com.appme.story.engine.app.fragments.NavigationDrawerFragment.POS_APP_MONITOR;
import static com.appme.story.engine.app.fragments.NavigationDrawerFragment.POS_APP_CLEANER;
import static com.appme.story.engine.app.fragments.NavigationDrawerFragment.POS_APP_CLIENT;
import static com.appme.story.engine.app.fragments.NavigationDrawerFragment.POS_APP_SERVER;
import static com.appme.story.engine.app.fragments.NavigationDrawerFragment.POS_APP_MORE;
import static com.appme.story.engine.app.fragments.FolderStructureFragment.Callback;
import static com.appme.story.engine.app.fragments.FolderStructureFragment.FileActionListener;
import static com.appme.story.engine.app.fragments.FolderStructureFragment.newInstance;

import com.appme.story.R;
import com.appme.story.engine.Api;
import com.appme.story.engine.app.commons.activity.ActionBarActivity;
import com.appme.story.engine.app.fragments.NavigationDrawerFragment;
import com.appme.story.engine.app.fragments.ApplicationFragment;
import com.appme.story.engine.app.fragments.AppInfoFragment;
import com.appme.story.engine.app.fragments.AppManagerFragment;
import com.appme.story.engine.app.fragments.AppMonitorFragment;
import com.appme.story.engine.app.fragments.AppCleanerFragment;
import com.appme.story.engine.app.fragments.AppClientFragment;
import com.appme.story.engine.app.fragments.AppServerFragment;
import com.appme.story.engine.app.fragments.AppAboutFragment;
import com.appme.story.engine.app.fragments.AppChromeFragment;
import com.appme.story.engine.app.fragments.FolderStructureFragment;
import com.appme.story.engine.app.folders.preview.IconPreview;
import com.appme.story.engine.app.folders.fileTree.project.ProjectFileContract;
import com.appme.story.engine.app.folders.fileTree.project.ProjectFilePresenter;
import com.appme.story.engine.app.folders.fileTree.utils.FUtils;
import com.appme.story.engine.app.folders.fileTree.settings.AppSetting;
import com.appme.story.engine.app.folders.FileSelector;
import com.appme.story.engine.app.utils.AppUtil;
import com.appme.story.engine.app.utils.ScreenUtils;
import com.appme.story.engine.app.listeners.OnRequestHandlerListener;
import com.appme.story.engine.graphics.SystemBarTintManager;
import com.appme.story.receiver.AppMeReceiver;
import com.appme.story.receiver.SendBroadcast;
import com.appme.story.service.ServiceUtils;
import com.appme.story.service.CustomTabsActivityHelper;
import com.appme.story.settings.FolderSettings;
import com.appme.story.settings.theme.ThemePreference;
import com.appme.story.settings.theme.Theme;

public class ApplicationActivity extends ActionBarActivity implements AppMeReceiver.OnSendBroadcastListener, FileActionListener {

    public static final String TAG = ApplicationActivity.class.getSimpleName();
    private SharedPreferences sp;
    private static final int CODE_READ = 42;
    private static final int CODE_WRITE = 43;
    private static final int CODE_TREE = 44;
    private static final int CODE_RENAME = 45;

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    //private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;
    private View mFragmentNavigationView;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private boolean mUserLearnedDrawer;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private LinearLayout mRootView;
    private CardView mContentView;

    private Toolbar mToolbar;
    private static final String KEY_PROJECT_FILE = "KEY_PROJECT_FILE";
    private String mProjectFile;
    private ProjectFileContract.Presenter mFilePresenter;
    private FolderSettings mPreferences;
    private AppSetting mFileTreePreference;
    
    public final static String ACTION_CHROME_TABS = "com.appme.story.application.ACTION_CHROME_TABS";
    public final static String EXTRA_URL = "EXTRA_URL";

    public static void start(Context c) {
        Intent mIntent = new Intent(c, ApplicationActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        c.startActivity(mIntent);
    }

    public static void startChrome(Context c, String url) {
        Intent mIntent = new Intent(c, ApplicationActivity.class);
        mIntent.setAction(ACTION_CHROME_TABS);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mIntent.putExtra(EXTRA_URL, url);
        c.startActivity(mIntent);
    }

    private AppMeReceiver mAppMeReceiver;
    //private static final Uri PROJECT_URI = Uri.parse("https://github.com/DreaminginCodeZH/CustomTabsHelper");

    private final CustomTabsActivityHelper.CustomTabsFallback mCustomTabsFallback = new CustomTabsActivityHelper.CustomTabsFallback() {
        @Override
        public void openUri(Activity activity, Uri uri) {
            //showToast(R.string.custom_tabs_failed);
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                //showToast(R.string.activity_not_found);               
            }
        }
    };
    private AppChromeFragment mCustomTabsHelperFragment;
    private CustomTabsIntent mCustomTabsIntent;
    private int mColorPrimary;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme();
        setUpStatusBar();
        super.onCreate(savedInstanceState);       
        setContentView(R.layout.activity_application);
        sp = PreferenceManager.getDefaultSharedPreferences(ApplicationActivity.this);
        mPreferences = FolderSettings.getSettings(this);
        mFileTreePreference = new AppSetting(this);
        
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        if (mToolbar == null) {
            getSupportActionBar().setTitle(null);         
            setSupportActionBar(mToolbar);
        }
        final TextView mAppName = (TextView) findViewById(R.id.app_title);
        mTitle = getString(R.string.app_name);
        mAppName.setText(mTitle);

        mRootView = (LinearLayout)findViewById(R.id.root_view);
        mRootView.setBackgroundColor(color(R.color.windowBackground));

        mContentView = (CardView)findViewById(R.id.cardview);
        mContentView.setCardBackgroundColor(color(R.color.windowBackground));
        ScreenUtils.setScaleAnimation(mContentView, 2000);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mFragmentContainerView = findViewById(R.id.content_frame);
        mFragmentNavigationView = findViewById(R.id.navigation_drawer);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
            this,                    /* host Activity */
            mDrawerLayout,                    /* DrawerLayout object */
            mToolbar,             /* nav drawer image to replace 'Up' caret */
            R.string.action_drawer_open,  /* "open drawer" description for accessibility */
            R.string.action_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(mFragmentNavigationView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    mDrawerToggle.syncState();
                }
            });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        mNavigationDrawerFragment = new NavigationDrawerFragment();
        String action = getIntent().getAction();  
        if (action != null && action.equals(ACTION_CHROME_TABS)) {         
            mColorPrimary = R.color.colorPrimary;
            String url = getIntent().getStringExtra(EXTRA_URL);
            final Uri PROJECT_URI = Uri.parse(url);

            mCustomTabsHelperFragment = AppChromeFragment.attachTo(this);
            mCustomTabsIntent = new CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setToolbarColor(mColorPrimary)
                .setShowTitle(true)
                .build();

            mCustomTabsHelperFragment.setConnectionCallback(new CustomTabsActivityHelper.ConnectionCallback() {
                    @Override
                    public void onCustomTabsConnected() {
                        mCustomTabsHelperFragment.mayLaunchUrl(PROJECT_URI, null, null);
                    }
                    @Override
                    public void onCustomTabsDisconnected() {}
                });

            AppChromeFragment.open(ApplicationActivity.this, mCustomTabsIntent, PROJECT_URI, mCustomTabsFallback);
        } else {
            mNavigationDrawerFragment.setSelected(NavigationDrawerFragment.POS_APP_INFO);
        }
        mNavigationDrawerFragment.setOnNavigationDrawerListener(new NavigationDrawerFragment.OnNavigationDrawerListener(){
                @Override
                public void onItemSelected(int position) {
                    if (position == POS_APP_INFO) {
                        switchFragment(AppInfoFragment.newInstance("Info"));
                    }
                    if (position == POS_APP_MANAGER) {
                        switchFragment(AppManagerFragment.newInstance("Manager"));
                    }
                    if (position == POS_APP_MONITOR) {
                        switchFragment(AppMonitorFragment.newInstance("Monitor"));
                    }
                    if (position == POS_APP_CLEANER) {
                        switchFragment(AppCleanerFragment.newInstance("Cleaner"));
                    }
                    if (position == POS_APP_CLIENT) {
                        switchFragment(AppClientFragment.newInstance("Client"));
                    }
                    if (position == POS_APP_SERVER) {
                        switchFragment(AppServerFragment.newInstance("Server"));
                    }
                    if (position == POS_APP_MORE) {
                        switchFragment(ApplicationFragment.newInstance("More"));
                    }
                    closeDrawer();
                }
            });
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.navigation_drawer, mNavigationDrawerFragment)
            .commit();

        if (ServiceUtils.isServiceRunning(this)) {          
            ServiceUtils.getInstance().onStartAppMeService(); 
        } else {
            ServiceUtils.getInstance().launchAppMeService();
        }

        mAppMeReceiver = new AppMeReceiver();
        mAppMeReceiver.setOnSendBroadcastListener(this);
        registerBroadcastReceiver();
        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_PROJECT_FILE)) {
            mProjectFile = savedInstanceState.getString(KEY_PROJECT_FILE);
        } else {
            mProjectFile = getFolderMe().getAppMeFolder();
        }

        //Change PrimaryColor
        changeActionBarColor();

        // start IconPreview class to get thumbnails if BrowserListAdapter
        // request them
        new IconPreview(this);
    }
    
    @Override
    public void onTrimMemory(int level) {
        IconPreview.clearCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRootView.setBackgroundColor(color(R.color.windowBackground));
        mContentView.setCardBackgroundColor(color(R.color.windowBackground));
        Log.v(TAG, "onResume:");  
        registerBroadcastReceiver();
        Theme.getInstance().getTheme(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause:");
        //unregisterReceiver(mAppMeReceiver); 
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy:");
        unregisterReceiver(mAppMeReceiver); 
    }

    public void registerBroadcastReceiver() {
        IntentFilter statusIntentFilter = new IntentFilter(SendBroadcast.PROCESS_BROADCAST_ACTION);
        registerReceiver(mAppMeReceiver, statusIntentFilter);
    }

    @Override
    public void onServiceReady(String message) {
        showToast(message);     
    }

    @Override
    public void onStartService(String message) {
        showToast(message);
    }

    @Override
    public void onStartActivity(String message) {
        showToast(message);
    }
    
    @Override
    public void onStartServer(String message) {
        showToast(message);
    }

    @Override
    public void onNetworkStatus(String message) {
        showToast(message);
    }

    @Override
    public void onOpenBrowser(String message) {
        showToast(message);
    }

    @Override
    public void onPauseService(String message) {
        showToast(message);
    }

    @Override
    public void onResumeService(String message) {
        showToast(message);
    }

    @Override
    public void onStopService(String message) {
        showToast(message);
    }

    @Override
    public void onServiceShutDown(String message) {
        showToast(message);
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

        setUpStatusBar();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setUpStatusBar() {
        int color = ScreenUtils.getStatusBarColor(ThemePreference.getPrimaryColor());
        if (Api.hasLollipop()) {
            getWindow().setStatusBarColor(color);
        } else if (Api.hasKitKat()) {
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setTintColor(color);
            systemBarTintManager.setStatusBarTintEnabled(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setUpDefaultStatusBar() {
        int color = ContextCompat.getColor(this, android.R.color.black);
        if (Api.hasLollipop()) {
            getWindow().setStatusBarColor(color);
        } else if (Api.hasKitKat()) {
            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(this);
            systemBarTintManager.setTintColor(ScreenUtils.getStatusBarColor(color));
            systemBarTintManager.setStatusBarTintEnabled(true);
        }
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentNavigationView);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentNavigationView);
    }  

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentNavigationView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getAboutPage() {
        closeDrawer();
        StringBuilder sb = new StringBuilder();
        sb.append("Check Activity").append("  ");
        sb.append("\n");
        sb.append("Please wait...");
        showProgress(sb.toString());
        setRequestHandler(new OnRequestHandlerListener(){
                @Override
                public void onHandler() {
                    hideProgress();
                    switchFragment(AppAboutFragment.newInstance("About"));
                }
            });
    }
    
    public void getFolderStructure() {
        closeDrawer();
        StringBuilder sb = new StringBuilder();
        sb.append("Check Folder").append("  ");
        sb.append("\n");
        sb.append("Please wait...");
        showProgress(sb.toString());
        mProjectFile = getFolderMe().getAppMeFolder();
        final File folder = new File(mProjectFile);    
        setRequestHandler(new OnRequestHandlerListener(){
                @Override
                public void onHandler() {
                    hideProgress();                
                    mFilePresenter.show(folder, true);
                }
            });
        FolderStructureFragment folderStructureFragment = null;   
        if (folderStructureFragment == null) {
            folderStructureFragment = newInstance("Folder", folder);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, folderStructureFragment, FolderStructureFragment.TAG).commit();
        mFilePresenter = new ProjectFilePresenter(folderStructureFragment);     
    }
    
    public void getAppPreference(){
        closeDrawer();
        StringBuilder sb = new StringBuilder();
        sb.append("Check Activity").append("  ");
        sb.append("\n");
        sb.append("Please wait...");
        showProgress(sb.toString());
        setRequestHandler(new OnRequestHandlerListener(){
                @Override
                public void onHandler() {
                    hideProgress();
                    ApplicationPreferences.start(ApplicationActivity.this);
                }
            });
        
    }
    
    public void getExit(){
        closeDrawer();
        StringBuilder sb = new StringBuilder();
        sb.append("Check Activity").append("  ");
        sb.append("\n");
        sb.append("Please wait...");
        showProgress(sb.toString());
        setRequestHandler(new OnRequestHandlerListener(){
                @Override
                public void onHandler() {
                    hideProgress();
                    Application.getInstance().exitApplication(ApplicationActivity.this);                
                }
            });
    }
    
    public void getChrome(final String url){
        closeDrawer();
        StringBuilder sb = new StringBuilder();
        sb.append("Check Activity").append("  ");
        sb.append("\n");
        sb.append("Please wait...");
        showProgress(sb.toString());
        setRequestHandler(new OnRequestHandlerListener(){
                @Override
                public void onHandler() {
                    hideProgress();
                    startChrome(ApplicationActivity.this, url);
                }
            });
    }
    
    @Override
    public void clickNewModule() {
    }

    @Override
    public boolean clickCreateNewFile(File file, FolderStructureFragment.Callback callBack) {
        return false;
    }

    @Override
    public boolean clickRemoveFile(File file, FolderStructureFragment.Callback callBack) {
        return false;
    }

    @Override
    public void onFileClick(File file, FolderStructureFragment.Callback callBack) {    
        boolean success = openFileByAnotherApp(file);
        if (!success) {
            showFileInfo(file);
        }      
    }

    @Override
    public void onFileLongClick(File file, FolderStructureFragment.Callback callBack) {
        if (FUtils.canRead(file)) {
            showFileInfo(file);
        } else {
            if (!openFileByAnotherApp(file)) {
                showFileInfo(file);
            }
        }
    }

    private boolean openFileByAnotherApp(File file) {
        //don't open compiled file
        if (FUtils.hasExtension(file, "class", "dex", "jar")) {
            Toast.makeText(this, "Unable to open file", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            //create intent open file
            MimeTypeMap myMime = MimeTypeMap.getSingleton();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String ext = FUtils.fileExt(file.getPath());
            String mimeType = myMime.getMimeTypeFromExtension(ext != null ? ext : "");
            intent.setDataAndType(uri, mimeType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return true;
    }


    /**
     * show dialog with file info
     * filePath, path, size, extension ...
     *
     * @param file - file to show info
     */
    private void showFileInfo(File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(file.getName());
        builder.setView(R.layout.dialog_view_file);
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView txtInfo = dialog.findViewById(R.id.txt_info);
        txtInfo.setText(file.getPath() + "\n" + file.length() + " byte");
        TextView text = dialog.findViewById(R.id.editor_view);
        text.setText(FUtils.readFileToString(file));
    }

    public void getApkBackup() {
        new FileSelector(ApplicationActivity.this, new String[]{FileSelector.APK})
            .selectFile(new FileSelector.OnSelectListener() {
                @Override
                public void onSelect(String path) {
                    File file = new File(path);
                    if (file.isDirectory())
                        throw new IllegalArgumentException("File cannot be a directory!");

                    Intent intent = AppUtil.createFileOpenIntent(file);

                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        startActivity(Intent.createChooser(intent, getString(R.string.action_open_file_with, file.getName())));
                    } catch (Exception e) {
                        new AlertDialog.Builder(ApplicationActivity.this)
                            .setMessage(e.getMessage())
                            .setTitle("Error")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                    }
                }
            });
    }
    
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_PROJECT_FILE, mProjectFile);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isDrawerOpen()) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

}
