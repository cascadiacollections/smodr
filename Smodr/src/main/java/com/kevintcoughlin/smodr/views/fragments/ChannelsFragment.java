package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kevintcoughlin.common.adapter.BinderRecyclerAdapter;
import com.kevintcoughlin.common.fragment.BinderRecyclerFragment;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChannelsFragment extends BinderRecyclerFragment<Channel, ChannelsFragment.ChannelViewHolder> {
    private static Map<String, Channel> CHANNEL_MAP;

    public ChannelsFragment() {
        final int NUMBER_OF_CHANNELS = 14;
        CHANNEL_MAP = new ArrayMap<>(NUMBER_OF_CHANNELS);
        CHANNEL_MAP.put("Smodcast", new Channel("Smodcast", "https://feeds.feedburner.com/SModcasts", "https://static1.squarespace.com/static/55c25a62e4b0030db3b1280e/t/5b15787d758d4695c53d5adb/1528133780814/smodcast2.png"));
        CHANNEL_MAP.put("Hollywood Babble-On", new Channel( "Hollywood BabbleOn", "https://feeds.feedburner.com/HollywoodBabbleOnPod", "https://i1.sndcdn.com/avatars-000206902111-797ay8-original.jpg"));
        CHANNEL_MAP.put("Jay & Bob Get Old", new Channel("Jay & Bob Get Old", "https://i1.sndcdn.com/avatars-000309983285-92en5d-original.jpg", "https://i1.sndcdn.com/avatars-000309983285-92en5d-original.jpg"));
        CHANNEL_MAP.put("FatMan on Batman", new Channel("FatMan on Batman", "https://feeds.feedburner.com/FatManOnBatman", "https://i1.sndcdn.com/avatars-000506851536-gqk9tf-original.jpg"));
        CHANNEL_MAP.put("Edumacation", new Channel("Edumacation", "https://feeds.feedburner.com/Edumacation", "https://i1.sndcdn.com/avatars-000218447123-qbi8m4-original.jpg"));
        CHANNEL_MAP.put("Talk Salad", new Channel("Talk Salad", "https://feeds.feedburner.com/TalkSalad", "https://i1.sndcdn.com/avatars-000207963814-gadcf1-original.jpg" ));
        CHANNEL_MAP.put("I Sell Comics", new Channel("I Sell Comics", "https://feeds.feedburner.com/ISellComics", "https://i1.sndcdn.com/avatars-000069228172-84ip26-original.jpg"));
        CHANNEL_MAP.put("Tell 'Em Steve-Dave", new Channel("Tell 'Em Steve-Dave", "https://feeds.feedburner.com/TellEmSteveDave", "https://i1.sndcdn.com/avatars-000069229441-16gxj6-original.jpg"));
        CHANNEL_MAP.put("BlowHard", new Channel("BlowHard", "https://feeds.feedburner.com/BlowHardPod", "https://i1.sndcdn.com/avatars-000272296929-jemdrj-original.jpg"));
        CHANNEL_MAP.put("Feab", new Channel("Feab", "https://feeds.feedburner.com/Feab", "https://content.production.cdn.art19.com/images/a0/f0/38/40/a0f03840-509e-498c-84e6-fcaeb270947d/dbb99a8930be6096d08dc7308659cf49e824ace9d56e09bd31cadb5ae9b35ff9c7a8cb3577af5071d57357fbfc2f7d1b8e544d251b152ab01e0d7efded60ff8e.jpeg"));
        CHANNEL_MAP.put("Netheads", new Channel("Netheads", "https://feeds.feedburner.com/NetHeadsOnAir", "https://i1.sndcdn.com/avatars-000342515609-lucm4t-original.jpg"));
        CHANNEL_MAP.put("Nooner", new Channel("Nooner", "https://feeds.feedburner.com/NoonerPod", "https://i1.sndcdn.com/avatars-000170407225-ax2vmg-original.jpg"));
        CHANNEL_MAP.put("PodU", new Channel("Pod U", "https://feeds.feedburner.com/Podu", "https://i1.sndcdn.com/avatars-000292224694-w7wbkc-original.jpg"));
        CHANNEL_MAP.put("The Wayne Foundation", new Channel("The Wayne Foundation", "https://feeds.feedburner.com/thewaynefoundation", "https://i1.sndcdn.com/avatars-000191437900-zwb68x-original.jpg"));
    }

    public static final String TAG = ChannelsFragment.class.getSimpleName();

    static final class ChannelViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.cover_art)
        SimpleDraweeView mCoverArtView;

        ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    final class ChannelAdapter extends BinderRecyclerAdapter<Channel, ChannelViewHolder> {
        ChannelAdapter(@NonNull OnClick<Channel> onClick) {
            super(new ChannelView(onClick));
        }
    }

    private static final int NUMBER_OF_COLUMNS = 2;

    @NonNull
    private final RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);

    @NonNull
    private final BinderRecyclerAdapter<Channel, ChannelViewHolder> mAdapter = new ChannelAdapter(this);

    class ChannelView implements BinderRecyclerAdapter.Binder<Channel,ChannelViewHolder> {
        private WeakReference<BinderRecyclerAdapter.OnClick<Channel>> mOnClickListener;

        ChannelView(@NonNull final BinderRecyclerAdapter.OnClick<Channel> onClick) {
            this.mOnClickListener = new WeakReference<>(onClick);
        }

        @Override
        public void bind(@NonNull Channel model, @NonNull ChannelViewHolder viewHolder) {
            viewHolder.mCoverArtView.setImageURI(model.image.url);
            viewHolder.itemView.setOnClickListener(v -> this.mOnClickListener.get().onClick(model));
        }

        @Override
        public ChannelViewHolder createViewHolder(@NonNull final ViewGroup parent) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel_layout, parent, false);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter.setItems(CHANNEL_MAP.values());
        mAdapter.notifyDataSetChanged();
    }
}
