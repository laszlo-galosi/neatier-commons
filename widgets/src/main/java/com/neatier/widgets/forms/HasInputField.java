/*
 * Copyright (C) 2017 Extremenet Ltd., All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *  All information contained herein is, and remains the property of Extremenet Ltd.
 *  The intellectual and technical concepts contained herein are proprietary to Extremenet Ltd.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Extremenet Ltd.
 *
 */

package com.neatier.widgets.forms;

import android.support.annotation.ColorRes;
import android.view.View;
import com.neatier.widgets.helpers.WidgetUtils;

/**
 * Interface for custom widgets which has a unique identifier with type K, has string label and
 * holds a value with type T. It can show helper or error text within its layout.
 *
 * @author László Gálosi
 * @since 29/11/16
 */
public interface HasInputField<K, V> {

    /**
     * Returns the unique identifier with type K
     */
    K getKey();

    /**
     * Returns the value with type V.
     */
    V getValue();

    /**
     * Set the value of this widget to the given value.
     */
    void setValue(V value);

    /**
     * Set the label text to the given value.
     */
    void setLabel(String text);

    /**
     * Returns the label text of this widget.
     */
    String getLabel();

    /**
     * Set the helper text to the given text data, with the given color.
     */
    void setHelper(String textData, @ColorRes int color);

    /**
     * Set the visibility of the helper text view to the given value.
     *
     * @see WidgetUtils#setVisibilityOf(View, boolean)
     */
    void showHideHelper(boolean visible);

    /**
     * Returns the helper text of this widget.
     */
    String getHelper();

    /**
     * Returns the view id of the View which displays the label of this widget.
     */
    int getLabelViewId();

    /**
     * Returns the view id of the View which displays the helper text of this widget.
     */
    int getHelperViewId();
}
