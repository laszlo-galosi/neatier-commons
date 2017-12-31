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

package com.neatier.widgets.renderers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import com.fernandocejas.arrow.optional.Optional;
import com.neatier.commons.helpers.DateTimeHelper;
import com.neatier.widgets.helpers.DisplayableValue;
import org.joda.time.DateTime;

/**
 * {@link Renderable} implementation for rendering date time range with a starting and ending {@link
 * DateTime}s.
 *
 * @author László Gálosi
 * @since 29/04/16
 */
public class TimeRangeRenderer implements Renderable<DateTime> {

    private Optional<DateTime> mStartDateTime = Optional.absent();
    private Optional<DateTime> mEndDateTime = Optional.absent();
    private Optional<Long> mDurationInMillis = Optional.absent();
    private Optional<DisplayableValue> mDisplayableValue = Optional.absent();

    private int mTimeFormatFlags = DateUtils.FORMAT_SHOW_TIME;

    public TimeRangeRenderer() {
    }

    @Override
    public void render(final View itemView, @NonNull DateTime itemData) {
        mStartDateTime = Optional.fromNullable(itemData);
        Context context = itemView.getContext();
        StringBuilder sb = new StringBuilder();
        sb.append(getDisplayableText(mStartDateTime.get(), context));
        if (mDurationInMillis.isPresent() && !mEndDateTime.isPresent()) {
            mEndDateTime = Optional.of(
                  mStartDateTime.get().plus(mDurationInMillis.get().longValue())
            );
        }
        if (mEndDateTime.isPresent()) {
            sb.append(" - ").append(getDisplayableText(mEndDateTime.get(), context));
        }
        ((TextView) itemView).setText(sb.toString());
    }

    public Optional<DateTime> getStartDateTime() {
        return mStartDateTime;
    }

    public Optional<DateTime> getEndDateTime() {
        return mEndDateTime;
    }

    public Optional<Long> getDurationInMillis() {
        return mDurationInMillis;
    }

    public TimeRangeRenderer withEndDateTime(final DateTime endDateTime) {
        this.mEndDateTime = Optional.fromNullable(endDateTime);
        return this;
    }

    public TimeRangeRenderer withDurationInMillis(final Long durationInMillis) {
        this.mDurationInMillis = Optional.of(durationInMillis);
        return this;
    }

    public TimeRangeRenderer withFormatFlags(final int timeFormatFlags) {
        mTimeFormatFlags = timeFormatFlags;
        return this;
    }

    public TimeRangeRenderer withDisplayableValue(Optional<DisplayableValue> mDisplayableValue) {
        this.mDisplayableValue = mDisplayableValue;
        return this;
    }

    private String getDisplayableText(final DateTime value, final Context context) {
        if (mDisplayableValue.isPresent()) {
            return mDisplayableValue.get().toString(value, context);
        }
        return DateTimeHelper.formatDate(value, mTimeFormatFlags, context);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TimeRangeRenderer{");
        sb.append(mStartDateTime.get());
        if (mEndDateTime.isPresent()) {
            sb.append(" - ").append(mEndDateTime.get());
        }
        if (mDurationInMillis.isPresent()) {
            sb.append("duratuon=").append(mDurationInMillis.get());
        }
        sb.append('}');
        return sb.toString();
    }
}
