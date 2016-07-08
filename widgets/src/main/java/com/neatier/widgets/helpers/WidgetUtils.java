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

package com.neatier.widgets.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fernandocejas.arrow.optional.Optional;

/**
 * Created by László Gálosi on 06/04/16
 */
public class WidgetUtils {

    /**
     * @param stateDrawables map containing states and corresponding drawables.
     * @param defaultDrawableRes the default drawable resource if the mapped one not found.
     * @param context the application context
     * @return a stateful drawable from the specified stateDrawables {@link SparseIntArray} mapping
     * state attributes to drawableRes or default drawables
     */
    @SuppressWarnings("deprecation")
    public static Drawable createStatefulDrawable(final SparseIntArray stateDrawables,
          final @DrawableRes int defaultDrawableRes, final Context context) {
        //noinspection deprecation
        Drawable defaultDrawable = context.getResources().getDrawable(defaultDrawableRes);
        android.graphics.drawable.StateListDrawable stateListDrawable =
              new android.graphics.drawable.StateListDrawable();
        for (int i = 0, len = stateDrawables.size(); i < len; i++) {
            final int state = stateDrawables.keyAt(i);
            @DrawableRes int drawableRes = stateDrawables.get(state, defaultDrawableRes);
            stateListDrawable.addState(new int[] { state },
                                       context.getResources()
                                              .getDrawable(drawableRes > 0 ? drawableRes
                                                                           : defaultDrawableRes));
        }
        stateListDrawable.addState(new int[] {}, defaultDrawable);
        return stateListDrawable;
    }

    public static boolean isVisible(final View view, int... visibilityDef) {
        return view.getVisibility() == (visibilityDef.length > 0 ? visibilityDef[0] : View.VISIBLE);
    }

    /**
     * Set the view text, abd set the  visibility based on the srtingRes value if it's > 0, you can
     * överride this rule wuth forceVisible vararg parameter, if provided.
     */
    public static void setTextAndVisibilityOf(@Nullable View view, @StringRes int stringRes,
          boolean... forceVisible) {
        setTextOf(view, stringRes);
        if (forceVisible.length > 0) {
            setVisibilityOf(view, forceVisible[0]);
        } else {
            setVisibilityOf(view, stringRes > 0);
        }
    }

    /**
     * Set the view text, abd set the  visibility based on the text value if it's not null and not
     * empty, you can
     * överride this rule wuth forceVisible vararg parameter, if provided.
     */
    public static void setTextAndVisibilityOf(@Nullable View view, String text,
          boolean... forceVisible) {
        setTextOf(view, cleanNewLine(text));
        if (forceVisible.length > 0) {
            setVisibilityOf(view, forceVisible[0]);
        } else {
            setVisibilityOf(view, !TextUtils.isEmpty(text));
        }
    }

    public static void setVisibilityOf(@Nullable View view, boolean visible) {
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public static void setVisibilityOf(@Nullable View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    public static void setTextOf(@Nullable View view, String text) {
        if (view != null) {
            ((TextView) view).setText(cleanNewLine(text));
        }
    }

    private static String cleanNewLine(final String text) {
        String newLine = System.getProperty("line.separator");
        String s = text.replaceAll("\\r\\n", newLine).replaceAll("\\t", " ");
        //Log.v("cleanNewLine", text, s);
        return s;
    }

    public static void setTextOf(View view, @StringRes int textRes) {
        if (view != null && textRes > 0) {
            ((TextView) view).setText(textRes);
        }
    }

    public static void setLayoutSizeOf(final View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            view.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            return;
        }
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).width = width;
            ((ViewGroup.MarginLayoutParams) layoutParams).height = height;
        } else {
            layoutParams.width = width;
            layoutParams.height = height;
        }
    }

    public static void setTextWithLinksOf(TextView textView, String htmlText) {
        setHtmlTextOf(textView, htmlText);
        // TODO https://code.google.com/p/android/issues/detail?id=191430
        //noinspection Convert2Lambda
        textView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_UP ||
                      action == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    TextView widget = (TextView) v;
                    x -= widget.getTotalPaddingLeft();
                    y -= widget.getTotalPaddingTop();

                    x += widget.getScrollX();
                    y += widget.getScrollY();

                    Layout layout = widget.getLayout();
                    int line = layout.getLineForVertical(y);
                    int off = layout.getOffsetForHorizontal(line, x);

                    ClickableSpan[] link = Spannable.Factory.getInstance()
                                                            .newSpannable(widget.getText())
                                                            .getSpans(off, off,
                                                                      ClickableSpan.class);

                    if (link.length != 0) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(widget);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static void setHtmlTextOf(TextView textView, String htmlText) {
        textView.setText(TextUtils.isEmpty(htmlText) ? null : trim(Html.fromHtml(htmlText)));
    }

    private static CharSequence trim(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return charSequence;
        }
        int end = charSequence.length() - 1;
        while (Character.isWhitespace(charSequence.charAt(end))) {
            end--;
        }
        return charSequence.subSequence(0, end + 1);
    }

    @Nullable
    public static String getTextData(Object data, @Nullable Context context, String... fallback) {
        Optional<String> textData = Optional.absent();
        if (data instanceof Integer && context != null) {
            int stringRes = (Integer) data;
            textData = stringRes > 0 ? Optional.of(context.getString(stringRes))
                                     : Optional.absent();
        } else if (data instanceof String) {
            textData = Optional.of((String) data);
        } else if (data instanceof DisplayableValue && context != null) {
            textData = Optional.of(((DisplayableValue) data).toString(context));
        } else {
            textData = Optional.fromNullable(data.toString());
        }
        return textData.or(fallback.length > 0 ? fallback[0] : null);
    }
}
