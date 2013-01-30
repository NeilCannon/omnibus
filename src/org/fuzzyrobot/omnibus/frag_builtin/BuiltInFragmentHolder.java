package org.fuzzyrobot.omnibus.frag_builtin;

import android.app.Fragment;
import org.fuzzyrobot.omnibus.core.FragmentHolder;

/**
 * User: neil
 * Date: 29/01/2013
 */
public class BuiltInFragmentHolder extends FragmentHolder {

    public BuiltInFragmentHolder(Fragment fragment) {
        super(fragment.getActivity(), fragment);
    }
}
