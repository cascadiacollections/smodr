package com.kevintcoughlin.smodr.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.kevintcoughlin.smodr.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ChannelViewHolder extends RecyclerView.ViewHolder {
	@Bind(R.id.image)
	SimpleDraweeView mImage;

	public ChannelViewHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}
}
