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

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.InputType;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.widget.EditText;

/**
 * Helper class used for {@link EditText} with {@link InputType#TYPE_TEXT_VARIATION_PASSWORD}
 * to set the typeface of the hint text.
 *
 * @author László Gálosi
 * @since 28/02/18
 */
public class CustomTypefaceSpan extends TypefaceSpan {
    private final Typeface mNewType;

    public CustomTypefaceSpan(Typeface type) {
        super("");
        mNewType = type;
    }

    public CustomTypefaceSpan(String family, Typeface type) {
        super(family);
        mNewType = type;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, mNewType);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, mNewType);
    }

    private static void applyCustomTypeFace(Paint paint, Typeface tf) {
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
}
