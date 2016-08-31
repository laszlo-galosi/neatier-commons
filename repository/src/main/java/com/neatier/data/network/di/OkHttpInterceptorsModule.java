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

package com.neatier.data.network.di;

import android.support.annotation.NonNull;
import com.neatier.data.network.retrofit.OkHttpInterceptors;
import com.neatier.data.network.retrofit.OkHttpNetworkInterceptors;
import dagger.Module;
import dagger.Provides;
import java.util.Collections;
import java.util.List;
import javax.inject.Singleton;
import okhttp3.Interceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import trikita.log.Log;

@Module
public class OkHttpInterceptorsModule {

    // Provided as separate dependency for Developer Settings to be able to change HTTP log level
    // at runtime.
    @Provides @Singleton @NonNull
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        return new HttpLoggingInterceptor(message -> Log.d(message));
    }

    @Provides @OkHttpInterceptors @Singleton @NonNull
    public List<Interceptor> provideOkHttpInterceptors(
          @NonNull HttpLoggingInterceptor httpLoggingInterceptor) {
        return Collections.singletonList(httpLoggingInterceptor);
    }

    @Provides @OkHttpNetworkInterceptors @Singleton @NonNull
    public List<Interceptor> provideOkHttpNetworkInterceptors() {
        //return Collections.singletonList(new StethoInterceptor());
        //Todo: setup interceptors for caching directives.
        return Collections.emptyList();
    }
}
