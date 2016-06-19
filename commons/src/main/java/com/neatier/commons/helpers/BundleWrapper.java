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

import android.os.Bundle;
import android.os.Parcelable;
import java.io.Serializable;

/**
 * Created by László Gálosi on 27/03/16
 */
public class BundleWrapper {

    final Bundle mBundle;

    public static BundleWrapper wrap(final Bundle bundle) {
        return new BundleWrapper(bundle);
    }

    public BundleWrapper copy() {
        return BundleWrapper.wrap(new Bundle(mBundle));
    }

    public boolean containsKey(final String key) {
        return mBundle.containsKey(key);
    }

    public int getInt(final String itemId) {
        return mBundle.getInt(itemId);
    }

    protected BundleWrapper() {
        mBundle = new Bundle();
    }

    protected BundleWrapper(final Bundle bundle) {
        mBundle = bundle;
    }

    public BundleWrapper putString(String key, String value) {
        mBundle.putString(key, value);
        return this;
    }

    public BundleWrapper putInt(String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    public BundleWrapper putLong(String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }

    public BundleWrapper putAll(final Bundle bundle) {
        mBundle.putAll(bundle);
        return this;
    }

    public BundleWrapper putParcelable(final String key, final Parcelable parcelable) {
        mBundle.putParcelable(key, parcelable);
        return this;
    }

    public BundleWrapper put(final String key, final Object value) {
        if (value instanceof String) {
            mBundle.putString(key, (String) value);
        } else if (value instanceof Long) {
            mBundle.putLong(key, (Long) value);
        } else if (value instanceof Short) {
            mBundle.putLong(key, (Short) value);
        } else if (value instanceof Integer) {
            mBundle.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            mBundle.putFloat(key, (Float) value);
        } else if (value instanceof Double) {
            mBundle.putDouble(key, (Double) value);
        } else if (value instanceof Boolean) {
            mBundle.putBoolean(key, (Boolean) value);
        } else if (value instanceof Parcelable) {
            mBundle.putParcelable(key, (Parcelable) value);
        } else if (value instanceof Serializable) {
            mBundle.putSerializable(key, (Serializable) value);
        } else {
            throw new IllegalArgumentException(
                    String.format("Invalid value type: %s", value.getClass().getSimpleName()));
        }
        return this;
    }

    public <T> T getAsOrThrows(final String key, final Class<T> returnClass, final Exception t)
            throws Exception {
        T result = getAs(key, returnClass);
        if (result == null) {
            throw t;
        }
        return result;
    }

    public <T> T getAs(final String key, final Class<T> returnClass, final T... defaultValue) {
        if (!mBundle.containsKey(key)) {
            return defaultValue.length > 0 ? defaultValue[0] : null;
        }
        if (returnClass == String.class) {
            return (T) mBundle.getString(key);
        } else if (returnClass == Long.class) {
            return (T) Long.valueOf(mBundle.getLong(key));
        } else if (returnClass == Integer.class) {
            return (T) Integer.valueOf(mBundle.getInt(key));
        } else if (returnClass == Short.class) {
            return (T) Short.valueOf(mBundle.getShort(key));
        } else if (returnClass == Float.class) {
            return (T) Float.valueOf(mBundle.getFloat(key));
        } else if (returnClass == Double.class) {
            return (T) Double.valueOf(mBundle.getDouble(key));
        } else if (returnClass == Boolean.class) {
            return (T) Boolean.valueOf(mBundle.getBoolean(key));
        } else if (returnClass == Parcelable.class) {
            return (T) mBundle.getParcelable(key);
        } else if (returnClass == Serializable.class) {
            return (T) mBundle.getSerializable(key);
        }
        throw new IllegalArgumentException(
                String.format("Invalid returnClass type: %s", returnClass.getSimpleName()));
    }

    public BundleWrapper putBoolean(String key, final boolean b) {
        mBundle.putBoolean(key, b);
        return this;
    }

    public String getString(final String key, final String... defaultValues) {
        if (containsKey(key)) {
            return getBundle().getString(key);
        }
        return defaultValues.length > 0 ? defaultValues[0] : null;
    }

    public int getInt(final String key, final int... defaultValues) {
        if (containsKey(key)) {
            return getBundle().getInt(key);
        }
        return defaultValues.length > 0 ? defaultValues[0] : 0;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\nBundleWrapper{");
        sb.append("\nmBundle=").append(mBundle);
        sb.append('}');
        return CommonUtils.cleanNewLine(sb.toString());
    }
}
