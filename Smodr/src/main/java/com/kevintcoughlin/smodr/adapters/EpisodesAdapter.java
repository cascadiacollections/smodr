package com.kevintcoughlin.smodr.adapters;

import android.support.annotation.NonNull;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	    final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.episodes_list_item_layout, parent, false);
	    return new ItemViewHolder(v);
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

	public void setResults(ArrayList<Item> results) {
        mItems.clear();
        mItems.addAll(results);
		notifyDataSetChanged();
	}

	public static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title) TextView mTitle;
        @Bind(R.id.description) TextView mDescription;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}