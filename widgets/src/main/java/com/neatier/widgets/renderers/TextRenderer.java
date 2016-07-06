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
import com.neatier.widgets.helpers.DisplayableValue;
import com.neatier.widgets.helpers.WidgetUtils;

/**
 * Created by László Gálosi on 29/04/16
 */
public class TextRenderer<T> implements Renderable<Object> {

    private Optional<String> mTextData;
    private Class<T> mTextDataClass;

    public TextRenderer() {
    }

    @Override
    public void render(final View itemView, @NonNull Object itemData) {
        final TextView textView = (TextView) itemView;
        mTextData = Optional.absent();
        if (itemData instanceof Integer ) {
            int stringRes = (Integer) itemData;
            mTextData = stringRes > 0 ? Optional.of(itemView.getContext().getString(stringRes))
                                      : Optional.absent();
        } else if (itemData instanceof String) {
            mTextData = Optional.of((String) itemData);
        } else if (itemData instanceof DisplayableValue) {
            mTextData = Optional.of(((DisplayableValue) itemData).toString(itemView.getContext()));
        } else {
            mTextData = Optional.fromNullable(itemData.toString());
        }
        WidgetUtils.setTextOf(itemView, mTextData.or(""));
    }

    public Optional<String> getText() {
        return mTextData;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("TextRenderer{'");
        sb.append(mTextData.or(""));
        sb.append("'}");
        return sb.toString();
    }
}
