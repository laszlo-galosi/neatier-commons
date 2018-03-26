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

import java.util.List;
import rx.Observable;
import rx.functions.Func1;

/**
 * A helper class to transform a list of objects with Type O to an {@link Observable} emitting
 * the transformed objects with type T transformed with the a transformer function.
 *
 * @author László Gálosi
 * @since 25/11/15
 */
public class ObjectListTransformerStream<O, T> {

    /**
     * The object list to be transformed.
     */
    private List<O> mObjectList;

    /**
     * The transformer function.
     */
    private Func1<O, T> mTransformFunction;

    /**
     * Constructor with the given object list to be transformed with the given
     * transformer function.
     */
    public ObjectListTransformerStream(final List<O> objectList,
            final Func1<O, T> transformFunction) {
        mObjectList = objectList;
        mTransformFunction = transformFunction;
    }

    /**
     * Returns the {@link Observable} emitting the transformed elements of the object list.
     */
    public Observable<T> getTransformedStream() {
        return Observable.from(mObjectList).map(mTransformFunction);
    }
}
