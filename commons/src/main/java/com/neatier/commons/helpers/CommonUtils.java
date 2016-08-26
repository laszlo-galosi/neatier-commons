/*
 * Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 * All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions Kft.
 *  and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *  Delight Solutions Kft.
 */

package com.neatier.commons.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import java.util.Locale;
import java.util.StringTokenizer;

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

    public static Locale languageTagToLocale(String languageTag) {
        String langTag = languageTag;
        if (languageTag.contains("_")) {
            langTag = languageTag.replaceAll("_", "-");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Locale.forLanguageTag(langTag);
        }
        StringTokenizer tokenizer = new StringTokenizer(langTag, "-");
        return new Locale((String) tokenizer.nextElement(), (String) tokenizer.nextElement());
    }
}
