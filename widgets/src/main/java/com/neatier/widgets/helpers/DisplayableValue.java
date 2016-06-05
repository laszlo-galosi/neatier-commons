/*
 * Copyright (C) 2016 Laszlo Galosi, All Rights Reserved
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

package com.neatier.widgets.helpers;

import android.content.Context;
import android.support.annotation.StringRes;
import com.neatier.commons.helpers.DateTimeHelper;
import java.util.Arrays;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

/**
 * Helper class to format values to a user friendly displayable text.
 * Dormatting works as in  {@link String#format(String, Object...)}
 * Created by László Gálosi on 09/11/15
 */
public class DisplayableValue {
    public String valueFormat = "%.0f";
    public Object[] valueFormatParams;
    public Object value;

    public static DisplayableValue empty() {
        return new DisplayableValue("", "%s");
    }

    public static DisplayableValue simpleText(@StringRes final int stringRes) {
        return new DisplayableValue("", "%s%s", stringRes);
    }

    public static DisplayableValue simpleText(final String text) {
        return new DisplayableValue("", "%s%s", text);
    }

    public DisplayableValue() {
    }

    /**
     * Constructor.
     *
     * @param value the value to be formatted
     * @param valueFormat format string as in {@link String#format(String, Object...)}
     * @param valueFormatParams format argumentums.
     */
    public DisplayableValue(final Object value, final String valueFormat,
          final Object... valueFormatParams) {
        this.value = value;
        this.valueFormat = valueFormat;
        this.valueFormatParams = valueFormatParams;
    }

    public String toString(final Context context) {
        return toString(value, context);
    }

    /**
     * Returns the formatted value.
     *
     * @param value the value to be formatted.
     * @param context the mContext if {@link #valueFormatParams} contains {@link StringRes} string
     * resource identifiers.
     * @return the formatted value.
     */
    public String toString(final Object value, final Context context) {
        if (valueFormatParams != null && valueFormatParams.length > 0) {
            boolean isDateTime = (value instanceof DateTime || value instanceof LocalTime)
                  && valueFormatParams.length > 0;
            int offset = isDateTime ? 0 : 1;
            int len = valueFormatParams.length + offset;
            Object[] formatParams = new Object[len];
            if (!isDateTime) {
                formatParams[0] = value;
            }
            for (int i = offset; i < len; i++) {
                Object formatParam = valueFormatParams[i - offset];
                if (isDateTime && formatParam instanceof Integer
                      && DateTimeHelper.isValidFormattingFlag((Integer) formatParam)) {
                    if (value instanceof DateTime) {
                        formatParams[i] =
                              DateTimeHelper.formatDate((DateTime) value, (Integer) formatParam,
                                                        context);
                    } else if (value instanceof LocalTime) {
                        DateTime dateTime = DateTime.now()
                                                    .withTime((LocalTime) value)
                                                    .withZone(DateTimeZone.getDefault());
                        formatParams[i] = DateTimeHelper.formatDate(
                              new DateTime(dateTime, DateTimeZone.getDefault()),
                              (Integer) formatParam, context);
                    }
                } else if (formatParam instanceof Integer) {
                    formatParams[i] = context.getString((Integer) formatParam);
                } else {
                    formatParams[i] = formatParam.toString();
                }
            }
            return String.format(valueFormat, formatParams);
        } else {
            return String.format(valueFormat, value);
        }
    }

    public DisplayableValue setValue(final Object value) {
        this.value = value;
        return this;
    }

    public DisplayableValue setValueFormat(final String valueFormat) {
        this.valueFormat = valueFormat;
        return this;
    }

    public DisplayableValue setValueFormatParams(final Object... valueFormatParams) {
        this.valueFormatParams = valueFormatParams;
        return this;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("DisplayableValue{");
        sb.append("String.format('").append(valueFormat).append("\'");
        sb.append(", ").append(value);
        int len = valueFormatParams.length;
        for (int i = 0; i < len; i++) {
            sb.append(", ").append(valueFormatParams[i]);
        }
        sb.append(")}");
        return sb.toString();
    }

    @Override public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisplayableValue)) {
            return false;
        }

        final DisplayableValue that = (DisplayableValue) o;

        if (valueFormat != null ? !valueFormat.equals(that.valueFormat)
                                : that.valueFormat != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(valueFormatParams, that.valueFormatParams)) {
            return false;
        }
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override public int hashCode() {
        int result = valueFormat != null ? valueFormat.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(valueFormatParams);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
