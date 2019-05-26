package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import android.support.v7.widget.Toolbar
import arestory.com.marvelmoviefans.BuildConfig
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseActivity
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity:BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_about

    override fun doMain() {

        initToolbarSetting(toolbar)

        tvVersion.text = if(BuildConfig.DEBUG){
            "调试版v${BuildConfig.VERSION_NAME}"
        }else{
            "v${BuildConfig.VERSION_NAME}"
        }
    }

    companion object {


        fun start(context:Context){

            context.startActivity(Intent(context,AboutActivity::class.java))
        }
    }
}