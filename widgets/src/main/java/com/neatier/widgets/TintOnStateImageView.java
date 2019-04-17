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

package com.neatier.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.ColorInt;

/**
 * ImageView that changes it's image color depending on the state (pressed, selected...)
 *
 * @author Sotti https://plus.google.com/+PabloCostaTirado/about
 */
public class TintOnStateImageView extends ImageView {
    private ColorStateList mColorStateList;

    public TintOnStateImageView(Context context) {
        super(context);
    }

    public TintOnStateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context, attrs, 0);
    }

    /**
     * Create, bind and set up the resources
     *
     * @param context is the context to get the resources from
     * @param attributeSet is the attributeSet
     * @param defStyle is the style
     */
    private void initialise(Context context, AttributeSet attributeSet, int defStyle) {
        TypedArray a =
              context.obtainStyledAttributes(attributeSet, R.styleable.TintOnStateImageView,
                                             defStyle, 0);
        mColorStateList = a.getColorStateList(R.styleable.TintOnStateImageView_colorStateList);
        a.recycle();
    }

    public TintOnStateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (mColorStateList != null && mColorStateList.isStateful()) {
            updateTintColor();
        }
    }

    /**
     * Updates the color of the image
     */
    private void updateTintColor() {
        int color = mColorStateList.getColorForState(getDrawableState(),
                                                     getResources().getColor(
                                                           R.color.colorTextPrimary));

        super.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public @ColorInt int getTintColor() {
        return mColorStateList.getColorForState(getDrawableState(),
                                                mColorStateList.getDefaultColor());
    }

    public void setColorStateList(final ColorStateList colorStateList) {
        mColorStateList = colorStateList;
        updateTintColor();
    }

    public ColorStateList getColorStateList() {
        return mColorStateList;
    }
}
