package com.kevintcoughlin.smodr.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public interface Binder<T, VH extends RecyclerView.ViewHolder> {
    void bind(@NonNull final T model, @NonNull final VH viewHolder);

    VH createViewHolder(@NonNull final ViewGroup parent);
}
