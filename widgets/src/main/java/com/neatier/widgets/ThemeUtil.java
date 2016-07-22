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
import android.support.annotation.DimenRes;
import android.util.TypedValue;

/**
 * Created by László Gálosi on 29/04/16
 */
public class ThemeUtil {
    private static TypedValue value;

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

    @SuppressWarnings("deprecation")
    private static int getColor(Context context, int id, int defaultValue) {
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
        return getColor(context, android.R.attr.windowBackground, defaultValue);
    }

    public static int textColorPrimary(Context context, int defaultValue) {
        return getColor(context, android.R.attr.textColorPrimary, defaultValue);
    }

    public static int textColorSecondary(Context context, int defaultValue) {
        return getColor(context, android.R.attr.textColorSecondary, defaultValue);
    }
}
