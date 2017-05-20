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

package com.neatier.widgets;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.neatier.widgets.helpers.DrawableHelper;

/**
 * Created by László Gálosi on 27/02/17
 */
public class TintedImageView extends AppCompatImageView {

    protected final ColorStateList mDrawableColor;

    public TintedImageView(final Context context) {
        this(context, null);
    }

    public TintedImageView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintedImageView(final Context context, final AttributeSet attrs,
          final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                                                                 R.styleable
                                                                       .TintedImageView,
                                                                 defStyleAttr, 0);
        mDrawableColor =
              createDefaultColorStateList(a, R.styleable.TintedImageView_tiv_drawableTintList,
                                          android.R.attr.textColorPrimary,
                                          R.color.colorTextPrimary);
        a.recycle();
    }

    @Override protected void drawableStateChanged() {
        super.drawableStateChanged();
        int defaultColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        this.setImageDrawable(
              DrawableHelper.drawableForColorState(getDrawable(), mDrawableColor,
                                                   getDrawableState(getDrawableState()),
                                                   defaultColor, getContext()
              )
        );
    }

    protected int[] getDrawableState(final int[] state) {
        //Log.v("onCreateDrawableState", getId(), Arrays.toString(state));
        return state;
    }

    private ColorStateList createDefaultColorStateList(TintTypedArray a, int attr,
          int baseColorThemeAttr,
          @ColorRes int... defaultColorRes) {
        final TypedValue value = new TypedValue();
        if (!getContext().getTheme().resolveAttribute(baseColorThemeAttr, value, true)) {
            return null;
        }
        ColorStateList baseColorStateList = AppCompatResources.getColorStateList(
              getContext(), value.resourceId);
        if (!getContext().getTheme().resolveAttribute(
              android.support.v7.appcompat.R.attr.colorControlNormal, value, true)) {
            return null;
        }
        int baseColor = baseColorStateList.getDefaultColor();
        int defaultColor =
              defaultColorRes.length > 0 ? ContextCompat.getColor(getContext(), defaultColorRes[0])
                                         : baseColor;
        return new ColorStateList(new int[][] {
              EMPTY_STATE_SET
        }, new int[] {
              a.hasValue(attr) ? a.getColor(attr, defaultColor) : defaultColor
        });
    }
}
