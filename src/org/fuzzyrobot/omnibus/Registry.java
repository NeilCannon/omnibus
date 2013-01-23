package org.fuzzyrobot.omnibus;

/**
 * User: neil
 * Date: 12/11/2012
 */
public interface Registry {
    <T> void register(Class<T> clazz, Subscriber<T> subscriber);

    void unRegister(Subscriber subscriber);

    <T> void registerAndRequest(Class<T> clazz, Subscriber<T> subscriber, String[] params);

    <T> void register(Class<T> clazz, String id, Subscriber<T> subscriber);
}
