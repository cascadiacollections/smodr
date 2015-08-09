package com.kevintcoughlin.smodr.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
	@Nullable
	private ChannelViewHolder.IChannelViewHolderClicks mListener;

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channels_grid_item_layout, parent, false);
		return new ChannelViewHolder(v, mListener);
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
		mItems.clear();
		mItems.addAll(results);
		notifyDataSetChanged();
	}

	public void addChannel(@NonNull final Channel channel) {
		mItems.add(channel);
		notifyItemInserted(mItems.size() - 1);
	}

	public Channel getItem(final int position) {
		return mItems.get(position);
	}

	public void setClickListener(@NonNull final ChannelViewHolder.IChannelViewHolderClicks listener) {
		mListener = listener;
	}

	public static final class ChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.image)
		ImageView mImage;
		@NonNull
		private final IChannelViewHolderClicks mListener;

		public ChannelViewHolder(View itemView, @NonNull IChannelViewHolderClicks listener) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			mListener = listener;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			mListener.onChannelClick(getAdapterPosition());
		}

		public interface IChannelViewHolderClicks {
			void onChannelClick(final int position);
		}
	}
}