package org.fuzzyrobot.omnibus;

/**
 * User: neil
 * Date: 09/11/2012
 */
public interface ParameterisedProvider<T> {
    void provide(Subscriber<T> subscriber, String param);

    void invalidate();

    void update(T value);
}
