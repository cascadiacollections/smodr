package com.kevintcoughlin.smodr.ui.presenters;

import android.content.Intent;

public interface ChannelPresenter {
    public void initializeViews();

    public void onApplyColorChange(int color);

    public void onSwipeRefresh();

    public void onFavourite();

    public void initializeData();

    public void handleInitialData(Intent intent);
}