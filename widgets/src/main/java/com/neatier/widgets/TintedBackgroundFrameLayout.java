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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import com.neatier.widgets.helpers.ColorStates;
import com.neatier.widgets.helpers.DrawableHelper;

/**
 * A {@link FrameLayout} sub class with tinted background.
 *
 * @author László Gálosi
 * @since 14/05/17
 */
@BindingMethods(
        @BindingMethod(type = TintedBackgroundFrameLayout.class, attribute = "tbvg_drawableTintList",
                     method = "setBackgroundDrawableColor")
)
public class TintedBackgroundFrameLayout extends FrameLayout {

    /**
     * The Color of the background drawable as a {@link ColorStateList}
     */
    private ColorStateList mDrawableColor;

    public TintedBackgroundFrameLayout(final Context context) {
        this(context, null);
    }

    public TintedBackgroundFrameLayout(final Context context,
          @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor performing inflation from XML and apply a class-specific base style from the
     * given
     * theme attribute or style resource.
     *
     * @param context The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     * reference to a style resource that supplies default values for
     * the view. Can be 0 to not look for defaults.
     * @see super#FrameLayout(Context, AttributeSet, int)
     */
    @SuppressLint("RestrictedApi")
    public TintedBackgroundFrameLayout(final Context context,
          @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(
              context, attrs, R.styleable.TintedBackgroundViewGroup, defStyleAttr, 0);
        mDrawableColor =
                ColorStates.with(a, context)
                        .styleable(R.styleable.TintedBackgroundViewGroup_tbvg_drawableTintList, 0)
                        .defaultColorRes(R.color.colorPrimary)
                        .stateSet(EMPTY_STATE_SET)
                        .create();
        a.recycle();
    }

    /**
     * This function is called whenever the state of the view changes in such
     * a way that it impacts the state of drawables being shown.
     *
     * @see Drawable#setState(int[])
     */
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

    /**
     * Returns the background color as a ColorStateList
     */
    public ColorStateList getBackgroundDrawableColor() {
        return mDrawableColor;
    }

    /**
     * Sets the views background to the given color state list.
     */
    public void setBackgroundDrawableColor(final ColorStateList drawableColor) {
        mDrawableColor = drawableColor;
    }
}
