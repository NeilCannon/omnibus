package org.fuzzyrobot.omnibus.core;

import android.content.Context;
import android.util.Log;

/**
 * User: neil
 * Date: 27/01/2013
 */
class ProviderBinding<T> implements ProviderBindingInterface<T> {
    ProviderInterface<T> provider;
    private BusApp busApp;

    ProviderBinding(BusApp busApp, ProviderInterface<T> provider) {
        this.busApp = busApp;
        this.provider = provider;
    }

    @Override
    public void provideValue(Channel channel) {
        provideValue(null, channel, null);
    }

    @Override
    public void provideValue(Context context, final Channel channel, String[] params) {
        provider.provide(context, new Subscriber<T>() {
            public void receive(T value) {
                Log.d(BusApp.TAG + ".ProviderBinding", "receive(");

                busApp.propagate(channel, value);
            }
        }, params);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        //sb.append("ProviderBinding");
        sb.append("{provider=").append(BusContext.getShortClassName(provider.toString()));
        sb.append('}');
        return sb.toString();
    }

    @Override
    public void invalidate() {
    }

    @Override
    public void update(T value) {
    }
}
