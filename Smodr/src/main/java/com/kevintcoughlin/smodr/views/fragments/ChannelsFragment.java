package com.kevintcoughlin.smodr.views.fragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kevintcoughlin.common.adapter.BinderRecyclerAdapter;
import com.kevintcoughlin.common.fragment.BinderRecyclerFragment;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChannelsFragment extends BinderRecyclerFragment<Channel, ChannelsFragment.ChannelViewHolder> {

    final class ChannelViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cover_art)
        SimpleDraweeView mCoverArtView;

        ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    final class ChannelAdapter extends BinderRecyclerAdapter<Channel, ChannelViewHolder> {
        ChannelAdapter() {
            super(new ChannelView());
        }
    }

    private static final int NUMBER_OF_COLUMNS = 2;

    @NonNull
    private final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);

    @NonNull
    private final BinderRecyclerAdapter<Channel, ChannelViewHolder> mAdapter = new ChannelAdapter();

    class ChannelView implements BinderRecyclerAdapter.Binder<Channel,ChannelViewHolder> {
        @Override
        public void bind(@NonNull Channel model, @NonNull ChannelViewHolder viewHolder) {
            viewHolder.mCoverArtView.setImageURI(model.image.href);
        }

        @Override
        public ChannelViewHolder createViewHolder(@NonNull final ViewGroup parent) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_episode_layout, parent, false);
            return new ChannelViewHolder(view);
        }
    }

    @Override
    protected BinderRecyclerAdapter<Channel, ChannelViewHolder> getAdapter() {
        return this.mAdapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return this.mLayoutManager;
    }

    @Override
    public void onClick(Channel item) {

    }
}
