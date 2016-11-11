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

package com.neatier.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

/**
 * Created by László Gálosi on 29/04/16
 */
public class ThemeUtil {
    private static TypedValue value;

    private static final int[] APPCOMPAT_CHECK_ATTRS = {
          android.support.v7.appcompat.R.attr.colorPrimary
    };

    public static int dpToPx(Context context, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                                                context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public static int pxToDp(Context context, int px) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
                                                context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public static int spToPx(Context context, float sp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                                                context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public static int dimenToPx(Context context, @DimenRes int dimenRes) {
        return context.getResources().getDimensionPixelSize(dimenRes);
    }

    public static int dimenToDp(Context context, @DimenRes int dimenRes) {
        Resources resources = context.getResources();
        return (int) (resources.getDimension(dimenRes) / resources.getDisplayMetrics().density);
    }

    @ColorInt public static int getColor(Context context, int id, @ColorInt int defaultValue) {
        try {
            return ContextCompat.getColor(context, id);
        } catch (Resources.NotFoundException nfe) {
        }
        return defaultValue;
    }

    @SuppressWarnings("deprecation")
    private static int getThemeColor(Context context, int id, int defaultValue) {
        if (value == null) {
            value = new TypedValue();
        }

        try {
            Resources.Theme theme = context.getTheme();
            if (theme != null && theme.resolveAttribute(id, value, true)) {
                if (value.type >= TypedValue.TYPE_FIRST_INT
                      && value.type <= TypedValue.TYPE_LAST_INT) {
                    return value.data;
                } else if (value.type == TypedValue.TYPE_STRING) {
                    return context.getResources().getColor(value.resourceId);
                }
            }
        } catch (Exception ex) {
        }

        return defaultValue;
    }

    public static int windowBackground(Context context, int defaultValue) {
        return getThemeColor(context, android.R.attr.windowBackground, defaultValue);
    }

    public static int textColorPrimary(Context context, int defaultValue) {
        return getThemeColor(context, android.R.attr.textColorPrimary, defaultValue);
    }

    public static int textColorSecondary(Context context, int defaultValue) {
        return getThemeColor(context, android.R.attr.textColorSecondary, defaultValue);
    }

    public static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed = !a.hasValue(0);
        if (a != null) {
            a.recycle();
        }
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                                                     + "(or descendant) with the design library.");
        }
    }
}
