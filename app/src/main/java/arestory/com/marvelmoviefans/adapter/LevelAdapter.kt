package arestory.com.marvelmoviefans.adapter

import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.activities.LevelQuestionActivity
import arestory.com.marvelmoviefans.activities.UserLoginActivity
import arestory.com.marvelmoviefans.bean.QuestionPage
import  arestory.com.marvelmoviefans.databinding.ItemQuestionPageBinding
import arestory.com.marvelmoviefans.datasource.UserDataSource

class LevelAdapter(data: List<QuestionPage>) :
    BaseQAdapter<QuestionPage, ItemQuestionPageBinding, MVViewHolder<ItemQuestionPageBinding>>(
        R.layout.item_question_page,
        data
    ) {

    override fun convert(helper: MVViewHolder<ItemQuestionPageBinding>?, item: QuestionPage?) {

        helper?.dataViewBinding?.page =item
        helper?.dataViewBinding?.btnLevel?.isSelected = item!!.finish
        helper?.dataViewBinding?.btnLevel?.setOnClickListener {
            if(UserDataSource.getLoginUser(helper.itemView.context)!=null){

                LevelQuestionActivity.start(helper.itemView.context,item)
            }else{
                UserLoginActivity.start(helper.itemView.context)
            }

        }

    }


}