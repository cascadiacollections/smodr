package com.kevintcoughlin.smodr.ui.presenters;

import android.os.Bundle;

public interface ChannelsPresenter {
    public void initializeViews();

    public void saveState(Bundle outState);

    public void restoreState(Bundle savedState);

    public void releaseAllResources();
}
