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

package com.neatier.widgets.renderers;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Interface for rendering a data with type T.
 * @author László Gálosi
 * @since 29/04/16
 */
public interface Renderable<T> {
    /**
     * Renders the given data item onto the given view.
     * @param view the view on the data should be displayed
     * @param dataItem the data item with type T.
     */
    void render(final View view, @NonNull T dataItem);
}
