package com.kevintcoughlin.smodr.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.bumptech.glide.Glide;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.parse.ParseObject;

public final class ChannelViewBinder implements BinderAdapter.Binder<ParseObject,RecyclerView.ViewHolder> {
	@Override
	public void bind(@NonNull final ParseObject model, @NonNull final RecyclerView.ViewHolder viewHolder) {
		final ChannelViewHolder vh = (ChannelViewHolder) viewHolder;
		Glide.with(vh.itemView.getContext())
				.load(model.getString("image_url"))
				.fitCenter()
				.crossFade()
				.into(vh.mImage);
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder createViewHolder(View view) {
		return new ChannelViewHolder(view);
	}
}
