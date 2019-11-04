package com.kevintcoughlin.smodr.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.ui.MainView;
import com.kevintcoughlin.smodr.ui.presenters.MainPresenter;
import com.kevintcoughlin.smodr.ui.presenters.MainPresenterImpl;

public class MainActivity extends ActionBarActivity implements MainView {
    public static final String TAG = MainActivity.class.getSimpleName();
    private MainPresenter mMainPresenter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_channels);
        mMainPresenter = new MainPresenterImpl(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        mMainPresenter.initializeViews();

        if (savedInstanceState != null) {
            mMainPresenter.restoreState(savedInstanceState);
        } else {
            mMainPresenter.initializeMainLayout(getIntent());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMainPresenter.saveState(outState);
    }

    @Override
    public void initializeToolbar() {
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.app_name);
            mToolbar.setBackgroundColor(getResources().getColor(R.color.orange));
            setSupportActionBar(mToolbar);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public int getMainLayoutId() {
        return R.id.container;
    }
}