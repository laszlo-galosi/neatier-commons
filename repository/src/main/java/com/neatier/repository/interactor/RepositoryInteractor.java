/*
 * Copyright (C) 2017 Extremenet Ltd., All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *  All information contained herein is, and remains the property of Extremenet Ltd.
 *  The intellectual and technical concepts contained herein are proprietary to Extremenet Ltd.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Extremenet Ltd.
 *
 */

/*
 * Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 * All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions
  *  Kft.
 *  and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *  Delight Solutions Kft.
 */

package com.neatier.repository.interactor;

import com.neatier.commons.helpers.Leakable;
import com.neatier.commons.helpers.LongTaskScheduler;
import com.neatier.commons.helpers.RxUtils;
import com.neatier.repository.AsyncRepository;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Base class for objects which main class is to interact with the data layer via
 * {@link AsyncRepository}
 * Created by László Gálosi on 14/06/16
 */
public class RepositoryInteractor implements Leakable {

    private CompositeSubscription subscriptions = new CompositeSubscription();
    private final LongTaskScheduler longTaskScheduler;

    protected RepositoryInteractor(final LongTaskScheduler longTaskScheduler) {
        this.longTaskScheduler = longTaskScheduler;
    }

    public void unsubsribe() {
        RxUtils.unsubscribeIfNotNull(this.subscriptions);
    }

    public void resubscribe() {
        this.subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(this.subscriptions);
    }

    public void execute(Observable resultObservable, RxUtils.SubscriberAdapter subscriber) {
        ensureSubs().add(subscriber);
        resultObservable
              .onBackpressureBuffer(10000)
              .subscribeOn(longTaskScheduler.performMeOn())
              .observeOn(longTaskScheduler.notifyMeOn())
              .subscribe(subscriber);
    }

    /**
     * Ensures if the {@link Subscription#isUnsubscribed()} false, or creates a new {@link
     * CompositeSubscription} and returns it.
     */
    protected CompositeSubscription ensureSubs() {
        this.subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(this.subscriptions);
        return this.subscriptions;
    }

    @Override public void clearLeakables() {
        this.subscriptions.clear();
    }
}

