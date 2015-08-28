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
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying a collection of {@link Channel}s.
 *
 * @author kevincoughlin
 */
public final class ChannelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	/**
	 * The collection of {@link Channel} to display.
	 */
	@NonNull
	private final ArrayList<ParseObject> mItems = new ArrayList<>();
	/**
	 * The interface for clicks on {@link com.kevintcoughlin.smodr.adapters.ChannelsAdapter.ChannelViewHolder}.
	 */
	@Nullable
	private ChannelViewHolder.IChannelViewHolderClicks mListener;

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
		final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_channel_layout, parent, false);
		return new ChannelViewHolder(v, mListener);
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		final ParseObject channel = mItems.get(position);
		final ChannelViewHolder vh = (ChannelViewHolder) holder;
		Glide.with(vh.itemView.getContext())
				.load(channel.getString("image_url"))
				.fitCenter()
				.crossFade()
				.into(vh.mImage);
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	/**
	 * Clear and set the adapter to the passed in collection and invokes {@link #notifyDataSetChanged()}.
	 *
	 * @param results
	 * 		a {@link List <Channel>} to set the adapter to.
	 */
	public void setChannels(@NonNull final List<ParseObject> results) {
		mItems.clear();
		mItems.addAll(results);
		notifyDataSetChanged();
	}

	/**
	 * Adds a {@link Channel} to the adapter and invokes {@link #notifyItemInserted(int)}.
	 *
	 * @param channel
	 *      the {@link Channel} to insert.
	 */
	public void addChannel(@NonNull final ParseObject channel) {
		mItems.add(channel);
		notifyItemInserted(mItems.size() - 1);
	}

	/**
	 * Returns a {@link List<Channel>} backing the adapter.
	 *
	 * @return a {@link List<Channel>}.
	 */
	public ArrayList<ParseObject> getChannels() {
		return mItems;
	}

	/**
	 * Returns a {@link Channel} at the given position.
	 *
	 * @param position
	 *      the position to fetch.
	 *
	 * @return
	 *      the resultant {@link Channel}.
	 */
	public ParseObject getItem(final int position) {
		return mItems.get(position);
	}

	/**
	 * Sets the click listener for {@link com.kevintcoughlin.smodr.adapters.ChannelsAdapter.ChannelViewHolder}.
	 *
	 * @param listener
	 *      the {@link com.kevintcoughlin.smodr.adapters.ChannelsAdapter.ChannelViewHolder.IChannelViewHolderClicks}
	 *      to set.
	 */
	public void setClickListener(@NonNull final ChannelViewHolder.IChannelViewHolderClicks listener) {
		mListener = listener;
	}

	/**
	 * A {@link android.support.v7.widget.RecyclerView.ViewHolder} for display a {@link Channel}.
	 */
	public static final class ChannelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.image)
		ImageView mImage;
		@Nullable
		private final IChannelViewHolderClicks mListener;

		public ChannelViewHolder(View itemView, @Nullable IChannelViewHolderClicks listener) {
			super(itemView);
			ButterKnife.bind(this, itemView);
			mListener = listener;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onChannelClick(getAdapterPosition());
			}
		}

		/**
		 * Interface for clicks on the view.
		 */
		public interface IChannelViewHolderClicks {
			/**
			 * Invoked when the view is clicked, returning the corresponding position.
			 *
			 * @param position
			 *      the position of the data for the view clicked.
			 */
			void onChannelClick(final int position);
		}
	}
}