package org.fuzzyrobot.omnibus.core;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import org.fuzzyrobot.omnibus.MultiSubscribe;
import org.fuzzyrobot.omnibus.Subscriber2;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: neil
 * Date: 07/11/2012
 * <p/>
 * Manages the receivers for an Android Context. When the Context (i.e. Activity, Service) is destroyed, so is the BusContext.
 * A single Subscriber can only be registered once, and when a Subscriber is no longer needed by the client, it will be destroyed
 * (so if a client keeps registering new Receivers the old ones will be cleaned up)
 */
public class BusContext implements BusInterface {
    private static final String TAG = BusContext.class.getSimpleName();

    private final BusInterface parent;
    private final Context context;
    private Map<Subscriber, ReceiverBinding> receiverBindings = new ConcurrentHashMap<Subscriber, ReceiverBinding>();

    private Map<FragmentHolder, BusContext> busContexts = new WeakHashMap<FragmentHolder, BusContext>();

    private int receivedCount;
    private int receiverNotifiedCount;

    private int instanceId;
    private static int instanceCount;
    private static int finalizedCount;
    private int attachCount;
    private int detachCount;

    public void clear() {
        receiverBindings.clear();
    }

    public BusContext(BusInterface parent, Context context) {
        this.parent = parent;
        this.context = context;
        instanceId = instanceCount++;
    }

    BusContext doAttach(FragmentHolder fragment, Activity activity) {
        BusContext busContext = busContexts.get(fragment);
        if (busContext == null) {
            busContext = new BusContext(this, activity);
            busContexts.put(fragment, busContext);
            attachCount++;
        }
        return busContext;
    }

    void doDetach(FragmentHolder fragment) {
        BusContext detached = busContexts.remove(fragment);
        if (detached != null) {
            for (Subscriber subscriber : detached.receiverBindings.keySet()) {
                unSubscribe(subscriber);
            }
            detached.clear();
            detachCount++;
        }
    }

    public <T> void subscribe(Class<T> clazz, Subscriber<T> subscriber) {
        Log.d(TAG, "register(");
        if (subscriber == null) {
            throw new RuntimeException("null Subscriber for " + clazz);
        }
        if (receiverBindings.containsKey(subscriber)) {
            Log.w(TAG, "Duplicate Registration Ignored");
            return;
        }
        receiverBindings.put(subscriber, new ReceiverBinding(clazz, subscriber));
        parent.subscribe(clazz, subscriber);
    }

    public <T1, T2> void subscribe(Class<T1> clazz1, Class<T2> clazz2, Subscriber2<T1, T2> subscriber) {
        MultiSubscribe.subscribe(this, clazz1, clazz2, subscriber);
    }

    @Override
    public void unSubscribe(Subscriber subscriber) {
        ReceiverBinding removed = receiverBindings.remove(subscriber);
        parent.unSubscribe(subscriber);
    }

    @Override
    public <T> void subscribeAndRequest(Class<T> clazz, Subscriber<T> subscriber, String[] params) {
        //String channel = subscriber.toString();
        subscribe(clazz, subscriber);
        request(clazz, params);
    }

    public <T> void registerAndRequest(Class<T> clazz, Subscriber<T> subscriber, String param) {
        //String channel = subscriber.toString();
        subscribe(clazz, subscriber);
        request(clazz, param);
    }


    @Override
    public <T> void subscribe(Class<T> clazz, String channelId, Subscriber<T> subscriber) {
        receiverBindings.put(subscriber, new ReceiverBinding(clazz, channelId, subscriber));
        parent.subscribe(clazz, channelId, subscriber);
    }

//    public <T> void provide(Class<T> clazz, String channelId, Provider<T> provider) {
//        parent.provide(clazz, channelId, provider);
//    }
//
//    public <T> void provide(Class<T> clazz, Provider<T> provider) {
//        provide(clazz, null, provider);
//    }

    public <T> void request(Class<T> clazz, String channelId, String[] params) {
        parent.request(clazz, channelId, params);
    }

    public <T> void request(Class<T> clazz, String[] params) {
        request(clazz, null, params);
    }

    public <T> void request(Class<T> clazz, String param) {
        request(clazz, null, new String[]{param});
    }

    @Override
    public void publish(Object value) {
        parent.publish(null, value);
    }

    @Override
    public void publish(Channel channel, Object value) {
        parent.publish(channel, value);
    }

    @Override
    public void invalidate(Class clazz) {
        parent.invalidate(clazz);
    }

    @Override
    public void update(Object value) {
        parent.update(value);
    }

    public void post(String channelId, Object value) {
        parent.publish(new Channel(value.getClass(), channelId), value);
    }

    void receive(Channel channel, Object value) {
        Log.d(TAG, "receive(" + value);
        receivedCount++;
        for (ReceiverBinding receiverBinding : receiverBindings.values()) {
            receiverBinding.receive(channel, value);
            receiverNotifiedCount++;
        }
        for (BusContext busContext : busContexts.values()) {
            busContext.receive(channel, value);
        }
    }

    void receive(Object value) {
        receivedCount++;
        for (ReceiverBinding receiverBinding : receiverBindings.values()) {
            receiverBinding.receive(null, value);
            receiverNotifiedCount++;
        }
        for (BusContext busContext : busContexts.values()) {
            busContext.receive(value);
        }
    }

//    public void receiveProvider(Channel channel, ParameterisedProvider provider) {
//        receivedCount++;
//        for (ReceiverBinding receiverBinding : receiverBindings.values()) {
//            receiverBinding.receive(channel, provider);
//            receiverNotifiedCount++;
//        }
//    }

    public void receiveProvider(Channel channel, ProviderInterface provider) {
        receiveProvider(channel, provider, null);
    }

    public void receiveProvider(Channel channel, ProviderInterface provider, String[] params) {
        receivedCount++;
        for (ReceiverBinding receiverBinding : receiverBindings.values()) {
            receiverBinding.receive(channel, context, provider, params);
            receiverNotifiedCount++;
        }
    }

    public int getReceivedCount() {
        return receivedCount;
    }

    public int getReceiverNotifiedCount() {
        return receiverNotifiedCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("BusContext[");
        sb.append(instanceId);
        sb.append(']');
        return sb.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        finalizedCount++;
    }

    public void dump(String prefix) {
        String pre = prefix + "    ";
        if (parent instanceof BusApp) {
            dump(pre, "instanceCount: " + instanceCount);
            dump(pre, "finalizedCount: " + finalizedCount);
        }
        dump(pre, "attachCount: " + attachCount);
        dump(pre, "detachCount: " + detachCount);
        for (Map.Entry<Subscriber, ReceiverBinding> entry : receiverBindings.entrySet()) {
            String receiver = entry.getKey().toString();
            dump(pre, getShortClassName(receiver) + " --> " + entry.getValue());
        }
        for (Map.Entry<FragmentHolder, BusContext> entry : busContexts.entrySet()) {
            dump(pre, entry.getKey() + " --> " + entry.getValue());
            entry.getValue().dump(pre);
        }
    }

    private void dump(String prefix, String msg) {
        Log.d(BusApp.TAG, "| " + prefix + msg);
    }

    public static String getShortClassName(String name) {
        return name.startsWith("com.panasonic.inflight") ? name.substring("com.panasonic.inflight".length()) : name;
    }
}
