package com.kevintcoughlin.smodr.ui.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.util.PaletteBitmapTarget;
import com.kevintcoughlin.smodr.util.PaletteBitmapTranscoder;
import com.kevintcoughlin.smodr.util.PaletteBitmapWrapper;
import com.kevintcoughlin.smodr.util.PaletteUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecyclerChannelsAdapter extends RecyclerView.Adapter<RecyclerChannelsAdapter.ViewHolder> {
    private final Context mContext;
    private final Channel[] mChannels;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public RecyclerChannelsAdapter(final Context context, final Channel[] channels) {
        mContext = context;
        mChannels = channels;
    }

    @Override
    public RecyclerChannelsAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.channels_item_layout, parent, false);
        final ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Channel channel = mChannels[position];
        final int coverPhotoResource = mContext
                .getResources()
                .getIdentifier(channel.getShortName().replace("-", ""), "drawable", mContext.getPackageName());

        Glide.with(this.mContext)
                .load(coverPhotoResource)
                .asBitmap()
                .transcode(new PaletteBitmapTranscoder(), PaletteBitmapWrapper.class)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .animate(android.R.anim.fade_in)
                .into(new PaletteBitmapTarget(holder.mCoverPhoto) {
                    @Override
                    public void onResourceReady(PaletteBitmapWrapper resource, GlideAnimation<? super PaletteBitmapWrapper> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);

                        final int color = PaletteUtils.getColorWithDefault(resource.getPalette(), R.color.orange);
                        final GradientDrawable footer = new GradientDrawable();

                        footer.setCornerRadii(new float[] { 0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 4.0f, 4.0f, 4.0f });
                        footer.setColor(color);

                        holder.mCoverPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        holder.mFooter.setBackground(footer);
                    }
                });
        holder.mChannelName.setText(channel.getTitle());
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public Channel getItem(final int position) {
        return mChannels[position];
    }

    @Override
    public int getItemCount() {
        return mChannels.length;
    }

    public void setOnItemClickListener(final AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private void onItemHolderClick(final ViewHolder viewHolder) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(null, viewHolder.itemView, viewHolder.getPosition(), viewHolder.getItemId());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final RecyclerChannelsAdapter mAdapter;

        @InjectView(R.id.image)
        ImageView mCoverPhoto;

        @InjectView(R.id.name)
        TextView mChannelName;

        @InjectView(R.id.footer)
        LinearLayout mFooter;

        public ViewHolder(final View v, final RecyclerChannelsAdapter adapter) {
            super(v);
            mAdapter = adapter;
            v.setOnClickListener(this);
            ButterKnife.inject(this, v);
        }

        @Override
        public void onClick(final View v) {
            mAdapter.onItemHolderClick(this);
        }
    }
}