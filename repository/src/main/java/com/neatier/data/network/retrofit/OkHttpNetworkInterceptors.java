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

package com.neatier.data.network.retrofit;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for using {@link OkHttpClient.Builder#addNetworkInterceptor(Interceptor)}
 */
@Documented
@Qualifier
@Retention(RUNTIME)
public @interface OkHttpNetworkInterceptors {
}
