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

import android.util.SparseArray;
import android.widget.RadioButton;
import android.widget.Spinner;

/**
 * Interface defining a widget with multiple selectable options.
 * Options has an id of type K, and a value of type V.
 *
 * @author László Gálosi
 * @see Spinner
 * @see RadioButton
 * @since 29/11/16
 */
public interface HasChoiceValue<K, V> {
    /**
     * Returns the array of selectable option id-s with type K.
     */
    SparseArray<K> choiceIds();

    /**
     * Returns the array of selectable option values with type V.
     */
    SparseArray<V> choiceValues();

    /**
     * Returns the string array of selectable option names
     */
    SparseArray<String> choiceNames();

    /**
     * Returns the option value by the given key.
     */
    V valueByKey(K key);

    /**
     * Returns the option name by the given key.
     */
    String nameByKey(K key);

    /**
     * Returns the option index  by the given key.
     */
    int indexByKey(K key);

    /**
     * Returns the option's index in the options array by the given value.
     */
    int indexByValue(V value);

    /**
     * Returns the id of the option by the given index in the options array.
     */
    K keyAt(int index);
}
