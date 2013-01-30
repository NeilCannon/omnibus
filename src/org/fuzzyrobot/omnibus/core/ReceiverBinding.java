package org.fuzzyrobot.omnibus.core;

import android.content.Context;
import android.util.Log;

/**
 * User: neil
 * Date: 12/11/2012
 */
class ReceiverBinding {
    private final Channel channel;
    private Subscriber subscriber;
    private Object lastValue;
    private static final String TAG = ReceiverBinding.class.getSimpleName();

    ReceiverBinding(Class clazz, String id, Subscriber subscriber) {
        this.channel = new Channel(clazz, id);
        this.subscriber = subscriber;
    }

    ReceiverBinding(Class clazz, Subscriber subscriber) {
        this.channel = new Channel(clazz, null);
        this.subscriber = subscriber;
    }

    private void receive(Object value) {
        if (this.channel.isInstance(value)) {
            if (lastValue != value) {      // yes, we are using identity, we'd like to receive updates even if equals is true
                Log.d(TAG, "receive(");
                subscriber.receive(value);
                lastValue = value;
            }
        }
    }

    public void receive(Channel channel, Object value) {
        if (this.channel.isAssignableFrom(channel)) {
            receive(value);
        }
    }

    public void receive(Channel channel, Context context, ProviderInterface provider, String[] params) {
        if (this.channel.isAssignableFrom(channel)) {
            provider.provide(context, subscriber, params);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{channel=").append(channel);
        if (lastValue != null) {
            sb.append(", lastValue=").append(lastValue);
        }
        sb.append('}');
        return sb.toString();
    }

    //    public void receive(Channel channel1, ParameterisedProvider provider) {
//        if (lastValue != null) {
//            if (this.channel.isAssignableFrom(channel1)) {
//                provider.provide(subscriber, (String) lastValue);
//            }
//        }
//    }
}
