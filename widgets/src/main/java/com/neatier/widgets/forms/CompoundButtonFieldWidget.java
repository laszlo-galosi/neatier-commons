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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.ColorRes;
import androidx.annotation.IdRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import com.fernandocejas.arrow.checks.Preconditions;
import com.neatier.widgets.R;
import com.neatier.widgets.helpers.DrawableHelper;

/**
 * Custom widget for a input field widget with compound Drawables button.
 * <p>Custom style attributes:
 * <ul>
 * <li>app:layout - the widgets layout resource</li>
 * <li>app:if_fieldKey - string identifier of the key </li>
 * <li>app:if_labelViewId - the label view </li>
 * <li>app:if_helperViewId - the helper text view id </li>
 * <li>app:if_widgetLayout the custom widget layout to be inflated</li>
 * <li>app:if_showLabel - true if the label should be visible</li>
 * <li>app:if_showHelper - true if the helper text should be visible</li>
 * <li>app:if_label - label text</li>
 * <li>app:if_helper - helper text</li>
 * <li>app:if_labelFormat -label {@link String#format(String, Object...)}</li>
 * <li>app:if_labelTextColor - label text color resource</li>
 * <li>app:if_helperTextColor helper text color resource</li>
 * <li>app:ew_labelAsHint - true if the label should be shown as the {@link EditText} hint</li>
 * <li>app:ew_helperAsHint - true if the helper text should be shown as the {@link EditText}
 * hint</li>
 * <li>app:ew_multiLine" true if the {@link EditText} can be multiline</li>
 * <li>app:ew_unfocusedFieldAlign - text alignment of the {@link EditText#setGravity(int)} in
 * unfocused mode </li>
 * <li>app:ew_focusedFieldAlign - text alignment of the {@link EditText#setGravity(int)} in focused
 * mode </li>
 * <li>app:value - the {@link EditText} initial value./>
 * <li>app:cbfw_clickableViewId - the view id of the button which is clickable</li>
 * <li>app:cbfw_buttonDrawable- the icon drawable resource of the button </li>
 * <li>app:cbfw_drawableTintList - {@link StateListDrawable} resource of the button</li>
 * <li>app:cbfw_onClick - OnClickListener for the button</li>
 * </ul>
 * </p>
 *
 * @author László Gálosi
 * @since 27/02/17
 */
@BindingMethods({
                      @BindingMethod(type = CompoundButtonFieldWidget.class,
                                     attribute = "cbfw_onClick", method = "setOnClickListener"),
                })
public class CompoundButtonFieldWidget extends EditFieldWidget {

    public static final int[] EXPANDED_STATE_SET = { android.R.attr.state_expanded };
    public static final int[] EXPANDED_STATES =
          new int[] { android.R.attr.state_expanded, -android.R.attr.state_expanded };

    protected final Drawable mButtonDrawable;
    protected final ColorStateList mDrawableColor;
    protected ImageView mButton;
    protected boolean mExpanded;
    View.OnClickListener mOnClickListener;
    private @IdRes int mClickableViewId;
    private View mClickableView;

    public CompoundButtonFieldWidget(final Context context) {
        this(context, null);
    }

    public CompoundButtonFieldWidget(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public CompoundButtonFieldWidget(final Context context, final AttributeSet attrs,
            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable
                        .CompoundButtonFieldWidget,
                defStyleAttr, 0);
        mButtonDrawable = a.hasValue(R.styleable.CompoundButtonFieldWidget_cbfw_buttonDrawable)
                ? a.getDrawable(R.styleable.CompoundButtonFieldWidget_cbfw_buttonDrawable)
                : ContextCompat.getDrawable(getContext(), R.drawable.ic_eye_24dp);
        mClickableViewId =
                a.getResourceId(R.styleable.CompoundButtonFieldWidget_cbfw_clickableViewId, 0);
        mDrawableColor =
                createDefaultColorStateList(a,
                        R.styleable.CompoundButtonFieldWidget_cbfw_drawableTintList,
                        android.R.attr.textColorPrimary,
                        R.color.colorTextPrimary);
        setExpanded(mExpanded);
        a.recycle();
        initView(context);
    }

    @SuppressLint("RestrictedApi")
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
              androidx.appcompat.R.attr.colorControlNormal, value, true)) {
            return null;
        }
        int baseColor = baseColorStateList.getDefaultColor();
        int defaultColor =
                defaultColorRes.length > 0 ? ContextCompat.getColor(getContext(),
                        defaultColorRes[0])
                        : baseColor;
        return new ColorStateList(new int[][] {
                EXPANDED_STATES,
                EMPTY_STATE_SET
        }, new int[] {
                baseColorStateList.getColorForState(EXPANDED_STATE_SET, defaultColor),
                a.hasValue(attr) ? a.getColor(attr, defaultColor) : defaultColor
        });
    }

    @Override public void initView(final Context context) {
        super.initView(context);
        mButton = (ImageView) mItemView.findViewById(R.id.btn_action);
        Preconditions.checkNotNull(mButton, "No ImageButton with id btn_action found.");
        getEditText().setInputType(InputType.TYPE_CLASS_TEXT);
        if (mClickableViewId != 0) {
            mClickableView = mItemView.findViewById(mClickableViewId);
        }
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mButton != null) {
            mButton.setOnClickListener(v -> {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(this);
                }
            });
        }
        if (mClickableView != null) {
            mClickableView.setOnClickListener(v -> {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(this);
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow() {
        mButton.setOnClickListener(null);
        mItemView.setOnClickListener(null);
        if (mClickableView != null) {
            mClickableView.setOnClickListener(null);
        }
        mOnClickListener = null;
        super.onDetachedFromWindow();
    }

    @Override public int getFieldPaddingRight() {
        return getEditText().getPaddingRight();
    }

    protected int[] getDrawableState(final int[] state) {
        if (mExpanded) {
            mergeDrawableStates(state, EXPANDED_STATE_SET);
        }
        //Log.v("onCreateDrawableState", getId(), Arrays.toString(state));
        return state;
    }

    @Override public void setOnClickListener(final View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] state = super.onCreateDrawableState(extraSpace + 2);
        return getDrawableState(state);
    }

    public boolean isExpanded() {
        return mExpanded;
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
                    /*getDrawableState(getDrawableState())*/drawableState,
                                                   defaultColor, getContext()
              )
        );
        super.refreshDrawableState();
    }
}
