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

package com.neatier.widgets.recyclerview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.fernandocejas.arrow.optional.Optional;
import java.util.List;
import trikita.log.Log;

/**
 * An abstract class on {@link ItemAdapter} which implement an infinite loading {@link
 * RecyclerView.Adapter} meaning when the user scrolls near the last X items, it displays
 * a progress view, and try to load the next n item by overriding {@link
 * OnLoadMoreListener#onLoadMore()}
 *
 * @author László Gálosi
 * @since 18/07/17
 */
public abstract class InfiniteItemAdapter<T> extends ItemAdapter<T> {

    /**
     * The index of the first visible item
     */
    private int mFirstVisibleItem;

    /**
     * The number of items visible to the user right now
     */
    private int mVisibleItemCount;
    /**
     * The total number of items in the RecyclerView (Visible + Invisible)
     */
    private int mTotalItemCount;

    private int mPreviousTotal = 0;

    /**
     * A boolean to differentiate between if more items are being loaded or not
     */
    private boolean mLoading = true;

    /**
     * int variable which specifies the number of remaining elements before starting to load next
     * page of data.
     */
    private int mVisibleTreshold;

    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener mOnScrollListener;
    private int mPreviousTotalCount;

    public InfiniteItemAdapter(Context context, @LayoutRes final int itemLayout,
          @Nullable final List<T> dataSet) {
        super(context, itemLayout, dataSet);
    }

    /**
     * @return the number of remaining elements before starting to load next
     */
    public abstract int getVisibleItemThreshold();

    /**
     * @param position the position in the list.
     * @return the basic item view type which populetes the list.
     */
    public abstract int getBaseItemViewType(int position);

    /**
     * @return the footer item view type used to indicate that more items loading.
     */
    public abstract int getFooterItemViewType();

    /**
     * {@inheritDoc #onCreateViewHolder}
     */
    public abstract RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent,
          int viewType);

    /**
     * {@inheritDoc #onBindViewHolder}
     */
    public abstract void onBindBasicItemView(RecyclerView.ViewHolder holder, int position);

    public abstract RecyclerView.ViewHolder onCreateFooterItemViewHolder(ViewGroup parent,
          int viewType);

    public abstract void onBindFooterItemView(RecyclerView.ViewHolder holder, int position);

    @Override public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        Log.d("onAttachedToRecyclerView");
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager =
                  (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(getOrCreateScrollListener(linearLayoutManager));
        }
    }

    @Override public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        Log.d("onDetachedFromRecyclerView");
        if (mOnScrollListener != null) {
            recyclerView.removeOnScrollListener(mOnScrollListener);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataset.get(position) != null ? getBaseItemViewType(position)
                                              : getFooterItemViewType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == getFooterItemViewType()) {
            return onCreateFooterItemViewHolder(parent, viewType);
        } else {
            return onCreateBasicItemViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == getFooterItemViewType()) {
            onBindFooterItemView(holder, position);
        } else {
            onBindBasicItemView(holder, position);
        }
    }

    public void reset() {
        mLoading = true;
        mFirstVisibleItem = 0;
        mVisibleItemCount = 0;
        mTotalItemCount = 0;
        mPreviousTotal = 0;
        //mDataset.clear();
    }

    public void addItems(@NonNull List<T> newDataSetItems) {
        mDataset.addAll(newDataSetItems);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        if (!mDataset.contains(item)) {
            mDataset.add(item);
            notifyItemInserted(mDataset.size() - 1);
        }
    }

    public Optional<T> getItem(int index) {
        if (mDataset != null) {
            return Optional.fromNullable(mDataset.get(index));
        } else {
            throw new IllegalArgumentException("Item with index " + index + " doesn't exist.");
        }
    }

    public RecyclerView.OnScrollListener getOrCreateScrollListener(
          LinearLayoutManager linearLayoutManager) {
        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mTotalItemCount = linearLayoutManager.getItemCount();
                mVisibleItemCount = linearLayoutManager.getChildCount();
                mFirstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if (mLoading) {
                    if (mTotalItemCount > (mPreviousTotal + 1)) {
                        mLoading = false;
                        mPreviousTotal = mTotalItemCount;
                    }
                }
                if (!mLoading && (mTotalItemCount - mVisibleItemCount)
                      <= (mFirstVisibleItem + getVisibleItemThreshold())) {
                    // End has been reached

                    //Add the null element which displays the loading item.
                    if (mOnLoadMoreListener != null) {
                        addItem(null);
                        mOnLoadMoreListener.onLoadMore();
                    }
                    mLoading = true;
                }
                //Log.d("onScrolled loading", mLoading, "prevtotal/total",
                //      mPreviousTotal, mTotalItemCount,
                //      "visible", mVisibleItemCount, "firstvis",
                //      mFirstVisibleItem);
            }
        };
        return mOnScrollListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public InfiniteItemAdapter setOnLoadMoreListener(final OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
        return this;
    }
}

