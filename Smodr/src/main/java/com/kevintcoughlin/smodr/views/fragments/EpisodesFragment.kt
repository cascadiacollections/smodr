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
import com.kevintcoughlin.smodr.database.AppDatabase
import com.kevintcoughlin.smodr.models.Channel
import com.kevintcoughlin.smodr.models.Feed
import com.kevintcoughlin.smodr.models.Item
import com.kevintcoughlin.smodr.models.Item.Companion.create
import com.kevintcoughlin.smodr.services.FeedService
import com.kevintcoughlin.smodr.viewholders.EpisodeView
import com.kevintcoughlin.smodr.viewholders.EpisodeViewHolder
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import org.jetbrains.annotations.Contract
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class EpisodesFragment : BinderRecyclerFragment<Item?, EpisodeViewHolder?>(), Callback<Feed?> {
    private var mFeedService: FeedService? = null

    fun markCompleted(item: Item?) {
        mAdapter.markCompleted(item)
    }

    private class ItemAdapter :
        BinderRecyclerAdapter<Item?, EpisodeViewHolder?>(EpisodeView()) {
        fun markCompleted(item: Item?) {
            updateItem(item)
        }

        fun updateItem(item: Item?) {
            val index = items.indexOf(item)
            val newItem = create(
                item!!, true
            )
            items[index] = newItem
            notifyItemChanged(index)
        }
    }

    private val mAdapter = ItemAdapter()
    private val mLinearLayoutManager = LinearLayoutManager(context)
    
    @Contract(pure = true)
    override fun getAdapter(): BinderRecyclerAdapter<Item?, EpisodeViewHolder?> {
        return mAdapter
    }

    @Contract(pure = true)
    override fun getLayoutManager(): LinearLayoutManager {
        return mLinearLayoutManager
    }

    override fun onRefresh() {
        fetchEpisodes()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mDividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            layoutManager.orientation
        )
        recyclerView.addItemDecoration(mDividerItemDecoration)
        val items = AppDatabase.getData(context)
        mAdapter.setItems(items)
        fetchEpisodes()
    }

    override fun onLongClick(item: Item?): Boolean {
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResponse(call: Call<Feed?>, response: Response<Feed?>) {
        val feed = response.body()
        if (feed?.channel != null) {
            val items = feed.channel!!.item
            AppDatabase.insertData(context, items)
            val dbItems = AppDatabase.getData(context)
            mAdapter.setItems(dbItems)

        }
    }

    override fun onFailure(call: Call<Feed?>, t: Throwable) {
        Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
    }

    private fun fetchEpisodes() {
        if (mFeedService == null) {
            initializeFeedService()
        }
        val arguments = arguments
        if (arguments != null) {
            val feedUrl = arguments.getString(EPISODE_FEED_URL)
            if (feedUrl != null) {
                mFeedService!!.feed(feedUrl).enqueue(this)
            }
        }
    }

    private fun initializeFeedService() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(TikXmlConverterFactory.create())
            .build()
        mFeedService = retrofit.create(FeedService::class.java)
    }

    companion object {
        private const val EPISODE_FEED_URL =
            "com.com.kevintcoughlin.smodr.views.fragments.EpisodesFragment.feedUrl"
        private const val BASE_URL = "https://www.smodcast.com/"
        @JvmStatic
        fun create(channel: Channel): Fragment {
            val fragment: Fragment = EpisodesFragment()
            val bundle = Bundle()
            bundle.putString(EPISODE_FEED_URL, channel.link)
            fragment.arguments = bundle
            return fragment
        }

        @JvmField
        val TAG: String = EpisodesFragment::class.java.simpleName
    }
}
