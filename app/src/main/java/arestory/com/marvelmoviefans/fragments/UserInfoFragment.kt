package arestory.com.marvelmoviefans.fragments

import android.os.Bundle
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.activities.*
import arestory.com.marvelmoviefans.base.BaseDataBindingFragment
import arestory.com.marvelmoviefans.bean.UserInfo
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.databinding.FragmentUserBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.GlideCircleTransform
import arestory.com.marvelmoviefans.util.RxBus
import ywq.ares.funapp.activity.ShowImageActivity

class UserInfoFragment : BaseDataBindingFragment<FragmentUserBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_user


    private fun refreshUserPoint(userId:String){
        UserDataSource.getUserPoint(userId, object : DataCallback<Int> {
            override fun onSuccess(data: Int) {

                dataBinding.point = ":$data"
            }

            override fun onFail(msg: String?) {
            }

        })

    }
    private fun initUser(user: UserInfo?){

        if(user!=null){

            GlideApp.with(context!!).load(AppConstants.URL.FILE_PRE_URL+user.avatar).placeholder(R.drawable.placeholder).transform(GlideCircleTransform(10,resources.getColor(R.color.white)))
                .into(dataBinding.ivUser)

            dataBinding.userName = user.nickName

            dataBinding.point = ""
            refreshUserPoint(user.id!!)


            dataBinding.ivUser.setOnClickListener {


                ShowImageActivity.start(context!!,AppConstants.URL.FILE_PRE_URL+user.avatar)
            }


        }else{
            GlideApp.with(context!!).load(R.drawable.placeholder).transform(GlideCircleTransform(10,resources.getColor(R.color.white)))
                .into(dataBinding.ivUser)
            dataBinding.userName = "未登录"
            dataBinding.point = ""

            dataBinding.ivUser.setOnClickListener {



                UserLoginActivity.start(context!!)
            }
        }


    }
    override fun doMain() {


       initUser(getLoginUser())

       val dis =  RxBus.get().toFlowable(UserInfo::class.java).subscribe {


           initUser(it)

        }
        val disPoint = RxBus.get().toFlowable(String::class.java).subscribe {


            if(it=="answer"){
                refreshUserPoint(getLoginUser()?.id!!)
            }else if(it =="exitUser"){

                initUser(getLoginUser())
            }

        }


        dataBinding.btnWatch.setOnClickListener {


            dataBinding.topLayout.performClick()
        }


        dataBinding.topLayout.setOnClickListener {


            if(getLoginUser()!=null){

//                UserInfoActivity.start(activity!!)
                UserInfoActivity.startWithAnim(activity!!,dataBinding.ivUser)
            }else{
                UserLoginActivity.start(context!!)
            }
        }


        dataBinding.btnMyQuestion.setOnClickListener {

            if(UserDataSource.getLoginUser(context!!)!=null){

                MyQuestionActivity.start(context!!)
            }else{
                UserLoginActivity.start(context!!)
            }

        }
        dataBinding.btnMyPoint.setOnClickListener {

        }
        dataBinding.btnRank.setOnClickListener {

            UserPointActivity.start(context!!)

        }
        dataBinding.btnSetting.setOnClickListener {
            SettingActivity.start(context!!)

        }
        dataBinding.btnAbout.setOnClickListener {

            AboutActivity.start(context!!)

        }
        dataBinding.btnFeedback.setOnClickListener {
            FeedbackActivity.start(context!!)
        }
    }

    fun getLoginUser(): UserInfo? {



        return UserDataSource.getLoginUser(context!!)
    }


    companion object {

        fun newInstance(): UserInfoFragment {

            val args = Bundle()
            val fragment = UserInfoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
