package com.kevintcoughlin.smodr.viewholders;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.databinding.ItemListEpisodeLayoutBinding;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.utils.StringResourceUtilities;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class EpisodeView implements BinderRecyclerAdapter.Binder<Item, EpisodeViewHolder> {
    // @todo: Date format locale aware
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static final SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
    // @todo: Date format locale aware
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static final SimpleDateFormat format2 = new SimpleDateFormat("dd MMM", Locale.US);
    // @todo: theme
    private static final int COLOR_BLACK = Color.rgb(0,0,0);
    private static final int COLOR_GRAY = Color.rgb(222,222,222);

    @RequiresApi(api = Build.VERSION_CODES.N)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            viewHolder.mMetadata.setText(StringResourceUtilities.getString(viewHolder.mMetadata.getContext(), R.string.metadata, formatDate(model.getPubDate()), model.getDuration()));
        }

        if (model.getCompleted()) {
            // @todo extension method?
            viewHolder.mTitle.setTextColor(COLOR_GRAY);
            viewHolder.mDescription.setTextColor(COLOR_GRAY);
            viewHolder.mMetadata.setTextColor(COLOR_GRAY);
        } else {
            viewHolder.mTitle.setTextColor(COLOR_BLACK);
            viewHolder.mDescription.setTextColor(COLOR_BLACK);
            viewHolder.mMetadata.setTextColor(COLOR_BLACK);
        }
    }

    @Override
    public EpisodeViewHolder createViewHolder(@NonNull final ViewGroup parent) {
        return new EpisodeViewHolder(ItemListEpisodeLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }
}