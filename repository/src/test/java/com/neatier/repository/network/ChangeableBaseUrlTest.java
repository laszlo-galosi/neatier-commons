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

package com.neatier.repository.network;

import com.neatier.data.network.ChangeableBaseUrl;
import okhttp3.HttpUrl;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by László Gálosi on 09/06/16
 */
public class ChangeableBaseUrlTest {

    public static final String BASE_URL = "https://delightsolutions.com";

    @Test
    public void constructor_shouldUsePassedBaseUrl() {
        ChangeableBaseUrl changeableBaseUrl = new ChangeableBaseUrl(BASE_URL);
        assertThat(changeableBaseUrl.url()).isEqualTo(HttpUrl.parse(BASE_URL));
    }

    @Test
    public void setBaseUrl() {
        ChangeableBaseUrl changeableBaseUrl = new ChangeableBaseUrl("https://1");
        changeableBaseUrl.setBaseUrl("https://2");
        assertThat(changeableBaseUrl.url()).isEqualTo(HttpUrl.parse("https://2"));
    }
}
