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

package com.neatier.widgets.forms;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.TintTypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import com.neatier.widgets.R;
import com.neatier.widgets.helpers.DrawableHelper;

/**
 * Created by László Gálosi on 27/02/17
 */
@BindingMethods(
      @BindingMethod(type = CompoundButtonFieldWidget.class, attribute = "cbfw_onClick",
                     method = "setOnClickListener")
)
public class CompoundButtonFieldWidget extends EditFieldWidget {

    public static final int[] EXPANDED_STATE_SET = { android.R.attr.state_expanded };
    public static final int[] EXPANDED_STATES =
          new int[] { android.R.attr.state_expanded, -android.R.attr.state_expanded };

    protected final Drawable mButtonDrawable;
    protected final ColorStateList mDrawableColor;
    protected ImageButton mButton;

    View.OnClickListener mOnClickListener;
    protected boolean mExpanded;

    public CompoundButtonFieldWidget(final Context context) {
        this(context, null);
    }

    public CompoundButtonFieldWidget(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompoundButtonFieldWidget(final Context context, final AttributeSet attrs,
          final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                                                                 R.styleable
                                                                       .CompoundButtonFieldWidget,
                                                                 defStyleAttr, 0);
        mButtonDrawable = a.hasValue(R.styleable.CompoundButtonFieldWidget_cbfw_buttonDrawable)
                          ? a.getDrawable(R.styleable.CompoundButtonFieldWidget_cbfw_buttonDrawable)
                          :
                          ContextCompat.getDrawable(getContext(), R.drawable.ic_eye_24dp);
        mDrawableColor =
              createDefaultColorStateList(a,
                                          R.styleable
                                                .CompoundButtonFieldWidget_cbfw_drawableTintList,
                                          android.R.attr.textColorPrimary,
                                          R.color.colorTextPrimary);
        setExpanded(mExpanded);
        a.recycle();
    }

    @Override public void initView(final Context context) {
        super.initView(context);
        mButton = (ImageButton) mItemView.findViewById(R.id.btn_action);
        getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
    }

    @Override public void setOnClickListener(final View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override public int getFieldPaddingRight() {
        return getEditText().getPaddingRight();
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mButton == null) {
            return;
        }
        mButton.setOnClickListener(v -> {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(this);
            }
        });
        //mLabelView.setTag(this.getId());
    }

    @Override protected void onDetachedFromWindow() {
        mButton.setOnClickListener(null);
        mOnClickListener = null;
        super.onDetachedFromWindow();
    }

    public void setExpanded(final boolean expanded) {
        mExpanded = expanded;
        int defaultColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        int[] drawableState = getDrawableState(getDrawableState());
        if (mButtonDrawable.isStateful()) {
            mButtonDrawable.setState(drawableState);
        }
        mButton.setImageDrawable(
              DrawableHelper.drawableForColorState(mButtonDrawable, mDrawableColor,
                                                   getDrawableState(),
                                                   defaultColor, getContext()
              )
        );
        super.refreshDrawableState();
    }

    /*@Override protected void drawableStateChanged() {
        super.drawableStateChanged();
        int defaultColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        mButton.setImageDrawable(
              DrawableHelper.drawableForColorState(mButtonDrawable, mDrawableColor,
                                                   getDrawableState(getDrawableState()),
                                                   defaultColor, getContext()
              ));
    }*/

    public boolean isExpanded() {
        return mExpanded;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] state = super.onCreateDrawableState(extraSpace + 2);
        return getDrawableState(state);
    }

    protected int[] getDrawableState(final int[] state) {
        if (mExpanded) {
            mergeDrawableStates(state, EXPANDED_STATE_SET);
        }
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
              EXPANDED_STATES,
              EMPTY_STATE_SET
        }, new int[] {
              baseColorStateList.getColorForState(EXPANDED_STATE_SET, defaultColor),
              a.hasValue(attr) ? a.getColor(attr, defaultColor) : defaultColor
        });
    }
}
