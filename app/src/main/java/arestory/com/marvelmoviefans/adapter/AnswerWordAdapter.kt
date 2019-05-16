package arestory.com.marvelmoviefans.adapter

import android.util.Log
import android.view.View
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.bean.AnswerWord
import arestory.com.marvelmoviefans.databinding.ItemAnswerWordBinding
import arestory.com.marvelmoviefans.databinding.ItemRankBinding
import arestory.com.marvelmoviefans.util.AnimationUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import java.lang.StringBuilder

import java.util.ArrayList
import java.util.Objects

class AnswerWordAdapter : BaseQAdapter<AnswerWord, ItemAnswerWordBinding, MVViewHolder<ItemAnswerWordBinding>> {


    private var wordOpClick:WordOperationCallBack?=null
    private lateinit var rightAnswer:String
    constructor(data: List<AnswerWord>?) : super(R.layout.item_answer_word, data) {}
    constructor(words: String) : super(R.layout.item_answer_word) {

        this.rightAnswer = words
        setNewData(words.split("").filter {
            it != ""
        }.mapIndexed { index, s ->
            AnswerWord("", index)
        })

    }
    var beginRightAnswerAnim = false

    fun notifyRightAnswerAnim(){
        beginRightAnswerAnim = true

        for(i in 0 until itemCount){

            notifyItemChanged(i)
        }

    }

    private fun getCurrentAnswer(): String {

        val builder = StringBuilder()
        for (item in data) {
            builder.append(item.word)
        }
        return builder.toString()
    }

    fun add(word: AnswerWord) {


        for (i in 0 until data.size) {
            val lastItem = data[i]
            if ("" == lastItem.word) {
                setData(i, word)
                break
            }
        }
        if (data.filter {
                it.word != ""
            }.size == rightAnswer.length) {

            if(getCurrentAnswer()!=rightAnswer){
                Log.d("answer","答案错误："+getCurrentAnswer())
                wordOpClick?.wrongAnswer(getCurrentAnswer())
            }else{

                Log.d("answer","答案正确")
                wordOpClick?.rightAnswer(rightAnswer)

            }
        }
    }



    fun remove(word: AnswerWord) {

        for (i in 0 until data.size) {
            val lastItem = data[i]
            if (word.index == lastItem.index) {
                val emptyWord = AnswerWord("", -1)
                setData(i, emptyWord)
                break
            }
        }
    }


    fun setWordOperationCallBack(wp:WordOperationCallBack){
        this.wordOpClick = wp
    }

    interface WordOperationCallBack{

        fun wrongAnswer(answer:String)
        fun rightAnswer(answer:String)
        fun onNotEmptyWordClick(item: AnswerWord)

    }

    override fun convert(helper: MVViewHolder<ItemAnswerWordBinding>, item: AnswerWord) {

        helper.dataViewBinding.keyword = item.word
        helper.dataViewBinding.tvWord.isSelected="" != item.word



         helper.dataViewBinding.tvWord.setOnClickListener {
            if ("" != item.word) {
                remove(item)
                wordOpClick?.onNotEmptyWordClick(item)
            }
        }


    }
}
