package com.kevintcoughlin.smodr.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kevintcoughlin.smodr.R;

import butterknife.Bind;
import butterknife.ButterKnife;

class EpisodeViewHolder extends RecyclerView.ViewHolder {
	@Bind(R.id.title)
	TextView mTitle;
	@Bind(R.id.description)
	TextView mDescription;

	EpisodeViewHolder(final View view) {
		super(view);
		ButterKnife.bind(this, view);
	}
}
