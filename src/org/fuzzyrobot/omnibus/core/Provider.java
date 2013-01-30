package org.fuzzyrobot.omnibus.core;

/**
 * User: neil
 * Date: 27/01/2013
 */
public abstract class Provider<T> implements ExternalProviderInterface<T> {

    @Override
    public void invalidate() {
        // no-op default
    }

    @Override
    public void update(T value) {
        // no-op default
    }
}
