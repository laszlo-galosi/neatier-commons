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
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.neatier.commons.helpers.Preconditions;

/**
 * Contains builder-like helper methods for coloring {@link Drawable}s.
 *
 * @author Filipe Bezerra
 * @version 18/01/2016
 * @since 18/01/2016
 */
@SuppressWarnings({ "WeakerAccess", "CanBeFinal" })
public class DrawableHelper {

    /**
     * The context for  accessing resources.
     */
    @NonNull private Context mContext;

    /**
     * The color of the drawable.
     */
    private int mColor;

    /**
     * The drawable to be colored.
     */
    private Drawable mDrawable;

    /**
     * A wrapper for drawable.
     */
    private Drawable mWrappedDrawable;

    /**
     * Creates and returns a colored {@link StateListDrawable} from the given drawable resource and
     * color states,
     *
     * @param drawableRes the drawable resource which to be colored based on the given states.
     * @param colorStateRes the color state resource for the drawable to be colored.
     * @param state the StateListDrawable state array
     * @param defaultColorRes a default color resource for the colored {@link StateListDrawable}
     * @param context the context
     */
    public static Drawable drawableForColorState(@DrawableRes int drawableRes,
            @ColorRes int colorStateRes, int[] state, @ColorRes int defaultColorRes,
            final Context context) {
        ColorStateList colorStateList = ContextCompat.getColorStateList(context, colorStateRes);
        Drawable drawable = AppCompatResources.getDrawable(context, drawableRes);
        int defaultColor = ContextCompat.getColor(context, defaultColorRes);
        return drawableForColorState(drawable, colorStateList, state, defaultColor, context);
    }

    /**
     * Creates and returns a colored {@link StateListDrawable} from the given drawable resource and
     * color states,
     *
     * @param drawable the drawable which to be colored based on the given states.
     * @param colorStateList the color state list for the drawable to be colored.
     * @param state the StateListDrawable state array
     * @param defaultColor a default color for the colored {@link StateListDrawable}
     * @param context the context
     */
    public static Drawable drawableForColorState(@NonNull Drawable drawable,
            ColorStateList colorStateList,
            int[] state, @ColorInt int defaultColor, final Context context) {
        if (drawable == null) {
            return null;
        }
        int baseColor = colorStateList.getColorForState(state, defaultColor);
        if (baseColor == defaultColor) {
            return DrawableHelper.withContext(context)
                    .withDrawable(drawable)
                    .withColor(baseColor)
                    .tint().get();
        }
        return DrawableHelper.withContext(context)
                .withColorState(colorStateList, state, defaultColor)
                .withDrawable(drawable)
                .tint().get();
    }

    /**
     * Returns the created colored drawable of this instance.
     */
    public Drawable get() {
        assert mWrappedDrawable != null;

        return mWrappedDrawable;
    }

    /**
     * Set the tint of the drawable. Checked whether the drawable and color is set by calling
     * {@link
     * #withDrawable(int)} and {@link #withColor(int)} or {@link #withColorRes(int)} and throws
     * {@link NullPointerException} if not.
     */
    public DrawableHelper tint() {
        assert mDrawable != null;
        Preconditions.checkNotNull(mDrawable, "Drawable not set.");
        mWrappedDrawable = mDrawable.mutate();
        mWrappedDrawable = DrawableCompat.wrap(mWrappedDrawable);
        DrawableCompat.setTint(mWrappedDrawable, mColor);
        DrawableCompat.setTintMode(mWrappedDrawable, PorterDuff.Mode.SRC_IN);

        return this;
    }

    /**
     * Set the color of the drawable set by {@link #withDrawable(int)} to the given color value.
     */
    public DrawableHelper withColor(int color) {
        mColor = color;
        return this;
    }

    /**
     * Set the drawable to be colored to the given drawable.
     */
    public DrawableHelper withDrawable(@NonNull Drawable drawable) {
        mDrawable = drawable;
        return this;
    }

    /**
     * Set the context of this instance for accessing resources.
     */
    public static DrawableHelper withContext(@NonNull Context context) {
        return new DrawableHelper(context);
    }

    /**
     * Set the color state of the drawable to be colored
     *
     * @param color the color state list
     * @param stateSet the state set upon the color is to be set.
     * @param defaultColor the default color
     * @see ColorStateList#getColorForState(int[], int)
     */
    public DrawableHelper withColorState(ColorStateList color, int[] stateSet, int defaultColor) {
        mColor = color.getColorForState(stateSet, defaultColor);
        return this;
    }

    /**
     * Instance creator method with the given context.
     */
    public DrawableHelper(@NonNull Context context) {
        mContext = context;
    }

    /**
     * Set the drawable to be colored of this distance to the given vector drawable resource.
     *
     * @see VectorDrawableCompat#create(Resources, int, Resources.Theme)
     */
    @SuppressLint("RestrictedApi")
    public DrawableHelper withDrawable(@DrawableRes int drawableRes) {
        //mDrawable = AppCompatResources.getDrawable(mContext, drawableRes);
        mDrawable = AppCompatResources.getDrawable(mContext, drawableRes);
        if (mDrawable == null) {
            withDrawable(AppCompatResources.getDrawable(mContext, drawableRes));
        }
        return this;
    }

    /**
     * Set the drawable to be colored of this distance to the given bitmap drawable resource.
     *
     * @see ContextCompat#getDrawable(Context, int)
     */
    public DrawableHelper withRasterDrawable(@DrawableRes int drawableRes) {
        mDrawable = AppCompatResources.getDrawable(mContext, drawableRes);
        //mDrawable = VectorDrawableCompat.create(mContext.getResources(), drawableRes, null);
        return this;
    }

    /**
     * Set the color of this instance to the given color resource.
     */
    public DrawableHelper withColorRes(@ColorRes int colorRes) {
        mColor = ContextCompat.getColor(mContext, colorRes);
        return this;
    }

    /**
     * Apply the colored drawable of this instance to the background of the given view.
     *
     * @see View#setBackground(Drawable)
     * @see View#setBackgroundDrawable(Drawable)
     */
    @SuppressWarnings("deprecation")
    public void applyToBackground(@NonNull View view) {
        assert mWrappedDrawable != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(mWrappedDrawable);
        } else {
            view.setBackgroundDrawable(mWrappedDrawable);
        }
    }

    /**
     * Set he colored drawable of this instance to the given ImageView drawable.
     *
     * @see ImageView#setImageAlpha(int)
     */
    public void applyTo(@NonNull ImageView imageView) {
        assert mWrappedDrawable != null;

        imageView.setImageDrawable(mWrappedDrawable);
    }

    /**
     * Set the colored drawable of this instance to the given menu item as an icon.
     *
     * @see MenuItem#setIcon(Drawable)
     */
    public void applyTo(@NonNull MenuItem menuItem) {
        assert mWrappedDrawable != null;

        menuItem.setIcon(mWrappedDrawable);
    }
}
