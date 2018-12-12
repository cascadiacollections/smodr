package com.kevintcoughlin.smodr.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.viewholders.Binder;

import butterknife.Bind;
import butterknife.ButterKnife;

class EpisodeViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.description)
    TextView mDescription;

    EpisodeViewHolder(final View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}

class EpisodeView implements Binder<Item, EpisodeViewHolder> {

    private BinderRecyclerAdapter.OnClick<Item> mOnClickListener;

    EpisodeView(BinderRecyclerAdapter.OnClick<Item> onClick) {
        this.mOnClickListener = onClick;
    }

    @Override
    public void bind(@NonNull final Item model, @NonNull final EpisodeViewHolder viewHolder) {
        viewHolder.mTitle.setText(model.title);
        viewHolder.mDescription.setText(model.description);

        viewHolder.itemView.setOnClickListener(v -> {
            if (this.mOnClickListener != null) {
                this.mOnClickListener.onClick(model);
            }
        });
    }

    @Override
    public EpisodeViewHolder createViewHolder(@NonNull final ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_episode_layout, parent, false);
        return new EpisodeViewHolder(view);
    }
}

public class ItemsAdapter extends BinderRecyclerAdapter<Item, EpisodeViewHolder> {
    public ItemsAdapter(final OnClick<Item> onClickListener) {
        super(new EpisodeView(onClickListener));
    }
}
