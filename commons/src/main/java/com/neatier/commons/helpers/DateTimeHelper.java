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
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import net.danlew.android.joda.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

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
 * Helper class containing  {@link org.joda.time.DateTime} related static helper methods.
 *
 * @author László Gálosi
 * @since 13/09/15
 */
public class DateTimeHelper {

    /**
     * Static constant for defining a Rest Api date and time format, with {@link DateTimeZone#UTC}
     * time zone.
     */
    public static final String SERVER_UTC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Static constant for defining empty or null date string which is the Epoch time.
     */
    public static final String NULL_DATE = "1970-01-01T01:00:00Z";

    /**
     * Static constant for defining a date and time format string.
     *
     * @see DateTimeFormatter#withZone(DateTimeZone)
     * time zone.
     */
    public static final String PATTERN_W_ZONE = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Static constant for defining a default standard date and time format string.
     */
    public static final String STANDARD_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Static constant for defining a default standard date format string.
     */
    public static final String STANDARD_DATE_PATTERN = "yyyy-MM-dd";

    /**
     * Static constant for defining a default standard time format string.
     */
    public static final String STANDARD_TIME_PATTERN = "HH:mm:ss";

    /**
     * Static constant for defining a default standard date and time format string.
     */
    public static final String DISPLAYABLE_TIME_FORMAT = "HH:mm";
    //2015-09-16T23:31:48.413

    /**
     * static constant int array for formatting flags used by the {@link android.text.format
     * .DateUtils#formatDateTime(Context,long, int)}
     */
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

    /**
     * Returns and sql formatted date time string of the given date time object.
     */
    public static String toSql(DateTime dateTime) {
        return new Timestamp(dateTime.getMillis()).toString();
    }

    /**
     * Returns null if the first date time string parameter is chronologically later than the second
     * one.
     *
     * @param dateTimeStringA datetime string operand
     * @param dateTimeStringB datetime string operand to compare.
     * @return true if dateTimeA is after dateTimeB in time.
     * @see DateTime#isAfter(ReadableInstant)
     */
    public static boolean isAfter(String dateTimeStringA, String dateTimeStringB) {
        DateTime dateTimeA = new DateTime(dateTimeStringA);
        DateTime dateTimeB = new DateTime(dateTimeStringB);
        return dateTimeA.isAfter(dateTimeB);
    }

    /**
     * Returns true if the first date time parameter is chronologically later than the second one.
     *
     * @param dateOne datetime to compare
     * @param dateTwo datetime to compare to
     * @return true if dateTimeA is after dateTimeB in time.
     * @see DateTime#isAfter(ReadableInstant)
     */
    public static boolean isAfter(DateTime dateOne, DateTime dateTwo, boolean checkTime) {
        dateOne = !checkTime ? dateOne.withTimeAtStartOfDay() : dateOne;
        dateTwo = !checkTime ? dateTwo.withTimeAtStartOfDay() : dateTwo;
        return dateOne.isAfter(dateTwo);
    }

    /**
     * Returns true if the first date time parameter is chronologically earlier than the second one.
     *
     * @param dateOne datetime to compare
     * @param dateTwo datetime to compare to
     * @return true if dateTimeA is after dateTimeB in time.
     * @see DateTime#isBefore(ReadableInstant)
     */
    public static boolean isBefore(DateTime dateOne, DateTime dateTwo, boolean checkTime) {
        dateOne = !checkTime ? dateOne.withTimeAtStartOfDay() : dateOne;
        dateTwo = !checkTime ? dateTwo.withTimeAtStartOfDay() : dateTwo;
        return dateOne.isBefore(dateTwo);
    }

    /**
     * Returns a converted datetime object of the given {@link #SERVER_UTC_PATTERN} formatted
     * string.
     *
     * @see #parseDate(String, String)
     */
    public static DateTime toCloudUTC(String dateTimeString) {
        return new DateTime(parseDate(dateTimeString, SERVER_UTC_PATTERN));
    }

    /**
     * Returns a converted datetime object of the given formatted string, with the given date time
     * format pattern.
     *
     * @see #parseDate(String, String, DateTimeZone)
     */
    public static DateTime parseDate(final String dateStr, String pattern) {
        return parseDate(dateStr, pattern, DateTimeZone.UTC);
    }

    /**
     * Returns a converted datetime object of the given formatted string, with the given date time
     * format pattern string with the given datetime zone.
     *
     * @see DateTime#parse(String, DateTimeFormatter)
     */
    public static DateTime parseDate(final String dateStr, String pattern, final DateTimeZone tz) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(pattern).withZone(tz);
        if (dateStr == null) {
            return null;
        }
        DateTime dateTime;
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

    /**
     * Returns a converted datetime object of the given formatted string, with the given date time
     * format pattern string or he given defaultDateTime if a parsing error happens.
     *
     * @param dateStr the datetime string to parse
     * @param defaultDateTime the fallback return value if any parsing error happens.
     * @see DateTime#parse(String, DateTimeFormatter)
     */
    public static DateTime parseDate(final @Nullable String dateStr, String pattern,
            String defaultDateTime) {
        String dateToParse = dateStr;
        if (dateToParse == null) {
            dateToParse = defaultDateTime;
        }
        return parseDate(dateToParse, pattern);
    }

    /**
     * Returns a formatted string of the given date time object.Formatting specified by the given
     * formatting flags.
     *
     * @see DateUtils#formatDateTime(Context, ReadableInstant, int)
     */
    public static String formatDate(final DateTime dateTime, int flags, final Context context) {
        return DateUtils.formatDateTime(context, dateTime, flags);
    }

    /**
     * Returns a formatted string of the given date time object.Formatting flags are a default oe
     * {@link DateUtils#FORMAT_SHOW_DATE} | {@link DateUtils#FORMAT_SHOW_YEAR} which displays the
     * date and also the year of the given datetime.
     *
     * @see DateUtils#formatDateTime(Context, ReadableInstant, int)
     */
    public static String formatDate(final DateTime dateTime, final Context context) {
        return DateUtils.formatDateTime(context, dateTime, DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_YEAR);
    }

    /**
     * Returns a formatted string of the date object represented by the given calendar object.
     *
     * @see SimpleDateFormat#format(Date)
     */
    public static String formatDateTime(final Calendar calendar, final Context context) {
        if (context != null) {
            return formatShortDateAndTime(new DateTime(calendar), true, context);
        }
        return new SimpleDateFormat(STANDARD_PATTERN, Locale.getDefault())
                .format(calendar.getTime());
    }

    /**
     * Returns a short formatted string  of the given date time object with format flags using
     * {@link DateUtils#FORMAT_SHOW_DATE} | {@link DateUtils#FORMAT_ABBREV_ALL} formatting flags.
     *
     * @see SimpleDateFormat#format(Date)
     */
    public static String formatShortDateAndTime(final DateTime dateTime, boolean withTime,
            final Context context) {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL;
        if (withTime) {
            flags |= DateUtils.FORMAT_SHOW_TIME;
        }
        return DateUtils.formatDateTime(context, dateTime, flags);
    }

    /**
     * Returns a date time object converted from the given Calendar with {@link
     * DateTimeZone#getDefault()} time zone.
     */
    @NonNull
    public static DateTime dateTime(final Calendar calendar) {
        return new DateTime(calendar == null ? Calendar.getInstance() : calendar.getTime())
                .withZone(DateTimeZone.getDefault());
    }

    /**
     * Returns a formatted time string  of the given date time object using the give time format
     * pattern with the device local time zone specified by {@link DateTimeZone#getDefault()}.
     */
    public static String formatLocalTime(final DateTime dateTime, String timePattern) {
        //See http://stackoverflow.com/a/24643661
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(timePattern)
                .withZone(DateTimeZone.getDefault());
        return dateTime.toString(timeFormatter);
    }

    /**
     * Returns a formatted time string  of the given local time object using the give time format
     * pattern with the device local time zone specified by {@link DateTimeZone#getDefault()}.
     */
    public static String formatLocalTime(final LocalTime localTime, final Context context) {
        //See http://stackoverflow.com/a/24643661
        //DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(STANDARD_TIME_PATTERN)
        //                                                .withZone(DateTimeZone.getDefault());
        //return localTime.toString(timeFormatter);
        return formatTime(
                DateTime.now().withTime(localTime).withZone(DateTimeZone.getDefault()), context);
    }

    /**
     * Returns a formatted time string  of the given date time object using the default {@link
     * DateUtils#FORMAT_SHOW_TIME}
     * formatting flags.
     */
    public static String formatTime(final DateTime dateTime, final Context context) {
        return DateUtils.formatDateTime(context, dateTime, DateUtils.FORMAT_SHOW_TIME);
    }

    /**
     * Returns a date time string  of the given date  object using the given datetime formatting
     * pattern. Tries to convert the date object to {@link DateTime} so it can be an instance of
     * {@link DateTime}, {@link Long} as Epoch time, {@link LocalTime}, {@link Calendar} or {@link
     * String}.
     */
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

    /**
     * Returns a time string  of the given date  object using the given time formatting
     * pattern. Tries to convert the date object to {@link DateTime} so it can be an instance of
     * {@link DateTime}, {@link Long} as Epoch time, {@link LocalTime}, {@link Calendar} or {@link
     * String}.
     */
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

    /**
     * Returns a date time object from the given date time object with local time with the default
     * local time zone of the device.
     */
    public static DateTime withLocalTime(DateTime dateTime, Object timeObject) {
        DateTime result = dateTime.withZone(DateTimeZone.getDefault());
        return result.withTime(new LocalTime(timeObject));
    }

    /**
     * Tries to parse the given time string formatted by the given time pattern, if any parsing
     * error happens it to parse the given default time string as a fallback.
     * *1 @see LocalTime#parse(String, DateTimeFormatter)
     */
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

    /**
     * Tries to parse the given time string formatted by {@link #STANDARD_TIME_PATTERN}.
     *
     * @see LocalTime#parse(String, DateTimeFormatter)
     */
    public static LocalTime parseLocalTime(final String timeString) {
        //See http://stackoverflow.com/a/24643661
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(STANDARD_TIME_PATTERN)
                .withZone(DateTimeZone.getDefault());
        return LocalTime.parse(timeString, timeFormatter);
    }

    /**
     * Tries to parse the given datetime string formatted by {@link #STANDARD_PATTERN}.
     *
     * @see DateTime#parse(String, DateTimeFormatter)
     */
    public static DateTime parseLocalDate(final String dateTimeString) {
        return parseDate(dateTimeString, STANDARD_PATTERN, DateTimeZone.getDefault());
    }

    /**
     * Tries to parse the given datetime string formatted by the given datetime pattern using the
     * local {@link DateTimeZone#getDefault()} time zon of the device. If any parsing error happens
     * it tries to parse
     * the give default datetime string as a fallback.
     *
     * @see DateTime#parse(String, DateTimeFormatter)
     */
    public static DateTime parseLocalDate(final String dateTimeStr, String pattern,
            String defaultDateTime) {
        String dateTimeToParse = dateTimeStr;
        if (TextUtils.isEmpty(dateTimeToParse)) {
            dateTimeToParse = defaultDateTime;
        }
        return parseDate(dateTimeToParse, pattern, DateTimeZone.getDefault());
    }

    /**
     * Tries to parse the given datetime string formatted by the given datetime pattern using the
     * local {@link DateTimeZone#getDefault()} time zon of the device. If any parsing error happens
     * it tries to parse
     * the give default datetime string as a fallback.
     *
     * @see LocalTime#parse(String, DateTimeFormatter)
     */
    public static DateTime parseLocalDate(final String dateTimeStr, String pattern,
            DateTime defaultDateTime) {
        String dateTimeToParse = dateTimeStr;
        if (TextUtils.isEmpty(dateTimeToParse)) {
            dateTimeToParse = DateTimeHelper.toStoreableDateString(defaultDateTime, pattern);
        }
        return parseDate(dateTimeToParse, pattern, DateTimeZone.getDefault());
    }

    /**
     * Tries to parse the given datetime string formatted {@link ISODateTimeFormat}
     * If any parsing error happens it tries to parse the give default datetime string as a
     * fallback.
     *
     * @see LocalTime#parse(String, DateTimeFormatter)
     */
    public static DateTime parseISODateTime(final String dateTimeStr, DateTime defaultDateTime) {
        String dateTimeToParse = dateTimeStr;
        if (TextUtils.isEmpty(dateTimeToParse)) {
            dateTimeToParse = ISODateTimeFormat.dateTime().print(defaultDateTime);
        }
        return ISODateTimeFormat.dateTimeParser().parseDateTime(dateTimeToParse);
    }

    /**
     * Returns true if the given flags contains only valid datetime formatting flags specified by
     * {@link #sFormattingFlags}
     */
    public static boolean isValidFormattingFlag(int flags) {
        int allFlags = 0;
        int len = sFormattingFlags.length;
        for (final int sFormattingFlag : sFormattingFlags) {
            allFlags |= sFormattingFlag;
        }
        return ((flags & allFlags) == flags);
        //int invalidFlags = (byte) ~allFlags;
        //return ((flags & invalidFlags) == 0);
    }

    /**
     * Returns the current DateTime using the device local {@link DateTimeZone}
     *
     * @see DateTime#withZone(DateTimeZone)
     */
    public static DateTime nowLocal() {
        return DateTime.now().withZone(DateTimeZone.getDefault());
    }

    /**
     * Returns an empty or null date of Epoxy time, by parsing {@link #NULL_DATE} datetime string.
     */
    public static DateTime nullDate() {
        return DateTimeHelper.parseDate(DateTimeHelper.NULL_DATE,
                DateTimeHelper.SERVER_UTC_PATTERN);
    }
}
