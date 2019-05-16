package arestory.com.marvelmoviefans.util

import android.content.Context
import android.widget.Toast

object ToastUtil {

    fun showToast(context: Context,msg:String?){

        ToastUtil.showToast(context,msg,Toast.LENGTH_SHORT)
    }

    fun showToast(context: Context,msg:String?,duration:Int){


        Toast.makeText(context,msg,duration).show()
    }

    fun showLongToast(context: Context,msg:String?){
        ToastUtil.showToast(context,msg,Toast.LENGTH_LONG)
    }
    fun showShortToast(context: Context,msg:String?){
        ToastUtil.showToast(context,msg,Toast.LENGTH_SHORT)
    }

}