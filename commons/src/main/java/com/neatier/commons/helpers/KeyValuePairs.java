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

package com.neatier.commons.helpers;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import com.neatier.commons.exception.ErrorBundleException;
import com.neatier.commons.exception.InternalErrorException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Helper class to build key value pairs which uses {@link Map<K,V>} interface.
 * If {@link Build.VERSION#SDK_INT} is greater then API level 19 {@link Build.VERSION_CODES#KITKAT}
 * internally it uses the more android friendly
 * {@link ArrayMap} then {@link HashMap}
 * Created by László Gálosi on 30/07/15
 */
public class KeyValuePairs<K, V> implements KeyValueStreamer<K, V> {

    final private Map<K, V> internalMap;

    private V mErrorIfAnyIsThisValue;

    public KeyValuePairs() {
        this(5);
    }

    public KeyValuePairs(int capacity) {
        //if (Build.VERSION.SDK_INT >= 19) {
        this.internalMap = new ArrayMap<>(capacity);
        //} else {
        //    this.internalMap = new HashMap<>(capacity);
        //}
    }

    public KeyValuePairs(final Map<K, V> map) {
        this.internalMap = map;
    }

    /**
     * Returns a {@link KeyValuePairs<String, String>} from the specified {@link SharedPreferences}
     *
     * @param preferences the preferences.
     */
    public static KeyValuePairs<String, String> from(final SharedPreferences preferences) {
        final KeyValuePairs<String, String> stringKeyValuePairs = new KeyValuePairs<>();
        Observable.from(preferences.getAll().keySet()).doOnNext(new Action1<String>() {
            @Override
            public void call(final String prefKey) {
                stringKeyValuePairs.put(prefKey, preferences.getString(prefKey, ""));
            }
        }).subscribe();
        return stringKeyValuePairs;
    }

    /**
     * @see Map#put(Object, Object)
     */
    public KeyValuePairs put(final K key, final V value) {
        this.internalMap.put(key, value);
        return this;
    }

    /**
     * Returns a {@link KeyValuePairs<Integer, Object>} from the specified {@link LongSparseArray}
     *
     * @param sparseArray the preferences.
     */
    public static KeyValuePairs<Integer, Object> from(final SparseArray sparseArray) {
        final KeyValuePairs<Integer, Object> keyValuePairs = new KeyValuePairs<>();
        int len = sparseArray.size();
        for (int i = 0; i < len; i++) {
            int key = sparseArray.keyAt(i);
            keyValuePairs.put(key, sparseArray.get(key));
        }
        return keyValuePairs;
    }

    /**
     * Returns a {@link KeyValuePairs<Integer, Object>} from the specified {@link LongSparseArray}
     *
     * @param sparseArray the preferences.
     */
    public static KeyValuePairs<Integer, Boolean> from(final SparseBooleanArray sparseArray) {
        final KeyValuePairs<Integer, Boolean> keyValuePairs = new KeyValuePairs<>();
        int len = sparseArray.size();
        for (int i = 0; i < len; i++) {
            int key = sparseArray.keyAt(i);
            keyValuePairs.put(key, sparseArray.get(key));
        }
        return keyValuePairs;
    }

    /**
     * Returns a {@link KeyValuePairs<Integer, Object>} from the specified {@link LongSparseArray}
     *
     * @param sparseArray the preferences.
     */
    public static KeyValuePairs<Integer, Integer> from(final SparseIntArray sparseArray) {
        final KeyValuePairs<Integer, Integer> keyValuePairs = new KeyValuePairs<>();
        int len = sparseArray.size();
        for (int i = 0; i < len; i++) {
            int key = sparseArray.keyAt(i);
            keyValuePairs.put(key, sparseArray.get(key));
        }
        return keyValuePairs;
    }

    /**
     * Returns a {@link KeyValuePairs<Integer, Object>} from the specified {@link LongSparseArray}
     *
     * @param sparseArray the preferences.
     */
    public static KeyValuePairs<Integer, Long> from(
            final SparseLongArray sparseArray) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            throw new UnsupportedOperationException(
                    String.format("Not supported android version %s < %s", Build.VERSION.SDK_INT,
                            Build.VERSION_CODES.JELLY_BEAN_MR2));
        }
        final KeyValuePairs<Integer, Long> keyValuePairs = new KeyValuePairs<>();
        int len = sparseArray.size();
        for (int i = 0; i < len; i++) {
            int key = sparseArray.keyAt(i);
            keyValuePairs.put(key, sparseArray.get(key));
        }
        return keyValuePairs;
    }

    /**
     * Returns a {@link KeyValuePairs<String, String>} from the specified {@link SharedPreferences}
     *
     * @param sharedKeyValueStore the preferences.
     */
    public Observable<Boolean> fromAsync(final SharedKeyValueStore<String, V> sharedKeyValueStore) {
        return sharedKeyValueStore.keysAsStream().doOnNext(new Action1<String>() {
            @Override
            public void call(final String key) {
                put((K) key, sharedKeyValueStore.get(key));
            }
        }).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(final String key) {
                return Observable.just(Boolean.TRUE);
            }
        }).takeLast(1);
    }

    /**
     * Sets a value which any of the value in this map is equals an error is thrown in {@link
     * #getOrError(Object, ErrorBundleException)} or {@link #getOrThrows(Object,
     * ErrorBundleException)}
     */
    public KeyValuePairs setErrorValue(final V errorIfAnyIsThisValue) {
        mErrorIfAnyIsThisValue = errorIfAnyIsThisValue;
        return this;
    }

    /**
     * Maps only if the value is not null.
     *
     * @see Map#put(Object, Object)
     */
    public KeyValuePairs putChecked(final K key, final V value) {
        if (key != null && value != null) {
            this.internalMap.put(key, value);
        }
        return this;
    }

    /**
     * @see Map#putAll(Map)
     */
    public KeyValuePairs putAll(final Map<K, V> otherMap) {
        this.internalMap.putAll(otherMap);
        return this;
    }

    /**
     * @see Map#putAll(Map)
     */
    public KeyValuePairs copy(final KeyValuePairs<K, V> otherMap) {
        this.internalMap.putAll(otherMap.internalMap);
        return this;
    }

    /**
     * Pairs the keys with the values. The keys and values size must be equal.
     *
     * @param keys   the array containing the keys.
     * @param values the array containing the values.
     * @return this object for api chaining.
     */
    public KeyValuePairs putAll(K[] keys, V[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException(
                    String.format("Keys and values length mismatch: %d <> %d", keys.length,
                            values.length));
        }
        int len = keys.length;
        for (int i = 0; i < len; i++) {
            internalMap.put(keys[i], values[i]);
        }
        return this;
    }

    /**
     * Pairs the keys with the values. The keys and values size must be equal.
     *
     * @param keys   the list containing the keys.
     * @param values the list containing the values.
     * @return this object for api chaining.
     */
    public KeyValuePairs putAll(final List<K> keys, final List<V> values) {
        if (keys.size() != values.size()) {
            throw new IllegalArgumentException(
                    String.format("Keys and values length mismatch: %d <> %d", keys.size(),
                            values.size()));
        }
        int len = keys.size();
        for (int i = 0; i < len; i++) {
            put(keys.get(i), values.get(i));
        }
        return this;
    }

    /**
     * @see Map#size()
     */
    public int size() {
        return internalMap.size();
    }

    /**
     * @see Map#clear()
     */
    public KeyValuePairs clear() {
        this.internalMap.clear();
        return this;
    }

    /**
     * @see Map#containsKey(Object)
     */
    public boolean containsKey(final Object key) {
        return internalMap.containsKey(key);
    }

    /**
     * @see Map#containsValue(Object)
     */
    public boolean containsValue(final Object value) {
        return internalMap.containsValue(value);
    }

    /**
     * @see Map#isEmpty()
     */
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    /**
     * @see Map#remove(Object)
     */
    public KeyValuePairs remove(K key) {
        this.internalMap.remove(key);
        return this;
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#empty()}
     */
    @Override
    public Observable<V> getOrEmpty(K key) {
        if (internalMap.containsKey(key)) {
            return Observable.just(internalMap.get(key));
        }
        return Observable.empty();
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or default value.
     */
    @Override
    public Observable<V> getOrJustDefault(K key, V defaultValue) {
        if (internalMap.containsKey(key)) {
            return Observable.just(internalMap.get(key));
        }
        return Observable.just(defaultValue);
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    @Override
    public Observable<V> getOrError(K key, ErrorBundleException t) {
        return getOrError(key, this.mErrorIfAnyIsThisValue, t);
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    @Override
    public Observable<V> getOrError(K key, V errorIfEquals, ErrorBundleException t) {
        if (internalMap.containsKey(key)) {
            V val = internalMap.get(key);
            if (val.equals(errorIfEquals)) {
                return Observable.error(t);
            }
            return Observable.just(internalMap.get(key));
        }
        return Observable.error(t);
    }

    /**
     * @return an {@link Observable} emitting the stored keys.
     */
    @Override
    public Observable<K> keysAsStream() {
        return Observable.from(keySet());
    }

    /**
     * @return an {@link Observable} emitting the stored values.
     */
    @Override
    public Observable<List<K>> keysAsListStream() {
        return Observable.from(keySet()).toList();
    }

    /**
     * @see Map#keySet()
     */
    @NonNull
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    /**
     * @return an {@link Observable} emitting a list of stored keys.
     */
    @Override
    public Observable<V> valuesAsStream() {
        return Observable.from(values());
    }

    /**
     * @see Map#values()
     */
    @NonNull
    public Collection<V> values() {
        return internalMap.values();
    }

    /**
     * @return an {@link Observable} emitting a list of stored values.
     */
    @Override
    public Observable<List<V>> valuesAsListStream() {
        return Observable.from(values()).toList();
    }

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#empty()}
     */
    public V getOrDefault(K key, V defaultValue) {
        if (internalMap.containsKey(key)) {
            return internalMap.get(key);
        }
        return defaultValue;
    }

    public V getOrDefaultChecked(K key, V defaultValue) {
        if (internalMap.containsKey(key)) {
            V value = internalMap.get(key);
            return value == null ? defaultValue : value;
        }
        return defaultValue;
    }

    public <T> Observable<T> getJsonSerializedAsync(K key, final Class<T> clazz,
                                                    final JsonSerializer<T> jsonSerializer) {
        return getOrError(key, new InternalErrorException(String.format("Cannot find %s", key)))
                .flatMap(new Func1<V, Observable<T>>() {
                    @Override
                    public Observable<T> call(final V value) {
                        return Observable.just(
                                jsonSerializer.deserialize(String.valueOf(value), clazz));
                    }
                });
    }

    /**
     * @return an  the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    public V getOrThrows(K key, ErrorBundleException t) throws ErrorBundleException {
        return getOrThrows(key, this.mErrorIfAnyIsThisValue, t);
    }

    /**
     * @return an  the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    public V getOrThrows(K key, V throwIfEquals, ErrorBundleException t)
            throws ErrorBundleException {
        if (internalMap.containsKey(key)) {
            V val = internalMap.get(key);
            if (val.equals(throwIfEquals)) {
                throw t;
            }
            return internalMap.get(key);
        }
        throw t;
    }

    /**
     * Put all the value-pairs from this to a {@link SharedPreferences} with the correct type.
     * if  the clear parameter is the preferences will not contain any existing key-value pair
     *
     * @param preferences the preferences to write the key-value pairs.
     */
    public void to(final SharedPreferences preferences) {
        keysAsStream().doOnNext(new Action1<K>() {
            @Override
            public void call(final K key) {
                V value = get(key);
                if (value instanceof String) {
                    preferences.edit()
                            .putString(String.valueOf(key), String.valueOf(value)).commit();
                } else if (value instanceof Integer) {
                    preferences.edit().putInt(String.valueOf(key), (Integer) value).commit();
                }
                if (value instanceof Long) {
                    preferences.edit().putLong(String.valueOf(key), (Long) value).commit();
                }
                if (value instanceof Boolean) {
                    preferences.edit().putBoolean(String.valueOf(key), (Boolean) value).commit();
                }
                if (value instanceof Float) {
                    preferences.edit().putFloat(String.valueOf(key), (Float) value).commit();
                }
            }
        }).subscribe();
    }

    /**
     * @see Map#get(Object)
     */
    public V get(final K key) {
        return internalMap.get(key);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeyValuePairs<?, ?> that = (KeyValuePairs<?, ?>) o;

        return !(internalMap != null
                ? !internalMap.equals(that.internalMap)
                : that.internalMap != null);
    }

    @Override
    public int hashCode() {
        return internalMap != null
                ? internalMap.hashCode()
                : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KeyValuePairs");
        sb.append(internalMap);
        return sb.toString();
    }
}
