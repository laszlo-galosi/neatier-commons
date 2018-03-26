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

package com.neatier.commons.helpers;

import android.os.Bundle;
import com.neatier.commons.CommonsTestCase;
import java.io.Serializable;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author László Gálosi
 * @since 15/04/16
 */
public class BundleWrapperTest extends CommonsTestCase {

    @Test
    public void test_GetAsSerializable_HappyCase() throws Exception {
        DateTime now = DateTime.now();
        BundleWrapper bundleWrapper = BundleWrapper.wrap(new Bundle())
                .put("dateNow", now);

        Serializable dateNow = bundleWrapper.getAs("dateNow", Serializable.class);
        assertThat(dateNow, CoreMatchers.<Serializable>is(now));
        assertThat((DateTime) (dateNow), is(now));

        bundleWrapper.getBundle().remove("dateNow");

        DateTime nullDate = DateTimeHelper.nullDate();
        assertThat(bundleWrapper.getAs("dateNow", Serializable.class,
                nullDate),
                CoreMatchers.<Serializable>is(nullDate));
    }
}
