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
 * An {@link ErrorBundleException} subclass for presenting an error message when something
 * unexpected happens.
 *
 * @author László Gálosi
 * @since 30/07/15
 */
public class InternalErrorException extends ErrorBundleException {
    public InternalErrorException() {
        super();
    }

    /**
     * Constructor with the given error message.
     *
     * @see Exception#Exception(String)
     */
    public InternalErrorException(final String message) {
        super(message);
    }

    /**
     * Construction with the given error message and error cause.
     *
     * @see Exception#Exception(String, Throwable)
     */
    public InternalErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with the given error cause.
     *
     * @see Exception#Exception(Throwable)
     */
    public InternalErrorException(final Throwable cause) {
        super(cause);
    }
}
