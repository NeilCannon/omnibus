package org.fuzzyrobot.omnibus;

import android.util.Log;
import android.util.LruCache;

import java.util.Arrays;
import java.util.List;

/**
 * User: neil
 * Date: 16/11/2012
 */
public abstract class ParamProviderFactory<T> implements Provider<T>, ValueRetriever<T> {
    private boolean needsParams = false;

    private LruCache<List<String>, Provider<T>> providers = new LruCache<List<String>, Provider<T>>(5);
    private static final String TAG = ParamProviderFactory.class.getSimpleName();

    protected ParamProviderFactory(boolean needsParams) {
        this.needsParams = needsParams;
    }

    @Override
    public void provide(Subscriber<T> subscriber, String[] params) {
        Log.d(TAG, "provide(");

        if (params == null && needsParams) {
            return;
        }
        List<String> key = Arrays.asList(params);
        Provider<T> provider = providers.get(key);
        if (provider == null) {
            provider = new AsyncProvider<T>(needsParams) {
                @Override
                public T retrieveValue(String[] params) throws Exception {
                    return ParamProviderFactory.this.retrieveValue(params);
                }
            };
            providers.put(key, provider);
        }
        provider.provide(subscriber, params);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ParamProviderFactory");
        sb.append("{needsParams=").append(needsParams);
        sb.append(", providers=").append(providers);
        sb.append('}');
        return sb.toString();
    }
}
