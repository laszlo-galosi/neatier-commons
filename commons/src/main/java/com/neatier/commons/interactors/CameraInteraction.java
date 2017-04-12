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
import com.neatier.commons.helpers.Leakable;
import rx.Observable;

/**
 * Created by LargerLife on 02/02/17.
 */

public interface CameraInteraction extends Leakable {

    String EXTRA_OUTPUT_PATH = "CameraOutputPath";

    Observable<Intent> stillImageChooserIntent(int requestCode, Bundle callbackBundle);

    Observable<Intent> stillImageCaptureIntent(int requestCode);

    Observable<Intent> videoCaptureChooserIntent(int requestCode);

    Intent videoImageCaptureIntent(int requestCode);

    String getStillImageCaptureAction();

    String getVideoImageCaptureAction();
}
