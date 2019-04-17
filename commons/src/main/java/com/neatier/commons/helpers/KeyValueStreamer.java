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

package com.neatier.commons.helpers;

import androidx.annotation.Nullable;
import com.neatier.commons.exception.ErrorBundleException;
import java.util.Map;
import rx.Observable;

/**
 * * Helper abstract class to build and retrieve key value associations which uses {@link Map}
 * interface.
 * @author László Gálosi
 * @since 04/08/15
 */
public interface KeyValueStreamer<K, V> {

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#empty()}
     */
    Observable getOrEmpty(K key);

    /**
     * @return an {@link Observable} emitting the required value if found, or default value.
     */
    Observable getOrJustDefault(K key, V defaultValue);

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    Observable getOrError(K key, ErrorBundleException t);

    /**
     * @return an {@link Observable} emitting the required value if found, or an {@link
     * Observable#error(Throwable)}
     */
    Observable getOrError(K key, @Nullable V errorIfEquals, ErrorBundleException t);

    /**
     * @return an {@link Observable} emitting the stored keys.
     */
    Observable keysAsStream();

    /**
     * @return an {@link Observable} emitting the stored values.
     */
    Observable keysAsListStream();

    /**
     * @return an {@link Observable} emitting a list of stored keys.
     */
    Observable valuesAsStream();

    /**
     * @return an {@link Observable} emitting a list of stored values.
     */
    Observable valuesAsListStream();
}
