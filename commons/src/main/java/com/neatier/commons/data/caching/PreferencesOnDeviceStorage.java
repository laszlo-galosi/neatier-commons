/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions
  *  Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.commons.data.caching;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;
import com.neatier.commons.helpers.JsonSerializer;
import com.neatier.commons.helpers.SharedKeyValueStore;
import java.util.List;
import rx.Observable;

/**
 * OnDeviceKeyedStorage implementation using {@link android.content.SharedPreferences}.
 *
 * @author László Gálosi
 * @since 24/07/15
 */
public abstract class PreferencesOnDeviceStorage<K, V>
      implements OnDeviceKeyedStorage.FileOnDeviceKeyStorage<K, V> {
    protected final String keyPrefix;
    protected final SharedKeyValueStore<K, V> mSharedKeyValueStore;
    protected final JsonSerializer mJsonSerializer;

    /**
     * Constructor with the given preferences fil name, and key prefix
     *
     * @param jsonSerializer json serializer for content stored
     * @param preferenceFileName the SharedPreferences file name
     * @param prefix key prefix
     * @see #extractKeyFromFileName(String)
     */
    public PreferencesOnDeviceStorage(final Context context,
          @Nullable final JsonSerializer jsonSerializer,
          final String preferenceFileName,
          String prefix) {
        this.keyPrefix = prefix;
        this.mSharedKeyValueStore =
              new SharedKeyValueStore<>(context, jsonSerializer, preferenceFileName);
        this.mJsonSerializer = jsonSerializer;
    }

    @Override
    public void writeKeyedContent(final K key, final V content) {
        String keyToStore = (String) getStoreableKey(key);
        if ((content instanceof JsonElement
              || content instanceof LinkedTreeMap
              || content instanceof List) && mJsonSerializer != null) {
            mSharedKeyValueStore.put(keyToStore, mJsonSerializer.serialize(content)).apply();
        } else {
            mSharedKeyValueStore.put(keyToStore, content).apply();
        }
    }

    @Override
    public V readOneByKey(final K key) {
        return mSharedKeyValueStore.getOrDefault(getStoreableKey(key), null);
    }

    /**
     *
     * Returns a  key with type K from the given key and the key prefix.
     * @see #getKeyClass()
     */
    @SuppressLint("DefaultLocale")
    @SuppressWarnings({ "unchecked", "MalformedFormatString" })
    protected K getStoreableKey(final K key) {
        Class keyClass = getKeyClass();
        if (keyClass == Long.class) {
            return (K) String.format("%s%d", this.keyPrefix, key);
        } else if (keyClass == Integer.class) {
            return (K) String.format("%s%d", this.keyPrefix, key);
        }
        return (K) String.format("%s%s", this.keyPrefix, key);
    }

    @Override
    public Observable readAll() {
        return mSharedKeyValueStore.valuesAsStream();
    }

    @Override
    public void removeOneByKey(final K key) {
        mSharedKeyValueStore.remove((String) getStoreableKey(key))
                            .commit();
    }

    @Override
    public boolean containsKey(final K key) {
        return mSharedKeyValueStore.containsKey(getStoreableKey(key));
    }

    @Override
    public void clear() {
        mSharedKeyValueStore.clear().commit();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Observable keys() {
        return mSharedKeyValueStore.keysAsStream().map(o -> {
            String key = (String) o;
            if (getKeyClass() == Long.class) {
                return Long.parseLong(key.substring(keyPrefix.length()));
            } else if (getKeyClass() == Integer.class) {
                return Integer.parseInt(key.substring(keyPrefix.length()));
            } else {
                return (key.substring(keyPrefix.length()));
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
