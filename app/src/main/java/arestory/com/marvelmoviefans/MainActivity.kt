package arestory.com.marvelmoviefans

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import arestory.com.marvelmoviefans.databinding.ActivityMainBinding
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.fragments.NoAdminQuestionFragment
import arestory.com.marvelmoviefans.fragments.PassAnswerFragment
import arestory.com.marvelmoviefans.fragments.RandomAnswerFragment
import arestory.com.marvelmoviefans.fragments.UserInfoFragment
import arestory.com.marvelmoviefans.util.ToastUtil
import cn.waps.AppConnect
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta


class MainActivity : BaseDataBindingActivity<ActivityMainBinding>() {

    private  var randomAnswerFragment: RandomAnswerFragment?=null
    private  var passAnswerFragment: PassAnswerFragment?=null
    private   var noAdminQuestionFragment: NoAdminQuestionFragment?=null
    private   var userInfoFragment: UserInfoFragment?=null
    private var lastIndex:Int=0
    private val fragments = ArrayList<Fragment?>()
    override fun getLayoutId(): Int =R.layout.activity_main

    private val fragmentTags = arrayListOf("random","pass","noAdmin","user")
    override fun doMain(savedInstanceState: Bundle?) {
        if(savedInstanceState==null){
            randomAnswerFragment = RandomAnswerFragment.newInstance()
            passAnswerFragment = PassAnswerFragment.newInstance()
            noAdminQuestionFragment = NoAdminQuestionFragment.newInstance()
            userInfoFragment = UserInfoFragment.newInstance()
            lastIndex = 0
        }else{

            val fragment1 = supportFragmentManager.findFragmentByTag(fragmentTags[0])
            if( fragment1 is RandomAnswerFragment){
                randomAnswerFragment = fragment1
            }
            val fragment2 = supportFragmentManager.findFragmentByTag(fragmentTags[1])
            if( fragment2 is PassAnswerFragment){
                passAnswerFragment = fragment2
            }
            val fragment3 = supportFragmentManager.findFragmentByTag(fragmentTags[2])
            if(fragment3 is NoAdminQuestionFragment){
                noAdminQuestionFragment = fragment3
            }
            val fragment4 = supportFragmentManager.findFragmentByTag(fragmentTags[3])
            if(fragment4 is UserInfoFragment){
                userInfoFragment = fragment4
            }
            lastIndex = savedInstanceState.getInt("lastIndex",0)
        }
        fragments.add(randomAnswerFragment)
        fragments.add(passAnswerFragment)
        fragments.add(noAdminQuestionFragment)
        fragments.add(userInfoFragment)


        dataBinding.bottomNv.labelVisibilityMode=1
        dataBinding.bottomNv.setOnNavigationItemSelectedListener{

            when(it.itemId){
                R.id.action_random-> switchFragment(0)
                R.id.action_pass-> switchFragment(1)
                R.id.action_circle-> switchFragment(2)
                R.id.action_user-> switchFragment(3)

            }

            true
        }


        switchFragment(lastIndex)

    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putInt("lastIndex",lastIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppConnect.getInstance(this).close()
    }

   private fun switchFragment(pos:Int){


       val ft = supportFragmentManager.beginTransaction()

       val currentFragment =fragments[pos]

       val lastFragment = fragments[lastIndex]
       lastIndex = pos
       if(lastFragment!=null){
           ft.hide(lastFragment)
       }

       if(currentFragment!=null){
           if(!currentFragment.isAdded){
               ft.add(R.id.frameLayout,currentFragment,fragmentTags[pos])
           }
           ft.show(currentFragment)
       }


       ft.commitAllowingStateLoss()

    }

    companion object {


        fun start(context: Context){

            context.startActivity(Intent(context,MainActivity::class.java))
        }
    }


}
