package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import android.support.v7.widget.Toolbar
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseActivity

class AboutActivity:BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_about

    override fun doMain() {

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        initToolbarSetting(toolbar)
    }

    companion object {


        fun start(context:Context){

            context.startActivity(Intent(context,AboutActivity::class.java))
        }
    }
}