package com.kevintcoughlin.smodr.viewholders;

import android.support.annotation.NonNull;
import android.view.View;
import com.bumptech.glide.Glide;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.kevintcoughlin.smodr.models.Channel;

public final class ChannelViewBinder implements BinderAdapter.Binder<Channel, ChannelViewHolder> {
	@Override
	public void bind(@NonNull final Channel model, @NonNull final ChannelViewHolder viewHolder) {
		Glide.with(viewHolder.itemView.getContext())
				.load(model.imageUrl())
				.fitCenter()
				.into(viewHolder.mImage);
	}

	@NonNull
	@Override
	public ChannelViewHolder createViewHolder(View view) {
		return new ChannelViewHolder(view);
	}
}
