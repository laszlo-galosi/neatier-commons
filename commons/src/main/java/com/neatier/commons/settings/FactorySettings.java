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

/**
 * Created by Krisztian on 4/12/2015.
 */
public class FactorySettings {

    public static final String PACKAGE_NAME = "com.delightsolutions.hirtv";

    //Intent related constants

    //Caching related constants.
    public static final int CHECK_CACHED_EXPIRED = -1;
    public static final int CHECK_CACHED_NEWER = 1;
    public static final int CHECK_CACHED_UPTODATE = 0;
    public static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    //SharedPreferences related constants
    public static final String PREF_DEFAULT_STORAGE_FILE = "HirTv";
    public static final String PREF_DEV_SETTINGS_FILE = "HirTv.DevSttings";

    private FactorySettings() {
        // This class cannot be instantiated
    }
}
