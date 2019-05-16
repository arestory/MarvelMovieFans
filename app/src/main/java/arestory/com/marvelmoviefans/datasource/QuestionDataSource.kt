package arestory.com.marvelmoviefans.datasource

import arestory.com.marvelmoviefans.bean.NoAdminQuestion
import arestory.com.marvelmoviefans.bean.QuestionEntity
import arestory.com.marvelmoviefans.http.RetrofitServiceManager
import arestory.com.marvelmoviefans.http.QuestionApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object QuestionDataSource {

    private val questionApi = RetrofitServiceManager.getManager().create(QuestionApi::class.java)

    fun getQuestionTotalCount(callback: DataCallback<Int>){


        val disposable = questionApi.getQuestionCount().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                callback.onSuccess(it.data!!)
            },{
                callback.onFail(it.message!!)

            })

    }

    fun getNotAdminQuestions(createUserId: String?,userId: String?,page: Int,callback: DataCallback<List<NoAdminQuestion>>?){

        val dis = questionApi.getNotAdminQuestions(page, 20,createUserId, userId)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                if(it.code==200){
                    callback?.onSuccess(it.data!!)
                }else{
                    callback?.onFail(it.msg)
                }

            },{
                callback?.onFail(it.message)

            })

    }


    fun getRandomQuestion(userId: String?,callback: DataCallback<QuestionEntity>){


        val disposable = questionApi.getRandomOne(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                callback.onSuccess(it.data!!)

            },{

                callback.onFail(it.message!!)

            })

    }

    fun getQuestionList(userId:String?,page:Int,count:Int,callback: DataCallback<List<QuestionEntity>>){


        val disposable =  questionApi.getQuestions(page,count,userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
             .subscribe({

                 if(it.code ==200){

                     callback.onSuccess(it.data!!)

                 }else{

                     callback.onFail(it.msg)
                 }

             },{

                 callback.onFail(it.message!!)
             })

    }


}