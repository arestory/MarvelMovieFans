package arestory.com.marvelmoviefans.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.support.annotation.RequiresApi
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.datasource.SettingDataSource

object ShortSoundPlayer {


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun playSound(context: Context, soundRes : Int){

        if(SettingDataSource.isSoundOpen(context)){

            //播放音效
            val audioAttributes = AudioAttributes.Builder()
            audioAttributes.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            audioAttributes.setUsage(AudioAttributes.USAGE_GAME)
            audioAttributes.setLegacyStreamType(AudioManager.STREAM_MUSIC)
            val soundPoolBuilder = SoundPool.Builder()
            soundPoolBuilder.setAudioAttributes(audioAttributes.build())
            val soundPool =soundPoolBuilder.build()
            soundPool.load(context,soundRes,1)

            soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->

                soundPool.play(sampleId,1f,1f,0,0,1f)

            }
        }


    }


}