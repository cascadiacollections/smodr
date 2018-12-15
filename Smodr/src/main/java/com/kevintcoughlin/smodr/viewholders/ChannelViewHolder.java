package com.kevintcoughlin.smodr.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

//import com.facebook.drawee.view.SimpleDraweeView;

final class ChannelViewHolder extends RecyclerView.ViewHolder {
//    @Bind(R.id.image)
//    SimpleDraweeView mImage;

    ChannelViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
