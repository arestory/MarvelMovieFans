package arestory.com.marvelmoviefans.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.adapter.QuestionAdapter
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.databinding.ActivityAnswerQuestionBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.QuestionDataSource
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.RxBus
import com.ares.datacontentlayout.DataContentLayout

class AnswerQuestionActivity: BaseDataBindingActivity<ActivityAnswerQuestionBinding>() {


    override fun getLayoutId(): Int  = R.layout.activity_answer_question

    override fun doMain(savedInstanceState: Bundle?) {
        initToolbarSetting(dataBinding.toolbar)

        getRandomQuestion()

        dataBinding.btnSkip.setOnClickListener {

            getRandomQuestion()
        }

     }


    companion object {


        fun startActivityForResult(activity:Activity,code:Int){


            val intent = Intent(activity,AnswerQuestionActivity::class.java)

            activity.startActivityForResult(intent,code)
        }
    }

    fun getRandomQuestion(){
        dataBinding.loadFinish = false
        dataBinding.title ="正在获取数据..."
        dataBinding.dataContentLayout.showLoading()


        QuestionDataSource.getRandomQuestion(UserDataSource.getLoginUser(this)?.id,object :
            DataCallback<QuestionEntity> {

            override fun onSuccess(data: QuestionEntity) {

                dataBinding.loadFinish = true
                dataBinding.dataContentLayout.showContent()
                dataBinding.title = data.title
                dataBinding.rvQuestion.layoutManager = LinearLayoutManager(this@AnswerQuestionActivity, RecyclerView.VERTICAL,false)
                dataBinding.questionAdapter = QuestionAdapter(arrayListOf(data))

                ( dataBinding.questionAdapter as QuestionAdapter).addAnswerCallBack(object : QuestionAdapter.AnswerCallback{
                    override fun wrongAnswer() {


                    }

                    override fun rightAnswer(answer: String) {


                        Toast.makeText(this@AnswerQuestionActivity,"恭喜你答对了", Toast.LENGTH_SHORT).show()

                        setResult(Activity.RESULT_OK)
                        finish()

                    }

                })
//                val pagerSnapHelper =  PagerSnapHelper()
//                pagerSnapHelper.attachToRecyclerView(dataBinding.rvQuestion)
            }

            override fun onFail(msg: String?) {
                dataBinding.loadFinish = false
                dataBinding.dataContentLayout.showError(object : DataContentLayout.ErrorListener{
                    override fun showError(view: View) {
                        getRandomQuestion()
                    }

                })


            }

        })

    }

}