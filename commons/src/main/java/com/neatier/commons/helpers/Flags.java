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
 * Helper class containing methods for examining and modifying int flags.
 *
 * @author László Gálosi
 * @since 17/04/16
 */
public class Flags {

    /**
     * Return true if the given flag  is set in the flags to be examined.
     *
     * @param flags the flags to be examined
     * @param flag the flag to be determined if it set or not
     */
    public static boolean isSet(int flags, int flag) {
        return (flags & flag) != 0;
    }

    /**
     * Set the given flag in the given flags to be changes.
     *
     * @param flags the flag which value to be changed.
     * @param flag the flag to be set
     */
    public static int addFlag(int flags, int flag) {
        return flags |= flag;
    }

    /**
     * Unset the given flag in the given flags to be changed.
     *
     * @param flags the flag which value to be changed.
     * @param flag the flag to be unset
     */
    public static int clearFlag(int flags, int flag) {
        return flags & ~flag;
    }

    /**
     * Returns true if the given flags value contains only the given valid flags
     *
     * @param flags the flags which value to be examined
     * @param validFlags array of flags with the valid value set.
     */
    public static boolean isValidFlagsOf(int flags, int... validFlags) {
        int allFlags = 0;
        int len = validFlags.length;
        for (final int validFlag : validFlags) {
            allFlags |= validFlag;
        }
        return ((flags & allFlags) == flags);
    }
}
