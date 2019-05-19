package arestory.com.marvelmoviefans.adapter

import android.graphics.Rect
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.bean.AnswerWord
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.databinding.QuestionPageLayoutBinding
import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.datasource.SettingDataSource
import arestory.com.marvelmoviefans.datasource.UserDataSource
import cn.waps.AppConnect
import com.google.android.gms.ads.AdRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ywq.ares.funapp.activity.ShowImageActivity
import java.lang.Exception
import android.view.MenuInflater
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import android.widget.Toast
import arestory.com.marvelmoviefans.activities.FeedbackActivity
import arestory.com.marvelmoviefans.bean.FeedbackEntity
import arestory.com.marvelmoviefans.common.HelpPopupMenuUtil
import arestory.com.marvelmoviefans.util.*
import com.bumptech.glide.load.engine.DiskCacheStrategy


class QuestionAdapter :
    BaseQAdapter<QuestionEntity, QuestionPageLayoutBinding, MVViewHolder<QuestionPageLayoutBinding>> {

    constructor(data: List<QuestionEntity>?) : super(R.layout.question_page_layout, data)

    private var answerCallback: AnswerCallback? = null
    fun addAnswerCallBack(answerCallback: AnswerCallback?) {
        this.answerCallback = answerCallback
    }

    fun answerQuestion(pos:Int){

        val question = data[pos]
        question.hadAnswer = true

        doAnswerMap[question] = true
        setData(pos,question)
    }

    interface AnswerCallback {

        fun wrongAnswer()
        fun rightAnswer(answer: String)
    }

    private var map = HashMap<QuestionEntity, ExMediaPlayer?>()
    fun getMediaPlayer(item: QuestionEntity?)=map[item]
    fun releaseLastMediaPlayer(lastPos:Int){


        if(lastPos>=0){

            getMediaPlayer(data[lastPos])?.pause()
            notifyItemChanged(lastPos)
        }
    }

    private var answerAdapterItemDecoration = HashMap<QuestionEntity?, RecyclerView.ItemDecoration?>()
    private var keywordAdapterItemDecoration = HashMap<QuestionEntity?, RecyclerView.ItemDecoration?>()
    private var answerAdapterMap = HashMap<QuestionEntity?, AnswerWordAdapter?>()
    private var keywordAdapterMap = HashMap<QuestionEntity?, KeywordAdapter?>()
    private var doAnswerMap = HashMap<QuestionEntity?, Boolean?>()


    override fun convert(helper: MVViewHolder<QuestionPageLayoutBinding>?, item: QuestionEntity?) {

        val context = helper?.itemView?.context!!
        if(SettingDataSource.isAdvOpen(context)){

//
//            helper.dataViewBinding.adView.loadAd( AdRequest.Builder().build())
            val openAdv=AppConnect.getInstance(context).getConfig("openADV","open")

            print("openAdv?:$openAdv")
            val loginUserPoint = UserDataSource.getLoginUserPoint(context)
            if(openAdv=="open"&&(loginUserPoint>100)&&!item?.hadAnswer!!){
                helper.dataViewBinding.advLayout.visibility = View.VISIBLE
                AppConnect.getInstance(context).showBannerAd(context, helper.dataViewBinding.advWps)
            }else{
                helper.dataViewBinding.advLayout.visibility = View.INVISIBLE
            }
        }else{
            helper.dataViewBinding.advLayout.visibility = View.INVISIBLE

        }

        helper.dataViewBinding.ivHelp.setOnClickListener {

            HelpPopupMenuUtil.showPopupMenu(context,helper.dataViewBinding.ivHelp,item!!)
        }
        var player: ExMediaPlayer?=null
        if (item?.type == "image") {
            GlideApp.with(helper.itemView).load(AppConstants.URL.FILE_PRE_URL + item.url).diskCacheStrategy(
                DiskCacheStrategy.ALL)
                .placeholder(R.drawable.loading).into(helper.dataViewBinding.ivQuestion)

            helper.dataViewBinding.ivQuestion.setOnClickListener {
                ShowImageActivity.start(helper.itemView.context,AppConstants.URL.FILE_PRE_URL + item.url)

            }

        } else if (item?.type == "audio") {
            try {
                if (map[item] == null) {
                    var dis =
                        Observable.just(item).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe {


                                player = ExMediaPlayer()
                                player?.setDataSource(AppConstants.URL.FILE_PRE_URL + it.url)
                                val audioAttributes = AudioAttributes.Builder()
                                audioAttributes.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                audioAttributes.setUsage(AudioAttributes.USAGE_MEDIA)
                                audioAttributes.setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                player?.setAudioAttributes(audioAttributes.build())
                                player?.prepareAsync()
                                map[item] = player!!
                            }
                } else {
                    player = map[item]
                }

                helper.dataViewBinding.playBtn.setOnClickListener {

                    if (player?.isPlaying!!) {
                        player?.pause()
                        helper.dataViewBinding.playBtn.isSelected = false

                    } else {
                        player?.start()
                        helper.dataViewBinding.playBtn.isSelected = true

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        helper.dataViewBinding.question = item
        helper.dataViewBinding.imageVisible = item!!.type == "image"
        when( item.type){
            "image"->{
                helper.dataViewBinding.audioLayout.visibility =View.GONE
                helper.dataViewBinding.playBtn.visibility =View.GONE
            }
            "audio"->{
                helper.dataViewBinding.audioLayout.visibility =View.VISIBLE
                helper.dataViewBinding.playBtn.visibility =View.VISIBLE
            }
        }

        helper.dataViewBinding.rvAnswer.isNestedScrollingEnabled = false
        helper.dataViewBinding.rvAnswer.layoutManager =
            GridLayoutManager(helper.itemView.context, item.answer!!.length)


        if(answerAdapterItemDecoration[item]==null){
            answerAdapterItemDecoration[item] = object : RecyclerView.ItemDecoration() {

                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)

                    val rvWidth = helper.dataViewBinding.rvAnswer.width
                    val childWidth = MeasUtils.dpToPx(50f, helper.dataViewBinding.rvAnswer.context)
                    val pos = parent.getChildLayoutPosition(view)
                    val answerLength = item.answer!!.length
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
            }
            if(doAnswerMap[item] ==null){
                helper.dataViewBinding.rvAnswer.addItemDecoration( answerAdapterItemDecoration[item]!!)
            }else{
                helper.dataViewBinding.rvAnswer.removeItemDecoration( answerAdapterItemDecoration[item]!!)
            }
//            helper.dataViewBinding.rvAnswer.addItemDecoration( answerAdapterItemDecoration[item]!!)
        }
        helper.dataViewBinding.rvKeywords.isNestedScrollingEnabled = false
        helper.dataViewBinding.rvKeywords.layoutManager = GridLayoutManager(helper.itemView.context, 7)
        if(keywordAdapterItemDecoration[item]==null){
            keywordAdapterItemDecoration[item] = object : RecyclerView.ItemDecoration() {

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
            }
            helper.dataViewBinding.rvKeywords.addItemDecoration(keywordAdapterItemDecoration[item]!!)
        }else{
            helper.dataViewBinding.rvKeywords.addItemDecoration(keywordAdapterItemDecoration[item]!!)
        }

        if(keywordAdapterMap[item]==null){
            keywordAdapterMap[item]  =  KeywordAdapter(item.answer!!, item.keywords!!)
            helper?.dataViewBinding?.keywordAdapter = keywordAdapterMap[item]
        }else{
            helper?.dataViewBinding?.keywordAdapter =keywordAdapterMap[item]
        }

        if(answerAdapterMap[item]==null){
            answerAdapterMap[item] = AnswerWordAdapter(item.answer!!)
            helper?.dataViewBinding?.answerAdapter = answerAdapterMap[item]

        }else{
            helper?.dataViewBinding?.answerAdapter =answerAdapterMap[item]

        }

//        helper?.dataViewBinding?.rvAnswer?.itemAnimator =AnimationUtil.create()
        (helper?.dataViewBinding?.answerAdapter as AnswerWordAdapter).setWordOperationCallBack(object :
            AnswerWordAdapter.WordOperationCallBack {
            override fun wrongAnswer(answer: String) {
                AnimationUtil.startShakeByPropertyAnim(helper?.dataViewBinding?.rvAnswer, 0.9f, 1.0f, 5f, 500)
                answerCallback?.wrongAnswer()
//                ( helper?.dataViewBinding?.answerAdapter as AnswerWordAdapter).notifyRightAnswerAnim()

            }

            override fun rightAnswer(answer: String) {
                answerCallback?.rightAnswer(answer)

            }

            override fun onNotEmptyWordClick(item: AnswerWord) {
                (helper?.dataViewBinding?.keywordAdapter as KeywordAdapter).changeItemSelectStatus(item.index!!, false)
            }
        })


        (helper?.dataViewBinding?.keywordAdapter as KeywordAdapter).setKeyWordClick(object :
            KeywordAdapter.KeyWordClick {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun add(word: AnswerWord) {

                (helper?.dataViewBinding?.answerAdapter as AnswerWordAdapter).add(word)

                //播放音效
                ShortSoundPlayer.playSound(helper.itemView.context, R.raw.click)

            }

            override fun remove(word: AnswerWord) {
                (helper?.dataViewBinding?.answerAdapter as AnswerWordAdapter).remove(word)


            }
        })
    }
}