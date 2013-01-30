package org.fuzzyrobot.omnibus.core;

import android.content.Context;

/**
 * User: neil
 * Date: 09/11/2012
 */
public interface ProviderInterface<T> {
    void provide(Context appContext, Subscriber<T> subscriber, String[] params);
}
