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

package com.neatier.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

/**
 * An {@link AppCompatImageView} subclass view with circle shaped mask and shadow, for its
 * ImageDrawable.
 *
 * @author László Gálosi
 * @since 28/11/16
 */
public class ColoredCircleImageView extends AppCompatImageView {

    /**
     * Constant for the shadow edge color.
     */
    private static final int KEY_SHADOW_COLOR = 0x1E000000;

    /**
     * Constant for the shadow fill color.
     */
    private static final int FILL_SHADOW_COLOR = 0x3D000000;

    /**
     * X offset for the shadow.
     */
    private static final float X_OFFSET = 0f;

    /**
     * Constant for the Y offset for the shadow.
     */
    private static final float Y_OFFSET = 1.75f;

    /**
     * Constant for the shadow radius.
     */
    private static final float SHADOW_RADIUS = 3.5f;

    /**
     * Constant for the elevation of the shadow.
     */
    private static final int SHADOW_ELEVATION = 4;

    /**
     * The radius of the shadow.
     */
    int mShadowRadius;

    /**
     * The filling color of the circle.
     */
    private @ColorInt int mFillColor;

    /**
     * Animation listener.
     */
    private Animation.AnimationListener mListener;

    public ColoredCircleImageView(final Context context) {
        this(context, null);
    }

    public ColoredCircleImageView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColoredCircleImageView(final Context context, final AttributeSet attrs,
          final int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Creates a shadowed circle image view with the given style attribute set
     *
     * @param defStyleAttr contains {@code ColoredCircleImageView_cciv_fillColor} defining
     * the custom fill color of this circle image view.
     */
    public ColoredCircleImageView(final Context context, final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes) {
        super(context, attrs, defStyleAttr);
        TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.ColoredCircleImageView,
                        defStyleAttr,
                        0);
        mFillColor = a.getColor(R.styleable.ColoredCircleImageView_cciv_fillColor, Color.WHITE);
        a.recycle();
        initView();
    }

    /**
     * Initializes the {@link Paint} of the circle shapes, and shadows by the member fields.
     */
    public void initView() {
        final float density = getContext().getResources().getDisplayMetrics().density;
        final int shadowYOffset = (int) (density * Y_OFFSET);
        final int shadowXOffset = (int) (density * X_OFFSET);

        mShadowRadius = (int) (density * SHADOW_RADIUS);

        ShapeDrawable circle;
        if (elevationSupported()) {
            circle = new ShapeDrawable(new OvalShape());
            ViewCompat.setElevation(this, SHADOW_ELEVATION * density);
        } else {
            OvalShape oval = new OvalShadow(mShadowRadius);
            circle = new ShapeDrawable(oval);
            setLayerType(View.LAYER_TYPE_SOFTWARE, circle.getPaint());
            circle.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset,
                    KEY_SHADOW_COLOR);
            final int padding = mShadowRadius;
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding);
        }
        circle.getPaint().setColor(mFillColor);
        ViewCompat.setBackground(this, circle);
    }

    /**
     * Returns true if the elevation is supported by the android framework {@link
     * android.os.Build.VERSION#SDK_INT >= 21}
     */
    private boolean elevationSupported() {
        return android.os.Build.VERSION.SDK_INT >= 21;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!elevationSupported()) {
            setMeasuredDimension(getMeasuredWidth() + mShadowRadius * 2, getMeasuredHeight()
                  + mShadowRadius * 2);
        }
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    public void setBackgroundColorRes(int colorRes) {
        setBackgroundColor(ContextCompat.getColor(getContext(), colorRes));
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param color the color of the background.
     */
    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    /**
     * Called when this view is to be animated, it also calls its {@link
     * Animation.AnimationListener#onAnimationStart(Animation)} if set previously.
     */
    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    /**
     * Called when this view is finished  its animation. Tt also calls its {@link
     * Animation.AnimationListener#onAnimationEnd(Animation)} if set previously.
     */
    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }

    /**
     * An oval shaped shadow shape which draws a a shadow around itself.
     */
    private class OvalShadow extends OvalShape {
        /**
         * The shadow gradient.
         */
        private RadialGradient mRadialGradient;

        /**
         * The paint of the shadow to be drawn om the canvas.
         */
        private Paint mShadowPaint;

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            updateRadialGradient((int) width);
        }

        /**
         * Set a gradient shader computed from the given diameter.
         *
         * @param diameter the diameter for the gradient to compute.
         */
        private void updateRadialGradient(int diameter) {
            mRadialGradient = new RadialGradient(diameter / 2, diameter / 2,
                                                 mShadowRadius,
                                                 new int[] { FILL_SHADOW_COLOR, Color.TRANSPARENT },
                                                 null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }

        /**
         * Draws the oval shaped shadow around its canvas with the given paint.
         */
        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = ColoredCircleImageView.this.getWidth();
            final int viewHeight = ColoredCircleImageView.this.getHeight();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2, mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2 - mShadowRadius, paint);
        }

        /**
         * Constructor with the shadow radius.
         */
        OvalShadow(int shadowRadius) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            updateRadialGradient((int) rect().width());
        }
    }
}

