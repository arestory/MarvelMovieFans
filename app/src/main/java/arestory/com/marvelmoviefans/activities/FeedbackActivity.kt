package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import android.support.v7.widget.Toolbar
import android.view.View
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.base.BaseActivity
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.databinding.ActivityFeedbackBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.ToastUtil
import com.ares.movie.entity.FeedbackEntity

class FeedbackActivity:BaseDataBindingActivity<ActivityFeedbackBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_feedback

    override fun doMain() {

        initToolbarSetting(dataBinding.toolbar)
        dataBinding.btnCommit.setOnClickListener {

            val content  = dataBinding.etContent.text.toString()
            if(content.isEmpty()){
                dataBinding.inputContent.error = "内容不能为空"
                return@setOnClickListener
            }
            enableInput(false)
            val feedbackEntity = FeedbackEntity()
            feedbackEntity.content = content
            val userId =  UserDataSource.getLoginUser(this@FeedbackActivity)?.id
            feedbackEntity.userId =when(userId){

                null->"traveler"
                else -> userId

            }
            UserDataSource.commitFeedback(feedbackEntity,object :DataCallback<String>{
                override fun onSuccess(data: String) {
                    enableInput(true)

                    ToastUtil.showToast(this@FeedbackActivity,"反馈成功")

                    finish()
                }

                override fun onFail(msg: String?) {

                    enableInput(true)

                    dataBinding.inputContent.error = msg

                }
            })


        }
    }
    fun enableInput(enable:Boolean){

        dataBinding.inputContent.isEnabled = enable
        dataBinding.btnCommit.isEnabled = enable
        dataBinding.pb.visibility = when(enable){

            true-> View.INVISIBLE
            else -> View.VISIBLE

        }

    }

    companion object {


        fun start(context:Context){

            context.startActivity(Intent(context,FeedbackActivity::class.java))
        }
    }
}