package com.kevintcoughlin.smodr.ui;

import com.kevintcoughlin.smodr.ui.base.BaseContextView;
import com.kevintcoughlin.smodr.ui.base.BaseRecyclerView;
import com.kevintcoughlin.smodr.ui.base.BaseSwipeRefreshLayoutView;
import com.kevintcoughlin.smodr.ui.base.BaseToolbarView;

public interface ChannelView extends BaseContextView, BaseToolbarView, BaseSwipeRefreshLayoutView, BaseRecyclerView {
    public void initializeFavouriteButton(boolean isFavourite);

    public void setTitle(String title);

    public void setDescription(String description);

    public void setThumbnail(String url);

    public void setFavouriteButton(boolean isFavourite);

    public void toastMangaError();
}
