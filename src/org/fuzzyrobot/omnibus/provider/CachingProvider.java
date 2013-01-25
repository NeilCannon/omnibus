package org.fuzzyrobot.omnibus.provider;

import org.fuzzyrobot.omnibus.core.Provider;
import org.fuzzyrobot.omnibus.core.Subscriber;

/**
 * User: neil
 * Date: 10/11/2012
 */
public class CachingProvider<T> implements Provider<T> {

    private final Cacher<String, T> cacher;
    private final ParameterisedProvider<T> delegate;

    private int hits;
    private int accesses;

    public CachingProvider(ParameterisedProvider<T> delegate, int size) {
        this(delegate, new LruCacher<String, T>(size));
    }

    public CachingProvider(ParameterisedProvider<T> delegate, Cacher<String, T> cacher) {
        this.cacher = cacher;
        this.delegate = delegate;
    }

    public void provide(final Subscriber<T> subscriber, final String[] params) {
        accesses++;
        final String key = getKey(params);
        T value = cacher.get(key);
        if (value == null) {
            delegate.provide(new Subscriber<T>() {
                public void receive(T value) {
                    cacher.put(key, value);
                    subscriber.receive(value);
                }
            }, key);
        } else {
            hits++;
            subscriber.receive(value);
        }
    }

    private String getKey(String[] params) {
        StringBuilder builder = new StringBuilder();
        for (String param : params) {
            builder.append(param);
        }
        return builder.toString();
    }

    @Override
    public void invalidate() {
        cacher.evictAll();
        delegate.invalidate();
    }

    @Override
    public void update(T value) {
        delegate.update(value);
    }

    public int getHits() {
        return hits;
    }

    public int getAccesses() {
        return accesses;
    }
}
