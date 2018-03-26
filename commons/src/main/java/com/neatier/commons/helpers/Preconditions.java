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
package com.neatier.commons.helpers;

import com.neatier.commons.exception.NullArgumentException;

/**
 * Helper class to perform conditional checks which always throws a {@link RuntimeException} when
 * the condition fails.
 */
public final class Preconditions {
    private Preconditions() {
        throw new AssertionError("No instances");
    }

    /**
     * Checks and returns the given value with Type T if it is not null.
     *
     * @param value the value to be checked
     * @param message the error message to be thrown when the value is null
     * @throws NullArgumentException thrown when the condition fails.
     */
    public static <T> T checkNotNull(T value, String message) {
        if (value == null) {
            throw new NullArgumentException(message);
        }
        return value;
    }

    /**
     * Checks and returns the given value with Type T if the given condition applies.
     *
     * @param condition the condition to be checked
     * @param message the error message to be thrown when the given condition not applies.
     * @throws IllegalArgumentException thrown when the condition fails.
     */
    public static void checkArgument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks and returns the given value with Type T if the given condition applies.
     *
     * @param condition the condition to be checked
     * @param message the error message to be thrown when the given condition not applies.
     * @throws IllegalStateException thrown when the condition fails.
     */
    public static void checkState(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Checks and returns the given value  if the it is an instance of the given class.
     *
     * @param value the value to be checked
     * @param message the error message to be thrown when the value is not an instance of the
     * given class.
     * @throws IllegalArgumentException thrown when the condition fails.
     */
    public static void checkClass(Object value, Class clazz, String message) {
        if (value.getClass() != clazz) {
            throw new IllegalArgumentException(message);
        }
    }
}
