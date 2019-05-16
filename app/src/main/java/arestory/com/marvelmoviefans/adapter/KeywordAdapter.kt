package arestory.com.marvelmoviefans.adapter

import android.databinding.ViewDataBinding
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.bean.AnswerWord
import arestory.com.marvelmoviefans.databinding.ItemKeywordBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

import java.util.ArrayList
import java.util.Objects

class KeywordAdapter : BaseQAdapter<KeywordAdapter.KeywordItem, ItemKeywordBinding, MVViewHolder<ItemKeywordBinding>> {


    private var answer: String? = null
    private var selectedLength = 0

    private var keyWordClick: KeyWordClick? = null

    constructor(answer: String, data: List<KeywordItem>?) : super(R.layout.item_keyword, data) {
        this.answer = answer
    }

    constructor(answer: String, keywords: String) : super(R.layout.item_keyword) {
        this.answer = answer
        val keywordItemList = ArrayList<KeywordItem>()
        keywords.split("").filter {

            it!=""
        }.map {

            keywordItemList.add(KeywordItem(it,false))
        }

        setNewData(keywordItemList)
    }


    fun setKeyWordClick(keyWordClick: KeyWordClick) {
        this.keyWordClick = keyWordClick
    }

    fun changeItemSelectStatus(index: Int, selected: Boolean) {

        if (selected) {
            if (answer!!.length != selectedLength) {
                selectedLength++
                keyWordClick?.add(AnswerWord(Objects.requireNonNull<KeywordItem>(getItem(index)).word!!, index))
                Objects.requireNonNull<KeywordItem>(getItem(index)).isSelected = selected
                notifyItemChanged(index)
            }
        } else {
            selectedLength--
            keyWordClick?.remove(AnswerWord(Objects.requireNonNull<KeywordItem>(getItem(index)).word!!, index))

            Objects.requireNonNull<KeywordItem>(getItem(index)).isSelected = selected
            notifyItemChanged(index)

        }

    }

    override fun convert(helper: MVViewHolder<ItemKeywordBinding>, item: KeywordItem) {

        helper.dataViewBinding.tvKey.isSelected = item.isSelected
        when( helper.dataViewBinding.tvKey.isSelected){

            true-> helper.dataViewBinding.tvKey.paint.isFakeBoldText = true
            false-> helper.dataViewBinding.tvKey.paint.isFakeBoldText = false
        }
        helper.dataViewBinding.word = item.word
        helper.dataViewBinding.tvKey.setOnClickListener {
            if (helper.dataViewBinding.tvKey.isSelected) {

                changeItemSelectStatus(helper.adapterPosition, false)
            } else {
                changeItemSelectStatus(helper.adapterPosition, true)
            }
        }
    }

    interface KeyWordClick {

        fun add(word: AnswerWord)

        fun remove(word: AnswerWord)
    }

    class KeywordItem {

        var word: String? = null
        var isSelected: Boolean = false

        constructor() {

        }

        constructor(word: String, selected: Boolean) {
            this.word = word
            this.isSelected = selected
        }
    }
}
