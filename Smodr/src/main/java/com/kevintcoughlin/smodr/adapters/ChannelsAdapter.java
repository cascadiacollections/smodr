package com.kevintcoughlin.smodr.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.data.model.Channel;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChannelsAdapter extends CursorAdapter {

    private Context mContext;

    public ChannelsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.channels_grid_item_layout, parent, false);
        final ViewHolder holder;
        holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        Channel channel = new Channel(cursor);

        Picasso.with(context)
                .load(channel.getCoverPhotoUrl())
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(holder.mImage);
    }

    static class ViewHolder {
        @InjectView(R.id.image)
        ImageView mImage;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}