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
 * An {@link ErrorBundleException} subclass for presenting an error message when network connection
 * related errors occurs.
 *
 * @author László Gálosi
 * @since 21/09/17
 */
public class NetworkConnectionException extends ErrorBundleException {
    public NetworkConnectionException() {
        super();
    }

    /**
     * Constructor with the given error message.
     *
     * @see Exception#Exception(String)
     */
    public NetworkConnectionException(final String message) {
        super(message);
    }

    /**
     * Construction with the given error message and error cause.
     *
     * @see Exception#Exception(String, Throwable)
     */
    public NetworkConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with the given error cause.
     *
     * @see Exception#Exception(Throwable)
     */
    public NetworkConnectionException(final Throwable cause) {
        super(cause);
    }
}
