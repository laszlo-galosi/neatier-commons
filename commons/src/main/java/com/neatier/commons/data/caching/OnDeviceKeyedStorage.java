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

import rx.Observable;

/**
 * Interface for different storage options which stores  a key-value pair on the device.
 * The key is type K, the value is type V.
 *
 * @author László Gálosi
 * @since 24/07/15
 */
public interface OnDeviceKeyedStorage<K, V> {
    /**
     * Write operation for a key-value pair
     *
     * @param key the  key with type K
     * @param content the content with type V value
     */
    void writeKeyedContent(K key, V content);

    /**
     * Returns the stored content identified by the key.
     *
     * @param key the key
     */
    V readOneByKey(K key);

    /**
     * Returns an Observable emitting all the stored contents.
     */
    Observable readAll();

    /**
     * Removes a keyed content from the storage.
     *
     * @param key the key
     */
    void removeOneByKey(K key);

    /**
     * Returns true if the given key is exist in the storage.
     */
    boolean containsKey(K key);

    /**
     * Removes all stored keys.
     */
    void clear();

    /**
     * Returns an Observable emitting all the stored keys.
     */
    Observable keys();

    /**
     * OnDeviceKeyedStorage sub interface which stores the key value pairs in a File on the
     * device
     *
     * @param <K> the type of the key
     * @param <V> tje type of the value
     */
    interface FileOnDeviceKeyStorage<K, V> extends OnDeviceKeyedStorage<K, V> {
        /**
         * Returns a key object with type K from the given fileName.
         *
         * @see #getKeyClass()
         */
        Object extractKeyFromFileName(final String fileName);

        /**
         * Returns the class of the keys.
         */
        Class<K> getKeyClass();
    }
}
