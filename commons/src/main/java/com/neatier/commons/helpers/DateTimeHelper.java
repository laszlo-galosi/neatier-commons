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

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import net.danlew.android.joda.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static net.danlew.android.joda.DateUtils.FORMAT_ABBREV_ALL;
import static net.danlew.android.joda.DateUtils.FORMAT_ABBREV_MONTH;
import static net.danlew.android.joda.DateUtils.FORMAT_ABBREV_RELATIVE;
import static net.danlew.android.joda.DateUtils.FORMAT_ABBREV_TIME;
import static net.danlew.android.joda.DateUtils.FORMAT_ABBREV_WEEKDAY;
import static net.danlew.android.joda.DateUtils.FORMAT_NO_MIDNIGHT;
import static net.danlew.android.joda.DateUtils.FORMAT_NO_MONTH_DAY;
import static net.danlew.android.joda.DateUtils.FORMAT_NO_NOON;
import static net.danlew.android.joda.DateUtils.FORMAT_NO_YEAR;
import static net.danlew.android.joda.DateUtils.FORMAT_NUMERIC_DATE;
import static net.danlew.android.joda.DateUtils.FORMAT_SHOW_DATE;
import static net.danlew.android.joda.DateUtils.FORMAT_SHOW_TIME;
import static net.danlew.android.joda.DateUtils.FORMAT_SHOW_WEEKDAY;
import static net.danlew.android.joda.DateUtils.FORMAT_SHOW_YEAR;

/**
 * @author László Gálosi
 * @since 13/09/15
 */
public class DateTimeHelper {

    public static final String SERVER_UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String NULL_DATE = "1970-01-01T01:01:00Z";
    public static final String PATTERN_W_ZONE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String STANDARD_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String STANDARD_DATE_PATTERN = "yyyy-MM-dd";
    public static final String STANDARD_TIME_PATTERN = "HH:mm:ss";
    public static final String DISPLAYABLE_TIME_FORMAT = "HH:mm";
    //2015-09-16T23:31:48.413

    static int[] sFormattingFlags = new int[] {
          FORMAT_SHOW_TIME,
          FORMAT_SHOW_WEEKDAY,
          FORMAT_SHOW_YEAR,
          FORMAT_NO_YEAR,
          FORMAT_SHOW_DATE,
          FORMAT_NO_MONTH_DAY,
          FORMAT_NO_NOON,
          FORMAT_NO_MIDNIGHT,
          FORMAT_ABBREV_TIME,
          FORMAT_ABBREV_WEEKDAY,
          FORMAT_ABBREV_MONTH,
          FORMAT_NUMERIC_DATE,
          FORMAT_ABBREV_RELATIVE,
          FORMAT_ABBREV_ALL,
    };

    public static String toSql(DateTime dateTime) {
        return new Timestamp(dateTime.getMillis()).toString();
    }

    /**
     * @param dateTimeStringA datetime string operand
     * @param dateTimeStringB datetime string operand to compare.
     * @return true if dateTimeA is after dateTimeB in time.
     */
    public static boolean isAfter(String dateTimeStringA, String dateTimeStringB) {
        DateTime dateTimeA = new DateTime(dateTimeStringA);
        DateTime dateTimeB = new DateTime(dateTimeStringB);
        return dateTimeA.isAfter(dateTimeB);
    }

    public static boolean isAfter(DateTime dateOne, DateTime dateTwo, boolean checkTime) {
        dateOne = !checkTime ? dateOne.withTimeAtStartOfDay() : dateOne;
        dateTwo = !checkTime ? dateTwo.withTimeAtStartOfDay() : dateTwo;
        return dateOne.isAfter(dateTwo);
    }

    public static boolean isBefore(DateTime dateOne, DateTime dateTwo, boolean checkTime) {
        dateOne = !checkTime ? dateOne.withTimeAtStartOfDay() : dateOne;
        dateTwo = !checkTime ? dateTwo.withTimeAtStartOfDay() : dateTwo;
        return dateOne.isBefore(dateTwo);
    }

    public static DateTime toCloudUTC(String dateTimeString) {
        return new DateTime(parseDate(dateTimeString, SERVER_UTC_PATTERN));
    }

    public static DateTime parseDate(final String dateStr, String pattern,
          String defaultDateTime) {
        String dateToParse = dateStr;
        if (dateToParse == null) {
            dateToParse = defaultDateTime;
        }
        return parseDate(dateToParse, pattern);
    }

    public static DateTime parseDate(final String dateStr, String pattern) {
        return parseDate(dateStr, pattern, DateTimeZone.UTC);
    }

    public static DateTime parseDate(final String dateStr, String pattern, final DateTimeZone tz) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern).withZone(tz);
        if (dateStr == null) {
            return null;
        }
        DateTime dateTime = null;
        try {
            dateTime = DateTime.parse(dateStr, dateTimeFormatter).toDateTime(tz);
        } catch (Exception e) {
            int truncEnd = pattern.length() - (pattern.contains("'T'") ? 2 : 0);
            dateTime = DateTime.parse(
                  dateStr.substring(0, Math.min(dateStr.length(), truncEnd)),
                  dateTimeFormatter).toDateTime(DateTimeZone.UTC);
        }
        return dateTime;
    }

    public static String formatDate(final DateTime dateTime, int flags, final Context context) {
        return DateUtils.formatDateTime(context, dateTime, flags);
    }

    public static String formatDate(final DateTime dateTime, final Context context) {
        return DateUtils.formatDateTime(context, dateTime, DateUtils.FORMAT_SHOW_DATE
              | DateUtils.FORMAT_SHOW_YEAR);
    }

    public static String formatDateTime(final Calendar calendar, final Context context) {
        if (context != null) {
            return formatShortDateAndTime(new DateTime(calendar), true, context);
        }
        return new SimpleDateFormat(STANDARD_PATTERN).format(calendar.getTime());
    }

    public static String formatShortDateAndTime(final DateTime dateTime, boolean withTime,
          final Context context) {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL;
        if (withTime) {
            flags |= DateUtils.FORMAT_SHOW_TIME;
        }
        return DateUtils.formatDateTime(context, dateTime, flags);
    }

    public static String formatTime(final DateTime dateTime, final Context context) {
        return DateUtils.formatDateTime(context, dateTime, DateUtils.FORMAT_SHOW_TIME);
    }

    @NonNull
    public static DateTime dateTime(final Calendar calendar) {
        return new DateTime(calendar == null ? Calendar.getInstance() : calendar.getTime())
              .withZone(DateTimeZone.getDefault());
    }

    public static String formatLocalTime(final DateTime dateTime, String timePattern) {
        //See http://stackoverflow.com/a/24643661
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(timePattern)
                                                        .withZone(DateTimeZone.getDefault());
        return dateTime.toString(timeFormatter);
    }

    public static String formatLocalTime(final LocalTime localTime, final Context context) {
        //See http://stackoverflow.com/a/24643661
        //DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(STANDARD_TIME_PATTERN)
        //                                                .withZone(DateTimeZone.getDefault());
        //return localTime.toString(timeFormatter);
        return formatTime(
              DateTime.now().withTime(localTime).withZone(DateTimeZone.getDefault()), context);
    }

    public static String toStoreableDateString(final Object dateObject, String pattern) {
        DateTime dateTime = DateTime.now();
        if (dateObject instanceof DateTime) {
            dateTime = (DateTime) dateObject;
        } else if (dateObject instanceof Long) {
            dateTime.withMillis((Long) dateObject);
        } else if (dateObject instanceof LocalTime) {
            //return DateTimeHelper.formatLocalTime((LocalTime) dateObject, mContext);
            return DateTime.now().withTime((LocalTime) dateObject).toString(pattern);
        } else if (dateObject instanceof Calendar) {
            Calendar calendar = (Calendar) dateObject;
            DateTimeZone tz = DateTimeZone.forTimeZone(calendar.getTimeZone());
            return toStoreableDateString(new DateTime(calendar.getTime()).withZone(tz), pattern);
        }
        return dateTime.toString(pattern);
    }

    public static String toStoreableTimeString(final Object dateObject, String pattern) {
        DateTime dateTime = DateTime.now();
        if (dateObject instanceof DateTime) {
            dateTime = (DateTime) dateObject;
            LocalTime localtime = new LocalTime(dateTime);
            return localtime.toString(pattern);
        } else if (dateObject instanceof Long) {
            dateTime = dateTime.withMillis((Long) dateObject);
            LocalTime localtime = new LocalTime(dateTime);
            return localtime.toString(pattern);
        } else if (dateObject instanceof LocalTime) {
            //return DateTimeHelper.formatLocalTime((LocalTime) dateObject, mContext);
            return ((LocalTime) dateObject).toString(pattern);
        } else if (dateObject instanceof Calendar) {
            Calendar calendar = (Calendar) dateObject;
            DateTimeZone tz = DateTimeZone.forTimeZone(calendar.getTimeZone());
            return toStoreableTimeString(new DateTime(calendar.getTime()).withZone(tz), pattern);
        }
        return dateTime.toString(STANDARD_PATTERN);
    }

    public static DateTime withLocalTime(DateTime dateTime, Object timeObject) {
        DateTime result = dateTime.withZone(DateTimeZone.getDefault());
        return result.withTime(new LocalTime(timeObject));
    }

    public static LocalTime parseLocalTime(final String timeStr, String pattern,
          String defaultTime) {
        String timeToParse = timeStr;
        if (timeToParse == null) {
            timeToParse = defaultTime;
        }
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(pattern)
                                                        .withZone(DateTimeZone.getDefault());
        return LocalTime.parse(timeToParse, timeFormatter);
    }

    public static LocalTime parseLocalTime(final String timeString) {
        //See http://stackoverflow.com/a/24643661
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(STANDARD_TIME_PATTERN)
                                                        .withZone(DateTimeZone.getDefault());
        return LocalTime.parse(timeString, timeFormatter);
    }

    public static DateTime parseLocalDate(final String dateTimeString) {
        return parseDate(dateTimeString, STANDARD_PATTERN, DateTimeZone.getDefault());
    }

    public static DateTime parseLocalDate(final String dateTimeStr, String pattern,
          String defaultDateTime) {
        String dateTimeToParse = dateTimeStr;
        if (TextUtils.isEmpty(dateTimeToParse)) {
            dateTimeToParse = defaultDateTime;
        }
        return parseDate(dateTimeToParse, pattern, DateTimeZone.getDefault());
    }

    public static DateTime parseLocalDate(final String dateTimeStr, String pattern,
          DateTime defaultDateTime) {
        String dateTimeToParse = dateTimeStr;
        if (TextUtils.isEmpty(dateTimeToParse)) {
            dateTimeToParse = DateTimeHelper.toStoreableDateString(defaultDateTime, pattern);
        }
        return parseDate(dateTimeToParse, pattern, DateTimeZone.getDefault());
    }

    public static boolean isValidFormattingFlag(int flags) {
        int allFlags = 0;
        int len = sFormattingFlags.length;
        for (int i = 0; i < len; i++) {
            allFlags |= sFormattingFlags[i];
        }
        return ((flags & allFlags) == flags);
        //int invalidFlags = (byte) ~allFlags;
        //return ((flags & invalidFlags) == 0);
    }

    public static DateTime nowLocal() {
        return DateTime.now().withZone(DateTimeZone.getDefault());
    }

    public static DateTime nullDate() {
        return DateTimeHelper.parseDate(DateTimeHelper.NULL_DATE,
                                        DateTimeHelper.SERVER_UTC_PATTERN);
    }
}
