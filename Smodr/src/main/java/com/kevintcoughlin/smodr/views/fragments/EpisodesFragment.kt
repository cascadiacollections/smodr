package com.kevintcoughlin.smodr.views.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cascadiacollections.jamoka.fragment.BinderRecyclerFragment
import com.kevintcoughlin.smodr.models.Item
import com.kevintcoughlin.smodr.viewholders.EpisodeViewHolder
import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter
import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapterConfig
import com.kevintcoughlin.smodr.viewholders.EpisodeView

class EpisodesFragment : BinderRecyclerFragment<Item, EpisodeViewHolder>() {
    override val adapter: BinderRecyclerAdapter<Item, EpisodeViewHolder> by lazy {
        BinderRecyclerAdapter(
            binder = EpisodeView(), // Use the new binder
            config = BinderRecyclerAdapterConfig.Builder<Item>()
                .enableDiffUtil(true) // Re-enable DiffUtil
                .build()
        )
    }

    override fun configureRecyclerView(recyclerView: RecyclerView) {
        super.configureRecyclerView(recyclerView) // Sets the adapter
        recyclerView.layoutManager = LinearLayoutManager(context) // Set LayoutManager
    }

    override fun onRefresh() {
        fetchEpisodes()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchEpisodes()
    }

    private fun fetchEpisodes() {
        val episodes = listOf(
            Item(
                title = "Fetched Episode 1",
                summary = "<p>This is the first fetched episode.</p>",
                pubDate = "Mon, 15 Aug 2023 10:00:00 -0400",
                duration = "30 min"
            ),
            Item(
                title = "Fetched Episode 2",
                summary = "<p>This is the <b>second</b> fetched episode.</p>",
                pubDate = "Tue, 16 Aug 2023 12:00:00 -0400",
                duration = "45 min"
            )
        )
        adapter.updateItems(episodes)
        swipeRefreshLayout.isRefreshing = false
    }
}