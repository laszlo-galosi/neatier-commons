package com.neatier.widgets.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.TintTypedArray;
import android.util.TypedValue;
import com.fernandocejas.arrow.checks.Preconditions;

/**
 * Contains builder-like helper methods for {@link ColorStates} an {@link ColorStateList} handling.
 *
 * @author László Gálosi
 * @since 05/09/17
 */
public class ColorStates {

    /**
     * The context.
     */
    @NonNull private final Context mContext;

    /**
     * A TypedArray class for handling color state related attributes.
     */
    private final TintTypedArray mTypedArray;

    /**
     * The styleable resource.
     */
    @StyleableRes private int mStyleableRes;

    /**
     * A color state list created from an an xml resource file.
     */
    private ColorStateList mBaseColorState;

    /**
     * The default color to be used, as a fallback color.
     */
    private @ColorInt int mDefaultColor = 0;

    /**
     * The state set of the color set.
     */
    private int[] mStateSet;

    /**
     * An instance creator method with the given typed array and context.
     */
    public static ColorStates with(TintTypedArray tintedTypedArr, @NonNull Context context) {
        return new ColorStates(tintedTypedArr, context);
    }

    /**
     * Sets the given styleable resource and base color theme attribute, to create a color state
     * list for this instance.
     *
     * @param styleableRes the styleable resource
     * @param baseColorThemeAttr the base color theme attribute or 0 if not defined. It uses {@link
     * android.support.v7.appcompat.R.attr#colorControlNormal} if not defined.
     * @see AppCompatResources#getCachedColorStateList(Context, int)
     */
    public ColorStates styleable(@StyleableRes int styleableRes, @AttrRes int baseColorThemeAttr) {
        mStyleableRes = styleableRes;
        Preconditions.checkNotNull(mContext);
        final TypedValue typedValue = new TypedValue();
        if (baseColorThemeAttr == 0) {
            baseColorThemeAttr = android.support.v7.appcompat.R.attr.colorControlNormal;
        }
        boolean resolved =
              mContext.getTheme().resolveAttribute(baseColorThemeAttr, typedValue, true);
        Preconditions.checkArgument(resolved,
                                    "Cannot resolve theme attribute" + baseColorThemeAttr);
        if (!resolved) {
            return this;
        }
        mBaseColorState = AppCompatResources.getColorStateList(mContext, typedValue.resourceId);
        return this;
    }

    /**
     * Set the given state set for the color state list of this instance.
     */
    public ColorStates stateSet(int[] stateSet) {
        mStateSet = stateSet;
        return this;
    }

    /**
     * Set the default color resource of the color state list to be created
     */
    public ColorStates defaultColorRes(@ColorRes int colorRes) {
        //noinspection ConstantConditions
        assert mContext != null;
        mDefaultColor = ContextCompat.getColor(mContext, colorRes);
        return this;
    }

    /**
     * Set the default color resource of the color state list to be created
     */
    public ColorStates defaultColor(@ColorInt int color) {
        mDefaultColor = color;
        return this;
    }

    /**
     * Creates and returns a ColorStateList from the previously set {@link #mBaseColorState}
     * {@link #mTypedArray}, {@link #mStyleableRes}, {@link #mStateSet} of this instance.
     * It checks whethet the typed array, the base color state and state set is defined or throws a
     * NullPointerException.
     */
    @SuppressLint("RestrictedApi") public ColorStateList create() {
        Preconditions.checkNotNull(mTypedArray,
                                   "Styleable not set: Use: ColorStateList.styleable(TypedArray,"
                                         + "@StyleableRes int, @AttrRes int)");
        Preconditions.checkArgument(mDefaultColor != 0 || mBaseColorState != null,
                                    "Base theme attribute not set: Use: ColorStateList.styleable"
                                          + "(TypedArray,"
                                          + "@StyleableRes int, @AttrRes int) or set default color"
                                          + ".");
        int defaultColor = mDefaultColor != 0 ? mDefaultColor : mBaseColorState.getDefaultColor();
        Preconditions.checkNotNull(mStateSet,
                                   "State set not set: Use: ColorStateList.stateSet(in[)[]");
        return new ColorStateList(new int[][] {
              mStateSet
        }, new int[] {
              mTypedArray.hasValue(mStyleableRes) ? mTypedArray.getColor(mStyleableRes,
                                                                         defaultColor)
                                                  : defaultColor
        });
    }

    private ColorStates(TintTypedArray tintedTypedArr, @NonNull Context context) {
        mContext = context;
        mTypedArray = tintedTypedArr;
    }
}

