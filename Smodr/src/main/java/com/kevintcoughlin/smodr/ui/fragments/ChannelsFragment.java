package com.kevintcoughlin.smodr.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.ui.adapters.RecyclerChannelAdapter;

public class ChannelsFragment extends Fragment {
    public static final String TAG = "ChannelsGridViewFragment";
    private static final int NUM_COLUMNS = 2;
    private Context mAppContext;
    private RecyclerView mRecyclerView;
    private RecyclerChannelAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public interface Callbacks {
        public void onChannelSelected(Channel channel);
    }

    private static Callbacks sChannelCallbacks = new Callbacks() {
        @Override
        public void onChannelSelected(Channel channel) {}
    };

    private Callbacks mCallbacks = sChannelCallbacks;

    public void ChannelsFragment() {}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAppContext = getActivity().getApplicationContext();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.channels_layout, container, false);

        Channel[] channels = {
                new Channel("tellemstevedave", "Tell Em Steve Dave"),
                new Channel("smodcast", "Smodcast")
        };

        mRecyclerView = (RecyclerView) root.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(mAppContext, NUM_COLUMNS);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new RecyclerChannelAdapter(mAppContext, channels);
        mAdapter.setHasStableIds(true);
        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallbacks.onChannelSelected(mAdapter.getItem(position));
            }
        });
        mRecyclerView.setAdapter(mAdapter);

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
        mCallbacks = sChannelCallbacks;
    }
}
