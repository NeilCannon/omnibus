package org.fuzzyrobot.omnibus.provider;

import org.fuzzyrobot.omnibus.core.Subscriber;

/**
 * User: neil
 * Date: 09/11/2012
 */
public interface ParameterisedProvider<T> {
    void provide(Subscriber<T> subscriber, String param);

    void invalidate();

    void update(T value);
}
