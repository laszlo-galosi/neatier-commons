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

import android.content.Context;
import android.text.format.DateUtils;

import com.neatier.commons.CommonsTestCase;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;

import java.util.Arrays;

import rx.functions.Func1;

/**
 * @author László Gálosi
 * @since 25/11/15
 */
public class ObjectListTransformerStreamTest extends CommonsTestCase {

    private Context mContext;

    @Before
    public void setUp() throws Exception {
        mContext = ShadowApplication.getInstance().getApplicationContext();
    }

    @Test
    public void test_SimpleDateToStringTransform_HappyCase() throws Exception {
        KeyValuePairs<String, DateTime> keyValuePairs = new KeyValuePairs<>(2);
        DateTime dateTimeOne = DateTime.now();
        DateTime dateTimeTwo = dateTimeOne.plusHours(2);

        String[] result = new String[]{
                DateTimeHelper.formatDate(dateTimeOne, DateUtils.FORMAT_SHOW_TIME, mContext),
                DateTimeHelper.formatDate(dateTimeTwo, DateUtils.FORMAT_SHOW_TIME, mContext)
        };

        ObjectListTransformerStream<DateTime, String> transformedStream =
                new ObjectListTransformerStream<>(
                        Arrays.asList(dateTimeOne, dateTimeTwo),
                        new Func1<DateTime, String>() {
                            @Override
                            public String call(
                                    final DateTime dateTime) {
                                return DateTimeHelper.formatDate(dateTime, DateUtils.FORMAT_SHOW_TIME,
                                        mContext);
                            }
                        });
        assertObservableHappyCase(transformedStream.getTransformedStream(), null, null, result);
    }
}
