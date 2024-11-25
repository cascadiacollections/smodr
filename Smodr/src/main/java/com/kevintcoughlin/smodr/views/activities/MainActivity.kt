package com.kevintcoughlin.smodr.views.activities

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.kevintcoughlin.smodr.R
import com.kevintcoughlin.smodr.databinding.ActivityMainLayoutBinding
import com.kevintcoughlin.smodr.models.Channel
import com.kevintcoughlin.smodr.models.Item
import com.kevintcoughlin.smodr.services.MediaService
import com.kevintcoughlin.smodr.services.MediaService.IPlaybackListener
import com.kevintcoughlin.smodr.views.fragments.EpisodesFragment
import com.kevintcoughlin.smodr.views.setElapsedTime

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: ActivityMainLayoutBinding
    private var mediaService: MediaService? = null
    private var isServiceBound = false
    private val handler = Handler(Looper.getMainLooper())
    private var currentItem: Item? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MediaService.MediaServiceBinder
            mediaService = binder.service
            mediaService?.setPlaybackListener(createPlaybackListener())
            isServiceBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            isServiceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        initializeAds()

        if (savedInstanceState == null) {
            val fragment = EpisodesFragment.create(DEFAULT_CHANNEL)
            supportFragmentManager.beginTransaction()
                .add(R.id.coordinator_layout, fragment, EpisodesFragment.TAG)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, MediaService::class.java), connection, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isServiceBound) {
            unbindService(connection)
            isServiceBound = false
        }
    }

    private fun setupUI() {
        binding.apply {
            replay.setOnClickListener { onRewindClick() }
            play.setOnClickListener { onTogglePlaybackClick() }
            forward.setOnClickListener { onForwardClick() }
            seekbar.setOnSeekBarChangeListener(this@MainActivity)
        }
    }

    private fun initializeAds() {
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
            .build()
        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(this)
    }

    private fun createPlaybackListener() = object : IPlaybackListener {
        override fun onStartPlayback() {
            updatePlayButtonState(R.drawable.ic_round_pause_24)
            binding.seekbar.max = mediaService?.duration ?: 0
            binding.player.visibility = View.VISIBLE
            startProgressUpdater()
            logEvent("start_playback", safeGetEventBundle(currentItem))
        }

        override fun onStopPlayback() {
            updatePlayButtonState(R.drawable.ic_round_play_arrow_24)
            logEvent("stop_playback", safeGetEventBundle(currentItem))
        }

        override fun onCompletion() {
            binding.seekbar.progress = 0
            updatePlayButtonState(R.drawable.ic_round_play_arrow_24)
            currentItem?.completed = true
            currentItem = null
            logEvent("complete_playback", safeGetEventBundle(currentItem))
        }
    }

    private fun updatePlayButtonState(drawableRes: Int) {
        binding.play.setImageDrawable(AppCompatResources.getDrawable(applicationContext, drawableRes))
    }

    private fun startProgressUpdater() {
        val updateProgress = object : Runnable {
            override fun run() {
                updateSeekProgress()
                handler.postDelayed(this, ONE_SECOND_MS.toLong())
            }
        }
        handler.post(updateProgress)
    }

    private fun updateSeekProgress() {
        mediaService?.let { service ->
            binding.seekbar.progress = service.currentTime
            binding.currentTime.setElapsedTime(service.currentTime)
            binding.remainingTime.setElapsedTime(service.remainingTime)
        }
    }

    private fun onTogglePlaybackClick() {
        mediaService?.let { service ->
            if (service.isPlaying) {
                service.pausePlayback()
                logEvent("pause_playback", safeGetEventBundle(currentItem))
            } else {
                service.resumePlayback()
                logEvent("resume_playback", safeGetEventBundle(currentItem))
            }
        }
    }

    private fun onForwardClick() {
        mediaService?.forward()
        updateSeekProgress()
        logEvent("forward_playback", safeGetEventBundle(currentItem))
    }

    private fun onRewindClick() {
        mediaService?.rewind()
        updateSeekProgress()
        logEvent("rewind_playback", safeGetEventBundle(currentItem))
    }

    private fun safeGetEventBundle(item: Item?): Bundle = item?.toEventBundle() ?: Bundle()

    private fun logEvent(event: String, bundle: Bundle) {
        FirebaseAnalytics.getInstance(this).logEvent(event, bundle)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) mediaService?.seekTo(progress)
        logEvent("seek_playback", Bundle().apply {
            putInt("progress", progress)
            putBoolean("fromUser", fromUser)
        })
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.feedback -> openUrl(FEEDBACK_URL)
            R.id.privacy_policy -> openUrl(PRIVACY_POLICY_URL)
            R.id.third_party_notices -> startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)))
    }

    companion object {
        private const val ONE_SECOND_MS = 1000
        private const val PRIVACY_POLICY_URL = "https://kevintcoughlin.blob.core.windows.net/smodr/privacy_policy.html"
        private const val FEEDBACK_URL = "https://github.com/cascadiacollections/SModr/issues/new"
        private val DEFAULT_CHANNEL = Channel("Tell 'Em Steve-Dave", "http://feeds.feedburner.com/TellEmSteveDave/")
    }

    fun onItemSelected(item: Item?) {
        currentItem = item
        mediaService?.startPlayback(item?.uri)
        logEvent("selected_item", safeGetEventBundle(item))
    }
}