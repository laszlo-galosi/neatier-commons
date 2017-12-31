/*
 * Copyright (C) 2017 Extremenet Ltd., All Rights Reserved
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

package com.neatier.commons.interactors;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import com.neatier.commons.helpers.Leakable;
import rx.Observable;

/**
 * Interface for Android camera handling with {@link Observable}s.
 *
 * @author LargerLife
 * @since 02/02/17.
 */

public interface CameraInteraction extends Leakable {

    String EXTRA_OUTPUT_PATH = "CameraOutputPath";

    /**
     * Returns an Observable emitting an Intent which shows an image picker ui with all the
     * available
     * options to pick an image.
     *
     * @param requestCode the request code of the intent.
     * @param callbackBundle a callback bundle containing the picked image properties.
     */
    Observable<Intent> stillImageChooserIntent(int requestCode, Bundle callbackBundle);

    /**
     * Returns an Observable emitting an Intent which shows an image capture ui with all the
     * available
     * options to capture a still image.
     *
     * @param requestCode the request code of the intent.
     */
    Observable<Intent> stillImageCaptureIntent(int requestCode);

    /**
     * Returns an Observable emitting an Intent which shows an video picker ui with all the
     * available
     * options to pick an video file.
     *
     * @param requestCode the request code of the intent.
     */
    Observable<Intent> videoCaptureChooserIntent(int requestCode);

    /**
     * Returns an an Intent which shows an video picker ui with all the available
     * options to pick an video file.
     *
     * @param requestCode the request code of the intent.
     */
    Intent videoImageCaptureIntent(int requestCode);

    /**
     * Returns the still image capture Intent's action string.
     *
     * @see #stillImageCaptureIntent(int)
     * @see MediaStore#ACTION_IMAGE_CAPTURE
     */
    String getStillImageCaptureAction();

    /**
     * Returns the still image capture Intent's action string.
     *
     * @see #stillImageCaptureIntent(int)
     * @see MediaStore#ACTION_VIDEO_CAPTURE
     */
    String getVideoImageCaptureAction();
}
