package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.ItemsAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ChannelsFragment extends Fragment {
    private static final int NUM_OF_COLUMNS = 2;

    @NonNull
    private final RecyclerView.Adapter mAdapter = new ItemsAdapter();

    @Bind(R.id.list)
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recycler_layout, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), NUM_OF_COLUMNS));
        mRecyclerView.setAdapter(mAdapter);
    }
}