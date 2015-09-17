package com.kevintcoughlin.smodr.viewholders;

import android.support.annotation.NonNull;
import android.view.View;
import com.kevintcoughlin.smodr.adapters.BinderAdapter;
import com.parse.ParseObject;

public final class EpisodeViewBinder implements BinderAdapter.Binder<ParseObject, EpisodeViewHolder> {
	@Override
	public void bind(@NonNull ParseObject model, @NonNull EpisodeViewHolder viewHolder) {
		viewHolder.mTitle.setText(model.getString("title"));
		viewHolder.mDescription.setText(model.getString("description"));
	}

	@NonNull
	@Override
	public EpisodeViewHolder createViewHolder(View view) {
		return new EpisodeViewHolder(view);
	}
}
