package com.kevintcoughlin.smodr.viewholders;

import android.support.annotation.NonNull;
import android.view.View;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.kevintcoughlin.smodr.models.Item;

public final class EpisodeViewBinder implements BinderAdapter.Binder<Item, EpisodeViewHolder> {
	@Override
	public void bind(@NonNull Item model, @NonNull EpisodeViewHolder viewHolder) {
		viewHolder.mTitle.setText(model.title());
		viewHolder.mDescription.setText(model.description());
	}

	@NonNull
	@Override
	public EpisodeViewHolder createViewHolder(View view) {
		return new EpisodeViewHolder(view);
	}
}
