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

import android.content.Context;
import com.neatier.commons.helpers.JsonSerializer;
import com.neatier.commons.helpers.KeyValuePairs;
import com.neatier.commons.settings.FactorySettings;
import java.io.File;

/**
 * Created by László Gálosi on 30/07/15
 */
public class KeyedStorageFactory {

    private KeyedStorageFactory() {

    }

    public static KeyedStorageFactory getInstance() {
        return SInstanceHolder.sInstance;
    }

    public <T> T create(final Class<T> storageClass, final Context context,
                        final Class entityClass) {
        final String fileName = String.format("%s.%s", FactorySettings.PACKAGE_NAME,
                entityClass.getSimpleName());
        return create(storageClass, context, fileName, "");
    }

    public <T> T create(final Class<T> storageClass, final Context context, final String fileName,
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
