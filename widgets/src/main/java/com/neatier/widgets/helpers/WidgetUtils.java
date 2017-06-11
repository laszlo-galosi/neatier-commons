/*
 * Copyright (C) 2016 Extremenet Ltd., All Rights Reserved
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

package com.neatier.widgets.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fernandocejas.arrow.optional.Optional;
import com.squareup.picasso.Transformation;
import trikita.log.Log;

/**
 * Created by László Gálosi on 06/04/16
 */
public class
WidgetUtils {

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;

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

    public static void setTextColorOf(@Nullable View view, @ColorInt int color) {
        if (view != null) {
            ((TextView) view).setTextColor(color);
        }
    }

    public static void setImageOf(@Nullable View view, Object imageObject, Context context) {
        if (view != null && view instanceof ImageView) {
            if (imageObject instanceof Integer) {
                ((ImageView) view).setImageResource((Integer) imageObject);
            } else if (imageObject instanceof Bitmap) {
                ((ImageView) view).setImageBitmap((Bitmap) imageObject);
            } else if (imageObject instanceof Drawable) {
                ((ImageView) view).setImageDrawable((Drawable) imageObject);
            }
        }
    }

    private static String cleanNewLine(final String text) {
        if (text == null) {
            return text;
        }
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

    public static void setPaddingOf(final View view, int... paddings) {
        final int len = paddings.length;
        if (view == null || len == 0) {
            return;
        }
        view.setPadding(len > 0 ? paddings[0] : view.getPaddingLeft(),
                        len > 1 ? paddings[1] : view.getPaddingTop(),
                        len > 2 ? paddings[2] : view.getPaddingRight(),
                        len > 3 ? paddings[3] : view.getPaddingBottom()
        );
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
        if (data == null) {
            if (fallback.length > 0) {
                return fallback[0];
            }
            return null;
        }
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
        if (fallback.length > 0) {
            return textData.or(fallback[0]);
        }
        return textData.orNull();
    }

    /**
     * Will scroll the {@code scrollView} to make {@code viewToScroll} visible
     *
     * @param scrollView parent of {@code scrollableContent}
     * @param scrollableContent a child of {@code scrollView} which holds the scrollable content
     * (fills the viewport).
     * @param viewToScroll a child of {@code scrollableContent} to whitch will scroll the the
     * {@code
     * scrollView}
     */
    public static void scrollToView(final NestedScrollView scrollView,
          final ViewGroup scrollableContent,
          final View viewToScroll) {
        long delay = 100; //delay to let finish with possible modifications to ScrollView
        scrollView.postDelayed(() -> {
            Rect viewToScrollRect = new Rect(); //coordinates to scroll to
            //fills viewToScrollRect with coordinates of viewToScroll relative to its parent
            // (LinearLayout)
            viewToScroll.getHitRect(viewToScrollRect);
            //ScrollView will make sure, the given viewToScrollRect is visible
            scrollView.requestChildRectangleOnScreen(scrollableContent, viewToScrollRect,
                                                     false);
        }, delay);
    }

    @Nullable public static Bitmap drawableToBitmap(@NonNull Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        try {
            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, BITMAP_CONFIG);
                // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                             drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            Log.e("Unable to create bitmap from drawable", e);
            return null;
        }
    }

    public static class FixedWidthResizeTransform implements Transformation {
        final int mTargetWidth;

        public FixedWidthResizeTransform(final int mtargetWidth) {
            this.mTargetWidth = mtargetWidth;
        }

        public Bitmap getTransformedBitmap(Bitmap bitmap) {
            float aspectRatio = (float) bitmap.getHeight() / (float) bitmap.getWidth();
            int targetHeight = (int) (mTargetWidth * aspectRatio);
            Bitmap output = Bitmap.createScaledBitmap(bitmap, mTargetWidth, targetHeight, false);
            if (output != bitmap) {
                // Same bitmap is returned if sizes are the same
                bitmap.recycle();
            }
            return output;
        }

        @Override public Bitmap transform(final Bitmap source) {
            return getTransformedBitmap(source);
        }

        @Override public String key() {
            return String.format("%s_%d", getClass().getSimpleName(), mTargetWidth);
        }
    }
}
