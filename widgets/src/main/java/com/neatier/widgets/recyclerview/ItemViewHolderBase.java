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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.fernandocejas.arrow.optional.Optional;
import com.neatier.widgets.Bindable;

/**
 * Created by László Gálosi on 18/03/16
 */
public class ItemViewHolderBase extends RecyclerView.ViewHolder
      implements Bindable {

    View mMainWidget;
    protected View mItemView;
    Context mContext;
    protected Optional<Object> mDataItem;

    @Override public void bind(final Object dataItem) {
        Log.v("bind", dataItem.toString());
        mDataItem = Optional.fromNullable(dataItem);
    }

    @Override public Optional getData() {
        return mDataItem;
    }

    protected ItemViewHolderBase(final View itemView, Context context) {
        super(itemView);
        mItemView = itemView;
        mContext = context;
    }

    /**
     * Constructor
     *
     * @param itemView the parent view of this recycled item
     * @param widgetId the main widget of this item which can be as simple as {@link TextView} or
     * a complex custom view. Represents the main widget which the user interacts with.
     */
    protected ItemViewHolderBase(final View itemView, @IdRes int widgetId, Context context) {
        super(itemView);
        mItemView = itemView;
        mMainWidget = mItemView.findViewById(widgetId);
        mContext = context;
    }
}
