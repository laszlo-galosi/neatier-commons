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
import android.content.res.TypedArray;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.TintTypedArray;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.neatier.widgets.R;
import com.neatier.widgets.helpers.DrawableHelper;
import com.neatier.widgets.helpers.WidgetUtils;
import rx.functions.Action1;

/**
 * Created by László Gálosi on 27/02/17
 */

@BindingMethods(
      @BindingMethod(type = CompoundButtonWidget.class, attribute = "cbw_onClick",
                     method = "setOnClickListener")
)
public class CompoundButtonWidget extends FrameLayout
      implements HasInputField<String, Action1<View>> {

    public static final int[] DISABLED_STATE_SET = { -android.R.attr.state_enabled };
    public static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };
    public static final int[] PRESSED_STATE_SET = { android.R.attr.state_pressed };

    protected Drawable mIconDrawable;
    protected ColorStateList mIconTintList;
    private @LayoutRes int mLayoutRes;
    private String mLabelText;
    private @IdRes int mLabelViewId;
    private String mHelperText;
    private @IdRes int mHelperViewId;
    private boolean mShowLabel;
    private boolean mShowHelper;
    private String mLabelFormat;
    private @ColorInt int mLabelTextColor;
    private @ColorInt int mHelperTextColor;
    private @IdRes int mIconViewId;
    private Drawable mBackgroundDrawable;
    private ColorStateList mBackgroundTintList;
    private View mItemView;
    protected TextView mLabelView;
    protected ImageView mIconView;
    private TextView mHelperView;

    View.OnClickListener mOnClickListener;
    private Action1 mAction;
    private String mKey;
    private Paint mLabelTextPaint;

    public CompoundButtonWidget(final Context context) {
        this(context, null);
    }

    public CompoundButtonWidget(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompoundButtonWidget(final Context context, final AttributeSet attrs,
          final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray prefa =
              context.obtainStyledAttributes(attrs, R.styleable.Preference, defStyleAttr, 0);
        TypedArray ifa =
              context.obtainStyledAttributes(attrs, R.styleable.HasInputField, defStyleAttr, 0);
        TintTypedArray wa = TintTypedArray.obtainStyledAttributes(context, attrs,
                                                                  R.styleable.CompoundButtonWidget,
                                                                  defStyleAttr, 0);

        mKey = prefa.getString(R.styleable.Preference_android_key);

        mLayoutRes = TypedArrayUtils.getResourceId(prefa, R.styleable.Preference_layout,
                                                   R.styleable.Preference_android_layout,
                                                   R.layout.widget_editfield_w_label);
        mLabelText = ifa.getString(R.styleable.HasInputField_if_label);
        mLabelViewId =
              ifa.getResourceId(R.styleable.HasInputField_if_labelViewId, 0);
        mHelperText = ifa.getString(R.styleable.HasInputField_if_helper);
        mHelperViewId =
              ifa.getResourceId(R.styleable.HasInputField_if_helperViewId, 0);
        mShowLabel = ifa.getBoolean(R.styleable.HasInputField_if_showLabel, true);
        mShowHelper = ifa.getBoolean(R.styleable.HasInputField_if_showHelper, false);
        if (ifa.hasValue(R.styleable.HasInputField_if_labelFormat)) {
            mLabelFormat = ifa.getString(R.styleable.HasInputField_if_labelFormat);
        } else {
            mLabelFormat = "%s";
        }

        mLabelTextColor = ifa.getColor(R.styleable.HasInputField_if_labelTextColor,
                                       ContextCompat.getColor(getContext(),
                                                              R.color.colorTextPrimary));
        mHelperTextColor = ifa.getColor(R.styleable.HasInputField_if_helperTextColor,
                                        ContextCompat.getColor(getContext(),
                                                               R.color.colorTextSecondary));
        mIconViewId =
              wa.getResourceId(R.styleable.CompoundButtonWidget_cbw_iconViewId, 0);

        mIconDrawable = wa.hasValue(R.styleable.CompoundButtonWidget_cbw_icon)
                        ? wa.getDrawable(R.styleable.CompoundButtonWidget_cbw_icon) :
                        ContextCompat.getDrawable(getContext(), android.R.drawable.ic_input_add);
        mBackgroundDrawable = wa.hasValue(R.styleable.CompoundButtonWidget_cbw_background)
                              ? wa.getDrawable(R.styleable.CompoundButtonWidget_cbw_background) :
                              ContextCompat.getDrawable(getContext(),
                                                        R.drawable.background_panel_cornered_2dp);
        mIconTintList =
              createDefaultColorStateList(wa, R.styleable.CompoundButtonWidget_cbw_iconTintList,
                                          android.R.attr.textColorPrimary,
                                          R.color.colorTextPrimary);
        mBackgroundTintList =
              createDefaultColorStateList(wa,
                                          R.styleable.CompoundButtonWidget_cbw_backgroundTintList,
                                          android.R.attr.textColorPrimary, R.color.colorAccent);
        prefa.recycle();
        ifa.recycle();
        wa.recycle();
        initView(context);
    }

    public void initView(final Context context) {
        if (mLayoutRes > 0) {
            removeAllViews();
            mItemView = LayoutInflater.from(getContext()).inflate(mLayoutRes, this, false);
            WidgetUtils.setLayoutSizeOf(mItemView, LayoutParams.MATCH_PARENT,
                                        LayoutParams.MATCH_PARENT);
            addView(mItemView);
            if (mHelperViewId > 0) {
                mHelperView = (TextView) mItemView.findViewById(mHelperViewId);
                setHelper(mHelperText, 0);
                mHelperView.setTextColor(mHelperTextColor);
            }
            if (mLabelViewId > 0) {
                mLabelView = (TextView) mItemView.findViewById(mLabelViewId);
                initLabelPaint();
                mLabelView.setTextColor(mLabelTextColor);
                setLabel(mLabelText);
            }
            if (mIconViewId > 0) {
                mIconView = (ImageView) mItemView.findViewById(mIconViewId);
                setDrawables();
            }
            WidgetUtils.setVisibilityOf(mHelperView, mShowHelper);
            WidgetUtils.setVisibilityOf(mLabelView, mShowLabel);
            mItemView.setClickable(true);
        }
    }

    private void setDrawables() {
        final Context context = getContext();
        int[] drawableState = getDrawableState(getDrawableState());
        if (mItemView != null) {
            Drawable backgroundDrawable =
                  DrawableHelper.drawableForColorState(
                        mBackgroundDrawable,
                        mBackgroundTintList,
                        drawableState,
                        ContextCompat.getColor(context, R.color.colorAccent), context);
            mItemView.setBackground(backgroundDrawable);
        }
        if (mIconView != null) {
            Drawable iconDrawable =
                  DrawableHelper.drawableForColorState(
                        mIconDrawable,
                        mIconTintList,
                        drawableState,
                        ContextCompat.getColor(context, R.color.colorTextPrimary), context);
            mIconView.setImageDrawable(iconDrawable);
        }
    }

    public void initLabelPaint() {
        mLabelTextPaint = new TextPaint();
        mLabelTextPaint.setColor(mLabelTextColor);
        if (mLabelView != null) {
            mLabelTextPaint.setTextSize(mLabelView.getTextSize());
        }
        mLabelTextPaint.setAntiAlias(true);
    }

    @Override public String getKey() {
        return mKey;
    }

    @Override public Action1<View> getValue() {
        return mAction;
    }

    @Override public void setValue(final Action1<View> action) {
        mAction = action;
    }

    @Override public void setLabel(final String labelText) {
        mLabelText = labelText;
        if (mLabelView == null) {
            mLabelView = (TextView) findViewById(mLabelViewId);
        }
        WidgetUtils.setTextOf(mLabelView, String.format(mLabelFormat, mLabelText));
    }

    @Override public String getLabel() {
        return null;
    }

    public void setIconDrawable(final Drawable iconDrawable) {
        mIconDrawable = iconDrawable;
        setDrawables();
    }

    public void setIconTintList(final ColorStateList iconTintList) {
        mIconTintList = iconTintList;
        setDrawables();
    }

    public void setLayoutRes(final int layoutRes) {
        mLayoutRes = layoutRes;
        initView(getContext());
    }

    public void setLabelFormat(final String labelFormat) {
        mLabelFormat = labelFormat;
        setLabel(mLabelText);
    }

    public void setBackgroundDrawable(final Drawable backgroundDrawable) {
        mBackgroundDrawable = backgroundDrawable;
        setDrawables();
    }

    @Override public void setBackgroundTintList(final ColorStateList backgroundTintList) {
        mBackgroundTintList = backgroundTintList;
        setDrawables();
    }

    @Override public void setHelper(final String textData, @ColorRes final int color) {

    }

    @Override public void showHideHelper(final boolean visible) {

    }

    @Override public String getHelper() {
        return null;
    }

    @Override public int getLabelViewId() {
        return 0;
    }

    @Override public int getHelperViewId() {
        return 0;
    }

    @Override public void setOnClickListener(final View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mItemView == null) {
            return;
        }
        mItemView.setOnClickListener(v -> {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(this);
            }
            if (mAction != null) {
                mAction.call(this);
            }
        });
        //mLabelView.setTag(this.getId());
    }

    @Override protected void onDetachedFromWindow() {
        mLabelView.setOnClickListener(null);
        mOnClickListener = null;
        super.onDetachedFromWindow();
    }

    @Override public void refreshDrawableState() {
        super.refreshDrawableState();
        setDrawables();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] state = super.onCreateDrawableState(extraSpace + 2);
        return getDrawableState(state);
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
              PRESSED_STATE_SET,
              DISABLED_STATE_SET,
              CHECKED_STATE_SET,
              EMPTY_STATE_SET
        }, new int[] {
              baseColorStateList.getColorForState(PRESSED_STATE_SET, defaultColor),
              baseColorStateList.getColorForState(DISABLED_STATE_SET, defaultColor),
              baseColorStateList.getColorForState(CHECKED_STATE_SET, defaultColor),
              defaultColor
        });
    }
}
