package arestory.com.marvelmoviefans.common

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.view.View
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.activities.FeedbackActivity
import arestory.com.marvelmoviefans.activities.RewardActivity
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.datasource.SettingDataSource
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.ToastUtil
import cn.waps.AppConnect
import com.afollestad.materialdialogs.MaterialDialog

object HelpPopupMenuUtil{



    fun showPopupMenu(context: Context,view:View,questionEntity: QuestionEntity){

        val popup = PopupMenu(context,view)//第二个参数是绑定的那个view
        //获取菜单填充器
        val inflater = popup.menuInflater
        //填充菜单
        inflater.inflate(R.menu.menu_help, popup.menu)
        //绑定菜单项的点击事件
        val tipsCount =  SettingDataSource.getTodayTipsCount(context)
        popup.menu.getItem(0).title = "获取提示（剩余${tipsCount}次）"

        popup.setOnMenuItemClickListener {

            when(it.itemId){
                R.id.feedback-> FeedbackActivity.start(context,questionEntity.id)
                R.id.tips->{

                    if(SettingDataSource.useTodayTips(context)){
                        ToastUtil.showLongToast(context,questionEntity.answer!!.toCharArray()[0].toString())
                    }else{
                        val openAdv= AppConnect.getInstance(context).getConfig("openADV","close")

                        if(openAdv=="open"){

                            MaterialDialog.Builder(context).title("今天的提示次数已用完，观看广告可增加提示次数哦")
                                .positiveText("去观看").onPositive { dialog, _ ->

                                    RewardActivity.start(context)
                                    dialog.dismiss()
                                }.negativeText("不了").onNegative { dialog, _ ->
                                    dialog.dismiss()
                                }.show()
                        }else{
                            ToastUtil.showToast(context,"今天的提示次数已用完")
                        }



                    }
                }
            }
            false
        }
        popup.show()
    }

}
