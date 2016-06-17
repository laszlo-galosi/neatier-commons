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

import android.support.annotation.NonNull;
import rx.Observable;

/**
 * Interface for different storage options which stores  a key-content pair on the device.
 * The key is ajn integer f.ex.: an uid, the content is a string f.ex: ajson string.
 * Created by László Gálosi on 24/07/15
 */
public interface OnDeviceKeyedStorage<K, V> {
    /**
     * write operation for a key-value pair
     *
     * @param key     the int key or id of the content
     * @param content the content value
     * @throws if a writing error occurs.
     */
    void writeKeyedContent(K key, @NonNull V content);

    /**
     * @param key the key id
     * @return the stored content identified by the key.
     */
    V readOneByKey(K key);

    /**
     * @return an Observable from all the stored individual contents.
     */
    Observable readAll();

    /**
     * Removes a keyed content from the storage.
     *
     * @param key the key
     * @throws Exception if error occured upon removing.
     */
    void removeOneByKey(K key);

    /**
     * Checks whether the specifed key is exist in the storage.
     *
     * @return true if the key is stored on the device.
     */
    boolean containsKey(K key);

    /**
     * @return a int array to iterate over.
     */
    void clear();

    Observable keys();

    interface FileOnDeviceKeyStorage<K, V> extends OnDeviceKeyedStorage<K, V> {
        Object extractKeyFromFileName(final String fileName);

        Class<K> getKeyClass();

    }
}
