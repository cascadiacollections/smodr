package com.kevintcoughlin.smodr.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kevintcoughlin.smodr.R;

public class EpisodeViewHolder extends RecyclerView.ViewHolder {
	@Bind(R.id.title)
	TextView mTitle;
	@Bind(R.id.description)
	TextView mDescription;

	public EpisodeViewHolder(final View view) {
		super(view);
		ButterKnife.bind(this, view);
	}
}
