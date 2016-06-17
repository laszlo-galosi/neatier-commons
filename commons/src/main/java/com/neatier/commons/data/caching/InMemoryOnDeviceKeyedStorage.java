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

import android.util.SparseArray;
import com.neatier.commons.helpers.KeyValuePairs;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

/**
 * Implementation of  an InMemory {@link OnDeviceKeyedStorage} implementation via {@link
 * SparseArray<Object>}
 * Created by László Gálosi on 24/07/15
 */
@Singleton
public class InMemoryOnDeviceKeyedStorage<K, V> implements OnDeviceKeyedStorage<K, V> {

    private final KeyValuePairs<K, V> inMemoryMap;

    @Inject
    public InMemoryOnDeviceKeyedStorage(final KeyValuePairs<K, V> inMemoryMap) {
        this.inMemoryMap = inMemoryMap;
    }

    @Override
    public void writeKeyedContent(final K key, final V content) {
        inMemoryMap.put(key, content);
    }

    @Override
    public V readOneByKey(final K key) {
        return inMemoryMap.get(key);
    }

    @Override
    public Observable readAll() {
        return inMemoryMap.valuesAsStream();
    }

    @Override
    public void removeOneByKey(final K key) {
        this.inMemoryMap.remove(key);
    }

    @Override
    public boolean containsKey(final K key) {
        return this.inMemoryMap.containsKey(key);
    }

    @Override
    public void clear() {
        inMemoryMap.clear();
    }

    @Override
    public Observable keys() {
        return inMemoryMap.keysAsStream();
    }
}
