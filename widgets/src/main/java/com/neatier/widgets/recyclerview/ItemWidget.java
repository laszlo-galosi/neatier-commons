/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions
  *  Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.widgets.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import com.fernandocejas.arrow.optional.Optional;
import com.neatier.widgets.Bindable;
import com.neatier.widgets.R;
import com.neatier.widgets.ThemeUtil;
import com.neatier.widgets.helpers.WidgetUtils;

/**
 * A custom {@link Bindable} {@link LinearLayout} with custom item height and background color and
 * supporting
 * {@link OnClickListener} and {@link OnLongClickListener}s
 *
 * @author László Gálosi
 * @since 31/10/15
 */
public class ItemWidget extends LinearLayout implements Bindable {

    private @LayoutRes int mContentLayout = 0;
    private int mLayoutHeight = android.R.attr.listPreferredItemHeight;
    private boolean mContentClickable = true;
    private boolean mContentLongClickable = true;
    private OnClickListener mClickListener;
    private OnLongClickListener mLongClickListener;
    private int mBackgroundColor = R.color.translucent_100;

    private Optional<Object> mBindedData = Optional.absent();
    private Optional<View> mContentView = Optional.absent();
    private int mBackgroundDrawable;

    public ItemWidget(Context context) {
        super(context);
    }

    public ItemWidget(@LayoutRes int contentLayout, int layoutHeight, Context context) {
        super(context);
        mContentLayout = contentLayout;
        mLayoutHeight = layoutHeight;
    }

    public ItemWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    @SuppressLint("ResourceAsColor") @SuppressWarnings("deprecation")
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemWidget, defStyleAttr,
                                                      defStyleRes);

        mContentLayout = a.getInteger(R.styleable.ItemWidget_iw_contentLayout,
                                      R.layout.list_item_default);
        mContentClickable = a.getBoolean(R.styleable.ItemWidget_iw_clickableContent, true);
        mLayoutHeight = a.getInteger(R.styleable.ItemWidget_iw_layoutHeight, 48);
        mBackgroundColor = a.getColor(R.styleable.ItemWidget_iw_contentBackground,
                                      getContext().getResources()
                                                  .getColor(R.color.translucent_100));
        a.recycle();
        initView(getContext());
    }

    @SuppressWarnings("deprecation")
    public void initView(Context context) {
        if (mContentLayout == 0) {
            throw new IllegalArgumentException("You must specify li_contentLayout parameter");
        }
        mContentView = Optional.of(LayoutInflater.from(context).inflate(mContentLayout, null));
        if (mContentView.isPresent()) {
            if (mBackgroundColor > 0) {
                int bgColor = context.getResources().getColor(mBackgroundColor);
                mContentView.get().setBackgroundColor(bgColor);
            }
            if (mBackgroundDrawable > 0) {
                Drawable drawable = context.getResources().getDrawable(mBackgroundDrawable);
                mContentView.get().setBackgroundDrawable(drawable);
            }
            addView(mContentView.get());
        }
        MarginLayoutParams layoutParams = new MarginLayoutParams(
              LayoutParams.MATCH_PARENT,
              ThemeUtil.dpToPx(getContext(), mLayoutHeight));

        WidgetUtils.setLayoutSizeOf(mContentView.get(), LayoutParams.MATCH_PARENT,
                                    ThemeUtil.dpToPx(getContext(), mLayoutHeight));

        //layoutParams.leftMargin = ThemeUtil.dpToPx(getContext(), R.dimen.material_padding);
        //layoutParams.rightMargin = ThemeUtil.dpToPx(getContext(), R.dimen.material_padding);
        //setLayoutParams(layoutParams);
    }

    public ItemWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public ItemWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override public void bind(final Object data) {
        mBindedData = Optional.fromNullable(data);
    }

    @Override public Optional getData() {
        return mBindedData;
    }

    @Override public void setOnClickListener(final OnClickListener l) {
        if (mContentClickable && mContentView.isPresent()) {
            mContentView.get().setOnClickListener(l);
            mContentView.get().setTag(this);
        }
    }

    @Override public void setOnLongClickListener(final OnLongClickListener l) {
        if (mContentLongClickable && mContentView.isPresent()) {
            mContentView.get().setOnLongClickListener(l);
            mContentView.get().setTag(this);
        }
    }

    public ItemWidget contentLayout(final int contentLayout) {
        mContentLayout = contentLayout;
        return this;
    }

    public ItemWidget layoutHeight(final int layoutHeight) {
        mLayoutHeight = layoutHeight;
        return this;
    }

    public ItemWidget setContentClickable(final boolean contentClickable) {
        mContentClickable = contentClickable;
        return this;
    }

    public ItemWidget setContentLongClickable(final boolean contentLongClickable) {
        mContentLongClickable = contentLongClickable;
        return this;
    }

    public ItemWidget withBackgroundColor(final @ColorRes int backgroundColor) {
        mBackgroundColor = backgroundColor;
        return this;
    }

    public ItemWidget withBackgroundDrawable(final @DrawableRes int drawable) {
        mBackgroundDrawable = drawable;
        return this;
    }
}
