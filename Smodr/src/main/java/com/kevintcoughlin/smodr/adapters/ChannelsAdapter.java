package com.kevintcoughlin.smodr.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;

import java.util.ArrayList;

public final class ChannelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	@NonNull
	private final ArrayList<Channel> mItems = new ArrayList<>();

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channels_grid_item_layout, parent, false);
		return new ChannelViewHolder(v);
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		final Channel channel = mItems.get(position);
		final ChannelViewHolder vh = (ChannelViewHolder) holder;
		final String href = !channel.getImages().isEmpty() ? channel.getImages().get(0).getHref() : "";
		Glide.with(vh.itemView.getContext())
				.load(href)
				.fitCenter()
				.crossFade()
				.into(vh.mImage);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	public void setChannels(@NonNull final ArrayList<Channel> results) {
		if (!mItems.isEmpty()) {
			mItems.clear();
		}
		mItems.addAll(results);
		notifyDataSetChanged();
	}

	public void addChannel(@NonNull final Channel channel) {
		mItems.add(channel);
		notifyItemInserted(mItems.size() - 1);
	}

	public static final class ChannelViewHolder extends RecyclerView.ViewHolder {
		@Bind(R.id.image)
		ImageView mImage;

		public ChannelViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}