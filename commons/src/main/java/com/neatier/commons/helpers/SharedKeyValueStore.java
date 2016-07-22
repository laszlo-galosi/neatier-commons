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

package com.neatier.commons.helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neatier.commons.exception.ErrorBundleException;
import java.util.Map;
import java.util.Set;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Helper class to build key value pairs which uses {@link SharedPreferences} internally.
 * You must explicitly call {@link #apply()} or {@link #commit()} jusr like in the case of {@link
 * SharedPreferences.Editor}
 * Created by László Gálosi on 04/08/15
 */

public class SharedKeyValueStore<K, V> implements KeyValueStreamer<K, V>, SharedPreferences.Editor {

    private final SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mPrefEditor;
    private JsonSerializer mJsonSerializer;

    public SharedKeyValueStore(final Context context, final String preferencesFileName) {
        if (context == null || TextUtils.isEmpty(preferencesFileName)) {
            throw new IllegalArgumentException("The constructor parameters cannot be null!!!");
        }
        mSharedPreferences =
              context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
        mPrefEditor = this.mSharedPreferences.edit();
        mJsonSerializer = new JsonSerializer();
    }

    public SharedKeyValueStore(final Context context, final JsonSerializer jsonSerializer,
          final String preferencesFileName) {
        if (context == null || TextUtils.isEmpty(preferencesFileName)) {
            throw new IllegalArgumentException("The constructor parameters cannot be null!!!");
        }
        mSharedPreferences =
              context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
        mPrefEditor = this.mSharedPreferences.edit();
        mJsonSerializer = jsonSerializer;
    }

    /**
     * @see Map#putAll(Map)
     */
    public SharedKeyValueStore putAll(final Map<String, V> otherMap) {
        for (Map.Entry<String, V> entry : otherMap.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * @see Map#put(Object, Object)
     */
    public SharedKeyValueStore put(final String key, final Object value) {
        if (value instanceof String) {
            this.putString(String.valueOf(key), String.valueOf(value));
        } else if (value instanceof Set) {
            this.putStringSet(String.valueOf(key), (Set<String>) value);
        } else if (value instanceof Integer) {
            this.putInt(String.valueOf(key), ((Integer) value).intValue());
        } else if (value instanceof Long) {
            this.putLong(String.valueOf(key), ((Long) value).longValue());
        } else if (value instanceof Float) {
            this.putFloat(String.valueOf(key), ((Float) value).floatValue());
        } else if (value instanceof Boolean) {
            this.putBoolean(String.valueOf(key), ((Boolean) value).booleanValue());
        } else {
            this.putString(String.valueOf(key), mJsonSerializer.serialize(value, value.getClass()));
        }
        return this;
    }

    public <T> T getAsOrDefault(final K key, Class<T> returnClass, T defaultValue) {
        if (returnClass == String.class && containsKey(key)) {
            return (T) get(key);
        } else if (returnClass == JsonArray.class && containsKey(key)) {
            return (T) mJsonSerializer.deserialize((String) this.get(key), returnClass);
        } else if (returnClass == JsonObject.class && containsKey(key)) {
            return (T) mJsonSerializer.deserialize((String) this.get(key));
        } else if (containsKey(key) && mJsonSerializer.isJsonObject(get(key))) {
            return (T) mJsonSerializer.deserialize((String) this.get(key), returnClass);
        }
        return containsKey(key) ? (T) get(key) : defaultValue;
    }

    @Override
    public SharedPreferences.Editor putString(final java.lang.String key,
          final java.lang.String value) {
        return this.mPrefEditor.putString(key, value);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public SharedPreferences.Editor putStringSet(final java.lang.String key,
          final Set<java.lang.String> values) {
        return this.mPrefEditor.putStringSet(key, values);
    }

    @Override
    public SharedPreferences.Editor putInt(final java.lang.String key, final int value) {
        return this.mPrefEditor.putInt(key, value);
    }

    @Override
    public SharedPreferences.Editor putLong(final java.lang.String key, final long value) {
        this.mPrefEditor.putLong(key, value);
        return mPrefEditor;
    }

    @Override
    public SharedPreferences.Editor putFloat(final java.lang.String key, final float value) {
        return this.mPrefEditor.putFloat(key, value);
    }

    @Override
    public SharedPreferences.Editor putBoolean(final java.lang.String key, final boolean value) {
        return this.mPrefEditor.putBoolean(key, value);
    }

    @Override
    public SharedPreferences.Editor remove(final java.lang.String key) {
        return this.mPrefEditor.remove(key);
    }

    @Override
    public SharedPreferences.Editor clear() {
        return mPrefEditor.clear();
    }

    @Override
    public boolean commit() {
        return this.mPrefEditor.commit();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void apply() {
        this.mPrefEditor.apply();
    }

    /**
     * @see Map#size()
     */
    public int size() {
        return mSharedPreferences.getAll().size();
    }

    /**
     * @see Map#isEmpty()
     */
    public boolean isEmpty() {
        return mSharedPreferences.getAll().keySet().isEmpty();
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#empty()}
     */
    @Override
    public Observable getOrEmpty(final K key) {
        if (containsKey(key)) {
            return Observable.just(mSharedPreferences.getAll().get(key));
        }
        return Observable.empty();
    }

    /**
     * @see Map#containsKey(Object)
     */
    public boolean containsKey(final Object key) {
        return mSharedPreferences.getAll().containsKey(key);
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or default value.
     */
    @Override
    public Observable getOrJustDefault(final K key, final V defaultValue) {
        if (containsKey(key)) {
            return Observable.just(mSharedPreferences.getAll().get(key));
        }
        return Observable.just(defaultValue);
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    @Override
    public Observable<V> getOrError(final K key, final ErrorBundleException t) {
        if (mSharedPreferences.getAll().containsKey(key)) {
            return Observable.just((V) mSharedPreferences.getAll().get(key));
        }
        return Observable.error(t);
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    @Override
    public Observable<V> getOrError(final K key, final V errorIfEquals,
          ErrorBundleException t) {
        if (containsKey(key)) {
            V val = (V) mSharedPreferences.getAll().get(key);
            if (val.equals(errorIfEquals)) {
                return Observable.error(t);
            }
            return Observable.just(val);
        }
        return Observable.error(t);
    }

    /**
     * @return an {@link Observable} emitting the stored keys.
     */
    @Override
    public Observable keysAsStream() {
        return Observable.from(keySet());
    }

    /**
     * @see Map#keySet()
     */
    @NonNull
    public Set<String> keySet() {
        return mSharedPreferences.getAll().keySet();
    }

    /**
     * @return an {@link Observable} emitting the stored values.
     */
    @Override
    public Observable keysAsListStream() {
        return Observable.from(keySet()).toList();
    }

    /**
     * @return an {@link Observable} emitting a list of stored keys.
     */
    @Override
    public Observable valuesAsStream() {
        return Observable.from(mSharedPreferences.getAll().values());
    }

    @Override
    public Observable valuesAsListStream() {
        return valuesAsStream().toList();
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#empty()}
     */
    public V get(final K key) {
        return (V) mSharedPreferences.getAll().get(key);
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#empty()}
     */
    public V getOrDefault(final K key, final V defaultValue) {
        if (containsKey(key)) {
            return (V) mSharedPreferences.getAll().get(key);
        }
        return defaultValue;
    }

    /**
     * @return an  the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    public V getOrThrows(final K key, ErrorBundleException t) throws ErrorBundleException {
        if (containsKey(key)) {
            V val = (V) mSharedPreferences.getAll().get(key);
            return val;
        }
        throw t;
    }

    /**
     * @return an  the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    public V getOrThrows(final K key, final V throwIfEquals, ErrorBundleException t)
          throws ErrorBundleException {
        if (containsKey(key)) {
            V val = (V) mSharedPreferences.getAll().get(key);
            if (val.equals(throwIfEquals)) {
                throw t;
            }
            return val;
        }
        throw t;
    }

    /**
     * Returns an observable to remove the specified key asynchronous
     *
     * @return {@link Observable} emitting the
     */
    public Observable<Boolean> putAsync(final K key, final V value) {
        return Observable.just(true).doOnNext(new Action1<Boolean>() {
            @Override
            public void call(final Boolean aBoolean) {
                put((String) key, value).commit();
            }
        });
    }

    /**
     * Returns an observable to remove the specified key asynchronous
     *
     * @param keys multiple keys to remove
     * @return {@link Observable<Boolean>} or {@link Observable#empty()}  if the key not found.
     */
    public Observable<Boolean> removeAsync(final K... keys) {
        return Observable.from(keys).flatMap(new Func1<K, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(final K k) {
                remove(String.valueOf(k)).commit();
                return Observable.just(Boolean.TRUE);
            }
        });
    }

    /**
     * Returns an observable to remove the specified key asynchronous
     *
     * @return {@link Observable} emitting the
     */
    public Observable<Boolean> clearAsync() {
        return Observable.just(true).doOnNext(new Action1<Boolean>() {
            @Override
            public void call(final Boolean k) {
                clear().commit();
            }
        });
    }

    /**
     * Returns an observable to remove the specified key asynchronous
     *
     * @return {@link Observable} emitting the
     */
    public Observable<Boolean> commitAsync() {
        return Observable.just(true).doOnNext(new Action1<Boolean>() {
            @Override
            public void call(final Boolean k) {
                commit();
            }
        });
    }

    public JsonSerializer getJsonSerializer() {
        return mJsonSerializer;
    }
}
