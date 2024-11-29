package com.cascadiacollections.jamoka.fragment

import android.os.Bundle
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
abstract class BinderRecyclerFragment<T, VH : RecyclerView.ViewHolder>(
    @LayoutRes private val layoutResId: Int = R.layout.fragment_recycler_layout
) : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    protected val swipeRefreshLayout: SwipeRefreshLayout
        get() = requireView().findViewById(R.id.swipeContainer)

    protected val recyclerView: RecyclerView
        get() = requireView().findViewById(R.id.list)

    abstract fun getLayoutManager(): RecyclerView.LayoutManager

    abstract fun getAdapter(): RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layoutResId, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = getLayoutManager()
            adapter = getAdapter()
            configureRecyclerView(this)
        }

        swipeRefreshLayout.apply {
            setOnRefreshListener(this@BinderRecyclerFragment)
            configureSwipeRefresh(this)
        }
    }

    protected open fun configureRecyclerView(recyclerView: RecyclerView) {
        // Optional for subclasses
    }

    protected open fun configureSwipeRefresh(swipeRefreshLayout: SwipeRefreshLayout) {
        // Optional for subclasses
    }
}