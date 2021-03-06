/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.commons.helpers;

/**
 * Interface denoting that the implementing class is suspected to memory leak, so it should
 * clear the leak suspects.
 * @author László Gálosi
 * @since 23/03/16
 */
public interface Leakable {

    /**
     * Call this when you suspects that the implementing class may leak memory, when it is
     * destroyed.
     */
    void clearLeakables();
}
