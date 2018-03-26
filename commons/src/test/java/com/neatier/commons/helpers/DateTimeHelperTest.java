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

import com.neatier.commons.CommonsTestCase;
import java.sql.Timestamp;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import static com.neatier.commons.helpers.DateTimeHelper.SERVER_UTC_PATTERN;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author László Gálosi
 * @since 17/09/15
 */
public class DateTimeHelperTest extends CommonsTestCase {

    public static final String FAKE_SERVER_DATE_TIME = "2015-06-16T23:21:49Z";

    public static final String FAKE_BIRTH_DATE = "1978-02-18T09:20:00";
    DateTimeFormatter mDateTimeFormatter =
            DateTimeFormat.forPattern(SERVER_UTC_PATTERN).withZone(DateTimeZone.UTC);

    @Test
    public void print_DateTimeConversion() throws Exception {
        LocalDateTime date = LocalDateTime.now();
        DateTimeZone tz = DateTimeZone.getDefault();

        System.out.println(date);
        System.out.println(tz);
        System.out.println("-----");
        System.out.println(date.toDateTime(tz));
        System.out.println(date.toDateTime(tz).toInstant());
        System.out.println(date.toDateTime(tz).toDateTime(DateTimeZone.UTC));
        System.out.println("-----");
        System.out.println(date.toDateTime(tz).getMillis());
        System.out.println(date.toDateTime(tz).toInstant().getMillis());
        System.out.println(date.toDateTime(tz).toDateTime(DateTimeZone.UTC).getMillis());
        System.out.println("-----");
        System.out.println(new Timestamp(date.toDateTime(tz).getMillis()));
        System.out.println(new Timestamp(date.toDateTime(tz).toInstant().getMillis()));
        System.out.println(
                new Timestamp(date.toDateTime(tz).toDateTime(DateTimeZone.UTC).getMillis()));
    }

    @Test
    public void test_ServerDateTimeStringConversionHappyCase() throws Exception {
        DateTime serverTime = DateTimeHelper.toCloudUTC(FAKE_SERVER_DATE_TIME);
        assertThat(serverTime.toString(SERVER_UTC_PATTERN), is(FAKE_SERVER_DATE_TIME));
        DateTime dateTime = new DateTime(FAKE_SERVER_DATE_TIME);
        assertThat(dateTime.toString(mDateTimeFormatter), is(FAKE_SERVER_DATE_TIME));
    }

    @Test
    public void print_DateTimeFormattings() throws Exception {
        DateTime birthDate = DateTimeHelper.parseLocalDate(FAKE_BIRTH_DATE);
        String sValue = DateTimeHelper.formatDate(birthDate, mContext);
        assertThat(sValue, is("February 18, 1978"));
        System.out.println(sValue);
        sValue = DateTimeHelper.formatTime(birthDate, mContext);
        //assertThat(sValue, is("9:20 AM"));
        System.out.println(sValue);
    }

    @Test
    public void test_ISODateToServerDateTimeStringConversionHappyCase()
            throws Exception {
        DateTime isoTime = DateTime.parse(FAKE_SERVER_DATE_TIME);
        assertThat(isoTime.toString(SERVER_UTC_PATTERN), is(FAKE_SERVER_DATE_TIME));
    }
}
