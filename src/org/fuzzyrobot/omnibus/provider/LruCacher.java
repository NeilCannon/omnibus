package org.fuzzyrobot.omnibus.provider;

import android.util.LruCache;

/**
 * User: neil
 * Date: 23/01/2013
 */ /* Default cache implementation
 */
public class LruCacher<K, V> implements Cacher<K, V> {
    private final LruCache<K, V> lruCache;

    LruCacher(int size) {
        lruCache = new LruCache<K, V>(size);
    }

    @Override
    public V get(K key) {
        return lruCache.get(key);
    }

    @Override
    public V put(K key, V value) {
        return lruCache.put(key, value);
    }

    @Override
    public V remove(K key) {
        return lruCache.remove(key);
    }

    @Override
    public void evictAll() {
        lruCache.evictAll();
    }
}
