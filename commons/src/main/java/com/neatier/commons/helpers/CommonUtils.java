package com.neatier.commons.helpers;

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
}