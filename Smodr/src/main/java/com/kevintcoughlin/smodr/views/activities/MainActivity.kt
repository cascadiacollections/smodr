package com.kevintcoughlin.smodr.views.activities

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.kevintcoughlin.smodr.R
import com.kevintcoughlin.smodr.databinding.ActivityMainLayoutBinding
import com.kevintcoughlin.smodr.models.Item
import com.kevintcoughlin.smodr.services.MediaService
import com.kevintcoughlin.smodr.services.MediaService.IPlaybackListener
import com.kevintcoughlin.smodr.views.setElapsedTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Main activity for the Smodr podcast player.
 *
 * This activity manages:
 * - Media service binding and lifecycle
 * - Playback UI controls and progress updates
 * - Firebase Analytics event logging
 * - Seekbar interactions
 *
 * Uses modern Android patterns:
 * - ViewBinding for type-safe view access
 * - Lifecycle-aware coroutines for periodic updates
 * - Kotlin property delegation for lazy initialization
 */
class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    // View Binding
    private lateinit var binding: ActivityMainLayoutBinding

    // Media Service
    private var mediaService: MediaService? = null
    private var isServiceBound = false

    // Playback State
    private var currentItem: Item? = null
    private var progressUpdateJob: Job? = null

    // Analytics
    private val analytics: FirebaseAnalytics by lazy { FirebaseAnalytics.getInstance(this) }

    // Service Connection
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MediaService.MediaServiceBinder
            mediaService = binder.service.also { mediaService ->
                mediaService.setPlaybackListener(playbackListener)
                isServiceBound = true
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mediaService = null
            isServiceBound = false
        }
    }

    // Playback Listener
    private val playbackListener = object : IPlaybackListener {
        override fun onStartPlayback() {
            updatePlaybackState(PlaybackState.Playing)
            logAnalyticsEvent(AnalyticsEvent.StartPlayback(currentItem))
        }

        override fun onStopPlayback() {
            updatePlaybackState(PlaybackState.Paused)
            logAnalyticsEvent(AnalyticsEvent.StopPlayback(currentItem))
        }

        override fun onCompletion() {
            updatePlaybackState(PlaybackState.Completed)
            currentItem = currentItem?.copy(completed = true)
            logAnalyticsEvent(AnalyticsEvent.CompletePlayback(currentItem))
            currentItem = null
        }
    }

    // Lifecycle Methods

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    override fun onStart() {
        super.onStart()
        bindMediaService()
    }

    override fun onStop() {
        super.onStop()
        unbindMediaService()
        stopProgressUpdates()
    }

    // UI Setup

    private fun setupUI() {
        binding.apply {
            replay.setOnClickListener { handleRewindClick() }
            play.setOnClickListener { handleTogglePlaybackClick() }
            forward.setOnClickListener { handleForwardClick() }
            seekbar.setOnSeekBarChangeListener(this@MainActivity)
        }
    }

    // Media Service Management

    private fun bindMediaService() {
        Intent(this, MediaService::class.java).also { intent ->
            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    private fun unbindMediaService() {
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
            mediaService = null
        }
    }

    // Playback State Management

    /**
     * Sealed class representing possible playback states.
     */
    private sealed class PlaybackState {
        object Playing : PlaybackState()
        object Paused : PlaybackState()
        object Completed : PlaybackState()
    }

    private fun updatePlaybackState(state: PlaybackState) {
        when (state) {
            is PlaybackState.Playing -> {
                updatePlayButton(isPlaying = true)
                mediaService?.duration?.let { duration ->
                    binding.seekbar.max = duration
                }
                binding.player.isVisible = true
                startProgressUpdates()
            }

            is PlaybackState.Paused -> {
                updatePlayButton(isPlaying = false)
                stopProgressUpdates()
            }

            is PlaybackState.Completed -> {
                updatePlayButton(isPlaying = false)
                binding.seekbar.progress = 0
                stopProgressUpdates()
            }
        }
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        val drawableRes = if (isPlaying) {
            R.drawable.ic_round_pause_24
        } else {
            R.drawable.ic_round_play_arrow_24
        }
        binding.play.setImageDrawable(
            AppCompatResources.getDrawable(applicationContext, drawableRes),
        )
    }

    // Progress Updates (Coroutine-based)

    private fun startProgressUpdates() {
        stopProgressUpdates()
        progressUpdateJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (isActive) {
                    updateSeekProgress()
                    delay(PROGRESS_UPDATE_INTERVAL_MS)
                }
            }
        }
    }

    private fun stopProgressUpdates() {
        progressUpdateJob?.cancel()
        progressUpdateJob = null
    }

    private fun updateSeekProgress() {
        mediaService?.run {
            binding.apply {
                seekbar.progress = currentTime
                current_time.setElapsedTime(this@run.currentTime.toLong())
                remaining_time.setElapsedTime(this@run.remainingTime.toLong())
            }
        }
    }

    // Playback Control Handlers

    private fun handleTogglePlaybackClick() {
        mediaService?.let { service ->
            if (service.isPlaying) {
                service.pausePlayback()
                logAnalyticsEvent(AnalyticsEvent.PausePlayback(currentItem))
            } else {
                service.resumePlayback()
                logAnalyticsEvent(AnalyticsEvent.ResumePlayback(currentItem))
            }
        }
    }

    private fun handleForwardClick() {
        mediaService?.forward()
        updateSeekProgress()
        logAnalyticsEvent(AnalyticsEvent.ForwardPlayback(currentItem))
    }

    private fun handleRewindClick() {
        mediaService?.rewind()
        updateSeekProgress()
        logAnalyticsEvent(AnalyticsEvent.RewindPlayback(currentItem))
    }

    // SeekBar.OnSeekBarChangeListener Implementation

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            mediaService?.seekTo(progress)
            logAnalyticsEvent(AnalyticsEvent.SeekPlayback(progress, fromUser))
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        // No-op: Could pause auto-updates if needed
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        // No-op: Could resume auto-updates if needed
    }

    // Options Menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.feedback -> openUrl(FEEDBACK_URL)
            R.id.privacy_policy -> openUrl(PRIVACY_POLICY_URL)
            R.id.third_party_notices -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openUrl(url: String) {
        Intent(Intent.ACTION_VIEW, url.toUri()).also { intent ->
            startActivity(intent)
        }
    }

    // Analytics

    /**
     * Sealed class for type-safe analytics events.
     */
    private sealed class AnalyticsEvent(val eventName: String, val bundle: Bundle) {
        class StartPlayback(item: Item?) : AnalyticsEvent("start_playback", item.toBundle())
        class StopPlayback(item: Item?) : AnalyticsEvent("stop_playback", item.toBundle())
        class CompletePlayback(item: Item?) : AnalyticsEvent("complete_playback", item.toBundle())
        class PausePlayback(item: Item?) : AnalyticsEvent("pause_playback", item.toBundle())
        class ResumePlayback(item: Item?) : AnalyticsEvent("resume_playback", item.toBundle())
        class ForwardPlayback(item: Item?) : AnalyticsEvent("forward_playback", item.toBundle())
        class RewindPlayback(item: Item?) : AnalyticsEvent("rewind_playback", item.toBundle())
        class SeekPlayback(progress: Int, fromUser: Boolean) : AnalyticsEvent(
            "seek_playback",
            Bundle().apply {
                putInt("progress", progress)
                putBoolean("fromUser", fromUser)
            },
        )

        companion object {
            private fun Item?.toBundle(): Bundle = this?.toEventBundle() ?: Bundle()
        }
    }

    private fun logAnalyticsEvent(event: AnalyticsEvent) {
        analytics.logEvent(event.eventName, event.bundle)
    }

    // Companion Object

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL_MS = 1000L
        private const val PRIVACY_POLICY_URL = "https://kevintcoughlin.blob.core.windows.net/smodr/privacy_policy.html"
        private const val FEEDBACK_URL = "https://github.com/cascadiacollections/SModr/issues/new"
    }
}
