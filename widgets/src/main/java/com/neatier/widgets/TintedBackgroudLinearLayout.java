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
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import com.neatier.widgets.helpers.DrawableHelper;

/**
 * Created by László Gálosi on 14/05/17
 */
@BindingMethods(
      @BindingMethod(type = TintedBackgroudLinearLayout.class, attribute =
            "tbvg_drawableTintList", method = "setBackgroundDrawableColor")
)
public class TintedBackgroudLinearLayout extends LinearLayout {

    private ColorStateList mDrawableColor;

    public TintedBackgroudLinearLayout(final Context context) {
        this(context, null);
    }

    public TintedBackgroudLinearLayout(final Context context,
          @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TintedBackgroudLinearLayout(final Context context,
          @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(
              context, attrs, R.styleable.TintedBackgroundViewGroup, defStyleAttr, 0);
        mDrawableColor =
              createDefaultColorStateList(a,
                                          R.styleable
                                                .TintedBackgroundViewGroup_tbvg_drawableTintList,
                                          android.R.attr.textColorPrimary,
                                          R.color.colorTextPrimary);
        a.recycle();
    }

    @Override protected void drawableStateChanged() {
        super.drawableStateChanged();
        int defaultColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        this.setBackground(
              DrawableHelper.drawableForColorState(getBackground(), mDrawableColor,
                                                   getDrawableState(),
                                                   defaultColor, getContext()
              )
        );
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

    public ColorStateList getBackgroundDrawableColor() {
        return mDrawableColor;
    }

    public void setBackgroundDrawableColor(final ColorStateList drawableColor) {
        mDrawableColor = drawableColor;
        refreshDrawableState();
    }
}
