package org.fuzzyrobot.omnibus;

/**
 * User: neil
 * Date: 19/11/2012
 */
public interface BusInterface {

    <T> void subscribe(Class<T> clazz, Subscriber<T> subscriber);

    <T> void subscribe(Class<T> clazz, String channelId, Subscriber<T> subscriber);


    void publish(Object value);

    void publish(Channel channel, Object value);

    <T> void publish(Class<T> clazz, Provider<T> provider);

    <T> void publish(Class<T> clazz, String channelId, Provider<T> provider);

    void invalidate(Class clazz);

    void update(Object value);

    <T> void request(Class<T> clazz, String channelId, String[] params);

    <T> void subscribeAndRequest(Class<T> clazz, Subscriber<T> subscriber, String[] params);

    void unSubscribe(Subscriber subscriber);

}
