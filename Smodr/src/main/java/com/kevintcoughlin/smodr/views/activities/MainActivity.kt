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
            logAnalyticsEvent(AnalyticsEvent.PlaybackEvent.Start(currentItem))
        }

        override fun onStopPlayback() {
            updatePlaybackState(PlaybackState.Paused)
            logAnalyticsEvent(AnalyticsEvent.PlaybackEvent.Stop(currentItem))
        }

        override fun onCompletion() {
            updatePlaybackState(PlaybackState.Completed)
            currentItem = currentItem?.copy(completed = true)
            logAnalyticsEvent(AnalyticsEvent.PlaybackEvent.Complete(currentItem))
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
        mediaService?.let { service ->
            binding.seekbar.progress = service.currentTime
            binding.currentTime.setElapsedTime(service.currentTime.toLong())
            binding.remainingTime.setElapsedTime(service.remainingTime.toLong())
        }
    }

    // Playback Control Handlers

    private fun handleTogglePlaybackClick() {
        mediaService?.let { service ->
            if (service.isPlaying) {
                service.pausePlayback()
                logAnalyticsEvent(AnalyticsEvent.PlaybackEvent.Pause(currentItem))
            } else {
                service.resumePlayback()
                logAnalyticsEvent(AnalyticsEvent.PlaybackEvent.Resume(currentItem))
            }
        }
    }

    private fun handleForwardClick() {
        mediaService?.forward()
        updateSeekProgress()
        logAnalyticsEvent(AnalyticsEvent.PlaybackEvent.Forward(currentItem))
    }

    private fun handleRewindClick() {
        mediaService?.rewind()
        updateSeekProgress()
        logAnalyticsEvent(AnalyticsEvent.PlaybackEvent.Rewind(currentItem))
    }

    // SeekBar.OnSeekBarChangeListener Implementation

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            mediaService?.seekTo(progress)
            logAnalyticsEvent(AnalyticsEvent.Seek(progress, fromUser))
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
     * Sealed interface representing type-safe analytics events.
     *
     * Using sealed interface instead of sealed class provides:
     * - Better flexibility for implementation
     * - Cleaner separation between data and behavior
     * - Ability to implement multiple interfaces if needed
     */
    private sealed interface AnalyticsEvent {
        val eventName: String
        val bundle: Bundle

        /**
         * Playback lifecycle events that include item metadata.
         */
        sealed class PlaybackEvent(item: Item?, override val eventName: String) : AnalyticsEvent {
            override val bundle: Bundle = item?.toEventBundle() ?: Bundle()

            data class Start(val item: Item?) : PlaybackEvent(item, EVENT_START_PLAYBACK)
            data class Stop(val item: Item?) : PlaybackEvent(item, EVENT_STOP_PLAYBACK)
            data class Complete(val item: Item?) : PlaybackEvent(item, EVENT_COMPLETE_PLAYBACK)
            data class Pause(val item: Item?) : PlaybackEvent(item, EVENT_PAUSE_PLAYBACK)
            data class Resume(val item: Item?) : PlaybackEvent(item, EVENT_RESUME_PLAYBACK)
            data class Forward(val item: Item?) : PlaybackEvent(item, EVENT_FORWARD_PLAYBACK)
            data class Rewind(val item: Item?) : PlaybackEvent(item, EVENT_REWIND_PLAYBACK)
        }

        /**
         * Seek event with progress information.
         *
         * @property progress Current seekbar position
         * @property fromUser Whether the seek was user-initiated
         */
        data class Seek(val progress: Int, val fromUser: Boolean) : AnalyticsEvent {
            override val eventName: String = EVENT_SEEK_PLAYBACK
            override val bundle: Bundle = Bundle().apply {
                putInt(PARAM_PROGRESS, progress)
                putBoolean(PARAM_FROM_USER, fromUser)
            }
        }

        companion object {
            // Event names
            private const val EVENT_START_PLAYBACK = "start_playback"
            private const val EVENT_STOP_PLAYBACK = "stop_playback"
            private const val EVENT_COMPLETE_PLAYBACK = "complete_playback"
            private const val EVENT_PAUSE_PLAYBACK = "pause_playback"
            private const val EVENT_RESUME_PLAYBACK = "resume_playback"
            private const val EVENT_FORWARD_PLAYBACK = "forward_playback"
            private const val EVENT_REWIND_PLAYBACK = "rewind_playback"
            private const val EVENT_SEEK_PLAYBACK = "seek_playback"

            // Parameter names
            private const val PARAM_PROGRESS = "progress"
            private const val PARAM_FROM_USER = "from_user"
        }
    }

    /**
     * Logs an analytics event to Firebase.
     *
     * @param event The type-safe analytics event to log
     */
    private fun logAnalyticsEvent(event: AnalyticsEvent) {
        analytics.logEvent(event.eventName, event.bundle)
    }

    // Companion Object

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL_MS = 1000L
        private const val PRIVACY_POLICY_URL = "https://cascadiacollections.github.io/smodr/privacy_policy.html"
        private const val FEEDBACK_URL = "https://github.com/cascadiacollections/SModr/issues/new"
    }
}
