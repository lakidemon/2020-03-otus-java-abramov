package ru.otus.cachehw;

import com.google.common.collect.MapMaker;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author sergey
 * created on 14.12.18.
 */
@Slf4j
public class MyCache<K, V> implements HwCache<K, V> {
    private final Map<K, V> map = new MapMaker().weakValues().makeMap();
    private final List<SoftReference<HwListener<K, V>>> listeners = new ArrayList<>();
    private final List<KeyedPhantomReference> valueReferences = new CopyOnWriteArrayList<>();
    private final ReferenceQueue<V> valueQueue = new ReferenceQueue<>();
    @Getter
    private final Thread watcherThread = new Thread(this::checkQueue);
    private volatile boolean doWatch = true;

    public MyCache() {
        startQueueWatcher();
    }

    @Override
    public void put(@NonNull K key, V value) {
        map.put(key, value);
        valueReferences.add(new KeyedPhantomReference(key, value, valueQueue));
        notifyListeners(key, value, HwListener.PUT);
    }

    @Override
    public void remove(@NonNull K key) {
        Optional.ofNullable(map.remove(key)).ifPresent(removed -> notifyListeners(key, removed, HwListener.REMOVE));
    }

    @Override
    public V get(@NonNull K key) {
        return map.get(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(new SoftReference<>(listener));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.removeIf(ref -> listener == ref.get());
    }

    public void stopWatch() {
        doWatch = false;
        watcherThread.interrupt();
    }

    private void notifyListeners(K key, V value, String action) {
        var it = listeners.iterator();
        while (it.hasNext()) {
            var listener = it.next().get();
            if (listener != null) {
                notifyListener(listener, key, value, action);
            } else {
                it.remove();
            }
        }
    }

    private void notifyListener(HwListener<K, V> listener, K key, V value, String action) {
        try {
            listener.notify(key, value, action);
        } catch (Exception e) {
            log.error(String.format("Encountered exception during listener notification! Action: %s; K: %s; V: %s",
                    action, key, value), e);
        }
    }

    private void startQueueWatcher() {
        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    private void checkQueue() {
        while (doWatch) {
            try {
                var ref = (KeyedPhantomReference) valueQueue.remove();
                notifyListeners(ref.key, null, HwListener.GARBAGE_COLLECTED);
                valueReferences.remove(ref);
                ref.key = null;
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private class KeyedPhantomReference extends PhantomReference<V> {
        private K key;

        public KeyedPhantomReference(K key, V referent, ReferenceQueue<? super V> q) {
            super(referent, q);
            this.key = key;
        }
    }

}
