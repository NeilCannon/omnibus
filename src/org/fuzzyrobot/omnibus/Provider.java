package org.fuzzyrobot.omnibus;

/**
 * User: neil
 * Date: 09/11/2012
 */
public interface Provider<T> {
    void provide(Subscriber<T> subscriber, String[] params);

    void invalidate();

    void update(T value);
}
