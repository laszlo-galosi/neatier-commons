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
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import com.fernandocejas.arrow.optional.Optional;
import com.neatier.commons.helpers.Leakable;
import com.neatier.widgets.Bindable;
import com.neatier.widgets.R;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} with {@link ItemWidget} ViewHolder
 * containing data items with type T.
 * It has a custom {@link View.OnClickListener} attached by
 * {@link ItemWidget#setOnClickListener(View.OnClickListener)}
 */
public class ItemWidgetAdapter<T> extends RecyclerView.Adapter<ViewHolder>
      implements Leakable {
    protected final Context mContext;
    @LayoutRes protected int mItemLayout;
    protected List<T> mDataset;
    protected int mCurrentItemId = 0;
    int mItemHeight;
    private boolean mItemClickable = true;
    private Optional<View.OnClickListener> mCustomClickListener = Optional.absent();

    public ItemWidgetAdapter(final Context context, int itemHeight) {
        mContext = context;
        mItemHeight = itemHeight;
    }

    public ItemWidgetAdapter(final Context context,
          @LayoutRes final int itemLayout, int itemHeight, @Nullable List<T> dataSet) {
        mContext = context;
        mItemLayout = itemLayout;
        mItemHeight = itemHeight;
        mDataset = dataSet;
    }

    /**
     * Create new views (invoked by the layout manager)
     */
    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemWidget itemWidget =
              new ItemWidget(getItemLayout(viewType), getItemHeight(viewType), mContext)
                    .setContentClickable(mItemClickable);
        itemWidget.initView(mContext);
        return new ListItemViewHolder(itemWidget, R.id.itemText, mContext);
    }

    /*
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override public void onBindViewHolder(ViewHolder holder, int position) {
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

    @Override public void onViewAttachedToWindow(final RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        Optional<ItemWidget> itemWidget = getItemWidget(holder);
        if (mCustomClickListener.isPresent() && itemWidget.isPresent()) {
            itemWidget.get().setOnClickListener(mCustomClickListener.get());
        }
    }

    @Override public void onViewDetachedFromWindow(final RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Optional<ItemWidget> itemWidget = getItemWidget(holder);
        if (mCustomClickListener.isPresent() && itemWidget.isPresent()) {
            itemWidget.get().setOnClickListener(null);
        }
    }

    @Override public void clearLeakables() {
        setCustomClickListener(null);
    }

    public void add(int position, T item) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(T item) {
        int position = mDataset.indexOf(item);
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAt(int position) {
        mDataset.remove(position);
        notifyItemRemoved(position);
    }

    public @LayoutRes int getItemLayout(int viewType) {
        return mItemLayout;
    }

    public ItemWidgetAdapter itemHeight(final int itemHeight) {
        mItemHeight = itemHeight;
        return this;
    }

    public ItemWidgetAdapter setItemClickable(final boolean itemClickable) {
        mItemClickable = itemClickable;
        return this;
    }

    private Optional<ItemWidget> getItemWidget(final ViewHolder holder) {
        if (holder instanceof ListItemViewHolder) {
            Optional.of((ItemWidget) ((ListItemViewHolder) holder).itemView);
        }
        return Optional.absent();
    }

    public List<T> getDataset() {
        return mDataset;
    }

    public int getItemHeight(int viewType) {
        return mItemHeight;
    }

    public boolean isItemClickable() {
        return mItemClickable;
    }

    public View.OnClickListener getCustomClickListener() {
        return mCustomClickListener.orNull();
    }

    public ItemWidgetAdapter setCustomClickListener(
          final View.OnClickListener customClickListener) {
        mCustomClickListener = Optional.fromNullable(customClickListener);
        return this;
    }
}
