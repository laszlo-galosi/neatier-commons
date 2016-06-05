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

package com.neatier.widgets.recyclerview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.neatier.widgets.Bindable;
import java.util.List;

/**
 * Created by László Gálosi on 18/03/16
 */
public class ItemAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final Context mContext;
    @LayoutRes protected final int mItemLayout;
    protected List<T> mDataset;
    protected int mCurrentItemId = 0;
    int mItemHeight;
    private boolean mItemClickable = true;
    private View.OnClickListener mCustomClickListener;

    public ItemAdapter(final Context context) {
        this(context, 0, null);
    }

    public ItemAdapter(final Context context, @LayoutRes final int itemLayout) {
        this(context, itemLayout, null);
    }

    public ItemAdapter(final Context context, @LayoutRes final int itemLayout,
          @Nullable List<T> dataSet) {
        mContext = context;
        mItemLayout = itemLayout;
        mDataset = dataSet;
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mItemLayout, parent, false);
        return new ItemViewHolderBase(view, mContext);
    }

    /*
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //noinspection unchecked
        ((Bindable) holder).bind(mDataset.get(position));
    }

    /**
     * @return the size of your dataset (invoked by the layout manager)
     */
    @Override public int getItemCount() {
        return mDataset.size();
    }

    public void add(int position, T item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Object datatSetItem) {
        int position = mDataset.indexOf(datatSetItem);
        if (position > 0) {
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeAt(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public @LayoutRes int getItemLayout(int viewType) {
        return mItemLayout;
    }

    public ItemAdapter itemHeight(final int itemHeight) {
        mItemHeight = itemHeight;
        return this;
    }

    public ItemAdapter itemIsClickable(final boolean itemClickable) {
        mItemClickable = itemClickable;
        return this;
    }

    public List<T> getDataset() {
        return mDataset;
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public boolean isItemClickable() {
        return mItemClickable;
    }

    public View.OnClickListener getCustomClickListener() {
        return mCustomClickListener;
    }

    public ItemAdapter setCustomClickListener(final View.OnClickListener customClickListener) {
        mCustomClickListener = customClickListener;
        return this;
    }
}
