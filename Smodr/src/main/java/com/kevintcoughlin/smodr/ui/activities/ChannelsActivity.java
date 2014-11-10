package com.kevintcoughlin.smodr.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.ui.fragments.ChannelsFragment;

public class ChannelsActivity extends ActionBarActivity implements ChannelsFragment.Callbacks {
    private static final String TAG = "ChannelsView";
    private Toolbar mActionBarToolbar;
    private int mThemedStatusBarColor;
    private int mNormalStatusBarColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_channels);

        //mThemedStatusBarColor = getResources().getColor(R.color.theme_primary_dark);
        //mNormalStatusBarColor = mThemedStatusBarColor;

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
    }

    public int getThemedStatusBarColor() {
        return mThemedStatusBarColor;
    }

    public void setNormalStatusBarColor(int color) {
        mNormalStatusBarColor = color;
    }

    @Override
    public void onChannelSelected(String shortName, String photoUrl, long channelId, String title) {

    }

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
    }

}
