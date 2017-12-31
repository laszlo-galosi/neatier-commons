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

/**
 * Interface for {@link rx.Observable#subscribeOn(Scheduler)} and {@link
 * rx.Observable#observeOn(Scheduler)} scheduler pair to execute and
 * long running tasks and observe execution results on different Threads.
 * @author László Gálosi
 * @since 24/07/15
 */
public interface LongTaskScheduler {
    /**
     * @return the {@link Scheduler} to perform the task on.
     */
    Scheduler performMeOn();

    /**
     * @return the {@link Scheduler} to be notified by the task on.
     */
    Scheduler notifyMeOn();
}
