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
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import androidx.annotation.AnimRes;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import com.google.android.material.appbar.AppBarLayout;
import com.neatier.widgets.R;
import rx.functions.Action1;

/**
 * Helper class containing Animation related helper functions.
 *
 * @author László Gálosi
 * @since 26/08/15
 */
@SuppressLint("ObsoleteSdkInt")
public class AnimationUtils {

    private static final boolean HONEYCOMB_AND_ABOVE =
          Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    public static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR =
          new FastOutSlowInInterpolator();
    public static final Interpolator FAST_OUT_LINEAR_IN_INTERPOLATOR =
          new FastOutLinearInInterpolator();
    public static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();

    /**
     * Performs a fade animation for the target view, for {@link Build.VERSION#SDK_INT} >= 14.
     *
     * @param targetView the animated view
     * @param endAlphaValue the ending alpha of the fade animation
     * @param interpolator the animation interpolator.
     * @param listener the animation listener
     */
    public static ViewPropertyAnimatorCompat fadeCompat(View targetView, float endAlphaValue,
            Interpolator interpolator, ViewPropertyAnimatorListener listener) {
        return ViewCompat.animate(targetView)
                         .alpha(endAlphaValue)
                         .setInterpolator(interpolator)
                         .withLayer()
                         .setListener(listener);
    }

    /**
     * Performs a fade animation for the target view, for {@link Build.VERSION#SDK_INT} < 14.
     *
     * @param targetView the animated view
     * @param animResId the animation resource to load
     * @param interpolator the animation interpolator.
     * @param listener the animation listener
     * @link duration the duration of the fade animation
     */
    public static Animation fadeLegacy(View targetView, @AnimRes int animResId, long duration,
            Interpolator interpolator, Animation.AnimationListener listener) {
        Animation animation =
                android.view.animation.AnimationUtils.loadAnimation(targetView.getContext(),
                        animResId);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        animation.setAnimationListener(listener);
        return animation;
    }

    /**
     * Performs a rotation animation for the target view, for {@link Build.VERSION#SDK_INT} >= 14.
     *
     * @param targetView the animated view
     * @param endDegree the ending angle in degrees of the animation
     * @param duration the duration of the animation.
     * @param interpolator the animation interpolator.
     * @param listener the animation listener
     */
    public static ViewPropertyAnimatorCompat rotateCompat(View targetView, float endDegree,
            long duration,
            Interpolator interpolator, ViewPropertyAnimatorListener listener) {
        return ViewCompat.animate(targetView)
                .rotation(endDegree)
                .setInterpolator(interpolator)
                .setDuration(duration)
                .withLayer()
                .setListener(listener);
    }

    /**
     * Performs a rotation animation for the target view, for {@link Build.VERSION#SDK_INT} < 14.
     *
     * @param targetView the animated view
     * @param toDegree the ending angle in degrees of the animation
     * @param duration the duration of the animation.
     * @param interpolator the animation interpolator.
     * @param listener the animation listener
     */
    public static Animation rotateLegacy(View targetView, float toDegree, long duration,
            Interpolator interpolator, Animation.AnimationListener listener) {
        RotateAnimation animation = new RotateAnimation(targetView.getRotation(), toDegree);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        animation.setAnimationListener(listener);
        return animation;
    }

    /**
     * Loads the given animation resource.
     *
     * @see AnimationUtils#loadAnimation(Context, int)
     */
    public static Animation loadAnimation(final Context context, final int animResId) {
        return android.view.animation.AnimationUtils.loadAnimation(context, animResId);
    }

    AnimationUtils() {
    }

    static float lerp(float startValue, float endValue, float fraction) {
        return startValue + fraction * (endValue - startValue);
    }

    static int lerp(int startValue, int endValue, float fraction) {
        return startValue + Math.round(fraction * (float) (endValue - startValue));
    }

    /**
     * {@link Animation.AnimationListener} adapter.
     */
    public static class AnimationListenerAdapter implements Animation.AnimationListener {
        public AnimationListenerAdapter() {
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    /**
     * Performs animation which performs a scale x and y up or down and simoultanosly fades in or
     * out. Same animation that FloatingActionButton.Behavior uses to show the FAB when the
     * {@link AppBarLayout} enters or exits.
     */
    @SuppressLint("PrivateResource")
    public static class GrowShrinkAnimator<V extends View> {

        private Interpolator mInterpolator = FAST_OUT_SLOW_IN_INTERPOLATOR;
        private boolean mIsAnimatingOut;
        private @AnimRes int mInAnimation = R.anim.design_fab_in;
        private @AnimRes int mOutAnimation = R.anim.design_fab_out;
        private AnimationCompatCallback[] mAnimationCompatCallbacks;

        public GrowShrinkAnimator() {
        }

        @SuppressLint("ObsoleteSdkInt")
        public void animateOut(final V view) {
            if (Build.VERSION.SDK_INT >= 14) {
                ViewCompat.animate(view)
                        .scaleX(0.0F)
                        .scaleY(0.0F)
                        .alpha(0.0F)
                        .setInterpolator(mInterpolator)
                        .withLayer()
                        .setListener(new ViewPropertyAnimatorListener() {
                            public void onAnimationStart(View view) {
                                mIsAnimatingOut = true;
                                callBackOnAnimationStart(view, mAnimationCompatCallbacks[1]);
                            }

                            public void onAnimationEnd(View view) {
                                mIsAnimatingOut = false;
                                //view.setVisibility(View.GONE);
                                callBackOnAnimationEnd(view, mAnimationCompatCallbacks[1]);
                            }

                            public void onAnimationCancel(View view) {
                                mIsAnimatingOut = false;
                                callBackOnAnimationCancel(view, mAnimationCompatCallbacks[1]);
                            }
                        })
                        .start();
            } else {
                Animation anim = AnimationUtils.loadAnimation(view.getContext(), mOutAnimation);
                anim.setInterpolator(mInterpolator);
                anim.setDuration(200L);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        mIsAnimatingOut = true;
                        callBackOnAnimationStart(view, mAnimationCompatCallbacks[1]);
                    }

                    public void onAnimationEnd(Animation animation) {
                        mIsAnimatingOut = false;
                        //view.setVisibility(View.GONE);
                        callBackOnAnimationEnd(view, mAnimationCompatCallbacks[1]);
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {
                        callBackOnAnimationRepeat(view, mAnimationCompatCallbacks[1]);
                    }
                });
                view.startAnimation(anim);
            }
        }

        /**
         * Same animation that FloatingActionButton.Behavior uses to show the FAB when the {@link
         * AppBarLayout} enters.
         */
        public void animateIn(final V view) {
            view.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= 14) {
                ViewCompat.animate(view).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                        .setInterpolator(mInterpolator).withLayer().setListener(null)
                        .setListener(new ViewPropertyAnimatorListener() {
                            public void onAnimationStart(View view) {
                                callBackOnAnimationStart(view, mAnimationCompatCallbacks[0]);
                            }

                            public void onAnimationEnd(View view) {
                                callBackOnAnimationEnd(view, mAnimationCompatCallbacks[0]);
                            }

                            public void onAnimationCancel(View view) {
                                callBackOnAnimationCancel(view, mAnimationCompatCallbacks[0]);
                            }
                        })
                        .start();
            } else {
                Animation anim = AnimationUtils.loadAnimation(view.getContext(), mInAnimation);
                anim.setDuration(200L);
                anim.setInterpolator(mInterpolator);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        callBackOnAnimationStart(view, mAnimationCompatCallbacks[0]);
                    }

                    public void onAnimationEnd(Animation animation) {
                        callBackOnAnimationEnd(view, mAnimationCompatCallbacks[0]);
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {
                        callBackOnAnimationRepeat(view, mAnimationCompatCallbacks[0]);
                    }
                });
                view.startAnimation(anim);
            }
        }

        public GrowShrinkAnimator inAnimation(final int inAnimation) {
            mInAnimation = inAnimation;
            return this;
        }

        public GrowShrinkAnimator setInterpolator(final Interpolator interpolator) {
            mInterpolator = interpolator;
            return this;
        }

        public GrowShrinkAnimator outAnimation(final int outAnimation) {
            mOutAnimation = outAnimation;
            return this;
        }

        public GrowShrinkAnimator setAnimationCompatCallback(
                final AnimationCompatCallback... animationCompatCallbacks) {
            mAnimationCompatCallbacks = animationCompatCallbacks;
            return this;
        }
    }

    /**
     * Performs a rotation animation for the target view
     *
     * @see #rotateCompat(View, float, long, Interpolator, ViewPropertyAnimatorListener)
     * @see #rotateLegacy(View, float, long, Interpolator, Animation.AnimationListener)
     */
    public static class RotateAnimator<V extends View> {

        private Interpolator mInterpolator = FAST_OUT_SLOW_IN_INTERPOLATOR;
        private AnimationCompatCallback[] mAnimationCompatCallbacks;
        private long mDuration = 250;
        private float mToDegree = 180.0f;
        private boolean mRotating;
        private boolean mRotatationCompleted;

        public RotateAnimator() {
        }

        public void rotate(View targetView) {
            if (Build.VERSION.SDK_INT >= 14) {
                ViewCompat.animate(targetView)
                        .rotation(mToDegree)
                        .setInterpolator(mInterpolator)
                        .setDuration(mDuration)
                        .withLayer()
                        .setListener(new ViewPropertyAnimatorListener() {
                            public void onAnimationStart(View view) {
                                mRotating = true;
                                callBackOnAnimationStart(targetView,
                                        mAnimationCompatCallbacks[0]);
                            }

                            public void onAnimationEnd(View view) {
                                mRotating = false;
                                mRotatationCompleted = true;
                                //view.setVisibility(View.GONE);
                                callBackOnAnimationEnd(targetView,
                                        mAnimationCompatCallbacks[0]);
                            }

                            public void onAnimationCancel(View view) {
                                mRotating = false;
                                mRotatationCompleted = false;
                                callBackOnAnimationCancel(view, mAnimationCompatCallbacks[0]);
                            }
                        }).start();
            } else {
                rotateLegacy(targetView, mToDegree, mDuration, mInterpolator,
                        new Animation.AnimationListener() {
                            @Override public void onAnimationStart(final Animation animation) {
                                mRotating = true;
                                callBackOnAnimationStart(targetView,
                                        mAnimationCompatCallbacks[1]);
                            }

                            @Override public void onAnimationEnd(final Animation animation) {
                                mRotating = false;
                                mRotatationCompleted = true;
                                //view.setVisibility(View.GONE);
                                callBackOnAnimationEnd(targetView,
                                        mAnimationCompatCallbacks[1]);
                            }

                            @Override
                            public void onAnimationRepeat(final Animation animation) {

                            }
                        }).start();
            }
        }

        public RotateAnimator withInterpolator(final Interpolator interpolator) {
            mInterpolator = interpolator;
            return this;
        }

        public RotateAnimator withDuration(final long duration) {
            mDuration = duration;
            return this;
        }

        public RotateAnimator toDegree(final float toDegree) {
            mToDegree = toDegree;
            return this;
        }

        public RotateAnimator setAnimationCompatCallback(
                final AnimationCompatCallback... animationCompatCallbacks) {
            mAnimationCompatCallbacks = animationCompatCallbacks;
            return this;
        }

        public boolean isRotating() {
            return mRotating;
        }

        public boolean isRotatationCompleted() {
            return mRotatationCompleted;
        }

        public float getToDegree() {
            return mToDegree;
        }
    }

    /**
     * A call back for animations which defines {@link Animation.AnimationListener}
     * like callback with {@link Action1}
     * <ul>
     * <li>ActionOnAnimationStart - called when the animation starts</li>
     * <li>ActionOnAnimationEnd - called when the animation finishes</li>
     * <li>ActionOnAnimationCancel - called when the animation canceled</li>
     * <li>ActionOnAnimationRepeat - called when the animation repeats</li>
     * </ul>
     *
     * @see Animation.AnimationListener#onAnimationStart(Animation)
     * @see Animation.AnimationListener#onAnimationEnd(Animation)
     */
    public static class AnimationCompatCallback<V extends View> {
        @Nullable Action1<V> mActionOnAnimationStart;
        @Nullable Action1<V> mActionOnAnimationEnd;
        @Nullable Action1<V> mActionOnAnimationCancel;
        @Nullable Action1<V> mActionOnAnimationRepeat;

        public AnimationCompatCallback() {
        }

        public AnimationCompatCallback setActionOnAnimationStart(
                @Nullable final Action1<V> actionOnAnimationStart) {
            mActionOnAnimationStart = actionOnAnimationStart;
            return this;
        }

        public AnimationCompatCallback setActionOnAnimationEnd(
                @Nullable final Action1<V> actionOnAnimationEnd) {
            mActionOnAnimationEnd = actionOnAnimationEnd;
            return this;
        }

        public AnimationCompatCallback setActionOnAnimationCancel(
                @Nullable final Action1<V> actionOnAnimationCancel) {
            mActionOnAnimationCancel = actionOnAnimationCancel;
            return this;
        }

        public AnimationCompatCallback setActionOnAnimationRepeat(
                @Nullable final Action1<V> actionOnAnimationRepeat) {
            mActionOnAnimationRepeat = actionOnAnimationRepeat;
            return this;
        }
    }

    /**
     * Sets the given {@link AnimationCompatCallback} for the given view, called when the animation
     * starts.
     */
    @SuppressWarnings("unchecked")
    public static void callBackOnAnimationStart(final View view, AnimationCompatCallback callback) {
        if (callback != null && callback.mActionOnAnimationStart != null) {
            callback.mActionOnAnimationStart.call(view);
        }
    }

    /**
     * Sets the given {@link AnimationCompatCallback} for the given view, called when the animation
     * finishes.
     */
    @SuppressWarnings("unchecked")
    public static void callBackOnAnimationEnd(final View view, AnimationCompatCallback callback) {
        if (callback != null && callback.mActionOnAnimationEnd != null) {
            callback.mActionOnAnimationEnd.call(view);
        }
    }

    /**
     * Sets the given {@link AnimationCompatCallback} for the given view, called when the animation
     * canceled.
     */
    @SuppressWarnings("unchecked")
    public static void callBackOnAnimationCancel(final View view,
            AnimationCompatCallback callback) {
        if (callback != null && callback.mActionOnAnimationCancel != null) {
            callback.mActionOnAnimationCancel.call(view);
        }
    }

    /**
     * Sets the given {@link AnimationCompatCallback} for the given view, called when the animation
     * repeats.
     */
    @SuppressWarnings("unchecked")
    public static void callBackOnAnimationRepeat(final View view,
            AnimationCompatCallback callback) {
        if (callback != null && callback.mActionOnAnimationRepeat != null) {
            callback.mActionOnAnimationRepeat.call(view);
        }
    }
}

