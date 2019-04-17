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
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import com.fernandocejas.arrow.collections.Lists;
import com.fernandocejas.arrow.optional.Optional;
import com.neatier.widgets.R;
import com.squareup.picasso.Transformation;
import java.util.List;
import java.util.Locale;
import trikita.log.Log;

/**
 * Helper class containing {@link View} related helper methods.
 *
 * @author László Gálosi
 * @since 06/04/16
 */
public class WidgetUtils {

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLOR_DRAWABLE_DIMENSION = 2;

    /**
     * Returns and creates a StateFul drawable from the given arguments.
     *
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
                            .getDrawable(drawableRes != 0 ? drawableRes
                                    : defaultDrawableRes));
        }
        stateListDrawable.addState(new int[] {}, defaultDrawable);
        return stateListDrawable;
    }

    /**
     * Returns true if the given view's visibility is equal to the given optional visibilityDef or
     * if omitted if the view visibility is {@link View#VISIBLE}
     */
    public static boolean isVisible(final View view, int... visibilityDef) {
        return view.getVisibility() == (visibilityDef.length > 0 ? visibilityDef[0] : View.VISIBLE);
    }

    /**
     * Set the view text, abd set the  visibility based on the stringRes value if it's > 0, you can
     * override this rule with forceVisible vararg parameter, if provided.
     * <p>The view nullability and instance of {@link TextView} is checked.</p>
     */
    public static void setTextAndVisibilityOf(@Nullable View view, @StringRes int stringRes,
            boolean... forceVisible) {
        setTextOf(view, stringRes);
        if (forceVisible.length > 0) {
            setVisibilityOf(view, forceVisible[0]);
        } else {
            setVisibilityOf(view, stringRes != 0);
        }
    }

    /**
     * Set the text of the given view to the given text resource.
     * <p>The view nullability and instance of {@link TextView} is checked.</p>
     */
    public static void setTextOf(View view, @StringRes int textRes) {
        if (view != null && textRes != 0) {
            ((TextView) view).setText(textRes);
        }
    }

    /**
     * Set the visibility of the given view to {@link View#VISIBLE} if the given visible value is
     * true or {@link View#GONE} otherwise unless the given visibility override is provided.
     * <p>The view nullability is checked.</p>
     * @param visibilityOverride vararg visibility flags to override based on the given visible conditions
     * the first item is when the condition is true, the second when the condition is false.
     */
    public static void setVisibilityOf(@Nullable View view, boolean visible, int... visibilityOverride) {
        if (view != null) {
            int visibility = visible ? View.VISIBLE : View.GONE;
            if (visible && visibilityOverride.length > 0) {
                visibility = visibilityOverride[0];
            }
            if (!visible && visibilityOverride.length > 1) {
                visibility = visibilityOverride[1];
            }
            view.setVisibility(visibility);
        }
    }

    /**
     * Set the text  of the given view to the given text string. It also set the visibility of the
     * given view to {@link View#VISIBLE} if the given vararg boolean argument is provided and is
     * true or {@link View#INVISIBLE} otherwise.
     * <p>The view nullability and instance of {@link TextView} is checked.</p>
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

    /**
     * Set the text of the given view to the given text string.
     * <p>The view nullability and instance of {@link TextView} is checked.</p>
     */
    public static void setTextOf(@Nullable View view, String text) {
        if (view != null) {
            ((TextView) view).setText(cleanNewLine(text));
        }
    }

    /**
     * Removes  any newLine characters of the given string and returns a new string.
     */
    @Nullable private static String cleanNewLine(final String text) {
        if (text == null) {
            return null;
        }
        String newLine = System.getProperty("line.separator");
        //Log.v("cleanNewLine", text, s);
        return text.replaceAll("\\r\\n", newLine).replaceAll("\\t", " ");
    }

    /**
     * Set the visibility of the given view to the given value.
     *
     * @param view the view to change visibility.
     * @param visibility the visibility {@link View#VISIBLE}, {@link View#INVISIBLE} or {@link
     * View#GONE}.
     */
    public static void setVisibilityOf(@Nullable View view, int visibility) {
        if (view != null) {
            view.setVisibility(visibility);
        }
    }

    /**
     * Set the text color of the given view to the given color.
     * <p>The view nullability and instance of {@link TextView} is checked.</p>
     */
    public static void setTextColorOf(@Nullable View view, @ColorInt int color) {
        if (view != null) {
            ((TextView) view).setTextColor(color);
        }
    }

    /**
     * Set the image  drawable of the given view to the given image object.
     * <p>The view nullability and instance of {@link ImageView} is checked.</p>
     *
     * @param view the {@link ImageView}instance to set the image of.
     * @param imageObject can be instance of {@link Integer} as a drawable resource, {@link Bitmap}
     * or {@link Drawable}
     */
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

    /**
     * Set the layout size of the given view to the give with and height in pixels.
     * <p>The given view {@link ViewGroup.LayoutParams} is checked.</p>
     */
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

    /**
     * Change the padding of the given view to the given vararg paddings.
     *
     * @param view the view of which paddings is to be set.
     * @param paddings contains left, top, right, bottom padding values in pixels,if any padding is
     * omitted that padding is not changed.
     * @see View#setPadding(int, int, int, int)
     */
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

    /**
     * Creates a clickable {@link TextView} from the given TextView by creating a html text with
     * link for the given htmlText.
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void setTextWithLinksOf(TextView textView, String htmlText) {
        setHtmlTextOf(textView, htmlText);
        // TODO https://code.google.com/p/android/issues/detail?id=191430
        //noinspection Convert2Lambda
        textView.setOnTouchListener(new View.OnTouchListener() {
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

    /**
     * Set the text of the given TextView tot the given html text.
     * <p>The view nullability and instance of {@link ImageView} is checked.</p>
     *
     * @see Html#fromHtml(String)
     */
    public static void setHtmlTextOf(TextView textView, String htmlText) {
        if (textView != null) {
            textView.setText(TextUtils.isEmpty(htmlText) ? null : trim(Html.fromHtml(htmlText)));
        }
    }

    /**
     * Removes all starting and trailing white spaces from the given CharSequence and returns a
     * trimmed
     * CharSequence.
     */
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

    /**
     * Returns a String from the given text data object according to its class or returns the given
     * fallback
     * if no match for the text data.
     *
     * @param data the text object instance of {@link Integer} as a string resource, a {@link
     * String} or calls {@link Object#toString()}
     * @param fallback vararg as a fallback, if the data is null.
     */
    @Nullable
    public static String getTextData(@Nullable Object data, @Nullable Context context,
            String... fallback) {
        Optional<String> textData;
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
     * Will scroll the given scroll view to make the given  child view visible.
     *
     * @param scrollView parent of {@code scrollableContent}
     * @param scrollableContent a child of {@code scrollView} which holds the scrollable content
     * (fills the viewport).
     * @param viewToScroll a child of {@code scrollableContent} to which will scroll the the
     * {@code
     * scrollView}
     */
    public static void scrollToView(final NestedScrollView scrollView,
            final ViewGroup scrollableContent,
            final View viewToScroll) {
        long delay = 100; //delay to let finish with possible modifications to ScrollView
        scrollView.postDelayed(() -> {
            Rect viewToScrollRect = new Rect(); //coordinates to scroll to
            viewToScroll.getHitRect(
                    viewToScrollRect); //fills viewToScrollRect with coordinates of
            // viewToScroll relative to its parent (LinearLayout)
            scrollView.requestChildRectangleOnScreen(scrollableContent, viewToScrollRect,
                    false); //ScrollView will make sure, the
            // given viewToScrollRect is visible
        }, delay);
    }

    /**
     * Creates and returns a {@link Bitmap} from the given drawable with the given width and height.
     */
    @Nullable
    public static Bitmap drawableToBitmap(@NonNull Drawable drawable, int width, int height) {
        Bitmap bitmap = drawableToBitmap(drawable);
        assert bitmap != null;
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    /**
     * Creates and returns a {@link Bitmap} from the given drawable.
     */
    @Nullable public static Bitmap drawableToBitmap(@NonNull Drawable drawable) {
        Bitmap bitmap;
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

    /**
     * Creates a {@link ClickableSpan} with the given foreground color, click listener and the given
     * span range of the text.
     *
     * @param view the TextView to create the ClickableSpan
     * @param fgColor the foreground color of the ClickableSpan
     * @param cbClick the OnClickListener of the ClickableSpan
     * @param range the start and end position of the Span
     * @see SpannableString#setSpan(Object, int, int, int)
     */
    public static void setClickableSpanOf(TextView view, @ColorRes int fgColor,
            View.OnClickListener cbClick, int... range) {
        String label = (String) view.getText();
        view.setClickable(true);
        SpannableString ss = new SpannableString(label);
        ClickableSpan span = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                cbClick.onClick(textView);
            }
        };
        ss.setSpan(span, 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int color = ContextCompat.getColor(view.getContext(),
                fgColor != 0 ? fgColor : R.color.colorAccent);
        int start = range.length > 0 ? range[0] : 0;
        int end = range.length > 1 ? range[1] : label.length();
        ss.setSpan(new ForegroundColorSpan(color),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(ss);
    }

    /**
     * Returns a list of Views with type specified by the given class for the given parent
     * ViewGroup, specified by
     * the given class.
     */
    public static <T extends View> List<T> findViewsByClass(View parent,
            Class<T> clazz) {
        List<T> children = Lists.newArrayList();
        if (clazz.isInstance(parent)) {
            children.add((T) parent);
        } else if (parent instanceof ViewGroup) {
            int childCount = ((ViewGroup) parent).getChildCount();
            for (int i = 0, len = childCount; i < len; i++) {
                View child = ((ViewGroup) parent).getChildAt(i);
                if (clazz.isInstance(child)) {
                    children.add((T) child);
                } else if (child instanceof ViewGroup) {
                    children.addAll(findViewsByClass((ViewGroup) child, clazz));
                }
            }
        }
        return children;
    }

    /**
     * {@link Transformation} sub class to set the image width to the specified target width
     * and its height is resized to preserve its aspect rations.
     */
    public static class FixedWidthResizeTransform implements Transformation {

        final int mTargetWidth;

        /**
         * Constructor with the give fixed target width.
         */
        public FixedWidthResizeTransform(final int targetWidth) {
            this.mTargetWidth = targetWidth;
        }

        /**
         * Transform the source bitmap into a new bitmap. If you create a new bitmap instance, you
         * must
         * call {@link android.graphics.Bitmap#recycle()} on {@code source}. You may return the
         * original
         * if no transformation is required.
         */
        @Override public Bitmap transform(final Bitmap source) {
            return getTransformedBitmap(source);
        }

        /**
         * Returns resized bitmap from the given bitmap.
         */
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

        /**
         * Returns a unique key for the transformation, used for caching purposes. If the
         * transformation
         * has parameters (e.g. size, scale factor, etc) then these should be part of the key.
         */
        @Override public String key() {
            return String.format(Locale.getDefault(), "%s_%d", getClass().getSimpleName(),
                    mTargetWidth);
        }
    }
}

