package com.kevintcoughlin.smodr.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import kotlin.math.min

/**
 * Interface defining media service operations.
 */
interface IMediaService {
    fun seekTo(milliseconds: Int)
    val isPlaying: Boolean
    val duration: Int
    val currentTime: Int
    val remainingTime: Int
    fun resumePlayback()
    fun pausePlayback()
    fun stopPlayback()
    fun forward()
    fun rewind()
    fun setPlaybackListener(listener: MediaService.IPlaybackListener?)
}

/**
 * Service for managing media playback with MediaPlayer.
 * Implements playback controls, seeking, and listener notifications.
 */
class MediaService : Service(), MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, IMediaService {

    /**
     * Interface for playback lifecycle callbacks.
     */
    interface IPlaybackListener {
        fun onStartPlayback()
        fun onStopPlayback()
        fun onCompletion()
    }

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer().apply {
            setOnCompletionListener(this@MediaService)
            setOnErrorListener(this@MediaService)
            setOnPreparedListener(this@MediaService)
        }
    }
    private val binder = MediaServiceBinder()
    private var playbackListener: IPlaybackListener? = null

    inner class MediaServiceBinder : Binder() {
        val service: MediaService
            get() = this@MediaService
    }

    override val currentTime: Int
        get() = mediaPlayer.currentPosition

    override val remainingTime: Int
        get() = (duration - currentTime).coerceAtLeast(0)

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val url = intent.getStringExtra(INTENT_EPISODE_URL)?.toUri()
        handleAction(intent.action, url)
        return START_REDELIVER_INTENT
    }

    private fun handleAction(action: String?, url: Uri?) {
        when (action) {
            ACTION_PAUSE -> pausePlayback()
            ACTION_PLAY -> startPlayback(url)
            ACTION_RESUME -> resumePlayback()
            ACTION_STOP -> stopPlayback()
            ACTION_FORWARD -> forward()
            ACTION_REWIND -> rewind()
        }
    }

    override fun onDestroy() {
        stopPlayback()
        mediaPlayer.release()
        playbackListener = null
        super.onDestroy()
    }

    // IMediaService Implementation

    override val isPlaying: Boolean
        get() = mediaPlayer.isPlaying

    override val duration: Int
        get() = mediaPlayer.duration

    override fun seekTo(milliseconds: Int) {
        mediaPlayer.seekTo(milliseconds)
    }

    override fun resumePlayback() {
        mediaPlayer.start()
        notifyListener { onStartPlayback() }
    }

    override fun pausePlayback() {
        mediaPlayer.pause()
        notifyListener { onStopPlayback() }
    }

    override fun stopPlayback() {
        mediaPlayer.stop()
        notifyListener { onStopPlayback() }
    }

    override fun forward() {
        mediaPlayer.run {
            val newPosition = min(currentPosition + SEEK_INTERVAL, duration)
            seekTo(newPosition)
        }
    }

    override fun rewind() {
        mediaPlayer.run {
            val newPosition = (currentPosition - SEEK_INTERVAL).coerceAtLeast(0)
            seekTo(newPosition)
        }
    }

    override fun setPlaybackListener(listener: IPlaybackListener?) {
        playbackListener = listener
    }

    override fun onCompletion(mp: MediaPlayer) {
        notifyListener { onCompletion() }
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        stopPlayback()
        return true
    }

    override fun onPrepared(mp: MediaPlayer) {
        resumePlayback()
    }

    /**
     * Starts playback of media from the given URL.
     *
     * @param url The URI of the media to play
     */
    fun startPlayback(url: Uri?) {
        url ?: return
        try {
            if (mediaPlayer.isPlaying) stopPlayback()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(this, url)
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting playback: ${e.message}", e)
        }
    }

    private inline fun notifyListener(action: IPlaybackListener.() -> Unit) {
        playbackListener?.action()
    }

    companion object {
        const val SEEK_INTERVAL = 30_000 // 30 seconds in milliseconds
        const val INTENT_EPISODE_URL = "intent_episode_url"
        const val ACTION_PLAY = "com.kevintcoughlin.smodr.app.PLAY"
        const val ACTION_PAUSE = "com.kevintcoughlin.smodr.app.PAUSE"
        const val ACTION_RESUME = "com.kevintcoughlin.smodr.app.RESUME"
        const val ACTION_STOP = "com.kevintcoughlin.smodr.app.STOP"
        const val ACTION_FORWARD = "com.kevintcoughlin.smodr.app.FORWARD"
        const val ACTION_REWIND = "com.kevintcoughlin.smodr.app.REWIND"
        private const val TAG = "MediaService"
    }
}
