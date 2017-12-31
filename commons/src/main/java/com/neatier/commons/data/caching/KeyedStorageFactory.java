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

import android.content.Context;
import com.neatier.commons.helpers.JsonSerializer;
import com.neatier.commons.helpers.KeyValuePairs;
import com.neatier.commons.settings.FactorySettings;
import java.io.File;

/**
 * Creates an {@link OnDeviceKeyedStorage} instance bases on the specified entity class.
 *
 * @author László Gálosi
 * @since 30/07/15
 */
public class KeyedStorageFactory {

    private KeyedStorageFactory() {

    }

    /**
     * Returns the static instance of this class.
     */
    public static KeyedStorageFactory getInstance() {
        return SInstanceHolder.sInstance;
    }

    /**
     * Returns and Creates a {@link OnDeviceKeyedStorage} based on the given storage class and
     * entity class.
     *
     * @param context the context
     * @param entityClass the entity class as the content class
     * @param <T> the sub class of {@link OnDeviceKeyedStorage}
     */
    public <T extends OnDeviceKeyedStorage> T create(final Class<T> storageClass,
          final Context context,
          final Class entityClass) {
        final String fileName = String.format("%s.%s", FactorySettings.PACKAGE_NAME,
                                              entityClass.getSimpleName());
        return create(storageClass, context, fileName, "");
    }

    /**
     * Creates and returns a {@link OnDeviceKeyedStorage.FileOnDeviceKeyStorage} based on the given
     * storage class and
     *
     * @param context the context
     * @param storageClass the storage class to be created.
     * @param fileName the file name of the file storage
     * @param keyPrefix the key prefix for the keys to be stored.
     * @param <T> the sub class of {@link OnDeviceKeyedStorage.FileOnDeviceKeyStorage}
     */
    @SuppressWarnings("unchecked")
    public <T extends OnDeviceKeyedStorage> T create(final Class<T> storageClass,
          final Context context, final String fileName,
          final String keyPrefix) {
        if (storageClass == FileOnDeviceKeyedStorageImpl.class) {
            return (T) new FileOnDeviceKeyedStorageImpl(new File(fileName), keyPrefix) {
                @Override public Class getKeyClass() {
                    return Long.class;
                }
            };
        } else if (storageClass == PreferencesOnDeviceStorage.class) {
            return (T) new PreferencesOnDeviceStorage(context, new JsonSerializer(), fileName,
                                                      keyPrefix) {
                @Override public Class getKeyClass() {
                    return Long.class;
                }
            };
        } else if (storageClass == InMemoryOnDeviceKeyedStorage.class) {
            return (T) new InMemoryOnDeviceKeyedStorage(new KeyValuePairs<Long, String>());
        } else {
            throw new IllegalArgumentException(
                  "Invalid domain class for mapping between " + storageClass.getSimpleName());
        }
    }

    private static class SInstanceHolder {
        private static final KeyedStorageFactory sInstance = new KeyedStorageFactory();
    }
}
