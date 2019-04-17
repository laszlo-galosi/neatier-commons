/*
 * Copyright (C) 2018 Extremenet Ltd., All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *  All information contained herein is, and remains the property of Extremenet Ltd.
 *  The intellectual and technical concepts contained herein are proprietary to Extremenet Ltd.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 * Extremenet Ltd.
 */

package com.neatier.widgets.helpers;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.fernandocejas.arrow.checks.Preconditions;
import com.neatier.commons.helpers.KeyValuePairs;
import com.neatier.widgets.ThemeUtil;

/**
 * Contains builder-like helper methods for crating {@link Spannable}s using with a text view.
 *
 * @author László Gálosi
 * @since 05/09/17
 */
public class Spannables {

    /**
     * The context.
     */
    @NonNull private final Context mContext;

    /**
     * The string from the {@link SpannableStringBuilder} is created.
     */
    private final String mTextToSpan;

    /**
     * Map containing span text - span range - pairs for creating {@link SpannableString}s.
     *
     * @see SpannableString#setSpan(Object, int, int, int)
     */
    private KeyValuePairs<String, Pair<Integer, Integer>> mSpanRanges = new KeyValuePairs<>();

    /**
     * Map containing span text - color int value-pairs for creating {@link ForegroundColorSpan}s.
     */
    private KeyValuePairs<String, Integer> mForegroundColors = new KeyValuePairs<>(2);

    /**
     * Map containing span text - color int value-pairs for creating {@link BackgroundColorSpan}s.
     */
    private KeyValuePairs<String, Integer> mTypeFaces = new KeyValuePairs<>(2);

    /**
     * Map containing span text - clicklistener int value-pairs for creating {@link ClickableSpan}s.
     */
    private KeyValuePairs<String, View.OnClickListener> mClickables = new KeyValuePairs<>(2);

    /**
     * Map containing span text - type face int value-pairs for creating {@link StyleSpan}s.
     */
    private KeyValuePairs<String, Integer> mBackgroundColors = new KeyValuePairs<>(2);

    /**
     * Map containing span text - relative size float value-pairs for creating {@link
     * RelativeSizeSpan}s.
     */
    private KeyValuePairs<String, Float> mRelativeSizeSpans = new KeyValuePairs<>(2);

    private Spannables(String textToSpan, @NonNull Context context) {
        mContext = context;
        mTextToSpan = textToSpan;
    }

    /**
     * Intance creator method with the given text to split.
     */
    public static Spannables with(@StringRes int textRes, @NonNull Context context) {
        return new Spannables(context.getString(textRes), context);
    }

    /**
     * Intance creator method with the given text to split.
     */
    public static Spannables with(String textToSpan, @NonNull Context context) {
        return new Spannables(textToSpan, context);
    }

    /**
     * Builder like method for adding a new sub span, from the given sub string resource.
     * <p>The text to be spanned should be set previously.
     */
    public Spannables subSpan(@StringRes int substringRes) {
        Preconditions.checkNotNull(mContext, "Context not initialized.");
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        return subSpan(mContext.getString(substringRes));
    }

    /**
     * Builder like method for adding a new sub span, from the given sub string.
     * <p>The text to be spanned should be set previously.
     */
    public Spannables subSpan(String substring) {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        int start = Math.max(0, mTextToSpan.indexOf(substring));
        int end = Math.min(mTextToSpan.length(), start + substring.length());
        mSpanRanges.put(substring, new Pair<>(start, end));
        return this;
    }

    /**
     * Builder like method for adding a new sub span, from the given start and end range.
     * <p>The text to be spanned should be set previously.
     */
    public Spannables subSpan(int start, int end) {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        String substring = mTextToSpan.substring(Math.max(0, start),
                Math.min(mTextToSpan.length(), end));
        return subSpan(substring);
    }

    /**
     * Builder like method for adding a {@link ForegroundColorSpan}, for the given sub string with
     * the given color.
     * <p>The text to be spanned should be set previously.
     */
    public Spannables foregroundRes(String substring, @ColorRes int colorRes) {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        Preconditions.checkArgument(mSpanRanges.containsKey(substring),
                String.format("No sub span added yet:%s", substring));
        int defaultColor = ThemeUtil.colorFromAttrRes(
              androidx.appcompat.R.attr.colorControlNormal, mContext);
        return foreground(substring, ThemeUtil.getColor(mContext, colorRes, defaultColor));
    }

    /**
     * Builder like method for adding a {@link ForegroundColorSpan}, for the given sub string with
     * the given color.
     * <p>The text to be spanned should be set previously.
     */
    public Spannables foreground(String substring, @ColorInt int color) {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        Preconditions.checkArgument(mSpanRanges.containsKey(substring),
                String.format("No sub span added yet:%s", substring));
        mForegroundColors.put(substring, color);
        return this;
    }

    /**
     * Builder like method for adding a {@link BackgroundColorSpan}, for the given sub string with
     * the given color.
     * <p>The text to be spanned should be set previously.
     */
    public Spannables backgroundRes(String substring, @ColorRes int colorRes) {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        Preconditions.checkArgument(mSpanRanges.containsKey(substring),
                String.format("No sub span added yet:%s", substring));
        int defaultColor = ThemeUtil.colorFromAttrRes(
              androidx.appcompat.R.attr.colorPrimary, mContext);
        return background(substring, ThemeUtil.getColor(mContext, colorRes, defaultColor));
    }

    /**
     * Builder like method for adding a {@link BackgroundColorSpan}, for the given sub string with
     * the given color.
     * <p>The text to be spanned should be set previously.
     */
    public Spannables background(String substring, @ColorInt int color) {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        Preconditions.checkArgument(mSpanRanges.containsKey(substring),
                String.format("No sub span added yet:%s", substring));
        mBackgroundColors.put(substring, color);
        return this;
    }

    /**
     * Builder like method for adding a {@link StyleSpan} with the given type face.
     * the given color.
     * <p>The text to be spanned should be set previously.
     */
    public Spannables typeface(String substring, int typeFace) {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        Preconditions.checkArgument(mSpanRanges.containsKey(substring),
                String.format("No sub span added yet:%s", substring));
        mTypeFaces.put(substring, typeFace);
        return this;
    }

    /**
     * Builder like method for adding a {@link ClickableSpan} with the given click listener.
     * <p>The text to be spanned should be set previously. In order the {@link
     * ClickableSpan#onClick(View)} is to be called, don't forget to {@link
     * TextView#setMovementMethod(MovementMethod)} to {@link LinkMovementMethod#getInstance()} for
     * the TextView for which the the Spannable is
     * applied.
     */
    public Spannables clickable(String substring, View.OnClickListener clickListener) {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        Preconditions.checkArgument(mSpanRanges.containsKey(substring),
                String.format("No sub span added yet:%s", substring));
        mClickables.put(substring, clickListener);
        return this;
    }

    /**
     * Builder like method for adding a {@link RelativeSizeSpan} with the given relative size.
     * <p>The text to be spanned should be set previously.
     * applied.
     *
     * @param relativeSize the proportion of the TextView's font size.
     * @see RelativeSizeSpan#RelativeSizeSpan(float)
     */
    public Spannables relativeSize(String substring, Float relativeSize) {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        Preconditions.checkArgument(mSpanRanges.containsKey(substring),
                String.format("No sub span added yet:%s", substring));
        mRelativeSizeSpans.put(substring, relativeSize);
        return this;
    }

    /**
     * Creates and returns a SpannableStringBuilder to use with a {@link TextView}.
     */
    public SpannableStringBuilder build() {
        Preconditions.checkNotNull(mTextToSpan, "Spannable text not initialized.");
        SpannableStringBuilder ssb = new SpannableStringBuilder(mTextToSpan);
        if (mSpanRanges.isEmpty()) {
            subSpan(mTextToSpan);
        }
        int defaultFgColor = ThemeUtil.colorFromAttrRes(
              androidx.appcompat.R.attr.colorControlNormal, mContext);
        mSpanRanges.keysAsStream()
                .subscribe(key -> {
                    Pair<Integer, Integer> range = mSpanRanges.get(key);
                    Integer fgColor = mForegroundColors.get(key);
                    Integer bgColor = mBackgroundColors.get(key);
                    Integer typeFace = mTypeFaces.get(key);
                    Float relativeSize = mRelativeSizeSpans.get(key);
                    View.OnClickListener onClick = mClickables.get(key);
                    if (fgColor == null) {
                        foreground(key, defaultFgColor);
                        fgColor = mForegroundColors.get(key);
                    }
                    ssb.setSpan(new ForegroundColorSpan(fgColor), range.first,
                            range.second, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    if (bgColor != null) {
                        ssb.setSpan(new BackgroundColorSpan(bgColor), range.first,
                                range.second, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                    if (typeFace != null) {
                        ssb.setSpan(new StyleSpan(typeFace), range.first,
                                range.second, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                    if (relativeSize != null) {
                        ssb.setSpan(new RelativeSizeSpan(relativeSize), range.first,
                                range.second, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                    if (onClick != null) {
                        ssb.setSpan(new ClickableSpan() {
                                        @Override public void onClick(View v) {
                                            onClick.onClick(v);
                                        }
                                    }, range.first,
                                range.second, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                });
        return ssb;
    }
}

