package com.kevintcoughlin.smodr.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cascadiacollections.jamoka.adapter.BinderRecyclerAdapter
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

class EpisodesFragment : BinderRecyclerFragment<Item?, EpisodeViewHolder?>(), Callback<Feed?> {

    private val feedService: FeedService by lazy { createFeedService() }
    private val adapter: ItemAdapter by lazy { ItemAdapter() }

    // @todo - Refactor to
    // private val layoutManager: LinearLayoutManager by lazy { LinearLayoutManager(context) }
    override fun getLayoutManager(): LinearLayoutManager = layoutManager

    override fun getAdapter(): BinderRecyclerAdapter<Item?, EpisodeViewHolder?> = adapter

    override fun onRefresh() = fetchEpisodes()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        )
        fetchEpisodes()
    }

    override fun onLongClick(item: Item?): Boolean = true

    override fun onResponse(call: Call<Feed?>, response: Response<Feed?>) {
        response.body()?.channel?.item?.let { adapter.setItems(it) }
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

    private class ItemAdapter : BinderRecyclerAdapter<Item?, EpisodeViewHolder?>(EpisodeView()) {
        @SuppressLint("NotifyDataSetChanged")
        fun setItems(newItems: List<Item?>) {
            mItems = newItems.toMutableList()
            notifyDataSetChanged()
        }
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