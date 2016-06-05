/*
 * Copyright (C) 2015 Dr. Krisztian Balazs, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * All information contained herein is, and remains the property of Dr. Krisztian Balazs.
 * The intellectual and technical concepts contained herein are proprietary to Dr. Krisztian
 * Balazs and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 * by trade secret or copyright law. Dissemination of this information or reproduction of
 * this material is strictly forbidden unless prior written permission is obtained from
 * Dr. Krisztian Balazs.
 */

package com.neatier.commons.exception;

/**
 * Created by Krisztian on 4/9/2015.
 */
public class TimeFormatException extends IllegalArgumentException {
    public TimeFormatException() {
        super("Wrong time format.");
    }
}
