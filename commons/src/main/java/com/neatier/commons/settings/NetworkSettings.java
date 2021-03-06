/*
 * Copyright (C) 2015 Laszlo Galosi, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * All information contained herein is, and remains the property of Dr. Krisztian Balazs.
 * The intellectual and technical concepts contained herein are proprietary to Dr. Krisztian
 * Balazs and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 * by trade secret or copyright law. Dissemination of this information or reproduction of
 * this material is strictly forbidden unless prior written permission is obtained from
 * Laszlo Galosi.
 */

package com.neatier.commons.settings;

import java.util.concurrent.TimeUnit;

/**
 * Contains Network connection related static timeout values.
 *
 * @author László Gálosi
 * @since 08/08/15
 */
public class NetworkSettings {
    //Networking related constants
    /**
     * Connection read timeout in seconds.
     */
    public static final long READ_TIMEOUT = 30;

    /**
     * Connection timeout in seconds.
     */
    public static final long CONNECTION_TIMEOUT = 20;
    /**
     * Timeout time unit.
     */
    public static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    private NetworkSettings() {
        // This class cannot be instantiated
    }
}
