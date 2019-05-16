package arestory.com.marvelmoviefans.datasource

import android.content.Context

object SettingDataSource {

    fun toggleAdv(context: Context, open:Boolean){

        val sp =  context.getSharedPreferences("movie", Context.MODE_PRIVATE).edit()
        sp.putBoolean("openAdv",open)
        sp.apply()

    }

    fun isAdvOpen(context: Context):Boolean{


        return  context.getSharedPreferences("movie", Context.MODE_PRIVATE).getBoolean("openAdv",true)
    }

    fun toggleSound(context: Context, open:Boolean){
        val sp =  context.getSharedPreferences("movie", Context.MODE_PRIVATE).edit()
        sp.putBoolean("openSound",open)
        sp.apply()
    }

    fun isSoundOpen(context: Context):Boolean{


        return  context.getSharedPreferences("movie", Context.MODE_PRIVATE).getBoolean("openSound",true)
    }


}