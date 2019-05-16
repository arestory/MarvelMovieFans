package arestory.com.marvelmoviefans.fragments

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.activities.QuestionDetailActivity
import arestory.com.marvelmoviefans.adapter.NotAdminQuestionAdapter
import arestory.com.marvelmoviefans.base.BaseDataBindingFragment
import arestory.com.marvelmoviefans.bean.NoAdminQuestion
import arestory.com.marvelmoviefans.bean.UserInfo
import arestory.com.marvelmoviefans.databinding.FragmentNotAdminQuestionsBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.QuestionDataSource
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.RxBus
import arestory.com.marvelmoviefans.util.ToastUtil
import com.ares.datacontentlayout.DataContentLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NoAdminQuestionFragment:BaseDataBindingFragment<FragmentNotAdminQuestionsBinding>() {

    private var currentPage =1

    private var isLoadingMore = false
    private lateinit var footView: View
    private lateinit var adapter: NotAdminQuestionAdapter
    override fun getLayoutId(): Int = R.layout.fragment_not_admin_questions

    private var createUserId:String?=null
    override fun doMain() {

        createUserId = arguments
            ?.getString(CREATE_USER_ID,null)
        dataBinding.createUserId = createUserId
        val dis =RxBus.get().toFlowable(NoAdminQuestion::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{

            val index = adapter.data.indexOf(it)
            if(index>=0){

                adapter.setData(index,it)
            }

        }

        val dis2 = RxBus.get().toFlowable(String::class.java).subscribe {


            if(it==UserDataSource.EXIT_USER){

                getQuestion(UserDataSource.getLoginUserId(context!!),1)
            }

        }
        val dis3 = RxBus.get().toFlowable(UserInfo::class.java).subscribe {
            getQuestion(UserDataSource.getLoginUserId(context!!),1)

        }
        getQuestion(UserDataSource.getLoginUserId(context!!),currentPage)
     }

    private fun getQuestion(userId:String?,page:Int){

        if(page==1){
            dataBinding.dataContentLayout.showLoading()
        }
        QuestionDataSource.getNotAdminQuestions(createUserId,userId,page,object :DataCallback<List<NoAdminQuestion>>{
            override fun onSuccess(data: List<NoAdminQuestion>) {

                if(page==1){
                    adapter = NotAdminQuestionAdapter(data)

                    adapter.setItemClick(object :NotAdminQuestionAdapter.ItemClick{
                        override fun onClick(questionEntity: NoAdminQuestion?,imageView: ImageView) {

                            QuestionDetailActivity.startWithAnim(context!! as Activity,questionEntity!!,imageView)
                        }


                    })
                    footView = LayoutInflater.from(context!!).inflate(R.layout.view_footer,null)
                    adapter.addFooterView(footView)
                    dataBinding.questionAdapter=adapter
                    dataBinding.rvQuestion.layoutManager = LinearLayoutManager(context!!,RecyclerView.VERTICAL,false)
                    dataBinding.rvQuestion.addItemDecoration(
                        DividerItemDecoration(
                            context,
                            RecyclerView.VERTICAL
                        )
                    )

                    dataBinding.rvQuestion.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                                val pos =
                                    (dataBinding.rvQuestion.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                                println("pos = $pos")
                                if (pos >= adapter.itemCount.minus(1) && !isLoadingMore) {
                                    println("到达底部啦！！")
                                    isLoadingMore = true
                                    getQuestion(userId, currentPage + 1)
                                }
                            }
                        }
                    })

                   if(data.isEmpty()){
                       if(createUserId==null){
                           dataBinding.dataContentLayout.showEmptyContent()

                       }else{

                           dataBinding.dataContentLayout.showEmptyContent("她/他没有提交过问题")
                       }
                   }else{
                       dataBinding.dataContentLayout.showContent()
                   }

                }else{
                    adapter.removeFooterView(footView)

                    if (data.isEmpty()) {
                        ToastUtil.showShortToast(context!!, "已经没有更多数据了")

                    } else {
                        adapter.addData(data)
                        adapter.addFooterView(footView)
                    }
                    isLoadingMore = false
                }

            }

            override fun onFail(msg: String?) {

                ToastUtil.showLongToast(context!!, msg)
                if (page == 1) {
                    dataBinding.dataContentLayout.showError(object : DataContentLayout.ErrorListener {
                        override fun showError(view: View) {

                            getQuestion(userId, page)
                        }
                    })
                } else {
                    isLoadingMore = false
                    adapter.removeFooterView(footView)
                    ToastUtil.showShortToast(context!!, "加载更多数据失败")
                }

             }


        })
    }
    companion object {

        private const val CREATE_USER_ID ="createUserId"
        fun newInstance(): NoAdminQuestionFragment {

            val args = Bundle()
            val fragment = NoAdminQuestionFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(createUserId:String): NoAdminQuestionFragment {

            val args = Bundle()
            val fragment = NoAdminQuestionFragment()
            args.putString(CREATE_USER_ID,createUserId)
            fragment.arguments = args
            return fragment
        }
    }


}