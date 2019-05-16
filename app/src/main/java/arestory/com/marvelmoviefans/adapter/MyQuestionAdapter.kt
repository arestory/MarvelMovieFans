package arestory.com.marvelmoviefans.adapter

import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.databinding.ItemMyQuestionBinding

class MyQuestionAdapter(data:List<QuestionEntity>):BaseQAdapter<QuestionEntity,ItemMyQuestionBinding,MVViewHolder<ItemMyQuestionBinding>>(R.layout.item_my_question,data){


    override fun convert(helper: MVViewHolder<ItemMyQuestionBinding>?, item: QuestionEntity?) {

        helper?.dataViewBinding?.question = item
        val image = helper?.dataViewBinding?.ivQuestion!!

        GlideApp.with(image).load(AppConstants.URL.FILE_PRE_URL + item?.url)
            .placeholder(R.drawable.loading).into(image)

        helper.itemView.setOnClickListener {

            itemClick?.onClick(item)
        }
    }

    private var itemClick:ItemClick?=null
    interface ItemClick{

        fun onClick(questionEntity: QuestionEntity?)
    }

}