package ru.otus.cachehw;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author sergey
 * created on 14.12.18.
 */
public interface HwListener<K, V> {
    public static final String REMOVE = "REMOVE";
    public static final String PUT = "PUT";
    public static final String AUTOREMOVE = "AUTOREMOVE";

    void notify(@Nonnull K key, @Nullable V value, String action);
}
