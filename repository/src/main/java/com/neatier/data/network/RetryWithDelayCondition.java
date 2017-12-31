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

import android.util.Pair;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Func1;

/**
 * A conditional function with the specified retry condition to be met.
 * (a function taking a throwable and returning true if this throwable should trigger
 * the retry and false if not).
 *
 * @author László Gálosi
 * @since 08/06/16
 */
public class RetryWithDelayCondition implements
                                     Func1<Observable<? extends Throwable>, Observable<?>> {

    private final int mMaxRetries;
    private final int mRetryDelayMillis;
    private int mRetryCount;
    private Func1<Throwable, Boolean> mRetryCondition;

    /**
     * Creates a conditional function with the specified retry condition to be met.
     * (a function taking a throwable and returning true if this throwable should trigger
     * the retry and false if not).
     *
     * @param maxRetries the maximum number of retries
     * @param retryDelayMillis the delay in milliseconds between each retry.
     * @param mRetryCondition the retry condition to be met
     */
    public RetryWithDelayCondition(
          final int maxRetries,
          final int retryDelayMillis,
          Func1<Throwable, Boolean> mRetryCondition) {

        this.mMaxRetries = maxRetries;
        this.mRetryDelayMillis = retryDelayMillis;
        this.mRetryCount = 0;
        this.mRetryCondition = mRetryCondition;
    }

    @Override public Observable<?> call(Observable<? extends Throwable> attempts) {
        return attempts.zipWith(Observable.range(1, mMaxRetries + 1), (t, i) -> new Pair<>(t, i))
                       .flatMap(pair -> {
                           if (mRetryCondition.call(pair.first) && pair.second <= mMaxRetries) {
                               return Observable.timer((long) Math.pow(2, pair.second),
                                                       TimeUnit.SECONDS);
                           } else {
                               return Observable.error(pair.first);
                           }
                       });
    }
}

