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

import com.neatier.commons.helpers.KeyValuePairs;
import com.neatier.data.entity.Identifiable;
import java.util.Collection;
import java.util.List;
import rx.Observable;

/**
 * Created by László Gálosi on 10/06/16
 */
public interface AsyncDataSources {

    interface WriteableAsyncDataSource<K, V extends Identifiable<K>> {
        /**
         * Adds or update the provided value into this data source.
         *
         * @param value The value to be persisted.
         * @return The value after its addition or update.
         * @throws Exception any exception on the data source.
         */
        Observable<V> addOrUpdate(V value);

        /**
         * Add or updates all the provided values into this data source.
         *
         * @param values A collection of values to be added or persisted.
         * @return The values that has been persisted.
         * @throws Exception any exception on the data source.
         */
        Observable<List<V>> addOrUpdateAll(Collection<V> values,
              final KeyValuePairs<String, Object> requestParams);

        /**
         * Deletes a value given its associated key.
         *
         * @param key The key that uniquely identifies the value to be deleted.
         * @throws Exception any exception on the data source.
         */
        Observable<Boolean> deleteByKey(K key, final KeyValuePairs<String, Object> requestParams);

        /**
         * Delete all the values stored in this data source.
         *
         * @throws Exception any exception on the data source.
         */
        Observable<Boolean> deleteAll(final KeyValuePairs<String, Object> requestParams);
    }

    interface ReadableAsyncDataSource<K, V extends Identifiable<K>> {

        Observable<V> getByKey(K key, final KeyValuePairs<String, Object> requestParams);

        /**
         * Returns all the values available in the data source or null if the operation does not
         * make
         * sense in the context of the data source.
         *
         * @return A collection of values or null if the operation is not implemented by this data
         * source.
         * @throws Exception any exception on the data source.
         */
        Observable<List<V>> getAll(final KeyValuePairs<String, Object> requestParams);
    }
}
