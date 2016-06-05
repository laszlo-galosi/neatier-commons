/*
 * Copyright (C) 2015 Laszlo Galosi, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 *
 * All information contained herein is, and remains the property of Dr. Krisztian Balazs.
 * The intellectual and technical concepts contained herein are proprietary to Dr. Krisztian
 * Balazs and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 * by trade secret or copyright law. Dissemination of this information or reproduction of
 * this material is strictly forbidden unless prior written permission is obtained from
 * Laszlo Galosi.
 */

package com.neatier.widgets.helpers;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.View;
import com.neatier.commons.helpers.Leakable;
import java.util.EventListener;

/**
 * Created by László Gálosi on 03/09/15
 */
public interface WidgetLeakable extends Leakable {
    /**
     * Method to free up all potentional leak suspects, e.g. {@link EventListener}s, or anonymous,
     * inner classes which can leak {@link Activity} by preserving a reference for a {@link
     * View} or the {@link Context}
     * This method should be called on {@link Fragment#onDestroyView()}
     */
    void clearLeakablesOnDestroyView();
}
