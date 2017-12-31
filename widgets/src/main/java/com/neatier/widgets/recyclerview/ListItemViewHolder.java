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

package com.neatier.widgets.recyclerview;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;
import com.neatier.widgets.Bindable;

/**
 * An {@link ItemViewHolderBase} sub class implementing {@link Bindable} interface.
 *
 * @author László Gálosi
 * @since 25/08/15
 */
public class ListItemViewHolder extends ItemViewHolderBase implements Bindable {

    private Context mContext;

    public ListItemViewHolder(final View itemView, @IdRes final int widgetId, Context context) {
        super(itemView, widgetId, context);
        mContext = context;
    }

    @Override public void bind(final Object dataItem) {
        super.bind(dataItem);
        if (itemView instanceof Bindable) {
            //noinspection unchecked
            ((Bindable) itemView).bind(dataItem);
        }
    }
}
