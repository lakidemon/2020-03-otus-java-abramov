package ru.otus.cachehw;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author sergey
 * created on 14.12.18.
 */
public interface HwCache<K, V> {

    void put(@Nonnull K key, V value);

    void remove(@Nonnull K key);

    @Nullable
    V get(@Nonnull K key);

    void addListener(HwListener<K, V> listener);

    void removeListener(HwListener<K, V> listener);
}
