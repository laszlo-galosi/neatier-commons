/*
 * Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 * All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions Kft.
 *  and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *  Delight Solutions Kft.
 */

package com.neatier.widgets.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * An abstract {@link FragmentPagerAdapter} sub class which contains helper function
 * to get the Fragment of any page.
 * @see #getRegisteredFragmentByPageId(int)
 * @see #getRegisteredFragmentByPos(int)
 * @see #getPageId(int)
 * @param <T>
 */
public abstract class SmartFragmentStatePagerAdapter<T extends Fragment>
      extends FragmentStatePagerAdapter {

    protected SparseArray<T> mRegisteredFragments = new SparseArray<>(3);

    public SmartFragmentStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        T fragment = (T) super.instantiateItem(container, position);
        mRegisteredFragments.put(getPageId(position), fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mRegisteredFragments.remove(getPageId(position));
        super.destroyItem(container, position, object);
    }

    /**
     * Returns the fragment by the position in the adapter. This is exactly as same as calling
     * {@code getRegisteredFragmentByPageId(getPageId(postion)}
     *
     * @param position the id which the fragment was registered.
     * @return the registered fragment.
     * @see SmartFragmentStatePagerAdapter#getPageId(int)
     */
    public T getRegisteredFragmentByPos(int position) {
        return mRegisteredFragments.get(getPageId(position));
    }

    /**
     * Returns the fragment by the pageId which is the key pf the fragment was registered.
     *
     * @param pageId the id which the fragment was registered.
     * @return the registered fragment.
     * @see SmartFragmentStatePagerAdapter#getPageId(int)
     */
    public T getRegisteredFragmentByPageId(int pageId) {
        return mRegisteredFragments.get(pageId);
    }

    /**
     * Returns the pageId of the fragment which is used as a key in the registered fragments map.
     *
     * @param position the position of the fragment in the adapter.
     * @return the fragment id.
     */
    public int getPageId(int position) {
        return position;
    }
}
