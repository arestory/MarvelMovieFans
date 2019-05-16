package arestory.com.marvelmoviefans.util

import android.media.MediaPlayer

import java.util.Timer
import java.util.TimerTask

class ExMediaPlayer : MediaPlayer() {

    private var timer: Timer? = null
    private var progressListener: ProgressListener? = null


    fun setProgressListener(progressListener: ProgressListener) {


        setProgressListener(1000, progressListener)
    }

    fun setProgressListener(period: Long, progressListener: ProgressListener) {

        timer = Timer()

        timer!!.schedule(object : TimerTask() {
            override fun run() {


                val currentPosition = this@ExMediaPlayer.currentPosition
                val duration = this@ExMediaPlayer.duration
                val progress = (currentPosition * 100.0f / duration).toInt()

                progressListener.onProgress(progress, currentPosition, duration)


            }
        }, 0, period)
    }





    fun releaseTimeTask() {

        release()
        timer!!.cancel()
        timer = null
    }

    interface ProgressListener {


        fun onProgress(progress: Int, currentPosition: Int, duration: Int)
    }
}