package com.kevintcoughlin.smodr.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.data.model.Episodes;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EpisodesAdapter extends CursorAdapter {

    private Context mContext;

    public EpisodesAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.episodes_list_item_layout, parent, false);
        final ViewHolder holder;
        holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();

        Episodes episode = new Episodes(cursor);

        holder.mTitle.setText(episode.getTitle());
        holder.mDescription.setText(episode.getDescription());
    }

    static class ViewHolder {
        @InjectView(R.id.title)
        TextView mTitle;

        @InjectView(R.id.description)
        TextView mDescription;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}