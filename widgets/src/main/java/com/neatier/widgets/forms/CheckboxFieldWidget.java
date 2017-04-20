/*
 * Copyright (C) 2016 Extremenet Ltd., All Rights Reserved
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
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.neatier.commons.helpers.Preconditions;
import com.neatier.widgets.R;
import com.neatier.widgets.helpers.WidgetUtils;
import trikita.log.Log;

/**
 * Created by László Gálosi on 12/05/16
 */
public class CheckboxFieldWidget extends FrameLayout
      implements HasInputField<String, Boolean> {

    private String mLabelText;
    private String mLabelFormat = "%s";
    private String mHelperText;

    private boolean mShowHelper;
    private boolean mShowLabel;

    private AppCompatCheckBox mCheckBox;
    private View mItemView;

    private Boolean mValue = Boolean.FALSE;
    private @LayoutRes int mLayoutRes;
    private @IdRes int mLabelViewId;
    private @IdRes int mHelperViewId;
    private String mKey;
    private TextView mLabelView;
    private TextView mHelperView;
    private Paint mLabelTextPaint;

    public CheckboxFieldWidget(final Context context) {
        this(context, null);
    }

    public CheckboxFieldWidget(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckboxFieldWidget(final Context context, final AttributeSet attrs,
          final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray prefa =
              context.obtainStyledAttributes(attrs, R.styleable.Preference, defStyleAttr, 0);
        TypedArray ifa =
              context.obtainStyledAttributes(attrs, R.styleable.HasInputField, defStyleAttr, 0);

        mKey = prefa.getString(R.styleable.Preference_android_key);

        mLayoutRes = TypedArrayUtils.getResourceId(prefa, R.styleable.Preference_layout,
                                                   R.styleable.Preference_android_layout,
                                                   R.layout.widget_checkbox_w_helper);
        mLabelText = ifa.getString(R.styleable.HasInputField_if_label);
        mLabelViewId =
              ifa.getResourceId(R.styleable.HasInputField_if_labelViewId, 0);
        mHelperText = ifa.getString(R.styleable.HasInputField_if_helper);
        mHelperViewId =
              ifa.getResourceId(R.styleable.HasInputField_if_helperViewId, 0);
        mShowLabel = ifa.getBoolean(R.styleable.HasInputField_if_showLabel, true);
        mShowHelper = ifa.getBoolean(R.styleable.HasInputField_if_showHelper, true);

        if (ifa.hasValue(R.styleable.HasInputField_if_labelFormat)) {
            mLabelFormat = ifa.getString(R.styleable.HasInputField_if_labelFormat);
        }
        prefa.recycle();
        ifa.recycle();
        initView(context);
    }

    public void initView(Context context) {
        if (mLayoutRes > 0) {
            removeAllViews();
            mItemView = LayoutInflater.from(getContext()).inflate(mLayoutRes, this, false);
            WidgetUtils.setLayoutSizeOf(mItemView, LayoutParams.MATCH_PARENT,
                                        LayoutParams.MATCH_PARENT);
            addView(mItemView);
            if (mHelperViewId > 0) {
                mHelperView = (TextView) mItemView.findViewById(mHelperViewId);
                setHelper(mHelperText, 0);
            }
            mCheckBox = (AppCompatCheckBox) mItemView.findViewById(R.id.checkbox);
            if (mLabelViewId > 0) {
                mLabelView = (TextView) mItemView.findViewById(mLabelViewId);
                initLabelPaint();
            }
            setLabel(mLabelText);
            WidgetUtils.setVisibilityOf(mHelperView, mShowHelper);
            WidgetUtils.setVisibilityOf(mLabelView, mShowLabel);
            setValue(mValue);
        }
    }

    public void initLabelPaint() {
        mLabelTextPaint = new TextPaint();
        mLabelTextPaint.setColor(mCheckBox.getTextColors().getDefaultColor());
        mLabelTextPaint.setTextSize(mCheckBox.getTextSize());
        mLabelTextPaint.setAntiAlias(true);
    }

    public AppCompatCheckBox getCheckBox() {
        Preconditions.checkNotNull(mCheckBox,
                                   "Widget hasn't been initialized properly: "
                                         + getKey());
        return mCheckBox;
    }

    @Override public Boolean getValue() {
        Log.d("getValue", getKey(), getCheckBox().isChecked());
        return getCheckBox().isChecked();
    }

    @Override public void setValue(final Boolean value) {
        Log.d("setValue", getKey(), value);
        mValue = value;
        getCheckBox().setChecked(value);
    }

    @Override public void setLabel(final String labelText) {
        mLabelText = labelText;
        if (mLabelView == null && mLabelViewId > 0) {
            mLabelView = (TextView) findViewById(mLabelViewId);
            WidgetUtils.setTextOf(mLabelView, String.format(mLabelFormat, mLabelText));
        } else {
            mCheckBox.setText(String.format(mLabelFormat, mLabelText));
        }
        //Update left padding of editText.
    }

    @Nullable @Override public String getLabel() {
        return mLabelText;
    }

    @Override public void setHelper(final String helperText, @ColorRes int colorRes) {
        mHelperText = helperText;
        if (mHelperView == null && mHelperViewId > 0) {
            mHelperView = (TextView) findViewById(mHelperViewId);
        }
        ;
        WidgetUtils.setTextOf(mHelperView, mHelperText);
        if (colorRes > 0 && mHelperView != null) {
            mHelperView.setTextColor(ContextCompat.getColor(getContext(), colorRes));
        }
    }

    @Override public void showHideHelper(final boolean visible) {
        WidgetUtils.setVisibilityOf(mHelperView, visible);
    }

    @Nullable @Override public String getHelper() {
        return mHelperText;
    }

    @Override public int getHelperViewId() {
        return mHelperViewId;
    }

    @Override public int getLabelViewId() {
        return mLabelViewId;
    }

    @Override public String getKey() {
        return mKey;
    }

    public void setLabelColor(@ColorInt int color) {
        if (mLabelView != null) {
            mLabelView.setTextColor(color);
        } else {
            getCheckBox().setTextColor(color);
        }
    }

    @Override public void setOnClickListener(final OnClickListener l) {
        getCheckBox().setOnClickListener(l);
    }

    public Paint getLabelTextPaint() {
        return mLabelTextPaint;
    }
}