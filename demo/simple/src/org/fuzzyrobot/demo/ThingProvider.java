package org.fuzzyrobot.demo;

import android.content.Context;
import org.fuzzyrobot.omnibus.core.Provider;
import org.fuzzyrobot.omnibus.core.Subscriber;

/**
 * User: neil
 * Date: 28/01/2013
 */
class ThingProvider extends Provider<Thing> {
    @Override
    public void provide(Context appContext, final Subscriber<Thing> subscriber, String[] params) {
        new Thread() {
            public void run() {
                subscriber.receive(new Thing("abc"));
            }
        }.start();
    }

}
