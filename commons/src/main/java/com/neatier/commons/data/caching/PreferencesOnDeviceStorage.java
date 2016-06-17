/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.commons.data.caching;

/**
 * OnDeviceKeyedStorage implementation using {@link android.content.SharedPreferences}.
 * Created by László Gálosi on 24/07/15
 */

import android.content.Context;
import android.support.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.neatier.commons.helpers.JsonSerializer;
import com.neatier.commons.helpers.SharedKeyValueStore;
import java.util.List;
import rx.Observable;

public abstract class PreferencesOnDeviceStorage<K, V>
        implements OnDeviceKeyedStorage.FileOnDeviceKeyStorage<K, V> {
    protected final String keyPrefix;
    protected final SharedKeyValueStore<K, V> mSharedKeyValueStore;
    protected final JsonSerializer mJsonSerializer;

    public PreferencesOnDeviceStorage(final Context context,
                                      @Nullable final JsonSerializer jsonSerializer,
                                      final String preferenceFileName,
                                      String prefix) {
        this.keyPrefix = prefix;
        this.mSharedKeyValueStore =
                new SharedKeyValueStore<K, V>(context, jsonSerializer, preferenceFileName);
        this.mJsonSerializer = jsonSerializer;
    }

    @Override
    public void writeKeyedContent(final K key, final V content) {
        String keyToStore = String.format("%s%s", this.keyPrefix, key);
        if ((content instanceof JsonElement
                || content instanceof JsonObject
                || content instanceof LinkedTreeMap
                || content instanceof List) && mJsonSerializer != null) {
            mSharedKeyValueStore.put(keyToStore, mJsonSerializer.serialize(content)).apply();
        } else {
            mSharedKeyValueStore.put(keyToStore, content).apply();
        }
    }

    @Override
    public V readOneByKey(final K key) {
        return mSharedKeyValueStore.getOrDefault(getStorableKey(key), null);
    }

    protected K getStorableKey(final K key) {
        Class keyClass = getKeyClass();
        if (keyClass == Long.class) {
            return (K) String.format("%s%d", this.keyPrefix, key);
        } else if (keyClass == Integer.class) {
            return (K) String.format("%s%d", this.keyPrefix, key);
        }
        return (K) String.format("%s%s", this.keyPrefix, key);
    }

    @Override
    public Observable<Object> readAll() {
        return mSharedKeyValueStore.valuesAsStream();
    }

    @Override
    public void removeOneByKey(final K key) {
        mSharedKeyValueStore.remove(String.format("%s%d", this.keyPrefix, key))
                .commit();
    }

    @Override
    public boolean containsKey(final K key) {
        return mSharedKeyValueStore.containsKey(String.format("%s%d", this.keyPrefix, key));
    }

    @Override
    public void clear() {
        mSharedKeyValueStore.clear().commit();
    }

    @Override
    public Observable<Object> keys() {
        return mSharedKeyValueStore.keysAsStream().map(o -> {
            if (getKeyClass() == Long.class) {
                return Long.parseLong(((String) o).substring(keyPrefix.length()));
            } else if (getKeyClass() == Integer.class) {
                return Integer.parseInt(((String) o).substring(keyPrefix.length()));
            } else {
                return (((String) o).substring(keyPrefix.length()));
            }
        });
    }

    @Override
    public Object extractKeyFromFileName(final String fileName) {
        String keyPart = fileName.substring(keyPrefix.length());
        Class keyClass = getKeyClass();
        if (keyClass == Long.class) {
            return Long.parseLong(keyPart);
        } else if (keyClass == Integer.class) {
            return Integer.parseInt(keyPart);
        } else if (keyClass == String.class) {
            return keyPart;
        }
        return keyPrefix;
    }

    @Override
    public abstract Class<K> getKeyClass();
}
