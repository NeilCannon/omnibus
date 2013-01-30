package org.fuzzyrobot.omnibus.core;

import android.content.Context;

/**
 * User: neil
 * Date: 27/01/2013
 */
public interface ProviderBindingInterface<T> {
    void provideValue(Channel channel);

    void provideValue(Context context, Channel channel, String[] params);

    void invalidate();

    void update(T value);
}
