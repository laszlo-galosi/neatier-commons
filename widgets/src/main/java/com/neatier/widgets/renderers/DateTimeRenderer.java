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

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import com.fernandocejas.arrow.optional.Optional;
import com.neatier.commons.helpers.DateTimeHelper;
import net.danlew.android.joda.DateUtils;
import org.joda.time.DateTime;

/**
 * Created by László Gálosi on 29/04/16
 */
public class DateTimeRenderer implements Renderable<DateTime> {

    private Optional<DateTime> mDateTimeData;

    public DateTimeRenderer() {
    }

    @Override
    public void render(final View itemView, @NonNull DateTime itemData) {
        mDateTimeData = Optional.fromNullable(itemData);
        if (itemView != null) {
            ((TextView) itemView).setText(DateUtils.getRelativeTimeSpanString(
                  itemView.getContext(), mDateTimeData.or(DateTimeHelper.defaultLocalDate()))
            );
        }
    }

    public Optional<DateTime> getData() {
        return mDateTimeData;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("DateTimeRenderer{");
        sb.append(mDateTimeData);
        sb.append('}');
        return sb.toString();
    }
}
