package arestory.com.marvelmoviefans.datasource

import android.annotation.SuppressLint
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object SettingDataSource {

    private const val MOVIE ="movie"
    private const val OPEN_ADV ="openAdv"
    private const val OPEN_SOUND ="openSound"
    private const val TIPS_COUNT ="tipsCount"
    private const val DEFAULT_TIPS_COUNT =3

    fun toggleAdv(context: Context, open:Boolean){

        val sp =  context.getSharedPreferences(MOVIE, Context.MODE_PRIVATE).edit()
        sp.putBoolean(OPEN_ADV,open)
        sp.apply()

    }

    fun isAdvOpen(context: Context):Boolean{


        return  context.getSharedPreferences(MOVIE, Context.MODE_PRIVATE).getBoolean(OPEN_ADV,true)
    }

    fun toggleSound(context: Context, open:Boolean){
        val sp =  context.getSharedPreferences(MOVIE, Context.MODE_PRIVATE).edit()
        sp.putBoolean(OPEN_SOUND,open)
        sp.apply()
    }


    fun getTodayTipsCount(context: Context):Int{


        val time =getCurrentDate()
        val key = "$TIPS_COUNT$time"
        return context.getSharedPreferences(MOVIE, Context.MODE_PRIVATE).getInt(key,DEFAULT_TIPS_COUNT)
    }

    fun useTodayTips(context: Context):Boolean{

       var count =  getTodayTipsCount(context)

        if(count>0){
            count--
            val editor =  context.getSharedPreferences(MOVIE, Context.MODE_PRIVATE).edit()
            val time =getCurrentDate()
            val key = "$TIPS_COUNT$time"
            editor.putInt(key,count)
            editor.apply()
            return true
        }
        return false
    }
   private fun getCurrentDate():String = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())

    fun addTodayTipsCount(context: Context,plusCount:Int){

        val time =getCurrentDate()
        val key = "$TIPS_COUNT$time"
        val count =  getTodayTipsCount(context)
        val editor =  context.getSharedPreferences(MOVIE, Context.MODE_PRIVATE).edit()
        editor.putInt(key,count+plusCount)
        editor.apply()
    }



    fun isSoundOpen(context: Context):Boolean{


        return  context.getSharedPreferences(MOVIE, Context.MODE_PRIVATE).getBoolean(OPEN_SOUND,true)
    }


}