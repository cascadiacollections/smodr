package com.kevintcoughlin.smodr.viewholders;

import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.kevintcoughlin.smodr.R;


public final class ChannelViewHolder extends BaseViewHolder {
	@Bind(R.id.image)
	ImageView mImage;

	public ChannelViewHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}
}
