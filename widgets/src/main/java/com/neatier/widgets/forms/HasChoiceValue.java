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

/**
 * Created by László Gálosi on 29/11/16
 */
public interface HasChoiceValue<K, V> {
    SparseArray<K> choiceIds();

    SparseArray<V> choiceValues();

    SparseArray<String> choiceNames();

    V valueByKey(K key);

    String nameByKey(K key);

    int indexByKey(K key);

    int indexByValue(V value);

    K keyAt(int index);
}
