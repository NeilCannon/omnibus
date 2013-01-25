package org.fuzzyrobot.omnibus.provider;

/**
 * User: neil
 * Date: 23/01/2013
 */ /* Cache implementation to use
 */
interface Cacher<K, V> {
    V get(K key);

    V put(K key, V value);

    V remove(K key);

    void evictAll();
}
