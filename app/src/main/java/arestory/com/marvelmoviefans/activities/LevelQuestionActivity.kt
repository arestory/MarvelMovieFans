package arestory.com.marvelmoviefans.activities

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.adapter.QuestionAdapter
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.bean.QuestionPage
import arestory.com.marvelmoviefans.bean.UserInfo
import  arestory.com.marvelmoviefans.databinding.ActivityLevelQuestionBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.QuestionDataSource
import arestory.com.marvelmoviefans.datasource.SettingDataSource
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.RxBus
import arestory.com.marvelmoviefans.util.ToastUtil
import com.ares.datacontentlayout.DataContentLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd


class LevelQuestionActivity : BaseDataBindingActivity<ActivityLevelQuestionBinding>() {


    override fun getLayoutId(): Int = R.layout.activity_level_question

    private lateinit var page: QuestionPage

    companion object {

        fun start(context: Context, page: QuestionPage) {

            val intent = Intent(context, LevelQuestionActivity::class.java)
            intent.putExtra("page", page)

            context.startActivity(intent)

        }
    }

    fun getLoginUser(): UserInfo? {


        return UserDataSource.getLoginUser(this@LevelQuestionActivity)
    }

    fun getQuestionList() {


        page = intent.getSerializableExtra("page") as QuestionPage
        dataBinding.page = page
        dataBinding.dataContentLayout.showLoading()
        QuestionDataSource.getQuestionList(
            UserDataSource.getLoginUser(this@LevelQuestionActivity)?.id,
            page.pageIndex!!,
            10,
            object : DataCallback<List<QuestionEntity>> {
                override fun onSuccess(data: List<QuestionEntity>) {

                    val levelFinish = data.all {
                        it.hadAnswer
                    }
                    if (levelFinish) {
                        val questionPage = QuestionPage()
                        questionPage.count = 10
                        questionPage.finish = true
                        questionPage.pageIndex = page.pageIndex!!
                        RxBus.get().post(questionPage)

                    }
                    val firstQuestion = data[0]
                    initToolbarSetting(dataBinding.toolbar, firstQuestion.title!!)
                    print(data)

                    dataBinding.btnSkip.text = if (firstQuestion.hadAnswer) {
                        "已回答，下一个"
                    } else {
                        "下一个"
                    }
                    dataBinding.questionAdapter = QuestionAdapter(data)
                    val questionAdapter = (dataBinding.questionAdapter as QuestionAdapter)
                    questionAdapter.addAnswerCallBack(object : QuestionAdapter.AnswerCallback {
                        override fun wrongAnswer() {

                        }

                        override fun rightAnswer(answer: String) {


                            val layoutManager = dataBinding.rvQuestion.layoutManager as LinearLayoutManager
                            val position = layoutManager.findFirstVisibleItemPosition()
                            val question = data[position]
                            if (!question.hadAnswer) {

                                UserDataSource.answerQuestion(
                                    getLoginUser()?.id!!,
                                    data[position].id!!,
                                    object : DataCallback<String> {
                                        override fun onSuccess(data: String) {


                                            RxBus.get().post("answer")
                                            questionAdapter.answerQuestion(position)
                                        }

                                        override fun onFail(msg: String?) {

                                        }
                                    })
                            }
                            if (position == data.size - 1) {

                                //如果全部回答正确
                                if (data.all {
                                        it.hadAnswer
                                    }) {

                                    val questionPage = QuestionPage()
                                    questionPage.count = 10
                                    questionPage.finish = true
                                    questionPage.pageIndex = page.pageIndex!!
                                    RxBus.get().post(questionPage)

                                    ToastUtil.showLongToast(this@LevelQuestionActivity, "恭喜你本关已全部答对！")
                                    finish()
                                }

                            }else{
                                ToastUtil.showShortToast(this@LevelQuestionActivity, "恭喜你答对了")

                                dataBinding.btnSkip.postDelayed({
                                    dataBinding.btnSkip.performClick()

                                }, 1000)
                            }


                        }


                    })
                    val pagerSnapHelper = PagerSnapHelper()
                    pagerSnapHelper.attachToRecyclerView(dataBinding.rvQuestion)
                    dataBinding.rvQuestion.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                        }

                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            super.onScrollStateChanged(recyclerView, newState)
                            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                val position = layoutManager.findFirstVisibleItemPosition()
                                val question = data[position]
                                dataBinding.toolbar.title = dataBinding?.questionAdapter?.getItem(position)?.title
                                if (position == data.size - 1) {

                                    dataBinding.btnSkip.text = "完成"
                                    dataBinding.btnSkip.paint.isFakeBoldText = true
                                } else {

                                    dataBinding.btnSkip.text = if (question.hadAnswer) {
                                        "已回答，下一个"
                                    } else {
                                        "下一个"
                                    }
                                }
//                            //停止了
//                            (dataBinding.questionAdapter as QuestionAdapter).releaseLastMediaPlayer(position-1)
                            }
                        }
                    })
                    dataBinding.rvQuestion.layoutManager = LinearLayoutManager(
                        this@LevelQuestionActivity,
                        RecyclerView.HORIZONTAL, false
                    )
                    dataBinding.rvQuestion.addItemDecoration(object : RecyclerView.ItemDecoration() {

                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            super.getItemOffsets(outRect, view, parent, state)
                            outRect.left = 5
                            outRect.right = 5
                            //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
//                            if (parent.getChildLayoutPosition(view) %3==0) {
//                                outRect.left = 0;
//                            }
                        }
                    })

                    dataBinding.dataContentLayout.showContent()

                }

                override fun onFail(msg: String?) {

                    print(msg)
                    dataBinding.dataContentLayout.showError(object : DataContentLayout.ErrorListener {
                        override fun showError(view: View) {

                            getQuestionList()
                        }

                    })
                }


            })
    }


    private lateinit var mInterstitialAd: InterstitialAd

    override fun doMain(savedInstanceState: Bundle?) {
        initToolbarSetting(dataBinding.toolbar)



        if(SettingDataSource.isAdvOpen(this)){

            mInterstitialAd = InterstitialAd(this)
            mInterstitialAd.adUnitId = "ca-app-pub-8884790662094305/2457116808"
//        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
            mInterstitialAd.loadAd(AdRequest.Builder().build())

            mInterstitialAd.adListener = object : AdListener() {

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    mInterstitialAd.show()
                }
            }
        }
        dataBinding.btnSkip.setOnClickListener {
            val layoutManager = (dataBinding.rvQuestion.layoutManager as LinearLayoutManager)
            if (layoutManager.findFirstVisibleItemPosition() + 1 >= dataBinding.questionAdapter!!.itemCount) {

                val questionAdapter = (dataBinding.questionAdapter as QuestionAdapter)

                //如果全部回答正确
                if (questionAdapter.data.all {
                        it.hadAnswer
                    }) {

                    val questionPage = QuestionPage()
                    questionPage.count = 10
                    questionPage.finish = true
                    questionPage.pageIndex = page.pageIndex!!
                    RxBus.get().post(questionPage)
                    ToastUtil.showLongToast(this@LevelQuestionActivity, "恭喜你本关已全部答对！")
                }
                finish()
            } else {
                dataBinding.rvQuestion.smoothScrollToPosition(layoutManager.findFirstVisibleItemPosition() + 1)
            }
        }

        getQuestionList()

    }

}