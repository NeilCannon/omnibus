package org.fuzzyrobot.omnibus.core;

import android.app.Activity;

/**
 * User: neil
 * Date: 29/01/2013
 * <p/>
 * Abstracts away which type of Fragment we are working with (support lib or android.app),
 * to reduce dependency on which type of fragment to as small a part of the code as possible
 */
public abstract class FragmentHolder {
    private final Activity activity;
    private final Object fragment;

    public FragmentHolder(Activity activity, Object fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    public Activity getActivity() {
        return activity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FragmentHolder that = (FragmentHolder) o;

        if (!fragment.equals(that.fragment)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fragment.hashCode();
    }

    @Override
    public String toString() {
        return "FragmentHolder{" +
                "fragment=" + fragment +
                '}';
    }
}
