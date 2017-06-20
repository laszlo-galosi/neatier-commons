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
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.neatier.commons.helpers.Flags;
import com.neatier.commons.helpers.Preconditions;
import com.neatier.commons.helpers.RxUtils;
import com.neatier.widgets.R;
import com.neatier.widgets.ThemeUtil;
import com.neatier.widgets.helpers.WidgetUtils;
import java.util.concurrent.TimeUnit;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import trikita.log.Log;

/**
 * Created by László Gálosi on 12/05/16
 */
public class EditFieldWidget extends FrameLayout
      implements HasInputField<String, String> {
    public static final int DEFAULT_TEXTEVENT_FREQ = 400;
    protected final int mLabelTextColor;
    protected final int mHelperTextColor;
    protected final int mFieldTextColor;
    protected String mLabelFormat = "%s";
    protected float mInputTextSize;
    protected @ColorInt int mInputTextColor;
    protected String mLabelText;
    protected String mHelperText;

    protected int mFocusedFieldAlign = Gravity.LEFT;
    protected int mUnFocusedFieldAlign = Gravity.LEFT;

    protected boolean mShowHelper;
    protected boolean mShowLabel;
    protected boolean mHelperAsHint;
    protected boolean mLabelAsHint;

    private EditText mEditText;
    protected View mItemView;

    private Object mValue;
    private @LayoutRes int mLayoutRes;
    private @IdRes int mLabelViewId;
    private @IdRes int mHelperViewId;
    private String mKey;
    private TextView mLabelView;
    private TextView mHelperView;
    protected Paint mLabelTextPaint;

    protected int mDefaultFieldPaddingStart;

    public static final int FOCUS_FLAG_NONE = 1 << 0;
    public static final int FOCUS_FLAG_GRAVITY = 1 << 1;
    public static final int FOCUS_FLAG_LABEL_COLOR = 1 << 2;
    public static final int FOCUS_FLAG_EDIT_COLOR = 1 << 3;
    public static final int FOCUS_FLAG_SHOW_KEYBOARD = 1 << 4;
    public static final int FOCUS_FLAG_HIDE_KEYBOARD = 1 << 5;
    private int mFocusFlags = FOCUS_FLAG_NONE;
    private OnFocusChangeListener mFocusChangeListener;
    OnFocusChangeListener mDefaultFocusChangeListener = new OnFocusChangeListener() {
        @Override public void onFocusChange(final View v, final boolean hasFocus) {
            applyFocusFlags(hasFocus);
            int color = ContextCompat.getColor(getContext(), hasFocus ? R.color.colorAccent
                                                                      : R.color.colorTextPrimary);
            if (Flags.isSet(mFocusFlags, FOCUS_FLAG_EDIT_COLOR) && mEditText != null) {
                mEditText.setTextColor(color);
            }

            if (Flags.isSet(mFocusFlags, FOCUS_FLAG_LABEL_COLOR) && mLabelView != null) {
                mLabelView.setTextColor(color);
            }
            if (Flags.isSet(mFocusFlags, FOCUS_FLAG_SHOW_KEYBOARD) && hasFocus) {
                getEditText().post(() -> showSoftInput());
            }
            if (Flags.isSet(mFocusFlags, FOCUS_FLAG_HIDE_KEYBOARD) && !hasFocus) {
                getEditText().post(() -> hideSoftInput());
            }
            moveCursorToEnd();
        }
    };

    private boolean mMultiLine;
    private CompositeSubscription mSubscriptions;
    private boolean mIgnoreTextChange;

    public EditFieldWidget(final Context context) {
        this(context, null);
    }

    public EditFieldWidget(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditFieldWidget(final Context context, final AttributeSet attrs,
          final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray prefa =
              context.obtainStyledAttributes(attrs, R.styleable.Preference, defStyleAttr, 0);
        TypedArray ifa =
              context.obtainStyledAttributes(attrs, R.styleable.HasInputField, defStyleAttr, 0);
        TypedArray wa =
              context.obtainStyledAttributes(attrs, R.styleable.EditFieldWidget, defStyleAttr,
                                             0);

        mKey = prefa.getString(R.styleable.Preference_android_key);

        mLayoutRes = TypedArrayUtils.getResourceId(prefa, R.styleable.Preference_layout,
                                                   R.styleable.Preference_android_layout,
                                                   R.layout.widget_editfield_w_label);

        mFocusedFieldAlign =
              wa.getInteger(R.styleable.EditFieldWidget_ew_focusedFieldAlign, Gravity.LEFT);
        mUnFocusedFieldAlign =
              wa.getInteger(R.styleable.EditFieldWidget_ew_unfocusedFieldAlign, Gravity.LEFT);
        if (wa.hasValue(R.styleable.EditFieldWidget_ew_focusedFieldAlign)) {
            setFocusBehavior(null, FOCUS_FLAG_GRAVITY);
        }
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

        mLabelTextColor = ifa.getColor(R.styleable.HasInputField_if_labelTextColor,
                                       ContextCompat.getColor(getContext(),
                                                              R.color.colorTextPrimary));
        mHelperTextColor = ifa.getColor(R.styleable.HasInputField_if_helperTextColor,
                                        ContextCompat.getColor(getContext(),
                                                               R.color.colorTextSecondary));
        mFieldTextColor = ifa.getColor(R.styleable.HasInputField_if_fieldTextColor,
                                       ContextCompat.getColor(getContext(),
                                                              R.color.colorTextPrimary));
        mLabelAsHint = wa.getBoolean(R.styleable.EditFieldWidget_ew_labelAsHint, false);
        mHelperAsHint = wa.getBoolean(R.styleable.EditFieldWidget_ew_helperAsHint, false);
        mMultiLine = wa.getBoolean(R.styleable.EditFieldWidget_ew_multiLine, false);
        mValue = wa.getString(R.styleable.EditFieldWidget_value);
        prefa.recycle();
        ifa.recycle();
        wa.recycle();
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
                mHelperView.setTextColor(mHelperTextColor);
            }
            mEditText = (EditText) mItemView.findViewById(R.id.inputField);
            mEditText.setTextColor(mFieldTextColor);
            if (mLabelViewId > 0) {
                mLabelView = (TextView) mItemView.findViewById(mLabelViewId);
                mDefaultFieldPaddingStart = mEditText.getPaddingLeft();
                initLabelPaint();
                mLabelView.setTextColor(mLabelTextColor);
                setLabel(mLabelText);
            }
            if (mLabelAsHint) {
                mEditText.setHint(getLabel());
            }
            if (mHelperAsHint) {
                mEditText.setHint(getHelper());
            }
            WidgetUtils.setVisibilityOf(mHelperView, mShowHelper);
            WidgetUtils.setVisibilityOf(mLabelView, mShowLabel);
            WidgetUtils.setTextOf(mEditText, getValue());
            mItemView.setClickable(false);
            setFocusFlags(mFocusFlags);
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

    /**
     * Adds a #TextChangeObserver to the {@link EditText} of this preference.
     *
     * @return the subscriptions list.
     */
    public CompositeSubscription ensureWidgetSubs(TextChangeObserver textChangeObserver) {
        mSubscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(mSubscriptions);
        Subscription sub = RxTextView.textChangeEvents(mEditText)
                                     .filter(event -> !mIgnoreTextChange)
                                     .debounce(DEFAULT_TEXTEVENT_FREQ, TimeUnit.MILLISECONDS)
                                     .observeOn(AndroidSchedulers.mainThread())
                                     .subscribe(textChangeObserver);
        mSubscriptions.add(sub);
        return mSubscriptions;
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //mEditText.setOnFocusChangeListener(mFocusChangeListener);
    }

    @Override protected void onDetachedFromWindow() {
        RxUtils.unsubscribeIfNotNull(mSubscriptions);
        mEditText.setOnFocusChangeListener(null);
        super.onDetachedFromWindow();
    }

    public void ignoreTextChange(final boolean ignore) {
        mIgnoreTextChange = ignore;
    }

    public void moveCursorToEnd() {
        int length = getValue().length();
        getEditText().setSelection(length, length);
    }

    public EditText getEditText() {
        Preconditions.checkNotNull(mEditText,
                                   "InputField hasn't been initialized properly: "
                                         + getKey());
        return mEditText;
    }

    @Override public String getValue() {
        Log.v("getValue", getKey(), String.format("'%s'", getEditText().getText()));
        return getEditText().getText().toString();
    }

    @Override public void setValue(final String value) {
        Log.v("setValue", getKey(), String.format("'%s'", value));
        getEditText().setText(WidgetUtils.getTextData(value, getContext()));
    }

    @Override public void setLabel(final String labelText) {
        mLabelText = labelText;
        if (mLabelView == null) {
            mLabelView = (TextView) findViewById(mLabelViewId);
        }
        WidgetUtils.setTextOf(mLabelView, String.format(mLabelFormat, mLabelText));
        //Update left padding of editText.
        updatePaddings();
    }

    protected void updatePaddings() {
        Rect textBounds = new Rect();
        mLabelTextPaint.getTextBounds(mLabelText, 0, mLabelText.length(), textBounds);
        int leftPadding;
        if (isMultiLine()) {
            leftPadding = getEditText().getPaddingLeft();
        } else {
            leftPadding = mDefaultFieldPaddingStart
                  + mLabelView.getPaddingLeft()
                  + textBounds.right
                  + ThemeUtil.dpToPx(getContext(), 2);
        }
        getEditText().setPadding(leftPadding,
                                 getEditText().getPaddingTop(),
                                 getFieldPaddingRight(),
                                 getEditText().getPaddingBottom());
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

    public void setLayoutRes(final int layoutRes) {
        mLayoutRes = layoutRes;
        initView(getContext());
    }

    public void setFocusedFieldAlign(final int focusedFieldAlign) {
        mFocusedFieldAlign = focusedFieldAlign;
    }

    public void setUnfocusedFieldAlign(final int unFocusedFieldAlign) {
        mUnFocusedFieldAlign = unFocusedFieldAlign;
    }

    @Override public String getKey() {
        return mKey;
    }

    public void setMultiLine(final boolean multiLine) {
        mMultiLine = multiLine;
    }

    public boolean isMultiLine() {
        //return Flags.isSet(mEditText.getInputType(), EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        return mEditText.getMaxLines() > 1;
    }

    public int getFieldPaddingRight() {
        return mDefaultFieldPaddingStart;
    }

    public EditFieldWidget setFocusFlags(int focusFlags) {
        mFocusFlags = focusFlags;
        if (mEditText != null && mFocusFlags != FOCUS_FLAG_NONE) {
            mEditText.setOnFocusChangeListener(mFocusChangeListener);
        }
        return this;
    }

    public void setFocusBehavior(final @Nullable OnFocusChangeListener fl, int focusFlags) {
        mFocusChangeListener = (view, hasFocus) -> {
            if (mDefaultFocusChangeListener != null) {
                mDefaultFocusChangeListener.onFocusChange(view, hasFocus);
            }
            if (fl != null) {
                fl.onFocusChange(view, hasFocus);
            }
        };
        setFocusFlags(focusFlags);
    }

    public void setLabelColor(@ColorInt int color) {
        mLabelView.setTextColor(color);
    }

    public Paint getLabelTextPaint() {
        return mLabelTextPaint;
    }

    public void setKey(final String key) {
        mKey = key;
    }

    public static class TextChangeObserver implements Observer<TextViewTextChangeEvent> {
        @Override public void onCompleted() {
        }

        @Override public void onError(final Throwable e) {
            Log.e("onError", e);
        }

        @Override public void onNext(final TextViewTextChangeEvent event) {
            Log.d("onTextChangeEvent", "text", event.text()).v(event);
        }
    }

    public void showSoftInput() {
        InputMethodManager mgr = (InputMethodManager) getContext().getSystemService(
              Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(getEditText(), InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideSoftInput() {
        InputMethodManager mgr = (InputMethodManager) getContext().getSystemService(
              Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromInputMethod(getEditText().getWindowToken(),
                                         InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void applyFocusFlags(final boolean hasFocus) {
        if (Flags.isSet(mFocusFlags, FOCUS_FLAG_GRAVITY)) {
            mEditText.setGravity(hasFocus ? mFocusedFieldAlign : mUnFocusedFieldAlign);
        }
    }

}
