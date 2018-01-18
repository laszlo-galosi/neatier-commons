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
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.TintTypedArray;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import com.neatier.commons.helpers.Preconditions;
import com.neatier.widgets.R;
import com.neatier.widgets.ThemeUtil;
import com.neatier.widgets.helpers.DrawableHelper;
import com.neatier.widgets.helpers.WidgetUtils;

/**
 * An {@link EditFieldWidget} sub class of a widget with password transformation type type,
 * and a reveal compound button.
 * <p>Custom style attributes includes {@link EditFieldWidget}'s attributes:
 * <ul>
 * <li>app:pf_buttonDrawable - drawable resource of the reveal password button</li>
 * <li>app:pf_buttonTintList - {@link ColorStateList} of the reveal password button</li>
 * <li>app:pf_autoHide - true if the password should be revealed when the field gets the focus</li>
 * </ul>
 *
 * @author László Gálosi
 * @see EditText#setTransformationMethod(TransformationMethod)
 * @see PasswordTransformationMethod#getInstance()
 * @since 27/02/17
 */
public class PasswordFieldWidget extends EditFieldWidget {

    public static final int[] REVEALED_STATE_SET = { R.attr.state_revealed };
    public static final int[] REVEALED_STATES =
          new int[] { R.attr.state_revealed, -R.attr.state_revealed };

    private final Drawable mRevealDrawable;
    private final ColorStateList mIconColorStateList;
    ImageButton mBtnReveal;
    boolean isPasswordRevealed;
    private boolean mAutoHide = true;
    View.OnFocusChangeListener mAutoHideFocusChangeListener = (view, hasFocus) -> {
        super.mDefaultFocusChangeListener.onFocusChange(view, hasFocus);
        boolean visible = mAutoHide ? hasFocus : true;
        WidgetUtils.setVisibilityOf(mBtnReveal, visible);
        updatePaddings();
    };

    public PasswordFieldWidget(final Context context) {
        this(context, null);
    }

    public PasswordFieldWidget(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("RestrictedApi")
    public PasswordFieldWidget(final Context context, final AttributeSet attrs,
          final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                                                                 R.styleable.PasswordFieldWidget,
                                                                 defStyleAttr, 0);
        mRevealDrawable = a.hasValue(R.styleable.PasswordFieldWidget_pf_buttonDrawable)
                          ? a.getDrawable(R.styleable.PasswordFieldWidget_pf_buttonDrawable) :
                          ContextCompat.getDrawable(getContext(), R.drawable.ic_eye_24dp);
        mIconColorStateList =
              createDefaultColorStateList(a, R.styleable.PasswordFieldWidget_pf_buttonTintList,
                                          android.R.attr.textColorPrimary,
                                          R.color.colorTextPrimary);
        mAutoHide = a.getBoolean(R.styleable.PasswordFieldWidget_pf_autoHide, false);
        setPasswordRevealed(isPasswordRevealed);
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
              android.support.v7.appcompat.R.attr.colorControlNormal, value, true)) {
            return null;
        }
        int baseColor = baseColorStateList.getDefaultColor();
        int defaultColor =
              defaultColorRes.length > 0 ? ContextCompat.getColor(getContext(), defaultColorRes[0])
                                         : baseColor;
        return new ColorStateList(new int[][] {
              REVEALED_STATES,
              EMPTY_STATE_SET
        }, new int[] {
              baseColorStateList.getColorForState(REVEALED_STATE_SET, defaultColor),
              a.hasValue(attr) ? a.getColor(attr, defaultColor) : defaultColor
        });
    }

    public PasswordFieldWidget setPasswordRevealed(final boolean passwordRevealed) {
        isPasswordRevealed = passwordRevealed;
        WidgetUtils.setVisibilityOf(mBtnReveal, mAutoHide ? getEditText().hasFocus() : true);
        int defaultColor = mFieldTextColor;
        mBtnReveal.setImageDrawable(
              DrawableHelper.drawableForColorState(mRevealDrawable, mIconColorStateList,
                                                   getDrawableState(getDrawableState()),
                                                   defaultColor, getContext())
        );
        getEditText().setTransformationMethod(isPasswordRevealed
                                              ? null : PasswordTransformationMethod.getInstance());
        getEditText().setTypeface(Typeface.DEFAULT);
        getEditText().setTextColor(mFieldTextColor);
        super.refreshDrawableState();
        moveCursorToEnd();
        return this;
    }

    private int[] getDrawableState(final int[] state) {
        if (isPasswordRevealed) {
            mergeDrawableStates(state, REVEALED_STATE_SET);
        }
        //Log.v("onCreateDrawableState", getId(), Arrays.toString(state));
        return state;
    }

    @Override public void initView(final Context context) {
        super.initView(context);
        mBtnReveal = (ImageButton) mItemView.findViewById(R.id.btn_action);
        getEditText().setInputType(
              InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        getEditText().setTransformationMethod(isPasswordRevealed
                                              ? PasswordTransformationMethod.getInstance() : null);
        Preconditions.checkNotNull(mBtnReveal, "No ImageButton with id btn_show found.");
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mBtnReveal == null) {
            return;
        }
        mBtnReveal.setOnClickListener(v -> setPasswordRevealed(!isPasswordRevealed));
    }

    @Override protected void onDetachedFromWindow() {
        mBtnReveal.setOnClickListener(null);
        super.onDetachedFromWindow();
    }

    @Override public int getFieldPaddingRight() {
        boolean visible = mAutoHide ? getEditText().hasFocus() : true;
        return visible ? ThemeUtil.dpToPx(getContext(), 36) : mDefaultFieldPaddingStart;
    }

    @Override
    public void setFocusBehavior(@Nullable final View.OnFocusChangeListener fl,
          final int focusFlags) {
        OnFocusChangeListener mergedFl = (view, hasFocus) -> {
            if (mAutoHideFocusChangeListener != null) {
                mAutoHideFocusChangeListener.onFocusChange(view, hasFocus);
            }
            if (fl != null) {
                fl.onFocusChange(view, hasFocus);
            }
        };
        super.setFocusBehavior(mergedFl, focusFlags);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] state = super.onCreateDrawableState(extraSpace + 2);
        return getDrawableState(state);
    }
}
