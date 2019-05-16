package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.adapter.MyQuestionAdapter
import arestory.com.marvelmoviefans.base.BaseActivity
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.databinding.ActivityMyQuestionBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.uiview.CustomLoadMoreView
import arestory.com.marvelmoviefans.util.RxBus
import arestory.com.marvelmoviefans.util.ToastUtil
import com.ares.datacontentlayout.DataContentLayout

class MyQuestionActivity : BaseDataBindingActivity<ActivityMyQuestionBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_my_question

    private var currentPage: Int = 1
    private lateinit var adapter: MyQuestionAdapter

    private var isLoadingMore = false
    private lateinit var footView: View

    override fun doMain() {
        val userId = UserDataSource.getLoginUserId(this)!!

        val dis = RxBus.get().toFlowable(String::class.java).subscribe {

            if(it == REFRESH_MY_QUESTION){
                getUserQuestion(userId,1)
            }

        }
        initToolbarSetting(dataBinding.toolbar)

        dataBinding.btnAddQuestion.setOnClickListener {


            AddQuestionActivity.start(this@MyQuestionActivity)
        }



        getUserQuestion(userId, currentPage)

    }
    private fun getUserQuestion(userId: String, page: Int) {

        if (page == 1) {
            dataBinding.dataContentLayout.showLoading()
        }
        currentPage = page
        UserDataSource.getUserQuestion(userId, page, object : DataCallback<List<QuestionEntity>> {
            override fun onSuccess(data: List<QuestionEntity>) {


                if (page == 1) {
                    if (data.isEmpty()) {
                        dataBinding.dataContentLayout.showEmptyContent()
                    } else {

                        adapter = MyQuestionAdapter(data)
                        dataBinding.dataContentLayout.showContent()
                        footView = LayoutInflater.from(this@MyQuestionActivity).inflate(R.layout.view_footer, null)
                        if(data.size>=3){
                            adapter.addFooterView(footView)
                        }
                        adapter.bindToRecyclerView(dataBinding.rvQuestion)
                        dataBinding.rvQuestion.layoutManager = LinearLayoutManager(this@MyQuestionActivity)
                        dataBinding.rvQuestion.addItemDecoration(
                            DividerItemDecoration(
                                this@MyQuestionActivity,
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
                                        getUserQuestion(userId, currentPage + 1)
                                    }
                                }
                            }
                        })
                    }
                } else {
                    adapter.removeFooterView(footView)

                    if (data.isEmpty()) {
                        ToastUtil.showShortToast(this@MyQuestionActivity, "已经没有更多数据了")

                    } else {
                        adapter.addData(data)
                        adapter.addFooterView(footView)
                    }
                    isLoadingMore = false

                }
            }

            override fun onFail(msg: String?) {

                ToastUtil.showLongToast(this@MyQuestionActivity, msg)
                if (page == 1) {
                    dataBinding.dataContentLayout.showError(object : DataContentLayout.ErrorListener {
                        override fun showError(view: View) {

                            getUserQuestion(userId, page)
                        }
                    })
                } else {
                    isLoadingMore = false
                    adapter.removeFooterView(footView)
                    ToastUtil.showShortToast(this@MyQuestionActivity, "加载更多数据失败")
                }

            }


        })

    }

    companion object {


        const val REFRESH_MY_QUESTION = "refreshMyQuestion"
        fun start(context: Context) {

            context.startActivity(Intent(context, MyQuestionActivity::class.java))
        }
    }
}