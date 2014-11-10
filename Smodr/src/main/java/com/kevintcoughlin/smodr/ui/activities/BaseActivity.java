package com.kevintcoughlin.smodr.ui.activities;

import com.kevintcoughlin.smodr.ui.fragments.ChannelsFragment;

public class BaseActivity extends ChannelsActivity implements ChannelsFragment.Callbacks {
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }
}
