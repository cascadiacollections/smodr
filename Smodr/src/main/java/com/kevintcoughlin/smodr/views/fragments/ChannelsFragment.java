package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kevintcoughlin.common.adapter.BinderRecyclerAdapter;
import com.kevintcoughlin.common.fragment.BinderRecyclerFragment;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChannelsFragment extends BinderRecyclerFragment<Channel, ChannelsFragment.ChannelViewHolder> {
    private static SparseArray<Channel> CHANNEL_MAP;

    public ChannelsFragment() {
        CHANNEL_MAP = new SparseArray<>(1);
        CHANNEL_MAP.put(0, new Channel("Smodcast", "https://static1.squarespace.com/static/55c25a62e4b0030db3b1280e/t/5b15787d758d4695c53d5adb/1528133780814/smodcast2.png"));
    }

    public static final String TAG = ChannelsFragment.class.getSimpleName();

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
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_channel_layout, parent, false);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Collection<Channel> channelsList = new ArrayList<>(CHANNEL_MAP.size());

        for (int i = 0; i < CHANNEL_MAP.size(); i++) {
            channelsList.add(CHANNEL_MAP.get(i));
        }

        mAdapter.setItems(channelsList);
        mAdapter.notifyDataSetChanged();
    }
}
