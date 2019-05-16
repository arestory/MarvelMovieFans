package arestory.com.marvelmoviefans.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.activities.LevelQuestionActivity
import arestory.com.marvelmoviefans.adapter.QuestionAdapter
import arestory.com.marvelmoviefans.base.BaseDataBindingFragment
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.databinding.FragmentRandomQuestionBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.QuestionDataSource
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.RxBus
import com.ares.datacontentlayout.DataContentLayout

class RandomAnswerFragment : BaseDataBindingFragment<FragmentRandomQuestionBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_random_question

    override fun doMain() {



        getRandomQuestion()

        dataBinding.btnSkip.setOnClickListener {

            getRandomQuestion()
        }
     }

    fun getRandomQuestion(){
        dataBinding.loadFinish = false
        dataBinding.title ="正在获取数据..."
        dataBinding.dataContentLayout.showLoading()


        QuestionDataSource.getRandomQuestion(UserDataSource.getLoginUser(context!!)?.id,object :DataCallback<QuestionEntity>{

            override fun onSuccess(data: QuestionEntity) {

                dataBinding.loadFinish = true
                dataBinding.dataContentLayout.showContent()
                dataBinding.title = data.title
                dataBinding.rvQuestion.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
                dataBinding.questionAdapter = QuestionAdapter(arrayListOf(data))

                ( dataBinding.questionAdapter as QuestionAdapter).addAnswerCallBack(object :QuestionAdapter.AnswerCallback{
                    override fun wrongAnswer() {


                    }

                    override fun rightAnswer(answer: String) {

                        val user = UserDataSource.getLoginUser(context!!)
                        if(user!=null&&!data.hadAnswer){
                            UserDataSource.answerQuestion(user.id!!,data.id!!,object :DataCallback<String>{
                                override fun onSuccess(data: String) {
                                    RxBus.get().post("answer")

                                }

                                override fun onFail(msg: String?) {

                                 }

                            })
                        }
                        Toast.makeText(context,"恭喜你答对了",Toast.LENGTH_SHORT).show()
                        getRandomQuestion()

                    }

                })
//                val pagerSnapHelper =  PagerSnapHelper()
//                pagerSnapHelper.attachToRecyclerView(dataBinding.rvQuestion)
            }

            override fun onFail(msg: String?) {
                dataBinding.loadFinish = false
                dataBinding.dataContentLayout.showError(object :DataContentLayout.ErrorListener{
                    override fun showError(view: View) {
                         getRandomQuestion()
                    }

                })


            }

        })

    }

    companion object {

        fun newInstance(): RandomAnswerFragment {

            val args = Bundle()
            val fragment = RandomAnswerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
