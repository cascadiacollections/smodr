package com.kevintcoughlin.smodr.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.kevintcoughlin.smodr.viewholders.Binder;

import java.util.ArrayList;
import java.util.List;

public class BinderRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public interface OnClick<T> {
        void onClick(T item);
    }

    private List<T> items;
    private Binder<T, VH> binderViewHolder;
    private OnClick<T> mOnClickListener;

    public BinderRecyclerAdapter(@NonNull final Binder<T, VH> binderViewHolder) {
        super();
        this.items = new ArrayList<>();
        this.binderViewHolder = binderViewHolder;
    }

    public void setOnClickListener(@NonNull final OnClick<T> onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setItems(List<T> items) {
        this.items = items;
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

        viewHolder.itemView.setOnClickListener(v -> this.mOnClickListener.onClick(item));

        this.binderViewHolder.bind(item, viewHolder);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
