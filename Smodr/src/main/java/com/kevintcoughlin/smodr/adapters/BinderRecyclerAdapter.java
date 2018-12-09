package com.kevintcoughlin.smodr.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

interface Binder<T, VH extends RecyclerView.ViewHolder> {
    void bind(@NonNull final T model, @NonNull final VH viewHolder);

    VH createViewHolder();
}

public class BinderRecyclerAdapter <T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private List<T> items;
    private Binder<T, VH> binderViewHolder;

    public void setItems(List<T> items) {
        this.items = items;
    }

    public void setBinderViewHolder(@NonNull final Binder<T, VH> binderViewHolder) {
        this.binderViewHolder = binderViewHolder;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        return binderViewHolder.createViewHolder();
    }

    @Override
    public void onBindViewHolder(@NonNull final VH viewHolder, int i) {
        final T item = this.items.get(i);

        this.binderViewHolder.bind(item, viewHolder);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
