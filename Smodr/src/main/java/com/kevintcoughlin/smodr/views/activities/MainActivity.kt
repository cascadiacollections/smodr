package com.kevintcoughlin.smodr.views.activities

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.cascadiacollections.jamoka.fragment.BinderRecyclerFragment.OnItemSelected
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.firebase.analytics.FirebaseAnalytics
import com.kevintcoughlin.smodr.R
import com.kevintcoughlin.smodr.database.AppDatabase
import com.kevintcoughlin.smodr.databinding.ActivityMainLayoutBinding
import com.kevintcoughlin.smodr.models.Channel
import com.kevintcoughlin.smodr.models.Item
import com.kevintcoughlin.smodr.services.MediaService
import com.kevintcoughlin.smodr.services.MediaService.IPlaybackListener
import com.kevintcoughlin.smodr.services.MediaService.MediaServiceBinder
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment.Companion.create
import com.kevintcoughlin.smodr.views.setElapsedTime

@SuppressLint("NonConstantResourceId")
class MainActivity : AppCompatActivity(), OnItemSelected<Item?>,
    SeekBar.OnSeekBarChangeListener {
    private var mBinding: ActivityMainLayoutBinding? = null
    private var mService: MediaService? = null
    private var mBound = false
    private var mUpdateProgress: Runnable? = null
    private val mHandler = Handler(Looper.getMainLooper()) // Run on main thread.
    private var mBinderRecyclerFragment: EpisodesFragment? = null
    private var mItem: Item? = null

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, MediaService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()

        unbindService(connection)
        mBound = false
    }

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MediaServiceBinder
            mService = binder.service
            mService!!.setPlaybackListener(object : IPlaybackListener {
                override fun onStartPlayback() {
                    mBinding!!.play.setImageDrawable(
                        AppCompatResources.getDrawable(
                            applicationContext, R.drawable.ic_round_pause_24
                        )
                    )
                    mBinding!!.seekbar.max = mService!!.duration
                    mBinding!!.player.visibility = View.VISIBLE
                    mUpdateProgress = Runnable {
                        updateSeekProgress()
                        mHandler.postDelayed(mUpdateProgress!!, ONE_SECOND_IN_MS.toLong())
                    }
                    runOnUiThread(mUpdateProgress)

                    FirebaseAnalytics.getInstance(applicationContext)
                        .logEvent("start_playback", safeGetEventBundle(mItem))
                }

                override fun onStopPlayback() {
                    mBinding!!.play.setImageDrawable(
                        AppCompatResources.getDrawable(
                            applicationContext, R.drawable.ic_round_play_arrow_24
                        )
                    )

                    FirebaseAnalytics.getInstance(applicationContext)
                        .logEvent("stop_playback", safeGetEventBundle(mItem))
                }

                override fun onCompletion() {
                    mBinding!!.seekbar.max = 0
                    mBinding!!.seekbar.progress = 0
                    mBinding!!.play.setImageDrawable(
                        AppCompatResources.getDrawable(
                            applicationContext, R.drawable.ic_round_play_arrow_24
                        )
                    )
                    mItem!!.completed = true
                    AppDatabase.updateData(applicationContext, mItem)
                    mBinderRecyclerFragment!!.markCompleted(mItem)
                    mItem = null

                    FirebaseAnalytics.getInstance(applicationContext)
                        .logEvent("complete_playback", safeGetEventBundle(mItem))
                }
            })
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainLayoutBinding.inflate(
            layoutInflater
        )
        val view: View = mBinding!!.root
        setContentView(view)

        mBinding!!.replay.setOnClickListener { view: View ->
            this.onRewindClick(
                view
            )
        }
        mBinding!!.play.setOnClickListener { view: View ->
            this.onTogglePlaybackClick(
                view
            )
        }
        mBinding!!.forward.setOnClickListener { view: View ->
            this.onForwardClick(
                view
            )
        }
        mBinding!!.seekbar.setOnSeekBarChangeListener(this)

        initializeAds()

        if (savedInstanceState == null) {
            val fm = supportFragmentManager
            val fragment = create(mChannel)

            fm.beginTransaction()
                .add(R.id.coordinator_layout, fragment, EpisodesFragment.TAG)
                .commit()
        }

        MobileAds.initialize(
            this
        ) { }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // @todo - fix compile time error with R import
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        //        final int id = item.getItemId();
//
//        switch (id) {
//            case R.id.feedback:
//                final Intent newIssue = new Intent(Intent.ACTION_VIEW);
//                newIssue.setData(Uri.parse(NEW_ISSUE_URL));
//                startActivity(newIssue);
//                return true;
//            case R.id.privacy_policy:
//                final Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(PRIVACY_POLICY_URL));
//                startActivity(i);
//                return true;
//            case R.id.third_party_notices:
//                startActivity(new Intent(this, OssLicensesMenuActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("deprecation")
    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment is EpisodesFragment) {
            mBinderRecyclerFragment = fragment
            mBinderRecyclerFragment!!.setOnItemSelectedListener(this)
        }
    }

    private fun onTogglePlaybackClick(view: View) {
        if (mBound) {
            if (mService!!.isPlaying) {
                mService!!.pausePlayback()

                FirebaseAnalytics.getInstance(this)
                    .logEvent("pause_playback", safeGetEventBundle(mItem))
            } else {
                mService!!.resumePlayback()

                FirebaseAnalytics.getInstance(this)
                    .logEvent("resume_playback", safeGetEventBundle(mItem))
            }
        }
    }

    private fun onForwardClick(view: View) {
        if (mBound) {
            mService!!.forward()
            this.updateSeekProgress()
        }

        FirebaseAnalytics.getInstance(this).logEvent("forward_playback", safeGetEventBundle(mItem))
    }

    fun onRewindClick(view: View) {
        if (mBound) {
            mService!!.rewind()
            this.updateSeekProgress()
        }

        FirebaseAnalytics.getInstance(this).logEvent("rewind_playback", safeGetEventBundle(mItem))
    }

    private fun updateSeekProgress() {
        if (!mBound) {
            return
        }

        mBinding!!.seekbar.progress = mService!!.currentTime
        mBinding!!.currentTime.setElapsedTime(mService!!.currentTime)
        mBinding!!.remainingTime.setElapsedTime(mService!!.remainingTime)
    }

    private fun initializeAds() {
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
            .build()

        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(this)
    }

    private fun safeGetEventBundle(item: Item?): Bundle {
        if (item == null) {
            return Bundle()
        }

        return item.eventBundle()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        val bundle = safeGetEventBundle(mItem)
        bundle.putInt("progress", progress)
        bundle.putBoolean("fromUser", fromUser)

        FirebaseAnalytics.getInstance(this).logEvent("seek_playback", bundle)

        if (fromUser && mService != null) {
            mService!!.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
    }

    companion object {
        private const val ONE_SECOND_IN_MS = 1000
        private val mChannel = Channel(
            "Tell 'Em Steve-Dave",
            "https://feeds.feedburner.com/TellEmSteveDave"
        )
        private const val PRIVACY_POLICY_URL =
            "https://kevintcoughlin.blob.core.windows.net/smodr/privacy_policy.html"
        private const val NEW_ISSUE_URL = "https://github.com/cascadiacollections/SModr/issues/new"
    }

    override fun onItemSelected(item: Item?) {
        if (!mBound) {
            return
        }

        mItem = item
        mService!!.startPlayback(item?.uri)

        FirebaseAnalytics.getInstance(this).logEvent("selected_item", safeGetEventBundle(item))
    }
}
