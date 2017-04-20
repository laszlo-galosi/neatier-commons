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

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.neatier.widgets.helpers.WidgetUtils;
import java.util.Arrays;
import oxim.digital.rxanim.RxAnimationBuilder;
import oxim.digital.rxanim.RxValueAnimator;
import rx.Completable;
import trikita.log.Log;

/**
 * Created by bruce on 14-10-30.
 */
public class DonutProgress extends FrameLayout {

    private static final String PROPERTY_PROGRESS = "progress";

    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_FINISHED_STROKE_COLOR = "finished_stroke_color";
    private static final String INSTANCE_UNFINISHED_STROKE_COLOR = "unfinished_stroke_color";
    private static final String INSTANCE_MAX = "mMax";
    private static final String INSTANCE_PROGRESS = "mProgress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_LABEL = "prefix";
    private static final String INSTANCE_FINISHED_STROKE_WIDTH = "finished_stroke_width";
    private static final String INSTANCE_UNFINISHED_STROKE_WIDTH = "unfinished_stroke_width";
    private static final String INSTANCE_BACKGROUND_COLOR = "inner_background_color";
    private static final String INSTANCE_LABEL_TEXT_SIZE = "label_text_size";
    private static final String INSTANCE_SUFFIX_TEXT_SIZE = "suffix_text_size";
    private static final String INSTANCE_LABEL_TEXT_COLOR = "label_text_color";
    private static final String INSTANCE_SUFFIX_TEXT_COLOR = "suffix_text_color";
    private static final String INSTANCE_PROGRESS_TEXT_FORMAT = "progress_text_format";
    private static final String INSTANCE_ANIM_DURATION = "anim_duration";
    private static final String INSTANCE_ANIM_INTERPOLATOR = "anim_interpolator";

    @Nullable @BindView(R2.id.prefix) TextView mLabelTextView;
    @Nullable @BindView(R2.id.progress) TextView mProgressTextView;
    @Nullable @BindView(R2.id.suffix) TextView mSuffixView;

    private final float mDefaultStrokeWidth;
    private final int mDefaultFinishedColor = Color.rgb(66, 145, 241);
    private final int mDefaultUnfinishedColor = Color.rgb(204, 204, 204);
    private final int mDefaultTextColor = Color.rgb(66, 145, 241);
    private final int mDefaultInnerBackgroundColor = Color.TRANSPARENT;
    private final int mDefaultMax = 100;
    private final float mDefaultTextSize;
    private final int mMinSize;
    protected Paint mTextPaint;
    protected Paint mSuffixTextPaint;
    protected Paint mLabelTextPaint;
    private Paint mFinishedPaint;
    private Paint mUnfinishedPaint;
    private Paint mInnerCirclePaint;
    private RectF mFinishedOuterRect = new RectF();
    private RectF mUnfinishedOuterRect = new RectF();
    private float mTextSize;
    private int mTextColor;
    private float mProgress = 0.0f;
    private int mMax;
    private int mFinishedStrokeColor;
    private int mUnfinishedStrokeColor;
    private float mFinishedStrokeWidth;
    private float mUnfinishedStrokeWidth;
    private int mInnerBackgroundColor;
    private String mProgressTextFormat = "%.0f";
    private String mSuffixText = "";
    private float mSuffixTextSize;
    private int mSuffixTextColor;
    private String mLabelText = "";
    private float mLabelTextSize;
    private int mLabelTextColor;
    private int mWidgetLayout;
    private View mContentView;
    private Unbinder mUnbinder;
    private int mWidth;
    private int mHeight;
    private float mStartAngle;
    private float mDefaultStartAngle = 0.0f;
    private long mAnimDuration;
    private Interpolator mAnimationInterPolator;
    private ObjectAnimator mProgressAnimator;
    private float[] mProgressValuesOnAttach;
    private int mAnimInterpolatorRes;
    private Completable mProgressAnimationCompletable;

    public DonutProgress(Context context) {
        this(context, null);
    }

    public DonutProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DonutProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDefaultTextSize = ThemeUtil.spToPx(context, 18);
        mMinSize = (int) ThemeUtil.dpToPx(context, 126);
        mWidth = mMinSize;
        mHeight = mMinSize;
        mDefaultStrokeWidth = ThemeUtil.dpToPx(context, 10);
        TypedArray a =
              context.obtainStyledAttributes(attrs, R.styleable.DonutProgress, defStyleAttr, 0);
        initByAttributes(a);
        a.recycle();
        initView(context);
    }

    protected void initByAttributes(TypedArray attributes) {
        mFinishedStrokeColor = attributes.getColor(R.styleable.DonutProgress_finishedColor,
                                                   mDefaultFinishedColor);
        mUnfinishedStrokeColor = attributes.getColor(R.styleable.DonutProgress_unfinishedColor,
                                                     mDefaultUnfinishedColor);
        mTextColor = attributes.getColor(R.styleable.DonutProgress_progressTextColor,
                                         mDefaultTextColor);
        mLabelTextColor = attributes.getColor(R.styleable.DonutProgress_labelTextColor,
                                              mDefaultTextColor);
        mSuffixTextColor = attributes.getColor(R.styleable.DonutProgress_labelTextColor,
                                               mDefaultTextColor);
        mTextSize = attributes.getDimension(R.styleable.DonutProgress_progressTextSize,
                                            mDefaultTextSize);
        mSuffixTextSize = attributes.getDimension(R.styleable.DonutProgress_suffixTextSize,
                                                  mDefaultTextSize);
        mLabelTextSize = attributes.getDimension(R.styleable.DonutProgress_labelTextSize,
                                                 mDefaultTextSize);

        setMax(attributes.getInt(R.styleable.DonutProgress_maxValue, mDefaultMax));
        setProgress(attributes.getFloat(R.styleable.DonutProgress_progressValue, 0.0f));
        mFinishedStrokeWidth =
              attributes.getDimension(R.styleable.DonutProgress_finishedStrokeWidth,
                                      mDefaultStrokeWidth);
        mUnfinishedStrokeWidth =
              attributes.getDimension(R.styleable.DonutProgress_unfinishedStrokeWidth,
                                      mDefaultStrokeWidth);
        if (attributes.getString(R.styleable.DonutProgress_progressTextFormat) != null) {
            mProgressTextFormat =
                  attributes.getString(R.styleable.DonutProgress_progressTextFormat);
        }
        if (attributes.getString(R.styleable.DonutProgress_labelText) != null) {
            mLabelText = attributes.getString(R.styleable.DonutProgress_labelText);
        }
        if (attributes.getString(R.styleable.DonutProgress_suffixText) != null) {
            mSuffixText = attributes.getString(R.styleable.DonutProgress_suffixText);
        }
        mInnerBackgroundColor =
              attributes.getColor(R.styleable.DonutProgress_innerCircleBackgroundColor,
                                  mDefaultInnerBackgroundColor);

        if (attributes.getResourceId(R.styleable.DonutProgress_donut_widgetLayout, 0) > 0) {
            mWidgetLayout =
                  attributes.getResourceId(R.styleable.DonutProgress_donut_widgetLayout, 0);
        }
        mStartAngle = attributes.getFloat(R.styleable.DonutProgress_startAngle, mDefaultStartAngle);
        long defaultAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
        mAnimDuration =
              attributes.getInt(R.styleable.DonutProgress_animDuration, (int) defaultAnimTime);
        mAnimInterpolatorRes =
              attributes.getResourceId(R.styleable.DonutProgress_animInterpolator,
                                       android.R.anim.decelerate_interpolator);
        mAnimationInterPolator =
              AnimationUtils.loadInterpolator(getContext(), mAnimInterpolatorRes);
    }

    private void initView(Context context) {
        setWillNotDraw(false);
        if (mWidgetLayout > 0) {
            removeAllViews();
            mContentView = LayoutInflater.from(getContext()).inflate(mWidgetLayout, this, false);
            WidgetUtils.setLayoutSizeOf(mContentView, LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
            addView(mContentView);
            mUnbinder = ButterKnife.bind(this, this);
        }
        initPainters();
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        if (this.mProgress > getMax()) {
            this.mProgress %= getMax();
        }
        Log.v("setProgress", mProgress);
        updateViews();
        invalidate();
    }

    protected void initPainters() {
        mTextPaint = new TextPaint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAntiAlias(true);

        mLabelTextPaint = new TextPaint();
        mLabelTextPaint.setColor(mLabelTextColor);
        mLabelTextPaint.setTextSize(mLabelTextSize);
        mLabelTextPaint.setAntiAlias(true);

        mSuffixTextPaint = new TextPaint();
        mSuffixTextPaint.setColor(mSuffixTextColor);
        mSuffixTextPaint.setTextSize(mSuffixTextSize);
        mSuffixTextPaint.setAntiAlias(true);

        mFinishedPaint = new Paint();
        mFinishedPaint.setColor(mFinishedStrokeColor);
        mFinishedPaint.setStyle(Paint.Style.STROKE);
        mFinishedPaint.setAntiAlias(true);
        mFinishedPaint.setStrokeWidth(mFinishedStrokeWidth);

        mUnfinishedPaint = new Paint();
        mUnfinishedPaint.setColor(mUnfinishedStrokeColor);
        mUnfinishedPaint.setStyle(Paint.Style.STROKE);
        mUnfinishedPaint.setAntiAlias(true);
        mUnfinishedPaint.setStrokeWidth(mUnfinishedStrokeWidth);

        mInnerCirclePaint = new Paint();
        mInnerCirclePaint.setColor(mInnerBackgroundColor);
        mInnerCirclePaint.setAntiAlias(true);
    }

    public int getMax() {
        return mMax;
    }

    private void updateViews() {
        if (!TextUtils.isEmpty(mLabelText)) {
            WidgetUtils.setTextOf(mLabelTextView, mLabelText);
            ;
            WidgetUtils.setTextColorOf(mLabelTextView, mLabelTextColor);
        }

        if (!TextUtils.isEmpty(mProgressTextFormat)) {
            WidgetUtils.setTextOf(mProgressTextView, String.format(mProgressTextFormat, mProgress));
            WidgetUtils.setTextColorOf(mProgressTextView, mTextColor);
        }
        if (!TextUtils.isEmpty(mSuffixText)) {
            WidgetUtils.setTextOf(mSuffixView, mSuffixText);
            WidgetUtils.setTextColorOf(mSuffixView, mSuffixTextColor);
        }
        invalidate();
    }

    public void setMax(int max) {
        if (max > 0) {
            this.mMax = max;
            invalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void syncAnim(final Animator animator) {
        animator.setStartDelay(mAnimDuration);
        animator.setDuration(mAnimDuration);
        animator.setInterpolator(mAnimationInterPolator);
    }

    public void syncAnimBuilder(final RxAnimationBuilder animator) {
        animator.delay((int) mAnimDuration)
                .duration((int) mAnimDuration)
                .interpolator(mAnimationInterPolator);
    }

    public float getProgress() {
        return mProgress;
    }

    public Completable ensureAnimators(float[] values) {
        mProgressAnimationCompletable = createAnimator(values);
        return mProgressAnimationCompletable;
    }

    private Completable createAnimator(final float[] animValues) {
        Log.d("Creating animator", "interpolator", mAnimationInterPolator, "duration",
              mAnimDuration, "range", Arrays.toString(animValues));
        //object animator animates goalCompletion value resulting an animated progress view.
        mProgressAnimator =
              ObjectAnimator.ofFloat(this, PROPERTY_PROGRESS, animValues);
        mProgressAnimator.setStartDelay(mAnimDuration);
        mProgressAnimator.setDuration(mAnimDuration);
        mProgressAnimator.setInterpolator(mAnimationInterPolator);
        //Now ensure the two animation run in sequence.
        return RxValueAnimator.from(mProgressAnimator, animator -> {
        }).schedule();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mProgressAnimator != null) {
            mProgressAnimator.end();
        }
        mUnbinder.unbind();
        super.onDetachedFromWindow();
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = Math.max(mMinSize, w);
        mHeight = Math.max(mMinSize, h);
        updateViews();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        if (mContentView == null) {
            drawTexts(canvas);
        } /*else {
            mContentView.draw(canvas);
        }*/
    }

    private void drawArc(final Canvas canvas) {
        float delta = Math.max(mFinishedStrokeWidth, mUnfinishedStrokeWidth);
        mFinishedOuterRect.set(delta,
                               delta,
                               mWidth - delta,
                               mHeight - delta);
        mUnfinishedOuterRect.set(delta,
                                 delta,
                                 mWidth - delta,
                                 mHeight - delta);

        float innerCircleRadius = (mWidth - Math.min(mFinishedStrokeWidth,
                                                     mUnfinishedStrokeWidth) + Math.abs(
              mFinishedStrokeWidth - mUnfinishedStrokeWidth)) / 2f;
        canvas.drawCircle(mWidth / 2.0f, mHeight / 2.0f, innerCircleRadius, mInnerCirclePaint);

        drawArc(mFinishedOuterRect, mStartAngle, getProgressAngle(), mFinishedPaint, canvas);
        drawArc(mUnfinishedOuterRect, mStartAngle + getProgressAngle(),
                360 - getProgressAngle(), mUnfinishedPaint, canvas);
    }

    private void drawTexts(final Canvas canvas) {
        if (!TextUtils.isEmpty(mLabelText)) {
            float textHeight = mLabelTextPaint.descent() + mLabelTextPaint.ascent();
            canvas.drawText(mLabelText,
                            (mWidth - mLabelTextPaint.measureText(mLabelText)) / 2.0f,
                            (mWidth - textHeight) / 2.0f,
                            mLabelTextPaint);
        }
        String text = String.format(mProgressTextFormat, mProgress);
        if (!TextUtils.isEmpty(text)) {
            float textHeight = mTextPaint.descent() + mTextPaint.ascent();
            canvas.drawText(text, (mWidth - mTextPaint.measureText(text)) / 2.0f,
                            (mWidth - textHeight) / 2.0f,
                            mTextPaint);
        }
        if (!TextUtils.isEmpty(mSuffixText)) {
            float textHeight = mSuffixTextPaint.descent() + mSuffixTextPaint.ascent();
            canvas.drawText(mSuffixText,
                            (mWidth - mSuffixTextPaint.measureText(mSuffixText)) / 2.0f,
                            (mWidth - textHeight) / 2.0f,
                            mSuffixTextPaint);
        }
    }

    private void drawArc(RectF rect, float startAngle, float endAngle, Paint paint, Canvas canvas) {
        float sweepAngle = endAngle;
        //Log.v("drawArc", String.format("%.02f, %.02f", sweepAngle, endAngle));
        canvas.drawArc(rect, startAngle, sweepAngle, false, paint);
    }

    private float getProgressAngle() {
        return getProgress() / (float) mMax * 360f;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());

        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getTextSize());

        bundle.putInt(INSTANCE_LABEL_TEXT_COLOR, getLabelTextColor());
        bundle.putInt(INSTANCE_SUFFIX_TEXT_COLOR, getSuffixTextColor());
        bundle.putFloat(INSTANCE_LABEL_TEXT_SIZE, getLabelTextSize());
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_SIZE, getSuffixTextSize());

        bundle.putInt(INSTANCE_FINISHED_STROKE_COLOR, getFinishedStrokeColor());
        bundle.putInt(INSTANCE_UNFINISHED_STROKE_COLOR, getUnfinishedStrokeColor());

        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putFloat(INSTANCE_PROGRESS, getProgress());

        bundle.putString(INSTANCE_PROGRESS_TEXT_FORMAT, getProgressTextFormat());
        bundle.putString(INSTANCE_SUFFIX, getSuffixText());
        bundle.putString(INSTANCE_LABEL, getLabelText());

        bundle.putFloat(INSTANCE_FINISHED_STROKE_WIDTH, getFinishedStrokeWidth());
        bundle.putFloat(INSTANCE_UNFINISHED_STROKE_WIDTH, getUnfinishedStrokeWidth());
        bundle.putInt(INSTANCE_BACKGROUND_COLOR, getInnerBackgroundColor());

        bundle.putLong(INSTANCE_ANIM_DURATION, getAnimDuration());
        bundle.putInt(INSTANCE_ANIM_INTERPOLATOR, getAnimInterpolatorRes());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            setTextColor(bundle.getInt(INSTANCE_TEXT_COLOR));
            setTextSize(bundle.getFloat(INSTANCE_TEXT_SIZE));

            setLabelTextColor(bundle.getInt(INSTANCE_LABEL_TEXT_COLOR));
            setSuffixTextColor(bundle.getInt(INSTANCE_SUFFIX_TEXT_COLOR));
            setLabelTextSize(bundle.getFloat(INSTANCE_LABEL_TEXT_SIZE));
            setSuffixTextSize(bundle.getFloat(INSTANCE_SUFFIX_TEXT_SIZE));

            setFinishedStrokeColor(bundle.getInt(INSTANCE_FINISHED_STROKE_COLOR));
            setUnfinishedStrokeColor(bundle.getInt(INSTANCE_UNFINISHED_STROKE_COLOR));

            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getFloat(INSTANCE_PROGRESS));

            setProgressTextFormat(bundle.getString(INSTANCE_PROGRESS_TEXT_FORMAT));
            setSuffixText(bundle.getString(INSTANCE_SUFFIX));
            setLabelText(bundle.getString(INSTANCE_LABEL));

            setFinishedStrokeWidth(bundle.getFloat(INSTANCE_FINISHED_STROKE_WIDTH));
            setUnfinishedStrokeWidth(bundle.getFloat(INSTANCE_UNFINISHED_STROKE_WIDTH));
            setInnerBackgroundColor(bundle.getInt(INSTANCE_BACKGROUND_COLOR));

            setAnimDuration(bundle.getLong(INSTANCE_ANIM_DURATION));
            setAnimInterpolatorRes(bundle.getInt(INSTANCE_ANIM_INTERPOLATOR));

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public int getTextColor() {
        return mTextColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public int getLabelTextColor() {
        return mLabelTextColor;
    }

    public int getSuffixTextColor() {
        return mSuffixTextColor;
    }

    public float getLabelTextSize() {
        return mLabelTextSize;
    }

    public float getSuffixTextSize() {
        return mSuffixTextSize;
    }

    public void setSuffixTextSize(final float suffixTextSize) {
        mSuffixTextSize = suffixTextSize;
        updateViews();
    }

    public int getFinishedStrokeColor() {
        return mFinishedStrokeColor;
    }

    public void setFinishedStrokeColor(int finishedStrokeColor) {
        this.mFinishedStrokeColor = finishedStrokeColor;
        this.mFinishedPaint.setColor(finishedStrokeColor);
        updateViews();
    }

    public int getUnfinishedStrokeColor() {
        return mUnfinishedStrokeColor;
    }

    public String getProgressTextFormat() {
        return mProgressTextFormat;
    }

    public void setProgressTextFormat(final String progressTextFormat) {
        mProgressTextFormat = progressTextFormat;
        updateViews();
    }

    public String getSuffixText() {
        return mSuffixText;
    }

    public void setSuffixText(String suffixText) {
        this.mSuffixText = suffixText;
        updateViews();
    }

    public String getLabelText() {
        return mLabelText;
    }

    public float getFinishedStrokeWidth() {
        return mFinishedStrokeWidth;
    }

    public void setFinishedStrokeWidth(float finishedStrokeWidth) {
        this.mFinishedStrokeWidth = finishedStrokeWidth;
        this.invalidate();
    }

    public float getUnfinishedStrokeWidth() {
        return mUnfinishedStrokeWidth;
    }

    public void setUnfinishedStrokeWidth(float unfinishedStrokeWidth) {
        this.mUnfinishedStrokeWidth = unfinishedStrokeWidth;
        this.invalidate();
    }

    public int getInnerBackgroundColor() {
        return mInnerBackgroundColor;
    }

    public long getAnimDuration() {
        return mAnimDuration;
    }

    public int getAnimInterpolatorRes() {
        return mAnimInterpolatorRes;
    }

    public void setAnimInterpolatorRes(final int animInterpolatorRes) {
        mAnimInterpolatorRes = animInterpolatorRes;
        mAnimationInterPolator =
              AnimationUtils.loadInterpolator(getContext(), mAnimInterpolatorRes);
        if (mProgressAnimator != null) {
            mProgressAnimator.setInterpolator(mAnimationInterPolator);
        }
    }

    public void setAnimDuration(final long animDuration) {
        mAnimDuration = animDuration;
        if (mProgressAnimator != null) {
            mProgressAnimator.setDuration(mAnimDuration);
        }
    }

    public void setInnerBackgroundColor(int innerBackgroundColor) {
        this.mInnerBackgroundColor = innerBackgroundColor;
        mInnerCirclePaint.setColor(this.mInnerBackgroundColor);
        this.invalidate();
    }

    public void setLabelText(String labelText) {
        this.mLabelText = labelText;
        updateViews();
    }

    public void setUnfinishedStrokeColor(int unfinishedStrokeColor) {
        this.mUnfinishedStrokeColor = unfinishedStrokeColor;
        this.mUnfinishedPaint.setColor(unfinishedStrokeColor);
        this.invalidate();
    }

    public void setLabelTextSize(final float labelTextSize) {
        mLabelTextSize = labelTextSize;
    }

    public void setSuffixTextColor(final int suffixTextColor) {
        mSuffixTextColor = suffixTextColor;
        mSuffixTextPaint.setColor(suffixTextColor);
        updateViews();
    }

    public void setLabelTextColor(final int labelTextColor) {
        mLabelTextColor = labelTextColor;
        mLabelTextPaint.setColor(labelTextColor);
        updateViews();
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        updateViews();
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        this.mTextPaint.setColor(textColor);
        updateViews();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
        //take care of paddingTop and paddingBottom
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int paddingY = getPaddingBottom() + getPaddingTop();
        int paddingX = getPaddingLeft() + getPaddingRight();

        measure(widthMeasureSpec);
        measure(heightMeasureSpec);

        //get height and width
        mWidth = MeasureSpec.getSize(widthMeasureSpec) + paddingX;
        mHeight = MeasureSpec.getSize(heightMeasureSpec) + paddingY;
        setMeasuredDimension(mWidth, mHeight);
    }

    /* @Override
       //This causing requestLayout() improperly called warning messages on animation
       protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
         super.onLayout(changed, left, top, right, bottom);
         mWidth = Math.max(mMinSize, right - left);
         mHeight = Math.max(mMinSize, bottom - top);
         updateViews();
     }
 */
    private void measure(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.EXACTLY:
                // Nothing to do
                break;
            case MeasureSpec.AT_MOST:
                measureSpec = MeasureSpec.makeMeasureSpec(
                      Math.min(MeasureSpec.getSize(measureSpec), mMinSize), MeasureSpec.EXACTLY);
                break;
        }
    }

    public void setProgressAnimValues(final float... progressValues) {
        mProgressValuesOnAttach = progressValues;
    }

    public DonutProgress setWidgetLayout(@LayoutRes final int widgetLayout) {
        mWidgetLayout = widgetLayout;
        initView(getContext());
        return this;
    }
}

