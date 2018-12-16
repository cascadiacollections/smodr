package com.kevintcoughlin.common.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BinderRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public interface OnClick<T> {
        void onClick(T item);
    }

    public interface Binder<T, VH extends RecyclerView.ViewHolder> {
        void bind(@NonNull final T model, @NonNull final VH viewHolder);

        VH createViewHolder(@NonNull final ViewGroup parent);
    }

    private final List<T> items = new ArrayList<>();
    private final Binder<T, VH> binderViewHolder;
    private WeakReference<OnClick<T>> mOnClickListener;

    public BinderRecyclerAdapter(@NonNull final Binder<T, VH> binderViewHolder) {
        super();
        this.binderViewHolder = binderViewHolder;
    }

    public void setOnClickListener(@NonNull final OnClick<T> onClickListener) {
        this.mOnClickListener = new WeakReference<>(onClickListener);
    }

    public void setItems(Collection<T> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    public T getItem(int index) {
        return items.get(index);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        return binderViewHolder.createViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH viewHolder, int i) {
        final T item = this.items.get(i);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickListener.get().onClick(item);
            }
        });

        this.binderViewHolder.bind(item, viewHolder);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
