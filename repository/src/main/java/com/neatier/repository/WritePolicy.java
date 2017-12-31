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
 * /**
 * Enum class for defining Write policies for data
 * {@link AsyncDataSources.WriteableAsyncDataSource}s
 *
 * @author László Gálosi
 * @since 13/06/16
 */
public enum WritePolicy {
    /**
     * Write into only one data source.
     */
    WRITE_ONCE,

    /**
     * Write into all available data source.
     */
    WRITE_ALL
}
