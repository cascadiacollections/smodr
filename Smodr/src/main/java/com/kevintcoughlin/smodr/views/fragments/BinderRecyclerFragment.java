package com.kevintcoughlin.smodr.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.BinderRecyclerAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

abstract class BinderRecyclerFragment<T, VH extends RecyclerView.ViewHolder> extends Fragment implements BinderRecyclerAdapter.OnClick<T> {
    @Bind(R.id.list)
    protected RecyclerView mRecyclerView;

    abstract BinderRecyclerAdapter<T, VH> getAdapter();

    abstract RecyclerView.LayoutManager getLayoutManager();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recycler_layout, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final BinderRecyclerAdapter<T, VH> adapter = getAdapter();
        adapter.setOnClickListener(this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.setAdapter(adapter);
    }
}
