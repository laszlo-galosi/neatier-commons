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

package com.neatier.commons.exception;

/**
 * Created by László Gálosi on 30/07/15
 */
public class ErrorBundleException extends Exception {
    public ErrorBundleException() {
        super();
    }

    public ErrorBundleException(final String message) {
        super(message);
    }

    public ErrorBundleException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ErrorBundleException(final Throwable cause) {
        super(cause);
    }

    public static ErrorBundleException wrap(Throwable t) {
        return new ErrorBundleException(t.getMessage(), t);
    }
}
