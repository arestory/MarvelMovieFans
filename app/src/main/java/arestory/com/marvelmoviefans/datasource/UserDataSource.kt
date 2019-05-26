package arestory.com.marvelmoviefans.datasource

import android.content.Context
import android.content.Context.MODE_PRIVATE
import arestory.com.marvelmoviefans.bean.*
import arestory.com.marvelmoviefans.http.RetrofitServiceManager
import arestory.com.marvelmoviefans.http.UserApi
import arestory.com.marvelmoviefans.util.RxBus
import arestory.com.marvelmoviefans.bean.FeedbackEntity
import com.ares.movie.entity.UserPoint
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import java.io.File
import okhttp3.RequestBody
import okhttp3.MultipartBody


object UserDataSource {

    private val api = RetrofitServiceManager.getManager().create(UserApi::class.java)


    fun getUserInfo(userId: String,callback: DataCallback<UserInfo>){

        val dis = api.getUserInfo(userId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({

                if(it.code==200){

                    callback.onSuccess(it.data!!)
                }else{

                    callback.onFail(it.msg)
                }

            },{


                callback.onFail(it.message)

            })
    }

    fun getPointRank(page:Int,callback: DataCallback<List<UserPoint>>){

        val dis = api.getPointRank(page,20).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({


                if(it.code==200){

                    callback.onSuccess(it.data!!)

                }else{
                    callback.onFail(it.msg)
                }

            },{

                callback.onFail(it.message)

            })

    }

    fun getUserQuestion(userId:String, page:Int,callback: DataCallback<List<QuestionEntity>>){

        val dis = api.getUserQuestion(userId,page,10).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({


                if(it.code==200){
                    callback.onSuccess(it.data!!)

                }else{
                    callback.onFail(it.msg)
                }
            },{

                callback.onFail(it.message)

            })

    }


    fun answerQuestion(userId: String?, questionId: String, callback: DataCallback<String>?) {

        val dis = api.answerQuestion(userId, questionId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if (it.code == 200) {
                    callback?.onSuccess(it.data!!)
                } else {
                    callback?.onFail(it.msg)
                }

            }, {
                callback?.onFail(it.message!!)
            })

    }

    fun getUserPoint(userId: String?, callback: DataCallback<Int>) {

        val dis = api.getUserPoint(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if (it.code == 200) {
                    callback.onSuccess(it.data!!)
                } else {
                    callback.onFail(it.msg)
                }

            }, {
                callback.onFail(it.message!!)


            })


    }

    fun getLoginUserId(context: Context): String? {


        return getLoginUser(context)?.id
    }



    fun login(body: UserRequestBody, callback: DataCallback<UserInfo>) {

        val dis = api.login(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({

            if (it.code == 200&&it.data!=null) {
                callback.onSuccess(it.data!!)
            } else {
                callback.onFail(it.msg)
            }

        }, {

            callback.onFail(it.message!!)

        })
    }

    fun saveLoginUser(context: Context, userInfo: UserInfo) {


        val sp = context.getSharedPreferences("movie", MODE_PRIVATE).edit()
        sp.putString("user", Gson().toJson(userInfo))
        sp.apply()
    }

    fun saveLoginUserPoint(context: Context,point:Int){

        val sp = context.getSharedPreferences("movie", MODE_PRIVATE).edit()
        sp.putInt("${getLoginUserId(context)}Point",point)
        sp.apply()

    }
    fun getLoginUserPoint(context: Context):Int{


        return context.getSharedPreferences("movie", MODE_PRIVATE).getInt("${getLoginUserId(context)}Point",0)
    }

    fun updateUserInfo(
        userId: String?,
        updateUserRequestBody: UpdateUserRequestBody,
        callback: DataCallback<UserInfo>
    ) {

        val dis = api.updateUserInfo(userId, updateUserRequestBody).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({

            if (it.code == 200) {
                callback.onSuccess(it.data!!)
            } else {
                callback.onFail(it.msg)
            }

        }, {

            callback.onFail(it.message)
        })
    }

    fun saveLevelPageDone(context: Context?, userId: String, level: Int) {

        val userFinishLevelJson =
            context?.getSharedPreferences("movie", MODE_PRIVATE)?.getString(userId.plus("-level"), "")
        if (userFinishLevelJson!!.isNotEmpty()) {

            val userFinishLevel = Gson().fromJson(userFinishLevelJson, UserFinishLevel::class.java)
            if (userFinishLevel != null) {

                userFinishLevel.levelSet.add(level)
                val sp = context.getSharedPreferences("movie", MODE_PRIVATE).edit()
                sp.putString(userId.plus("-level"), Gson().toJson(userFinishLevel))
                sp.apply()
            }
        } else {
            val sp = context.getSharedPreferences("movie", MODE_PRIVATE).edit()
            val userFinishLevel = UserFinishLevel()
            userFinishLevel.userId = userId
            val hashSet = HashSet<Int>()
            hashSet.add(level)
            sp.putString(userId.plus("-level"), Gson().toJson(userFinishLevel))
            sp.apply()
        }
    }

    fun checkLevelFinish(context: Context, userId: String?, level: Int):Boolean {

        if(userId==null){
            return false
        }
        val userFinishLevelJson =
            context.getSharedPreferences("movie", MODE_PRIVATE).getString(userId.plus("-level"), "")
        if (userFinishLevelJson!!.isNotEmpty()) {

            val userFinishLevel = Gson().fromJson(userFinishLevelJson, UserFinishLevel::class.java)
            if(userFinishLevel!=null){
                return userFinishLevel.levelSet.contains(level)
            }
        }

        return false

    }


    fun getLoginUser(context: Context): UserInfo? {


        val userJson = context.getSharedPreferences("movie", MODE_PRIVATE).getString("user", "")

        if (userJson.isNotEmpty()) {
            return Gson().fromJson(userJson, UserInfo::class.java)
        }

        return null


    }

    const val EXIT_USER  = "exitUser"

    fun clearLoginUser(context: Context) {
        val sp = context.getSharedPreferences("movie", MODE_PRIVATE).edit()
        sp.remove("user")
        sp.apply()
        RxBus.get().post(EXIT_USER)
    }

    fun register(body: UserRequestBody, callback: DataCallback<UserInfo>) {

        val dis = api.register(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200&&it.data!=null) {
                    callback.onSuccess(it.data!!)
                } else {
                    callback.onFail(it.msg)
                }
            }, {
                it.printStackTrace()
                callback.onFail(it.message!!)
            })
    }


    fun uploadAvatar(userId: String, file: File, callback: DataCallback<UserInfo>) {


        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val dis = api.uploadAvatar(userId, body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.code == 200) {

                    callback.onSuccess(it.data!!)
                } else {
                    callback.onFail(it.msg)
                }

            }, {

                callback.onFail(it.message)

            })
    }

    fun uploadQuestion(uploadBean: QuestionUploadBean, callback: DataCallback<String>) {

        val requestBody = RequestBody.create(MediaType.parse("image/jpg"), uploadBean.file!!)
        val body = MultipartBody.Part.createFormData("file", uploadBean.file!!.name, requestBody)

        val dis = api.uploadQuestion(
            body,
            uploadBean.answer,
            uploadBean.title,
            uploadBean.point,
            uploadBean.keywords,
            uploadBean.createUserId
        )
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({

                if (it.code == 200) {

                    callback.onSuccess(it.data!!)
                } else {
                    callback.onFail(it.msg)
                }


            }, {
                callback.onFail(it.message)


            })

    }

    fun commitFeedback(feedbackEntity: FeedbackEntity, callback: DataCallback<String>) {

        val dis =
            api.commitFeedback(feedbackEntity).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.code == 200) {

                            callback.onSuccess(it.data!!)
                        } else {
                            callback.onFail(it.msg)
                        }
                    }, {

                        callback.onFail(it.message)
                    }
                )

    }



}