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

package com.neatier.commons.helpers;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import java.io.Serializable;
import rx.Observable;
import rx.functions.Func1;

/**
 * A Wapper around {@link Bundle} with builder like assembling methods.
 *
 * @author László Gálosi
 * @since 27/03/16
 */
public class BundleWrapper implements Parcelable {

    /**
     * The internal wrapped bundle
     */
    private final Bundle mBundle;

    /**
     * Static instance creator method with the given bundle to wrap.
     */
    public static BundleWrapper wrap(final Bundle bundle) {
        return new BundleWrapper(bundle);
    }

    /**
     * Creates a new instance of the wrapped bundle by copying all the values from this bundle
     * Modifying this instance has no effect on the original bundle.
     *
     * @see Bundle#putAll(Bundle)
     */
    public BundleWrapper copy() {
        Bundle bundle = new Bundle();
        bundle.putAll(getBundle());
        return BundleWrapper.wrap(bundle);
    }

    /**
     * @see Bundle#containsKey(String)
     */
    public boolean containsKey(final String key) {
        return mBundle.containsKey(key);
    }

    /**
     * @see Bundle#containsKey(String)
     */
    public int getInt(final String itemId) {
        return mBundle.getInt(itemId);
    }

    /**
     * Constructor creates a new instance with an empty internal bundle.
     */
    protected BundleWrapper() {
        mBundle = new Bundle();
    }

    /**
     * Constructor creates a new instance with the given bundle as the wrapped bundle.
     * Mutating this instance also mutates th given bundle.
     */
    protected BundleWrapper(final Bundle bundle) {
        mBundle = bundle;
    }

    /**
     * @see Bundle#putString(String, String)
     */
    public BundleWrapper putString(String key, String value) {
        mBundle.putString(key, value);
        return this;
    }

    /**
     * @see Bundle#putInt(String, int)
     */
    public BundleWrapper putInt(String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    public BundleWrapper putLong(String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }

    /**
     * @see Bundle#putAll(Bundle)
     */
    public BundleWrapper putAll(final Bundle bundle) {
        mBundle.putAll(bundle);
        return this;
    }

    /**
     * @see Bundle#putParcelable(String, Parcelable)
     */
    public BundleWrapper putParcelable(final String key, final Parcelable parcelable) {
        mBundle.putParcelable(key, parcelable);
        return this;
    }

    /**
     * Put the given value into the wrapped bundle, by calling
     * any  of {@code Bundle#putXXX} method depending on the given value instance.
     */
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
                    String.format("Invalid value type: %s", value));
        }
        return this;
    }

    /**
     * Returns the stored value with type T if found, or throws the given exception.
     *
     * @param key the key to lookup for.
     * @param returnClass the class of the value
     * @param t the Exception to be thrown if the not found
     * @param <T> the type of the stored value
     */
    @SuppressWarnings("unchecked")
    public <T> T getAsOrThrows(final String key, final Class<T> returnClass, final Exception t)
            throws Exception {
        T result = getAs(key, returnClass);
        if (result == null) {
            throw t;
        }
        return result;
    }

    /**
     * Returns the stored value with type T if found, or the given default value if provided
     * as a fallback
     *
     * @param key the key to lookup for.
     * @param returnClass the class of the value
     * @param defaultValue vararg to return s a fallback.
     * @param <T> the type of the stored value
     */
    @SuppressWarnings("unchecked")
    public <T> T getAs(final String key, final Class<T> returnClass, final T... defaultValue)
            throws ClassCastException {
        if (!mBundle.containsKey(key)) {
            return defaultValue != null && defaultValue.length > 0 ? defaultValue[0] : null;
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

    /**
     * @see Bundle#putBoolean(String, boolean)
     */
    public BundleWrapper putBoolean(String key, final boolean b) {
        mBundle.putBoolean(key, b);
        return this;
    }

    /**
     * @see Bundle#getString(String) (String, boolean)
     */
    public String getString(final String key, final String... defaultValues) {
        if (containsKey(key)) {
            return getBundle().getString(key);
        }
        return defaultValues.length > 0 ? defaultValues[0] : null;
    }

    /**
     * @see Bundle#getInt(String)
     */
    public int getInt(final String key, final int... defaultValues) {
        if (containsKey(key)) {
            return getBundle().getInt(key);
        }
        return defaultValues.length > 0 ? defaultValues[0] : 0;
    }

    /**
     * Returns the wrapped internal bundle instance.
     */
    public Bundle getBundle() {
        return mBundle;
    }

    /**
     * Removes all the parameters except the ones specified in retainParams
     *
     * @param retainParams the parameters which should be retained.
     * @return this instance.
     */
    public BundleWrapper clear(String... retainParams) {
        removeKeys(key -> {
            boolean shouldFilter = true;
            for (final String retainParam : retainParams) {
                shouldFilter &= (!key.equals(retainParam));
            }
            return shouldFilter;
        });
        return this;
    }

    /**
     * @see Bundle#remove(String)
     */
    public BundleWrapper remove(String key) {
        mBundle.remove(key);
        return this;
    }

    /**
     * Removes all the entries in this bundle of which key filter applies.
     *
     * @param keyFilterFunction key filtering function-
     */
    public BundleWrapper removeKeys(final Func1<String, Boolean> keyFilterFunction) {
        getKeysAsStream(keyFilterFunction)
              .toList()
              .flatMap(keys -> Observable.defer(() -> Observable.from(keys)))
              .subscribe(key -> getBundle().remove(key));
        return this;
    }

    /**
     * Returns an {@link Observable<String>} emitting all
     * the keys in this bundle of which the filterFunction applies.
     *
     * @param keyFilterFunction filter function
     */
    public Observable<String> getKeysAsStream(
          @NonNull final Func1<String, Boolean> keyFilterFunction) {
        return Observable.from(getBundle().keySet())
                         .filter(keyFilterFunction);
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("BundleWrapper{");
        Observable.from(getBundle().keySet())
                .subscribe(key -> sb.append("\n")
                        .append(key)
                        .append(" : '")
                        .append(getBundle().get(key))
                        .append("', "));
        sb.append("\n}");
        return sb.toString();
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(this.mBundle);
    }

    public BundleWrapper(Parcel in) {
        this.mBundle = in.readBundle(getClass().getClassLoader());
    }

    public static final Creator<BundleWrapper> CREATOR = new Creator<BundleWrapper>() {
        @Override public BundleWrapper createFromParcel(Parcel source) {
            return new BundleWrapper(source);
        }

        @Override public BundleWrapper[] newArray(int size) {
            return new BundleWrapper[size];
        }
    };
}
