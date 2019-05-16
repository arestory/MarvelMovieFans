package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import android.support.v7.widget.Toolbar
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseActivity
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.databinding.ActivitySettingBinding
import arestory.com.marvelmoviefans.datasource.SettingDataSource
import arestory.com.marvelmoviefans.util.GlideCacheUtil
import com.tencent.bugly.beta.Beta

class SettingActivity:BaseDataBindingActivity<ActivitySettingBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun doMain() {


        dataBinding.cacheSize = GlideCacheUtil.getInstance().getCacheSize(this)
        initToolbarSetting(dataBinding.toolbar)
        dataBinding.btnClearCache.setOnClickListener {


            GlideCacheUtil.getInstance().clearImageDiskCache(this)
            dataBinding.cacheSize ="已清理所有缓存"
        }

        dataBinding.openAdv = SettingDataSource.isAdvOpen(this)
        dataBinding.openSound = SettingDataSource.isSoundOpen(this)
        dataBinding.layoutAdv.setOnClickListener {

            dataBinding.openAdv =  !dataBinding.openAdv!!

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
        dataBinding.cbAdv.setOnCheckedChangeListener { buttonView, isChecked ->

            SettingDataSource.toggleAdv(this, isChecked)

        }
    }

    companion object {


        fun start(context:Context){

            context.startActivity(Intent(context,SettingActivity::class.java))
        }
    }
}