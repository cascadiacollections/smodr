package com.kevintcoughlin.smodr.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Item;

import java.util.List;

public class EpisodesListAdapter extends ArrayAdapter {
    private final Context mContext;
    private final List<Item> mItems;

    public EpisodesListAdapter(Context context, List<Item> items) {
        super(context, R.layout.episodes_list_item_layout, items);
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.episodes_list_item_layout, parent, false);
        TextView mTitleView = (TextView) rowView.findViewById(R.id.title);
        TextView mDescriptionView = (TextView) rowView.findViewById(R.id.description);
        mTitleView.setText(mItems.get(position).getTitle());
        mDescriptionView.setText(mItems.get(position).getDescription());
        return rowView;
    }
}
