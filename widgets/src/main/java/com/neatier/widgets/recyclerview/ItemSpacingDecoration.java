/*
 * Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 * All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions Kft.
 *  and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *  Delight Solutions Kft.
 */

package com.neatier.widgets.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.neatier.widgets.ThemeUtil;

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {

    protected int mItemSpacing;

    public ItemSpacingDecoration(@NonNull Context context, int itemSpacingInDp) {
        mItemSpacing = ThemeUtil.dpToPx(context, itemSpacingInDp);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(mItemSpacing, mItemSpacing, mItemSpacing, mItemSpacing);
    }
}
