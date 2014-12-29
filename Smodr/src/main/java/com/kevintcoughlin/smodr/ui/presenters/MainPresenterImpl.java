package com.kevintcoughlin.smodr.ui.presenters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.kevintcoughlin.smodr.ui.MainView;
import com.kevintcoughlin.smodr.ui.fragments.ChannelsFragment;

public class MainPresenterImpl implements MainPresenter {
    public static final String TAG = MainPresenterImpl.class.getSimpleName();
    private MainView mMainView;
    private Fragment mFragment;

    public MainPresenterImpl(MainView mainView) {
        mMainView = mainView;
    }

    @Override
    public void initializeViews() {
        mMainView.initializeToolbar();
    }

    @Override
    public void initializeMainLayout(Intent argument) {
        if (mFragment == null) {
            mFragment = new ChannelsFragment();
        }

        ((FragmentActivity) mMainView.getContext()).getSupportFragmentManager().beginTransaction()
                .add(mMainView.getMainLayoutId(), mFragment)
                .commit();
    }

    @Override
    public void saveState(Bundle outState) {

    }

    @Override
    public void restoreState(Bundle savedState) {

    }
}