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

package com.neatier.commons.settings;

import java.util.concurrent.TimeUnit;

/**
 * Created by László Gálosi on 08/08/15
 */
public class ApiSettings {
    //Networking related constants
    public static final String HIRTV_SERVER_ENDPOINT = "http://api.delight-solutions.com/";
    public static final long READ_TIMEOUT = 30;
    public static final long CONNECTION_TIMEOOUT = 20;
    public static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    private ApiSettings() {
        // This class cannot be instantiated
    }
}
