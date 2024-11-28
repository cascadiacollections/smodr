package com.kevintcoughlin.smodr.views.fragments

import BinderRecyclerAdapter
import BinderRecyclerAdapterConfig
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
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

class EpisodesFragment : BinderRecyclerFragment<Item, EpisodeViewHolder>(), Callback<Feed?> {
    private val feedService: FeedService by lazy { createFeedService() }
    private val adapter: BinderRecyclerAdapter<Item, EpisodeViewHolder> by lazy {
        BinderRecyclerAdapter(
            binder = EpisodeView(),
            config = BinderRecyclerAdapterConfig(
                enableDiffUtil = false
            )
        )
    }
    override fun getAdapter(): RecyclerView.Adapter<*> = adapter

    override fun getLayoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

    override fun onRefresh() {
        fetchEpisodes()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView?.apply {
            setHasFixedSize(true)
            layoutManager = this@EpisodesFragment.getLayoutManager()
            adapter = this@EpisodesFragment.getAdapter()
        }

        val dummyData = listOf(
            Item(
                guid = "1",
                title = "Episode 1",
                pubDate = "2024-11-01",
                description = "This is a description for Episode 1",
                duration = "25:00",
                summary = "Summary of Episode 1",
                origEnclosureLink = "https://example.com/episode1.mp3",
                completed = false
            ),
            Item(
                guid = "2",
                title = "Episode 2",
                pubDate = "2024-11-02",
                description = "This is a description for Episode 2",
                duration = "30:00",
                summary = "Summary of Episode 2",
                origEnclosureLink = "https://example.com/episode2.mp3",
                completed = false
            ),
            Item(
                guid = "3",
                title = "Episode 3",
                pubDate = "2024-11-03",
                description = "This is a description for Episode 3",
                duration = "40:00",
                summary = "Summary of Episode 3",
                origEnclosureLink = "https://example.com/episode3.mp3",
                completed = false
            )
        )
        adapter.updateItems(dummyData)
//        fetchEpisodes()
    }

    override fun onResponse(call: Call<Feed?>, response: Response<Feed?>) {
        response.body()?.channel?.items?.let {
            adapter.updateItems(it)
        }
    }

    override fun onFailure(call: Call<Feed?>, t: Throwable) {
        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
    }

    private fun fetchEpisodes() {
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
        fun create(channel: Channel): Fragment {
            return EpisodesFragment().apply {
                arguments = Bundle().apply {
                    putString(EPISODE_FEED_URL, channel.link)
                }
            }
        }
    }
}