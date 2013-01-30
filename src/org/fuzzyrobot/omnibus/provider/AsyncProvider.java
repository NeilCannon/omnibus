package org.fuzzyrobot.omnibus.provider;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.fuzzyrobot.omnibus.core.BusApp;
import org.fuzzyrobot.omnibus.core.ExternalProviderInterface;
import org.fuzzyrobot.omnibus.core.Subscriber;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * User: neil
 * Date: 14/11/2012
 */
public abstract class AsyncProvider<T> implements ExternalProviderInterface<T>, ValueRetriever<T> {
    private static final String TAG = AsyncProvider.class.getSimpleName();

    private boolean needsParams = false;
    private String[] lastParams;
    private T value;
    private RetrieveTask retrieveTask;
    private UpdateTask updateTask;
    private Map<Subscriber<T>, Void> receivers = new HashMap<Subscriber<T>, Void>();

    protected AsyncProvider() {
    }

    protected AsyncProvider(boolean needsParams) {
        this.needsParams = needsParams;
    }

    public synchronized void provide(Context appContext, Subscriber<T> subscriber, String[] params) {
        if (!Arrays.equals(params, lastParams)) {
            value = null;
        }
        if (value != null) {
            subscriber.receive(value);
        }
        if (retrieveTask != null) {
            Log.w(TAG, "Duplicate Subscriber");
        }
        if (!needsParams || params != null) {
            receivers.put(subscriber, null);
            lastParams = params;
            startRetrieveTask();
        } else {
            // waiting for params
            return;
        }
    }

    @Override
    public void invalidate() {
        cancelRetrieveTask();
        value = null;
        startRetrieveTask();
    }

    private void cancelRetrieveTask() {
        if (retrieveTask != null) {
            retrieveTask.cancel(true);
        }
    }

    @Override
    public void update(T newValue) {
        cancelRetrieveTask();
        this.value = newValue;
        startUpdateTask(newValue);
    }

    protected synchronized AsyncTask startRetrieveTask() {
        retrieveTask = new RetrieveTask();
        retrieveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        return retrieveTask;
    }

    protected synchronized AsyncTask startUpdateTask(T newValue) {
        updateTask = new UpdateTask(newValue);
        updateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        return updateTask;
    }

    public abstract T retrieveValue(String[] params) throws Exception;

    public T updateValue(T value) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("AsyncProvider");
        sb.append("{needsParams=").append(needsParams);
        sb.append(", lastParams=").append(lastParams == null ? "null" : Arrays.asList(lastParams).toString());
        sb.append(", value=").append(value);
        sb.append(", task=").append(retrieveTask);
        sb.append(", receivers=").append(receivers);
        sb.append('}');
        return sb.toString();
    }

    private class RetrieveTask extends AsyncTask<Void, Void, T> {

        protected T doInBackground(Void... ignored) {
            try {
                value = retrieveValue(lastParams);
            } catch (Exception e) {
                e.printStackTrace();
                if (BusApp.DEBUG) {
                    throw new RuntimeException(e);
                }
            }
            return value;
        }

        protected void onPostExecute(T value) {
            if (value != null) {
                synchronized (AsyncProvider.this) {
                    for (Subscriber<T> subscriber : receivers.keySet()) {
                        subscriber.receive(value);
                    }
                    receivers.clear();
                    retrieveTask = null;
                }
            }
        }

    }

    private class UpdateTask extends AsyncTask<Void, Void, T> {
        private final T newValue;

        private UpdateTask(T newValue) {
            this.newValue = newValue;
        }

        protected T doInBackground(Void... ignored) {
            try {
                return updateValue(newValue);
            } catch (Exception e) {
                e.printStackTrace();
                if (BusApp.DEBUG) {
                    throw new RuntimeException(e);
                }
            }
            return value;
        }

        protected void onPostExecute(T newValue) {
            value = newValue;
        }

    }
}
