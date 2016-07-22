package com.neatier.widgets.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.neatier.widgets.ThemeUtil;

public class ItemSpacingDecoration extends RecyclerView.ItemDecoration {

    private int mItemSpacing;

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
