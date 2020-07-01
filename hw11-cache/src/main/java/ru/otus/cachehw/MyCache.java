package ru.otus.cachehw;

import lombok.extern.slf4j.Slf4j;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * @author sergey
 * created on 14.12.18.
 */
@Slf4j
public class MyCache<K, V> implements HwCache<K, V> {
    private WeakHashMap<K, V> map = new WeakHashMap<>();
    private List<SoftReference<HwListener<K, V>>> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        map.put(key, value);
        notifyListeners(key, value, HwListener.PUT);
    }

    @Override
    public void remove(K key) {
        Optional.ofNullable(map.remove(key)).ifPresent(v -> notifyListeners(key, v, HwListener.REMOVE));
    }

    @Override
    public V get(K key) {
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

    private void notifyListeners(K key, V value, String action) {
        var it = listeners.iterator();
        while (it.hasNext()) {
            Optional.ofNullable(it.next().get())
                    .ifPresentOrElse(l -> notifyListener(l, key, value, action), it::remove);
        }
    }

    private void notifyListener(HwListener<K, V> listener, K key, V value, String action) {
        try {
            listener.notify(key, value, action);
        } catch (Exception e) {
            log.error(String.format("Encountered exception during listener notification! Action: %s; K: %s; V: %s",
                    action, String.valueOf(key), String.valueOf(value)), e);
        }
    }
}
