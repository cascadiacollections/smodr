package com.kevintcoughlin.smodr.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Item;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecyclerEpisodeAdapter extends RecyclerView.Adapter<RecyclerEpisodeAdapter.ViewHolder> {
    private ArrayList<Item> mDataset;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public RecyclerEpisodeAdapter(ArrayList<Item> items) {
        mDataset = items;
    }

    @Override
    public RecyclerEpisodeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.episodes_list_item_layout, parent, false);
        final ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = mDataset.get(position);
        final String title = item.getTitle();
        final String description = item.getDescription();

        holder.mTitleView.setText(title);
        holder.mDescriptionView.setText(description);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Item getItem(int position) {
        return mDataset.get(position);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(ViewHolder viewHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, viewHolder.itemView,
                    viewHolder.getPosition(), viewHolder.getItemId());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @InjectView(R.id.title)
        TextView mTitleView;

        @InjectView(R.id.description)
        TextView mDescriptionView;

        private RecyclerEpisodeAdapter mAdapter;

        public ViewHolder(View v, RecyclerEpisodeAdapter adapter) {
            super(v);
            mAdapter = adapter;
            v.setOnClickListener(this);
            ButterKnife.inject(this, v);
        }

        @Override
        public void onClick(View v) {
            mAdapter.onItemHolderClick(this);
        }
    }
}
