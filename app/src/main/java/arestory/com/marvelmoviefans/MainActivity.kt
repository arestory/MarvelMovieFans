package arestory.com.marvelmoviefans

import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import arestory.com.marvelmoviefans.databinding.ActivityMainBinding
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.fragments.NoAdminQuestionFragment
import arestory.com.marvelmoviefans.fragments.PassAnswerFragment
import arestory.com.marvelmoviefans.fragments.RandomAnswerFragment
import arestory.com.marvelmoviefans.fragments.UserInfoFragment


class MainActivity : BaseDataBindingActivity<ActivityMainBinding>() {

    private lateinit var randomAnswerFragment: RandomAnswerFragment
    private lateinit var passAnswerFragment: PassAnswerFragment
    private lateinit var noAdminQuestionFragment: NoAdminQuestionFragment
    private lateinit var userInfoFragment: UserInfoFragment
    private var lastIndex:Int=0
    private val fragments = ArrayList<Fragment>()
    override fun getLayoutId(): Int =R.layout.activity_main

    override fun doMain() {

        randomAnswerFragment = RandomAnswerFragment.newInstance()
        passAnswerFragment = PassAnswerFragment.newInstance()
        noAdminQuestionFragment = NoAdminQuestionFragment.newInstance()
        userInfoFragment = UserInfoFragment.newInstance()
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


        switchFragment(0)
     }

   private fun switchFragment(pos:Int){

       val ft = supportFragmentManager.beginTransaction()

       val currentFragment =fragments[pos]

       val lastFragment = fragments[lastIndex]
       lastIndex = pos
       ft.hide(lastFragment)
       if(!currentFragment.isAdded){
           ft.add(R.id.frameLayout,currentFragment)
       }
       ft.show(currentFragment)

       ft.commitAllowingStateLoss()

    }


}
