package com.kevintcoughlin.smodr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EpisodesAdapter extends ArrayAdapter<Item> {
    private final LayoutInflater mInflater;

    public static class ViewHolder {
        @InjectView(R.id.title) public TextView title;
        @InjectView(R.id.description) public TextView description;

        public ViewHolder(final View view) {
            ButterKnife.inject(this, view);
        }
    }

    public EpisodesAdapter(Context context, List<Item> items) {
        super(context, R.layout.episodes_list_item_layout, items);
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.episodes_list_item_layout, parent, false);

            final ViewHolder viewHolder = new ViewHolder(convertView);

            assert convertView != null;
            convertView.setTag(viewHolder);
        }

        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        final Item item = getItem(position);

        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());

        return convertView;
    }
}

