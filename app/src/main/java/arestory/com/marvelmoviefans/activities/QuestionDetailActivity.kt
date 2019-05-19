package arestory.com.marvelmoviefans.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.adapter.AnswerWordAdapter
import arestory.com.marvelmoviefans.adapter.KeywordAdapter
import arestory.com.marvelmoviefans.base.BaseDataBindingActivity
import arestory.com.marvelmoviefans.bean.AnswerWord
import arestory.com.marvelmoviefans.bean.NoAdminQuestion
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.common.HelpPopupMenuUtil
import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.databinding.ActivityQuestionDetailBinding
import arestory.com.marvelmoviefans.datasource.DataCallback
import arestory.com.marvelmoviefans.datasource.SettingDataSource
import arestory.com.marvelmoviefans.datasource.UserDataSource
import arestory.com.marvelmoviefans.util.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import ywq.ares.funapp.activity.ShowImageActivity

class QuestionDetailActivity:BaseDataBindingActivity<ActivityQuestionDetailBinding>() {

    private lateinit var questionEntity: NoAdminQuestion
    override fun getLayoutId(): Int= R.layout.activity_question_detail
    private lateinit var mInterstitialAd: InterstitialAd

    override fun doMain(savedInstanceState: Bundle?) {


        if(SettingDataSource.isAdvOpen(this)){

            mInterstitialAd = InterstitialAd(this)
            mInterstitialAd.adUnitId = "ca-app-pub-8884790662094305/2457116808"
//        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/103317372"
            mInterstitialAd.loadAd(AdRequest.Builder().build())

            mInterstitialAd.adListener = object : AdListener() {

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    mInterstitialAd.show()
                }
            }
        }
      dataBinding.layoutQuestion.ivHelp.setOnClickListener {


          HelpPopupMenuUtil.showPopupMenu(this,dataBinding.layoutQuestion.ivHelp,questionEntity)

        }



        questionEntity = intent.getSerializableExtra(QUESTION) as NoAdminQuestion
        initToolbarSetting(dataBinding.toolbar,questionEntity.title!!)
        dataBinding.layoutQuestion.question = questionEntity
        dataBinding.layoutQuestion.imageVisible = questionEntity.type == "image"

        dataBinding.layoutQuestion.keywordAdapter = KeywordAdapter(questionEntity.answer!!,questionEntity.keywords!!)
        dataBinding.layoutQuestion.answerAdapter = AnswerWordAdapter(questionEntity.answer!!)

        when( questionEntity.type){
            "image"->{
                dataBinding.layoutQuestion.audioLayout.visibility = View.GONE
                dataBinding.layoutQuestion.playBtn.visibility = View.GONE
                GlideApp.with(this).load(AppConstants.URL.FILE_PRE_URL + questionEntity.url).diskCacheStrategy(
                    DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading).into( dataBinding.layoutQuestion.ivQuestion)
                dataBinding.layoutQuestion.ivQuestion.setOnClickListener {

                    ShowImageActivity.start(this@QuestionDetailActivity,AppConstants.URL.FILE_PRE_URL + questionEntity.url)
                }

            }
            "audio"->{
                dataBinding.layoutQuestion.audioLayout.visibility = View.VISIBLE
                dataBinding.layoutQuestion.playBtn.visibility = View.VISIBLE
            }
        }
       dataBinding.layoutQuestion.rvAnswer.isNestedScrollingEnabled = false
       dataBinding.layoutQuestion.rvAnswer.layoutManager =
            GridLayoutManager(this, questionEntity.answer!!.length)

       dataBinding.layoutQuestion.rvAnswer.addItemDecoration(object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)

                val rvWidth =  dataBinding.layoutQuestion.rvAnswer.width
                val childWidth = MeasUtils.dpToPx(50f, this@QuestionDetailActivity)
                val pos = parent.getChildLayoutPosition(view)
                val answerLength = questionEntity.answer!!.length
                val w0 = 10
                val w = (rvWidth - answerLength * childWidth - (answerLength - 1) * w0) / 2
                when (pos) {
                    0 -> {

//                        outRect.left =w
                        outRect.left = w0
                        outRect.right = 0

                    }
                    answerLength - 1 -> {
                        outRect.left = w0
                        outRect.right = w0
                    }
                    else -> {
                        outRect.left = w0
                        outRect.right = 0

                    }
                }
            }
        })
       dataBinding.layoutQuestion.rvKeywords?.isNestedScrollingEnabled = false
       dataBinding.layoutQuestion.rvKeywords?.layoutManager = GridLayoutManager(this, 7)
       dataBinding.layoutQuestion.rvKeywords?.addItemDecoration(object : RecyclerView.ItemDecoration() {

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = 5
                outRect.bottom = 5
            }
        })

        (dataBinding.layoutQuestion.answerAdapter as AnswerWordAdapter).setWordOperationCallBack(object :
            AnswerWordAdapter.WordOperationCallBack {
            override fun wrongAnswer(answer: String) {
                AnimationUtil.startShakeByPropertyAnim(dataBinding.layoutQuestion.rvAnswer, 0.9f, 1.0f, 5f, 500)

            }

            override fun rightAnswer(answer: String) {

                if (!questionEntity.hadAnswer) {
                    questionEntity.hadAnswer = true
                    RxBus.get().post(questionEntity)
                    val loginUserId =  UserDataSource.getLoginUserId(this@QuestionDetailActivity)

                    UserDataSource.answerQuestion(
                        loginUserId,
                        questionEntity.id!!,
                        object : DataCallback<String> {
                            override fun onSuccess(data: String) {
                                RxBus.get().post("answer")

                            }

                            override fun onFail(msg: String?) {

                            }
                        })
                }

                ToastUtil.showToast(this@QuestionDetailActivity,"回答正确")
                finish()

            }

            override fun onNotEmptyWordClick(item: AnswerWord) {
                (dataBinding.layoutQuestion.keywordAdapter as KeywordAdapter).changeItemSelectStatus(item.index!!, false)
            }
        })


        (dataBinding.layoutQuestion.keywordAdapter as KeywordAdapter).setKeyWordClick(object :
            KeywordAdapter.KeyWordClick {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun add(word: AnswerWord) {

                (dataBinding.layoutQuestion.answerAdapter as AnswerWordAdapter).add(word)

                //播放音效
                ShortSoundPlayer.playSound(this@QuestionDetailActivity, R.raw.click)

            }

            override fun remove(word: AnswerWord) {
                (dataBinding.layoutQuestion.answerAdapter as AnswerWordAdapter).remove(word)


            }
        })
    }

    companion object {


        private const val QUESTION = "question"
        fun  start(context: Context,questionEntity: NoAdminQuestion){

            val intent = Intent(context,QuestionDetailActivity::class.java)
            intent.putExtra(QUESTION,questionEntity)
            context.startActivity(intent)
        }

        fun  startWithAnim(context: Activity,questionEntity: NoAdminQuestion,coverImg:ImageView){

            val intent = Intent(context,QuestionDetailActivity::class.java)
            val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(context,coverImg,"coverImg").toBundle();

            intent.putExtra(QUESTION,questionEntity)
            context.startActivity(intent,bundle)
        }
    }
}