package com.cascadiacollections.jamoka.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter
import com.cascadiacollections.jamoka.R

/**
 * Generic fragment with RecyclerView and SwipeRefreshLayout integration.
 * Supports binding adapters, layout managers, and item selection callbacks.
 */
abstract class BinderRecyclerFragment<T, VH : RecyclerView.ViewHolder>(
    @LayoutRes private val layoutResId: Int = R.layout.fragment_recycler_layout
) : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    protected lateinit var swipeRefreshLayout: SwipeRefreshLayout
    protected lateinit var recyclerView: RecyclerView

    protected abstract val adapter: BinderRecyclerAdapter<T, VH>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view.findViewById(R.id.swipeContainer)
        recyclerView = view.findViewById(R.id.list)

        recyclerView.apply {
            setHasFixedSize(true)
            configureRecyclerView(this)
        }

        swipeRefreshLayout.apply {
            setOnRefreshListener(this@BinderRecyclerFragment)
            configureSwipeRefreshLayout(this)
        }
    }

    /**
     * Provides a hook for subclasses to configure the RecyclerView.
     * This is where the LayoutManager and Adapter should be set.
     *
     * @param recyclerView The RecyclerView instance.
     */
    protected open fun configureRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = this@BinderRecyclerFragment.adapter
    }

    /**
     * Provides a hook for subclasses to customize the SwipeRefreshLayout.
     *
     * @param swipeRefreshLayout The SwipeRefreshLayout instance.
     */
    protected open fun configureSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        // Optional for subclasses to customize further
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     * Subclasses should implement their refresh logic here.
     */
    abstract override fun onRefresh()
}