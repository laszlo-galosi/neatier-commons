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

package com.neatier.widgets;

import com.fernandocejas.arrow.optional.Optional;

/**
 * Interface for binding a data item with Type T.
 *
 * @author László Gálosi
 * @since 18/03/16
 */
public interface Bindable<T> {
    /**
     * Binds the given data item with type T.
     */
    void bind(final T dataItem);

    /**
     * Returns an Optional of data with type T.
     */
    Optional<T> getData();
}
