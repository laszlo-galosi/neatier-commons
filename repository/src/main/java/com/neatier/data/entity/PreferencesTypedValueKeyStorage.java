package com.neatier.data.entity;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.neatier.commons.data.caching.PreferencesOnDeviceStorage;
import com.neatier.commons.helpers.JsonSerializer;
import java.io.IOException;
import rx.Observable;

/**
 * A {@link PreferencesOnDeviceStorage} abstract class implementing the {@link OnDeviceKeyTypedValueStorage}
 * interface for using {@link TypeAdapter} when storing the values.
 * @author László Gálosi
 * @since 16/06/16
 */
public abstract class PreferencesTypedValueKeyStorage<K, V> extends PreferencesOnDeviceStorage<K, V>
      implements OnDeviceKeyTypedValueStorage<K, V> {

    final Gson mGson;

    public PreferencesTypedValueKeyStorage(final Context context,
          final String preferenceFileName, final String prefix, final Gson gson) {
        super(context, new JsonSerializer(gson, new JsonParser()), preferenceFileName, prefix);
        mGson = gson;
    }

    @Override public void writeKeyedContent(final K key, final V content) {
        mSharedKeyValueStore.put((String) getStoreableKey(key),
                                 getTypeAdapter().toJson(content)).commit();
    }

    @Override public V readOneByKey(final K key) {
        V emptyValue = (V) "{}";
        Object result = mSharedKeyValueStore.getOrDefault(getStoreableKey(key), emptyValue);
        try {
            return getTypeAdapter().fromJson((String) result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public Observable<Object> keys() {
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

    @Override public abstract TypeAdapter<V> getTypeAdapter();

    @Override public abstract Class<K> getKeyClass();
}
