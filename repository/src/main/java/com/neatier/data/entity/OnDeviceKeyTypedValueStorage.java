package com.neatier.data.entity;

import com.google.gson.TypeAdapter;
import com.neatier.commons.data.caching.OnDeviceKeyedStorage;

/**
 * Created by László Gálosi on 16/06/16
 */
public interface OnDeviceKeyTypedValueStorage<K, V> extends OnDeviceKeyedStorage<K, V> {
    TypeAdapter<V> getTypeAdapter();

    Class<K> getKeyClass();
}
