package com.cascadiacollections.jamoka.adapter;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BinderRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public interface IListeners<T> {
        void onClick(T item);
        boolean onLongClick(T item);
    }

    public interface Binder<T, VH extends RecyclerView.ViewHolder> {
        void bind(@NonNull final T model, @NonNull final VH viewHolder);

        VH createViewHolder(@NonNull final ViewGroup parent);
    }

    protected List<T> items = new ArrayList<>();
    private final Binder<T, VH> binderViewHolder;
    private WeakReference<IListeners<T>> onClickListener;

    public BinderRecyclerAdapter(@NonNull final Binder<T, VH> binder) {
        super();
        binderViewHolder = binder;
    }

    public void setOnClickListener(@NonNull final IListeners<T> onClickListener) {
        this.onClickListener = new WeakReference<>(onClickListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(T[] collection) {
        items = Arrays.asList(collection);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        return binderViewHolder.createViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH viewHolder, int i) {
        final T item = items.get(i);
        final IListeners<T> listener = onClickListener.get();

        if (listener != null) {
            viewHolder.itemView.setOnClickListener(view -> listener.onClick(item));
            viewHolder.itemView.setOnLongClickListener(view -> listener.onLongClick(item));
        }

        binderViewHolder.bind(item, viewHolder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
