/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.commons.helpers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * @author László Gálosi
 * @since 09/11/15
 */
public class ClearLeakableHelper {
    private static final Field TEXT_LINE_CACHED;

    static {
        Field textLineCached = null;
        try {
            textLineCached = Class.forName("android.text.TextLine").getDeclaredField("sCached");
            textLineCached.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        TEXT_LINE_CACHED = textLineCached;
    }

    /**
     * Helper method to clear this leakanle.
     * TextLine.sCached is a pool of 3 TextLine instances. TextLine.recycle() has had at least two
     * bugs that created memory leaks by not correctly clearing the recycled TextLine instances.
     * The first was fixed in android-5.1.0_r1:
     * https://github.com/android/platform_frameworks_base/commit
     * /893d6fe48d37f71e683f722457bea646994a10bf
     * <p/>
     * The second was fixed, not released yet:
     * https://github.com/android/platform_frameworks_base/commit
     * /b3a9bc038d3a218b1dbdf7b5668e3d6c12be5ee4
     * <p/>
     * Hack: to fix this, you could access TextLine.sCached and clear the pool every now and then
     * (e.g. on activity destroy).
     * See:http://stackoverflow.com/questions/30397356/android-memory-leak-on-textview-leakcanary
     * -leak-can-be-ignored
     */
    public static void clearTextLineCache() {
        // If the field was not found for whatever reason just return.
        if (TEXT_LINE_CACHED == null) {
            return;
        }

        Object cached = null;
        try {
            // Get reference to the TextLine sCached array.
            cached = TEXT_LINE_CACHED.get(null);
        } catch (Exception ex) {
            //
        }
        if (cached != null) {
            // Clear the array.
            for (int i = 0, size = Array.getLength(cached); i < size; i++) {
                Array.set(cached, i, null);
            }
        }
    }

    private ClearLeakableHelper() {
    }
}
