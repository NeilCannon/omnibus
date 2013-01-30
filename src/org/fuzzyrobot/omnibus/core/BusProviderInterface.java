package org.fuzzyrobot.omnibus.core;

/**
 * User: neil
 * Date: 28/01/2013
 */
public interface BusProviderInterface {
    <T> void provide(Class<T> clazz, Provider<T> provider);

    <T> void provide(Class<T> clazz, String channelId, Provider<T> provider);
}
