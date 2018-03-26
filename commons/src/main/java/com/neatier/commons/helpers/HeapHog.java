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

package com.neatier.commons.helpers;

/**
 * Interface designating that the implementation class must free up some memory, when it's no
 * longer use.
 *
 * @author László Gálosi
 * @since 26/09/15
 */
public interface HeapHog {
    /**
     * Typical  implementation is a Presenter which holds to some amount of heap by
     * referencing list of objects , {@link #freeUpHeap()} should called
     * when the corresponding lifecycle event (for ex.: destroy()) is happening.
     */
    void freeUpHeap();
}
