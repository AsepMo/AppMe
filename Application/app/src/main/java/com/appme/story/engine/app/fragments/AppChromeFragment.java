package com.appme.story.engine.app.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.appme.story.service.CustomTabsActivityHelper;

import java.util.List;

/**
 * A Fragment that manages a {@link CustomTabsActivityHelper}.
 */
public class AppChromeFragment extends Fragment {

    private static final String FRAGMENT_TAG = AppChromeFragment.class.getName();

    private CustomTabsActivityHelper mCustomTabsActivityHelper = new CustomTabsActivityHelper();

    /**
     * Ensure that an instance of this fragment is attached to an activity.
     *
     * @param activity The target activity.
     * @return An instance of this fragment.
     */
    public static AppChromeFragment attachTo(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        AppChromeFragment fragment = (AppChromeFragment) fragmentManager
                .findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new AppChromeFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commit();
        }
        return fragment;
    }

    /**
     * Ensure that an instance of this fragment is attached to the host activity of a fragment.
     *
     * @param fragment The target fragment, which will be used to retrieve the host activity.
     * @return An instance of this fragment.
     */
    public static AppChromeFragment attachTo(Fragment fragment) {
        return attachTo(fragment.getActivity());
    }

    // Cannot get javadoc to compile, saying "reference not found".
    /*
     * @see CustomTabsActivityHelper#openCustomTab(Activity, CustomTabsIntent, Uri, CustomTabsActivityHelper.CustomTabsFallback)
     */
    public static void open(Activity activity, CustomTabsIntent intent, Uri uri,
                            CustomTabsActivityHelper.CustomTabsFallback fallback) {
        try {
            CustomTabsActivityHelper.openCustomTab(activity, intent, uri, fallback);
        } catch (ActivityNotFoundException e) {
            fallback.openUri(activity, uri);
        }
    }

    /**
     * Get the {@link CustomTabsActivityHelper} this fragment manages.
     *
     * @return The {@link CustomTabsActivityHelper}.
     */
    public CustomTabsActivityHelper getHelper() {
        return mCustomTabsActivityHelper;
    }

    /**
     * @see CustomTabsActivityHelper#getSession()
     */
    public CustomTabsSession getSession() {
        return mCustomTabsActivityHelper.getSession();
    }

    /**
     * @see CustomTabsActivityHelper#setConnectionCallback(CustomTabsActivityHelper.ConnectionCallback)
     */
    public void setConnectionCallback(
            CustomTabsActivityHelper.ConnectionCallback connectionCallback) {
        mCustomTabsActivityHelper.setConnectionCallback(connectionCallback);
    }

    /**
     * @see CustomTabsActivityHelper#mayLaunchUrl(Uri, Bundle, List)
     */
    public boolean mayLaunchUrl(Uri uri, Bundle extras, List<Bundle> otherLikelyBundles) {
        return mCustomTabsActivityHelper.mayLaunchUrl(uri, extras, otherLikelyBundles);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setUserVisibleHint(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mCustomTabsActivityHelper.bindCustomTabsService(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();

        mCustomTabsActivityHelper.unbindCustomTabsService(getActivity());
    }
}
