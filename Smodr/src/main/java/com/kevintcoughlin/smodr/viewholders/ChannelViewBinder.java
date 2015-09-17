package com.kevintcoughlin.smodr.viewholders;

import android.support.annotation.NonNull;
import android.view.View;
import com.bumptech.glide.Glide;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.parse.ParseObject;

public final class ChannelViewBinder implements BinderAdapter.Binder<ParseObject, ChannelViewHolder> {
	@Override
	public void bind(@NonNull final ParseObject model, @NonNull final ChannelViewHolder viewHolder) {
		Glide.with(viewHolder.itemView.getContext())
				.load(model.getString("image_url"))
				.fitCenter()
				.crossFade()
				.into(viewHolder.mImage);
	}

	@NonNull
	@Override
	public ChannelViewHolder createViewHolder(View view) {
		return new ChannelViewHolder(view);
	}
}
