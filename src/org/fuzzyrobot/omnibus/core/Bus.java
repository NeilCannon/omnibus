package org.fuzzyrobot.omnibus.core;

import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import org.fuzzyrobot.omnibus.MultiSubscribe;
import org.fuzzyrobot.omnibus.Subscriber2;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: neil
 * Date: 07/11/2012
 * <p/>
 * An app-wide Data Bus: (see e.g. http://c2.com/cgi/wiki?DataBusPattern)
 * Activities should call attach() in their onResume() to get a BusContext that handles lifecycle, then call detach() in their onPause().
 * Then they should use:
 * busContext.register(Class, Subscriber<T>)
 * to register a Subscriber to be notified when an object of a certain class is placed on the Bus.
 * <p/>
 * To notify all Receivers, use:
 * busContext.post(anObject)
 * <p/>
 * If there is a need to distinguish between different instances of the same class, use a 'channel':
 * register(Class, "myChannelId", Subscriber<T>)
 * and:
 * busContext.post("myChannelId", anObject)
 * <p/>
 * <p/>
 * For asynchronous API calls, Providers are used instead of post:
 * <p/>
 * busContext.provide(Movies.class, new ParamProviderFactory<Movies>() {
 * protected Movies retrieveValue(String[] params) {
 * return someApi.getMoviesViaSomeHttpCall();
 * }
 * });
 * <p/>
 * When a value is requested via request(), the retrieveValue() call will be run in an AsyncTask and all Receivers registered for the given class will receive the
 * result on the UI Thread when the call completes.
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * As with post(), it's possible to provide/request on a channel.
 * <p/>
 * More notes:
 * It doesn't matter which order register/post/provide calls occur in, Receivers will be notified whenever a value is available.
 * All Providers and posted values are app-scope - the intention is that there is a finite number of these.
 * OTOH Receivers are cleaned up whenever a Context detaches itself.
 * The Bus can be used from Services as well as Activities, just call attach() in OnCreate() & detach() in onDestroy().
 * <p/>
 * Fragments:
 * A fragment can be attached as a child BusContext of a Context's BusContext with:
 * Bus.attach(Fragment) & Bus.detach(Fragment)
 * This ensures that the fragments Receivers are unregistered when it is destroyed
 */
public class Bus implements Postable, BusInterface {
    public static final boolean DEBUG = false;
    private Map<Context, BusContext> busContexts = new WeakHashMap<Context, BusContext>();
    private Map<Channel, Object> valueBindings = new ConcurrentHashMap<Channel, Object>();
    private Map<Channel, ProviderBinding> providerBindings = new HashMap<Channel, ProviderBinding>();

    static Bus instance = new Bus();
    static final String TAG = Bus.class.getSimpleName();
    private Handler handler;
    private static final boolean DUMP = false;

    public static Bus getInstance() {
        return instance;
    }

    public Bus() {
        if (DEBUG && DUMP) {
            if (handler == null) {
                handler = new Handler();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dump();
                    handler.postDelayed(this, 15000);
                }
            });
        }
    }

    public static BusContext attach(Context context) {
        return getInstance().doAttach(context);
    }

    public static BusContext attach(Fragment fragment) {
        return attach(fragment.getActivity()).doAttach(fragment);
    }

    public static void detach(Context context) {
        getInstance().doDetach(context);
    }

    public static void detach(Fragment fragment) {
        attach(fragment.getActivity()).doDetach(fragment);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(Class<T> key) {
        return (T) valueBindings.get(new Channel(key));
    }

    public void post(String id, Object value) {
        publish(new Channel(value.getClass(), id), value);
    }


    class ProviderBinding<T> {
        Provider<T> provider;

        ProviderBinding(Provider<T> provider) {
            this.provider = provider;
        }

        public void provideValue(Channel channel) {
            provideValue(channel, null);
        }

        public void provideValue(final Channel channel, String[] params) {
            provider.provide(new Subscriber<T>() {
                public void receive(T value) {
                    Log.d(TAG + ".ProviderBinding", "receive(");

                    propagate(channel, value);
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

        public void invalidate() {
            provider.invalidate();
        }

        public void update(T value) {
            provider.update(value);
        }
    }

    public BusContext doAttach(Context context) {
        BusContext busContext = busContexts.get(context);
        if (busContext == null) {
            busContext = new BusContext(this);
            busContexts.put(context, busContext);
        }
        return busContext;
    }

    public void doDetach(Context context) {
        BusContext detached = busContexts.remove(context);
        if (detached != null) {
            detached.clear();
        }
    }

    public void publish(Object value) {
        publish((Channel) null, value);
    }

    public void publish(Channel channel, Object value) {
        if (value instanceof Channel) {
            throw new RuntimeException("Can't post a Channel!");
        }
        if (channel == null) {
            channel = new Channel(value.getClass());
        }
        valueBindings.put(channel, value);
        propagate(channel, value);
    }

    @Override
    public void unSubscribe(Subscriber subscriber) {
    }

    @Override
    public <T> void subscribeAndRequest(Class<T> clazz, Subscriber<T> subscriber, String[] params) {
    }

    private void propagate(Channel channel, Object value) {
        Log.d(TAG, "propagate(");
        for (BusContext busContext : busContexts.values()) {
            busContext.receive(channel, value);
        }
    }

    public <T> void subscribe(Class<T> clazz, String channelId, Subscriber<T> subscriber) {
        Channel channel = new Channel(clazz, channelId);
        for (Map.Entry<Channel, Object> entry : valueBindings.entrySet()) {
            if (channel.isAssignableFrom(entry.getKey())) {
                subscriber.receive((T) entry.getValue());
            }
        }
        for (Map.Entry<Channel, ProviderBinding> entry : providerBindings.entrySet()) {
            if (channel.isAssignableFrom(entry.getKey())) {
                entry.getValue().provideValue(channel);
            }
        }
    }

    @Override
    public <T> void subscribe(Class<T> clazz, Subscriber<T> subscriber) {
        subscribe(clazz, null, subscriber);
    }

    public <T1, T2> void subscribe(Class<T1> clazz1, Class<T2> clazz2, Subscriber2<T1, T2> subscriber) {
        MultiSubscribe.subscribe(this, clazz1, clazz2, subscriber);
    }


    @Override
    public <T> void publish(Class<T> clazz, Provider<T> provider) {
        publish(clazz, null, provider);
    }

    @Override
    public void invalidate(Class clazz) {
        Channel channel = new Channel(clazz);
        valueBindings.remove(channel);
        providerBindings.get(channel).invalidate();
    }

    @Override
    public void update(Object value) {
        Channel channel = new Channel(value.getClass());
        providerBindings.get(channel).update(value);
    }

    @Override
    public void publish(Class clazz, String channelId, Provider provider) {
        Channel channel = new Channel(clazz, channelId);
        providerBindings.put(channel, new ProviderBinding(provider));
        for (BusContext busContext : busContexts.values()) {
            busContext.receiveProvider(channel, provider);
        }
    }

    public void provide(Class clazz, String channelId, Provider provider, String[] params) {
        Channel channel = new Channel(clazz, channelId);
        providerBindings.put(channel, new ProviderBinding(provider));
        for (BusContext busContext : busContexts.values()) {
            busContext.receiveProvider(channel, provider, params);
        }
    }

    public <T> void request(Class<T> clazz, String param) {
        request(clazz, null, new String[]{param});
    }

    @Override
    public <T> void request(Class<T> clazz, String channelId, String[] params) {
        Channel channel = new Channel(clazz, channelId);
        ProviderBinding providerBinding = providerBindings.get(channel);
        if (providerBinding == null) {
            throw new BusException("No such channel: " + channel);
        }
        providerBinding.provideValue(channel, params);
    }

    public void dump() {
        if (!Bus.DEBUG) {
            return;
        }
        Log.d(TAG, ",----------------------------------------------------------------------------------");

        for (Map.Entry<Context, BusContext> entry : busContexts.entrySet()) {
            Log.d(TAG, "| " + entry.getKey() + " --> " + entry.getValue());
            entry.getValue().dump("");
        }
        Log.d(TAG, "|");

        for (Map.Entry<Channel, Object> entry : valueBindings.entrySet()) {
            Log.d(TAG, "| " + entry.getKey() + " --> " + entry.getValue());
            //entry.getValue().dump();
        }
        Log.d(TAG, "|");

        for (Map.Entry<Channel, ProviderBinding> entry : providerBindings.entrySet()) {
            Log.d(TAG, "| " + entry.getKey() + " --> " + entry.getValue());
            //entry.getValue().dump();
        }
        Log.d(TAG, "`---------------------------------------------------------------------------------\n\n");
    }

}
