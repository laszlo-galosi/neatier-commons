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

package com.neatier.repository;

import com.neatier.repository.datasource.AsyncDataSources;

/**
 * Enum class for defining Read policies for data {@link AsyncDataSources}
 * @author László Gálosi
 * @since 13/06/16
 */
public enum ReadPolicy {
    /**
     * Lookup entry only from cache.
     */
    CACHE_ONLY,

    /**
     * Lookup entry only from {@link AsyncDataSources.ReadableAsyncDataSource}.
     */
    READABLE_ONLY,

    /**
     * Lookup any data sources.
     */
    READ_ALL;

    public boolean useCache() {
        return this == CACHE_ONLY || this == READ_ALL;
    }

    public boolean useReadable() {
        return this == READABLE_ONLY || this == READ_ALL;
    }
}
