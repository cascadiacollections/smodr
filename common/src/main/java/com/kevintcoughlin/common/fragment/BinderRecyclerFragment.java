package com.kevintcoughlin.common.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevintcoughlin.common.R;
import com.kevintcoughlin.common.adapter.BinderRecyclerAdapter;

public abstract class BinderRecyclerFragment<T, VH extends RecyclerView.ViewHolder> extends Fragment implements BinderRecyclerAdapter.OnClick<T> {
    protected RecyclerView mRecyclerView;

    protected abstract BinderRecyclerAdapter<T, VH> getAdapter();

    protected abstract RecyclerView.LayoutManager getLayoutManager();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_recycler_layout, container, false);

        mRecyclerView = view.findViewById(R.id.list);

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
