package arestory.com.marvelmoviefans.activities

import arestory.com.marvelmoviefans.MainActivity
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseActivity
import java.util.*

class WelcomeActivity:BaseActivity(){
    override fun getLayoutId(): Int = R.layout.activity_welcome

    override fun doMain() {
        Timer().schedule(object : TimerTask() {
            override fun run() {

                MainActivity.Companion.start(this@WelcomeActivity)

                finish()
            }
        }, 500)
    }
}