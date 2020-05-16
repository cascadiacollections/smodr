package com.kevintcoughlin.smodr.viewholders;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.kevintcoughlin.common.adapter.BinderRecyclerAdapter;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.utils.StringResourceUtilities;

import java.text.ParseException;
import java.util.Date;

public class EpisodeView implements BinderRecyclerAdapter.Binder<Item, EpisodeViewHolder> {
    @SuppressLint("NewApi")
    private static final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    @SuppressLint("NewApi")
    private static final SimpleDateFormat format2 = new SimpleDateFormat("dd MMM");

    @SuppressLint("NewApi")
    private static String formatDate(String dateTimeString) {
        // @todo: optimize
        Date date = null;
        String dateString = "";
        try {
            date = format.parse(dateTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            dateString = format2.format(date);
        }

        return dateString;
    }

    public EpisodeView() {

    }

    @Override
    public void bind(@NonNull final Item model, @NonNull final EpisodeViewHolder viewHolder) {
        viewHolder.mTitle.setText(model.getTitle());
        viewHolder.mDescription.setText(Html.fromHtml(model.getSummary()));
        viewHolder.mMetadata.setText(StringResourceUtilities.getString(viewHolder.mMetadata.getContext(), R.string.metadata, formatDate(model.getPubDate()), model.getDuration()));

        if (model.getCompleted()) {
            // @todo
            viewHolder.mTitle.setTextColor(Color.rgb(200,0,0));
        }
    }

    @Override
    public EpisodeViewHolder createViewHolder(@NonNull final ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_episode_layout, parent, false);
        return new EpisodeViewHolder(view);
    }
}