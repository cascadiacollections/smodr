package com.kevintcoughlin.smodr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.bumptech.glide.Glide;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;

import java.util.ArrayList;

public final class ChannelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private final ArrayList<Channel> mItems = new ArrayList<>();

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channels_grid_item_layout, parent, false);
		return new ChannelViewHolder(v);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		final Channel channel = mItems.get(position);
		final ChannelViewHolder vh = (ChannelViewHolder) holder;

		Glide.with(vh.mImage.getContext()).load("http://goo.gl/gEgYUd").into(vh.mImage);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	public void setChannels(ArrayList<Channel> results) {
		if (!mItems.isEmpty()) {
			mItems.clear();
		}
		mItems.addAll(results);
		notifyDataSetChanged();
	}

	public void addChannel(Channel channel) {
		mItems.add(channel);
		notifyDataSetChanged();
	}

	public static class ChannelViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.image) ImageView mImage;

		public ChannelViewHolder(View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
		}
	}
}