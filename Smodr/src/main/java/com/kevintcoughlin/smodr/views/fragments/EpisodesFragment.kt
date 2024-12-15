package com.kevintcoughlin.smodr.views.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cascadiacollections.jamoka.fragment.BinderRecyclerFragment
import com.kevintcoughlin.smodr.models.Item
import com.kevintcoughlin.smodr.viewholders.EpisodeView
import com.kevintcoughlin.smodr.viewholders.EpisodeViewHolder
import BinderRecyclerAdapter
import BinderRecyclerAdapterConfig

class EpisodesFragment : BinderRecyclerFragment() {
    private val adapter: BinderRecyclerAdapter<Item, EpisodeViewHolder> by lazy {
        BinderRecyclerAdapter(
            viewHolderBinder = EpisodeView(),
            config = BinderRecyclerAdapterConfig.Builder<Item>()
                .enableDiffUtil(false)
                .build()
        )
    }

    override fun configureRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = this@EpisodesFragment.adapter
        }
    }

    override fun onRefresh() {
        fetchEpisodes()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchEpisodes()
    }

    private fun fetchEpisodes() {
        swipeRefreshLayout.isRefreshing = true
    }
}