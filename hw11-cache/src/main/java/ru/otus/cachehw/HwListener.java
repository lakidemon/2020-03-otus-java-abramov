package ru.otus.cachehw;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author sergey
 * created on 14.12.18.
 */
public interface HwListener<K, V> {
    String REMOVE = "REMOVE";
    String PUT = "PUT";
    String GARBAGE_COLLECTED = "GC";

    void notify(@Nonnull K key, @Nullable V value, String action);
}
