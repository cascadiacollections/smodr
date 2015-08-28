package com.kevintcoughlin.smodr.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kevintcoughlin.smodr.R;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying a collection of {@link Item}s.
 *
 * @author kevincoughlin
 */
public final class EpisodesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	/**
	 * The collection of {@link Item} to display.
	 */
	@NonNull
    private final ArrayList<ParseObject> mItems = new ArrayList<>();
	/**
	 * The interface for clicks on {@link com.kevintcoughlin.smodr.adapters.EpisodesAdapter.ItemViewHolder}.
	 */
	@Nullable
	private ItemViewHolder.IItemViewHolderClicks mListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	    final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_episode_layout, parent, false);
	    return new ItemViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ParseObject item = mItems.get(position);
		final ItemViewHolder vh = (ItemViewHolder) holder;
	    vh.mTitle.setText(item.getString("title"));
	    vh.mDescription.setText(item.getString("description"));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

	/**
	 * Returns a {@link Item} at the given position.
	 *
	 * @param position
	 *      the position to fetch.
	 *
	 * @return
	 *      the resultant {@link Item}.
	 */
	public ParseObject getItem(final int position) {
		return mItems.get(position);
	}

	/**
	 * Clear and set the adapter to the passed in collection and invokes {@link #notifyDataSetChanged()}.
	 *
	 * @param results
	 * 		a {@link List <Item>} to set the adapter to.
	 */
	public void setResults(final List<ParseObject> results) {
		mItems.clear();
        mItems.addAll(results);
		notifyDataSetChanged();
	}

	/**
	 * Returns a {@link List<Item>} backing the adapter.
	 *
	 * @return a {@link List<Item>}.
	 */
	public ArrayList<ParseObject> getEpisodes() {
		return mItems;
	}

	/**
	 * Sets the click listener for {@link com.kevintcoughlin.smodr.adapters.EpisodesAdapter.ItemViewHolder}.
	 *
	 * @param listener
	 * 		the {@link com.kevintcoughlin.smodr.adapters.EpisodesAdapter.ItemViewHolder.IItemViewHolderClicks}
	 * 		to set.
	 */
	public void setClickListener(@Nullable final ItemViewHolder.IItemViewHolderClicks listener) {
		mListener = listener;
	}

	/**
	 * A {@link android.support.v7.widget.RecyclerView.ViewHolder} for display a {@link Item}.
	 */
	public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.title)
		TextView mTitle;
		@Bind(R.id.description)
		TextView mDescription;
		@Nullable
		private final IItemViewHolderClicks mListener;

		public ItemViewHolder(View itemView, @Nullable IItemViewHolderClicks listener) {
			super(itemView);
            ButterKnife.bind(this, itemView);
			mListener = listener;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			if (mListener != null) {
				mListener.onItemClick(getAdapterPosition());
			}
		}

		/**
		 * Interface for clicks on the view.
		 */
		public interface IItemViewHolderClicks {
			/**
			 * Invoked when the view is clicked, returning the corresponding position.
			 *
			 * @param position
			 *      the position of the data for the view clicked.
			 */
			void onItemClick(final int position);
		}
	}
}