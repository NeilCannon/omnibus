package org.fuzzyrobot.omnibus.core;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: neil
 * Date: 08/11/2012
 */
public class Receivers<T> implements Subscriber<T> {
    private final Collection<Subscriber<T>> subscribers = new ArrayList<Subscriber<T>>();

    public void register(Subscriber<T> subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void receive(T value) {
        for (Subscriber<T> subscriber : subscribers) {
            subscriber.receive(value);
        }
    }

}
