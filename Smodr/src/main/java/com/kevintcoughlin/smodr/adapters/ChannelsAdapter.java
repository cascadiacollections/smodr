package com.kevintcoughlin.smodr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChannelsAdapter extends ArrayAdapter<Channel> {

    private static final String TAG = "ChannelsAdapter";
    private final Context context;
    private final ArrayList<Channel> channels;

    static class ViewHolder {
        ImageView image;
    }

    private final LayoutInflater mLayoutInflater;

    public ChannelsAdapter(final Context context, final int textViewResourceId, ArrayList<Channel> channels) {
        super(context, textViewResourceId, channels);
        mLayoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.channels = channels;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.channels_grid_item_layout, parent, false);
            vh = new ViewHolder();
            vh.image = (ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Channel channel = channels.get(position);

        Picasso.with(this.context)
                .load(channel.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerInside()
                .into(vh.image);

        return convertView;
    }

}
