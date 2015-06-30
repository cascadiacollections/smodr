package com.kevintcoughlin.smodr.views.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.kevintcoughlin.smodr.R;
import com.kevintcoughlin.smodr.adapters.ChannelsAdapter;
import com.kevintcoughlin.smodr.http.SmodcastClient;
import com.kevintcoughlin.smodr.models.Channel;
import com.kevintcoughlin.smodr.models.Rss;
import com.kevintcoughlin.smodr.utils.AppUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public final class ChannelsFragment extends TrackedFragment {
	public static final String TAG = ChannelsFragment.class.getSimpleName();
    private Callbacks mCallbacks = sChannelCallbacks;
    private ChannelsAdapter mAdapter = new ChannelsAdapter();
	@Bind(R.id.list) RecyclerView mRecyclerView;

	public interface Callbacks {
        void onChannelSelected(Channel channel);
    }

    private static Callbacks sChannelCallbacks = new Callbacks() {
        @Override
        public void onChannelSelected(Channel channel) {
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sChannelCallbacks;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		for (final String channel : AppUtil.getStrings(getActivity(), R.array.podcasts)) {
			SmodcastClient.getClient().getFeed(channel, new Callback<Rss>() {
				@Override
				public void success(Rss rss, Response response) {
                    mAdapter.addChannel(rss.getChannel());
                    final Channel channel = rss.getChannel();
				}

				@Override
				public void failure(RetrofitError error) {
                    Timber.e(error, error.getMessage());
				}
			});
		}
	}

	@Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.channels_grid_layout, container, false);
	    ButterKnife.bind(this, view);

		final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
	    mRecyclerView.setLayoutManager(layoutManager);
	    mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}
