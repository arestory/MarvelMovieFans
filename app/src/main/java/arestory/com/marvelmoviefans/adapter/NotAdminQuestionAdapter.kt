package arestory.com.marvelmoviefans.adapter

import android.app.Activity
import android.widget.ImageView
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.activities.UserInfoActivity
import arestory.com.marvelmoviefans.activities.UserQuestionActivity
import arestory.com.marvelmoviefans.bean.NoAdminQuestion
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.databinding.ItemMyQuestionBinding
import arestory.com.marvelmoviefans.databinding.ItemNotadminQuestionBinding
import arestory.com.marvelmoviefans.util.GlideCircleTransform
import com.bumptech.glide.load.engine.DiskCacheStrategy

class NotAdminQuestionAdapter(data:List<NoAdminQuestion>):BaseQAdapter<NoAdminQuestion,ItemNotadminQuestionBinding,MVViewHolder<ItemNotadminQuestionBinding>>(R.layout.item_notadmin_question,data){


    override fun convert(helper: MVViewHolder<ItemNotadminQuestionBinding>?, item: NoAdminQuestion?) {

        helper?.dataViewBinding?.question = item
        val context =   helper?.itemView?.context!!
        val image = helper.dataViewBinding.ivQuestion
        val avatar = helper.dataViewBinding.ivAvatar

        helper.dataViewBinding.tvCreator.setOnClickListener {
//            UserInfoActivity.startWithAnim(context as Activity,item?.createUserId!!,avatar)
            UserInfoActivity.start(context as Activity,item?.createUserId!!)
        }
        avatar.setOnClickListener {
            UserInfoActivity.start(context as Activity,item?.createUserId!!)

//            UserInfoActivity.startWithAnim(context as Activity,item?.createUserId!!,avatar)
        }

        GlideApp.with(image).load(AppConstants.URL.FILE_PRE_URL + item?.url).diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.loading).into(image)
        GlideApp.with(image).load(AppConstants.URL.FILE_PRE_URL + item?.avatar).diskCacheStrategy(DiskCacheStrategy.ALL).transform(
            GlideCircleTransform(1, helper.itemView.context.resources.getColor(R.color.white))
        ) .placeholder(R.drawable.placeholder).into(avatar)

        helper.itemView.setOnClickListener {

            itemClick?.onClick(item,image)
        }
    }


    private var itemClick:ItemClick?=null

    fun setItemClick(click:ItemClick){
        this.itemClick = click
    }
    interface ItemClick{

        fun onClick(questionEntity: NoAdminQuestion?,imageView: ImageView)
    }

}