package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.databinding.ActivityUserQuestionBinding
import arestory.com.marvelmoviefans.fragments.NoAdminQuestionFragment

class UserQuestionActivity:BaseDataBindingActivity<ActivityUserQuestionBinding>() {

    lateinit var createUserId:String
    lateinit var userNickName:String
    lateinit var questionFragment: NoAdminQuestionFragment
    override fun getLayoutId(): Int = R.layout.activity_user_question

    override fun doMain() {

        createUserId = intent.getStringExtra(CREATE_USER_ID)
        userNickName = intent.getStringExtra(NICK_NAME)
        questionFragment = NoAdminQuestionFragment.newInstance(createUserId)


        initToolbarSetting(dataBinding.toolbar)
        dataBinding.title = userNickName.plus("提交的问题")
        val ft = supportFragmentManager.beginTransaction()
        if(!questionFragment.isAdded){
            ft.add(R.id.frameLayout,questionFragment)
        }
        ft.show(questionFragment)
        ft.commit()
    }


    companion object {

       private const val CREATE_USER_ID = "createUserId"
       private const val NICK_NAME = "nickName"

        fun start(context: Context,createUserId:String,userNickName:String){


            val intent = Intent(context,UserQuestionActivity::class.java)
            intent.putExtra(CREATE_USER_ID,createUserId)
            intent.putExtra(NICK_NAME,userNickName)

            context.startActivity(intent)

        }
    }
}