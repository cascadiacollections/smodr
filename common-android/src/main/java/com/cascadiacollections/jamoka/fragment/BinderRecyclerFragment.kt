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
abstract class BinderRecyclerFragment<T, VH : RecyclerView.ViewHolder> : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var recyclerView: RecyclerView

    abstract fun getLayoutManager(): RecyclerView.LayoutManager

    abstract fun getAdapter(): RecyclerView.Adapter<*>

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
        swipeRefreshLayout.setOnRefreshListener(this)
    }
}