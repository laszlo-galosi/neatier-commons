package com.neatier.commons.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

/**
 * Created by László Gálosi on 19/06/16
 */
public class CommonUtils {
    public static String cleanNewLine(final String text) {
        String newLine = System.getProperty("line.separator");
        String s = text.replaceAll("\\r\\n", newLine).replaceAll("\\t", " ");
        //Log.v("cleanNewLine", text, s);
        return s;
    }

    public static int compare(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static String colorIntToHex(int color) {
        return Integer.toHexString(color).toUpperCase();
    }

    public static String colorResToHex(@ColorRes int colorRes, Context context) {
        return Integer.toHexString(ContextCompat.getColor(context, colorRes)).toUpperCase();
    }

    public static boolean isResource(int resId, Context context) {
        try {
            context.getResources().getResourceName(resId);
            return true;
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }
}
