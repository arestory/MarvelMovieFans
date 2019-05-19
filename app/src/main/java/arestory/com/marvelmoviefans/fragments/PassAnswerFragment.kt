package arestory.com.marvelmoviefans.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.activities.LevelQuestionActivity
import arestory.com.marvelmoviefans.adapter.LevelAdapter
import arestory.com.marvelmoviefans.base.BaseDataBindingFragment
import arestory.com.marvelmoviefans.bean.QuestionPage
import arestory.com.marvelmoviefans.bean.UserInfo
import arestory.com.marvelmoviefans.databinding.FragmentPassQuestionsBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.QuestionDataSource
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.RxBus
import com.ares.datacontentlayout.DataContentLayout

class PassAnswerFragment : BaseDataBindingFragment<FragmentPassQuestionsBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_pass_questions

    override fun doMain() {


        getLevel()

       val dis = RxBus.get().toFlowable(UserInfo::class.java).subscribe {


            getLevel()


        }
        val dis2 = RxBus.get().toFlowable(String::class.java).subscribe{

            if(it == "exitUser"){
                getLevel()

            }
        }
     }

    fun getLevel(){

        dataBinding.dataContentLayout.showLoading()

        val dis = RxBus.get().toFlowable(QuestionPage::class.java).subscribe {


            val page = it.pageIndex!!-1
            UserDataSource.saveLevelPageDone(context!!,UserDataSource.getLoginUserId(context!!)!!,page)
            val levelAdapter =  dataBinding.levelAdapter as LevelAdapter
            levelAdapter.setData(page,it)

        }


        QuestionDataSource.getQuestionTotalCount(object :DataCallback<Int>{
            override fun onSuccess(data: Int) {



                if(context!=null){

                    val arrayList = ArrayList<QuestionPage>()
                    for(i in 0 until data){
                        if(i!=0&&i%10==0){
                            val page = QuestionPage()
                            page.pageIndex=i/10
                            page.count=10
                            page.finish = UserDataSource.checkLevelFinish(context!!,UserDataSource.getLoginUserId(context!!),page.pageIndex!!-1)
                            arrayList.add(page)
                        }
                    }
                    if(data%10>0){
                        val theEndPage =  QuestionPage()
                        theEndPage.pageIndex = arrayList.size+1
                        theEndPage.count=data%10

                        theEndPage.finish = UserDataSource.checkLevelFinish(context!!,UserDataSource.getLoginUserId(context!!),theEndPage.pageIndex!!-1)
                        arrayList.add(theEndPage)
                    }

                    print(arrayList.size)
                    dataBinding.levelAdapter = LevelAdapter(arrayList)
                    dataBinding.levelAdapter?.setOnItemClickListener { adapter, view, position ->



                    }
                    dataBinding.rvLevel.layoutManager = GridLayoutManager(context,5)
                    dataBinding.dataContentLayout.showContent()
                }



            }

            override fun onFail(msg: String?) {

                dataBinding.dataContentLayout.showError(object :DataContentLayout.ErrorListener{
                    override fun showError(view: View) {
                         getLevel()
                    }

                })
             }

        })
    }

    companion object {

        fun newInstance(): PassAnswerFragment {

            val args = Bundle()
            val fragment = PassAnswerFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
