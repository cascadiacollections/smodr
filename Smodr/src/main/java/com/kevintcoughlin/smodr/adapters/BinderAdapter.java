package com.kevintcoughlin.smodr.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base adapter.
 *
 * @author kevincoughlin
 */
public final class BinderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Object> mItems = new ArrayList<>();
    private final LayoutInflater mLayoutInflater;
    private final Map<String, ViewType> mViewTypes = new HashMap<>();
    private final SparseArray<ViewType> mViewTypesLayoutMap = new SparseArray<>();
    @Nullable
    private OnItemClickListener mListener = null;

    public BinderAdapter(@NonNull final Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = mLayoutInflater.inflate(viewType, null);
        return mViewTypesLayoutMap.get(viewType).getBinder().createViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Object object = getItemByPosition(position);
        //mViewTypes.get(object.getClass().getName()).getBinder().bind(object, holder);
        if (mListener != null) {
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(getItemByPosition(position));
                }
            });
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return mViewTypes.get(getItemByPosition(position).getClass().getName()).getLayoutId();
    }

    public final <T, VH extends RecyclerView.ViewHolder> void registerViewType(final int layoutResId,
                                                                               final Binder<T, VH> viewBinder,
                                                                               final Class<? extends T> viewModel) {
        final ViewType viewType = new ViewType() {
            @Override
            public int getLayoutId() {
                return layoutResId;
            }

            @NonNull
            @Override
            public Binder getBinder() {
                return viewBinder;
            }
        };
        mViewTypes.put(viewModel.getName(), viewType);
        mViewTypesLayoutMap.put(layoutResId, viewType);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private Object getItemByPosition(final int position) {
        return mItems.get(position);
    }

    public void setOnItemClickListener(@Nullable final OnItemClickListener listener) {
        this.mListener = listener;
    }

    public <T> void setItems(@NonNull final List<T> items) {
        this.mItems.clear();
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }

    private interface ViewType<T, VH extends RecyclerView.ViewHolder> {
        @LayoutRes
        int getLayoutId();

        @NonNull
        Binder<T, VH> getBinder();
    }

    public interface Binder<T, VH extends RecyclerView.ViewHolder> {
        void bind(@NonNull final T model, @NonNull final VH viewHolder);

        @NonNull
        VH createViewHolder(final View view);
    }

    public interface OnItemClickListener {
        void onItemClick(@NonNull Object item);
    }
}
