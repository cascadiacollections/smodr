package com.kevintcoughlin.smodr.viewholders;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevintcoughlin.common.adapter.BinderRecyclerAdapter;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Item;

import java.lang.ref.WeakReference;

public class EpisodeView implements BinderRecyclerAdapter.Binder<Item, EpisodeViewHolder> {

    private WeakReference<BinderRecyclerAdapter.OnClick<Item>> mOnClickListener;

    public EpisodeView(@NonNull final BinderRecyclerAdapter.OnClick<Item> onClick) {
        this.mOnClickListener = new WeakReference<>(onClick);
    }

    @Override
    public void bind(@NonNull final Item model, @NonNull final EpisodeViewHolder viewHolder) {
        viewHolder.mTitle.setText(model.title);
        viewHolder.mDescription.setText(model.description);
        viewHolder.itemView.setOnClickListener(v -> this.mOnClickListener.get().onClick(model));
    }

    @Override
    public EpisodeViewHolder createViewHolder(@NonNull final ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_episode_layout, parent, false);
        return new EpisodeViewHolder(view);
    }
}
