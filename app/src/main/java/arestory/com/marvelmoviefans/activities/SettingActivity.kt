package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseActivity
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.databinding.ActivitySettingBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.SettingDataSource
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.GlideCacheUtil
import arestory.com.marvelmoviefans.util.ToastUtil
import cn.waps.AppConnect
import com.ares.movie.entity.UserPoint
import com.tencent.bugly.beta.Beta

class SettingActivity:BaseDataBindingActivity<ActivitySettingBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun doMain(savedInstanceState: Bundle?) {


        val point = intent.getIntExtra(POINT,0)
        dataBinding.cacheSize = GlideCacheUtil.getInstance().getCacheSize(this)
        initToolbarSetting(dataBinding.toolbar)
        dataBinding.btnClearCache.setOnClickListener {


            GlideCacheUtil.getInstance().clearImageDiskCache(this)
            dataBinding.cacheSize ="已清理所有缓存"
        }

        val loginUserId = UserDataSource.getLoginUserId(this)

        dataBinding.openAdv = SettingDataSource.isAdvOpen(this)
        dataBinding.openSound = SettingDataSource.isSoundOpen(this)

        val openAdv= AppConnect.getInstance(this).getConfig("openADV","close")

        if(openAdv=="open"){
            dataBinding.layoutAdv.visibility=View.VISIBLE
        }else{
            dataBinding.layoutAdv.visibility=View.GONE

        }

        dataBinding.layoutAdv.setOnClickListener {

            if(loginUserId==null){
                ToastUtil.showToast(this,"未登录，不能更改！")
                dataBinding.openAdv =true
            }else{
                if(point>100){
                    dataBinding.openAdv =  !dataBinding.openAdv!!
                }else{
                    ToastUtil.showLongToast(this@SettingActivity,"想要关闭广告？答题得分超过100就可以啦！")

                }


            }
        }
        dataBinding.layoutSound.setOnClickListener {

            dataBinding.openSound =  !dataBinding.openSound!!

        }
        dataBinding.updateTitle ="检查更新"
        dataBinding.checkingUpdate =false
        dataBinding.layoutUpdate.setOnClickListener {
            dataBinding.updateTitle ="检查更新中..."
            dataBinding.checkingUpdate =true
            Beta.checkUpgrade()
        }
        dataBinding.cbSound.setOnCheckedChangeListener { buttonView, isChecked ->

            SettingDataSource.toggleSound(this, isChecked)

        }
        if(loginUserId==null||point<100){
            dataBinding.cbAdv.isChecked=true
            dataBinding.cbAdv.isEnabled=false
        }

        dataBinding.cbAdv.setOnCheckedChangeListener { buttonView, isChecked ->

            SettingDataSource.toggleAdv(this, isChecked)


        }
    }

    companion object {


        const val POINT ="point"
        fun start(context:Context,userPoint: Int){

            val intent = Intent(context,SettingActivity::class.java)
            intent.putExtra(POINT, userPoint)
            context.startActivity(intent)
        }
    }
}