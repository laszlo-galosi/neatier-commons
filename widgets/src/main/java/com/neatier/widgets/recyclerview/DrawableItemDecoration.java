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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * A recyclerView ItemDecoration subclass which draws a drawable around the recycler view
 * items with the specified orientation.
 */
public class DrawableItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * Constant for horizontal recycler view orientation.
     */
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    /**
     * Constant for vertical recycler view orientation.
     */
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    /**
     * Constant attribute set for the {@link android.R.attr#listDivider} attribute.
     */
    private static final int[] ATTRS = new int[] {
          android.R.attr.listDivider
    };

    /**
     * The divider drawable.
     */
    private Drawable mDivider;

    /**
     * The orientation of the recycler view items.
     */
    private int mOrientation = VERTICAL_LIST;

    public DrawableItemDecoration(Context context) {
        this(ATTRS, context, VERTICAL_LIST);
    }

    public DrawableItemDecoration(int[] styleAttrs, Context context, int orientation) {
        final TypedArray a = context.obtainStyledAttributes(styleAttrs);
        mDivider = a.getDrawable(0);
        a.recycle();
        setOrientation(VERTICAL_LIST);
    }

    /**
     * Set the orientation of the recycler view items to the given orientation {@link
     * #HORIZONTAL_LIST} or {@link #VERTICAL_LIST}
     */
    public DrawableItemDecoration setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
        return this;
    }

    /**
     * Constructor with the given drawable resource and context and {@link #VERTICAL_LIST} default
     * orientation.
     */
    public DrawableItemDecoration(@DrawableRes int drawableRes, Context context) {
        this(drawableRes, context, VERTICAL_LIST);
    }

    /**
     * Constructor with the given drawable resource and context and orientation.
     */
    public DrawableItemDecoration(@DrawableRes int drawableRes, Context context, int orientation) {
        mDivider = ContextCompat.getDrawable(context, drawableRes);
        setOrientation(orientation);
    }

    /**
     * Draw any appropriate decorations into the Canvas according to the orientation  supplied to
     * the RecyclerView.
     * Any content drawn by this method will be drawn after the item views are drawn
     * and will thus appear over the views.
     *
     * @param c Canvas to draw into
     * @param parent RecyclerView this ItemDecoration is drawing into
     * @param state The current state of RecyclerView.
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * Draws the decoration vertically into th given canvas supplied to the given RecyclerView.
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (isDecorated(child, parent)) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                      .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    /**
     * Draws the decoration horizontally  into th given canvas supplied to the given RecyclerView.
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            if (isDecorated(child, parent)) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                      .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    /**
     * Returns true whether the given child of the given parent RecyclerView should be decorated or
     * not.
     */
    public boolean isDecorated(View view, RecyclerView parent) {
        return true;
    }

    /**
     * Can be  overridden to control the decoration drawing for the given outer rectangle
     * of the given RecyclerView child view depending on the given RecyclerView state.
     *
     * @param outRect the outer rectangle of the child view
     * @param view the child view on which the item decoration should be drawn
     * @param parent the pRecyclerView parent of the child iew.
     * @param state the RecyclerView's state.
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
          RecyclerView.State state) {
        if (isDecorated(view, parent)) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        } else {
            super.getItemOffsets(outRect, view, parent, state);
        }
    }
}
