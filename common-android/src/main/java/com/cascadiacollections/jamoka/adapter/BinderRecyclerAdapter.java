package com.cascadiacollections.jamoka.adapter;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generic RecyclerView Adapter with a binder for custom view holders and item interaction listeners.
 */
public class BinderRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    /**
     * Interface for item click and long-click listeners.
     */
    public interface IListeners<T> {
        void onClick(T item);

        boolean onLongClick(T item);
    }

    /**
     * Interface for binding items to ViewHolders and creating ViewHolders.
     */
    public interface Binder<T, VH extends RecyclerView.ViewHolder> {
        void bind(@NonNull final T model, @NonNull final VH viewHolder);

        VH createViewHolder(@NonNull final ViewGroup parent);
    }

    public List<T> mItems = new ArrayList<>();
    private final Binder<T, VH> mBinderViewHolder;
    private WeakReference<IListeners<T>> mOnClickListener;

    /**
     * Constructor for BinderRecyclerAdapter.
     *
     * @param binder The binder for ViewHolder creation and binding.
     */
    public BinderRecyclerAdapter(@NonNull final Binder<T, VH> binder) {
        this.mBinderViewHolder = binder;
    }

    /**
     * Set a listener for item interactions.
     *
     * @param onClickListener The listener for click and long-click events.
     */
    public void setOnClickListener(@NonNull final IListeners<T> onClickListener) {
        this.mOnClickListener = new WeakReference<>(onClickListener);
    }

    /**
     * Update the items in the adapter and refresh the UI.
     *
     * @param collection Array of new items to display.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setItems(@NonNull T[] collection) {
        mItems = new ArrayList<>(Arrays.asList(collection));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        return mBinderViewHolder.createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH viewHolder, int position) {
        final T item = mItems.get(position);
        final IListeners<T> listener = mOnClickListener != null ? mOnClickListener.get() : null;

        if (listener != null) {
            viewHolder.itemView.setOnClickListener(view -> listener.onClick(item));
            viewHolder.itemView.setOnLongClickListener(view -> listener.onLongClick(item));
        }

        mBinderViewHolder.bind(item, viewHolder);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}