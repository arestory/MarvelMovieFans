package arestory.com.marvelmoviefans.adapter

import android.app.Activity
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.activities.UserInfoActivity
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.databinding.ItemRankBinding
import arestory.com.marvelmoviefans.util.GlideCircleTransform
import com.ares.movie.entity.UserPoint

class RankAdapter(data:List<UserPoint>): BaseQAdapter<UserPoint, ItemRankBinding, MVViewHolder<ItemRankBinding>>(R.layout.item_rank,data) {



    override fun convert(helper: MVViewHolder<ItemRankBinding>?, item: UserPoint?) {

        helper?.dataViewBinding?.index = helper?.adapterPosition?.plus(1)
        helper?.dataViewBinding?.userPoint = item

        helper?.itemView?.setOnClickListener {


            UserInfoActivity.startWithAnim( helper?.itemView?.context!! as Activity,item?.userId!!,helper?.dataViewBinding.ivAvatar)

        }



        GlideApp.with(  helper?.dataViewBinding?.ivAvatar?.context!!).load(AppConstants.URL.FILE_PRE_URL+item?.avatar).placeholder(R.drawable.placeholder).transform(
            GlideCircleTransform(1, helper.dataViewBinding.ivAvatar.context!!.resources.getColor(R.color.white))
        )
            .into(helper.dataViewBinding.ivAvatar)
     }

}