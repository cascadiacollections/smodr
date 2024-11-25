package com.cascadiacollections.jamoka.fragment

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cascadiacollections.jamoka.R

/**
 * Generic fragment with RecyclerView and SwipeRefreshLayout integration.
 * Supports binding adapters, layout managers, and item selection callbacks.
 */
abstract class BinderRecyclerFragment<T, VH : RecyclerView.ViewHolder> : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null

    /**
     * Provide the layout manager for the RecyclerView.
     */
    protected abstract fun getLayoutManager(): RecyclerView.LayoutManager?

    @LayoutRes
    protected open fun getLayoutResId(): Int = R.layout.fragment_recycler_layout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer)
        recyclerView = view.findViewById(R.id.list)

        setupRecyclerView()
        setupSwipeRefresh()
    }

    /**
     * Save the RecyclerView's layout state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getLayoutManager()?.onSaveInstanceState()?.let {
            outState.putParcelable(VIEW_STATE_KEY, it)
        }
    }

    /**
     * Restore the RecyclerView's layout state.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getParcelable(VIEW_STATE_KEY, Parcelable::class.java)?.let { layoutState ->
            getLayoutManager()?.onRestoreInstanceState(layoutState)
        }
    }

    /**
     * Configures the RecyclerView with the provided adapter and layout manager.
     */
    private fun setupRecyclerView() {
        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = layoutManager
            this.adapter = adapter
        }
    }

    /**
     * Configures the SwipeRefreshLayout and its listener.
     */
    private fun setupSwipeRefresh() {
        swipeRefreshLayout?.setOnRefreshListener(this)
    }

    companion object {
        private const val VIEW_STATE_KEY = "BinderRecyclerFragment.ViewState"
    }
}