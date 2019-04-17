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

import android.content.Context;
import androidx.annotation.NonNull;
import com.neatier.commons.settings.NetworkSettings;
import com.neatier.data.network.retrofit.OkHttpInterceptors;
import com.neatier.data.network.retrofit.OkHttpNetworkInterceptors;
import dagger.Module;
import dagger.Provides;
import java.io.File;
import java.util.List;
import javax.inject.Singleton;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * A Dagger {@link Module @Module} class providing the {@link OkHttpClient} Singleton instance.
 */
@Module
public class NetworkModule {

    public static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;

    @Provides @NonNull @Singleton
    public OkHttpClient provideOkHttpClient(Context context,
            @OkHttpInterceptors @NonNull List<Interceptor> interceptors,
            @OkHttpNetworkInterceptors @NonNull List<Interceptor> networkInterceptors) {

        File cacheDir = new File(context.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        final OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(NetworkSettings.CONNECTION_TIMEOUT, NetworkSettings.TIMEOUT_UNIT)
                .readTimeout(NetworkSettings.READ_TIMEOUT, NetworkSettings.TIMEOUT_UNIT);

        for (Interceptor interceptor : interceptors) {
            okHttpBuilder.addInterceptor(interceptor);
        }
        for (Interceptor networkInterceptor : networkInterceptors) {
            okHttpBuilder.addNetworkInterceptor(networkInterceptor);
        }
        return okHttpBuilder.build();
    }
}
