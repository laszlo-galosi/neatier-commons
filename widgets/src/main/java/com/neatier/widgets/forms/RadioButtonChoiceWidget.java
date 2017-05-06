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
import android.content.res.TypedArray;
import android.databinding.BindingMethod;
import android.databinding.BindingMethods;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.RadioGroup;
import com.neatier.widgets.R;
import rx.functions.Func1;

/**
 * Created by László Gálosi on 26/04/17
 */
@BindingMethods(
      @BindingMethod(type = RadioButtonChoiceWidget.class, attribute = "rbcw_onCheckedChanged",
                     method = "setOnCheckedChangeListener")
)
public class RadioButtonChoiceWidget extends ChoiceFieldWidget {

    @IdRes private int mButtonGroupId;
    @LayoutRes private final int mButtonLayoutId;
    RadioGroup mRadioGroup;

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener;

    public RadioButtonChoiceWidget(final Context context) {
        this(context, null);
    }

    public RadioButtonChoiceWidget(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioButtonChoiceWidget(final Context context, final AttributeSet attrs,
          final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray wa =
              context.obtainStyledAttributes(attrs, R.styleable.RadioButtonChoiceWidget,
                                             defStyleAttr, 0);
        mButtonGroupId =
              wa.getResourceId(R.styleable.RadioButtonChoiceWidget_rbcw_buttonGroupId, 0);
        mButtonLayoutId =
              wa.getResourceId(R.styleable.RadioButtonChoiceWidget_rbcw_buttonLayout, 0);
        if (wa.hasValue(R.styleable.RadioButtonChoiceWidget_rbcw_selectedValue)) {
            mValue = wa.getInt(R.styleable.RadioButtonChoiceWidget_rbcw_selectedValue, 0);
        }
        wa.recycle();
        if (mButtonGroupId == 0 || mButtonLayoutId == 0) {
            return;
        }
        mRadioGroup = (RadioGroup) mItemView.findViewById(mButtonGroupId);
        for (int i = 0, len = choiceIds().size(); i < len; i++) {
            Integer key = keyAtFunc().call(i);
            AppCompatRadioButton radioButton =
                  (AppCompatRadioButton) LayoutInflater.from(getContext())
                                                       .inflate(mButtonLayoutId, null,
                                                                false);
            new AppCompatRadioButton(context, attrs, defStyleAttr);
            radioButton.setId(key);
            radioButton.setText(nameByKey(key));
            radioButton.setChecked(valueByKey(key).equals(mValue));
            mRadioGroup.addView(radioButton);
        }
        setValue(mValue);
    }

    @Override public int choiceIdTypedValueType() {
        return TypedValue.TYPE_INT_DEC;
    }

    @Override public int choiceValueTypedValueType() {
        return TypedValue.TYPE_INT_DEC;
    }

    @Override public Func1<Integer, Integer> keyAtFunc() {
        return i -> choiceIds().keyAt(i);
    }

    public void setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mRadioGroup == null) {
            return;
        }
        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(group, checkedId);
            }
        });
    }

    @Override protected void onDetachedFromWindow() {
        mRadioGroup.setOnCheckedChangeListener(null);
        mOnCheckedChangeListener = null;
        super.onDetachedFromWindow();
    }
}
