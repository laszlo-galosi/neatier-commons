package com.neatier.data.entity;

import com.google.gson.TypeAdapter;
import com.neatier.commons.data.caching.OnDeviceKeyedStorage;

/**
 * {@link OnDeviceKeyedStorage} interface sub class to use  with {@link TypeAdapter}s of the stored
 * value.
 * @author László Gálosi
 * @since 16/06/16
 */
public interface OnDeviceKeyTypedValueStorage<K, V> extends OnDeviceKeyedStorage<K, V> {

    /**
     * Returns the {@link TypeAdapter} for the value
     */
    TypeAdapter<V> getTypeAdapter();

    /**
     * Returns the class of the key.
     */
    Class<K> getKeyClass();
}
