package com.cascadiacollections.jamoka.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter;
import com.cascadiacollections.jamoka.R;

import java.lang.ref.WeakReference;

/**
 * Generic fragment with RecyclerView and SwipeRefreshLayout integration.
 * Supports binding adapters, layout managers, and item selection callbacks.
 */
public abstract class BinderRecyclerFragment<T, VH extends RecyclerView.ViewHolder> extends Fragment
        implements BinderRecyclerAdapter.IListeners<T>, SwipeRefreshLayout.OnRefreshListener {

    private static final String VIEW_STATE_KEY = "BinderRecyclerFragment.ViewState";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private WeakReference<OnItemSelected<T>> mOnItemSelectedCallback;

    /**
     * Provide the adapter for the RecyclerView.
     */
    protected abstract BinderRecyclerAdapter<T, VH> getAdapter();

    /**
     * Provide the layout manager for the RecyclerView.
     */
    protected abstract RecyclerView.LayoutManager getLayoutManager();

    /**
     * Retrieve the RecyclerView instance.
     */
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * Interface for item selection callback.
     */
    public interface OnItemSelected<T> {
        void onItemSelected(T item);
    }

    /**
     * Set the callback listener for item selection.
     */
    public void setOnItemSelectedListener(OnItemSelected<T> listener) {
        mOnItemSelectedCallback = new WeakReference<>(listener);
    }

    @Override
    public void onClick(@NonNull final T item) {
        OnItemSelected<T> listener = mOnItemSelectedCallback != null ? mOnItemSelectedCallback.get() : null;
        if (listener != null) {
            listener.onItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cleanup references
        mOnItemSelectedCallback = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = view.findViewById(R.id.swipeContainer);
        mRecyclerView = view.findViewById(R.id.list);

        setupRecyclerView();
        setupSwipeRefresh();
    }

    /**
     * Save the RecyclerView's layout state.
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getLayoutManager() != null) {
            Parcelable layoutState = getLayoutManager().onSaveInstanceState();
            outState.putParcelable(VIEW_STATE_KEY, layoutState);
        }
    }

    /**
     * Restore the RecyclerView's layout state.
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Parcelable layoutState = savedInstanceState.getParcelable(VIEW_STATE_KEY, Parcelable.class);

            if (layoutState != null && getLayoutManager() != null) {
                getLayoutManager().onRestoreInstanceState(layoutState);
            }
        }
    }

    /**
     * Configures the RecyclerView with the provided adapter and layout manager.
     */
    private void setupRecyclerView() {
        BinderRecyclerAdapter<T, VH> adapter = getAdapter();
        if (adapter != null) {
            adapter.setOnClickListener(this);
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * Configures the SwipeRefreshLayout and its listener.
     */
    private void setupSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }
}