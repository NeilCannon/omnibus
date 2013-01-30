package org.fuzzyrobot.omnibus.core;

/**
 * User: neil
 * Date: 09/11/2012
 */
public interface ExternalProviderInterface<T> extends ProviderInterface<T> {

    void invalidate();

    void update(T value);
}
