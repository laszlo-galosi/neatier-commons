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

package com.neatier.data.network;

import androidx.annotation.NonNull;
import java.util.concurrent.atomic.AtomicReference;
import okhttp3.HttpUrl;

/**
 * A server end point url implementation allows us easily change base url.
 * Useful for ex. in the integration and functional tests.
 */
public class ChangeableBaseUrl {

    /**
     * The mutable base url of the server.
     */
    @NonNull
    private final AtomicReference<HttpUrl> baseUrl;

    public ChangeableBaseUrl(@NonNull String baseUrl) {
        this.baseUrl = new AtomicReference<>(HttpUrl.parse(baseUrl));
    }

    /**
     * Sets the baseUrl to the given value.
     */
    public void setBaseUrl(@NonNull String baseUrl) {
        this.baseUrl.set(HttpUrl.parse(baseUrl));
    }

    /**
     * Returns the current base url.
     */
    public HttpUrl getUrl() {
        return baseUrl.get();
    }
}
