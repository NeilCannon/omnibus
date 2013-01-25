package org.fuzzyrobot.omnibus;

import org.fuzzyrobot.omnibus.core.BusInterface;
import org.fuzzyrobot.omnibus.core.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: neil
 * Date: 07/01/2013
 */
public class MultiSubscribe {
    private final Subscriber2 subscriber2;
    private final List<Class> classes = new ArrayList<Class>(2);
    private final Map<Class, Object> values = new HashMap<Class, Object>(2);

    public static <T1, T2> void subscribe(BusInterface bus, Class<T1> clazz1, Class<T2> clazz2, Subscriber2<T1, T2> subscriber) {
        MultiSubscribe ms = new MultiSubscribe(subscriber);
        bus.subscribe(clazz1, ms.forType(clazz1));
        bus.subscribe(clazz2, ms.forType(clazz2));
    }

    public MultiSubscribe(Subscriber2 subscriber2) {
        this.subscriber2 = subscriber2;
    }

    public <T> Subscriber<T> forType(final Class<T> clazz) {
        classes.add(clazz);
        return new Subscriber<T>() {
            public void receive(T value) {
                values.put(clazz, value);
                if (values.size() == 2) {
                    subscriber2.receive(values.get(classes.get(0)), classes.get(1));
                }
            }
        };
    }
}
