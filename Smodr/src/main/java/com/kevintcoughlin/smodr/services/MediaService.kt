package com.kevintcoughlin.smodr.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import com.kevintcoughlin.smodr.services.MediaService.IPlaybackListener
import org.jetbrains.annotations.Contract
import kotlin.math.min

internal interface IMediaService {
    fun seekTo(milliseconds: Int)
    val isPlaying: Boolean
    fun resumePlayback()
    fun pausePlayback()
    fun stopPlayback()
    fun forward()
    fun rewind()
    fun setPlaybackListener(listener: IPlaybackListener?)
}

class MediaService : Service(), MediaPlayer.OnErrorListener, OnPreparedListener, IMediaService, OnCompletionListener {
    override fun onCompletion(mp: MediaPlayer) {
        if (mListener != null) {
            mListener!!.onCompletion()
        }
    }

    interface IPlaybackListener {
        fun onStartPlayback()
        fun onStopPlayback()
        fun onCompletion()
    }

    private var mMediaPlayer: MediaPlayer? = null
    private val mBinder: IBinder = MediaServiceBinder()
    private var mListener: IPlaybackListener? = null

    inner class MediaServiceBinder : Binder() {
        val service: MediaService
            get() = this@MediaService
    }

    @Contract(pure = true)
    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val url = Uri.parse(intent.getStringExtra(INTENT_EPISODE_URL))
        val action = intent.action
        if (action != null) when (action) {
            ACTION_PAUSE -> pausePlayback()
            ACTION_PLAY -> startPlayback(url)
            ACTION_RESUME -> resumePlayback()
            ACTION_STOP -> stopPlayback()
            ACTION_FORWARD -> rewind()
            ACTION_REWIND -> forward()
        }
        return START_REDELIVER_INTENT
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        stopPlayback()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
        }
        mListener = null
    }

    override fun seekTo(milliseconds: Int) {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.seekTo(milliseconds)
        }
    }

    override val isPlaying: Boolean
        get() = mMediaPlayer != null && mMediaPlayer!!.isPlaying

    val duration: Int
        get() = if (mMediaPlayer != null) mMediaPlayer!!.duration else -1

    val currentTime: Int
        get() = if (mMediaPlayer != null) mMediaPlayer!!.currentPosition else -1

    val remainingTime: Int
        get() = duration - currentTime

    override fun resumePlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.start()
            if (mListener != null) {
                mListener!!.onStartPlayback()
            }
        }
    }

    override fun pausePlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.pause()
            if (mListener != null) {
                mListener!!.onStopPlayback()
            }
        }
    }

    override fun stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            if (mListener != null) {
                mListener!!.onStopPlayback()
            }
        }
    }

    override fun forward() {
        if (mMediaPlayer != null) {
            val position = mMediaPlayer!!.currentPosition + THIRTY_SECONDS_IN_MILLISECONDS
            seekTo(min(position, mMediaPlayer!!.duration))
        }
    }

    override fun rewind() {
        if (mMediaPlayer != null) {
            seekTo(mMediaPlayer!!.currentPosition - THIRTY_SECONDS_IN_MILLISECONDS)
        }
    }

    override fun setPlaybackListener(listener: IPlaybackListener?) {
        mListener = listener
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        resumePlayback()
    }

    fun startPlayback(url: Uri?) {
        if (mMediaPlayer != null && mMediaPlayer!!.isPlaying) {
            stopPlayback()
        }
        try {
            mMediaPlayer = MediaPlayer.create(this, url)
            mMediaPlayer!!.setOnCompletionListener(this)
            mMediaPlayer!!.start()
            if (mListener != null) {
                mListener!!.onStartPlayback()
            }
        } catch (exception: NullPointerException) {
            Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val THIRTY_SECONDS_IN_MILLISECONDS = 30000
        const val INTENT_EPISODE_URL = "intent_episode_url"
        const val ACTION_PLAY = "com.com.kevintcoughlin.smodr.app.PLAY"
        const val ACTION_PAUSE = "com.com.kevintcoughlin.smodr.app.PAUSE"
        const val ACTION_RESUME = "com.com.kevintcoughlin.smodr.app.RESUME"
        const val ACTION_STOP = "com.com.kevintcoughlin.smodr.app.STOP"
        const val ACTION_FORWARD = "com.com.kevintcoughlin.smodr.app.FORWARD"
        const val ACTION_REWIND = "com.com.kevintcoughlin.smodr.app.REWIND"
    }
}