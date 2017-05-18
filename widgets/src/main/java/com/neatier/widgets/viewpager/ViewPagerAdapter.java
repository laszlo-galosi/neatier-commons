package com.neatier.widgets.viewpager;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;
import java.util.WeakHashMap;
import trikita.log.Log;

public abstract class ViewPagerAdapter<V extends View> extends PagerAdapter {

    protected final Map<V, Integer> instantiatedViews = new WeakHashMap<>();
    private final ViewIdGenerator viewIdGenerator = new ViewIdGenerator();

    protected ViewPagerAdapterState viewPagerAdapterState = ViewPagerAdapterState.newInstance();

    @Override
    public V instantiateItem(ViewGroup container, int position) {
        V view = createView(container, position);
        bindView(view, position);

        view.setId(viewIdGenerator.generateViewId());
        instantiatedViews.put(view, position);
        view.setTag(position);
        restoreViewState(position, view);
        container.addView(view);

        // key with which to associate this view
        return view;
    }

    /**
     * Inflate the view representing an item at the given position.
     * <p>
     * You should set tag on the view if you want to find the view from the viewPager,
     * because NestedScrollView
     * WRAP_CONTENT don't measure height correctly.

     * </p>
     * <p>
     * Do not add the view to the container, this is handled.
     *
     * @param container the parent view from which sizing information can be grabbed during
     * inflation
     * @param position the position of the data set that is to be represented by this view
     * @return the inflated and view
     * @see LockableViewPager#onMeasure(int, int)
     */
    protected abstract V createView(ViewGroup container, int position);

    /**
     * Bind the view to the item at the given position.
     * <p>
     *  Must set tag if you want to find the view from the viewPager, because NestedScrollView
     * WRAP_CONTENT don't measure height correctly.
     * @param view the view to bind
     * @param position the position of the data set that is to be represented by this view
     * @see LockableViewPager#onMeasure(int, int)
     */
    protected abstract void bindView(V view, int position);

    protected void restoreViewState(int position, V view) {
        SparseArray<Parcelable> parcelableSparseArray = viewPagerAdapterState.get(position);
        if (parcelableSparseArray == null) {
            return;
        }
        view.restoreHierarchyState(parcelableSparseArray);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d("notifyDataSetChanged", instantiatedViews.size());
        for (Map.Entry<V, Integer> entry : instantiatedViews.entrySet()) {
            bindView(entry.getKey(), entry.getValue());
        }
    }


    @SuppressWarnings("unchecked")
    // `key` is the object we return in `instantiateItem(ViewGroup container, int position)`
    @Override
    public void destroyItem(ViewGroup container, int position, Object key) {
        V view = (V) key;
        saveViewState(position, view);
        container.removeView(view);
    }

    protected void saveViewState(int position, V view) {
        SparseArray<Parcelable> viewState = new SparseArray<>();
        view.saveHierarchyState(viewState);
        viewPagerAdapterState.put(position, viewState);
    }

    @Override
    public Parcelable saveState() {
        for (Map.Entry<V, Integer> entry : instantiatedViews.entrySet()) {
            int position = entry.getValue();
            V view = entry.getKey();
            saveViewState(position, view);
        }
        return viewPagerAdapterState;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state instanceof ViewPagerAdapterState) {
            this.viewPagerAdapterState = ((ViewPagerAdapterState) state);
        } else {
            super.restoreState(state, loader);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object key) {
        return view.equals(key);
    }

    @Override public int getItemPosition(final Object object) {
        return instantiatedViews.containsKey(object) ? POSITION_UNCHANGED : POSITION_NONE;
    }
}
