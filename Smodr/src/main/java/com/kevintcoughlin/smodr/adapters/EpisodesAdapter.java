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
import com.kevintcoughlin.smodr.models.Item;

import java.util.ArrayList;

public final class EpisodesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    private final ArrayList<Item> mItems = new ArrayList<>();
	@Nullable
	private ItemViewHolder.IItemViewHolderClicks mListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_episode_layout, parent, false);
	    return new ItemViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = mItems.get(position);
		final ItemViewHolder vh = (ItemViewHolder) holder;
	    vh.mTitle.setText(item.getTitle());
	    vh.mDescription.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

	public Item getItem(final int position) {
		return mItems.get(position);
	}

	public void setResults(ArrayList<Item> results) {
        mItems.clear();
        mItems.addAll(results);
		notifyDataSetChanged();
	}

	public void setClickListener(@NonNull final ItemViewHolder.IItemViewHolderClicks listener) {
		mListener = listener;
	}

	public static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		@Bind(R.id.title) TextView mTitle;
        @Bind(R.id.description) TextView mDescription;
		@NonNull
		private final IItemViewHolderClicks mListener;

		public ItemViewHolder(View itemView, @NonNull IItemViewHolderClicks listener) {
			super(itemView);
            ButterKnife.bind(this, itemView);
			mListener = listener;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			mListener.onItemClick(getAdapterPosition());
		}

		public interface IItemViewHolderClicks {
			void onItemClick(final int position);
		}
	}
}