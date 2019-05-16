package arestory.com.marvelmoviefans.bean

import android.databinding.BindingAdapter
import android.widget.ImageView
import arestory.com.marvelmoviefans.R
import arestory.com.marvelmoviefans.common.GlideApp
import arestory.com.marvelmoviefans.constants.AppConstants
import arestory.com.marvelmoviefans.util.GlideCircleTransform
import java.io.Serializable

class UserInfo:Serializable{


    var id:String?=null
    var nickName:String?=null

    var sex:String?=null

    var age:Int?=null
    var avatar:String?=null
    var slogan:String?=null



}
