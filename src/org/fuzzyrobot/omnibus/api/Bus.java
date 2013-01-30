package org.fuzzyrobot.omnibus.api;

import android.app.Fragment;
import android.content.Context;
import org.fuzzyrobot.omnibus.core.BusApp;
import org.fuzzyrobot.omnibus.core.BusContext;
import org.fuzzyrobot.omnibus.core.FragmentHolder;
import org.fuzzyrobot.omnibus.frag_builtin.BuiltInFragmentHolder;

/**
 * User: neil
 * Date: 29/01/2013
 */
public class Bus {


    public static BusContext attach(Context context) {
        return BusApp.attach(context);
    }

    public static BusContext attach(Fragment fragment) {
        FragmentHolder fragmentHolder = getFragmentHolder(fragment);
        return BusApp.attach(fragmentHolder);
    }

    public static void detach(Context context) {
        BusApp.detach(context);
    }

    public static void detach(Fragment fragment) {
        FragmentHolder fragmentHolder = getFragmentHolder(fragment);
        BusApp.detach(fragmentHolder);
    }

    private static FragmentHolder getFragmentHolder(Fragment fragment) {
        return new BuiltInFragmentHolder(fragment);
    }


}
