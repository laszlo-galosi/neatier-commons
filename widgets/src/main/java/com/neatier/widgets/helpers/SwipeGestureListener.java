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
 *
 *   Delight Solutions Kft.
 */

package com.neatier.widgets.helpers;

import android.view.GestureDetector;
import android.view.MotionEvent;
import com.fernandocejas.arrow.optional.Optional;
import rx.functions.Action1;
import trikita.log.Log;

/**
 * Created by László Gálosi on 23/05/16
 */
public class SwipeGestureListener implements GestureDetector.OnGestureListener {

    private static final int SWIPE_MIN_DISTANCE = 150;
    private static final int SWIPE_MIN_DISTANCE_DP = 50;
    private static final int SWIPE_MAX_OFF_PATH = 100;
    private static final int SWIPE_MAX_OFF_PATH_DP = 250;

    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private static final int SWIPE_THRESHOLD_VELOCITY_DP = 200;
    protected MotionEvent mLastOnDownEvent = null;
    private Optional<Action1<Float>> mDeltaXAction = Optional.absent();
    private Optional<Action1<Float>> mDeltaYAction = Optional.absent();

    public SwipeGestureListener() {
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mLastOnDownEvent = e;
        Log.v("onDown", e);
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.v("onFling", "e1", e1, "e2", e2, "velX", velocityX, "velY", velocityY);
        if (e1 == null) {
            e1 = mLastOnDownEvent;
        }
        if (e1 == null || e2 == null) {
            return false;
        }

        float dX = e2.getX() - e1.getX();
        float dY = e2.getY() - e1.getY();

        if (Math.abs(dY) < SWIPE_MAX_OFF_PATH
              && Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY
              && Math.abs(dX) >= SWIPE_MIN_DISTANCE
              ) {
            if (mDeltaXAction.isPresent()) {
                mDeltaXAction.get().call(dX);
            }
            if (mDeltaYAction.isPresent()) {
                mDeltaYAction.get().call(dY);
            }
            return true;
        }

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    public SwipeGestureListener withDeltaXAction(final Action1<Float> deltaXAction) {
        mDeltaXAction = Optional.fromNullable(deltaXAction);
        return this;
    }

    public SwipeGestureListener withDeltaYAction(final Action1<Float> deltaYAction) {
        mDeltaYAction = Optional.fromNullable(deltaYAction);
        return this;
    }
}
