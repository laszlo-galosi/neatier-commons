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

package com.neatier.repository.datasource;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.neatier.commons.data.caching.OnDeviceKeyedStorage;
import com.neatier.data.entity.Identifiable;
import com.neatier.data.entity.OnDeviceKeyTypedValueStorage;
import java.util.Collection;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

import static rx.Observable.error;
import static rx.Observable.just;

/**
 * A key-value data source, which stores the entries locally via
 * {@link OnDeviceKeyTypedValueStorage}.
 *
 * @author László Gálosi
 * @since 10/06/16
 */
public class AsyncCacheDataSource<K, V extends Identifiable<K>> {

    protected final OnDeviceKeyTypedValueStorage<K, V> onDeviceKeyedStorage;

    public AsyncCacheDataSource(
          final OnDeviceKeyTypedValueStorage<K, V> onDeviceKeyedStorage) {
        this.onDeviceKeyedStorage = onDeviceKeyedStorage;
    }

    /**
     * Returns true if the given value is stored in the data source
     *
     * @see OnDeviceKeyedStorage#containsKey(Object)
     */
    public boolean isValid(final V value) {
        return onDeviceKeyedStorage.containsKey(value.getKey());
    }

    /**
     * Returns an Observable emitting all the stored values or {@link Observable#error(Throwable)}
     * if any Exception occurred.
     */
    @SuppressWarnings("unchecked")
    public Observable getAllAsync() {
        return onDeviceKeyedStorage.keys().flatMap(key -> {
            try {
                V value = onDeviceKeyedStorage.readOneByKey((K) key);
                if (value != null && isValid(value)) {
                    return Observable.just(value);
                } else {
                    return Observable.empty();
                }
            } catch (Exception e) {
                //logRxError();
                return Observable.error(e);
            }
        });
    }

    /**
     * Returns an Observable emitting the value stored by the given key, or if not
     * found an {@link Observable#empty()} or {@link Observable#error(Throwable)} if any
     * Exception occurred.
     */
    public Observable<V> getByKeyAsync(final K key) {
        return just(key).flatMap(k -> {
            try {
                V value = onDeviceKeyedStorage.readOneByKey(k);
                if (value != null && isValid(value)) {
                    return Observable.just(value);
                } else {
                    return Observable.empty();
                }
            } catch (Exception e) {
                return Observable.error(e);
            }
        });
    }

    /**
     * Adds or update the provided value into this data source.
     *
     * @param value The value to be persisted.
     * @return an Observable emitting the value after its addition or update.
     */
    @RxLogObservable
    public Observable<V> addOrUpdateAsync(final V value) {
        return just(value).flatMap(v -> {
            try {
                K key = v.getKey();
                onDeviceKeyedStorage.writeKeyedContent(key, v);
                return getByKeyAsync(key);
            } catch (Exception e) {
                return error(e);
            }
        });
    }

    /**
     * Add or updates all the provided values into this data source.
     *
     * @param values A collection of values to be added or persisted.
     * @return Observable emitting a list of values that has been persisted.
     */
    @RxLogObservable
    public Observable<List<V>> addOrUpdateAllAsync(final Collection<V> values) {
        return Observable.from(values).flatMap(v -> {
            try {
                onDeviceKeyedStorage.writeKeyedContent(v.getKey(), v);
                return Observable.just(v);
            } catch (Exception e) {
                return Observable.error(e);
            }
        }).toList();
    }

    /**
     * Deletes a value given its associated key.
     *
     * @param key The key that uniquely identifies the value to be deleted.
     * @return an {@link Observable} emitting Boolean.TRUE or {@link Observable#error(Throwable)}
     * if any Exception occurred.
     */
    @RxLogObservable
    public rx.Observable<Boolean> deleteByKeyAsync(K key) {
        return just(key).flatMap(k -> {
            try {
                onDeviceKeyedStorage.removeOneByKey(k);
                return just(Boolean.TRUE);
            } catch (Exception e) {
                return error(e);
            }
        });
    }

    /**
     * Delete all the values stored in this data source.
     *
     * @return an {@link Observable} emitting Boolean.TRUE or {@link Observable#error(Throwable)}
     * if any Exception occurred.
     */
    @RxLogObservable
    public Observable<Boolean> deleteAllAsync() {
        return just(Boolean.TRUE)
              .flatMap(r -> {
                  onDeviceKeyedStorage.clear();
                  return just(r);
              });
    }
}
