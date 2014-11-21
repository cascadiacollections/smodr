package com.kevintcoughlin.smodr.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Item;
import com.kevintcoughlin.smodr.models.Rss;
import com.kevintcoughlin.smodr.ui.adapters.RecyclerEpisodeAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EpisodesFragment extends Fragment {
    public static final String TAG = "EpisodesListViewFragment";
    private Context mAppContext;
    private RecyclerView mRecyclerView;
    private RecyclerEpisodeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public interface Callbacks {
        public void onEpisodeSelected(Item episode);
    }

    private static Callbacks sEpisodesCallbacks = new Callbacks() {
        @Override
        public void onEpisodeSelected(Item episode) {}
    };

    private Callbacks mCallbacks = sEpisodesCallbacks;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppContext = getActivity().getApplicationContext();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.episodes_layout, container, false);

        final ArrayList<Item> items = new ArrayList<>();

        mRecyclerView = (RecyclerView) root.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(mAppContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new RecyclerEpisodeAdapter(items);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item episode = mAdapter.getItem(position);
                mCallbacks.onEpisodeSelected(episode);
            }
        });

        SmodcastClient.getClient().getFeed("smodcast", new Callback<Rss>() {
            @Override
            public void success(Rss rss, Response response) {
                items.addAll(rss.getChannel().getItems());
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks.");
        }

        mAppContext = getActivity().getApplicationContext();
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sEpisodesCallbacks;
    }
}

