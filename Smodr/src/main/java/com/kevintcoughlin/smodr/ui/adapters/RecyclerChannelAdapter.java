package com.kevintcoughlin.smodr.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecyclerChannelAdapter extends RecyclerView.Adapter<RecyclerChannelAdapter.ViewHolder> {
    private Channel[] mChannels;
    private Context mContext;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public RecyclerChannelAdapter(Context context, Channel[] channels) {
        mContext = context;
        mChannels = channels;
    }

    @Override
    public RecyclerChannelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.channels_item_layout, parent, false);
        final ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Channel channel = mChannels[position];
        final int coverPhotoResource = mContext
                .getResources()
                .getIdentifier(channel.getShortName().replace("-", ""), "drawable", mContext.getPackageName());

        Picasso.with(mContext)
                .load(coverPhotoResource)
                .fit()
                .centerCrop()
                .into(holder.mCoverPhoto);
    }

    public Channel getItem(int position) {
        return mChannels[position];
    }

    @Override
    public int getItemCount() {
        return mChannels.length;
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
        @InjectView(R.id.image)
        ImageView mCoverPhoto;

        private RecyclerChannelAdapter mAdapter;

        public ViewHolder(View v, RecyclerChannelAdapter adapter) {
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