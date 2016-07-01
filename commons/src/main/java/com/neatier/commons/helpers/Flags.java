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

/**
 * Created by László Gálosi on 17/04/16
 */
public class Flags {
    public static boolean isSet(int flags, int flag) {
        return (flags & flag) != 0;
    }

    public static int addFlag(int flags, int flag) {
        return flags |= flag;
    }

    public static int clearFlag(int flags, int flag) {
        return flags & ~flag;
    }

    public static boolean isValidFlagsOf(int flags, int... validFlags) {
        int allFlags = 0;
        int len = validFlags.length;
        for (int i = 0; i < len; i++) {
            allFlags |= validFlags[i];
        }
        return ((flags & allFlags) == flags);
    }
}
