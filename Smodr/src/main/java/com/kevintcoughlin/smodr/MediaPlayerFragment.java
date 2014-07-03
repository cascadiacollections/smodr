package com.kevintcoughlin.smodr;

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kevintcoughlin.smodr.util.PlaybackController;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import timber.log.Timber;

public class MediaPlayerFragment extends Fragment {
    private static final String TAG = "MediaPlayerFragment";
    private EpisodePlayer mEpisodePlayer;
    private Context mContext;
    private PlaybackController mController;

    @InjectView(R.id.title) TextView mTitleView;
    @InjectView(R.id.play_button) ImageButton mPlayBtnImage;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @DebugLog
    @OnClick(R.id.play_button)
    public void togglePlayback() {
        if (mEpisodePlayer.isPlaying()) {
            mEpisodePlayer.pause();
            Timber.i("Playback paused.");
        } else {
            Timber.i("Playback started.");
            mEpisodePlayer.start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mediaplayer, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mController = setupPlaybackController();
        mPlayBtnImage.setOnClickListener(mController.newOnPlayButtonClickListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        mController.init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment is about to be destroyed");
        if (mController != null) {
            mController.release();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mController != null) {
            mController.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @DebugLog
    public void updateMediaButton(EpisodePlayer.State state) {
        if (state == EpisodePlayer.State.STARTED) {
            mPlayBtnImage.setImageResource(R.drawable.btn_pause);
        }
        else {
            mPlayBtnImage.setImageResource(R.drawable.btn_play);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @DebugLog
    private void setTitle(String title) {
        mTitleView.setText(title);
    }

    private boolean loadMediaInfo() {
        Log.d(TAG, "Loading media info");
        return true;
    }

    /**
     * Handle Media Playback
     * @return PlaybackController
     */
    private PlaybackController setupPlaybackController() {
        return new PlaybackController(getActivity(), true) {

            @Override
            public void setupGUI() {

            }

            @Override
            public void onPositionObserverUpdate() {

            }

            @Override
            public void onReloadNotification(int code) {

            }

            @Override
            public void onBufferStart() {

            }

            @Override
            public void onBufferEnd() {

            }

            @Override
            public void onBufferUpdate(float progress) {

            }

            @Override
            public void handleError(int code) {

            }

            @Override
            public ImageButton getPlayButton() {
                return mPlayBtnImage;
            }

            @Override
            public void postStatusMsg(int msg) {
                //txtvStatus.setText(msg);
            }

            @Override
            public void clearStatusMsg() {
                //txtvStatus.setText("");
            }

            @Override
            public boolean loadMediaInfo() {
                return false;
            }

            @Override
            public void onServiceQueried() {
            }

            @Override
            public void onShutdownNotification() {
                //if (fragmentLayout != null) {
                //    fragmentLayout.setVisibility(View.GONE);
                //}
                mController = setupPlaybackController();
                if (mPlayBtnImage != null) {
                    mPlayBtnImage.setOnClickListener(mController.newOnPlayButtonClickListener());
                }

            }

            @Override
            public void onPlaybackEnd() {
                //if (fragmentLayout != null) {
                //    fragmentLayout.setVisibility(View.GONE);
                //}
                mController = setupPlaybackController();
                if (mPlayBtnImage != null) {
                    mPlayBtnImage.setOnClickListener(mController.newOnPlayButtonClickListener());
                }
            }

        };
    }


}
