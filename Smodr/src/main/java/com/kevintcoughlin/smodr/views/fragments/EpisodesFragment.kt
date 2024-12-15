package com.kevintcoughlin.smodr.views.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cascadiacollections.jamoka.fragment.BinderRecyclerFragment
import com.kevintcoughlin.smodr.models.Channel
import com.kevintcoughlin.smodr.models.Feed
import com.kevintcoughlin.smodr.models.Item
import com.kevintcoughlin.smodr.services.FeedService
import com.kevintcoughlin.smodr.viewholders.EpisodeView
import com.kevintcoughlin.smodr.viewholders.EpisodeViewHolder
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import BinderRecyclerAdapter
import BinderRecyclerAdapterConfig

class EpisodesFragment : BinderRecyclerFragment(), Callback<Feed?> {
    private val feedService: FeedService by lazy { createFeedService() }
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

    override fun onResponse(call: Call<Feed?>, response: Response<Feed?>) {
        swipeRefreshLayout.isRefreshing = false
        response.body()?.channel?.items?.let {
            adapter.updateItems(it)
        }
    }

    override fun onFailure(call: Call<Feed?>, t: Throwable) {
        swipeRefreshLayout.isRefreshing = false
        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
    }

    private fun fetchEpisodes() {
        swipeRefreshLayout.isRefreshing = true
        arguments?.getString(EPISODE_FEED_URL)?.let { feedUrl ->
            feedService.feed(feedUrl).enqueue(this)
        }
    }

    private fun createFeedService(): FeedService {
        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(TikXmlConverterFactory.create())
            .build()
        return retrofit.create(FeedService::class.java)
    }

    companion object {
        private const val EPISODE_FEED_URL =
            "com.kevintcoughlin.smodr.views.fragments.EpisodesFragment.feedUrl"
        private const val BASE_URL = "http://feeds.feedburner.com/TellEmSteveDave/"
        val TAG: String = EpisodesFragment::class.java.simpleName

        @JvmStatic
        fun create(channel: Channel): EpisodesFragment {
            return EpisodesFragment().apply {
                arguments = Bundle().apply {
                    putString(EPISODE_FEED_URL, channel.link)
                }
            }
        }
    }
}