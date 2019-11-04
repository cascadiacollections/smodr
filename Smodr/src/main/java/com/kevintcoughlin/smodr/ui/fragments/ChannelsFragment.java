package com.kevintcoughlin.smodr.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.ui.ChannelsView;
import com.kevintcoughlin.smodr.ui.adapters.RecyclerChannelsAdapter;
import com.kevintcoughlin.smodr.ui.presenters.ChannelsPresenter;
import com.kevintcoughlin.smodr.ui.presenters.ChannelsPresenterImpl;
import com.kevintcoughlin.smodr.ui.presenters.mapper.ChannelMapper;

public class ChannelsFragment extends Fragment implements ChannelsView, ChannelMapper {
    public static final String TAG = ChannelsFragment.class.getSimpleName();
    private static final int NUM_COLUMNS = 3;
    private ChannelsPresenter mChannelsPresenter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mChannelsPresenter = new ChannelsPresenterImpl(this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.channels_layout, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.list);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mChannelsPresenter.restoreState(savedInstanceState);
        }
        mChannelsPresenter.initializeViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mChannelsPresenter.releaseAllResources();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mChannelsPresenter.saveState(outState);
    }

    @Override
    public void initializeToolbar() {
        if (getActivity() instanceof ActionBarActivity) {
            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public void initializeRecyclerView() {
        if (mRecyclerView != null) {
            mLayoutManager = new GridLayoutManager(this.getContext(), NUM_COLUMNS, GridLayoutManager.VERTICAL, false);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void registerAdapter(RecyclerChannelsAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }
}