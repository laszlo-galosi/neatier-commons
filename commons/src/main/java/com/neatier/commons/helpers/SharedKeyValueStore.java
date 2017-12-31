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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neatier.commons.exception.ErrorBundleException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Helper class to build key value pairs which uses {@link SharedPreferences} internally.
 * You must explicitly call {@link #apply()} or {@link #commit()} just like in the case of {@link
 * SharedPreferences.Editor}
 *
 * @author László Gálosi
 * @since 04/08/15
 */
public class SharedKeyValueStore<K, V> implements KeyValueStreamer<K, V>, SharedPreferences.Editor {

    /**
     * The internal shared preferences to store key-value pairs locally.óüpopö
     */
    private final SharedPreferences mSharedPreferences;

    /**
     * SharedPreferences editor to put and remove key-values.
     */
    private final SharedPreferences.Editor mPrefEditor;

    /**
     * Json serializer for serializing {@link V} objects
     */
    private JsonSerializer mJsonSerializer;

    /**
     * Static factory method for creating new instance with the gives shared preference file name.
     */
    public static SharedKeyValueStore newInstance(final String preferencesFileName,
          final Context context) {
        return new SharedKeyValueStore(context, preferencesFileName);
    }

    /**
     * Constructor
     *
     * @param context the application context
     * @param preferencesFileName preference file name
     * @see Context#getSharedPreferences(String, int)
     */
    @SuppressLint("CommitPrefEdits") public SharedKeyValueStore(final Context context,
          final String preferencesFileName) {
        if (context == null || TextUtils.isEmpty(preferencesFileName)) {
            throw new IllegalArgumentException("The constructor parameters cannot be null!!!");
        }
        mSharedPreferences =
              context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
        mPrefEditor = this.mSharedPreferences.edit();
        mJsonSerializer = new JsonSerializer();
    }

    /**
     * Constructor with the given preference file name an {@link JsonSerializer}
     *
     * @param context - the application context.
     * @param jsonSerializer - the json serializer used by {@link #getAsOrDefault(Object, Class,
     * Object)}
     * @param preferencesFileName the preference file name
     */
    @SuppressLint("CommitPrefEdits") public SharedKeyValueStore(final Context context,
          final JsonSerializer jsonSerializer,
          final String preferencesFileName) {
        if (context == null || TextUtils.isEmpty(preferencesFileName)) {
            throw new IllegalArgumentException("The constructor parameters cannot be null!!!");
        }
        mSharedPreferences =
              context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE);
        mPrefEditor = this.mSharedPreferences.edit();
        mJsonSerializer = jsonSerializer;
    }

    @Override
    public Observable getOrEmpty(final K key) {
        if (containsKey(key)) {
            return Observable.just(mSharedPreferences.getAll().get(key));
        }
        return Observable.empty();
    }

    @Override
    public Observable getOrJustDefault(final K key, final V defaultValue) {
        if (containsKey(key)) {
            return Observable.just(mSharedPreferences.getAll().get(key));
        }
        return Observable.just(defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Observable<V> getOrError(final K key, final ErrorBundleException t) {
        if (mSharedPreferences.getAll().containsKey(key)) {
            return Observable.just((V) mSharedPreferences.getAll().get(key));
        }
        return Observable.error(t);
    }

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

    @Override
    public Observable keysAsStream() {
        return Observable.from(keySet());
    }

    @Override
    public Observable keysAsListStream() {
        return Observable.from(keySet()).toList();
    }

    @NonNull
    public Set<String> keySet() {
        return mSharedPreferences.getAll().keySet();
    }

    @Override
    public Observable valuesAsStream() {
        return Observable.from(mSharedPreferences.getAll().values());
    }

    @Override
    public Observable valuesAsListStream() {
        return valuesAsStream().toList();
    }

    /**
     * @see Map#containsKey(Object)
     */
    public boolean containsKey(final Object key) {
        return mSharedPreferences.getAll().containsKey(key);
    }

    /**
     * Put all the {@link Map.Entry}'s in the given map into the internal {@link SharedPreferences}
     *
     * @see Map#putAll(Map)
     */
    public SharedKeyValueStore putAll(final Map<String, V> otherMap) {
        for (Map.Entry<String, V> entry : otherMap.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Stores the given value with the given key in the internal {@link SharedPreferences}.
     * The value will be stored by calling the appropriate putX method of the {@link
     * SharedPreferences} by examining the value class. If the object is a {@link Serializable}
     * object it will be serialized with the  {@link JsonSerializer} of this SharedKeyValueStore.
     *
     * @param key the key of the preference
     * @param value object
     * @see JsonSerializer#serialize(Object, Class)
     * @see SharedPreferences.Editor#putString(String, String)
     * @see SharedPreferences.Editor#putInt(String, int) (String, String)
     * @see SharedPreferences.Editor#putBoolean(String, boolean)
     * @see SharedPreferences.Editor#putFloat(String, float) (String, boolean)
     */
    @SuppressWarnings("unchecked")
    public SharedKeyValueStore put(final String key, final Object value) {
        if (value instanceof String) {
            this.putString(String.valueOf(key), String.valueOf(value));
        } else if (value instanceof Set) {
            this.putStringSet(String.valueOf(key), (Set<String>) value);
        } else if (value instanceof Integer) {
            this.putInt(String.valueOf(key), (Integer) value);
        } else if (value instanceof Long) {
            this.putLong(String.valueOf(key), (Long) value);
        } else if (value instanceof Float) {
            this.putFloat(String.valueOf(key), (Float) value);
        } else if (value instanceof Boolean) {
            this.putBoolean(String.valueOf(key), (Boolean) value);
        } else {
            this.putString(String.valueOf(key), mJsonSerializer.serialize(value, value.getClass()));
        }
        return this;
    }

    /**
     * @see SharedPreferences.Editor#putString(String, String)
     */
    @Override
    public SharedPreferences.Editor putString(final String key,
          final String value) {
        return this.mPrefEditor.putString(key, value);
    }

    /**
     * @see SharedPreferences.Editor#putStringSet(String, Set)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public SharedPreferences.Editor putStringSet(final String key,
          final Set<String> values) {
        return this.mPrefEditor.putStringSet(key, values);
    }

    /**
     * @see SharedPreferences.Editor#putInt(String, int)
     */

    @Override
    public SharedPreferences.Editor putInt(final String key, final int value) {
        return this.mPrefEditor.putInt(key, value);
    }

    /**
     * @see SharedPreferences.Editor#putLong(String, long)
     */

    @Override
    public SharedPreferences.Editor putLong(final String key, final long value) {
        this.mPrefEditor.putLong(key, value);
        return mPrefEditor;
    }

    /**
     * @see SharedPreferences.Editor#putFloat(String, float)
     */
    @Override
    public SharedPreferences.Editor putFloat(final String key, final float value) {
        return this.mPrefEditor.putFloat(key, value);
    }

    /**
     * @see SharedPreferences.Editor#putBoolean(String, boolean)
     */
    @Override
    public SharedPreferences.Editor putBoolean(final String key, final boolean value) {
        return this.mPrefEditor.putBoolean(key, value);
    }

    /**
     * @see SharedPreferences.Editor#remove(String)
     */
    @Override
    public SharedPreferences.Editor remove(final java.lang.String key) {
        return this.mPrefEditor.remove(key);
    }

    /**
     * @see SharedPreferences.Editor#clear()
     */
    @Override
    public SharedPreferences.Editor clear() {
        return mPrefEditor.clear();
    }

    /**
     * @see SharedPreferences.Editor#commit()
     */
    @Override
    public boolean commit() {
        return this.mPrefEditor.commit();
    }

    /**
     * @see SharedPreferences.Editor#apply()
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void apply() {
        this.mPrefEditor.apply();
    }

    /**
     * Returns the value casted to the given T type class or the given default value if not found.
     *
     * @param key the preference key
     * @param returnClass the class of the expected value to be casted.
     * @param defaultValue the fallback value to be returned if  not found.
     * @param <T> the type of the expected value.
     */
    @SuppressWarnings("unchecked")
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

    /**
     * Returns a value of the stored key-value pair if found, or null otherwise.
     */
    @Nullable public V get(final K key) {
        return (V) mSharedPreferences.getAll().get(key);
    }

    /**
     * Returns the size of the stored key-value pairs stored in the {@link SharedPreferences}
     *
     * @see Map#size()
     */
    public int size() {
        return mSharedPreferences.getAll().size();
    }

    /**
     * Returns a value of the stored key-value pair if found, or the given default value otherwise.
     */
    public V getOrDefault(final K key, final V defaultValue) {
        if (containsKey(key)) {
            return (V) mSharedPreferences.getAll().get(key);
        }
        return defaultValue;
    }

    /**
     * Returns a value of the stored key-value pair if found, or throws {@link ErrorBundleException}
     * if not found.
     */
    public V getOrThrows(final K key, ErrorBundleException t) throws ErrorBundleException {
        if (containsKey(key)) {
            return (V) mSharedPreferences.getAll().get(key);
        }
        throw t;
    }

    /**
     * Returns a value of the stored key-value pair if found, or throws {@link ErrorBundleException}
     * Also throws if the value is found but it's equal with the given object
     *
     * @param key the preference key
     * @param throwIfEquals the stored value examined with this value, and if it equals the given
     * exception will be thrown.
     * @param t exception should be thrown when the stored value equals the given expected value.
     * @throws ErrorBundleException if the stored value not found or its value is equal to the
     * given
     * expected value.
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
        return Observable.just(true).doOnNext(aBoolean -> put((String) key, value).commit());
    }

    /**
     * Returns an observable to remove the specified key asynchronous
     *
     * @param keys multiple keys to remove
     * @return {@link Observable<Boolean>} or {@link Observable#empty()}  if the key not found.
     */
    public Observable<Boolean> removeAsync(final K... keys) {
        return Observable.from(keys).flatMap(k -> {
            remove(String.valueOf(k)).commit();
            return Observable.just(Boolean.TRUE);
        });
    }

    /**
     * Returns an observable to remove the specified key asynchronous
     *
     * @return {@link Observable} emitting the
     */
    public Observable<Boolean> clearAsync() {
        return Observable.just(true).doOnNext(k -> clear().commit());
    }

    /**
     * Returns an observable to remove the specified key asynchronous
     *
     * @return {@link Observable} emitting the
     */
    public Observable<Boolean> commitAsync() {
        return Observable.just(true).doOnNext(k -> commit());
    }

    /**
     * Returns true if the internal {@link SharedPreferences} contains no key-value pairs.
     *
     * @see Map#isEmpty()
     */
    public boolean isEmpty() {
        return mSharedPreferences.getAll().keySet().isEmpty();
    }

    /**
     * Returns the serializer which will be used when storing key value pairs within the internal
     * {@link SharedKeyValueStore}.
     *
     * @see #put(String, Object)
     * @see #getAsOrDefault(Object, Class, Object)
     */
    public JsonSerializer getJsonSerializer() {
        return mJsonSerializer;
    }
}
