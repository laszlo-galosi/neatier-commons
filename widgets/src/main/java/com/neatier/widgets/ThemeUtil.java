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
package com.neatier.widgets;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import java.lang.reflect.InvocationTargetException;

/**
 * Helper class containing methods for theme based dimension computation.
 *
 * @author László Gálosi
 * @since 29/04/16
 */
@SuppressWarnings("SameParameterValue")
public class ThemeUtil {

    private static final int[]
            APPCOMPAT_CHECK_ATTRS = {
            android.R.attr.colorPrimary
    };
    private static TypedValue value;

    /**
     * Converts and returns the dimension in pixels computed from the given dimension in dip.
     *
     * @param dp the dimension in dip to be converted
     * @param context the context to accessing Resources
     */
    public static int dpToPx(Context context, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics()) + 0.5f);
    }

    /**
     * Converts and returns the dimension in dip computed from the given dimension in pixels.
     *
     * @param px the dimension in pixels to be converted
     * @param context the context to accessing Resources
     */
    public static int pxToDp(Context context, int px) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,
                context.getResources().getDisplayMetrics()) + 0.5f);
    }

    /**
     * Converts and returns the dimension in pixels computed from the given dimension in sp.
     *
     * @param sp the dimension in sp (scale dependent) to be converted.
     * @param context the context to accessing Resources
     */
    public static int spToPx(Context context, float sp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics()) + 0.5f);
    }

    /**
     * Converts and returns the given dimension resource in pixels.
     *
     * @param dimenRes the dimension resource to be converted.
     * @param context the context to accessing Resources
     */
    public static int dimenToPx(Context context, @DimenRes int dimenRes) {
        return context.getResources().getDimensionPixelSize(dimenRes);
    }

    /**
     * Converts and returns the given dimension resource in dip.
     *
     * @param dimenRes the dimension resource to be converted.
     * @param context the context to accessing Resources
     */
    public static int dimenToDp(Context context, @DimenRes int dimenRes) {
        Resources resources = context.getResources();
        return (int) (resources.getDimension(dimenRes) / resources.getDisplayMetrics().density);
    }

    /**
     * Converts and returns the given attribute resource in pixels.
     *
     * @param attrRes the dimension attribute resource
     * @param context the context to accessing Resources
     */
    public static int attrToPx(Context context, @AttrRes int attrRes) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[] { attrRes });
        int px = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return px;
    }

    /**
     * Returns a color for the given color resource or th given default value, if the color resource
     * cannot be resolved.
     *
     * @param context the context to accessing Resources
     * @param colorRes the color resource
     * @param defaultValue the default color as a fallback
     */
    @ColorInt public static int getColor(Context context, int colorRes,
            @ColorInt int defaultValue) {
        //noinspection EmptyCatchBlock
        try {
            return ContextCompat.getColor(context, colorRes);
        } catch (Resources.NotFoundException nfe) {

        }
        return defaultValue;
    }

    /**
     * Returns a color from the given color attribute.
     *
     * @param attr the styled attribute resource.
     * @param context the context to accessing Resources
     * @see Context#obtainStyledAttributes(int[])
     */
    public static int colorFromAttrRes(int attr, Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[] { attr });
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

    /**
     * Returns a float value from the given styled float attribute res.
     *
     * @param attrRes the float attribute resource
     * @param context the context to accessing Resources
     */
    public static float floatFromAttrRes(int attrRes, Context context) {
        TypedArray a = context.obtainStyledAttributes(new int[] { attrRes });
        try {
            return a.getFloat(0, 0);
        } finally {
            a.recycle();
        }
    }

    /**
     * Returns the tint mode of the given integer or the given default  mode if not found.
     *
     * @param value the tint mode as an int
     * @param defaultMode the default tint mode as a fallback
     */
    public static PorterDuff.Mode parseTintMode(int value, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3:
                return PorterDuff.Mode.SRC_OVER;
            case 5:
                return PorterDuff.Mode.SRC_IN;
            case 9:
                return PorterDuff.Mode.SRC_ATOP;
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            case 16:
                return PorterDuff.Mode.ADD;
            default:
                return defaultMode;
        }
    }

    /**
     * Returns the background color of the application's window or the given defaultValue
     *
     * @param context the context to accessing Resources
     * @param defaultValue the default color as  fallback.
     * @see #getThemeColor(Context, int, int)
     */
    public static int windowBackground(Context context, int defaultValue) {
        return getThemeColor(context, android.R.attr.windowBackground, defaultValue);
    }

    /**
     * Returns a color from the given color attribute resource a default color as a fallback, if the
     * attribute cannot be resolved.
     * * @param context
     */
    @SuppressWarnings("deprecation")
    private static int getThemeColor(Context context, int attrRes, int defaultValue) {
        if (value == null) {
            value = new TypedValue();
        }

        //noinspection EmptyCatchBlock
        try {
            Resources.Theme theme = context.getTheme();
            if (theme != null && theme.resolveAttribute(attrRes, value, true)) {
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

    /**
     * Returns {@link android.R.attr#textColorPrimary} as a color int or the given defaultValue if
     * the attribute cannot be resolved.
     */
    public static int textColorPrimary(Context context, int defaultValue) {
        return getThemeColor(context, android.R.attr.textColorPrimary, defaultValue);
    }

    /**
     * Returns {@link android.R.attr#textColorSecondary} as a color int or the given defaultValue if
     * the attribute cannot be resolved.
     */
    public static int textColorSecondary(Context context, int defaultValue) {
        return getThemeColor(context, android.R.attr.textColorSecondary, defaultValue);
    }

    /**
     * Checks whether the {@link #APPCOMPAT_CHECK_ATTRS} can be resolved from the given context or
     * throws an {@link IllegalArgumentException}
     *
     * @see Context#obtainStyledAttributes(int[])
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void checkAppCompatTheme(
            Context context) {
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

    /**
     * Returns the given resource id's name as a string.
     */
    public static String resourceName(Context context, int resId) {
        try {
            return context.getResources().getResourceEntryName(resId);
        } catch (Resources.NotFoundException nfe) {
            return String.valueOf(resId);
        }
}

    /**
     * Returns the device's navigation bar size as a Point.
     */
    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    /**
     * Returns the usable size in {@link Point} of the screen window. Navigation and status bar
     * areas are excluded.
     */
    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        Display display = windowManager.getDefaultDisplay();
        assert display != null;
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Returns the real size in {@link Point} of the screen window including navigation and status
     * bar areas.
     */
    @SuppressLint("ObsoleteSdkInt") public static Point getRealScreenSize(Context context) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        assert windowManager != null;
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
                //do nothing.
            } catch (InvocationTargetException e) {
                //do nothing
            } catch (NoSuchMethodException e) {
                //do nothing
            }
        }

        return size;
    }

    /**
     * Returns the navigation bar height in pixels.
     */
    public static int navigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * Returns the status bar height in pixels.
     */
    public static int statusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}

