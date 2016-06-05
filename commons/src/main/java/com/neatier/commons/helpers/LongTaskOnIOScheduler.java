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

package com.neatier.commons.helpers;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * {@link LongTaskScheduler} implementation, which performs the long running operation on the
 * {@link
 * Schedulers#io()} thread, and returns on the {@link AndroidSchedulers#mainThread()}
 * Created by László Gálosi on 03/08/15
 */
public class LongTaskOnIOScheduler implements LongTaskScheduler {
    @Override
    public Scheduler performMeOn() {
        return Schedulers.io();
    }

    @Override
    public Scheduler notifyMeOn() {
        return AndroidSchedulers.mainThread();
    }
}
