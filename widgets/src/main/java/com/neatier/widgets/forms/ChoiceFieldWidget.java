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
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.fernandocejas.arrow.collections.Lists;
import com.neatier.widgets.R;
import com.neatier.widgets.helpers.WidgetUtils;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;
import trikita.log.Log;

/**
 * Created by László Gálosi on 12/05/16
 */
public abstract class ChoiceFieldWidget extends FrameLayout
      implements HasInputField<String, Integer>, HasChoiceValue<Integer, Integer> {

    private final int mLabelTextColor;
    private final int mHelperTextColor;
    private String mLabelText;
    private String mLabelFormat = "%s";
    private String mHelperText;

    private boolean mShowHelper;
    private boolean mShowLabel;
    protected View mItemView;

    protected Integer mValue;

    private @LayoutRes int mLayoutRes;
    private @IdRes int mLabelViewId;
    private @IdRes int mHelperViewId;

    private String mKey;
    private TextView mLabelView;
    private TextView mHelperView;
    private Paint mLabelTextPaint;

    SparseArray<Integer> mChoiceIdMap = new SparseArray<>(5);
    SparseArray<Integer> mChoiceValueMap = new SparseArray<>(5);
    SparseArray<String> mChoiceNameMap = new SparseArray<>(5);

    public ChoiceFieldWidget(final Context context) {
        this(context, null);
    }

    public ChoiceFieldWidget(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChoiceFieldWidget(final Context context, final AttributeSet attrs,
          final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray prefa =
              context.obtainStyledAttributes(attrs, R.styleable.Preference, defStyleAttr, 0);
        TypedArray ifa =
              context.obtainStyledAttributes(attrs, R.styleable.HasInputField, defStyleAttr, 0);
        TypedArray wa =
              context.obtainStyledAttributes(attrs, R.styleable.ChoiceWidget, defStyleAttr, 0);

        mKey = prefa.getString(R.styleable.Preference_android_key);

        mLayoutRes = TypedArrayUtils.getResourceId(prefa, R.styleable.Preference_layout,
                                                   R.styleable.Preference_android_layout,
                                                   R.layout.widget_radiogroup_w_helper);
        mLabelText = ifa.getString(R.styleable.HasInputField_if_label);
        mLabelViewId =
              ifa.getResourceId(R.styleable.HasInputField_if_labelViewId, 0);
        mHelperText = ifa.getString(R.styleable.HasInputField_if_helper);
        mHelperViewId =
              ifa.getResourceId(R.styleable.HasInputField_if_helperViewId, 0);
        mShowLabel = ifa.getBoolean(R.styleable.HasInputField_if_showLabel, true);
        mShowHelper = ifa.getBoolean(R.styleable.HasInputField_if_showHelper, true);
        mLabelTextColor = ifa.getColor(R.styleable.HasInputField_if_labelTextColor,
                                       ContextCompat.getColor(getContext(),
                                                              R.color.colorTextPrimary));
        mHelperTextColor = ifa.getColor(R.styleable.HasInputField_if_helperTextColor,
                                        ContextCompat.getColor(getContext(),
                                                               R.color.colorTextSecondary));
        if (ifa.hasValue(R.styleable.HasInputField_if_labelFormat)) {
            mLabelFormat = ifa.getString(R.styleable.HasInputField_if_labelFormat);
        }
        initKeysAndValues(wa);
        prefa.recycle();
        ifa.recycle();
        wa.recycle();
        initView(context);
    }

    protected void initKeysAndValues(TypedArray attr) {
        int choiceIdsRes = attr.getResourceId(R.styleable.ChoiceWidget_cw_choiceIds, 0);
        int choiceNamesRes = attr.getResourceId(R.styleable.ChoiceWidget_cw_choiceNames, 0);
        int choiceValuesRes = attr.getResourceId(R.styleable.ChoiceWidget_cw_choiceValues, 0);
        if (!isInEditMode()) {
            TypedArray choiceIds = getContext().getResources().obtainTypedArray(choiceIdsRes);
            TypedArray choiceNames = getContext().getResources().obtainTypedArray(choiceNamesRes);
            TypedArray choiceValues = getContext().getResources().obtainTypedArray(choiceValuesRes);
            mChoiceIdMap = createSparseArray(choiceIds, choiceIdTypedValueType(), i -> i);
            mChoiceNameMap = createSparseArray(choiceNames, TypedValue.TYPE_STRING, keyAtFunc());
            mChoiceValueMap =
                  createSparseArray(choiceValues, choiceValueTypedValueType(), keyAtFunc());
        } else {
            int[] choiceIds = getContext().getResources().getIntArray(choiceIdsRes);
            int[] choiceValues = getContext().getResources().getIntArray(choiceValuesRes);
            String[] choiceNames = getContext().getResources().getStringArray(choiceNamesRes);
            mChoiceIdMap = createSparseArrayInEditMode(Lists.newArrayList(choiceIds), i -> i);
            mChoiceNameMap =
                  createSparseArrayInEditMode(Lists.newArrayList(choiceNames), keyAtFunc());
            mChoiceValueMap =
                  createSparseArrayInEditMode(Lists.newArrayList(choiceValues), keyAtFunc());
        }
    }

    protected SparseArray createSparseArray(@Nullable final TypedArray typedArray,
          int typedValueType, Func1<Integer, Integer> keyAtFunc) {
        int len = typedArray != null ? typedArray.length() : 0;
        SparseArray array = new SparseArray<>(len);
        Observable.range(0, len)
                  .subscribe(i -> {
                      int resId = typedArray.getResourceId(i, -1);
                      switch (typedValueType) {
                          case TypedValue.TYPE_STRING:
                              array.put(keyAtFunc.call(i), typedArray.getString(i));
                              break;
                          case TypedValue.TYPE_FLOAT:
                              array.put(keyAtFunc.call(i), typedArray.getFloat(i, 0.0f));
                              break;
                          case TypedValue.TYPE_INT_BOOLEAN:
                              array.put(keyAtFunc.call(i), typedArray.getBoolean(i, Boolean.FALSE));
                              break;
                          default:
                              array.put(keyAtFunc.call(i), typedArray.getInt(i, 0));
                      }
                  });
        return array;
    }

    protected SparseArray createSparseArrayInEditMode(@Nullable final List<?> typedList,
          Func1<Integer, Integer> keyAtFunc) {
        int len = typedList != null ? typedList.size() : 0;
        SparseArray array = new SparseArray<>(len);
        for (int i = 0; i < len; i++) {
            array.put(keyAtFunc.call(i), typedList.get(i));
        }
        return array;
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
                mHelperView.setTextColor(mHelperTextColor);
            }
            if (mLabelViewId > 0) {
                mLabelView = (TextView) mItemView.findViewById(mLabelViewId);
                initLabelPaint();
                mLabelView.setTextColor(mLabelTextColor);
                setLabel(mLabelText);
            }
            WidgetUtils.setVisibilityOf(mHelperView, mShowHelper);
            WidgetUtils.setVisibilityOf(mLabelView, mShowLabel);
            mItemView.setClickable(false);
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
        mLabelTextPaint.setColor(mLabelTextColor);
        if (mLabelView != null) {
            mLabelTextPaint.setTextSize(mLabelView.getTextSize());
        }
        mLabelTextPaint.setAntiAlias(true);
    }

    @Override public SparseArray<Integer> choiceIds() {
        return mChoiceIdMap;
    }

    @Override public SparseArray<Integer> choiceValues() {
        return mChoiceValueMap;
    }

    @Override public SparseArray<String> choiceNames() {
        return mChoiceNameMap;
    }

    @Override public Integer valueByKey(final Integer key) {
        return mChoiceValueMap.get(key);
    }

    @Override public String nameByKey(final Integer key) {
        return mChoiceNameMap.get(key);
    }

    @Override public int indexByKey(final Integer key) {
        return mChoiceIdMap.indexOfKey(key);
    }

    @Override public Integer keyAt(final int index) {
        return mChoiceIdMap.get(index);
    }

    @Override public Integer getValue() {
        Log.d("getValue", getKey());
        return mValue;
    }

    @Override public void setValue(final Integer value) {
        Log.d("setValue", getKey(), value);
        mValue = value;
    }

    public abstract int choiceIdTypedValueType();

    public abstract int choiceValueTypedValueType();

    public abstract Func1<Integer, Integer> keyAtFunc();

    @Override public void setLabel(final String labelText) {
        mLabelText = labelText;
        if (mLabelView == null && mLabelViewId > 0) {
            mLabelView = (TextView) findViewById(mLabelViewId);
        }
        WidgetUtils.setTextOf(mLabelView, String.format(mLabelFormat, mLabelText));
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
}